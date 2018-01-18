package littlesky;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalTime;

public class SkyColor {
    
    private final SkyColorGradation gradation = new SkyColorGradation();
    private final ReadOnlyObjectWrapper<Background> background = new ReadOnlyObjectWrapper<>();
    
    public SkyColor(SunriseSunsetTime sunriseSunsetTime) {
        LocalTime sunriseTime = sunriseSunsetTime.sunriseTime();
        LocalTime sunsetTime = sunriseSunsetTime.sunsetTime();

        Duration sunriseDuration = Duration.between(LocalTime.MIN, sunriseTime);
        Duration sunsetDuration = Duration.between(LocalTime.MIN, sunsetTime);
        Duration noonDuration = sunriseDuration.plus(sunsetDuration).dividedBy(2L);
        LocalTime noonTime = LocalTime.MIN.plus(noonDuration);

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
    
    public void tick(LocalTime time) {
        Color color = this.gradation.at(time);
        this.background.set(new Background(new BackgroundFill(color, null, null)));
    }
    
    public ReadOnlyObjectProperty<Background> backgroundProperty() {
        return this.background.getReadOnlyProperty();
    }
}
