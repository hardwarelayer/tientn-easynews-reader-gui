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
import javafx.scene.input.MouseButton;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import java.util.function.UnaryOperator;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import javafx.scene.control.CheckBox;

import java.lang.Character.UnicodeBlock;

import tientn.easynews.reader.gui.base.SimpleFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.JBGConstants;
import tientn.easynews.reader.data.TFMTTNAKanjiData;

import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.TFMTTNASentenceData;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/* note: add this to build.gradle
javafx {
    version = '16'
    modules = [ 'javafx.controls', 'javafx.fxml', 'javafx.media' ]
}
*/


// SimpleFormBase derived class
public class ArticleReadWindowTab extends SimpleFormBase {

    private TextArea tafArticleContent;
    private TextArea tafSentenceInput;
    private TextArea tafReadOnlyTestInput;
    private Label lblJCoinAmount;
    private Label lblCurrentSentenceValue;
    private Label lblSelectedArticleId;
    private Label lblSelectedArticleTitle;
    private Label lblCurrentListeningBonusValue;

    private Button btnRefresh;
    private Button btnLoadArticle;
    private Button btnStartTest;
    private Button btnRnLComplete;
    private Button btnListenToArticle;

    private CheckBox cbQuickTooltip;

    private MediaPlayer mediaPlayer = null;

    private TFMTTNAData currentTNA = null;
    private boolean isTNAFound = false;
    private String sCurrentSelectedText = "";
    private long lSelectedTextStart = 0;
    Timeline tmlTextSelectChecker = null;
    Tooltip tooltipArticle = null;

    private List<String> arrSentences;
    private String sCurrentTestSentence;
    private int sCurrentTestSentenceIdx;
    private int sCurrentTestSentenceVal;
    private int currentListeningSentenceCount = 0;
    private int currentListeningBonusVal;

