package littlesky;

public class OpenWeatherMap extends WeatherBase {
 
    public OpenWeatherMap() {
        this.weatherType.set(WeatherType.SUNNY);
        this.temperature.setValue(15.0);
    }
}
