package littlesky;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LocationFormController {
    @FXML
    private TextField latitudeTextField;
    @FXML
    private TextField longitudeTextField;
    
    public UserLocation getUserLocation() throws InvalidInputException {
        return new UserLocation(this.parseLatitude(), this.parseLongitude());
    }
    
    public void setUserLocation(UserLocation location) {
        this.latitudeTextField.setText(String.valueOf(location.getLatitude()));
        this.longitudeTextField.setText(String.valueOf(location.getLongitude()));
    }
    
    public BooleanBinding isNotEmpty() {
        return this.latitudeTextField.textProperty().isNotEmpty()
                .and(this.longitudeTextField.textProperty().isNotEmpty());
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
}
