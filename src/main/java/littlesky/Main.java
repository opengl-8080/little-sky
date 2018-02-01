package littlesky;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(Main.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((thread, th) -> ErrorDialog.show(th));

        Options options = Options.getInstance();
        if (!options.hasLocation()) {
            // TODO open initialize location dialog.
        }
        
        UserLocation.load(options.getLongitude(), options.getLatitude());

        this.showMainWindow(primaryStage);
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
