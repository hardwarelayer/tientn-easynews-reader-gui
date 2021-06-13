package tientn.easynews.reader.gui;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

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
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;

import java.lang.Character.UnicodeBlock;

import tientn.easynews.reader.gui.base.SimpleFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.JBGConstants;

import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.TFMTTNASentenceData;

// SimpleFormBase derived class
public class ArticleReadWindowTab extends SimpleFormBase {

    private TextArea tafArticleContent;
    private TextArea tafSentenceInput;
    private Label lblJCoinAmount;
    private Label lblCurrentSentenceValue;
    private Label lblSelectedArticleId;
    private Label lblSelectedArticleTitle;

    private Label lblWordKanji;
    private Label lblWordHiragana;
    private Label lblWordHanviet;
    private Label lblWordMeaning;

    private Button btnRefresh;
    private Button btnLoadArticle;
    private Button btnStartTest;

    private TFMTTNAData currentTNA = null;
    private boolean testStarted;
    private List<String> arrSentences;
    private String currentTestSentence;
    private int currentTestSentenceIdx;
    private int currentTestSentenceVal;

    static final String SENTENCE_DOT = "。";

    public ArticleReadWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

        testStarted = false;
        arrSentences = new ArrayList<String>();
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();
    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Read Article");

        EventHandler<ActionEvent> fncRefreshButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                refreshData();
            }
        };
        btnRefresh = createButton("Refresh", fncRefreshButtonClick);

        Label lblCoin = new Label("Current JCoin:");
        lblJCoinAmount = new Label("0");
        lblJCoinAmount.setId("read-article-coin-amount");

        Label lblSentenceVal = new Label("Current Sentence Value:");
        lblCurrentSentenceValue = new Label("0");
        lblCurrentSentenceValue.setId("read-article-sentence-value");

        lblSelectedArticleId = new Label("...");
        lblSelectedArticleTitle = new Label("...");

        HBox titleRow = new HBox(btnRefresh, lblSelectedArticleId, lblSelectedArticleTitle);
        HBox coinRow = new HBox(lblCoin, lblJCoinAmount, lblSentenceVal, lblCurrentSentenceValue);

        tafArticleContent = new TextArea();
        tafArticleContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        tafArticleContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafArticleContent.setId("read-article-content");
        tafArticleContent.setWrapText(true);
        tafArticleContent.setEditable(false);

        tafSentenceInput = new TextArea();
        tafSentenceInput.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.1));
        tafSentenceInput.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafSentenceInput.setId("read-article-sentence-input");
        tafSentenceInput.setWrapText(true);
        tafSentenceInput.setPromptText("Input highlighed sentence above, and press Shift+ENTER to check");

        this.addHeaderText(txtFormTitle, 0, 0);
        this.addHeaderPane(titleRow, 0, 1);
        this.addHeaderPane(coinRow, 0, 2);
        this.addHeaderCtl(tafArticleContent, 0, 3);
        this.addHeaderCtl(tafSentenceInput, 0, 4);

    }

    private void createBodyElements() {

        this.addBodyColumn(25);
        this.addBodyColumn(25);
        this.addBodyColumn(25);
        this.addBodyColumn(25);

        EventHandler<ActionEvent> fncLoadArticleButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadArticle();
            }
        };
        btnLoadArticle = createButton("Load Article", fncLoadArticleButtonClick);

        EventHandler<ActionEvent> fncStartTestButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doStartTest();
            }
        };
        btnStartTest = createButton("Start Test", fncStartTestButtonClick);

        this.addBodyCtl(btnLoadArticle, 2, 0);
        this.addBodyCtl(btnStartTest, 3, 0);

    }

    private void clearFields() {
        tafArticleContent.clear();
        tafSentenceInput.clear();
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
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode(); 
        if (!ke.isShiftDown()) return;
        switch (kc) {
            case ENTER:
                processInputEnter();
                break;
        }
    }

    private void appendTextToContent(final String sText, final boolean highlight) {
        int iStartSel = 0;
        int iEndSel = 0;
        String sContent = tafArticleContent.getText();
        iStartSel = sContent.length();
        StringBuilder sb = new StringBuilder(sContent + "\n" + sText.trim().replace("、", "、\n").replace(SENTENCE_DOT, "") + SENTENCE_DOT);
        tafArticleContent.setText(sb.toString());
        iEndSel = tafArticleContent.getText().length();

        if (highlight) {
            //System.out.println("highlighting ..." + String.valueOf(iStartSel) + " " + String.valueOf(iEndSel));
            tafArticleContent.positionCaret(iStartSel);
            tafArticleContent.selectRange(iStartSel, iEndSel);
        }
    }

    private void processInputEnter() {
        if (!this.testStarted) return;

        validateSentence();
    }

    private void validateSentence() {
        String sText = tafSentenceInput.getText();
        if (sText == null) return;
        sText = sText.trim();

        System.out.println("trial: " + currentTestSentence);
        System.out.println("enter: " + sText);

        if (sText.equals(currentTestSentence)) {
            //System.out.println("valid match!");
            this.getDataModel().setJCoin(this.getDataModel().getJCoin() + this.currentTestSentenceVal);
            lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
            if (stepUpTestSentence()) {
                //System.out.println("stepped up!");
                lblCurrentSentenceValue.setText(String.valueOf(this.currentTestSentenceVal));
                appendTextToContent(currentTestSentence, true);
                tafSentenceInput.clear();
                tafSentenceInput.requestFocus();
            }
            else {
                //System.out.println("can't stepped up, end test!");
                doEndTest();
            }
        }
        System.out.println(sText);
    }

    private void loadArticle() {

        if (this.testStarted) return;

        clearFields();

        if (this.arrSentences.size() < 1) return;

        currentTNA = this.getDataModel().getSelectedTNA();
        if (currentTNA != null) {

            StringBuilder sb = new StringBuilder();
            for (String sen: this.arrSentences) {
                if (sen == null) continue;

                appendTextToContent(sen, false);

            }
        }
    }

    private boolean skipSentence(final String sSen) {
        if (sSen == null || sSen.length() < 1)
            return true;
        else if (sSen.length() == 15 &&
         sSen.charAt(0) == '[' && sSen.charAt(sSen.length()-1) == ']') {
            //skip this because it's article date time
            return true;
        }
        return false;
    }

    private void refreshData() {
        if (this.testStarted) return;

        this.currentTNA = this.getDataModel().getSelectedTNA();

        String sTNAId = this.getDataModel().getSelectedArticleId();
        lblSelectedArticleId.setText(sTNAId);
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        String sArticleTitle = currentTNA.getArticleTitle();
        lblSelectedArticleTitle.setText(sArticleTitle);

        this.arrSentences.clear();
        for (TFMTTNASentenceData sen: this.currentTNA.getArticleSentences()) {
            String sSentence = sen.getSentence();

            if (skipSentence(sSentence)) continue;

            int iDotIdx = sSentence.indexOf(SENTENCE_DOT);
            //we don't care dot in the first or last pos in  sentence
            if (iDotIdx > 0 && (iDotIdx + 1) < sSentence.length()) {
                //NHK Easynews can concat multiple sentence in a "Sentence" structure
                String[] subSentences = sSentence.split(SENTENCE_DOT);
                for (String s: subSentences) {
                    if (skipSentence(s)) continue;
                    this.arrSentences.add(s.replace(SENTENCE_DOT, ""));
                }
            }
            else {
                this.arrSentences.add(sSentence.replace(SENTENCE_DOT, ""));
            }
        }

        loadArticle();

    }

    private void doStartTest() {
        if (this.testStarted) return;

        this.testStarted = true;
        clearFields();

        this.currentTestSentenceIdx = 0;
        if (!stepUpTestSentence()) return;
        lblCurrentSentenceValue.setText(String.valueOf(this.currentTestSentenceVal));
        appendTextToContent(this.currentTestSentence, true);
        tafSentenceInput.requestFocus();

        this.btnLoadArticle.setDisable(true);
        this.btnStartTest.setDisable(true);
        this.btnRefresh.setDisable(true);
    }

    private void doEndTest() {
        if (!this.testStarted) return;
        tafSentenceInput.clear();
        tafArticleContent.selectRange(0,0);
        this.testStarted = false;
        this.currentTestSentenceIdx = 0;
        this.currentTestSentenceVal = 0;
        this.lblCurrentSentenceValue.setText("0");
        this.btnLoadArticle.setDisable(false);
        this.btnStartTest.setDisable(false);
        this.btnRefresh.setDisable(false);
    }

    private boolean stepUpTestSentence() {
        if (this.currentTestSentenceIdx + 1 > this.arrSentences.size()) {
            //System.out.println("can't step up: " + String.valueOf(this.currentTestSentenceIdx+1) + "/" + String.valueOf(this.arrSentences.size()));
            return false;
        }
        this.currentTestSentenceIdx += 1;
        String sen = this.arrSentences.get(this.currentTestSentenceIdx - 1);
        if (skipSentence(sen)) return stepUpTestSentence();
        this.currentTestSentence = sen;
        this.currentTestSentenceVal = countKanjiInString(sen);
        return true;
    }

    private int countKanjiInString(String s) {
        int iTotal = 0;
        for (char c: s.toCharArray()) {
            //Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                iTotal++;
            }
        }
        return iTotal;
    }

}
