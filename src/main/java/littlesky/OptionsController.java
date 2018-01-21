package littlesky;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {
    private Options options = Options.getInstance();
    private Stage ownStage;

    @FXML
    private TextField openWeatherMapApiKeyTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.openWeatherMapApiKeyTextField.setText(this.options.getOpenWeatherMapApiKey().orElse(""));
    }
    
    @FXML
    public void save() {
        this.options.setOpenWeatherMapApiKey(this.openWeatherMapApiKeyTextField.getText());
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
