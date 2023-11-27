package tientn.easynews.reader.gui;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.input.MouseButton;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javax.swing.KeyStroke;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import tientn.easynews.reader.gui.base.SimpleStackedFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.JBGConstants;
import tientn.easynews.reader.data.DictKanjiTableViewItem;
import tientn.easynews.reader.data.TFMTTNAKanjiData;

import static javafx.geometry.Pos.CENTER;

import java.sql.*;

// SimpleFormBase derived class
public class KanjiAutoDisplayTab extends SimpleStackedFormBase {

    private List<JBGKanjiItem> kanjiList;

    private Label lblFormTitle;

    private Label lblTotalKanjis;
    private Label lblTotalDictKanjis;
    private Label lblJCoinAmount;
    private Label lblStartAnchor;
    private Label lblEndAnchor;
    private Label lblShownHira, lblShownHv, lblShownMeaning;
    private Label lblLastShownKanji, lblLastShownHira, lblLastShowHv, lblLastShownMeaning;
    private Label lblNextShownKanji;
    private Label lblBottomKanji, lblBottomInfo, lblBottomLastInfo, lblBottomRemind;

    private CheckBox cbSortKanjiByRelation;
    private CheckBox cbMinimalAutoDisplay;

    private TextField tfMaxWordDisplaySteps;

    private TableView<DictKanjiTableViewItem> tvKanjiForDisplay;

    private Button btnStartAutoDisplay;
    private Button btnPauseAutoDisplay;
    private Button btnStopAutoDisplay;

    Timeline kjBlinkTimeline = null;
    private int iCurrentKanjiOnDisplay = 0;
    private int iCurrentDisplayStep = 0;
    private int iTotalKanjiShown = 0;
    private int iMaxWordDisplaySteps;
    private int iStartAnchor = -1, iEndAnchor = -1;

    private boolean isAutoDisplaying = false;
    private boolean isPausingDisplay = false;

    private static final String START_BTN_LABEL = "Start";
    private static final String STOP_BTN_LABEL = "Stop";
    private static final String PAUSE_BTN_LABEL = "||";
    private static final String UNPAUSE_BTN_LABEL = ">";

    private TFMTTNAData currentTNA = null;

    private MainTabbedPane parentPane;

