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
        double latitude;
        double longitude;
        try {
            this.options.validateHttpProxyPort(this.httpProxyPortTextField.getText());

            latitude = this.parseLatitude();
            UserLocation.validateLatitude(latitude);
            
            longitude = this.parseLongitude();
            UserLocation.validateLongitude(longitude);
        } catch (InvalidInputException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        UserLocation location = UserLocation.getInstance();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        this.options.setLatitude(latitude);
        this.options.setLongitude(longitude);
        
        this.options.setOpenWeatherMapApiKey(this.openWeatherMapApiKeyTextField.getText());
        this.options.setHttpProxyPort(this.httpProxyPortTextField.getText());
        this.options.setHttpProxyHost(this.httpProxyHostTextField.getText());

        this.options.save();
        this.ownStage.close();
    }
    
    private double parseLatitude() throws InvalidInputException {
        try {
            return Double.parseDouble(this.latitudeTextField.getText());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Latitude must be decimal.");
        }
    }

    private double parseLongitude() throws InvalidInputException {
        try {
            return Double.parseDouble(this.longitudeTextField.getText());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Longitude must be decimal.");
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
