package tientn.easynews.reader.gui.base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.SingleSelectionModel;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.geometry.Insets;

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
import java.util.Locale;
import lombok.Getter;

import tientn.easynews.reader.gui.base.GridPaneBase;
import tientn.easynews.reader.gui.base.SimpleFormBase;

public class TabPaneBase extends TabPane {

  public TabPaneBase() {
    super();

    this.getStyleClass().add("tab-pane");
    this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
  }

  public Tab addPaneAsTab(final String tabTitle, final GridPaneBase ctl) {
    VBox vLayout = new VBox(ctl);
    VBox.setMargin(vLayout, new Insets(0,0,0,0));
    vLayout.setAlignment(Pos.TOP_CENTER);
    vLayout.setSpacing(0);

    Tab tabCtl = new Tab(tabTitle, vLayout);
    this.getTabs().add(tabCtl);

    this.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
      int iTabIdx = getSelectionModel().getSelectedIndex();
      processTabChangeEvent(iTabIdx);
    });

    return tabCtl;
  }

  protected void processTabChangeEvent(final int tabIdx) {}

  public Tab addSimpleFormAsTab(final String tabTitle, final SimpleFormBase frm) {
    Tab tabCtl = new Tab(tabTitle, frm);
    this.getTabs().add(tabCtl);
    return tabCtl;
  }

  public void switchToTab(final int tabIdx) {
      SingleSelectionModel<Tab> selectionModel = this.getSelectionModel();
      selectionModel.select(tabIdx);
  }

}