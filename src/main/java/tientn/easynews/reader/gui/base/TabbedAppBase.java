package tientn.easynews.reader.gui.base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.geometry.VPos;

import javafx.stage.FileChooser;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

import javafx.scene.layout.VBox;
import java.util.Locale;
import lombok.Getter;

import tientn.easynews.reader.gui.base.TabPaneBase;

public abstract class TabbedAppBase extends Application {

    @Getter private Desktop desktop = Desktop.getDesktop();
    @Getter private int maxWidth;
    @Getter private int maxHeight;
    @Getter private String appTitle;
    @Getter private boolean isMacOS;

    public TabbedAppBase() {
        super();
    }

    public void setDefaultArgs(final String sAppTitle, final int iWidth, final int iHeight) {
        this.appTitle = sAppTitle;
        this.maxWidth = iWidth;
        this.maxHeight = iHeight;
    } 

    public static void println(String s){
        Platform.runLater(new Runnable() {//in case you call from other thread
            @Override
            public void run() {
                System.out.println(s);//for echo if you want
            }
        });
    }

    protected abstract TabPaneBase initSceneElements(Stage primaryStage);

    // JavaFX entry point
    @Override
    public void start(Stage primaryStage) throws Exception {

        TabPaneBase ctl = initSceneElements(primaryStage);

        VBox root = new VBox(ctl);
        String os = System.getProperty("os.name","generic").toLowerCase(Locale.US);
        System.out.println(os);
        this.isMacOS = false;
        if (os.indexOf("mac") >= 0) {
            this.isMacOS = true;
            //root.setStyle("-fx-font-size: 12pt; -fx-font-family: Courier New");
        }

        // Top level container for all view content
        Scene scene = new Scene(root, this.maxWidth, this.maxHeight);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            //or scene.setOnKeyPressed((KeyEvent ke) -> {}
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.W && (ke.isMetaDown() || ke.isControlDown())) {
                    System.out.println("Key Pressed: " + ke.getCode());
                    ke.consume(); // <-- stops passing the event to next node
                    //exiting
                    Platform.exit();
                    System.exit(0);
                }
                else {
                    //normal key press
                }
            }
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                //If you have non-daemon threads running, Platform.exit() will not forcibly shut them down, but System.exit() will.
                Platform.exit();
                System.exit(0);
            }
        });
        scene.getStylesheets().add("/css/root.css");

        // primaryStage is the main top level window created by platform
        primaryStage.setTitle(appTitle);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}