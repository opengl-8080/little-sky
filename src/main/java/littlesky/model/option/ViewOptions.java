package littlesky.model.option;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ViewOptions {
    private final BooleanProperty alwaysOnTop = new SimpleBooleanProperty();
    private final BooleanProperty showSeconds = new SimpleBooleanProperty();
    private final BooleanProperty showTemperature = new SimpleBooleanProperty();
    private final BooleanProperty showSkyStatusIcon = new SimpleBooleanProperty();

    public boolean isAlwaysOnTop() {
        return alwaysOnTop.get();
    }

    public BooleanProperty alwaysOnTopProperty() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop.set(alwaysOnTop);
    }

    public boolean isShowSeconds() {
        return showSeconds.get();
    }

    public BooleanProperty showSecondsProperty() {
        return showSeconds;
    }

    public void setShowSeconds(boolean showSeconds) {
        this.showSeconds.set(showSeconds);
    }

    public boolean isShowTemperature() {
        return showTemperature.get();
    }

    public BooleanProperty showTemperatureProperty() {
        return showTemperature;
    }

    public void setShowTemperature(boolean showTemperature) {
        this.showTemperature.set(showTemperature);
    }

    public boolean isShowSkyStatusIcon() {
        return showSkyStatusIcon.get();
    }

    public BooleanProperty showSkyStatusIconProperty() {
        return showSkyStatusIcon;
    }

    public void setShowSkyStatusIcon(boolean showSkyStatusIcon) {
        this.showSkyStatusIcon.set(showSkyStatusIcon);
    }
}
