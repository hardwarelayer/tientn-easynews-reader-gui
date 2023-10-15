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
public class WordMatch2WindowTab extends SimpleStackedFormBase {

    private List<JBGKanjiItem> kanjiList;

    private Label lblFormTitle;

    private Label lblTotalKanjis;
    private Label lblTotalDictKanjis;
    private Label lblJCoinAmount;
    private Label lblStartAnchor;
    private Label lblEndAnchor;
    private Label lblShownKanji, lblShownHira, lblShownHv, lblShownMeaning;
    private Label lblLastShownKanji, lblLastShownHira, lblLastShownHv, lblLastShownMeaning;
    private Label lblNextShownKanji;
    private Label lblBottomKanji, lblBottomInfo, lblBottomLastInfo, lblBottomRemind;

    private CheckBox cbSortKanjiByRelation;

    private TextField tfKanjiRepeats;

    private TableView<DictKanjiTableViewItem> tvKanjiForDisplay;
    private GridPane gpMainArea;

    private Button btnStartGame;
    private Button btnStopGame;

    Timeline tmlNextPage = null;
    private int iCurrentKanjiOnDisplay = 0;
    private int iCurrentDisplayStep = 0;
    private int iTotalKanjiShown = 0;
    private int iStartAnchor = -1, iEndAnchor = -1;

    private int iTotalButtonRows = 18;
    private int iTotalButtonCols = 6;
    private int iTotalKanjiRepeat;
    private int iTotalButtons = iTotalButtonRows*iTotalButtonCols;
    private List<Button> lstSelectedButtons = new ArrayList<Button>();
    private int iTotalHiddenButtons = 0;

    private boolean isGameOn = false;

    private static final String START_BTN_LABEL = "Start";
    private static final String STOP_BTN_LABEL = "Stop";

    private TFMTTNAData currentTNA = null;

    public WordMatch2WindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

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

        cbSortKanjiByRelation = new CheckBox("Sort kanjis by relation");
        cbSortKanjiByRelation.setIndeterminate(false);
        cbSortKanjiByRelation.setTooltip(new Tooltip("If enabled, word in main list will be shown in related kanji order. Otherwise, it will be listed by article kanjis order(default)"));

        cbSortKanjiByRelation.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                processLoadKanjisFromTNA();
            }
        });

        lblTotalKanjis = createLabel(String.format("%d/%d", 0, this.getDataModel().getDataKanjiItems().size()));
        lblJCoinAmount = createLabel("0");
        lblJCoinAmount.setId("wordmatch-coin-amount");
        lblStartAnchor = createLabel("?");
        lblEndAnchor = createLabel("?");

        this.iTotalKanjiRepeat = JBGConstants.WORDMATCH2_KANJI_REPEAT;
        tfKanjiRepeats = new TextField(String.valueOf(this.iTotalKanjiRepeat));
        tfKanjiRepeats.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.04));

        EventHandler<ActionEvent> fncStartGame = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                startGame();
            }
        };
        btnStartGame = createButton(START_BTN_LABEL, fncStartGame);
        EventHandler<ActionEvent> fncStopGame = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                stopGame();
            }
        };
        btnStopGame = createButton(STOP_BTN_LABEL, fncStopGame);

        HBox bxControlBox = new HBox(new Label("Kanjis"), lblTotalKanjis, btnStartGame, 
            new Label("Current JCoin:"), lblJCoinAmount,
            new Label("Display Steps:"), tfKanjiRepeats,
            new Label("Start Anchor:"), lblStartAnchor,
            new Label("End Anchor:"), lblEndAnchor, 
            btnStopGame,
            cbSortKanjiByRelation);
        this.addHeaderPane(bxControlBox, 0, 0);
    }

    private void createBodyElements() {

        this.addBodyColumn(20);
        this.addBodyColumn(80);

        lblShownKanji = createLabel("");
        lblShownHira  = createLabel("");
        lblShownHv = createLabel("");
        lblShownMeaning = createLabel("");
        lblShownKanji.setId("wordmatch2-shown-kanji-label");
        lblShownHira.setId("wordmatch2-shown-hira-label");
        lblShownHv.setId("wordmatch2-shown-hv-label");
        lblShownMeaning.setId("wordmatch2-shown-meaning-label");

        lblLastShownKanji = new Label("...");
        lblLastShownHira = new Label("...");
        lblLastShownHv = new Label("...");
        lblLastShownMeaning = new Label("...");
        lblLastShownKanji.setId("auto-kanji-last-shown");

        lblNextShownKanji = new Label("...");
        lblNextShownKanji.setId("auto-kanji-next-shown-kanji");

        tvKanjiForDisplay = createTableView(0, 1);
        tvKanjiForDisplay.setId("word-match-2-loaded-kanjis");
        createTableViewColumn(tvKanjiForDisplay, "Id", 0.05);
        createTableViewColumn(tvKanjiForDisplay, "Kanji", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "HV", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "Hiragana", 0.2);
        createTableViewColumn(tvKanjiForDisplay, "Meaning", 0.3);

        tvKanjiForDisplay.getStyleClass().add("wordmatch-scroll-bar");
        tvKanjiForDisplay.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.9));

        ContextMenu articleCM = new ContextMenu();
        MenuItem acmMi1 = new MenuItem("Set Start Anchor");
        articleCM.getItems().add(acmMi1);
        MenuItem acmMi2 = new MenuItem("Set End Anchor");
        articleCM.getItems().add(acmMi2);
        MenuItem acmMi3 = new MenuItem("Clear Anchors");
        articleCM.getItems().add(acmMi3);
        acmMi1.setOnAction((ActionEvent event) -> {
            this.setStartAnchor();
        });
        acmMi2.setOnAction((ActionEvent event) -> {
            this.setEndAnchor();
        });
        acmMi3.setOnAction((ActionEvent event) -> {
            this.clearAnchors();
        });
        tvKanjiForDisplay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    articleCM.show(tvKanjiForDisplay, t.getScreenX(), t.getScreenY());
                }
            }
        });
        this.addBodyCtl(tvKanjiForDisplay, 0, 1);

        gpMainArea = new GridPane();
        gpMainArea.setAlignment(Pos.CENTER);
        gpMainArea.setHgap(4);
        gpMainArea.setVgap(10);
        gpMainArea.setPadding(new Insets(4, 4, 4, 4));
        //gpMainArea.setGridLinesVisible(true);
        for (int i = 0; i < 6; i++) {
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(15);
            gpMainArea.getColumnConstraints().add(column1);
        }

        HBox bxShownContent = new HBox(lblShownKanji, lblShownHira, lblShownHv, lblShownMeaning);
        bxShownContent.setSpacing(4);
        bxShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        bxShownContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.05));
        bxShownContent.setAlignment(CENTER);

        HBox bxLastShownContent = new HBox(lblLastShownKanji, lblLastShownHira, lblLastShownHv, lblLastShownMeaning);
        bxLastShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.25));
        bxLastShownContent.setAlignment(CENTER);

        this.addBodyPane(new VBox(bxShownContent, gpMainArea, bxLastShownContent), 1, 1);

