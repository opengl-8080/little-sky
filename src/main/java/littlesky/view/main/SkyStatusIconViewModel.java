package littlesky.view.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.image.Image;
import littlesky.model.moon.MoonPhase;
import littlesky.model.weather.Weather;
import littlesky.model.weather.WeatherType;

import static littlesky.util.BindingBuilder.*;

public class SkyStatusIconViewModel {
    
    private ReadOnlyObjectWrapper<Image> image = new ReadOnlyObjectWrapper<>();
    
    public void bind(MoonPhase moonPhase, Weather weather) {
        this.image.bind(
            binding(weather.weatherTypeProperty(), moonPhase.phaseProperty())
            .computeValue(() -> {
                WeatherType weatherType = weather.getWeatherType();
                if (weatherType.isSunny()) {
                    return this.getMoonImage(moonPhase);
                } else {
                    return new Image(weatherType.getIconStream());
                }
            })
        );
    }
    
    public ReadOnlyObjectProperty<Image> imageProperty() {
        return this.image.getReadOnlyProperty();
    }
    
    private Image getMoonImage(MoonPhase moonPhase) {
        double phase = moonPhase.getPhase();
        int imageNo = (int)(15*phase);
        return new Image("/moon_" + imageNo + ".png");
    }
}
