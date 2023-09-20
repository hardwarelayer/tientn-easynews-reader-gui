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
import javafx.scene.control.ScrollPane;
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
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;

import java.lang.Character.UnicodeBlock;

import tientn.easynews.reader.gui.base.SimpleFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.JBGConstants;

import tientn.easynews.reader.data.TFMTTNGData;
import tientn.easynews.reader.data.TFMTTNGPatternData;
import tientn.easynews.reader.data.TFMTTNGPatternSentence;
import tientn.easynews.reader.data.GrammarPatternTableViewItem;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/* note: add this to build.gradle
javafx {
    version = '16'
    modules = [ 'javafx.controls', 'javafx.fxml', 'javafx.media' ]
}
*/


// SimpleFormBase derived class
public class GrammarListenWindowTab extends SimpleFormBase {

    private TextArea tafGrammarContent;
    private TextArea tafSentenceInput;
    private Label lblJCoinAmount;
    private Label lblCurrentPattern;
    private Label lblSelectedGrammarId;
    private Label lblSelectedGrammarTitle;
    private Label lblGrammarPatternPreview;
    private Label lblCurrentSentenceValue;
    private Label lblReadOnlyBonus;

    private Button btnRefresh;
    private Button btnLoadGrammar;
    private Button btnStartTest;

    private Button btnReadAndListen;
    private Button btnRnLRepeatSentence;
    private Button btnRnLNextSentence;
    private Button btnRnLPreviousSentence;
    private Button btnRnLStop;

    private TFMTTNGData currentTNG = null;
    private TFMTTNGPatternData selectedPattern = null;
    private List<String> arrSentences;
    private String currentTestSentence;
    private int currentTestSentenceIdx;
    private int currentTestSentenceVal;
    private String selectedPatternId = null;

    private MediaPlayer mediaPlayer = null;
    private boolean inReadnListenMode = false;
    private int currentRnLSentenceIdx = -1;
    private String currentRnLSentenceId = null;

    private int iReadOnlyCount = 0;
    private int iReadOnlyBonusPoint = 0;

    TableView<GrammarPatternTableViewItem> tvGrammarPattern;

