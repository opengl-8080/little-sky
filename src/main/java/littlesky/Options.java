package littlesky;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;

public class Options {
    
    private static final String CONFIG_FILE = "./littlesky.xml";
    private static final String OPEN_WEATHER_MAP_API_KEY = "open-weather-map-api-key";
    private static final String HTTP_PROXY_HOST = "http-proxy.host";
    private static final String HTTP_PROXY_PORT = "http-proxy.port";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    
    private static final String ALWAYS_ON_TOP = "always-on-top";
    private static final Options instance = new Options();
    
    public static Options getInstance() {
        return instance;
    }
    
    private Properties properties;
    private final ReadOnlyStringWrapper openWeatherMapApiKey = new ReadOnlyStringWrapper();
    
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
    }
    
    
    public void save(String key, String value) {
        this.properties.put(key, value);
    }
    
    public String get(String key) {
        return (String) this.properties.get(key);
    }
    
    
    
    
    public void setOpenWeatherMapApiKey(String apiKey) {
        this.properties.put(OPEN_WEATHER_MAP_API_KEY, apiKey);
        this.openWeatherMapApiKey.set(apiKey);
    }
    
    public Optional<String> getOpenWeatherMapApiKey() {
        return Optional.ofNullable(this.getString(OPEN_WEATHER_MAP_API_KEY));
    }

    public ReadOnlyStringProperty getOpenWeatherMapApiKeyProperty() {
        return this.openWeatherMapApiKey.getReadOnlyProperty();
    }
    
    public void setHttpProxyHost(String host) {
        this.properties.put(HTTP_PROXY_HOST, host);
    }
    
    public Optional<String> getHttpProxyHost() {
        return Optional.ofNullable(this.getString(HTTP_PROXY_HOST));
    }

    public void setHttpProxyPort(String value) {
        this.properties.put(HTTP_PROXY_PORT, defaultEmpty(value).isEmpty() ? "" : value);
    }
    
    public void validateHttpProxyPort(String value) throws InvalidInputException {
        String text = defaultEmpty(value);
        if (!text.isEmpty() && !text.matches("\\d+")) {
            throw new InvalidInputException("Http Proxy Port must be number.");
        }
    }

    public OptionalInt getHttpProxyPort() {
        Integer value = this.getInt(HTTP_PROXY_PORT);
        if (value == null) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(value);
        }
    }
    
    public void setAlwaysOnTop(boolean flag) {
        this.properties.put(ALWAYS_ON_TOP, flag ? "true" : "false");
    }
    
    public boolean isAlwaysOnTop() {
        if (!this.properties.containsKey(ALWAYS_ON_TOP)) {
            return false;
        }
        return Boolean.valueOf((String)this.properties.get(ALWAYS_ON_TOP));
    }
    
    public boolean hasLocation() {
        return this.properties.get(LONGITUDE) != null
                && this.properties.get(LATITUDE) != null;
    }
    
    public UserLocation getUserLocation() {
        if (!this.hasLocation()) {
            throw new IllegalStateException("Location is not set.");
        }
        return new UserLocation(this.getLatitude(), this.getLongitude());
    }
    
    public void setUserLocation(UserLocation location) {
        this.setLatitude(location.getLatitude());
        this.setLongitude(location.getLongitude());
    }
    
    private double getLongitude() {
        String value = (String) this.properties.get(LONGITUDE);
        return Double.parseDouble(value);
    }

    private void setLongitude(double longitude) {
        this.properties.put(LONGITUDE, String.valueOf(longitude));
    }

    private double getLatitude() {
        String value = (String) this.properties.get(LATITUDE);
        return Double.parseDouble(value);
    }

    private void setLatitude(double latitude) {
        this.properties.put(LATITUDE, String.valueOf(latitude));
    }

    private String defaultEmpty(String text) {
        return text == null ? "" : text;
    }

    public void save() {
        try (OutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            this.properties.storeToXML(outputStream, "littkesky", "UTF-8");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private String getString(String key) {
        Object value = this.properties.get(key);
        if (value == null) {
            return null;
        }
        String string = (String)value;
        return string.isEmpty() ? null : string;
    }
    
    private Integer getInt(String key) {
        String text = this.getString(key);
        if (text == null) {
            return null;
        } else {
            return Integer.valueOf(text);
        }
    }
    
    private Double getDouble(String key) {
        String text = this.getString(key);
        if (text == null) {
            return null;
        } else {
            return Double.valueOf(text);
        }
    }
}
