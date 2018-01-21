package littlesky;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ToggleGroup;

import static littlesky.BindingBuilder.*;

public class DebugWeather implements Weather {
    private BooleanProperty rainy = new SimpleBooleanProperty();
    private BooleanProperty snowy = new SimpleBooleanProperty();
    private DoubleProperty cloudRate = new SimpleDoubleProperty();
    private DoubleProperty temperature = new SimpleDoubleProperty();
    
    public void bind(ToggleGroup weatherGroup) {
        this.rainy.bind(
            binding(weatherGroup.selectedToggleProperty())
            .computeValue(() -> weatherGroup.getSelectedToggle().getUserData().equals("rainy"))
        );
        this.snowy.bind(
            binding(weatherGroup.selectedToggleProperty())
            .computeValue(() -> weatherGroup.getSelectedToggle().getUserData().equals("snowy"))
        );
    }
    
    public void bindCloud(DoubleProperty cloud) {
        this.cloudRate.bind(cloud);
    }
    
    public void bindTemperature(DoubleProperty temperature) {
        this.temperature.bind(temperature);
    }
    
    @Override
    public boolean isRainy() {
        return this.rainy.get();
    }

    @Override
    public boolean isSnowy() {
        return this.snowy.get();
    }

    @Override
    public double getCloudyRate() {
        return this.cloudRate.get();
    }

    @Override
    public double getTemperature() {
        return this.temperature.get();
    }
}
