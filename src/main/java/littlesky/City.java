package littlesky;

import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.TimeZone;

public interface City {

    String country();
    String label();
    Location location();
    TimeZone timeZone();
}
