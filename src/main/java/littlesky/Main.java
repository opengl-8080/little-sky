package littlesky;

import javafx.application.Application;
import javafx.stage.Stage;
import littlesky.model.option.Options;
import littlesky.util.Logger;
import littlesky.view.Dialog;
import littlesky.view.main.MainWindow;
import littlesky.view.setup.SetupDialog;

public class Main extends Application {

    public static void main(String[] args) {
        for (String arg : args) {
            if ("--debug".equals(arg)) {
                Logger.getInstance().setDebug(true);
            }
        }
        
        launch(Main.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((thread, th) -> Dialog.error(th));

        Options options = Options.getInstance();
        if (!options.hasUserLocation()) {
            new SetupDialog().open(primaryStage);
            
            if (!options.hasUserLocation()) {
                return;
            }
        }

        new MainWindow().open(primaryStage);
    }
}
