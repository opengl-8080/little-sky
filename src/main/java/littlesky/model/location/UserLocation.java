package littlesky.model.location;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import littlesky.InvalidInputException;

import java.util.Objects;
import java.util.TimeZone;

public class UserLocation {
    
    private ReadOnlyDoubleWrapper longitude = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper latitude = new ReadOnlyDoubleWrapper();
    private ReadOnlyObjectWrapper<TimeZone> timeZone = new ReadOnlyObjectWrapper<>();
    
    public UserLocation(double latitude, double longitude, TimeZone timeZone) throws InvalidInputException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setTimeZone(timeZone);
    }

    public double getLongitude() {
        return longitude.get();
    }

    public void setLongitude(double longitude) {
        if (longitude < -180.0 || 180.0 < longitude) {
            throw new InvalidInputException("Longitude must be between -180.0 and 180.0.");
        }
        this.longitude.set(longitude);
    }

    public ReadOnlyDoubleProperty longitudeProperty() {
        return this.longitude.getReadOnlyProperty();
    }

    public double getLatitude() {
        return latitude.get();
    }

    public void setLatitude(double latitude) {
        if (latitude < -90.0 || 90.0 < latitude) {
            throw new InvalidInputException("Latitude must be between -90.0 and 90.0.");
        }
        this.latitude.set(latitude);
    }

    public ReadOnlyDoubleProperty latitudeProperty() {
        return this.latitude.getReadOnlyProperty();
    }

    public TimeZone getTimeZone() {
        return timeZone.get();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone.set(Objects.requireNonNull(timeZone));
    }
    
    public ReadOnlyObjectProperty<TimeZone> timeZoneProperty() {
        return this.timeZone.getReadOnlyProperty();
    }
}
