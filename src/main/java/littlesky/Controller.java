package littlesky;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static littlesky.BindingBuilder.*;
import static littlesky.WeatherType.*;

public class Controller implements Initializable {
    private static final CornerRadii WINDOW_CORNER_RADII = new CornerRadii(10.0);
    
    private Stage primaryStage;
    private double mouseOffsetX;
    private double mouseOffsetY;
    
    private OptionsWindow optionsWindow;
    private DebugDialog debugDialog;
    private RealTimeClock realTimeClock;
    private OpenWeatherMap openWeatherMap;
    private MoonAge moonAge;
    
    @FXML
    private CheckMenuItem alwaysOnTopMenuItem;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox hBox;
    @FXML
    private ImageView moonAgeImageView;
    @FXML
    private Pane temperaturePane;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        this.debugDialog = new DebugDialog();
        this.optionsWindow = new OptionsWindow();
        this.moonAge = new MoonAge();
        this.openWeatherMap = new OpenWeatherMap();
        
        this.realTimeClock = new RealTimeClock();
        this.replaceClockAndWeather(this.realTimeClock, this.openWeatherMap);
        this.realTimeClock.start();
    }
    
    private void replaceClockAndWeather(Clock newClock, Weather weather) {
        SkyColor skyColor = new SkyColor(JapaneseCity.OSAKA, newClock, weather);
        
        this.timeLabel.textProperty().bind(
            binding(newClock.timeProperty()).computeValue(this.formatClockTime(newClock))
        );
        this.timeLabel.textFillProperty().bind(
            binding(newClock.timeProperty())
            .computeValue(() -> {
                double brightness = skyColor.getBrightness();
                return this.decideTimeFontColor(brightness);
            })
        );
        
        this.hBox.backgroundProperty().bind(
            binding(skyColor.colorProperty()).computeValue(() -> background(skyColor.getColor()))
        );
        
        this.moonAge.bind(newClock.dateProperty());
        this.moonAgeImageView.imageProperty().bind(
            binding(this.moonAge.ageProperty(), weather.weatherTypeProperty())
            .computeValue(() -> {
                WeatherType weatherType = weather.getWeatherType();
                
                if (weatherType == SUNNY) {
                    return this.getMoonImage();
                } else {
                    return weatherType.getImage();
                }
            })
        );

        TemperatureColor temperatureColor = new TemperatureColor();
        temperatureColor.bind(weather.temperatureProperty());
        this.temperaturePane.backgroundProperty().bind(
            binding(temperatureColor.colorProperty())
            .computeValue(() -> {
                BackgroundFill fill = new BackgroundFill(temperatureColor.getColor(), new CornerRadii(5.0), null);
                return new Background(fill);
            })
        );
    }
    
    private Image getMoonImage() {
        double age = this.moonAge.getAge();
        double rate = age/29.0;
        int imageNo = (int)(15*rate);
        return new Image("/moon_" + imageNo + ".png");
    }
    
    private Color decideTimeFontColor(double skyBrightness) {
        if (skyBrightness < 0.4) {
            return Color.WHITE;
        } else if (0.4 <= skyBrightness && skyBrightness < 0.6) {
            return Color.LIGHTGRAY;
        } else if (0.6 <= skyBrightness && skyBrightness < 0.7) {
            return Color.web("#333");
        } else {
            return Color.BLACK;
        }
    }
    
    private Supplier<String> formatClockTime(Clock clock) {
        return () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = clock.getTime();
            return time.format(formatter);
        };
    }
    
    private Background background(Color color) {
        BackgroundFill fill = new BackgroundFill(color, WINDOW_CORNER_RADII, null);
        return new Background(fill);
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
        this.replaceClockAndWeather(this.debugDialog.getDebugClock(), this.debugDialog.getDebugWeather());
        this.debugDialog.show();
        this.replaceClockAndWeather(this.realTimeClock, this.openWeatherMap);
    }
    
    @FXML
    public void changeAlwaysOnTop() {
        this.primaryStage.setAlwaysOnTop(this.alwaysOnTopMenuItem.isSelected());
    }
    
    @FXML
    public void openOptions() {
        this.optionsWindow.open(this.primaryStage);
    }
}
