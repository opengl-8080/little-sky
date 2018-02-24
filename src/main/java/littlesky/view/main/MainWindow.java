package littlesky.view.main;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import littlesky.Main;
import littlesky.controller.main.MainController;

import java.awt.*;
import java.io.IOException;

public class MainWindow {
    private static final String TITLE = "littlesky";
    
    private boolean enableSystemTray = true;

    private MainController controller;
    private TrayIcon trayIcon;

    public void initStage(Stage primaryStage) throws java.io.IOException {
        if (this.enableSystemTray && SystemTray.isSupported()) {
            Stage dummyStage = new Stage();
            dummyStage.initOwner(primaryStage);
            
            this.initMainStage(dummyStage);
            
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.setOpacity(0.0);
            primaryStage.setX(-1000.0);
            primaryStage.setY(-1000.0);

            Platform.setImplicitExit(false);
            this.trayIcon = this.initSystemTray();

            primaryStage.show();
            dummyStage.show();
        } else {
            this.initMainStage(primaryStage);
            primaryStage.show();
        }
    }
    
    private void initMainStage(Stage mainStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/main.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        mainStage.setScene(scene);
        mainStage.setTitle(TITLE);
        mainStage.initStyle(StageStyle.TRANSPARENT);

        this.controller.setOwnStage(mainStage);
    }

    private TrayIcon initSystemTray() {
        Image fxImage = this.controller.iconProperty().get();
        TrayIcon trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(fxImage, null));
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> Platform.runLater(() -> this.controller.show()));
        trayIcon.setToolTip(TITLE);
        
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        
        this.controller.iconProperty().addListener((v, o, image) -> {
            Platform.runLater(() -> {
                trayIcon.setImage(SwingFXUtils.fromFXImage(image, null));
            });
        });
        
        return trayIcon;
    }

    public void onStopped() {
        if (this.trayIcon != null) {
            SystemTray.getSystemTray().remove(this.trayIcon);
        }
    }

    public void setEnableSystemTray(boolean enableSystemTray) {
        this.enableSystemTray = enableSystemTray;
    }
}
