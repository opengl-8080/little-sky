package littlesky;

import java.time.LocalTime;
import java.util.function.Supplier;

public class DummyClock extends RealTimeClock {

    private final Supplier<LocalTime> timeSupplier;

    public DummyClock(Supplier<LocalTime> timeSupplier) {
        this.timeSupplier = timeSupplier;
    }

    @Override
    protected LocalTime now() {
        return this.timeSupplier.get();
    }
}
