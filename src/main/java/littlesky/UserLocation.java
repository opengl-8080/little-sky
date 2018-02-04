package littlesky;

import java.util.Objects;
import java.util.TimeZone;

public class UserLocation {
    
    private double longitude;
    private double latitude;
    private TimeZone timeZone;
    
    public UserLocation(double latitude, double longitude, TimeZone timeZone) throws InvalidInputException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setTimeZone(timeZone);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180.0 || 180.0 < longitude) {
            throw new InvalidInputException("Longitude must be between -180.0 and 180.0.");
        }
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (latitude < -90.0 || 90.0 < latitude) {
            throw new InvalidInputException("Latitude must be between -90.0 and 90.0.");
        }
        this.latitude = latitude;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = Objects.requireNonNull(timeZone);
    }
}
