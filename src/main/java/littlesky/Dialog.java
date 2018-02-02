package littlesky;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Dialog {
    
    public static void error(Throwable th) {
        th.printStackTrace(System.err);
        Platform.runLater(() -> {
            String message = th.getMessage() == null ? "Unknown error" : th.getMessage();
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            TextArea textArea = new TextArea();
            textArea.setEditable(false);

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            th.printStackTrace(writer);
            textArea.setText(stringWriter.toString());

            alert.getDialogPane().setExpandableContent(textArea);

            alert.showAndWait();
        });
    }

    public static void warn(InvalidInputException e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Invalid input");

            alert.showAndWait();
        });
    }
    
    private Dialog() {}
}
