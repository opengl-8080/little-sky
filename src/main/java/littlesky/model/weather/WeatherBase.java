package littlesky.model.weather;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

abstract public class WeatherBase implements Weather {
    
    protected final ReadOnlyDoubleWrapper cloudRate = new ReadOnlyDoubleWrapper();
    protected final ReadOnlyDoubleWrapper temperature = new ReadOnlyDoubleWrapper();
    protected final ReadOnlyObjectWrapper<WeatherType> weatherType = new ReadOnlyObjectWrapper<>();

    @Override
    public WeatherType getWeatherType() {
        return this.weatherType.get();
    }

    @Override
    public double getCloudRate() {
        return this.cloudRate.get();
    }

    @Override
    public double getTemperature() {
        return this.temperature.get();
    }

    @Override
    public ReadOnlyObjectProperty<WeatherType> weatherTypeProperty() {
        return this.weatherType.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyDoubleProperty cloudRateProperty() {
        return this.cloudRate.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyDoubleProperty temperatureProperty() {
        return this.temperature.getReadOnlyProperty();
    }
}
