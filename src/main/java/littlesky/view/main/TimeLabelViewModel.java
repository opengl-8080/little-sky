package littlesky.view.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.paint.Color;
import littlesky.model.clock.Clock;
import littlesky.model.sky.SkyColor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static littlesky.util.BindingBuilder.*;

public class TimeLabelViewModel {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper("00:00:00");
    private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>(Color.BLACK);
    
    public void bind(Clock clock, SkyColor skyColor) {
        this.text.bind(
            binding(clock.timeProperty())
            .computeValue(() -> this.formatClockTime(clock))
        );
        
        this.color.bind(
            binding(skyColor.colorProperty())
            .computeValue(() -> this.decideTimeFontColor(skyColor.getBrightness()))
        );
    }
    
    private String formatClockTime(Clock clock) {
        LocalTime time = clock.getTime();
        return time.format(formatter);
    }
    
    private Color decideTimeFontColor(double skyBrightness) {
        if (skyBrightness < 0.4) {
            return Color.WHITE;
        } else if (0.4 <= skyBrightness && skyBrightness < 0.6) {
            return Color.LIGHTGRAY;
        } else if (0.6 <= skyBrightness && skyBrightness < 0.7) {
            return Color.web("#333");
        } else {
            return Color.BLACK;
        }
    }
    
    public ReadOnlyStringProperty textProperty() {
        return this.text.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color.getReadOnlyProperty();
    }
}
