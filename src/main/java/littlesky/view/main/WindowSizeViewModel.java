package littlesky.view.main;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import littlesky.model.option.ViewOptions;

import static littlesky.util.BindingBuilder.*;

public class WindowSizeViewModel {
    
    private final ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper();
    
    public void bind(ViewOptions viewOptions) {
        this.width.bind(
            binding(viewOptions.showSecondsProperty(), viewOptions.showSkyStatusIconProperty(), viewOptions.showTemperatureProperty())
            .computeValue(() -> {
                double width = 30.0;
                width += viewOptions.isShowSeconds() ? 90.0 : 60.0;
                width += viewOptions.isShowSkyStatusIcon() ? 30.0 : 0.0;
                width += viewOptions.isShowTemperature() ? 10.0 : 0.0;
                return width;
            })
        );
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return width.getReadOnlyProperty();
    }
}
