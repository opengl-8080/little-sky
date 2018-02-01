package littlesky;

public class UserLocation {
    
    private static UserLocation instance;

    public static void load(double longitude, double latitude) throws InvalidInputException {
        if (instance != null) {
            throw new IllegalStateException("UserLocation is already initialized.");
        }
        UserLocation location = new UserLocation();
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        instance = location;
    }
    
    public static UserLocation getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserLocation is not initialized.");
        }
        return instance;
    }

    public static void validateLongitude(double longitude) throws InvalidInputException {
        if (longitude < -180.0 || 180.0 < longitude) {
            throw new InvalidInputException("Longitude must be between -180.0 and 180.0.");
        }
    }
    
    public static void validateLatitude(double latitude) throws InvalidInputException {
        if (latitude < -90.0 || 90.0 < latitude) {
            throw new InvalidInputException("Latitude must be between -90.0 and 90.0.");
        }
    }
    
    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        validateLongitude(longitude);
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        validateLatitude(latitude);
        this.latitude = latitude;
    }
    
    private UserLocation() {}
}
