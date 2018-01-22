package littlesky;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ToggleGroup;

import static littlesky.BindingBuilder.*;
import static littlesky.WeatherType.*;

public class DebugWeather extends WeatherBase {
    
    public void bind(ToggleGroup weatherGroup) {
        this.weatherType.bind(
            binding(weatherGroup.selectedToggleProperty())
            .computeValue(() -> {
                Object selected = weatherGroup.getSelectedToggle().getUserData();
                if ("sunny".equals(selected)) {
                    return SUNNY;
                } else if ("rainy".equals(selected)) {
                    return RAINY;
                } else if ("snowy".equals(selected)) {
                    return SNOWY;
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
