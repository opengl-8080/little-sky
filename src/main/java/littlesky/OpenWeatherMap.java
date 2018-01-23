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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenWeatherMap extends WeatherBase {
    private static final OpenWeatherMap instance = new OpenWeatherMap();
    public static OpenWeatherMap getInstance() {
        return instance;
    }
 
    private ObjectMapper objectMapper = new ObjectMapper();
    private Options options = Options.getInstance();
    private ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);
    
    private OpenWeatherMap() {
        this.weatherType.set(WeatherType.SUNNY);
        this.temperature.setValue(15.0);
    }
    
    public void start() {
        if (!this.options.getOpenWeatherMapApiKey().isPresent()) {
            // TODO Error!!
            System.out.println("no api key");
            return;
        }
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("OpenWeatherMap Request Thread");
            thread.setDaemon(true);
            return thread;
        });

        String apiKey = this.options.getOpenWeatherMapApiKey().get();

        executor.scheduleWithFixedDelay(() -> {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=1853909&APPID=" + apiKey);
                System.out.println("connect(" + url + ")...");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                if (con.getResponseCode() == 200) {
                    try (InputStream in = con.getInputStream()) {
                        ResponseRoot root = objectMapper.readValue(in, ResponseRoot.class);
                        double temperature = root.main.temp;
                        double clouds = root.clouds.all;
                        int weatherId = root.weather.get(0).id;

                        System.out.println("temperature=" + temperature + ", clouds=" + clouds + ", weatherId=" + weatherId);
                        
                        updateTemperature(temperature - 273.15);
                        if (weatherId < 600) {
                            updateWeatherType(WeatherType.RAINY);
                        } else if (weatherId < 700) {
                            updateWeatherType(WeatherType.SNOWY);
                        } else {
                            updateWeatherType(WeatherType.SUNNY);
                        }
                        updateCloudRate(clouds / 100.0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("!");
                } else {
                    // TODO http error
                    System.out.println("http error? status=" + con.getResponseCode());
                }
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            } catch (IOException e) {
                // TODO control retry
                System.out.println(e.getMessage());
                throw new UncheckedIOException(e);
            }
        }, 0, 30, TimeUnit.MINUTES);
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
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ResponseWeather {
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
}
