package littlesky;

import javafx.beans.property.ReadOnlyStringProperty;

import java.time.LocalTime;
import java.util.function.Consumer;

public interface Clock {

    void addTask(Consumer<LocalTime> task);

    void start();

    ReadOnlyStringProperty textProperty();
}
