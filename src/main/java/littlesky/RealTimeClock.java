package littlesky;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeClock implements Clock {

    private final ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<LocalTime> time = new ReadOnlyObjectWrapper<>();
    
    public RealTimeClock() {
        this.updateDateTime();
    }
    
    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        
        executor.scheduleAtFixedRate(() -> {
            Platform.runLater(this::updateDateTime);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        this.date.set(now.toLocalDate());
        this.time.set(now.toLocalTime());
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
