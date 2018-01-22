package littlesky;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.paint.Color;

import static littlesky.BindingBuilder.*;

public class TemperatureColor {
    private static final Color STANDARD_COLOR = Color.web("#FFFFFF");
    private static final Color HOT_COLOR = Color.web("#FF3000");
    private static final Color COLD_COLOR = Color.web("#0010FF");
    private static final double HOT_TEMPERATURE = 40.0;
    private static final double COLD_TEMPERATURE = -10.0;
    private static final double STANDARD_TEMPERATURE = 15.0;
    private static final double HOT_RANGE_SIZE = Math.abs(HOT_TEMPERATURE - STANDARD_TEMPERATURE);
    private static final double COLD_RANGE_SIZE = Math.abs(COLD_TEMPERATURE - STANDARD_TEMPERATURE);
    
    private ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>(Color.BLACK);
    
    public void bind(ObservableDoubleValue temperatureProperty) {
        this.color.bind(
            binding(temperatureProperty)
            .computeValue(() -> {
                double temperature = temperatureProperty.get();
                double delta = temperature - STANDARD_TEMPERATURE;
                Color toColor = delta < 0 ? COLD_COLOR : HOT_COLOR;
                double rangeSize = delta < 0 ? COLD_RANGE_SIZE : HOT_RANGE_SIZE;
                return STANDARD_COLOR.interpolate(toColor, Math.abs(delta)/rangeSize);
            })
        );
    }
    
    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color.getReadOnlyProperty();
    }
    
    public Color getColor() {
        return this.color.get();
    }
}
