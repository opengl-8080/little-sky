package littlesky;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClockBase implements Clock {

    protected final ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>();
    protected final ReadOnlyObjectWrapper<LocalTime> time = new ReadOnlyObjectWrapper<>();

    @Override
    public LocalDate getDate() {
        return this.date.get();
    }

    @Override
    public LocalTime getTime() {
        return this.time.get();
    }

    @Override
    public ReadOnlyObjectProperty<LocalDate> dateProperty() {
        return this.date.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<LocalTime> timeProperty() {
        return this.time.getReadOnlyProperty();
    }
}
