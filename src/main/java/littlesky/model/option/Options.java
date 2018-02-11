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
import java.util.TimeZone;

public class Options {
    
    private static final String CONFIG_FILE = "./littlesky.xml";
    private static final String OPEN_WEATHER_MAP_API_KEY = "open-weather-map-api-key";
    private static final String HTTP_PROXY_HOST = "http-proxy.host";
    private static final String HTTP_PROXY_PORT = "http-proxy.port";
    private static final String HTTP_PROXY_USERNAME = "http-proxy.username";
    private static final String HTTP_PROXY_PASSWORD = "http-proxy.password";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TIME_ZONE_ID = "time-zone-id";
    
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

    private void put(String key, Object value) {
        if (value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value.toString());
        }
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
    
    public void setAlwaysOnTop(boolean flag) {
        this.properties.put(ALWAYS_ON_TOP, flag ? "true" : "false");
    }
    
    public boolean isAlwaysOnTop() {
        if (!this.properties.containsKey(ALWAYS_ON_TOP)) {
            return false;
        }
        return Boolean.valueOf((String)this.properties.get(ALWAYS_ON_TOP));
    }
    
    public boolean hasUserLocation() {
        return this.get(LONGITUDE) != null
                && this.get(LATITUDE) != null
                && this.get(TIME_ZONE_ID) != null;
    }
    
    public UserLocation getUserLocation() {
        if (!this.hasUserLocation()) {
            throw new IllegalStateException("Location is not set.");
        }
        return new UserLocation(this.getLatitude(), this.getLongitude(), this.getTimeZone());
    }
    
    public void setUserLocation(UserLocation location) {
        this.setLatitude(location.getLatitude());
        this.setLongitude(location.getLongitude());
        this.setTimeZone(location.getTimeZone());
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
    
    private void setTimeZone(TimeZone timeZone) {
        this.put(TIME_ZONE_ID, timeZone.getID());
    }
    
    private TimeZone getTimeZone() {
        String timeZoneId = this.get(TIME_ZONE_ID);
        return TimeZone.getTimeZone(timeZoneId);
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
}
