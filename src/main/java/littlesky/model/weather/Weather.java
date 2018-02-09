package littlesky.model.weather;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface Weather {
    
    WeatherType getWeatherType();
    double getCloudRate();
    double getTemperature();
    
    ReadOnlyObjectProperty<WeatherType> weatherTypeProperty();
    ReadOnlyDoubleProperty cloudRateProperty();
    ReadOnlyDoubleProperty temperatureProperty();
}
