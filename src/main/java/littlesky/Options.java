package littlesky;

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
    private static final Options instance = new Options();
    
    public static Options getInstance() {
        return instance;
    }
    
    private Properties properties;
    
    private Options() {
        this.properties = new Properties();
        
        try {
            FileInputStream inputStream = new FileInputStream(CONFIG_FILE);
            this.properties.loadFromXML(inputStream);
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public void setOpenWeatherMapApiKey(String apiKey) {
        this.properties.put(OPEN_WEATHER_MAP_API_KEY, apiKey);
    }
    
    public Optional<String> getOpenWeatherMapApiKey() {
        return Optional.ofNullable((String)this.properties.get(OPEN_WEATHER_MAP_API_KEY));
    }
    
    public void save() {
        try (OutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            this.properties.storeToXML(outputStream, "littkesky", "UTF-8");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
