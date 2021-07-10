package tientn.easynews.reader.gui.base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;
import javafx.stage.Stage;
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

import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;

import java.util.Locale;
import lombok.Getter;

import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.gui.base.GridPaneBase;

public class SimpleStackedFormBase extends VBox {

  @Getter protected Stage primaryStage = null;
  @Getter protected Desktop desktop = null;
  @Getter protected GridPane headerPane = null;
  @Getter protected Label topBodyLabel = null;
  @Getter protected StackPane bodyStackPane = null;
  @Getter protected GridPane bodyPane = null;
  @Getter protected GridPane footerPane = null;
  @Getter private ReaderModel dataModel;

  //TienTN's class for a simple form with header+body+footer
  //each can set different column configurations 
  public SimpleStackedFormBase(final int maxWidth, final int maxHeight, final Desktop desktop, final Stage primaryStage, final ReaderModel model) {
    super();
    this.dataModel = model;
    this.primaryStage = primaryStage;
    this.desktop = desktop;

    this.getStyleClass().add("simple-form");

    this.setMinWidth(maxWidth);
    this.setMinHeight(maxHeight);

    this.headerPane  = new GridPane();
    this.bodyStackPane = new StackPane();
    this.bodyPane = new GridPane();
    this.footerPane = new GridPane();

    topBodyLabel = new Label("...");
    this.bodyStackPane.getChildren().addAll(this.bodyPane, this.topBodyLabel);

    this.getChildren().add(headerPane);
    this.getChildren().add(bodyStackPane);
    this.getChildren().add(footerPane);

    initForm();

    this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
        //or scene.setOnKeyPressed((KeyEvent ke) -> {}
        public void handle(KeyEvent ke) {
            if (!(ke.getCode() == KeyCode.W) && !(ke.isMetaDown() || ke.isControlDown())) {
                //System.out.println("SimpleStackedFormBase KeyPressed event: " + ke.getCode());
                processKeyPress(ke);
            }
        }
      });

  }

  protected void processKeyPress(final KeyEvent ke) {
  }

  public void addHeaderPane(final Pane pane, final int col, final int row) {
    this.headerPane.add(pane, col, row);
  }

  public void addHeaderCtl(final Control ctl, final int col, final int row) {
    this.headerPane.add(ctl, col, row);
  }

  public void addHeaderText(final Text txt, final int col, final int row) {
    this.headerPane.add(txt, col, row);
  }

  public void addBodyPane(final Pane pane, final int col, final int row) {
    this.bodyPane.add(pane, col, row);
  }

  public void addBodyCtl(final Control ctl, final int col, final int row) {
    this.bodyPane.add(ctl, col, row);
  }

  public void addFooterCtl(final Control ctl, final int col, final int row) {
    this.footerPane.add(ctl, col, row);
  }

  public void addHeaderColumn(final int percentWidth) {
     ColumnConstraints col = new ColumnConstraints();
     col.setPercentWidth(percentWidth);
     this.headerPane.getColumnConstraints().add(col);
  }

  public void addBodyColumn(final int percentWidth) {
     ColumnConstraints col = new ColumnConstraints();
     col.setPercentWidth(percentWidth);
     this.bodyPane.getColumnConstraints().add(col);
  }

  public void addFooterColumn(final int percentWidth) {
     ColumnConstraints col = new ColumnConstraints();
     col.setPercentWidth(percentWidth);
     this.footerPane.getColumnConstraints().add(col);
  }

  protected void initForm() {
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

  protected void showInformation(final String header, final String msg) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Information");
      alert.setHeaderText(header);
      alert.setContentText(msg);

      DialogPane dialogPane = alert.getDialogPane();
      dialogPane.getStylesheets().add(
              getClass().getResource("/css/dialog.css").toExternalForm());

      Optional<ButtonType> result = alert.showAndWait();
  }

}