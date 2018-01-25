package littlesky;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ToggleGroup;

import static littlesky.BindingBuilder.*;

public class DebugWeather extends WeatherBase {
    
    public void bind(ToggleGroup weatherGroup) {
        this.weatherType.bind(
            binding(weatherGroup.selectedToggleProperty())
            .computeValue(() -> {
                Object selected = weatherGroup.getSelectedToggle().getUserData();
                if ("sunny".equals(selected)) {
                    return OpenWeatherMapType.SUNNY;
                } else if ("rainy".equals(selected)) {
                    return OpenWeatherMapType.RAINY;
                } else if ("snowy".equals(selected)) {
                    return OpenWeatherMapType.SNOWY;
                } else {
                    throw new IllegalStateException("unknown weather type = " + selected);
                }
            })
        );
    }
    
    public void bindCloud(DoubleProperty cloud) {
        this.cloudRate.bind(cloud);
    }
    
    public void bindTemperature(DoubleProperty temperature) {
        this.temperature.bind(temperature);
    }
    
}
