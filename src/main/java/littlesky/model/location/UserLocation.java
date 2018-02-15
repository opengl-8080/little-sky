package littlesky.model.location;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import littlesky.InvalidInputException;

public class UserLocation {
    
    private ReadOnlyDoubleWrapper longitude = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper latitude = new ReadOnlyDoubleWrapper();
    
    public UserLocation(double latitude, double longitude) throws InvalidInputException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
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
}
