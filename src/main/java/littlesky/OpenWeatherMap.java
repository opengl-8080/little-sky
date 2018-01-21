package littlesky;

public class OpenWeatherMap implements Weather {

    @Override
    public boolean isRainy() {
        return false;
    }

    @Override
    public boolean isSnowy() {
        return false;
    }

    @Override
    public double getCloudyRate() {
        return 0;
    }

    @Override
    public double getTemperature() {
        return 0;
    }
}
