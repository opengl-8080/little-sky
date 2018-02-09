package littlesky.model.moon;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static littlesky.util.BindingBuilder.*;

public class MoonPhase {
    private static final LocalDate BASE_DATE = LocalDate.of(1999, 1, 1);
    private static final double AGE_AT_BASE_DATE = 13.17;
    private static final double AVERAGE_OF_MOON_PERIOD = 29.53059;
    private static final double METONIC_CYCLE = 6939.68842;
    
    private final ReadOnlyDoubleWrapper phase = new ReadOnlyDoubleWrapper();
    
    public void bind(ObservableValue<LocalDate> date) {
        this.phase.bind(
            binding(date).computeValue(() -> this.calculatePhase(date.getValue()))
        );
    }
    
    public ReadOnlyDoubleProperty phaseProperty() {
        return this.phase.getReadOnlyProperty();
    }
    
    public double getPhase() {
        return this.phase.get();
    }

    private double calculatePhase(LocalDate date) {
        long days = Math.abs(BASE_DATE.until(date, ChronoUnit.DAYS));
        double normalizedDays = days % METONIC_CYCLE + 0.5; // at noon
        double age = (AGE_AT_BASE_DATE + normalizedDays) % AVERAGE_OF_MOON_PERIOD;
        return age / AVERAGE_OF_MOON_PERIOD;
    }
}
