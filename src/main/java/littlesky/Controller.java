package littlesky;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    
    private Stage primaryStage;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private Clock clock;
    
    @FXML
    private Label timeLabel;
    @FXML
    private HBox hBox;
    
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;


    @Override
    public void initialize(URL url, ResourceBundle resources) {
//        LocalTime now = LocalTime.now();
//        for (int i=0; i<24; i++) {
//            this.hourComboBox.getItems().add(i);
//        }
//        this.hourComboBox.setValue(now.getHour());
//        for (int i=0; i<60; i++) {
//            this.minuteComboBox.getItems().add(i);
//        }
//        this.minuteComboBox.setValue(now.getMinute());
//        
//        this.clock = new DummyClock(() -> {
//            int hour = this.hourComboBox.getValue();
//            int minute = this.minuteComboBox.getValue();
//            return LocalTime.of(hour, minute);
//        });
        this.clock = new RealTimeClock();
        
        this.timeLabel.textProperty().bind(this.clock.textProperty());
        this.clock.start();

        SunriseSunsetTime sunriseSunsetTime = new SunriseSunsetTime(JapaneseCity.OSAKA, LocalDate.now());
        SkyColor skyColor = new SkyColor(sunriseSunsetTime);
        this.clock.addTask(skyColor::tick);
        
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
    }
}
