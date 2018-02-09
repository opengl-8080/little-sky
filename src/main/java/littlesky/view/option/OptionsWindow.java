package littlesky.view.option;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import littlesky.controller.option.OptionsController;

import java.io.IOException;
import java.io.UncheckedIOException;

public class OptionsWindow {
    
    public void open(Stage owner) {
        FXMLLoader loader = new FXMLLoader(OptionsWindow.class.getResource("/options.fxml"));
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Options");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);

            OptionsController controller = loader.getController();
            controller.setOwnStage(stage);
            stage.show();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
