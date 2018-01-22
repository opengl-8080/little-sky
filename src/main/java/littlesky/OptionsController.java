package littlesky;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {
    private Options options = Options.getInstance();
    private Stage ownStage;

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
        if (this.httpProxyPortTextField.getText().isEmpty()) {
            this.options.clearHttpProxyPort();
        } else {
            if (!this.httpProxyPortTextField.getText().matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Http Proxy Port is not Number.", ButtonType.OK);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            this.options.setHttpProxyPort(Integer.parseInt(this.httpProxyPortTextField.getText()));
        }
        
        this.options.setOpenWeatherMapApiKey(this.openWeatherMapApiKeyTextField.getText());
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
