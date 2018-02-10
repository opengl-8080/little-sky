package littlesky.controller.option;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import littlesky.InvalidInputException;
import littlesky.model.http.HttpProxy;
import littlesky.model.location.UserLocation;
import littlesky.model.option.Options;
import littlesky.view.Dialog;

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

        HttpProxy httpProxy = this.options.getHttpProxy();
        httpProxy.getHost().ifPresent(this.httpProxyHostTextField::setText);
        httpProxy.getPort().map(String::valueOf).ifPresent(this.httpProxyPortTextField::setText);
        
        this.locationFormController.setUserLocation(this.options.getUserLocation());
    }
    
    @FXML
    public void save() {
        UserLocation location;
        HttpProxy httpProxy;
        try {
            httpProxy = new HttpProxy(this.httpProxyHostTextField.getText(), this.parseHttpProxyPort());
            location = this.locationFormController.getUserLocation();
        } catch (InvalidInputException e) {
            Dialog.warn(e);
            return;
        }

        this.options.setUserLocation(location);
        this.options.setHttpProxy(httpProxy);
        
        this.options.setOpenWeatherMapApiKey(this.openWeatherMapApiKeyTextField.getText());

        this.options.save();
        this.ownStage.close();
    }
    
    private Integer parseHttpProxyPort() {
        String text = this.httpProxyPortTextField.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("HTTP proxy port must be integer.");
        }
    }
    
    @FXML
    public void cancel() {
        this.ownStage.close();
    }

    public void setOwnStage(Stage ownStage) {
        this.ownStage = ownStage;
    }
}
