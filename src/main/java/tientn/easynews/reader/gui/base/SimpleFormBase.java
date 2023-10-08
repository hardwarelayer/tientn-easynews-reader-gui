package tientn.easynews.reader.gui.base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.event.EventHandler;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Dialog;

import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import javafx.stage.FileChooser;
import javafx.stage.Screen;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
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

public class SimpleFormBase extends VBox {

  @Getter protected Stage primaryStage = null;
  @Getter protected Desktop desktop = null;
  @Getter protected GridPane headerPane = null;
  @Getter protected GridPane bodyPane = null;
  @Getter protected GridPane footerPane = null;
  @Getter private ReaderModel dataModel;

  //TienTN's class for a simple form with header+body+footer
  //each can set different column configurations 
  public SimpleFormBase(final int maxWidth, final int maxHeight, final Desktop desktop, final Stage primaryStage, final ReaderModel model) {
    super();
    this.dataModel = model;
    this.primaryStage = primaryStage;
    this.desktop = desktop;

    this.getStyleClass().add("simple-form");

    this.setMinWidth(maxWidth);
    this.setMinHeight(maxHeight);

    this.headerPane  = new GridPane();
    this.bodyPane = new GridPane();
    this.footerPane = new GridPane();

    this.getChildren().add(headerPane);
    this.getChildren().add(bodyPane);
    this.getChildren().add(footerPane);

    initForm();

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

  protected void showInformation(final String header, final String msg) {

      Point2D currentStageXY = new Point2D(primaryStage.getX(), primaryStage.getY());
      Screen currentScreen = Screen.getScreens().stream()
        .filter(screen -> screen.getBounds().contains(currentStageXY))
        .findAny().get();
      Rectangle2D screenBounds = currentScreen.getBounds();
      double screenCenterX = screenBounds.getMinX() + screenBounds.getWidth()/2 ;
      double screenCenterY = screenBounds.getMinY() + screenBounds.getHeight()/2 ;

      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Information");
      alert.setHeaderText(header);
      alert.setContentText(msg);
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.getDialogPane().setMinWidth(600);
      alert.setResizable(true);
      alert.setX(primaryStage.getX()+100);
      alert.setY(screenCenterY);

      DialogPane dialogPane = alert.getDialogPane();
      dialogPane.getStylesheets().add(
              getClass().getResource("/css/dialog.css").toExternalForm());

      Optional<ButtonType> result = alert.showAndWait();
  }

  protected void showTextInputDialog(final String header, final String title, final String prompt) {
    TextInputDialog td = new TextInputDialog(header);
    td.setHeaderText(header);
    td.setTitle(title);
    td.setContentText(prompt);
    td.show();
  }

  protected void multilineTextInputSearchEvent(final String selText) {}
  protected void multilineTextInputOKEvent(TextArea taObj) {}
  protected void multilineTextInputPreShowEvent(TextArea taObj) {}
  protected String showMultilineTextInputDialog(final String sTitle, final String sPrompt, final String sValue, final double fWidth, final double fHeight) {

    Point2D currentStageXY = new Point2D(primaryStage.getX(), primaryStage.getY());
    Screen currentScreen = Screen.getScreens().stream()
      .filter(screen -> screen.getBounds().contains(currentStageXY))
      .findAny().get();
    Rectangle2D screenBounds = currentScreen.getBounds();

    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(sTitle);

    // Set the button types.
    ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
    dialog.getDialogPane().getStylesheets().add(
              getClass().getResource("/css/dialog.css").toExternalForm());

    TextArea newText = new TextArea();
    newText.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(fHeight));
    newText.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(fWidth));
    newText.setId("edit-content-dialog-text");
    newText.setWrapText(true);

    newText.setPromptText(sPrompt);
    newText.setText(sValue);

    ContextMenu cmTextSel = new ContextMenu();
    MenuItem mi1 = new MenuItem("Search");
    cmTextSel.getItems().add(mi1);
    mi1.setOnAction((ActionEvent event) -> {
        this.multilineTextInputSearchEvent(newText.getSelectedText());
    });
    newText.setContextMenu(cmTextSel);

    HBox pane = new HBox(newText);
    //pane.setHgap(10);
    //pane.setVgap(10);
    //pane.setPadding(new Insets(20, 150, 10, 10));
    dialog.getDialogPane().setContent(pane);
    dialog.setX(primaryStage.getX() + 50);
    dialog.setY(primaryStage.getY() + 50);

    // Request focus on the username field by default.
    Platform.runLater(() -> newText.requestFocus());

    // Convert the result to a username-password-pair when the login button is clicked.
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == okButtonType) {
          this.multilineTextInputOKEvent(newText);
          return new String(newText.getText());
        }
        return null;
    });

    this.multilineTextInputPreShowEvent(newText);
    Optional<String> result = dialog.showAndWait();

    if (result.isEmpty()) return null;
    return result.get();
  }

}