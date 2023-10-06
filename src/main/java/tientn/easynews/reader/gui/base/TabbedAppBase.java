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
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import javafx.stage.FileChooser;
import javafx.stage.Screen;
import java.awt.Desktop;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.QuitHandler;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

import javafx.application.Platform;

import javafx.scene.layout.VBox;
import java.util.Locale;
import lombok.Getter;

import tientn.easynews.reader.gui.base.TabPaneBase;

public abstract class TabbedAppBase extends Application {

    @Getter protected Stage primaryStage = null;
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

        this.primaryStage = primaryStage;

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

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                //System.out.println("mouse click detected! " + mouseEvent.getSource());
                ctl.processMouseEvent(me);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            //or scene.setOnKeyPressed((KeyEvent ke) -> {}
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.W && (ke.isMetaDown() || ke.isControlDown())) {
                    //System.out.println("Close key Pressed: " + ke.getCode());

                    if (showQuestion("Exit confirmation", "Confirm", "Do you really want to exit?")) {
                        ke.consume(); // <-- stops passing the event to next node
                        //exiting
                        Platform.exit();
                        System.exit(0);
                    }
                    else
                    {
                        ke.consume(); // <-- stops passing the event to next node
                    }
                }
                else {
                    //normal key press, we'll pass to nodes
                    //System.out.println("Key Pressed: " + ke.getCode());
                    ctl.processKeypressEvent(ke);
                }
            }
        });

        //no use
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setQuitHandler(new QuitHandler()
            {
                @Override
                public void handleQuitRequestWith(QuitEvent evt, QuitResponse res)
                {
                    System.out.println("quit request detected!");
                    // TODO: Handle the quit request
                    // res.cancelQuit();  // Cancel the quit request
                }
            });
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                //If you have non-daemon threads running, Platform.exit() will not forcibly shut them down, but System.exit() will.
                if (showQuestion("Exit confirmation", "Confirm", "Do you really want to exit?")) {
                    Platform.exit();
                    System.exit(0);
                }
                else {
                    t.consume();
                }
            }
        });
        scene.getStylesheets().add("/css/darktheme.css");//root.css");
        scene.getStylesheets().add("/css/itemspec.css");//root.css");

        // primaryStage is the main top level window created by platform
        primaryStage.setTitle(appTitle);
        primaryStage.setScene(scene);
        primaryStage.show();

        ctl.processFirstShowEvent();
    }

    protected boolean showQuestion(final String title, final String header, final String msg) {
      Point2D currentStageXY = new Point2D(primaryStage.getX(), primaryStage.getY());
      Screen currentScreen = Screen.getScreens().stream()
        .filter(screen -> screen.getBounds().contains(currentStageXY))
        .findAny().get();
      Rectangle2D screenBounds = currentScreen.getBounds();
      double screenCenterX = screenBounds.getMinX() + screenBounds.getWidth()/2 ;
      double screenCenterY = screenBounds.getMinY() + screenBounds.getHeight()/2 ;

      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(title);
      alert.setHeaderText(header);
      alert.setContentText(msg);
      alert.setX(primaryStage.getX()+100);
      alert.setY(screenCenterY);

      DialogPane dialogPane = alert.getDialogPane();
      dialogPane.getStylesheets().add(
              getClass().getResource("/css/dialog.css").toExternalForm());

      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == ButtonType.OK){
          return true;
      } else {
          return false;
      }
    }
}