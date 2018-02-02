package littlesky;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(Main.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((thread, th) -> Dialog.error(th));

        Options options = Options.getInstance();
        if (!options.hasLocation()) {
            this.showSetupDialog(primaryStage);
            
            if (!options.hasLocation()) {
                return;
            }
        }

        this.showMainWindow(primaryStage);
    }
    
    private void showSetupDialog(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/startup.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        SetupController controller = loader.getController();
        controller.setOwnStage(stage);

        stage.setScene(scene);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    private void showMainWindow(Stage primaryStage) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/main.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("littlesky");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