    public KanjiAutoDisplayTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model, MainTabbedPane parentPane) {
        super(width, height, desktop, primStage, model);

        this.parentPane = parentPane;

        this.getTopBodyLabel().setId("auto-kanji-top-kanji-label");
        this.getMidBodyLabel().setId("auto-kanji-middle-kanji-label");
        this.getBottomBodyLabel().setId("auto-kanji-bottom-kanji-label");

        this.setMidBodyLabelStyle("-fx-opacity: 0.5;-fx-effect: none;");
        this.getMidBodyLabel().setWrapText(true);
        this.getMidBodyLabel().setTextOverrun(OverrunStyle.CLIP);
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();

    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Kanji Auto Display");
        this.addHeaderText(txtFormTitle, 0, 0);
    }

    private void createBodyElements() {

        this.addBodyColumn(100);

        cbSortKanjiByRelation = new CheckBox("Sort kanjis by relation");
        cbSortKanjiByRelation.setIndeterminate(false);
        cbSortKanjiByRelation.setTooltip(new Tooltip("If enabled, word in main list will be shown in related kanji order. Otherwise, it will be listed by article kanjis order(default)"));

        cbSortKanjiByRelation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                processLoadKanjisFromTNA();
            }
        });

        cbMinimalAutoDisplay = new CheckBox("Minimal display");
        cbMinimalAutoDisplay.setIndeterminate(false);
        cbMinimalAutoDisplay.setTooltip(new Tooltip("If enabled, word will be shown in minimal display inside the bottom bar.\n有効にすると、単語が下部バー内の最小限の表示で表示されます。"));
        cbMinimalAutoDisplay.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                toggleBigKanjiFields(!newValue);
            }
        });

        lblTotalKanjis = createLabel(String.format("%d/%d", 0, this.getDataModel().getDataKanjiItems().size()));
        lblJCoinAmount = createLabel("0");
        lblJCoinAmount.setId("wordmatch-coin-amount");
        lblStartAnchor = createLabel("?");
        lblEndAnchor = createLabel("?");

        lblShownHira  = createLabel("");
        lblShownHv = createLabel("");
        lblShownMeaning = createLabel("");
        lblShownHira.setId("auto-kanji-shown-hira-label");
        lblShownHv.setId("auto-kanji-shown-hv-label");
        lblShownMeaning.setId("auto-kanji-shown-meaning-label");

        lblLastShownKanji = new Label("...");
        lblLastShownHira = new Label("...");
        lblLastShowHv = new Label("...");
        lblLastShownMeaning = new Label("...");
        lblLastShownKanji.setId("auto-kanji-last-shown");
        lblLastShownHira.setId("auto-kanji-last-shown");

        lblNextShownKanji = new Label("...");
        lblNextShownKanji.setId("auto-kanji-next-shown-kanji");

        this.iMaxWordDisplaySteps = 4; //this function goes before class declaration?
        tfMaxWordDisplaySteps = new TextField(String.valueOf(this.iMaxWordDisplaySteps));
        tfMaxWordDisplaySteps.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.04));

        EventHandler<ActionEvent> fncStartAutoDisplay = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                startAutoDisplay();
            }
        };
        btnStartAutoDisplay = createButton(START_BTN_LABEL, fncStartAutoDisplay);
        EventHandler<ActionEvent> fncStopAutoDisplay = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                stopAutoDisplay();
            }
        };
        btnStopAutoDisplay = createButton(STOP_BTN_LABEL, fncStopAutoDisplay);

        EventHandler<ActionEvent> fncPauseAutoDisplay = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                pauseAutoDisplay();
            }
        };
        btnPauseAutoDisplay = createButton(PAUSE_BTN_LABEL, fncPauseAutoDisplay);

        tvKanjiForDisplay = createTableView(0, 1);
        tvKanjiForDisplay.setId("auto-kanji-loaded-kanji-column");
        createTableViewColumn(tvKanjiForDisplay, "Id", 0.05);
        createTableViewColumn(tvKanjiForDisplay, "Kanji", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "HV", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "Hiragana", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "Meaning", 0.3);

        tvKanjiForDisplay.getStyleClass().add("wordmatch-scroll-bar");
        tvKanjiForDisplay.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.50));

        ContextMenu articleCM = new ContextMenu();
        MenuItem acmMi1 = new MenuItem("Set Start Anchor");
        articleCM.getItems().add(acmMi1);
        MenuItem acmMi2 = new MenuItem("Set End Anchor");
        articleCM.getItems().add(acmMi2);
        SeparatorMenuItem sep = new SeparatorMenuItem();
        articleCM.getItems().add(sep);
        MenuItem acmMi3 = new MenuItem("Clear Anchors");
        articleCM.getItems().add(acmMi3);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        articleCM.getItems().add(sep2);
        MenuItem acmMi4 = new MenuItem("Transfer to WordMatch2");
        articleCM.getItems().add(acmMi4);
        acmMi1.setOnAction((ActionEvent event) -> {
            this.setStartAnchor();
        });
        acmMi2.setOnAction((ActionEvent event) -> {
            this.setEndAnchor();
        });
        acmMi3.setOnAction((ActionEvent event) -> {
            this.clearAnchors();
        });
        acmMi4.setOnAction((ActionEvent event) -> {
            this.transferToWordMatch2();
        });
        tvKanjiForDisplay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    articleCM.show(tvKanjiForDisplay, t.getScreenX(), t.getScreenY());
                }
            }
        });

        VBox bxShownContent = new VBox(lblShownHira, lblShownHv, lblShownMeaning);
        bxShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.5));
        bxShownContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.22));
        bxShownContent.setAlignment(CENTER);

        VBox bxLastShownContent = new VBox(lblLastShownKanji, lblLastShownHira, lblLastShowHv, lblLastShownMeaning);
        bxLastShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.25));
        bxLastShownContent.setAlignment(CENTER);

        VBox bxNextShownContent = new VBox(lblNextShownKanji);
        bxNextShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.25));
        bxNextShownContent.setAlignment(CENTER);

        HBox bxControlBox = new HBox(new Label("Kanjis"), lblTotalKanjis, btnStartAutoDisplay, 
            new Label("Current JCoin:"), lblJCoinAmount,
            new Label("Display Steps:"), tfMaxWordDisplaySteps,
            new Label("Start Anchor:"), lblStartAnchor,
            new Label("End Anchor:"), lblEndAnchor, 
            btnPauseAutoDisplay,
            btnStopAutoDisplay,
            cbSortKanjiByRelation,
            cbMinimalAutoDisplay);
        this.addBodyPane(bxControlBox, 0, 0);
        this.addBodyCtl(tvKanjiForDisplay, 0, 1);

        HBox hbxKanjiSupplementalBox= new HBox(bxNextShownContent, bxShownContent, bxLastShownContent);
        //hbxKanjiSupplementalBox.setAlignment(CENTER);
        this.addBodyPane(hbxKanjiSupplementalBox, 0, 2);

        VBox bxPlaceHolderBar = new VBox(new Label("..."));
        bxPlaceHolderBar.setAlignment(CENTER);
        bxPlaceHolderBar.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.25));
        this.addBodyPane(bxPlaceHolderBar, 0, 3);

        lblBottomKanji = new Label("...");
        lblBottomInfo = new Label("...");
        lblBottomLastInfo = new Label("...");
        lblBottomRemind = new Label("...");
        lblBottomKanji.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.08));
        lblBottomInfo.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.08));
        lblBottomLastInfo.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.08));
        lblBottomRemind.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.76));
        lblBottomInfo.setId("auto-kanji-min-bottom-info-label");
        lblBottomLastInfo.setId("auto-kanji-min-bottom-info-label");

        HBox bxBottomHBar = new HBox(lblBottomKanji, lblBottomInfo, lblBottomLastInfo, lblBottomRemind);
        bxBottomHBar.setSpacing(2);
        VBox bxBottomVBar = new VBox(bxBottomHBar);
        bxBottomVBar.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.11));
        this.addBodyPane(bxBottomVBar, 0, 4);
    }

    private void createTableViewColumn(final TableView<DictKanjiTableViewItem> tblView, final String title, final double width)
    {
        TableColumn<DictKanjiTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(tblView.widthProperty().multiply(width));
        tcol.setResizable(false);
        tblView.getColumns().add(tcol);
    }

    private TableView<DictKanjiTableViewItem> createTableView(int idxCtl, final double height) {
        TableView<DictKanjiTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<DictKanjiTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DictKanjiTableViewItem rowData = row.getItem();
                    processTableViewDblClick(idxCtl, rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<DictKanjiTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;
    }

    private void processTableViewDblClick(int iCtl, DictKanjiTableViewItem rowData) {
        if (iCtl == 0) {
            //this.getTopBodyLabel().setText(rowData.getKanji());
            //tafKanjiMeaning.setText(rowData.toString().replace("|", "\n"));
        }
        else {
            this.getBottomBodyLabel().setText(rowData.getKanji());
            //tafDictKanjiMeaning.setText(rowData.toString().replace("|", "\n").replace("<br>", "\n"));
        }
        //this.dataModel.setSelectedArticleId(sTNAId); //this will also set needRefresh
        //this.parentPane.switchToTab(1);
    }

    private int reloadTNAWordList() {
        int iTotalLoad = 0;
        if (currentTNA == null) return 0;

        List<JBGKanjiItem> lstSortedBuffer = null;

        if (cbSortKanjiByRelation.isSelected()) {

            lstSortedBuffer = new ArrayList<JBGKanjiItem>();
            List<JBGKanjiItem> lstBuffer = new ArrayList<JBGKanjiItem>(currentTNA.getKanjisForTest());
            while (lstSortedBuffer.size() < currentTNA.getKanjisForTest().size()) {
                JBGKanjiItem kItem = lstBuffer.get(0);
                lstBuffer.remove(0);
                lstSortedBuffer.add(kItem);
                String sKanjiChar = kItem.getKanji();
                for (String sChar: sKanjiChar.split("")) {
                    boolean flgFoundSimilar = true;
                    while (flgFoundSimilar && lstBuffer.size() > 0) {
                        flgFoundSimilar = false;
                        for (int i = 0; i < lstBuffer.size(); i++) {
                            if (lstBuffer.get(i).getKanji().contains(sChar)) {
                                lstSortedBuffer.add(lstBuffer.get(i));
                                lstBuffer.remove(i);
                                flgFoundSimilar = true;
                                break;
                            }
                        }
                    }
                }
            }

        }
        else {
            lstSortedBuffer = new ArrayList<JBGKanjiItem>(currentTNA.getKanjisForTest());
        }

        for (JBGKanjiItem kItem: lstSortedBuffer) {
            addWordToKanjiList(tvKanjiForDisplay, 
                kItem.getId().toString(), kItem.getKanji(), kItem.getHv(), kItem.getHiragana(), kItem.getMeaning());
            iTotalLoad++;
        }

        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        lblTotalKanjis.setText(String.format("%d/%d", iTotalLoad, this.getDataModel().getDataKanjiItems().size()));
        return iTotalLoad;
    }

    private void clearLists() {
        tvKanjiForDisplay.getItems().clear();
    }

    private void clearFields() {
        this.getMidBodyLabel().setText("");
        this.lblShownHira.setText("");
        this.lblShownHv.setText("");
        this.lblShownMeaning.setText("");
    }

    private void toggleBigKanjiFields(final boolean flg) {
        this.getTopBodyLabel().setVisible(flg);
        this.getMidBodyLabel().setVisible(flg);
        this.getBottomBodyLabel().setVisible(flg);
        lblShownHira.setVisible(flg);
        lblShownHv.setVisible(flg);
        lblShownMeaning.setVisible(flg);
        tvKanjiForDisplay.setVisible(flg);
        lblNextShownKanji.setVisible(flg);
        lblLastShownKanji.setVisible(flg);
        lblLastShownHira.setVisible(flg);
        lblLastShowHv.setVisible(flg);
        lblLastShownMeaning.setVisible(flg);
    }

    private void setMidBodyLabelStyle(final String css) {
        if (cbMinimalAutoDisplay.isSelected()) return;
        this.getMidBodyLabel().setStyle(css);
    }

    private void setMidBodyLabelVisible(final boolean flg) {
        if (cbMinimalAutoDisplay.isSelected() && flg) return;
        this.getMidBodyLabel().setVisible(flg);
    }

    private void processLoadKanjisFromTNA() {
        clearLists();

        currentTNA = this.getDataModel().getSelectedTNA();
        if (currentTNA != null) {
            reloadTNAWordList();
        }
    }

    private void setStartAnchor() {
        if (this.isAutoDisplaying || this.currentTNA == null) return;

        int iSelectedItem = tvKanjiForDisplay.getSelectionModel().getSelectedIndex();
        if (iSelectedItem < 0 || iSelectedItem >= tvKanjiForDisplay.getItems().size()) {
            return;
        }
        if (iEndAnchor >= 0 && iSelectedItem >= iEndAnchor) {
            this.showInformation("Error", "Start anchor position must be before end anchor!");
            return;
        }
        iStartAnchor = iSelectedItem;
        lblStartAnchor.setText(String.valueOf(iStartAnchor));
    }

    private void setEndAnchor() {
        if (this.isAutoDisplaying || this.currentTNA == null) return;

        int iSelectedItem = tvKanjiForDisplay.getSelectionModel().getSelectedIndex();
        if (iSelectedItem < 0 || iSelectedItem >= tvKanjiForDisplay.getItems().size()) {
            return;
        }
        if (iStartAnchor >=0) {
            if (iSelectedItem <= iStartAnchor) {
                this.showInformation("Error", "End anchor position must be after start anchor!");
                return;
            }
        }
        else {
            this.showInformation("Error", "Start anchor position must be set first!");
            return;
        }
        iEndAnchor = iSelectedItem;
        lblEndAnchor.setText(String.valueOf(iEndAnchor));
    }

    private void clearAnchors() {
        if (this.isAutoDisplaying || this.currentTNA == null) return;

        iStartAnchor = -1;
        iEndAnchor = -1;
        lblStartAnchor.setText("");
        lblEndAnchor.setText("");
    }

    private void transferToWordMatch2() {
        if (this.currentTNA == null) return;
        if (iStartAnchor >= 0 && iEndAnchor > iStartAnchor) {
            this.getDataModel().setTransfering(true);
            this.getDataModel().setTransferSortOrder(0);
            this.getDataModel().setTransferStartIdx(iStartAnchor);
            this.getDataModel().setTransferEndIdx(iEndAnchor);
            this.getDataModel().setCurrentWorkMode(JBGConstants.TEST_WORD_IN_ARTICLE);
            this.getDataModel().setNeedRefresh(true);
            this.parentPane.switchToTab(3);
        }
    }

    private void startAutoDisplay() {

        if (this.isAutoDisplaying || this.currentTNA == null) return;

        this.iMaxWordDisplaySteps = Integer.valueOf(this.tfMaxWordDisplaySteps.getText());

        int iSelectedItem = tvKanjiForDisplay.getSelectionModel().getSelectedIndex();
        if (this.iStartAnchor >= 0) {
            if (iSelectedItem < this.iStartAnchor || iSelectedItem > this.iEndAnchor)
                iSelectedItem = this.iStartAnchor;
        }
        if (iSelectedItem != -1 && iSelectedItem < tvKanjiForDisplay.getItems().size()) {
            iCurrentKanjiOnDisplay = iSelectedItem; //start auto display from here
        }
        else {
            iCurrentKanjiOnDisplay = 0;
        }

        if (this.kjBlinkTimeline == null) {
            this.kjBlinkTimeline = new Timeline(
               new KeyFrame(Duration.millis(100), evt -> fncEventKeyFrame(0)), //end duration event
               new KeyFrame(Duration.millis(1500), evt -> fncEventKeyFrame(1))
               );
            kjBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        }
        kjBlinkTimeline.play();

        this.btnStartAutoDisplay.setDisable(true);
        this.btnPauseAutoDisplay.setDisable(false);
        this.btnStopAutoDisplay.setDisable(false);

        this.isAutoDisplaying = true;
        this.isPausingDisplay = false;
    }

    private void pauseAutoDisplay() {

        if (!this.isAutoDisplaying) return;

        this.isPausingDisplay = !this.isPausingDisplay;
        if (this.isPausingDisplay)
            this.btnPauseAutoDisplay.setText(UNPAUSE_BTN_LABEL);
        else
            this.btnPauseAutoDisplay.setText(PAUSE_BTN_LABEL);
    }

    private void stopAutoDisplay() {

        if (!this.isAutoDisplaying) return;

        if (this.kjBlinkTimeline != null) kjBlinkTimeline.stop();
        iCurrentDisplayStep = 0;
        this.btnStartAutoDisplay.setDisable(false);
        this.btnPauseAutoDisplay.setDisable(true);
        this.btnStopAutoDisplay.setDisable(true);

        clearFields();

        this.isPausingDisplay = false;
        this.isAutoDisplaying = false;
    }

    private void fncEventKeyFrame(final int step) {
        if (step == 0) {
            if (this.iCurrentDisplayStep < this.iMaxWordDisplaySteps) {
                if (!this.isPausingDisplay)
                    this.iCurrentDisplayStep++;
            }
            else {
                if (this.iStartAnchor >= 0 && this.iEndAnchor > this.iStartAnchor) {
                    //only loop inside anchors
                    if (this.iCurrentKanjiOnDisplay < this.iEndAnchor) {
                        this.iCurrentKanjiOnDisplay++;
                    }
                    else {
                        this.iCurrentKanjiOnDisplay = this.iStartAnchor;
                    }
                }
                else {
                    if (this.iCurrentKanjiOnDisplay + 1 < tvKanjiForDisplay.getItems().size()) {
                        this.iCurrentKanjiOnDisplay++;
                    }
                    else {
                        this.iCurrentKanjiOnDisplay = 0;
                    }
                }

                //save last kanji to remind row
                String kanji = this.getMidBodyLabel().getText();

                if (cbMinimalAutoDisplay.isSelected()) {
                    String sMinDispRemind = lblBottomRemind.getText();
                    if (sMinDispRemind.length() + kanji.length() >= JBGConstants.AUTODISP_REMIND_CHARS_LIMIT) {
                      sMinDispRemind = sMinDispRemind.substring(0, JBGConstants.AUTODISP_REMIND_CHARS_LIMIT-kanji.length());
                    }
                    sMinDispRemind = kanji + sMinDispRemind;
                    this.lblBottomRemind.setText(sMinDispRemind);
                    lblBottomLastInfo.setText(lblBottomInfo.getText());
                }
                else {

                    lblLastShownKanji.setText(kanji);
                    lblLastShownHira.setText(lblShownHira.getText());
                    lblLastShowHv.setText(lblShownHv.getText());
                    lblLastShownMeaning.setText(lblShownMeaning.getText());

                    String sRemindLine1 = this.getBottomBodyLabel().getText();
                    String sRemindLine2 = this.getTopBodyLabel().getText();
                    String sCutKanjiOfLine1 = "";
                    if (sRemindLine1.length() + kanji.length() >= JBGConstants.WORDMATCH_MAX_REMIND_CHARS) {
                      sCutKanjiOfLine1 = sRemindLine1.substring(0, (sRemindLine1.length() + kanji.length())-JBGConstants.WORDMATCH_MAX_REMIND_CHARS);
                      sRemindLine1 = sRemindLine1.replace(sCutKanjiOfLine1, "");
                    }
                    sRemindLine1 += kanji;
                    if (sCutKanjiOfLine1.length() > 0) {
                        if (sRemindLine2.length() + sCutKanjiOfLine1.length() >= JBGConstants.WORDMATCH_MAX_REMIND_CHARS) {
                          sRemindLine2 = sRemindLine2.replace(sRemindLine2.substring(0, (sRemindLine2.length() + sCutKanjiOfLine1.length())-JBGConstants.WORDMATCH_MAX_REMIND_CHARS), "");
                        }
                        sRemindLine2 += sCutKanjiOfLine1;
                        this.getTopBodyLabel().setText(sRemindLine2);
                    }
                    this.getBottomBodyLabel().setText(sRemindLine1);
                }

                this.iCurrentDisplayStep = 1;
                //increase kanji count and jcoin if possible
                this.iTotalKanjiShown++;
                if (this.iTotalKanjiShown >= JBGConstants.AUTO_KANJI_DISPLAY_WORD_PER_POINT) {
                    this.getDataModel().setJCoin(this.getDataModel().getJCoin()+1);
                    this.iTotalKanjiShown = 1;
                    this.lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
                }
            }

            tvKanjiForDisplay.getSelectionModel().select(this.iCurrentKanjiOnDisplay);
            if (!cbMinimalAutoDisplay.isSelected()) tvKanjiForDisplay.scrollTo(this.iCurrentKanjiOnDisplay);

            final String sLastShownKanji = this.getMidBodyLabel().getText();

            DictKanjiTableViewItem tblItem = tvKanjiForDisplay.getItems().get(iCurrentKanjiOnDisplay);
            this.getMidBodyLabel().setText(tblItem.getKanji());
            this.setMidBodyLabelVisible(true);

            DictKanjiTableViewItem nextTblItem = null;
            if (iCurrentKanjiOnDisplay + 1 < tvKanjiForDisplay.getItems().size())
                nextTblItem = tvKanjiForDisplay.getItems().get(iCurrentKanjiOnDisplay+1);
            else
                nextTblItem = tvKanjiForDisplay.getItems().get(0);
            if (nextTblItem != null) this.lblNextShownKanji.setText(nextTblItem.getKanji());

            lblShownHira.setText(tblItem.getHiragana());
            lblShownHv.setText(tblItem.getHv());
            lblShownMeaning.setText(tblItem.getMeaning());

            if (cbMinimalAutoDisplay.isSelected()) {
                if (!sLastShownKanji.equals(tblItem.getKanji()))
                    lblBottomKanji.setText(String.format("%s\n  %s", tblItem.getKanji(), sLastShownKanji));
                lblBottomInfo.setText(String.format("%s\n%s\n%s", tblItem.getHiragana(), tblItem.getHv(), tblItem.getMeaning()));
            }
        }
        else if (step == 1) { //after 2 secs visible
            this.setMidBodyLabelVisible(false);
        }

        if (step == 0 && this.iCurrentDisplayStep >= this.iMaxWordDisplaySteps) {
            this.setMidBodyLabelStyle("-fx-opacity: 1.0;-fx-effect: dropshadow( one-pass-box, lightblue, 8, 0.0, 2, 0);");
        }
        else {
            if (this.iCurrentDisplayStep > 1) {
                this.setMidBodyLabelStyle("-fx-opacity: 0.8;-fx-effect: dropshadow( one-pass-box, lightblue, 8, 0.0, 2, 0);");
            }
            else {
                this.setMidBodyLabelStyle("-fx-opacity: 0.6;-fx-effect: none;");
            }
        }
    }

    private void addWordToKanjiList(final TableView<DictKanjiTableViewItem> tblView, final String id, final String kanji, final String hv, final String hira, final String meaning) {
        DictKanjiTableViewItem showItem = new DictKanjiTableViewItem(
            id, kanji, hv, hira, meaning
            );
        tblView.getItems().add(showItem);
    }

    //event
    private void processColumnButtonClick(final int iCol) {

        String sItem = null;
        TableView tv = null;

        switch(iCol) {
        case 0:
            tv = tvKanjiForDisplay;
            break;
        }

        if (tv != null) {
            //sItem = getListSelectedString(lv);
            //lblFirstCol.setText(sItem);
        }
    }

    private Label createLabel(final String sText) {
        Label label = new Label(sText);
        return label;
    }

    private Button createButton(final String title, EventHandler<ActionEvent> evt) {
        Button btnCtl = new Button();
        DropShadow shadow = new DropShadow();

        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnCtl.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnCtl.setEffect(null);
                }
        };

        btnCtl.setText(title);
        btnCtl.setOnAction(evt);

        //Adding the shadow when the mouse cursor is on
        btnCtl.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btnCtl.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btnCtl;
    }

    @Override
    protected void processKeypressEvent(final KeyEvent ke) {
        KeyCode kc = ke.getCode();
        if (kc == KeyCode.ENTER) {
            startAutoDisplay();
        }
    }

    public void onStopShow() {
        if (this.isAutoDisplaying) stopAutoDisplay();
    }

    public void onShow() {
        //always refresh this
        if (this.isAutoDisplaying) return;
        processLoadKanjisFromTNA();
        this.btnStopAutoDisplay.setDisable(true);
    }


    private int removeItemFromList(ListView<String> lv, final String sText) {
        ObservableList<String> items = lv.getItems();
        for (int i=0; i<items.size();i++) {
            String sItem = items.get(i);
            if (sItem.equals(sText)) {
                items.remove(i);
                break;
            }
        }
        return items.size();
    }

}