package littlesky.controller.option;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
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
    private TextField proxyHostTextField;
    @FXML
    private TextField proxyPortTextField;
    @FXML
    private TextField proxyUserNameTextField;
    @FXML
    private PasswordField proxyPasswordField;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.openWeatherMapApiKeyTextField.setText(this.options.getOpenWeatherMapApiKey().orElse(""));

        HttpProxy httpProxy = this.options.getHttpProxy();
        httpProxy.getHost().ifPresent(this.proxyHostTextField::setText);
        httpProxy.getPort().map(String::valueOf).ifPresent(this.proxyPortTextField::setText);
        httpProxy.getUsername().ifPresent(this.proxyUserNameTextField::setText);
        httpProxy.getPassword().ifPresent(this.proxyPasswordField::setText);
        
        this.locationFormController.setUserLocation(this.options.getUserLocation());
    }
    
    @FXML
    public void save() {
        UserLocation location;
        HttpProxy httpProxy;
        try {
            httpProxy = new HttpProxy(
                this.proxyHostTextField.getText(),
                this.parseHttpProxyPort(),
                this.proxyUserNameTextField.getText(),
                this.proxyPasswordField.getText()
            );
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
        String text = this.proxyPortTextField.getText();
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
