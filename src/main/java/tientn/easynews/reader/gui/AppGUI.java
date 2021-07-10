package tientn.easynews.reader.gui;

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
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.VBox;
import java.util.Locale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;

import tientn.easynews.reader.data.JBGConstants;

import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.gui.base.TabPaneBase;
import tientn.easynews.reader.gui.base.TabbedAppBase;
import tientn.easynews.reader.gui.base.GridPaneBase;

public final class AppGUI extends TabbedAppBase {
 
    @Getter private ReaderModel dataModel;

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    protected TabPaneBase initSceneElements(Stage primaryStage) {

        setDefaultArgs("Article Learn&Read - (c) Tran Ngoc Tien 2021", JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT);

        this.dataModel = new ReaderModel();
        System.out.println(this.dataModel.toString());

        MainTabbedPane tabPane = new MainTabbedPane(getDesktop(), primaryStage, this.dataModel);
        tabPane.initTabs();

        return tabPane;

    }

}