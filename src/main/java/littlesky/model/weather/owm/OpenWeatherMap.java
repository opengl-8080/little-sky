package littlesky.model.weather.owm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import littlesky.model.location.UserLocation;
import littlesky.model.option.Options;
import littlesky.model.weather.WeatherBase;
import littlesky.model.weather.WeatherType;
import littlesky.util.Logger;
import littlesky.view.Dialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenWeatherMap extends WeatherBase {
    private static final Logger logger = Logger.getInstance();
    
    private static final int MAX_RETRY_COUNT = 3;
    private static final double ABSOLUTE_ZERO = -273.15;
    private static final int RETRY_WAIT_SECONDS = 5;

    private static final int REQUEST_INITIAL_DELAY = 0;
    private static final int REQUEST_DELAY_AMOUNT = 15;
    private static final TimeUnit REQUEST_DELAY_TIME_UNIT = TimeUnit.MINUTES;
    
    private static final WeatherType DEFAULT_WEATHER_TYPE = OpenWeatherMapType.SUNNY;
    private static final double DEFAULT_TEMPERATURE = 15.0;
    private static final double DEFAULT_CLOUD_RATE = 0.0;
    
    private static final OpenWeatherMap instance = new OpenWeatherMap();
    
    public static OpenWeatherMap getInstance() {
        return instance;
    }
 
    private ObjectMapper objectMapper = new ObjectMapper();
    private Options options = Options.getInstance();
    private ScheduledExecutorService executor;
    private ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);
    
    private OpenWeatherMap() {
        this.initStatus();
    }

    private void initStatus() {
        this.updateWeatherType(DEFAULT_WEATHER_TYPE);
        this.updateTemperature(DEFAULT_TEMPERATURE);
        this.updateCloudRate(DEFAULT_CLOUD_RATE);
    }
    
    public void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("OpenWeatherMap service thread already started.");
        }
        
        URL url = this.buildRequestUrl();

        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("OpenWeatherMap Request Thread");
            thread.setDaemon(true);
            return thread;
        });

        updateRunning(true);
        this.executor.scheduleWithFixedDelay(() -> {

            try {
                ResponseRoot root = withRetry(() -> tryRequest(url));
                
                updateWeatherType(root.getWeatherType());
                updateTemperature(root.getTemperature());
                updateCloudRate(root.getCloudRate());

                logger.debug(() -> 
                       "weatherType=" + root.getWeatherType()
                     + ", temperature=" + root.getTemperature()
                     + ", cloudRate=" + root.getCloudRate()
                );
            } catch (InterruptedException e) {
                this.stop();
            } catch (Exception e) {
                this.stop();
                Dialog.error(e);
            }
        }, REQUEST_INITIAL_DELAY, REQUEST_DELAY_AMOUNT, REQUEST_DELAY_TIME_UNIT);
    }

    public void stop() {
        if (!this.isRunning()) {
            return;
        }
        updateRunning(false);
        this.executor.shutdown();
        this.executor.shutdownNow();
        this.initStatus();
    }
    
    private URL buildRequestUrl() {
        UserLocation location = this.options.getUserLocation();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String apiKey = this.options.getOpenWeatherMapApiKey().orElseThrow(() -> new RuntimeException("OpenWeatherMap API Key is not set."));
        try {
            return new URL("http://api.openweathermap.org/data/2.5/weather?lon=" + longitude + "&lat=" + latitude + "&APPID=" + apiKey);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ResponseRoot withRetry(ApiRequest request) throws InterruptedException {
        IOException exception;
        int triedCount = 0;
        
        do {
            try {
                return request.execute();
            } catch (IOException e) {
                exception = e;
                e.printStackTrace(System.err);
                triedCount++;
            }
            
            TimeUnit.SECONDS.sleep(RETRY_WAIT_SECONDS);
        } while (triedCount < MAX_RETRY_COUNT);

        throw new UncheckedIOException(exception);
    }
    
    private ResponseRoot tryRequest(URL url) throws IOException {
        logger.debug("try request");
        Proxy proxy = this.options.getHttpProxy().toProxy();
        HttpURLConnection con = (HttpURLConnection)url.openConnection(proxy);
        int status = con.getResponseCode();
        if (status == 200) {
            return parseResponse(con);
        } else if (500 <= status && status < 600) {
            throw new IOException("http response status=" + con.getResponseCode() + ", message=" + con.getResponseMessage());
        } else {
            throw new RuntimeException("http response status=" + con.getResponseCode() + ", message=" + con.getResponseMessage());
        }
    }
    
    private ResponseRoot parseResponse(HttpURLConnection con) throws IOException {
        try (InputStream in = con.getInputStream()) {
            return objectMapper.readValue(in, ResponseRoot.class);
        }
    }
    
    private void updateTemperature(double temperature) {
        this.runLater(() -> this.temperature.set(temperature));
    }
    
    private void updateCloudRate(double cloudRate) {
        this.runLater(() -> this.cloudRate.set(cloudRate));
    }
    
    private void updateWeatherType(WeatherType type) {
        this.runLater(() -> this.weatherType.set(type));
    }
    
    private void updateRunning(boolean running) {
        this.runLater(() -> this.running.set(running));
    }
    
    private void runLater(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
    
    public ReadOnlyBooleanProperty runningProperty() {
        return this.running.getReadOnlyProperty();
    }
    
    public boolean isRunning() {
        return this.running.get();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseRoot {
        public List<ResponseWeather> weather;
        public ResponseMain main;
        public ResponseClouds clouds;
        
        private double getCloudRate() {
            return this.clouds.all / 100.0;
        }
        
        private double getTemperature() {
            return this.main.temp + ABSOLUTE_ZERO;
        }
        
        private WeatherType getWeatherType() {
            ResponseWeather weather = this.weather.get(0);
            return new OpenWeatherMapType(weather.id, weather.icon);
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseWeather {
        /**
         * <a href="https://openweathermap.org/weather-conditions">weather condition code list</a>
         */
        public int id;
        
        public String icon;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseMain {
        public double temp;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseClouds {
        public double all;
    }
    
    @FunctionalInterface
    private interface ApiRequest {
        ResponseRoot execute() throws IOException;
    }
}
