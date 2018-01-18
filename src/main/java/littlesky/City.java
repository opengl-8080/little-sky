package littlesky;

import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.TimeZone;

public interface City {
    
    Location location();
    TimeZone timeZone();
}
