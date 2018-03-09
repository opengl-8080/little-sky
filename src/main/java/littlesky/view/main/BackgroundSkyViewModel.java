package littlesky.view.main;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import littlesky.model.sky.SkyColor;

import static littlesky.util.BindingBuilder.*;

public class BackgroundSkyViewModel {
    private static final CornerRadii WINDOW_CORNER_RADII = new CornerRadii(10.0);
    
    private final ReadOnlyObjectWrapper<Background> background = new ReadOnlyObjectWrapper<>(background(Color.WHITE));
    
    public void bind(SkyColor skyColor) {
        this.background.bind(
            binding(skyColor.colorProperty())
            .computeValue(() -> background(skyColor.getLinearGradient()))
        );
    }
    
    public ReadOnlyObjectProperty<Background> backgroundProperty() {
        return this.background.getReadOnlyProperty();
    }

    private Background background(Paint paint) {
        BackgroundFill fill = new BackgroundFill(paint, WINDOW_CORNER_RADII, null);
        return new Background(fill);
    }
}
