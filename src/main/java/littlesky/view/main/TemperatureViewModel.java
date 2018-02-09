package littlesky.view.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import littlesky.model.weather.Weather;

import static littlesky.util.BindingBuilder.*;

public class TemperatureViewModel {
    private static final Color STANDARD_COLOR = Color.web("#FFFFFF");
    private static final Color HOT_COLOR = Color.web("#FF3000");
    private static final Color COLD_COLOR = Color.web("#0010FF");
    private static final double HOT_TEMPERATURE = 40.0;
    private static final double COLD_TEMPERATURE = -10.0;
    private static final double STANDARD_TEMPERATURE = 15.0;
    private static final double HOT_RANGE_SIZE = Math.abs(HOT_TEMPERATURE - STANDARD_TEMPERATURE);
    private static final double COLD_RANGE_SIZE = Math.abs(COLD_TEMPERATURE - STANDARD_TEMPERATURE);
    
    private final ReadOnlyObjectWrapper<Background> background = new ReadOnlyObjectWrapper<>(background(Color.WHITE));
    
    public void bind(Weather weather) {
        this.background.bind(
            binding(weather.temperatureProperty())
            .computeValue(() -> {
                double temperature = weather.getTemperature();
                double delta = temperature - STANDARD_TEMPERATURE;
                Color toColor = delta < 0 ? COLD_COLOR : HOT_COLOR;
                double rangeSize = delta < 0 ? COLD_RANGE_SIZE : HOT_RANGE_SIZE;
                Color color = STANDARD_COLOR.interpolate(toColor, Math.abs(delta) / rangeSize);
                return background(color);
            })
        );
    }

    public ReadOnlyObjectProperty<Background> backgroundProperty() {
        return this.background.getReadOnlyProperty();
    }

    private Background background(Color color) {
        BackgroundFill fill = new BackgroundFill(color, new CornerRadii(5.0), null);
        return new Background(fill);
    }
}