/*
        VBox bxNextShownContent = new VBox(lblNextShownKanji);
        bxNextShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.25));
        bxNextShownContent.setAlignment(CENTER);
*/

/*
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
*/
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
        this.lblShownKanji.setText("");
        this.lblShownHira.setText("");
        this.lblShownHv.setText("");
        this.lblShownMeaning.setText("");
        this.lblLastShownKanji.setText("");
        this.lblLastShownHira.setText("");
        this.lblLastShownHv.setText("");
        this.lblLastShownMeaning.setText("");
        this.lstSelectedButtons.clear();
        this.gpMainArea.getChildren().clear();
    }

    private void setMidBodyLabelStyle(final String css) {
        this.getMidBodyLabel().setStyle(css);
    }

    private void setMidBodyLabelVisible(final boolean flg) {
        if (flg) return;
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
        if (this.isGameOn || this.currentTNA == null) return;

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
        if (this.isGameOn || this.currentTNA == null) return;

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
        if (this.isGameOn || this.currentTNA == null) return;

        iStartAnchor = -1;
        iEndAnchor = -1;
        lblStartAnchor.setText("");
        lblEndAnchor.setText("");
    }

    private void fncLoadNext() {
        int iLoaded = this.loadNewKanjiButtons();
        if (iLoaded < 1) {
            stopGame();
        }
        tmlNextPage.stop();
    }

    private void processNextPageCheck() {
        if (this.iTotalHiddenButtons >= gpMainArea.getChildren().size()) {

            if (this.tmlNextPage == null) {
                //because we can't call button remove here, it will cause object access conflict
                //so I use a delayed trigger
                this.tmlNextPage = new Timeline(
                   new KeyFrame(Duration.millis(250), evt -> fncLoadNext()) //end duration event
                   );
                tmlNextPage.setCycleCount(1);
            }
            tmlNextPage.play();

        }
    }

    private void processKanjiButtonClick(DictKanjiTableViewItem item, ActionEvent e) {
        Button btnObj = (Button)e.getSource();
        if (btnObj == null) return;

        if (lblShownKanji.getText().length() > 0) {
            if (lblShownKanji.getText().equals(item.getKanji())) {
                btnObj.setDisable(true);
                this.lstSelectedButtons.add(btnObj);

                if (this.lstSelectedButtons.size() >= this.iTotalKanjiRepeat) {
                    //correct, clear selected
                    for (Button b: this.lstSelectedButtons) {
                        b.setVisible(false);
                        this.iTotalHiddenButtons++;
                        processNextPageCheck();
                    }

                    int totalJCoin = this.getDataModel().getJCoin();
                    totalJCoin += this.lstSelectedButtons.size();
                    this.getDataModel().setJCoin(totalJCoin); //set back to parent
                    lblJCoinAmount.setText(String.valueOf(totalJCoin));

                    this.lstSelectedButtons.clear();

                    lblLastShownKanji.setText(lblShownKanji.getText());
                    lblLastShownHira.setText(lblShownHira.getText());
                    lblLastShownHv.setText(lblShownHv.getText());
                    lblLastShownMeaning.setText(lblShownMeaning.getText());

                    lblShownKanji.setText("");
                    lblShownHira.setText("");
                    lblShownHv.setText("");
                    lblShownMeaning.setText("");
                }
            }
            return;
        }

        //first kanji
        lblShownKanji.setText(item.getKanji());
        lblShownHira.setText(item.getHiragana());
        lblShownHv.setText(item.getHv());
        lblShownMeaning.setText(item.getMeaning());
        btnObj.setDisable(true);
        this.lstSelectedButtons.add(btnObj);
    }

    private int loadNewKanjiButtons() {

        if (this.iStartAnchor >= 0 && this.iEndAnchor > this.iStartAnchor) {
            //only loop inside anchors
            if (this.iCurrentKanjiOnDisplay >= this.iEndAnchor) {
                return 0;
            }
        }
        else {
            if (this.iCurrentKanjiOnDisplay + 1 >= tvKanjiForDisplay.getItems().size()) {
                return 0;
            }
        }

        List<DictKanjiTableViewItem> lstBuffer = new ArrayList<DictKanjiTableViewItem>();
        for (int i = 0; i < (int) (this.iTotalButtons/this.iTotalKanjiRepeat); i++) {
            for (int rep = 0; rep < this.iTotalKanjiRepeat; rep++) {
                if (lstBuffer.size() >= this.iTotalButtons) break;
                lstBuffer.add(tvKanjiForDisplay.getItems().get(this.iCurrentKanjiOnDisplay));
            }

            if (this.iStartAnchor >= 0 && this.iEndAnchor > this.iStartAnchor) {
                //only loop inside anchors
                if (this.iCurrentKanjiOnDisplay < this.iEndAnchor) {
                    this.iCurrentKanjiOnDisplay++;
                }
                else {
                    break;
                    //this.iCurrentKanjiOnDisplay = this.iStartAnchor;
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

        }
        for (int rep = 0; rep < this.iTotalKanjiRepeat; rep++)
            Collections.shuffle(lstBuffer);

        int iBufferIdx = 0;
        gpMainArea.getChildren().clear();
        for (int r = 0; r < 18; r++) {
            for (int c = 0; c < 6; c++) {

                if (iBufferIdx >= lstBuffer.size()) break;
                //tvKanjiForDisplay.scrollTo(this.iCurrentKanjiOnDisplay);
                DictKanjiTableViewItem tblItem = lstBuffer.get(iBufferIdx);
                if (iBufferIdx + 1 < this.iTotalButtons)
                    iBufferIdx++;
                String sKj = tblItem.getKanji();

                EventHandler<ActionEvent> fncBtnClick = new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        processKanjiButtonClick(tblItem, e);
                    }
                };
                Button btnTest = createButton(sKj, fncBtnClick);
                btnTest.setTooltip(new Tooltip(tblItem.getHiragana()+"\n" + tblItem.getHv()+"\n" + tblItem.getMeaning()));
                btnTest.getStyleClass().add("word-match-2-button");
                gpMainArea.add(btnTest, c, r); 
            }
        }

        return lstBuffer.size();
    }

    private void startGame() {

        if (this.isGameOn || this.currentTNA == null) return;

        this.iTotalKanjiRepeat = Integer.valueOf(this.tfKanjiRepeats.getText());

        int iSelectedItem = tvKanjiForDisplay.getSelectionModel().getSelectedIndex();
        if (this.iStartAnchor >= 0) iSelectedItem = this.iStartAnchor;

        if (this.iStartAnchor >= 0 && this.iEndAnchor > this.iStartAnchor) {
            //only loop inside anchors
            this.iCurrentKanjiOnDisplay = this.iStartAnchor;
        }
        else {
            if (iSelectedItem != -1 && iSelectedItem < tvKanjiForDisplay.getItems().size()) {
                this.iCurrentKanjiOnDisplay = iSelectedItem; //start auto display from here
            }
            else {
                this.iCurrentKanjiOnDisplay = 0;
            }
        }

        this.iTotalHiddenButtons = 0;

        loadNewKanjiButtons();

        this.btnStartGame.setDisable(true);
        this.btnStopGame.setDisable(false);

        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        this.isGameOn = true;
    }

    private void stopGame() {

        if (!this.isGameOn) return;

        if (this.tmlNextPage != null) tmlNextPage.stop();
        iCurrentDisplayStep = 0;
        this.btnStartGame.setDisable(false);
        this.btnStopGame.setDisable(true);

        clearFields();

        this.iTotalHiddenButtons = 0;
        this.isGameOn = false;
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
            startGame();
        }
    }

    public void onStopShow() {
        if (this.isGameOn) stopGame();
    }

    public void onShow() {
        //always refresh this
        if (this.isGameOn) return;
        processLoadKanjisFromTNA();
        this.btnStopGame.setDisable(true);
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