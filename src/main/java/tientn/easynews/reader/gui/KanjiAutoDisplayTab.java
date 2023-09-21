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

    private TextField tfMaxWordDisplaySteps;

    private TableView<DictKanjiTableViewItem> lvFirstCol;
    private TableView<DictKanjiTableViewItem> lvSecondCol;

    private Button btnStartAutoDisplay;
    private Button btnStopAutoDisplay;

    Timeline kjBlinkTimeline = null;
    private int iCurrentKanjiOnDisplay = 0;
    private int iCurrentDisplayStep = 0;
    private int iTotalKanjiShown = 0;
    private int iMaxWordDisplaySteps;
    private int iStartAnchor = -1, iEndAnchor = -1;

    private boolean isAutoDisplaying = false;

    private static final String START_BTN_LABEL = "Start";
    private static final String STOP_BTN_LABEL = "Stop";

    private TFMTTNAData currentTNA = null;

    public KanjiAutoDisplayTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

        this.getTopBodyLabel().setId("auto-kanji-top-kanji-label");
        this.getMidBodyLabel().setId("auto-kanji-middle-kanji-label");
        this.getBottomBodyLabel().setId("auto-kanji-bottom-kanji-label");

        this.getMidBodyLabel().setStyle("-fx-opacity: 0.5;-fx-effect: none;");
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

        Label lblLoadedKanjis = new Label("Total Kanjis Loaded:");
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

        lvFirstCol = createTableView(0, 1);
        lvFirstCol.setId("auto-kanji-loaded-kanji-column");
        createTableViewColumn(lvFirstCol, "Id", 0.05);
        createTableViewColumn(lvFirstCol, "Kanji", 0.2);
        createTableViewColumn(lvFirstCol, "HV", 0.2);
        createTableViewColumn(lvFirstCol, "Hiragana", 0.2);
        createTableViewColumn(lvFirstCol, "Meaning", 0.3);

        lvFirstCol.getStyleClass().add("wordmatch-scroll-bar");
        lvFirstCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.50));

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
        lvFirstCol.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    articleCM.show(lvFirstCol, t.getScreenX(), t.getScreenY());
                }
            }
        });

        //this.addBodyPane(new HBox(lblLoadedKanjis, lblTotalKanjis), 0, 1);

        VBox bxShownContent = new VBox(lblShownHira, lblShownHv, lblShownMeaning);
        bxShownContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        bxShownContent.setAlignment(CENTER);
        HBox bxControlBox = new HBox(new Label("Kanjis"), lblTotalKanjis, btnStartAutoDisplay, 
            new Label("Current JCoin:"), lblJCoinAmount,
            new Label("Display Steps:"), tfMaxWordDisplaySteps,
            new Label("Start Anchor:"), lblStartAnchor,
            new Label("End Anchor:"), lblEndAnchor, 
            btnStopAutoDisplay);
        this.addBodyPane(bxControlBox, 0, 0);
        this.addBodyCtl(lvFirstCol, 0, 1);
        this.addBodyPane(bxShownContent, 0, 2);
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
            this.getTopBodyLabel().setText(rowData.getKanji());
            //tafKanjiMeaning.setText(rowData.toString().replace("|", "\n"));
        }
        else {
            this.getBottomBodyLabel().setText(rowData.getKanji());
            //tafDictKanjiMeaning.setText(rowData.toString().replace("|", "\n").replace("<br>", "\n"));
        }
        //this.dataModel.setSelectedArticleId(sTNAId); //this will also set needRefresh
        //this.parentPane.switchToTab(1);
    }

    private int reloadTNAWordList(final String sPromptText) {
        int iTotalLoad = 0;
        boolean bNeedPrompt = true;
        if (currentTNA == null) return 0;
        if (sPromptText.length() < 1) bNeedPrompt = false;
        for (JBGKanjiItem kItem: currentTNA.getKanjisForTest()) {
            if (!bNeedPrompt)
                addWordToKanjiList(lvFirstCol, 
                    kItem.getId().toString(), kItem.getKanji(), kItem.getHv(), kItem.getHiragana(), kItem.getMeaning());
            else {
                String[] subSenWords = sPromptText.split("\\s+");
                for (String wrd : subSenWords) {
                    if (kItem.getKanji().contains(wrd) || wrd.contains(kItem.getKanji())) {
                        addWordToKanjiList(lvFirstCol, 
                            kItem.getId().toString(), kItem.getKanji(), kItem.getHv(), kItem.getHiragana(), kItem.getMeaning());
                    }
                }
            }
        }

        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        lblTotalKanjis.setText(String.format("%d/%d", iTotalLoad, this.getDataModel().getDataKanjiItems().size()));
        return iTotalLoad;
    }

    private void clearLists() {
        lvFirstCol.getItems().clear();
    }

    private void clearFields() {
        this.getMidBodyLabel().setText("");
        this.lblShownHira.setText("");
        this.lblShownHv.setText("");
        this.lblShownMeaning.setText("");
    }

    private void processLoadTNA() {
        clearLists();

        currentTNA = this.getDataModel().getSelectedTNA();
        if (currentTNA != null) {
            reloadTNAWordList("");
        }
    }

    private void setStartAnchor() {
        int iSelectedItem = lvFirstCol.getSelectionModel().getSelectedIndex();
        if (iSelectedItem < 0 || iSelectedItem >= lvFirstCol.getItems().size()) {
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
        int iSelectedItem = lvFirstCol.getSelectionModel().getSelectedIndex();
        if (iSelectedItem < 0 || iSelectedItem >= lvFirstCol.getItems().size()) {
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
        iStartAnchor = -1;
        iEndAnchor = -1;
        lblStartAnchor.setText("");
        lblEndAnchor.setText("");
    }

    private void startAutoDisplay() {

        if (this.isAutoDisplaying) return;

        this.iMaxWordDisplaySteps = Integer.valueOf(this.tfMaxWordDisplaySteps.getText());

        int iSelectedItem = lvFirstCol.getSelectionModel().getSelectedIndex();
        if (this.iStartAnchor >= 0) iSelectedItem = this.iStartAnchor;
        if (iSelectedItem != -1 && iSelectedItem < lvFirstCol.getItems().size()) {
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
        this.btnStopAutoDisplay.setDisable(false);

        this.isAutoDisplaying = true;
    }

    private void stopAutoDisplay() {

        if (!this.isAutoDisplaying) return;

        if (this.kjBlinkTimeline != null) kjBlinkTimeline.stop();
        iCurrentDisplayStep = 0;
        this.btnStartAutoDisplay.setDisable(false);
        this.btnStopAutoDisplay.setDisable(true);

        clearFields();

        this.isAutoDisplaying = false;
    }

    private void fncEventKeyFrame(final int step) {
        if (step == 0) {
            if (this.iCurrentDisplayStep < this.iMaxWordDisplaySteps) {
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
                    if (this.iCurrentKanjiOnDisplay + 1 < lvFirstCol.getItems().size()) {
                        this.iCurrentKanjiOnDisplay++;
                    }
                    else {
                        this.iCurrentKanjiOnDisplay = 0;
                    }
                }

                //save last kanji to remind row
                String kanji = this.getMidBodyLabel().getText();
                String sRemind = this.getBottomBodyLabel().getText();
                if (sRemind.length() + kanji.length() >= JBGConstants.WORDMATCH_MAX_REMIND_CHARS) {
                  sRemind = sRemind.replace(sRemind.substring(0, (sRemind.length() + kanji.length())-JBGConstants.WORDMATCH_MAX_REMIND_CHARS), "");
                }
                sRemind += kanji;
                this.getBottomBodyLabel().setText(sRemind);

                this.iCurrentDisplayStep = 1;
                //increase kanji count and jcoin if possible
                this.iTotalKanjiShown++;
                if (this.iTotalKanjiShown >= JBGConstants.AUTO_KANJI_DISPLAY_WORD_PER_POINT) {
                    this.getDataModel().setJCoin(this.getDataModel().getJCoin()+1);
                    this.iTotalKanjiShown = 1;
                    this.lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
                }
            }

            lvFirstCol.getSelectionModel().select(this.iCurrentKanjiOnDisplay);
            lvFirstCol.scrollTo(this.iCurrentKanjiOnDisplay);
            DictKanjiTableViewItem tblItem = lvFirstCol.getItems().get(iCurrentKanjiOnDisplay);
            this.getMidBodyLabel().setText(tblItem.getKanji());
            this.getMidBodyLabel().setVisible(true);

            lblShownHira.setText(tblItem.getHiragana());
            lblShownHv.setText(tblItem.getHv());
            lblShownMeaning.setText(tblItem.getMeaning());
        }
        else if (step == 1) { //after 2 secs visible
            this.getMidBodyLabel().setVisible(false);
        }

        if (step == 0 && this.iCurrentDisplayStep >= this.iMaxWordDisplaySteps) {
            this.getMidBodyLabel().setStyle("-fx-opacity: 1.0;-fx-effect: dropshadow( one-pass-box, lightblue, 8, 0.0, 2, 0);");
        }
        else {
            if (this.iCurrentDisplayStep > 1) {
                this.getMidBodyLabel().setStyle("-fx-opacity: 0.8;-fx-effect: dropshadow( one-pass-box, lightblue, 8, 0.0, 2, 0);");
            }
            else {
                this.getMidBodyLabel().setStyle("-fx-opacity: 0.6;-fx-effect: none;");
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
            tv = lvFirstCol;
            break;
        case 1:
            tv = lvSecondCol;
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

    private Button createColumnButton(final int iCol) {
        Button btnCtl = new Button();
        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processColumnButtonClick(iCol);
            }
        };
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

        btnCtl.setText("Test Button in TabWindow");
        btnCtl.setOnAction(fncButtonClick);

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
        processLoadTNA();
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