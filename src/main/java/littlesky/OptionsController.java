package littlesky;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {
    private Options options = Options.getInstance();
    private Stage ownStage;

    @FXML
    private TextField latitudeTextField;
    @FXML
    private TextField longitudeTextField;
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
    }
    
    @FXML
    public void save() {
        try {
            this.options.validateHttpProxyPort(this.httpProxyPortTextField.getText());
            this.options.validateLatitude(this.latitudeTextField.getText());
            this.options.validateLongitude(this.longitudeTextField.getText());
        } catch (InvalidInputException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        this.options.setLatitude(this.latitudeTextField.getText());
        this.options.setLongitude(this.longitudeTextField.getText());
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
