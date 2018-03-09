package littlesky.view.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.paint.Color;
import littlesky.model.clock.Clock;
import littlesky.model.option.ViewOptions;
import littlesky.model.sky.SkyColor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static littlesky.util.BindingBuilder.*;

public class TimeLabelViewModel {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("HH:mm");
    private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper("00:00:00");
    private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>(Color.BLACK);
    
    public void bind(Clock clock, SkyColor skyColor, ViewOptions viewOptions) {
        this.text.bind(
            binding(clock.timeProperty(), viewOptions.showSecondsProperty())
            .computeValue(() -> this.formatClockTime(clock, viewOptions))
        );
        
        this.color.bind(
            binding(skyColor.colorProperty())
            .computeValue(() -> this.decideTimeFontColor(skyColor.getBrightness()))
        );
    }
    
    private String formatClockTime(Clock clock, ViewOptions viewOptions) {
        LocalTime time = clock.getTime();
        return time.format(viewOptions.isShowSeconds() ? formatter : formatterWithoutSeconds);
    }
    
    private Color decideTimeFontColor(double skyBrightness) {
        if (skyBrightness < 0.5) {
            return Color.WHITE;
        } else if (0.5 <= skyBrightness && skyBrightness < 0.7) {
            return Color.LIGHTGRAY;
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
