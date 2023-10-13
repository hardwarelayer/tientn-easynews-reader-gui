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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

public final class MainTabbedPane extends TabPaneBase {
 
    @Getter private ReaderModel dataModel;
    @Getter protected Stage primaryStage = null;
    @Getter protected Desktop desktop = null;

    ManagementWindowTab paneManagement;
    WordMatchWindowTab paneWordMatch;
    WordMatch2WindowTab paneWordMatch2;
    ArticleWordBuildWindowTab paneTNAWordBuilder;
    ArticleReadWindowTab paneArticleRead;
    KanjiAutoDisplayTab paneArticleAutoDisplay;
    GrammarReadWindowTab paneGrammarRead;
    GrammarListenWindowTab paneGrammarListen;
    TableViewTab tblViewTab;
    KanjiDictionaryTab kanjiDictTab;

    private int iLastShownTab = 0, iCurrentShownTab = 0;

    public MainTabbedPane(Desktop desktop, Stage primStage, final ReaderModel model) {
        super();
        this.dataModel = model;
        this.primaryStage = primStage;
        this.desktop = desktop;
    }
 
    @Override
    protected void processTabChangeEvent(final int tabIdx) {
        switch (tabIdx) {
        case 0:
            paneManagement.onShow();
            break;
        case 1:
            paneTNAWordBuilder.onShow();
            break;
        case 2:
            paneWordMatch.onShow();
            break;
        case 3:
            paneWordMatch2.onShow();
            break;
        case 4:
            paneArticleAutoDisplay.onShow();
            break;
        case 5:
            paneArticleRead.onShow();
            break;
        case 6:
            paneGrammarRead.onShow();
            break;
        case 7:
            paneGrammarListen.onShow();
            break;
        }
        iLastShownTab = iCurrentShownTab;
        iCurrentShownTab = tabIdx;

        if (iLastShownTab != iCurrentShownTab) {
            //notify last tab about this switch
            switch (iLastShownTab) {
            case 0:
                paneManagement.onStopShow();
                break;
            case 1:
                paneTNAWordBuilder.onStopShow();
                break;
            case 2:
                paneWordMatch.onStopShow();
                break;
            case 3:
                paneWordMatch2.onStopShow();
                break;
            case 4:
                paneArticleAutoDisplay.onStopShow();
                break;
            case 5:
                paneArticleRead.onStopShow();
                break;
            case 6:
                paneGrammarRead.onStopShow();
                break;
            case 7:
                paneGrammarListen.onStopShow();
                break;
            }
        }
    }

    @Override
    protected void processFirstShowEvent() {
        paneManagement.processFirstShowEvent();
    }

    @Override
    protected void processKeypressEvent(KeyEvent ke) {
        int iSelTab = this.getSelectionModel().getSelectedIndex();
        //System.out.println("Current selected tab: "+String.valueOf(iSelTab));
        switch (iSelTab) {
            case 2:
                //wordmatch window
                if (paneWordMatch != null) {
                    paneWordMatch.processKeypressEvent(ke);
                }
                break;
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me) {
        int iSelTab = this.getSelectionModel().getSelectedIndex();
        //System.out.println("Current selected tab: "+String.valueOf(iSelTab));
        switch (iSelTab) {
            case 2:
                //wordmatch window
                if (paneWordMatch != null) {
                    paneWordMatch.processMouseEvent(me);
                }
                break;
        }
    }

    public void initTabs() {

        paneManagement = new ManagementWindowTab("Management", getDesktop(), primaryStage, this.dataModel, this);
        paneManagement.setMinWidth(JBGConstants.MIN_WIDTH);
        paneManagement.setMaxWidth(JBGConstants.MIN_HEIGHT);

        paneTNAWordBuilder = new ArticleWordBuildWindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel, this);
        paneWordMatch = new WordMatchWindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        paneWordMatch2 = new WordMatch2WindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        paneArticleAutoDisplay = new KanjiAutoDisplayTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        paneArticleRead = new ArticleReadWindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        paneGrammarRead = new GrammarReadWindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        paneGrammarListen = new GrammarListenWindowTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);
        kanjiDictTab = new KanjiDictionaryTab(JBGConstants.MIN_WIDTH, JBGConstants.MIN_HEIGHT, getDesktop(), primaryStage, this.dataModel);

        tblViewTab = new TableViewTab("TableView", getDesktop(), primaryStage);

        this.addPaneAsTab("Management", paneManagement);
        this.addSimpleFormAsTab("ArticleWords", paneTNAWordBuilder);
        this.addSimpleStackedFormAsTab("WordMatch", paneWordMatch);
        this.addSimpleStackedFormAsTab("WordMatch2", paneWordMatch2);
        this.addSimpleStackedFormAsTab("Auto Kanji Display", paneArticleAutoDisplay);
        this.addSimpleFormAsTab("ArticleRead", paneArticleRead);
        this.addSimpleFormAsTab("GrammarRead", paneGrammarRead);
        this.addSimpleFormAsTab("GrammarListen", paneGrammarListen);
        this.addSimpleStackedFormAsTab("KanjiDict", kanjiDictTab);
        this.addPaneAsTab("Underworks", tblViewTab);

    }

}