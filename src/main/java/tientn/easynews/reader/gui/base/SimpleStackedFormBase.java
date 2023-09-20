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
import javafx.scene.control.ContentDisplay;
import javafx.scene.text.TextAlignment;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_RIGHT;

import java.util.Locale;
import lombok.Getter;

import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.gui.base.GridPaneBase;

public class SimpleStackedFormBase extends VBox {

  @Getter protected Stage primaryStage = null;
  @Getter protected Desktop desktop = null;
  @Getter protected GridPane headerPane = null;
  @Getter protected Label topBodyLabel = null, midBodyLabel = null, midBodyDescLabel = null, bottomBodyLabel = null;
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

    topBodyLabel = new Label("");
    midBodyLabel = new Label("");
    midBodyDescLabel = new Label(""); //a smaller font below midBody content, to add extra information (see WordMatchWindow)
    bottomBodyLabel = new Label("");
    HBox hb1 = new HBox(this.topBodyLabel);
    HBox hb2 = new HBox(new VBox(this.midBodyLabel, this.midBodyDescLabel));
    HBox hb3 = new HBox(this.bottomBodyLabel);
    VBox vb = new VBox(hb1, hb2, hb3);
    topBodyLabel.setDisable(true);
    midBodyLabel.setDisable(true);
    bottomBodyLabel.setDisable(true);
    vb.setDisable(true);

    hb1.setAlignment(CENTER_RIGHT);
    hb3.setAlignment(CENTER_RIGHT);
    hb2.setAlignment(CENTER);
    vb.setAlignment(CENTER);

    this.bodyStackPane.getChildren().addAll(this.bodyPane, vb);

    this.getChildren().add(headerPane);
    this.getChildren().add(bodyStackPane);
    this.getChildren().add(footerPane);

    topBodyLabel.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.1));
    //topBodyLabel.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
    topBodyLabel.setTextAlignment(TextAlignment.RIGHT);
    topBodyLabel.setContentDisplay(ContentDisplay.TOP);

    midBodyLabel.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
    bottomBodyLabel.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.4));

    initForm();

    //because, now we have forward the keyevent from tabpane directly to function processKeypressEvent
    //we don't need to register this, or the event will be duplicated.
    //this feature now only need if the class is used outside TabPaneBase
    /*
    this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
        //or scene.setOnKeyPressed((KeyEvent ke) -> {}
        public void handle(KeyEvent ke) {
            if (!(ke.getCode() == KeyCode.W) && !(ke.isMetaDown() || ke.isControlDown())) {
                //System.out.println("SimpleStackedFormBase KeyPressed event: " + ke.getCode());
                processKeypressEvent(ke);
            }
        }
      });
    */
  }

  protected void processKeypressEvent(final KeyEvent ke) {}
  protected void processMouseEvent(final MouseEvent me) {}

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