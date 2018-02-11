package littlesky.model.weather.owm;

import littlesky.model.option.Options;
import littlesky.model.weather.WeatherType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class OpenWeatherMapType implements WeatherType {
    public static final WeatherType SUNNY = new OpenWeatherMapType(800, "01d");
    public static final WeatherType RAINY = new OpenWeatherMapType(520, "09d");
    public static final WeatherType SNOWY = new OpenWeatherMapType(600, "13d");
    
    private final int conditionCode;
    private final String iconId;
    private final Options options = Options.getInstance();

    public OpenWeatherMapType(int conditionCode, String iconId) {
        this.conditionCode = conditionCode;
        this.iconId = iconId;
    }

    @Override
    public InputStream getIconStream() {
        try {
            URL url = new URL("http://openweathermap.org/img/w/" + this.iconId + ".png");
            Proxy proxy = this.options.getHttpProxy().toProxy();
            HttpURLConnection con = (HttpURLConnection)url.openConnection(proxy);
            return con.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean isSunny() {
        return 800 <= this.conditionCode && this.conditionCode < 900;
    }

    @Override
    public String toString() {
        return "OpenWeatherMapType{" +
                "conditionCode=" + conditionCode +
                ", iconId='" + iconId + '\'' +
                ", options=" + options +
                '}';
    }
}
