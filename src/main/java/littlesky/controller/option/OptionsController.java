package littlesky.controller.option;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import littlesky.controller.option.LocationFormController;
import littlesky.view.Dialog;
import littlesky.InvalidInputException;
import littlesky.model.option.Options;
import littlesky.model.location.UserLocation;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {
    private Options options = Options.getInstance();
    private Stage ownStage;

    @FXML
    private LocationFormController locationFormController;
    @FXML
    private TextField openWeatherMapApiKeyTextField;
    @FXML
    private TextField httpProxyHostTextField;
    @FXML
    private TextField httpProxyPortTextField;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.openWeatherMapApiKeyTextField.setText(this.options.getOpenWeatherMapApiKey().orElse(""));
        this.httpProxyHostTextField.setText(this.options.getHttpProxyHost().orElse(""));
        this.options.getHttpProxyPort().ifPresent(port -> {
            this.httpProxyPortTextField.setText(String.valueOf(port));
        });
        this.locationFormController.setUserLocation(this.options.getUserLocation());
    }
    
    @FXML
    public void save() {
        UserLocation location;
        try {
            this.options.validateHttpProxyPort(this.httpProxyPortTextField.getText());
            
            location = this.locationFormController.getUserLocation();
        } catch (InvalidInputException e) {
            Dialog.warn(e);
            return;
        }

        this.options.setUserLocation(location);
        
        this.options.setOpenWeatherMapApiKey(this.openWeatherMapApiKeyTextField.getText());
        this.options.setHttpProxyPort(this.httpProxyPortTextField.getText());
        this.options.setHttpProxyHost(this.httpProxyHostTextField.getText());

        this.options.save();
        this.ownStage.close();
    }
    
    @FXML
    public void cancel() {
        this.ownStage.close();
    }

    public void setOwnStage(Stage ownStage) {
        this.ownStage = ownStage;
    }
}
