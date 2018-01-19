package littlesky;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    
    private Stage primaryStage;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private DebugDialog debugDialog = new DebugDialog();
    private RealTimeClock realTimeClock;
    
    @FXML
    private CheckMenuItem alwaysOnTopMenuItem;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox hBox;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        this.realTimeClock = new RealTimeClock();
        this.replaceClock(this.realTimeClock);
        this.realTimeClock.start();
    }
    
    private void replaceClock(Clock newClock) {
        this.timeLabel.textProperty().bind(new TimeFormatBinder("HH:mm:ss", newClock.timeProperty()));
        SkyColor skyColor = new SkyColor(JapaneseCity.OSAKA, newClock);
        this.hBox.backgroundProperty().bind(skyColor.backgroundProperty());
    }
    
    @FXML
    public void onMousePressed(MouseEvent event) {
        this.mouseOffsetX = this.primaryStage.getX() - event.getScreenX();
        this.mouseOffsetY = this.primaryStage.getY() - event.getScreenY();
    }
    
    @FXML
    public void onMouseDragged(MouseEvent event) {
        this.primaryStage.setX(event.getScreenX() + this.mouseOffsetX);
        this.primaryStage.setY(event.getScreenY() + this.mouseOffsetY);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.debugDialog.initOwner(primaryStage);
    }
    
    @FXML
    public void close() {
        Platform.exit();
    }
    
    @FXML
    public void openDebug() {
        this.replaceClock(this.debugDialog.getDebugClock());
        this.debugDialog.show();
        this.replaceClock(this.realTimeClock);
    }
    
    @FXML
    public void changeAlwaysOnTop() {
        this.primaryStage.setAlwaysOnTop(this.alwaysOnTopMenuItem.isSelected());
    }
}
