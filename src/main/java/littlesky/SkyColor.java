package littlesky;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class SkyColor {
    
    private SkyColorGradation gradation;
    private final ReadOnlyObjectWrapper<Background> background = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>();
    
    public SkyColor(City city, Clock clock) {
        clock.dateProperty().addListener((value, oldDate, newDate) -> {
            this.updateGradation(city, newDate);
            this.updateBackgroundColor(clock.timeProperty().get());
        });

        clock.timeProperty().addListener((value, oldTime, newTime) -> {
            this.updateBackgroundColor(newTime);
        });
        
        this.updateGradation(city, clock.dateProperty().get());
        this.updateBackgroundColor(clock.timeProperty().get());
    }
    
    private void updateGradation(City city, LocalDate date) {
        SunriseSunsetTime sunriseSunsetTime = new SunriseSunsetTime(city, date);
        LocalTime sunriseTime = sunriseSunsetTime.sunriseTime();
        LocalTime sunsetTime = sunriseSunsetTime.sunsetTime();

        Duration sunriseDuration = Duration.between(LocalTime.MIN, sunriseTime);
        Duration sunsetDuration = Duration.between(LocalTime.MIN, sunsetTime);
        Duration noonDuration = sunriseDuration.plus(sunsetDuration).dividedBy(2L);
        LocalTime noonTime = LocalTime.MIN.plus(noonDuration);

        this.gradation = new SkyColorGradation();
        this.gradation
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
    }
    
    private void updateBackgroundColor(LocalTime time) {
        Color color = this.gradation.at(time);
        this.color.set(color);
        this.background.set(new Background(new BackgroundFill(color, new CornerRadii(10.0), null)));
    }
    
    public ReadOnlyObjectProperty<Background> backgroundProperty() {
        return this.background.getReadOnlyProperty();
    }

    public Color getColor() {
        return this.color.get();
    }
}
