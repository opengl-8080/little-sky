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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import littlesky.model.clock.Clock;
import littlesky.model.clock.RealTimeClock;
import littlesky.model.moon.MoonPhase;
import littlesky.model.option.Options;
import littlesky.model.option.ViewOptions;
import littlesky.model.sky.SkyColor;
import littlesky.model.weather.Weather;
import littlesky.model.weather.owm.OpenWeatherMap;
import littlesky.view.debug.DebugDialog;
import littlesky.view.main.BackgroundSkyViewModel;
import littlesky.view.main.SkyStatusIconViewModel;
import littlesky.view.main.TemperatureViewModel;
import littlesky.view.main.TimeLabelViewModel;
import littlesky.view.main.WindowSizeViewModel;
import littlesky.view.option.OptionsWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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
    private CheckMenuItem showSecondsCheckMenuItem;
    @FXML
    private CheckMenuItem showTemperatureCheckMenuItem;
    @FXML
    private CheckMenuItem showSkyStatusIconCheckMenuItem;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox backgroundSkyHBox;
    @FXML
    private BorderPane skyStatusIconPane;
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
        this.initInstances();
        this.initEventListeners();
        this.initMenuItems();
        this.initServices();
        this.replaceClockAndWeather(this.realTimeClock, this.openWeatherMap);
    }
    
    private void initInstances() {
        this.options = Options.getInstance();
        this.debugDialog = new DebugDialog();
        this.optionsWindow = new OptionsWindow();
        this.moonPhase = new MoonPhase();
        this.openWeatherMap = OpenWeatherMap.getInstance();
        this.realTimeClock = new RealTimeClock();
    }
    
    private void initMenuItems() {
        ViewOptions viewOptions = this.options.getViewOptions();
        this.alwaysOnTopMenuItem.selectedProperty().bindBidirectional(viewOptions.alwaysOnTopProperty());
        this.showSecondsCheckMenuItem.selectedProperty().bindBidirectional(viewOptions.showSecondsProperty());
        this.showTemperatureCheckMenuItem.selectedProperty().bindBidirectional(viewOptions.showTemperatureProperty());
        this.showSkyStatusIconCheckMenuItem.selectedProperty().bindBidirectional(viewOptions.showSkyStatusIconProperty());

        BooleanBinding enableStartWeatherService
                = this.openWeatherMap.runningProperty().not()
                .and(this.options.getOpenWeatherMapApiKeyProperty().isNotEmpty());
        this.startWeatherServiceMenuItem.disableProperty().bind(enableStartWeatherService.not());
        this.stopWeatherServiceMenuItem.disableProperty().bind(this.openWeatherMap.runningProperty().not());
    }
    
    private void initEventListeners() {
        this.options.getOpenWeatherMapApiKeyProperty().addListener((v, o, n) -> {
            if (n.isEmpty()) {
                this.stopWeatherService();
            }
        });
    }
    
    private void initServices() {
        this.realTimeClock.start();
        if (this.options.getOpenWeatherMapApiKey().isPresent()) {
            this.openWeatherMap.start();
        }
    }
    
    private void replaceClockAndWeather(Clock newClock, Weather weather) {
        SkyColor skyColor = new SkyColor(this.options.getUserLocation(), newClock, weather);

        TimeLabelViewModel timeLabelViewModel = new TimeLabelViewModel();
        timeLabelViewModel.bind(newClock, skyColor, this.options.getViewOptions());
        this.timeLabel.textProperty().bind(timeLabelViewModel.textProperty());
        this.timeLabel.textFillProperty().bind(timeLabelViewModel.colorProperty());

        BackgroundSkyViewModel backgroundSkyViewModel = new BackgroundSkyViewModel();
        backgroundSkyViewModel.bind(skyColor);
        this.backgroundSkyHBox.backgroundProperty().bind(backgroundSkyViewModel.backgroundProperty());

        WindowSizeViewModel windowSizeViewModel = new WindowSizeViewModel();
        windowSizeViewModel.bind(this.options.getViewOptions());
        this.backgroundSkyHBox.prefWidthProperty().bind(windowSizeViewModel.widthProperty());
        windowSizeViewModel.widthProperty().addListener((a, b, width) -> {
            this.primaryStage.setWidth(width.doubleValue());
        });
        
        this.moonPhase.bind(newClock.dateProperty());

        SkyStatusIconViewModel skyStatusIconViewModel = new SkyStatusIconViewModel();
        skyStatusIconViewModel.bind(this.moonPhase, weather);
        this.skyStatusIconImageView.imageProperty().bind(skyStatusIconViewModel.imageProperty());
        this.skyStatusIconPane.visibleProperty().bind(this.showSkyStatusIconCheckMenuItem.selectedProperty());
        this.skyStatusIconPane.managedProperty().bind(this.skyStatusIconPane.visibleProperty());

        TemperatureViewModel temperatureViewModel = new TemperatureViewModel();
        temperatureViewModel.bind(weather);
        this.temperaturePane.backgroundProperty().bind(temperatureViewModel.backgroundProperty());
        this.temperaturePane.visibleProperty().bind(this.showTemperatureCheckMenuItem.selectedProperty());
        this.temperaturePane.managedProperty().bind(this.temperaturePane.visibleProperty());
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
        this.options.save();
    }
    
    @FXML
    public void changeViewOptions() {
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