    private String[] arrSingleByteCharArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "–", "−"};
    private String[] arrDoubleByteCharArray = {"０", "１", "２", "３", "４", "５", "６", "７", "８", "９", "ー", "ー"};

    static final String SENTENCE_DOT = "。";
    static final String DOUBLE_SPACE = "　";

    public GrammarListenWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

        arrSentences = new ArrayList<String>();
    }

    @Override
    protected void initForm() {
        createMainSectionElements();
    }

    private void createMainSectionElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Read Grammar");

        EventHandler<ActionEvent> fncRefreshButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                refreshData();
            }
        };
        btnRefresh = createButton("Refresh", fncRefreshButtonClick);

        Label lblCoin = new Label("Current JCoin:");
        lblJCoinAmount = new Label("0");
        lblJCoinAmount.setId("read-grammar-coin-amount");

        lblCurrentSentenceValue = new Label("0");
        lblCurrentSentenceValue.setId("read-grammar-sentence-value");

        lblCurrentPattern = new Label("0");
        lblCurrentPattern.setId("read-grammar-sentence-value");

        lblReadOnlyBonus = new Label("0");
        lblReadOnlyBonus.setId("read-grammar-sentence-value");

        lblSelectedGrammarId = new Label("...");
        lblSelectedGrammarTitle = new Label("...");

        tvGrammarPattern = createGrammarPatternTV();
        tvGrammarPattern.setId("grammar-read-pattern-list");
        createGrammarPatternTVColumn("Id", 0.01);
        createGrammarPatternTVColumn("Title", 0.4);
        createGrammarPatternTVColumn("Description", 0.39);
        createGrammarPatternTVColumn("Test", 0.05);
        createGrammarPatternTVColumn("Correct", 0.05);

        EventHandler<ActionEvent> fncLoadGrammarButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadGrammar();
            }
        };
        btnLoadGrammar = createButton("Load Grammar", fncLoadGrammarButtonClick);

        EventHandler<ActionEvent> fncReadAndListen = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                tvGrammarPattern.setDisable(false);
                doReadAndListen();
            }
        };
        btnReadAndListen = createButton("Read&Listen", fncReadAndListen);

        EventHandler<ActionEvent> fncRnLRepeatSentence = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doListenRepeat();
            }
        };
        btnRnLRepeatSentence = createButton("Repeat Sentence", fncRnLRepeatSentence);
        btnRnLRepeatSentence.setDisable(true);

        EventHandler<ActionEvent> fncRnLNextSentence = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doListenNext();
            }
        };
        btnRnLNextSentence = createButton("Next Sentence", fncRnLNextSentence);
        btnRnLNextSentence.setDisable(true);

        EventHandler<ActionEvent> fncRnLPreviousSentence = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doListenPrevious();
            }
        };
        btnRnLPreviousSentence  = createButton("Previous Sentence", fncRnLPreviousSentence);
        btnRnLPreviousSentence.setDisable(true);

        EventHandler<ActionEvent> fncRnLStop = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doStopReadAndListen();
            }
        };
        btnRnLStop = createButton("Stop Read&Listen", fncRnLStop);
        btnRnLStop.setDisable(true);

        HBox titleRow = new HBox(btnRefresh, lblSelectedGrammarId, lblSelectedGrammarTitle, btnLoadGrammar);
        HBox coinRow = new HBox(lblCoin, lblJCoinAmount, 
            new Label("Sentence:"), lblCurrentSentenceValue,
            new Label("R/O bonus:"), lblReadOnlyBonus,
            new Label("  Current Pattern:"), lblCurrentPattern);

        tafGrammarContent = new TextArea();
        tafGrammarContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.56));

        tafGrammarContent.setId("read-grammar-content");
        tafGrammarContent.setWrapText(true);
        tafGrammarContent.setEditable(false);

        tafSentenceInput = new TextArea();
        tafSentenceInput.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.1));
        tafSentenceInput.setId("read-grammar-sentence-input");
        tafSentenceInput.setWrapText(true);
        tafSentenceInput.setEditable(false);
        tafSentenceInput.setPromptText("Input highlighted sentence above, and press Shift+ENTER to check");

        VBox readSection = new VBox(tafGrammarContent, tafSentenceInput);

        HBox mainRow = new HBox(tvGrammarPattern, readSection);
        HBox.setHgrow(tvGrammarPattern, Priority.ALWAYS);
        HBox.setHgrow(readSection, Priority.ALWAYS);
        tvGrammarPattern.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.4));
        readSection.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.6));

        lblGrammarPatternPreview = new Label("...");
        lblGrammarPatternPreview.setId("grammar-read-pattern-preview");
        lblGrammarPatternPreview.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.18));
        lblGrammarPatternPreview.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        lblGrammarPatternPreview.setAlignment(Pos.CENTER);
        lblGrammarPatternPreview.setWrapText(true);

        HBox footerRow = new HBox(btnReadAndListen, btnRnLRepeatSentence, btnRnLNextSentence, btnRnLPreviousSentence);
        HBox.setHgrow(footerRow, Priority.ALWAYS);

        this.addHeaderText(txtFormTitle, 0, 0);
        this.addHeaderPane(titleRow, 0, 1);
        this.addHeaderPane(coinRow, 0, 2);
        this.addHeaderCtl(lblGrammarPatternPreview, 0, 3);
        this.addHeaderPane(mainRow, 0, 4);
        this.addHeaderPane(footerRow, 0, 5);

        tvGrammarPattern.setDisable(true);
    }

    private void createGrammarPatternTVColumn(final String title, final double width)
    {
        TableColumn<GrammarPatternTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(tvGrammarPattern.widthProperty().multiply(width));
        tcol.setResizable(false);
        tvGrammarPattern.getColumns().add(tcol);
    }

    private TableView<GrammarPatternTableViewItem> createGrammarPatternTV() {
        TableView<GrammarPatternTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<GrammarPatternTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    GrammarPatternTableViewItem rowData = row.getItem();
                    processPatternTableViewDblClick(rowData);
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<GrammarPatternTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        return tableView;
    }

    private void processPatternTableViewDblClick(GrammarPatternTableViewItem rowData) {
        if (rowData == null) return;
        if (currentTNG == null) return;

        clearTestFields();

        final String patId = rowData.getId();
        selectPattern(patId, true);
        System.out.println(rowData.toString());
    }

    private void autoSelectNextPattern(boolean playSound) {
        int iCurrentSelect = tvGrammarPattern.getSelectionModel().getSelectedIndex();
        if (iCurrentSelect < 0) return;

        if (iCurrentSelect + 1  < tvGrammarPattern.getItems().size()) {
            iCurrentSelect++;
        }
        else {
            iCurrentSelect = 0;
        }
        tvGrammarPattern.getSelectionModel().select(iCurrentSelect);
        if (iCurrentSelect >= 10)
            tvGrammarPattern.scrollTo(iCurrentSelect - 10);
        GrammarPatternTableViewItem rowData = tvGrammarPattern.getSelectionModel().getSelectedItem();
        if (rowData == null) return;

        clearTestFields();
        final String patId = rowData.getId();
        selectPattern(patId, playSound);
    }

    private String playMP3(final String patternId, final String sentenceId) {
        Media media;
        final String fullFileName = this.getDataModel().getGrammarMP3FileName(patternId, sentenceId);
        try {
            Thread thread = new Thread();
            media = new Media("file:///" + fullFileName);
            System.out.println(media.getDuration().toSeconds());

            thread.setName("grammar_play_thread"+patternId+sentenceId);
            System.out.println(thread.getName());

            //why I declare the mediaPlayer outside?
            //because Java GB will destroy this object right after the function, but actually, it is used in other thread
            //so sometime the player can play all sound length, but mostly, it only plays some seconds
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.dispose();
                mediaPlayer = null;
            });
            mediaPlayer.setOnReady(() -> mediaPlayer.play());

            return fullFileName;
        } catch (Exception ex) {
          System.out.println("ERROR on playing MP3: " + ex.getMessage());
        }
        return null;
    }

    //currently, not used
    private String playSound(final String patternId, final String sentenceId) {
        final String fullFileName = this.getDataModel().getGrammarMP3FileName(patternId, sentenceId);

        try {
            AudioInputStream input=AudioSystem.getAudioInputStream(new File(fullFileName));
            Clip clip=AudioSystem.getClip();
            clip.open(input);
            clip.start();

            return fullFileName;
        } catch (Exception ex) {
          System.out.println("ERROR on playing MP3: " + ex.getMessage());
        }
        return null;
    }

    private void addBonusOfRepeating() {
        this.getDataModel().increaseJCoin(2); //bonus in repeat
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
    }

    private void doListenRepeat() {
        if (this.mediaPlayer != null) return;
        if (this.selectedPatternId == null || this.currentRnLSentenceId == null || this.currentRnLSentenceIdx < 0) return;

        addBonusOfRepeating();
        playMP3(this.selectedPatternId.toString(), this.currentRnLSentenceId);
    }

    private void doListenPrevious() {
        if (this.mediaPlayer != null) return;
        if (this.selectedPatternId == null || this.currentRnLSentenceId == null || this.currentRnLSentenceIdx < 0) return;
        if (this.currentRnLSentenceIdx > 0) {
            this.currentRnLSentenceIdx--;
            TFMTTNGPatternSentence s = this.selectedPattern.getSentence().get(this.currentRnLSentenceIdx);
            this.currentRnLSentenceId = s.getId().toString();

            addBonusOfRepeating();
            final String sRet = playMP3(this.selectedPatternId.toString(), this.currentRnLSentenceId);
//System.out.println("Sentence: " + s.getSentence() + " MP3:" + sRet);
        }
    }

    private void doListenNext() {
        if (this.mediaPlayer != null) return;
        if (this.selectedPatternId == null || this.currentRnLSentenceId == null || this.currentRnLSentenceIdx < 0) return;
        if (this.currentRnLSentenceIdx + 1 < this.selectedPattern.getSentence().size()) {
            this.currentRnLSentenceIdx++;
        }
        else {
            this.currentRnLSentenceIdx = 0;
        }

        TFMTTNGPatternSentence s = this.selectedPattern.getSentence().get(this.currentRnLSentenceIdx);
        this.currentRnLSentenceId = s.getId().toString();
        final String sRet = playMP3(this.selectedPatternId.toString(), this.currentRnLSentenceId);
System.out.println("Sentence: " + s.getSentence() + " MP3:" + sRet);
    }

    private void doStopReadAndListen() {
        if (!this.inReadnListenMode) return;
        if (this.mediaPlayer != null) return;

        this.inReadnListenMode = false;
        this.btnReadAndListen.setDisable(false);
        this.btnRnLRepeatSentence.setDisable(true);
        this.btnRnLNextSentence.setDisable(true);
        this.btnRnLPreviousSentence.setDisable(true);
        this.btnRnLPreviousSentence.setDisable(true);

        clearTestFields();
    }

    private void selectPattern(final String patId, final boolean playSound) {
        this.selectedPatternId = patId;
        this.selectedPattern = currentTNG.findPatternById(patId);
        lblCurrentPattern.setText(this.selectedPattern.getTitle());
         this.arrSentences.clear();

        for (TFMTTNGPatternSentence s: this.selectedPattern.getSentence()) {
            this.arrSentences.add(preprocessSentence(s.getSentence()));
        }

        this.currentRnLSentenceIdx = 0;
        TFMTTNGPatternSentence s = this.selectedPattern.getSentence().get(this.currentRnLSentenceIdx);
        this.currentRnLSentenceId = s.getId().toString();
        if (this.currentRnLSentenceId != null && playSound) {
            final String sRet = playMP3(this.selectedPatternId.toString(), this.currentRnLSentenceId);
System.out.println("Sentence: " + s.getSentence() + " MP3:" + sRet);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.selectedPattern.getSentenceAsString("\n"));
        appendTextToContent(sb.toString(), false, true);

        setPreviewValue();
    }

    private void reloadPatternList() {
        tvGrammarPattern.getItems().clear();
        TFMTTNGData tng = this.getDataModel().getSelectedTNG();
        if (tng == null) return;
        for (TFMTTNGPatternData pItem: tng.getGrammarPattern()) {
            //System.out.println(pItem);
            GrammarPatternTableViewItem showItem = new GrammarPatternTableViewItem(
                pItem.getId().toString(),
                pItem.getTitle(),
                pItem.getDescription().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n" )),
                pItem.getTotalTests(),
                pItem.getTotalCorrectTests()
                );
            tvGrammarPattern.getItems().add(showItem);
        }
    }

    private void setPreviewValue() {
        StringBuilder sb = new StringBuilder();
        if (this.selectedPattern != null) {
            sb.append(this.selectedPattern.getTitle())
                .append("\n")
                .append(this.selectedPattern.getDescriptionAsString("; "))
                .append("\n")
                .append(this.selectedPattern.getSentenceAndMeaningAsString("; "));
        }
        lblGrammarPatternPreview.setText(sb.toString());
    }

    private void clearTestFields() {
        tafGrammarContent.clear();
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
         {
            switch (kc) {
                case ENTER:
                    if (ke.isShiftDown()) {
                        doReadAndListen();
                    }
                    else
                        if (this.inReadnListenMode)
                            doListenNext();
                    break;
                case P:
                    if (this.inReadnListenMode)
                        doListenPrevious();
                    break;
                case R:
                    if (this.inReadnListenMode)
                        doListenRepeat();
                    break;
                case SPACE:
                    if (!this.inReadnListenMode)
                        moveGrammarToTail();
                    break;
            }
        }
    }

    private void appendTextToContent(final String sText, final boolean highlight, final boolean stripSentence) {
        int iStartSel = 0;
        int iEndSel = 0;

        if (sText.length() < 1) return;

        String sContent = tafGrammarContent.getText();
        iStartSel = sContent.length();
        StringBuilder sb = new StringBuilder();
        if (stripSentence) 
            sb.append(sContent + "\n" + sText.trim().replace("、", "、\n").replace(SENTENCE_DOT, "...\n") + SENTENCE_DOT);
        else
            sb.append(sContent + "\n" + sText);
        tafGrammarContent.setText(sb.toString());
        iEndSel = tafGrammarContent.getText().length();

        tafGrammarContent.selectPositionCaret(tafGrammarContent.getLength()); 
        tafGrammarContent.deselect(); 
        tafGrammarContent.setScrollTop(Double.MAX_VALUE);

        if (highlight) {
            //System.out.println("highlighting ..." + String.valueOf(iStartSel) + " " + String.valueOf(iEndSel));
            tafGrammarContent.positionCaret(iStartSel);
            tafGrammarContent.selectRange(iStartSel, iEndSel);
        }
    }

    private void loadGrammar() {
        if (this.getDataModel().isReadStarted()) return;
        clearTestFields();
        currentTNG = this.getDataModel().getSelectedTNG();
        if (currentTNG != null) {

            /*
            StringBuilder sb = new StringBuilder();
            for (String sen: this.arrSentences) {
                if (sen == null) continue;
                appendTextToContent(sen, false, false);
            }
            */
        }
        reloadPatternList();
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

        this.currentTNG = this.getDataModel().getSelectedTNG();
        if (this.currentTNG == null) return;

        String sTNGId = this.getDataModel().getSelectedGrammarId();
        lblSelectedGrammarId.setText(sTNGId);
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        String sGrammarTitle = currentTNG.getGrammarTitle();
        lblSelectedGrammarTitle.setText(sGrammarTitle);

        loadGrammar();

    }

    private void moveGrammarToTail() {
        if (this.getDataModel().isReadStarted()) return;
        if (this.mediaPlayer != null) return;

        GrammarPatternTableViewItem rowData = tvGrammarPattern.getSelectionModel().getSelectedItem();
        if (rowData == null) return;
        System.out.println(rowData.toString());
        if (this.getDataModel().moveTNGItemToTail(rowData.getId()) )
            reloadPatternList();
    }

    private void doReadAndListen() {
        if (this.getDataModel().isReadStarted()) return;
        if (this.mediaPlayer != null) return;

        if (!this.inReadnListenMode) {
            this.inReadnListenMode = true;
            this.btnReadAndListen.setDisable(true);
            this.btnRnLRepeatSentence.setDisable(false);
            this.btnRnLNextSentence.setDisable(false);
            this.btnRnLPreviousSentence.setDisable(false);
            this.btnRnLPreviousSentence.setDisable(false);
        }

        if (this.selectedPattern == null) {
            tvGrammarPattern.getSelectionModel().selectFirst();
            tvGrammarPattern.getFocusModel().focus(0);
            GrammarPatternTableViewItem rowData = tvGrammarPattern.getSelectionModel().getSelectedItem();
            if (rowData == null) return;
            clearTestFields();
            final String patId = rowData.getId();
            selectPattern(patId, true);
            return;
        }

        int iTotalValue = 0;
        for (String sen: this.arrSentences) {
            if (skipSentence(sen)) continue;
            String testSentence = normalizeSentenceForTest(sen);
            int iSentenceVal = countKanjiInString(testSentence);
            //for grammar, we set preferable amount
            if (iSentenceVal + 2 < 4) iSentenceVal = 4;
            iTotalValue += iSentenceVal;
        }
        int iGainValue = iTotalValue / 2;
        iGainValue += this.iReadOnlyBonusPoint;
        this.getDataModel().increaseJCoin(iGainValue);
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        autoSelectNextPattern(true);

        //the longer you read, the more bonus
        this.iReadOnlyCount++;
        if ( (this.iReadOnlyCount % 2) == 0 ) {
            this.iReadOnlyBonusPoint++;
            this.lblReadOnlyBonus.setText(String.valueOf(this.iReadOnlyBonusPoint));
        }
    }

    private void doStartTest() {
        if (this.selectedPattern == null) return;

        if (this.getDataModel().isReadStarted()) {
            doEndTest(false);
            return;
        }

        this.getDataModel().setReadStarted(true);
        clearTestFields();

        this.currentTestSentenceIdx = 0;
        if (!stepUpTestSentence()) return;
        lblCurrentSentenceValue.setText(String.valueOf(this.currentTestSentenceVal));
        appendTextToContent(this.currentTestSentence, true, true);

        tafSentenceInput.setEditable(true);
        tafSentenceInput.requestFocus();

        this.btnLoadGrammar.setDisable(true);
        this.btnStartTest.setText("Stop Test");
        this.btnRefresh.setDisable(true);
        this.btnReadAndListen.setDisable(true);
        this.tvGrammarPattern.setDisable(true);
    }

    private void doEndTest(final boolean autoNext) {
        if (!this.getDataModel().isReadStarted()) return;
        if (currentTNG == null) return;

        this.getDataModel().increaseJCoin(5); //bonus in end game
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        currentTNG.setTotalTests(currentTNG.getTotalTests() + 1);
        currentTNG.setTotalCorrectTests(currentTNG.getTotalCorrectTests() + 1);

        tafSentenceInput.clear();
        tafGrammarContent.selectRange(0,0);
        tafSentenceInput.setEditable(false);

        if (autoNext) {
            autoSelectNextPattern(false);
            this.getDataModel().setReadStarted(false); //force start
            doStartTest();
        }
        else {
            this.getDataModel().setReadStarted(false);
            this.currentTestSentenceIdx = 0;
            this.currentTestSentenceVal = 0;
            this.lblCurrentSentenceValue.setText("0");
            this.btnLoadGrammar.setDisable(false);
            this.btnStartTest.setText("Start Test");
            this.btnRefresh.setDisable(false);
            this.btnReadAndListen.setDisable(false);
            this.tvGrammarPattern.setDisable(false);
        }
    }

    private String normalizeSentenceForTest(final String s) {
        return s.replace('　', ' ').replace('(', '（').replace(')', '）').replace('-', 'ー').trim();
    }

    private boolean stepUpTestSentence() {
        if (this.currentTestSentenceIdx + 1 > this.arrSentences.size()) {
            //System.out.println("can't step up: " + String.valueOf(this.currentTestSentenceIdx+1) + "/" + String.valueOf(this.arrSentences.size()));
            return false;
        }
        this.currentTestSentenceIdx += 1;
        String sen = this.arrSentences.get(this.currentTestSentenceIdx - 1);
        if (skipSentence(sen)) return stepUpTestSentence();
        this.currentTestSentence = normalizeSentenceForTest(sen);
        this.currentTestSentenceVal = countKanjiInString(sen);
        //for grammar, we set preferable amount
        if (this.currentTestSentenceVal + 2 < 4) this.currentTestSentenceVal = 4;
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

    public void onStopShow() {}

    public void onShow() {
        //always refresh this
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        if (this.getDataModel().isNeedRefresh()) {
            //unset it
            this.getDataModel().setNeedRefresh(false);

            if (this.getDataModel().isReadStarted()) {
                return;
            }

            refreshData();
        }
    }

}
