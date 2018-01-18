package littlesky;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RealTimeClock implements Clock {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    private ReadOnlyStringWrapper text = new ReadOnlyStringWrapper();
    private List<Consumer<LocalTime>> tasks = new ArrayList<>();
    
    @Override
    public void addTask(Consumer<LocalTime> task) {
        this.tasks.add(task);
    }

    @Override
    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        
        executor.scheduleAtFixedRate(this::tick, 0, 500, TimeUnit.MILLISECONDS);
    }
    
    private void tick() {
        Platform.runLater(() -> {
            LocalTime now = this.now();
            this.text.setValue(now.format(formatter));
            this.tasks.forEach(task -> task.accept(now));
        });
    }
    
    protected LocalTime now() {
        return LocalTime.now();
    }

    @Override
    public ReadOnlyStringProperty textProperty() {
        return this.text.getReadOnlyProperty();
    }
}
