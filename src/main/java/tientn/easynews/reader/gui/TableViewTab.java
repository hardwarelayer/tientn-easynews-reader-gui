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
import javafx.geometry.VPos;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import tientn.easynews.reader.gui.base.GridPaneBase;
import tientn.easynews.reader.data.TableViewItem;
import lombok.Getter;

public class TableViewTab extends GridPaneBase {

    @Getter private Label lblSelectedName; //if = null here then in event handler it will always be null
 
    public TableViewTab(final String title, Desktop desktop, Stage primStage) {
        super(title, desktop, primStage);
    }

    @Override
    protected void initForm() {

        Text txtFormTitle = new Text("Form Title");
        this.setValignment(txtFormTitle, VPos.TOP);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        this.getColumnConstraints().addAll(col1,col2);

        this.add(txtFormTitle, 0, 0);

        createFormElements();
    }

    private void createFormElements() {

        Label label = new Label("Selected Name:");
        this.lblSelectedName = new Label("Test");

        Button btnHello = createButtonWithShadow();

        GridPane gLeftPanel = new GridPane();
        gLeftPanel.add(label, 0, 0);
        gLeftPanel.add(this.lblSelectedName, 0, 1);
        gLeftPanel.add(btnHello, 0, 2);

        TableView<TableViewItem> tableView = createTableView();

        this.add(gLeftPanel, 0, 1);
        this.add(tableView, 1, 1);
    }

    private Button createButtonWithShadow() {
        Button btnHello = new Button();
        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //label.setText("Accepted");
                System.out.println("Button clicked!");
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

        btnHello.setText("Test Button in TableViewTab");
        btnHello.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btnHello.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btnHello.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btnHello;
    }

    private void processTableViewDblClick(TableViewItem rowData) {
        if (this.lblSelectedName != null)
          this.lblSelectedName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private TableView<TableViewItem> createTableView() {
        TableView<TableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<TableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TableViewItem rowData = row.getItem();
                    processTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<TableViewItem, String> tcol1 = new TableColumn<>("Category");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<TableViewItem, String> tcol2 = new TableColumn<>("Value");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("value"));

        tcol1.prefWidthProperty().bind(tableView.widthProperty().multiply(0.4));
        tcol2.prefWidthProperty().bind(tableView.widthProperty().multiply(0.6));
        tcol1.setResizable(false);
        tcol2.setResizable(false);

        tableView.getColumns().add(tcol1);
        tableView.getColumns().add(tcol2);

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<TableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        tableView.getItems().add(new TableViewItem("John", "Doe"));
        tableView.getItems().add(new TableViewItem("Jane", "Deer"));

        return tableView;        
    }

} 