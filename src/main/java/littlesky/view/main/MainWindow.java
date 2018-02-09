package littlesky.view.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import littlesky.Main;
import littlesky.controller.main.MainController;

public class MainWindow {

    public void open(Stage primaryStage) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("littlesky");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
