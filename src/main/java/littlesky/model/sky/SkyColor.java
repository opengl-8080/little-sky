package littlesky.model.sky;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
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
    private final ReadOnlyObjectWrapper<LinearGradient> linearGradient = new ReadOnlyObjectWrapper<>();
    private final ObjectProperty<SkyColorGradation> gradationProperty = new SimpleObjectProperty<>(new SkyColorGradation());
    private final ObjectProperty<SkyColorGradation> topGradationProperty = new SimpleObjectProperty<>(new SkyColorGradation());
    private final Weather weather;
    
    public SkyColor(UserLocation userLocation, Clock clock, Weather weather) {
        this.weather = weather;
        this.gradationProperty.bind(
            binding(clock.dateProperty(), userLocation.latitudeProperty(), userLocation.longitudeProperty())
            .computeValue(() -> createGradation(userLocation, clock.getDate()))
        );
        this.topGradationProperty.bind(
            binding(clock.dateProperty(), userLocation.latitudeProperty(), userLocation.longitudeProperty())
            .computeValue(() -> createTopGradation(userLocation, clock.getDate()))
        );

        this.color.bind(
            binding(clock.timeProperty(), weather.cloudRateProperty())
            .computeValue(() -> this.applyCouldRate(this.gradationProperty.get().at(clock.getTime())))
        );
        
        this.linearGradient.bind(
            binding(clock.timeProperty(), weather.cloudRateProperty())
            .computeValue(() -> {
                Color topColor = this.topGradationProperty.get().at(clock.getTime());
                Color bottomColor = this.gradationProperty.get().at(clock.getTime());
                return LinearGradient.valueOf(topColor + ", " + bottomColor);
            })
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
                .addKeyFrame(sunriseTime.minusMinutes(50), Color.web("#111111"))
                .addKeyFrame(sunriseTime, Color.web("#af3e0a"))
                .addKeyFrame(sunriseTime.plusMinutes(30), Color.web("#ff9616"))
                .addKeyFrame(sunriseTime.plusMinutes(60), Color.web("#ffff82"))
                .addKeyFrame(sunriseTime.plusMinutes(90), Color.web("#f9ffd1"))
                .addKeyFrame(sunriseTime.plusMinutes(120), Color.web("#c1e3ff"))
                
                .addKeyFrame(noonTime, Color.web("#67b9f7"))
                
                .addKeyFrame(sunsetTime.minusMinutes(120), Color.web("#c1e3ff"))
                .addKeyFrame(sunsetTime.minusMinutes(90), Color.web("#f9ffd1"))
                .addKeyFrame(sunsetTime.minusMinutes(60), Color.web("#ffff82"))
                .addKeyFrame(sunsetTime.minusMinutes(30), Color.web("#ff9616"))
                .addKeyFrame(sunsetTime, Color.web("#af3e0a"))
                .addKeyFrame(sunsetTime.plusMinutes(50), Color.web("#111111"))
        ;
        
        return gradation;
    }

    private SkyColorGradation createTopGradation(UserLocation userLocation, LocalDate date) {
        SunriseSunsetTime sunriseSunsetTime = new SunriseSunsetTime(userLocation, date);
        LocalTime sunriseTime = sunriseSunsetTime.sunriseTime();
        LocalTime sunsetTime = sunriseSunsetTime.sunsetTime();

        Duration sunriseDuration = Duration.between(LocalTime.MIN, sunriseTime);
        Duration sunsetDuration = Duration.between(LocalTime.MIN, sunsetTime);
        Duration noonDuration = sunriseDuration.plus(sunsetDuration).dividedBy(2L);
        LocalTime noonTime = LocalTime.MIN.plus(noonDuration);

        logger.debug(() -> "[top color] date=" + date + ", sunrise=" + sunriseTime + ", sunset=" + sunsetTime + ", noon=" + noonTime);

        SkyColorGradation gradation = new SkyColorGradation();

        gradation
                .addKeyFrame(sunriseTime.minusMinutes(50), Color.web("#111111"))
                .addKeyFrame(sunriseTime, Color.web("#1853ad"))
                .addKeyFrame(sunriseTime.plusMinutes(30), Color.web("#336ebf"))
                .addKeyFrame(sunriseTime.plusMinutes(60), Color.web("#7ac3ff"))
                .addKeyFrame(sunriseTime.plusMinutes(90), Color.web("#56b2ff"))
                
                .addKeyFrame(noonTime, Color.web("#67b9f7"))
                
                .addKeyFrame(sunsetTime.minusMinutes(90), Color.web("#56b2ff"))
                .addKeyFrame(sunsetTime.minusMinutes(60), Color.web("#7ac3ff"))
                .addKeyFrame(sunsetTime.minusMinutes(30), Color.web("#336ebf"))
                .addKeyFrame(sunsetTime, Color.web("#1853ad"))
                .addKeyFrame(sunsetTime.plusMinutes(50), Color.web("#111111"))
        ;

        return gradation;
    }

    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color.getReadOnlyProperty();
    }
    
    public LinearGradient getLinearGradient() {
        return this.linearGradient.get();
    }

    public Color getColor() {
        return this.color.get();
    }
    
    public double getBrightness() {
        return this.getColor().getBrightness();
    }
}
