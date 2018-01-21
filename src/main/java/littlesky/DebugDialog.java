package littlesky;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

public class DebugDialog {
    
    private final Stage stage;
    private final DebugDialogController controller;

    public DebugDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(DebugDialog.class.getResource("/debug-dialog.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            this.stage = new Stage();
            this.stage.setScene(scene);
            this.stage.setTitle("Debug");
            this.stage.initModality(Modality.WINDOW_MODAL);
            this.controller = loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public void show() {
        this.stage.showAndWait();
    }

    public void initOwner(Stage owner) {
        this.stage.initOwner(owner);
    }

    public Clock getDebugClock() {
        return this.controller.getClock();
    }
    
    public Weather getDebugWeather() {
        return this.controller.getWeather();
    }
}
