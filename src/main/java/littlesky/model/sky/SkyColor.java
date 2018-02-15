package littlesky.model.sky;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import littlesky.model.clock.Clock;
import littlesky.model.location.UserLocation;
import littlesky.model.sun.SunriseSunsetTime;
import littlesky.model.weather.Weather;
import littlesky.util.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static littlesky.util.BindingBuilder.*;

public class SkyColor {
    private static final Logger logger = Logger.getInstance();
    private static final double MIN_SATURATION_AT_FULL_CLOUDY = 0.0;
    private static final double MIN_BRIGHTNESS_AT_FULL_CLOUDY = 0.7;
    
    private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>();
    private final ObjectProperty<SkyColorGradation> gradationProperty = new SimpleObjectProperty<>(new SkyColorGradation());
    private final Weather weather;
    
    public SkyColor(UserLocation userLocation, Clock clock, Weather weather) {
        this.weather = weather;
        this.gradationProperty.bind(
            binding(clock.dateProperty(), userLocation.latitudeProperty(), userLocation.longitudeProperty())
            .computeValue(() -> createGradation(userLocation, clock.getDate()))
        );
        
        this.color.bind(
            binding(clock.timeProperty(), weather.cloudRateProperty())
            .computeValue(() -> this.applyCouldRate(this.gradationProperty.get().at(clock.getTime())))
        );
    }
    
    private Color applyCouldRate(Color baseSkyColor) {
        double cloudRate = this.weather.getCloudRate();
        double saturationFactor = 1.0 - (1.0 - MIN_SATURATION_AT_FULL_CLOUDY) * cloudRate;
        double brightnessFactor = 1.0 - (1.0 - MIN_BRIGHTNESS_AT_FULL_CLOUDY) * cloudRate;
        return baseSkyColor.deriveColor(0, saturationFactor, brightnessFactor, 1.0);
    }

    private SkyColorGradation createGradation(UserLocation userLocation, LocalDate date) {
        SunriseSunsetTime sunriseSunsetTime = new SunriseSunsetTime(userLocation, date);
        LocalTime sunriseTime = sunriseSunsetTime.sunriseTime();
        LocalTime sunsetTime = sunriseSunsetTime.sunsetTime();

        Duration sunriseDuration = Duration.between(LocalTime.MIN, sunriseTime);
        Duration sunsetDuration = Duration.between(LocalTime.MIN, sunsetTime);
        Duration noonDuration = sunriseDuration.plus(sunsetDuration).dividedBy(2L);
        LocalTime noonTime = LocalTime.MIN.plus(noonDuration);
        
        logger.debug(() -> "date=" + date + ", sunrise=" + sunriseTime + ", sunset=" + sunsetTime + ", noon=" + noonTime);

        SkyColorGradation gradation = new SkyColorGradation();
        
        gradation
                .addKeyFrame(sunriseTime.minusMinutes(120), Color.web("#111111"))
                .addKeyFrame(sunriseTime.minusMinutes(90), Color.web("#4d548a"))
                .addKeyFrame(sunriseTime.minusMinutes(60), Color.web("#c486b1"))
                .addKeyFrame(sunriseTime.minusMinutes(30), Color.web("#ee88a0"))
                .addKeyFrame(sunriseTime, Color.web("#ff7d75"))
                .addKeyFrame(sunriseTime.plusMinutes(30), Color.web("#f4eeef"))
                .addKeyFrame(noonTime, Color.web("#5dc9f1"))
                .addKeyFrame(sunsetTime.minusMinutes(90), Color.web("#9eefe0"))
                .addKeyFrame(sunsetTime.minusMinutes(60), Color.web("#f1e17c"))
                .addKeyFrame(sunsetTime.minusMinutes(30), Color.web("#f86b10"))
                .addKeyFrame(sunsetTime, Color.web("#100028"))
                .addKeyFrame(sunsetTime.plusMinutes(30), Color.web("#111111"))
        ;
        
        return gradation;
    }

    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color.getReadOnlyProperty();
    }

    public Color getColor() {
        return this.color.get();
    }
    
    public double getBrightness() {
        return this.getColor().getBrightness();
    }
}
