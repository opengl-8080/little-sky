package littlesky.model.option;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import littlesky.model.http.HttpProxy;
import littlesky.model.location.UserLocation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Properties;

public class Options {
    
    private static final String CONFIG_FILE = "./littlesky.xml";
    private static final String OPEN_WEATHER_MAP_API_KEY = "open-weather-map-api-key";
    private static final String HTTP_PROXY_HOST = "http-proxy.host";
    private static final String HTTP_PROXY_PORT = "http-proxy.port";
    private static final String HTTP_PROXY_USERNAME = "http-proxy.username";
    private static final String HTTP_PROXY_PASSWORD = "http-proxy.password";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private static final String SHOW_SECONDS = "show-seconds";
    private static final String SHOW_TEMPERATURE = "show-temperature";
    private static final String SHOW_SKY_STATUS_ICON = "show-sky-status-icon";
    private static final String ALWAYS_ON_TOP = "always-on-top";
    
    private static final Options instance = new Options();

    public static Options getInstance() {
        return instance;
    }
    
    private Properties properties;
    private final ReadOnlyStringWrapper openWeatherMapApiKey = new ReadOnlyStringWrapper();
    private final ViewOptions viewOptions;
    
    private Options() {
        this.properties = new Properties();
        
        try {
            FileInputStream inputStream = new FileInputStream(CONFIG_FILE);
            this.properties.loadFromXML(inputStream);
            this.openWeatherMapApiKey.set(this.getOpenWeatherMapApiKey().orElse(""));
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
        this.viewOptions = this.initializeViewOptions();
    }

    // ViewOptions
    private ViewOptions initializeViewOptions() {
        ViewOptions viewOptions = new ViewOptions();

        if (this.has(SHOW_SECONDS)) {
            viewOptions.setAlwaysOnTop(this.getBoolean(ALWAYS_ON_TOP));
            viewOptions.setShowSeconds(this.getBoolean(SHOW_SECONDS));
            viewOptions.setShowTemperature(this.getBoolean(SHOW_TEMPERATURE));
            viewOptions.setShowSkyStatusIcon(this.getBoolean(SHOW_SKY_STATUS_ICON));
        } else {
            // at first time.
            viewOptions.setAlwaysOnTop(false);
            viewOptions.setShowSeconds(true);
            viewOptions.setShowTemperature(true);
            viewOptions.setShowSkyStatusIcon(true);
        }

        return viewOptions;
    }
    
    public ViewOptions getViewOptions() {
        return viewOptions;
    }
    
    // OpenWeatherMap API Key
    public void setOpenWeatherMapApiKey(String apiKey) {
        this.put(OPEN_WEATHER_MAP_API_KEY, apiKey);
        this.openWeatherMapApiKey.set(apiKey);
    }
    
    public Optional<String> getOpenWeatherMapApiKey() {
        return Optional.ofNullable(this.get(OPEN_WEATHER_MAP_API_KEY));
    }

    public ReadOnlyStringProperty getOpenWeatherMapApiKeyProperty() {
        return this.openWeatherMapApiKey.getReadOnlyProperty();
    }
    
    // HTTP Proxy
    public HttpProxy getHttpProxy() {
        String host = this.get(HTTP_PROXY_HOST);
        String textPort = this.get(HTTP_PROXY_PORT);
        String username = this.get(HTTP_PROXY_USERNAME);
        String password = this.get(HTTP_PROXY_PASSWORD);
        Integer port = textPort == null || textPort.isEmpty() ? null : Integer.valueOf(textPort);
        return new HttpProxy(host, port, username, password);
    }

    public void setHttpProxy(HttpProxy httpProxy) {
        String host = httpProxy.getHost().orElse(null);
        this.put(HTTP_PROXY_HOST, host);
        
        Integer port = httpProxy.getPort().orElse(null);
        this.put(HTTP_PROXY_PORT, port);
        
        String username = httpProxy.getUsername().orElse(null);
        this.put(HTTP_PROXY_USERNAME, username);
        
        String password = httpProxy.getPassword().orElse(null);
        this.put(HTTP_PROXY_PASSWORD, password);
    }

    // UserLocations
    public boolean hasUserLocation() {
        return this.has(LONGITUDE) && this.has(LATITUDE);
    }
    
    public UserLocation getUserLocation() {
        if (!this.hasUserLocation()) {
            throw new IllegalStateException("Location is not set.");
        }
        
        double longitude = Double.parseDouble(this.get(LONGITUDE));
        double latitude = Double.parseDouble(this.get(LATITUDE));
        
        return new UserLocation(latitude, longitude);
    }
    
    public void setUserLocation(UserLocation location) {
        this.put(LATITUDE, String.valueOf(location.getLatitude()));
        this.put(LONGITUDE, String.valueOf(location.getLongitude()));
    }

    // Internal Methods
    private void put(String key, Object value) {
        if (value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value.toString());
        }
    }

    private String get(String key) {
        return (String) this.properties.get(key);
    }
    
    private boolean getBoolean(String key) {
        return this.has(key) && Boolean.valueOf(this.get(key));
    }

    private boolean has(String key) {
        return this.properties.containsKey(key);
    }

    public void save() {
        this.saveViewOptions();
        
        try (OutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            this.properties.storeToXML(outputStream, "littkesky", "UTF-8");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void saveViewOptions() {
        this.put(ALWAYS_ON_TOP, viewOptions.isAlwaysOnTop());
        this.put(SHOW_SECONDS, viewOptions.isShowSeconds());
        this.put(SHOW_TEMPERATURE, viewOptions.isShowTemperature());
        this.put(SHOW_SKY_STATUS_ICON, viewOptions.isShowSkyStatusIcon());
    }
}
