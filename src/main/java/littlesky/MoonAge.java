package littlesky;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static littlesky.BindingBuilder.*;

public class MoonAge {
    private static final LocalDate BASE_DATE = LocalDate.of(1999, 1, 1);
    private static final double AGE_AT_BASE_DATE = 13.17;
    private static final double AVERAGE_OF_MOON_PERIOD = 29.53059;
    private static final double METONIC_CYCLE = 6939.68842;
    
    private final ReadOnlyDoubleWrapper age = new ReadOnlyDoubleWrapper();
    
    public void bind(ObservableValue<LocalDate> date) {
        this.age.bind(
            binding(date).computeValue(() -> this.calculateAge(date.getValue()))
        );
    }
    
    public ReadOnlyDoubleProperty ageProperty() {
        return this.age.getReadOnlyProperty();
    }
    
    public double getAge() {
        return this.age.get();
    }

    private double calculateAge(LocalDate date) {
        long days = Math.abs(BASE_DATE.until(date, ChronoUnit.DAYS)) + 1;
        double normalizedDays = days % METONIC_CYCLE + 0.5; // at noon
        return (AGE_AT_BASE_DATE + normalizedDays) % AVERAGE_OF_MOON_PERIOD;
    }
}
