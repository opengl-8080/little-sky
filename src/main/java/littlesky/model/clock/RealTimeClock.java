package littlesky.model.clock;

import javafx.application.Platform;
import littlesky.model.clock.ClockBase;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeClock extends ClockBase {
    
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
}
