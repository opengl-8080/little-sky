package littlesky;

import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatBinder extends StringBinding {
    
    private final DateTimeFormatter formatter;
    private final ObservableValue<LocalTime> value;

    public TimeFormatBinder(String pattern, ObservableValue<LocalTime> time) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        this.value = time;
        this.bind(time);
    }
    
    @Override
    protected String computeValue() {
        LocalTime time = this.value.getValue();
        if (time == null) {
            return "";
        }
        return time.format(this.formatter);
    }
}
