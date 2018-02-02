package littlesky;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SetupController implements Initializable {
    
    private Stage ownStage;
    @FXML
    private LocationFormController locationFormController;
    @FXML
    private Button okButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding enableOkButton = this.locationFormController.isNotEmpty();
        this.okButton.disableProperty().bind(enableOkButton.not());
    }

    public void setOwnStage(Stage ownStage) {
        this.ownStage = ownStage;
    }

    @FXML
    public void ok() {
        try {
            UserLocation userLocation = this.locationFormController.getUserLocation();
            
            Options options = Options.getInstance();
            options.setUserLocation(userLocation);
            options.save();

            this.ownStage.close();
        } catch (InvalidInputException e) {
            Dialog.warn(e);
        }
    }
}
