package tientn.easynews.reader.gui.base;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.input.KeyCode;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class GridPaneBase extends GridPane {

    @Getter protected Stage primaryStage = null;
    @Getter protected Desktop desktop = null;

    public GridPaneBase(final String title, Desktop desktop, Stage primaryStage) {
        super();
        this.primaryStage = primaryStage;
        this.desktop = desktop;

        //note, don't set maxheight for a view, it's content will be vertical centered if we doing so
        //
        this.setAlignment(Pos.CENTER);
        this.setHgap(4);
        this.setVgap(4);
        this.setPadding(new Insets(4, 4, 4, 4));

        initForm();

        //this.getStyleClass().add("grid-pane");
        this.setStyle("-fx-background-color: transparent;");

        this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            //or scene.setOnKeyPressed((KeyEvent ke) -> {}
            public void handle(KeyEvent ke) {
                if (!(ke.getCode() == KeyCode.W) && !(ke.isMetaDown() || ke.isControlDown())) {
                    //System.out.println("SimpleFormBase KeyPressed event: " + ke.getCode());
                    processKeyPress(ke);
                }
            }
          });

    }

    protected void processKeyPress(final KeyEvent ke) {
    }

    protected void processFirstShowEvent() {
    }

    protected void showBorder() {
        this.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");
    }

    protected void initForm() {
    }

    public static void println(String s){
        Platform.runLater(new Runnable() {//in case you call from other thread
            @Override
            public void run() {
                System.out.println(s);//for echo if you want
            }
        });
    }

    protected void showMessage(final String title, final String header, final String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/dialog.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
    }

    protected void showMessageWithStyle(final String title, final String msg, final String cssFile) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/"+cssFile).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
    }

    protected boolean showQuestion(final String title, final String header, final String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);

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

    protected boolean showQuestionWithStyle(final String title, final String msg, final String cssFile) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/"+cssFile).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    protected boolean showQuestionWithCustomOptions(final String title, final String header, final String msg, final String okText, final String cancelText) {
        ButtonType btnOK = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(AlertType.WARNING, msg, btnOK, btnCancel);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(btnCancel) == btnOK) {
            return true;
        }
        return false;
    }
}