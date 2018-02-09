package littlesky.controller.main;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import littlesky.view.main.BackgroundSkyViewModel;
import littlesky.view.debug.DebugDialog;
import littlesky.model.moon.MoonPhase;
import littlesky.model.weather.owm.OpenWeatherMap;
import littlesky.model.option.Options;
import littlesky.view.option.OptionsWindow;
import littlesky.model.clock.RealTimeClock;
import littlesky.model.sky.SkyColor;
import littlesky.view.main.SkyStatusIconViewModel;
import littlesky.view.main.TemperatureViewModel;
import littlesky.view.main.TimeLabelViewModel;
import littlesky.model.weather.Weather;
import littlesky.model.clock.Clock;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Stage primaryStage;
    private double mouseOffsetX;
    private double mouseOffsetY;
    
    private OptionsWindow optionsWindow;
    private DebugDialog debugDialog;
    private RealTimeClock realTimeClock;
    private OpenWeatherMap openWeatherMap;
    private MoonPhase moonPhase;
    private Options options;
    
    @FXML
    private CheckMenuItem alwaysOnTopMenuItem;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox backgroundSkyHBox;
    @FXML
    private ImageView skyStatusIconImageView;
    @FXML
    private Pane temperaturePane;
    @FXML
    private MenuItem startWeatherServiceMenuItem;
    @FXML
    private MenuItem stopWeatherServiceMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        this.options = Options.getInstance();
        this.debugDialog = new DebugDialog();
        this.optionsWindow = new OptionsWindow();
        this.moonPhase = new MoonPhase();
        this.openWeatherMap = OpenWeatherMap.getInstance();
        if (this.options.getOpenWeatherMapApiKey().isPresent()) {
            this.openWeatherMap.start();
        }
        
        this.realTimeClock = new RealTimeClock();
        this.replaceClockAndWeather(this.realTimeClock, this.openWeatherMap);
        this.realTimeClock.start();
        
        this.alwaysOnTopMenuItem.setSelected(this.options.isAlwaysOnTop());

        this.options.getOpenWeatherMapApiKeyProperty().addListener((v, o, n) -> {
            if (n.isEmpty()) {
                this.stopWeatherService();
            }
        });
        BooleanBinding enableStartWeatherService
                = this.openWeatherMap.runningProperty().not()
                    .and(this.options.getOpenWeatherMapApiKeyProperty().isNotEmpty());
        this.startWeatherServiceMenuItem.disableProperty().bind(enableStartWeatherService.not());
        this.stopWeatherServiceMenuItem.disableProperty().bind(this.openWeatherMap.runningProperty().not());
    }
    
    private void replaceClockAndWeather(Clock newClock, Weather weather) {
        SkyColor skyColor = new SkyColor(this.options.getUserLocation(), newClock, weather);

        TimeLabelViewModel timeLabelViewModel = new TimeLabelViewModel();
        timeLabelViewModel.bind(newClock, skyColor);
        this.timeLabel.textProperty().bind(timeLabelViewModel.textProperty());
        this.timeLabel.textFillProperty().bind(timeLabelViewModel.colorProperty());

        BackgroundSkyViewModel backgroundSkyViewModel = new BackgroundSkyViewModel();
        backgroundSkyViewModel.bind(skyColor);
        this.backgroundSkyHBox.backgroundProperty().bind(backgroundSkyViewModel.backgroundProperty());
        
        this.moonPhase.bind(newClock.dateProperty());

        SkyStatusIconViewModel skyStatusIconViewModel = new SkyStatusIconViewModel();
        skyStatusIconViewModel.bind(this.moonPhase, weather);
        this.skyStatusIconImageView.imageProperty().bind(skyStatusIconViewModel.imageProperty());

        TemperatureViewModel temperatureViewModel = new TemperatureViewModel();
        temperatureViewModel.bind(weather);
        this.temperaturePane.backgroundProperty().bind(temperatureViewModel.backgroundProperty());
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
        this.primaryStage.setAlwaysOnTop(this.alwaysOnTopMenuItem.isSelected());
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
        this.options.setAlwaysOnTop(this.alwaysOnTopMenuItem.isSelected());
        this.options.save();
    }
    
    @FXML
    public void openOptions() {
        this.optionsWindow.open(this.primaryStage);
    }

    @FXML
    public void startWeatherService() {
        this.openWeatherMap.start();
    }

    @FXML
    public void stopWeatherService() {
        this.openWeatherMap.stop();
    }
}
