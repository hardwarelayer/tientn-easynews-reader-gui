package tientn.easynews.reader.gui;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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

import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import tientn.easynews.reader.gui.base.GridPaneBase;

// Simple Hello World JavaFX program
public class TabWindow extends GridPaneBase {
 
    public TabWindow(final String title, Desktop desktop, Stage primStage) {
        super(title, desktop, primStage);
    }

    @Override
    protected void initForm() {

        this.setMinWidth(800);
        this.setMaxWidth(800);

        Text txtFormTitle = new Text("Form Title");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(30);
        this.getColumnConstraints().addAll(col1,col2);

        this.add(txtFormTitle, 0, 0);

        createFormElements();
    }

    private void createFormElements() {

        Label label = new Label("Tab Window Label");

        Button btnHello = new Button();
        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                label.setText("Accepted");


            }
        };
        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnHello.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnHello.setEffect(null);
                }
        };

        btnHello.setText("Test Button in TabWindow");
        btnHello.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btnHello.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btnHello.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        this.add(label, 0, 1);
        this.add(btnHello, 1, 1);
    }

}