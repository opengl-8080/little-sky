package littlesky.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:ss:SS");
    private static final Logger instance = new Logger();
    
    public static Logger getInstance() {
        return Logger.instance;
    }

    private boolean debug;
    
    public synchronized void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public synchronized void debug(Supplier<String> messageSupplier) {
        if (!this.debug) {
            return;
        }

        String message = messageSupplier.get();
        this.debug(message);
    }

    public synchronized void debug(String message) {
        if (!this.debug) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String time = now.format(formatter);
        System.out.println("[" + time + "] " + message);
    }
}
