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

public class SimpleFormBase extends SimpleBase {

  @Getter protected GridPane headerPane = null;
  @Getter protected GridPane bodyPane = null;
  @Getter protected GridPane footerPane = null;
  @Getter private ReaderModel dataModel;

  //TienTN's class for a simple form with header+body+footer
  //each can set different column configurations 
  public SimpleFormBase(final int maxWidth, final int maxHeight, final Desktop desktop, final Stage primaryStage, final ReaderModel model) {
    super(maxWidth, maxHeight, desktop, primaryStage);
    this.dataModel = model;

    this.getStyleClass().add("simple-form");

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

}