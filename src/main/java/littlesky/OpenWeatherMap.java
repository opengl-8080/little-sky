package littlesky;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenWeatherMap extends WeatherBase {
    private static final int MAX_RETRY_COUNT = 3;
    private static final double ABSOLUTE_ZERO = -273.15;
    private static final int RETRY_WAIT_SECONDS = 5;
    private static final OpenWeatherMap instance = new OpenWeatherMap();
    public static OpenWeatherMap getInstance() {
        return instance;
    }
 
    private ObjectMapper objectMapper = new ObjectMapper();
    private Options options = Options.getInstance();
    private ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);
    
    private OpenWeatherMap() {
        this.updateWeatherType(WeatherType.SUNNY);
        this.updateTemperature(15.0);
        this.updateCloudRate(0.0);
    }
    
    public void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("OpenWeatherMap service thread already started.");
        }
        
        URL url = this.buildRequestUrl();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("OpenWeatherMap Request Thread");
            thread.setDaemon(true);
            return thread;
        });

        updateRunning(true);
        executor.scheduleWithFixedDelay(() -> {
            if (Thread.currentThread().isInterrupted()) {
                updateRunning(false);
                executor.shutdown();
                return;
            }

            try {
                ResponseRoot root = withRetry(() -> tryRequest(url));

                updateWeatherType(root.getWeatherType());
                updateTemperature(root.getTemperature());
                updateCloudRate(root.getCloudRate());

            } catch (InterruptedException e) {
                updateRunning(false);
                executor.shutdown();
            } catch (Exception e) {
                updateRunning(false);
                executor.shutdown();
                ErrorDialog.show(e);
            }
        }, 0, 15, TimeUnit.MINUTES);
    }
    
    private URL buildRequestUrl() {
        String cityId = "1853909";
        String apiKey = this.options.getOpenWeatherMapApiKey().orElseThrow(() -> new RuntimeException("OpenWeatherMap API Key is not set."));
        try {
            return new URL("http://api.openweathermap.org/data/2.5/weather?id=" + cityId + "&APPID=" + apiKey);
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
            
            TimeUnit.SECONDS.wait(RETRY_WAIT_SECONDS);
        } while (triedCount < MAX_RETRY_COUNT);

        throw new UncheckedIOException(exception);
    }
    
    private ResponseRoot tryRequest(URL url) throws IOException {
        System.out.println("try request...");
        Proxy proxy = this.createProxy();
        System.out.println("proxy=" + proxy);
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
    
    private Proxy createProxy() {
        return this.options.getHttpProxyHost().map(host -> {
            int port = this.options.getHttpProxyPort().orElse(80);
            InetSocketAddress proxyAddress = new InetSocketAddress(host, port);
            return new Proxy(Proxy.Type.HTTP, proxyAddress);
        }).orElse(Proxy.NO_PROXY);
    }
    
    private ResponseRoot parseResponse(HttpURLConnection con) throws IOException {
        try (InputStream in = con.getInputStream()) {
            return objectMapper.readValue(in, ResponseRoot.class);
        }
    }
    
    private void updateTemperature(double temperature) {
        Platform.runLater(() -> this.temperature.set(temperature));
    }
    
    private void updateCloudRate(double cloudRate) {
        Platform.runLater(() -> this.cloudRate.set(cloudRate));
    }
    
    private void updateWeatherType(WeatherType type) {
        Platform.runLater(() -> this.weatherType.set(type));
    }
    
    private void updateRunning(boolean running) {
        Platform.runLater(() -> this.running.set(running));
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
            if (this.isSnowy()) {
                return WeatherType.SNOWY;
            } else if (this.isRainy()) {
                return WeatherType.RAINY;
            } else {
                return WeatherType.SUNNY;
            }
        }
        
        private boolean isSnowy() {
            int id = this.weather.get(0).id;
            return 600 <= id && id < 700;
        }
        
        private boolean isRainy() {
            int id = this.weather.get(0).id;
            return id < 600;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseWeather {
        /**
         * <a href="https://openweathermap.org/weather-conditions">weather condition code list</a>
         */
        public int id;
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
