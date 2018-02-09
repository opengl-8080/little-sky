package littlesky.model.weather;

import java.net.URL;

public interface WeatherType {
    URL getIcon();
    boolean isSunny();
}