    private String[] arrSingleByteCharArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "–", "−"};
    private String[] arrDoubleByteCharArray = {"０", "１", "２", "３", "４", "５", "６", "７", "８", "９", "ー", "ー"};

    static final String SENTENCE_DOT = "。";
    static final String DOUBLE_SPACE = "　";

    public ArticleReadWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

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

        Label lblSentenceVal = new Label("  Current Sentence Value:");
        lblCurrentSentenceValue = new Label("0");
        lblCurrentSentenceValue.setId("read-article-sentence-value");

        lblCurrentListeningBonusValue = new Label("0");
        lblCurrentListeningBonusValue.setId("read-article-listening-bonus-value");

        lblSelectedArticleId = new Label("...");
        lblSelectedArticleTitle = new Label("...");

        cbQuickTooltip = new CheckBox("Quick search with tooltip");
        cbQuickTooltip.setIndeterminate(false);
        cbQuickTooltip.setTooltip(new Tooltip("If enabled, content of selected kanjis will be shown in tooltip"));

        HBox titleRow = new HBox(btnRefresh, lblSelectedArticleId, lblSelectedArticleTitle);
        HBox coinRow = new HBox(lblCoin, lblJCoinAmount, lblSentenceVal, lblCurrentSentenceValue, 
            new Label("  Listening Bonus:"), lblCurrentListeningBonusValue, cbQuickTooltip);

        tafArticleContent = new TextArea();
        tafArticleContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.64));
        tafArticleContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafArticleContent.setId("read-article-content");
        tafArticleContent.setWrapText(true);
        tafArticleContent.setEditable(false);

        ContextMenu cmTNASens = new ContextMenu();
        MenuItem mi1 = new MenuItem("Dictionary");
        cmTNASens.getItems().add(mi1);
        mi1.setOnAction((ActionEvent event) -> {
            if (tafArticleContent.getSelectedText().length() > 0)
              this.showKanjiDlg(tafArticleContent.getSelectedText());
        });
        tafArticleContent.setContextMenu(cmTNASens);

        UnaryOperator<Change> filter = c -> {

            int caret = c.getCaretPosition();
            int anchor = c.getAnchor() ;

            if (caret - anchor > 0) {
                String text = tafArticleContent.getSelectedText();
                if (text.length() > 0 && text.equals(this.sCurrentSelectedText)) {
                    if (System.currentTimeMillis() - this.lSelectedTextStart > 150) {
                        if (this.tmlTextSelectChecker == null) {
                            this.tmlTextSelectChecker = new Timeline(
                               new KeyFrame(Duration.millis(500), evt -> fncShowLookupTooltip()) //end duration event
                               );
                            tmlTextSelectChecker.setCycleCount(1);
                        }
                        tmlTextSelectChecker.play();
                        //System.out.println(text);
                    }
                }
                else {
                    this.sCurrentSelectedText = text;
                    this.lSelectedTextStart = System.currentTimeMillis();
                }
            }

            return c ;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        tafArticleContent.setTextFormatter(formatter);

        tafSentenceInput = new TextArea();
        tafSentenceInput.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.1));
        tafSentenceInput.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafSentenceInput.setId("read-article-sentence-input");
        tafSentenceInput.setWrapText(true);
        tafSentenceInput.setEditable(false);
        tafSentenceInput.setPromptText("Input highlighted sentence above, and press Shift+ENTER to check");

        //this input is for user to test a kanji which they do not sure on ReadOnly mode
        tafReadOnlyTestInput = new TextArea();
        tafReadOnlyTestInput.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.06));
        tafReadOnlyTestInput.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafReadOnlyTestInput.setId("read-article-kanji-test-input");
        tafReadOnlyTestInput.setWrapText(false);
        tafReadOnlyTestInput.setEditable(true);
        tafReadOnlyTestInput.setPromptText("Input kanji here to test when you in ReadOnly mode");
        
        this.addHeaderText(txtFormTitle, 0, 0);
        this.addHeaderPane(titleRow, 0, 1);
        this.addHeaderPane(coinRow, 0, 2);
        this.addHeaderCtl(tafArticleContent, 0, 3);
        this.addHeaderCtl(tafSentenceInput, 0, 4);
        this.addHeaderCtl(tafReadOnlyTestInput, 0, 5);

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

        EventHandler<ActionEvent> fncListen = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                startListen();
            }
        };
        btnListenToArticle = createButton("Listen Whole Article", fncListen);

        EventHandler<ActionEvent> fncRnLComplete = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                completeReadOnly();
            }
        };
        btnRnLComplete = createButton("Complete Read&Listen", fncRnLComplete);

        EventHandler<ActionEvent> fncStartTestButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doStartTest();
            }
        };
        btnStartTest = createButton("Start Read", fncStartTestButtonClick);

        this.addBodyCtl(btnLoadArticle, 0, 0);
        this.addBodyCtl(btnListenToArticle, 1, 0);
        this.addBodyCtl(btnRnLComplete, 2, 0);
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
        switch (kc) {
            case ENTER:
                if (ke.isShiftDown()) {
                    processInputEnter(false);
                }
                else if (ke.isControlDown() || ke.isAltDown() || ke.isMetaDown()) {
                    processInputEnter(true);
                }
                break;
            case BACK_QUOTE:
                if (!ke.isShiftDown())
                    playCurrentSentenceMP3();
                else
                    doneListenToSentence();
                break;
        }
    }

    private void appendTextToContent(final String sText, final boolean highlight) {
        int iStartSel = 0;
        int iEndSel = 0;

        if (sText.length() < 1) return;

        String sContent = tafArticleContent.getText();
        iStartSel = sContent.length();
        StringBuilder sb = new StringBuilder(sContent + "\n" + sText.trim().replace("、", "、\n").replace(SENTENCE_DOT, "") + SENTENCE_DOT);
        tafArticleContent.setText(sb.toString());
        iEndSel = tafArticleContent.getText().length();

        tafArticleContent.selectPositionCaret(tafArticleContent.getLength()); 
        tafArticleContent.deselect(); 
        tafArticleContent.setScrollTop(Double.MAX_VALUE);

        if (highlight) {
            //System.out.println("highlighting ..." + String.valueOf(iStartSel) + " " + String.valueOf(iEndSel));
            tafArticleContent.positionCaret(iStartSel);
            tafArticleContent.selectRange(iStartSel, iEndSel);
        }
    }

    private void fncShowLookupTooltip() {
        if (!cbQuickTooltip.isSelected()) return;
        String sText = tafArticleContent.getSelectedText().trim();
        if (sText.equals(this.sCurrentSelectedText)) {
            //neu da select text va sau 500 millis chua change
            if (this.tooltipArticle == null) {
                this.tooltipArticle = new Tooltip("...");
                Tooltip.install(this.tafArticleContent, this.tooltipArticle);
            }
            String sValues = String.format("%s\n%s", sText, this.getDataModel().lookupKanjiValues(sText));
            tooltipArticle.setText(sValues);
            //System.out.println(sText);
        }
    }

    private void processInputEnter(final boolean flgSkip) {
        if (!this.getDataModel().isReadStarted()) return;

        if (!flgSkip) {
            validateSentence();
        }
        else {
            tafSentenceInput.setText(sCurrentTestSentence);
            validateSentence();
        }
    }

    private void processListenToSentence() {
        if (!this.getDataModel().isReadStarted()) return;
    }

    private void startListen() {
        if (currentTNA == null) return;
        if (mediaPlayer != null) return;

        String id = currentTNA.getId().toString();
        String sFileName = getDataModel().getArticleMP3FileName(id);
        System.out.println(sFileName);
        File f = new File(sFileName);
        if(f.exists() && !f.isDirectory()) { 
            // do something
            playMP3(sFileName);
            btnStartTest.setDisable(true);
        }
        else {
            showInformation("ERROR", "No sound file to listen!");
        }
    }

    private void playMP3(final String fullFileName) {
        if (mediaPlayer != null) return;

        Media media;
        try {
            Thread thread = new Thread();
            media = new Media("file:///" + fullFileName);

            thread.setName("article_play_thread1");

            //why I declare the mediaPlayer outside?
            //because Java GB will destroy this object right after the function, but actually, it is used in other thread
            //so sometime the player can play all sound length, but mostly, it only plays some seconds
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.dispose();
                mediaPlayer = null;
            });
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
        } catch (Exception ex) {
          System.out.println("ERROR on playing MP3: " + ex.getMessage());
        }
    }

    private int getIndexOfDifferent(final String sEnter, final String sTrial) {
        int minLen = Math.min(sEnter.length(), sTrial.length());
        for (int i = 0 ; i != minLen ; i++) {
            char chA = sEnter.charAt(i);
            char chB = sTrial.charAt(i);
            if (chA != chB) {
                return i;
            }
        }
        return -1;
    }

    private void validateSentence() {
        String sText = tafSentenceInput.getText();
        if (sText == null) return;
        sText = sText.trim();

        //System.out.println("trial: " + sCurrentTestSentence);
        //System.out.println("enter: " + sText);

        if (sText.equals(sCurrentTestSentence)) {
            //System.out.println("valid match!" + String.valueOf(this.sCurrentTestSentenceVal));
            this.getDataModel().increaseJCoin(this.sCurrentTestSentenceVal);
            lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
            if (stepUpTestSentence()) {
                //System.out.println("stepped up!");
                lblCurrentSentenceValue.setText(String.valueOf(this.sCurrentTestSentenceVal));
                appendTextToContent(sCurrentTestSentence, true);
                tafSentenceInput.clear();
                tafSentenceInput.requestFocus();
            }
            else {
                //System.out.println("can't stepped up, end test!");
                doEndTest();
            }
        }
        else {
            //System.out.println("NOMATCH trial: " + sCurrentTestSentence);
            //System.out.println("NOMATCH enter: " + sText);
            int iDiffPos = getIndexOfDifferent(sText, sCurrentTestSentence);
            if (iDiffPos >= 0) {
                int iRemainLength = sText.length() - iDiffPos;
                tafSentenceInput.positionCaret(iDiffPos);
                tafSentenceInput.selectRange(iDiffPos, iDiffPos+1);
            }
        }
        //System.out.println(sText);
    }

    private String getCurrentArticleEachSentenceMP3Folder() {
        final String articleMP3Folder = getDataModel().getArticleMP3FolderPath();
        final String currentArticleMP3FolderPath = articleMP3Folder + "/" + currentTNA.getId().toString();
        File folder = new File(currentArticleMP3FolderPath);
        if(folder.exists() && folder.isDirectory()) { 
            return currentArticleMP3FolderPath;
        }
        return null;
    }

    //this mode is for Read mode, not listen only
    private void playCurrentSentenceMP3() {
        if (!this.getDataModel().isReadStarted()) return;
        if (mediaPlayer != null) return;
        if (sCurrentTestSentenceIdx < 0) return;
        if (currentTNA == null) return;

        final String currentArticleMP3FolderPath = getCurrentArticleEachSentenceMP3Folder();
        if (currentArticleMP3FolderPath != null) {
            // do something
            final String sentenceMP3FullPath = String.format("%s/%03d.mp3", currentArticleMP3FolderPath, sCurrentTestSentenceIdx-1);
//System.out.println(sentenceMP3FullPath);
            File f = new File(sentenceMP3FullPath);
            if(f.exists() && !f.isDirectory()) {
                increaseListenBonusCounter();
                this.getDataModel().increaseJCoin((this.sCurrentTestSentenceVal / 3) + this.currentListeningBonusVal); //listen only gain 30%
                lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
                playMP3(sentenceMP3FullPath);
            }
            else {
                showInformation("ERROR", "File not found:\n"+sentenceMP3FullPath);
            }
        }
        else {
            showInformation("ERROR", "Folder for sentences MP3 not found!!!");
        }

    }

    private void increaseListenBonusCounter() {
        this.currentListeningSentenceCount++;
        if (this.currentListeningSentenceCount == 9) {
            this.currentListeningBonusVal += 1;
            this.currentListeningSentenceCount = 0;
            lblCurrentListeningBonusValue.setText(String.valueOf(this.currentListeningBonusVal));
        }
    }

    private void doneListenToSentence() {
        if (!this.getDataModel().isReadStarted()) return;
        if (mediaPlayer != null) return;
        if (currentTNA == null) return;
        String sText = sCurrentTestSentence;
        if (sText == null) return;

        //no mp3 for each sentence, do nothing
        if (getCurrentArticleEachSentenceMP3Folder() == null) return;

        increaseListenBonusCounter();
        this.getDataModel().increaseJCoin((this.sCurrentTestSentenceVal / 2) + this.currentListeningBonusVal); //listen only gain 50%
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        if (stepUpTestSentence()) {
            //System.out.println("stepped up!");
            lblCurrentSentenceValue.setText(String.valueOf(this.sCurrentTestSentenceVal));
            appendTextToContent(sCurrentTestSentence, true);
            tafSentenceInput.clear();
            tafSentenceInput.requestFocus();
            //listen to newly loaded sentence
            playCurrentSentenceMP3();
        }
        else {
            //System.out.println("can't stepped up, end test!");
            doEndTest();
        }
        //System.out.println(sText);
    }

    private void loadArticle() {

        if (this.getDataModel().isReadStarted()) return;

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

    //this mode is for reading by eye only, and voluntary click the button to gain jCoin after reading
    private void completeReadOnly() {
        if (this.getDataModel().isReadStarted()) return;

        if (this.arrSentences.size() < 1) return;

        if (!showQuestion("Complete article readonly", "Finish and get jCoin", "Are you sure you have completed?")) return;

        btnStartTest.setDisable(false);

        int iTotalValue = 0;
        for (String sen: this.arrSentences) {
            if (skipSentence(sen))
                continue;
            String senStrip = normalizeSentenceForTest(sen);
            iTotalValue += countKanjiInString(sen);
        }
        int iGainValue = (int) (iTotalValue / 2);

        this.getDataModel().increaseJCoin(iGainValue);
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
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

    private String preprocessSentence(String s) {
        String sRes = s.replace(SENTENCE_DOT, "").replace(DOUBLE_SPACE, "");
        if (!sRes.matches(".*\\d.*")) return sRes;

        for (int i = 0; i < 10; i ++) {
            sRes = sRes.replace(arrSingleByteCharArray[i], arrDoubleByteCharArray[i]);
        }
        return sRes;
    }

    private void refreshData() {
        if (this.getDataModel().isReadStarted()) return;

        this.currentTNA = this.getDataModel().getSelectedTNA();
        if (this.currentTNA == null) return;

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
                    this.arrSentences.add(preprocessSentence(s));
                }
            }
            else {
                this.arrSentences.add(preprocessSentence(sSentence));
            }
        }

        loadArticle();

    }

    private void doStartTest() {
        if (this.getDataModel().isReadStarted()) return;
        if (mediaPlayer != null) return;

        this.getDataModel().setReadStarted(true);
        clearFields();

        this.sCurrentTestSentenceIdx = 0;
        if (!stepUpTestSentence()) return;
        lblCurrentSentenceValue.setText(String.valueOf(this.sCurrentTestSentenceVal));
        appendTextToContent(this.sCurrentTestSentence, true);

        tafSentenceInput.setEditable(true);
        tafSentenceInput.requestFocus();

        this.btnLoadArticle.setDisable(true);
        this.tafReadOnlyTestInput.setEditable(false);
        this.btnRnLComplete.setDisable(true);
        this.btnStartTest.setDisable(true);
        this.btnListenToArticle.setDisable(true);
        this.btnRefresh.setDisable(true);
    }

    private void doEndTest() {
        if (!this.getDataModel().isReadStarted()) return;
        if (currentTNA == null) return;

        this.getDataModel().setReadStarted(false);

        this.getDataModel().increaseJCoin(5); //bonus in end game
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        currentTNA.setTotalTests(currentTNA.getTotalTests() + 1);
        currentTNA.setTotalCorrectTests(currentTNA.getTotalCorrectTests() + 1);

        tafSentenceInput.clear();
        tafArticleContent.selectRange(0,0);
        tafSentenceInput.setEditable(false);

        this.sCurrentTestSentenceIdx = 0;
        this.sCurrentTestSentenceVal = 0;
        this.lblCurrentSentenceValue.setText("0");
        this.btnLoadArticle.setDisable(false);
        this.tafReadOnlyTestInput.setEditable(true);
        this.btnRnLComplete.setDisable(false);
        this.btnListenToArticle.setDisable(false);
        this.btnStartTest.setDisable(false);
        this.btnRefresh.setDisable(false);
    }

    private String normalizeSentenceForTest(final String s) {
        return s.replace('　', ' ').replace('(', '（').replace(')', '）').replace('-', 'ー').trim();
    }

    private boolean stepUpTestSentence() {
        if (this.sCurrentTestSentenceIdx + 1 > this.arrSentences.size()) {
            //System.out.println("can't step up: " + String.valueOf(this.sCurrentTestSentenceIdx+1) + "/" + String.valueOf(this.arrSentences.size()));
            return false;
        }
        this.sCurrentTestSentenceIdx += 1;
        String sen = this.arrSentences.get(this.sCurrentTestSentenceIdx - 1);
        if (skipSentence(sen)) return stepUpTestSentence();
        this.sCurrentTestSentence = normalizeSentenceForTest(sen);
        this.sCurrentTestSentenceVal = countKanjiInString(sen);
        return true;
    }

    private void showKanjiDlg(final String selText) {
        showInformation(selText, String.format("%s\n\n\n", this.getDataModel().lookupKanjiValues(selText)));
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

    public void onStopShow() {}

    public void onShow() {
        //always refresh this
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        if (this.getDataModel().isNeedRefresh()) {
            //force refresh from article builder, the content may be changed so we need this
            //unset it
            this.getDataModel().setNeedRefresh(false);

            if (this.getDataModel().isReadStarted()) {
                return;
            }

            refreshData();
        }
        else if (tafArticleContent.getText().length() < 1) {
            //for autoload when emptied and has selected TNA
            currentTNA = this.getDataModel().getSelectedTNA();
            if (currentTNA != null) {
                refreshData();
            }
        }

    }

}
