package littlesky;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

public class DebugDialogController implements Initializable, Clock {
    @FXML
    private DatePicker datePicker;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private HBox skyColorSimulationHBox;
    
    private ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<LocalTime> time = new ReadOnlyObjectWrapper<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        TimeSliderConverter converter = new TimeSliderConverter();
        this.datePicker.setValue(LocalDate.now());
        this.date.bind(this.datePicker.valueProperty());
        
        this.timeSlider.setLabelFormatter(converter);
        this.time.bind(new ObjectBinding<LocalTime>() {
            {super.bind(timeSlider.valueProperty());}
            
            @Override
            protected LocalTime computeValue() {
                double rate = timeSlider.getValue();
                String timeText = converter.toString(rate);
                return LocalTime.parse(timeText, timeFormatter);
            }
        });
        
        this.timeLabel.textProperty().bind(new StringBinding() {
            {super.bind(timeSlider.valueProperty());}
            
            @Override
            protected String computeValue() {
                return converter.toString(timeSlider.getValue());
            }
        });

        String now = LocalTime.now().format(timeFormatter);
        Double rate = converter.fromString(now);
        this.timeSlider.setValue(rate);

        this.date.addListener((value, oldValue, newDate) -> {
            this.simulateSkyColor();
        });
        this.simulateSkyColor();
    }
    
    private void simulateSkyColor() {
        this.skyColorSimulationHBox.getChildren().clear();
        
        SimulationClock simulationClock = new SimulationClock(this.date.get(), LocalTime.MIN);
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

    @Override
    public ReadOnlyObjectProperty<LocalDate> dateProperty() {
        return this.date.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<LocalTime> timeProperty() {
        return this.time.getReadOnlyProperty();
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
    
    private static class SimulationClock implements Clock {
        
        private final ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<LocalTime> time = new ReadOnlyObjectWrapper<>();

        private SimulationClock(LocalDate date, LocalTime time) {
            this.date.set(date);
            this.time.set(time);
        }
        
        private void update(LocalTime time) {
            this.time.set(time);
        }
        
        @Override
        public ReadOnlyObjectProperty<LocalDate> dateProperty() {
            return this.date.getReadOnlyProperty();
        }

        @Override
        public ReadOnlyObjectProperty<LocalTime> timeProperty() {
            return this.time.getReadOnlyProperty();
        }
    }
}
