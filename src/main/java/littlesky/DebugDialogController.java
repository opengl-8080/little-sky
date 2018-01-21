package littlesky;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static littlesky.BindingBuilder.*;

public class DebugDialogController implements Initializable {
    @FXML
    private DatePicker datePicker;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox skyColorSimulationHBox;

    private static final DateTimeFormatter sliderLabelFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final TimeSliderConverter timeSliderConverter = new TimeSliderConverter();
    
    private final DebugClock debugClock = new DebugClock();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.datePicker.setValue(LocalDate.now());
        
        this.timeSlider.setLabelFormatter(timeSliderConverter);
        this.timeSlider.setValue(this.toSliderValue(LocalTime.now()));
        
        this.timeLabel.textProperty().bind(
            binding(this.timeSlider.valueProperty()).computeValue(this::getTimeLabelFromTimeSlider)
        );

        this.debugClock.bindTime(
            binding(this.timeSlider.valueProperty()).computeValue(this::getLocalTimeFromTimeSlider)
        );

        this.debugClock.bindDate(this.datePicker.valueProperty());
        this.debugClock.dateProperty().addListener((value, oldValue, newDate) -> {
            this.simulateSkyColor();
        });
        
        this.simulateSkyColor();
    }
    
    private Double toSliderValue(LocalTime time) {
        String now = time.format(sliderLabelFormatter);
        return timeSliderConverter.fromString(now);
    }

    private LocalTime getLocalTimeFromTimeSlider() {
        double rate = this.timeSlider.getValue();
        String timeText = timeSliderConverter.toString(rate);
        return LocalTime.parse(timeText, sliderLabelFormatter);
    }

    private String getTimeLabelFromTimeSlider() {
        return timeSliderConverter.toString(timeSlider.getValue());
    }
    
    private void simulateSkyColor() {
        this.skyColorSimulationHBox.getChildren().clear();
        
        SimulationClock simulationClock = new SimulationClock(this.debugClock.getDate(), LocalTime.MIN);
        SkyColor skyColor = new SkyColor(JapaneseCity.OSAKA, simulationClock);
        
        LocalTime time = LocalTime.MIN;
        while (time.equals(LocalTime.MAX) || time.isBefore(LocalTime.MAX)) {
            simulationClock.update(time);

            Pane pane = this.createSimulatedColorBlock(skyColor.getColor());
            this.skyColorSimulationHBox.getChildren().add(pane);

            time = time.plusMinutes(10);
            if (time.equals(LocalTime.MIN)) {
                break;
            }
        }
    }
    
    private Pane createSimulatedColorBlock(Color color) {
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
        return pane;
    }

    public Clock getClock() {
        return this.debugClock;
    }
    
    private static class DebugClock extends ClockBase {
        
        private void bindTime(ObjectBinding<LocalTime> binding) {
            this.time.bind(binding);
        }
        
        private void bindDate(ObjectProperty<LocalDate> binding) {
            this.date.bind(binding);
        }
    }
    
    private static class SimulationClock extends ClockBase {

        private SimulationClock(LocalDate date, LocalTime time) {
            this.date.set(date);
            this.time.set(time);
        }
        
        private void update(LocalTime time) {
            this.time.set(time);
        }
    }

    private static class TimeSliderConverter extends StringConverter<Double> {
        private static final long TOTAL_MINUTES = 60 * 23 + 59;

        @Override
        public String toString(Double rate) {
            long minutes = (long)(TOTAL_MINUTES * rate);
            long hour = minutes / 60;
            long minute = minutes % 60;
            return String.format("%02d:%02d", hour, minute);
        }

        @Override
        public Double fromString(String label) {
            String[] values = label.split(":");
            int hour = Integer.parseInt(values[0]);
            int minute = Integer.parseInt(values[1]);

            long minutes = (hour * 60) + minute;

            return (double)minutes / TOTAL_MINUTES;
        }
    }
}
