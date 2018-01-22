package littlesky;

import javafx.scene.image.Image;

public enum WeatherType {
    SUNNY(null),
    RAINY("/rainy.png"),
    SNOWY("/snowy.png"),
    ;
    
    private final Image image;

    WeatherType(String path) {
        this.image = path != null ? new Image(path) : null;
    }

    public Image getImage() {
        if (this.image == null) {
            throw new IllegalStateException("image is null");
        }
        return this.image;
    }
}
