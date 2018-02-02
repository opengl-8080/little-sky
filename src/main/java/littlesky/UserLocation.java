package littlesky;

public class UserLocation {
    
    private double longitude;
    private double latitude;
    
    public UserLocation(double latitude, double longitude) throws InvalidInputException {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
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
}
