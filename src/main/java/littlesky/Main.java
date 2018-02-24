package littlesky;

import javafx.application.Application;
import javafx.stage.Stage;
import littlesky.model.option.Options;
import littlesky.util.Logger;
import littlesky.view.Dialog;
import littlesky.view.main.MainWindow;
import littlesky.view.setup.SetupDialog;

import java.awt.*;

public class Main extends Application {
    
    private static boolean enableSystemTray = true;

    public static void main(String[] args) {
        for (String arg : args) {
            if ("--debug".equals(arg)) {
                Logger.getInstance().setDebug(true);
            } else if (arg.startsWith("--system-tray=")) {
                Main.enableSystemTray = Boolean.parseBoolean(arg.split("=")[1].toLowerCase());
            }
        }
        
        launch(Main.class, args);
    }
    
    private MainWindow mainWindow;
    
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

        this.mainWindow = new MainWindow();
        this.mainWindow.setEnableSystemTray(Main.enableSystemTray);
        this.mainWindow.initStage(primaryStage);
    }

    @Override
    public void stop() {
        if (SystemTray.isSupported()) {
            this.mainWindow.onStopped();
        }
    }
}
