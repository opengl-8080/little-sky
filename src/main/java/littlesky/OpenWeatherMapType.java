package littlesky;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenWeatherMapType implements WeatherType {
    public static final WeatherType SUNNY = new OpenWeatherMapType(800, "01d");
    public static final WeatherType RAINY = new OpenWeatherMapType(520, "09d");
    public static final WeatherType SNOWY = new OpenWeatherMapType(600, "13d");
    
    private final int conditionCode;
    private final String iconId;

    public OpenWeatherMapType(int conditionCode, String iconId) {
        this.conditionCode = conditionCode;
        this.iconId = iconId;
    }

    @Override
    public URL getIcon() {
        try {
            return new URL("http://openweathermap.org/img/w/" + this.iconId + ".png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSunny() {
        return 800 <= this.conditionCode && this.conditionCode < 900;
    }
}
