package littlesky.view.setup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import littlesky.Main;
import littlesky.controller.setup.SetupController;

import java.io.IOException;

public class SetupDialog {
    
    public void open(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/setup.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        SetupController controller = loader.getController();
        controller.setOwnStage(stage);

        stage.setTitle("Setup");
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
}
