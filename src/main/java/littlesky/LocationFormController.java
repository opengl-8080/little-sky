package littlesky;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LocationFormController implements Initializable {
    @FXML
    private TextField latitudeTextField;
    @FXML
    private TextField longitudeTextField;
    @FXML
    private TextField timeZoneFilterTextField;
    @FXML
    private ComboBox<String> timeZoneComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] availableIDs = TimeZone.getAvailableIDs();
        ObservableList<String> availableIDList = FXCollections.observableArrayList(availableIDs);
        FilteredList<String> filtered = availableIDList.filtered(zoneId -> true);
        this.timeZoneComboBox.setItems(filtered);
        
        this.timeZoneFilterTextField.textProperty().addListener((observable, oldValue, userInput) -> {
            filtered.setPredicate(zoneId -> zoneId.toUpperCase().contains(userInput.toUpperCase()));
            if (!filtered.contains(this.timeZoneComboBox.getValue())) {
                this.timeZoneComboBox.getSelectionModel().select("");
            }
        });
    }
    
    public UserLocation getUserLocation() throws InvalidInputException {
        String timeZoneId = this.timeZoneComboBox.getValue();
        if (timeZoneId.isEmpty()) {
            throw new InvalidInputException("Time zone is required.");
        }
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        return new UserLocation(this.parseLatitude(), this.parseLongitude(), timeZone);
    }
    
    public void setUserLocation(UserLocation location) {
        this.latitudeTextField.setText(String.valueOf(location.getLatitude()));
        this.longitudeTextField.setText(String.valueOf(location.getLongitude()));
        TimeZone timeZone = location.getTimeZone();
        this.timeZoneComboBox.setValue(timeZone.getID());
    }
    
    public BooleanBinding isNotEmpty() {
        return this.latitudeTextField.textProperty().isNotEmpty()
                .and(this.longitudeTextField.textProperty().isNotEmpty())
                .and(this.timeZoneComboBox.valueProperty().asString().isNotEmpty());
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
