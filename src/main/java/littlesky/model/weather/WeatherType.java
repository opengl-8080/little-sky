package littlesky.model.weather;

import java.io.InputStream;

public interface WeatherType {
    InputStream getIconStream();
    boolean isSunny();
}
