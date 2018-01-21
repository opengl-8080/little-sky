package littlesky;

import javafx.beans.property.ReadOnlyObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Clock {

    LocalDate getDate();
    LocalTime getTime();
    ReadOnlyObjectProperty<LocalDate> dateProperty();
    ReadOnlyObjectProperty<LocalTime> timeProperty();
}
