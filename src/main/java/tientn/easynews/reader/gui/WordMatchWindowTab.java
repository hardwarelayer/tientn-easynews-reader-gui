package tientn.easynews.reader.gui;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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

import tientn.easynews.reader.gui.base.SimpleStackedFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.JBGConstants;

// SimpleFormBase derived class
public class WordMatchWindowTab extends SimpleStackedFormBase {

    private List<JBGKanjiItem> kanjiList;

    private Label lblFormTitle;

    private Label lblFirstCol;
    private Label lblSecondCol;
    private Label lblThirdCol;
    private Label lblFourthCol;

    private Label lblFirstColSneekpeek;
    private Label lblSecondColSneekpeek;
    private Label lblThirdColSneekpeek;
    private Label lblFourthColSneekpeek;

    private Label lblTotalStats;
    private Label lblTotalTests;
    private Label lblTestStatus;
    private Label lblJCoinAmount;
    private Label lblProblematicWord;

    private ListView<String> lvFirstCol;
    private ListView<String> lvSecondCol;
    private ListView<String> lvThirdCol;
    private ListView<String> lvFourthCol;

    private Button btnReloadKanjis;

    private Button btnLoadNormalForTest;
    private Button btnLoadNewForTest;
    private Button btnLoadProblematicWordsForTest;
    private Button btnStartTest;
    private Button btnStopTest;

    private final String sWordMatchEmptyValue = "...";
    private static final String MATCH_WORD_OK = "OK";
    private static final String MATCH_WORD_NG = "NG";
    private static final int BONUS_ON_COMPLETE = 5;

    private int iCurrentTestKJCount = 0;

    List<String> lstProblematicWords;

    char[] arrA_JPChars_ForSearch = new char[] {'あ', 'ア'};
    char[] arrB_JPChars_ForSearch = new char[] {'ば', 'び', 'ぶ', 'べ', 'ぼ', 'バ', 'ビ', 'ブ', 'ベ', 'ボ'};
    char[] arrC_JPChars_ForSearch = new char[] {'ち', 'チ'};
    char[] arrD_JPChars_ForSearch = new char[] {'だ', 'ぢ', 'づ', 'で', 'ど', 'ダ', 'ヂ', 'ヅ', 'デ', 'ド'};
    char[] arrE_JPChars_ForSearch = new char[] {'え', 'エ'};
    char[] arrF_JPChars_ForSearch = new char[] {'ふ', 'フ'};
    char[] arrG_JPChars_ForSearch = new char[] {'が', 'ぎ', 'ぐ', 'げ', 'ご', 'ガ', 'ギ', 'グ', 'ゲ', 'ゴ'};
    char[] arrH_JPChars_ForSearch = new char[] {'は', 'ひ', 'ふ', 'へ', 'ほ', 'ハ', 'ヒ', 'フ', 'ヘ', 'ホ'};
    char[] arrI_JPChars_ForSearch = new char[] {'い', 'イ'};
    char[] arrJ_JPChars_ForSearch = new char[] {'じ', 'ジ'};
    char[] arrK_JPChars_ForSearch = new char[] {'か', 'き', 'く', 'け', 'こ', 'カ', 'キ', 'ク', 'ケ', 'コ'};
    char[] arrM_JPChars_ForSearch = new char[] {'ま', 'み', 'む', 'め', 'も', 'マ', 'ミ', 'ム', 'メ', 'モ'};
    char[] arrN_JPChars_ForSearch = new char[] {'な', 'に', 'ぬ', 'ね', 'の', 'ナ', '二', 'ヌ', 'ネ', 'ノ'};
    char[] arrO_JPChars_ForSearch = new char[] {'お', 'オ'};
    char[] arrP_JPChars_ForSearch = new char[] {'ぱ', 'ぴ', 'ぷ', 'ぺ', 'ぽ', 'パ', 'ピ', 'プ', 'ペ', 'ポ'};
    char[] arrR_JPChars_ForSearch = new char[] {'ら', 'り', 'る', 'れ', 'ろ', 'ラ', 'リ', 'ル', 'レ', 'ロ'};
    char[] arrS_JPChars_ForSearch = new char[] {'さ', 'し', 'す', 'せ', 'そ', 'サ', 'シ', 'ス', 'セ', 'ソ'};
    char[] arrT_JPChars_ForSearch = new char[] {'た', 'ち', 'つ', 'て', 'と', 'タ', 'チ', 'ツ', 'テ', 'ト'};
    char[] arrU_JPChars_ForSearch = new char[] {'う', 'ウ'};
    char[] arrW_JPChars_ForSearch = new char[] {'わ', 'ワ'};
    char[] arrY_JPChars_ForSearch = new char[] {'や', 'ゆ', 'よ', 'ヤ', 'ユ', 'ヨ'};
    char[] arrZ_JPChars_ForSearch = new char[] {'ざ', 'じ', 'ず', 'ぜ', 'ぞ', 'ザ', 'ジ', 'ズ', 'ゼ', 'ゾ'};

    char[] arrA_VNChars_ForSearch = new char[] {'a', 'á', 'à', 'ả', 'ã', 'ạ', 'A', 'Á', 'À', 'Ả', 'Ã', 'Ạ'};
    char[] arrE_VNChars_ForSearch = new char[] {'e', 'é', 'è', 'ẻ', 'ẽ', 'ẹ', 'E', 'É', 'È', 'Ẻ', 'Ẽ', 'Ẹ'};
    char[] arrI_VNChars_ForSearch = new char[] {'i', 'í', 'ì', 'ỉ', 'ĩ', 'ị', 'I', 'Í', 'Ì', 'Ỉ', 'Ĩ', 'Ị'};
    char[] arrO_VNChars_ForSearch = new char[] {'o', 'ó', 'ò', 'ỏ', 'õ', 'ọ', 'O', 'Ó', 'Ò', 'Ỏ', 'Õ', 'Ọ'};
    char[] arrU_VNChars_ForSearch = new char[] {'u', 'ú', 'ù', 'ủ', 'ũ', 'ụ', 'U', 'Ú', 'Ù', 'Ủ', 'Ũ', 'Ụ', 'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự', 'Ư', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự'};
    char[] arrY_VNChars_ForSearch = new char[] {'y', 'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ', 'Y', 'Ý', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ'};
    char[] arrD_VNChars_ForSearch = new char[] {'d', 'đ', 'D', 'Đ'};

    public WordMatchWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);
        lstProblematicWords = new ArrayList<String>();

        this.getTopBodyLabel().setId("wordmatch-top-kanji-label");
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();
    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("WordMatch Test");
        this.addHeaderText(txtFormTitle, 0, 0);
    }

    private void createBodyElements() {

        this.addBodyColumn(17);
        this.addBodyColumn(20);
        this.addBodyColumn(31);
        this.addBodyColumn(31);

        Label lblLoaded = new Label("Total Kanjis");
        lblTotalStats = createLabel("0/0");
        Label lblTest = new Label("Total Test:");
        lblTotalTests = createLabel("0");
        lblTestStatus = createLabel("Waiting ...");
        lblJCoinAmount = createLabel("0");
        lblJCoinAmount.setId("wordmatch-coin-amount");
        lblProblematicWord = createLabel("0");

        lblFirstCol = createLabel(sWordMatchEmptyValue);
        lblSecondCol = createLabel(sWordMatchEmptyValue);
        lblThirdCol = createLabel(sWordMatchEmptyValue);
        lblFourthCol = createLabel(sWordMatchEmptyValue);

        lblFirstColSneekpeek = createLabel(sWordMatchEmptyValue);
        lblSecondColSneekpeek = createLabel(sWordMatchEmptyValue);
        lblThirdColSneekpeek = createLabel(sWordMatchEmptyValue);
        lblFourthColSneekpeek = createLabel(sWordMatchEmptyValue);

        lblFirstCol.setId("wordmatch-kanji-test");
        lblSecondCol.setId("wordmatch-hiragana-test");
        lblThirdCol.setId("wordmatch-hv-test");
        lblFourthCol.setId("wordmatch-viet-test");

        lblFirstColSneekpeek.setId("wordmatch-kanji-sneekpeek");
        lblSecondColSneekpeek.setId("wordmatch-hiragana-sneekpeek");
        lblThirdColSneekpeek.setId("wordmatch-hv-sneekpeek");
        lblFourthColSneekpeek.setId("wordmatch-viet-sneekpeek");

        EventHandler<ActionEvent> fncRefreshButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                refreshKanjiStats();
            }
        };
        btnReloadKanjis = createButton("Refresh", fncRefreshButtonClick);

        EventHandler<ActionEvent> fncLoadNormalButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadNormalKanjisForTest();
            }
        };
        btnLoadNormalForTest = createButton("(L)oad Normal", fncLoadNormalButtonClick);

        EventHandler<ActionEvent> fncLoadNewButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadNewKanjisForTest();
            }
        };
        btnLoadNewForTest = createButton("Load (N)ew ", fncLoadNewButtonClick);

        EventHandler<ActionEvent> fncLoadWrongButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadProblematicWordsForTest();
            }
        };
        btnLoadProblematicWordsForTest = createButton("Load (P)roblematic", fncLoadWrongButtonClick);

        EventHandler<ActionEvent> fncStartTestButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                startTest();
            }
        };
        btnStartTest = createButton("(S)tart Test", fncStartTestButtonClick);
        EventHandler<ActionEvent> fncStopTestButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                stopTest();
            }
        };
        btnStopTest = createButton("Stop Test", fncStopTestButtonClick);

        lvFirstCol = createSingleSelectStringListView(0);
        lvSecondCol = createSingleSelectStringListView(1);
        lvThirdCol = createSingleSelectStringListView(2);
        lvFourthCol = createSingleSelectStringListView(3);

        lvFirstCol.setId("kanji-column");
        lvSecondCol.setId("hiragana-column");
        lvThirdCol.setId("hv-column");
        lvFourthCol.setId("meaning-column");

        lvFirstCol.getStyleClass().add("wordmatch-scroll-bar");
        lvSecondCol.getStyleClass().add("wordmatch-scroll-bar");
        lvThirdCol.getStyleClass().add("wordmatch-scroll-bar");
        lvFourthCol.getStyleClass().add("wordmatch-scroll-bar");

        lvFirstCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.72));
        lvSecondCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.72));
        lvThirdCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.72));
        lvFourthCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.72));

        this.addBodyCtl(lblTestStatus, 0, 0);
        this.addBodyCtl(btnReloadKanjis, 1, 0);
        this.addBodyPane(new HBox(new Label("Current JCoin:"), lblJCoinAmount), 2, 0);
        this.addBodyPane(new HBox(new Label("Problematic Words:"), lblProblematicWord), 3, 0);

        this.addBodyCtl(lblLoaded, 0, 1);
        this.addBodyCtl(lblTotalStats, 1, 1);
        this.addBodyPane(new HBox(lblTest, lblTotalTests), 2, 1);
        //this.addBodyCtl(lblTotalTests, 3, 1);

        this.addBodyCtl(btnLoadNormalForTest, 0, 2);
        this.addBodyCtl(btnLoadNewForTest, 1, 2);
        this.addBodyCtl(btnLoadProblematicWordsForTest, 2, 2);
        HBox bxStartStop = new HBox(btnStartTest, btnStopTest);
        this.addBodyPane(bxStartStop, 3, 2);

        this.addBodyCtl(lblFirstCol, 0, 3);
        this.addBodyCtl(lblSecondCol, 1, 3);
        this.addBodyCtl(lblThirdCol, 2, 3);
        this.addBodyCtl(lblFourthCol, 3, 3);

        this.addBodyCtl(lvFirstCol, 0, 4);
        this.addBodyCtl(lvSecondCol, 1, 4);
        this.addBodyCtl(lvThirdCol, 2, 4);
        this.addBodyCtl(lvFourthCol, 3, 4);

        this.addBodyCtl(lblFirstColSneekpeek, 0, 5);
        this.addBodyCtl(lblSecondColSneekpeek, 1, 5);
        this.addBodyCtl(lblThirdColSneekpeek, 2, 5);
        this.addBodyCtl(lblFourthColSneekpeek, 3, 5);

        //this.setHalignment(lblFirstColSneekpeek, HPos.CENTER);

    }

    private void refreshKanjiStats() {
        if (this.getDataModel().isTestStarted()) return;

        clearLists();
        if (this.getDataModel().getCurrentWorkMode() == JBGConstants.TEST_WORD_IN_MAJOR_LIST) {
            lblTestStatus.setText("Global Kanjis Mode");
        }
        else if (this.getDataModel().getCurrentWorkMode() == JBGConstants.TEST_WORD_IN_ARTICLE) {
            this.lstProblematicWords = new ArrayList<String>(this.getDataModel().getSelectedTNA().getProblematicWords());
            lblTestStatus.setText("Article Kanjis Mode");
        }

        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        lblProblematicWord.setText(String.valueOf(this.lstProblematicWords.size()));

        List<JBGKanjiItem> lstKj = this.getDataModel().getDataKanjiItems();
        int iTotalKanjis = lstKj.size();
        int iTotalTests = 0;
        int iTotalCorrect = 0;
        for (int i = 0; i < iTotalKanjis; i++) {
              JBGKanjiItem item = lstKj.get(i);
              if (item.getCorrectCount() >= JBGConstants.KANJI_MIN_TEST_CORRECT) {
                iTotalCorrect++;
              }
              iTotalTests += item.getTestCount();
        }
        StringBuilder sb = new StringBuilder(
            String.valueOf(iTotalCorrect) +
            "/" +
            String.valueOf(iTotalKanjis)
            );
        lblTotalStats.setText(sb.toString());
        lblTotalTests.setText(String.valueOf(iTotalTests));
        if (iTotalKanjis < 1) {
            btnStartTest.setDisable(true);
            btnStopTest.setDisable(true);
            btnLoadNewForTest.setDisable(true);
            btnLoadProblematicWordsForTest.setDisable(true);
            btnLoadNormalForTest.setDisable(true);
        }
        else {
            btnStartTest.setDisable(false);
            btnStopTest.setDisable(true);
            btnLoadNewForTest.setDisable(false);
            btnLoadProblematicWordsForTest.setDisable(false);
            btnLoadNormalForTest.setDisable(false);
        }

        //btnReloadKanjis.setDisable(true); no need to disable
    }

    private void clearLists() {
        lvFirstCol.getItems().clear();
        lvSecondCol.getItems().clear();
        lvThirdCol.getItems().clear();
        lvFourthCol.getItems().clear();
    }

    private void fillItemToLists(JBGKanjiItem item) {
        lvFirstCol.getItems().add(item.getKanji());
        lvSecondCol.getItems().add(item.getHiragana());
        lvThirdCol.getItems().add(item.getHv());
        lvFourthCol.getItems().add(item.getMeaning());
    }

    private void loadNormalKanjisForTest() {
        if (this.getDataModel().isTestStarted())
          return;

        List<JBGKanjiItem> lstKJ = this.getDataModel().getNormalKJSubset();
        this.iCurrentTestKJCount = lstKJ.size();

        if (lstKJ != null && lstKJ.size() > 0) {
            this.kanjiList = lstKJ;
            clearLists();
            for (int i = 0; i < lstKJ.size(); i++) {
                JBGKanjiItem item = lstKJ.get(i);
                fillItemToLists(item);
            }
        }

        refreshStartButton();
    }

    private void loadNewKanjisForTest() {
        if (this.getDataModel().isTestStarted())
          return;

        List<JBGKanjiItem> lstKJ = this.getDataModel().getNewKJSubset();
        this.iCurrentTestKJCount = lstKJ.size();

        if (lstKJ != null && lstKJ.size() > 0) {
            this.kanjiList = lstKJ;
            clearLists();
            for (int i = 0; i < lstKJ.size(); i++) {
                JBGKanjiItem item = lstKJ.get(i);
                fillItemToLists(item);
            }
        }

        refreshStartButton();
    }

    private void loadProblematicWordsForTest() {
        if (this.getDataModel().isTestStarted())
          return;

        if (this.lstProblematicWords.size() < 1) {
            System.out.println("no problematic word!");
            return;
        }

        List<JBGKanjiItem> lstKJ = this.getDataModel().getSpecificKJSubset(this.lstProblematicWords);
        this.iCurrentTestKJCount = lstKJ.size();

        if (lstKJ != null && lstKJ.size() > 0) {
            clearLists();
            for (int i = 0; i < lstKJ.size(); i++) {
                JBGKanjiItem item = lstKJ.get(i);
                fillItemToLists(item);
            }
        }

        refreshStartButton();
    }

    private void startTest() {
        boolean isStarted = this.getDataModel().isTestStarted();
        if (isStarted) {
            return;
        }
        else {
            doStartGame();
        }
    }

    private void stopTest() {
        boolean isStarted = this.getDataModel().isTestStarted();
        if (!isStarted) {
            return;
        }
        else {
            doEndGame();
        }
    }

    private int calculateBonusAmount() {
        if (this.iCurrentTestKJCount < BONUS_ON_COMPLETE)
            return BONUS_ON_COMPLETE;
        int iMultiply = (int) this.iCurrentTestKJCount / BONUS_ON_COMPLETE;
        if (iMultiply < 1) iMultiply = 1;
        return BONUS_ON_COMPLETE + iMultiply; 
    }

    //event
    private void processColumnButtonClick(final int iCol) {

        String sItem = null;
        ListView lv = null;

        switch(iCol) {
        case 0:
            lv = lvFirstCol;
            break;
        case 1:
            lv = lvSecondCol;
            break;
        case 2:
            lv = lvThirdCol;
            break;
        case 3:
            lv = lvFourthCol;
            break;
        }

        if (lv != null) {
            sItem = getListSelectedString(lv);
            lblFirstCol.setText(sItem);
        }
    }

    private void processColumnListViewKeyEvent(final KeyCode kc, final boolean isShiftDown ) {
        String sItem = null;
        ListView lv = null;

        boolean isDigitKey = false;
        boolean isLetterKey = false;

        if (kc.toString().length() == 1) {
            if (Character.isDigit(kc.toString().charAt(0))) isDigitKey = true;
            if (Character.isLetter(kc.toString().charAt(0))) isLetterKey = true;
        }

        if (!this.getDataModel().isTestStarted()) {
            return;
        }

        if (lvFirstCol.isFocused()) {
            lv = lvFirstCol;            
        }
        else if (lvSecondCol.isFocused()) {
            lv = lvSecondCol;            
        }
        else if (lvThirdCol.isFocused()) {
            lv = lvThirdCol;            
        }
        else if (lvFourthCol.isFocused()) {
            lv = lvFourthCol;            
        }

        if (lv != null) {
            if (kc == KeyCode.ENTER) {
                sItem = getListSelectedString(lv);
                if (sItem == null) return;

                if (lvFirstCol.isFocused()) {
                    if (!sItem.isEmpty()) {
                        lblFirstCol.setText(sItem);
                        this.getTopBodyLabel().setText(sItem);
                        chooseHiraganaList();
                    }
                }
                else if (lvSecondCol.isFocused()) {
                    if (!sItem.isEmpty()) {
                        lblSecondCol.setText(sItem);
                        chooseHanVietList();
                    }
                }
                else if (lvThirdCol.isFocused()) {
                    if (!sItem.isEmpty()) {
                        lblThirdCol.setText(sItem);
                        chooseMeaningList();
                    }
                }
                else if (lvFourthCol.isFocused()) {
                    if (lblFourthCol.getText() != sItem) {
                        lblFourthCol.setText(sItem);
                    }
                    else {
                        //double ENTER on fourth list
                        //submit row value
                        validateKanjiSelection(isShiftDown);
                    }
                }
            }
            else if (kc == KeyCode.SLASH && isShiftDown) {
                // character / with Shift is ?
                if (lvFirstCol.isFocused()) {
                    sItem = getListSelectedString(lv);
                    showSneakpeek(sItem);
                }
            }
            else if (kc == KeyCode.DELETE) {
                final int selectedIdx = lv.getSelectionModel().getSelectedIndex();
                if (selectedIdx >= 0) {
                    lv.getItems().remove(selectedIdx);
                }
            }
            else if (isLetterKey) {
                if (lvSecondCol.isFocused()) {
                    processHiraganaSearchKeyEvents(kc, isShiftDown, true);
                }
                else if (lvThirdCol.isFocused()) {
                    processVietnamSearchKeyEvents(lvThirdCol, kc, isShiftDown, true);
                }
                else if (lvFourthCol.isFocused()) {
                    processVietnamSearchKeyEvents(lvFourthCol, kc, isShiftDown, true);
                }
            }
        }

    }

    private boolean isCharInArray(char c, char[] arr) {
        boolean contains = false;
        for (char chr: arr) {
            if (chr == c) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private boolean matchHiraKanaFirstChar(final KeyCode kc, final String sItem) {
        char chrFirst = sItem.charAt(0);
        //System.out.println("Comparing "+kc.toString() + " " + Character.toString(chrFirst));
        switch (kc) {
            case A:
                return isCharInArray(chrFirst, arrA_JPChars_ForSearch);
            case B:
                return isCharInArray(chrFirst, arrB_JPChars_ForSearch);
            case C:
                return isCharInArray(chrFirst, arrC_JPChars_ForSearch);
            case D:
                return isCharInArray(chrFirst, arrD_JPChars_ForSearch);
            case E:
                return isCharInArray(chrFirst, arrE_JPChars_ForSearch);
            case F:
                return isCharInArray(chrFirst, arrF_JPChars_ForSearch);
            case G:
                return isCharInArray(chrFirst, arrG_JPChars_ForSearch);
            case H:
                return isCharInArray(chrFirst, arrH_JPChars_ForSearch);
            case I:
                return isCharInArray(chrFirst, arrI_JPChars_ForSearch);
            case J:
                return isCharInArray(chrFirst, arrJ_JPChars_ForSearch);
            case K:
                return isCharInArray(chrFirst, arrK_JPChars_ForSearch);
            case M:
                return isCharInArray(chrFirst, arrM_JPChars_ForSearch);
            case N:
                return isCharInArray(chrFirst, arrN_JPChars_ForSearch);
            case O:
                return isCharInArray(chrFirst, arrO_JPChars_ForSearch);
            case P:
                return isCharInArray(chrFirst, arrP_JPChars_ForSearch);
            case R:
                return isCharInArray(chrFirst, arrR_JPChars_ForSearch);
            case S:
                return isCharInArray(chrFirst, arrS_JPChars_ForSearch);
            case T:
                return isCharInArray(chrFirst, arrT_JPChars_ForSearch);
            case U:
                return isCharInArray(chrFirst, arrU_JPChars_ForSearch);
            case W:
                return isCharInArray(chrFirst, arrW_JPChars_ForSearch);
            case Y:
                return isCharInArray(chrFirst, arrY_JPChars_ForSearch);
            case Z:
                return isCharInArray(chrFirst, arrZ_JPChars_ForSearch);
        }
        return false;
    }

    private boolean matchVietnamFirstChar(final KeyCode kc, final String sItem) {
        char chrFirst = sItem.charAt(0);
        //System.out.println("Comparing "+kc.toString() + " " + Character.toString(chrFirst));
        switch (kc) {
            case A:
                return isCharInArray(chrFirst, arrA_VNChars_ForSearch);
            case E:
                return isCharInArray(chrFirst, arrE_VNChars_ForSearch);
            case I:
                return isCharInArray(chrFirst, arrI_VNChars_ForSearch);
            case O:
                return isCharInArray(chrFirst, arrO_VNChars_ForSearch);
            case U:
                return isCharInArray(chrFirst, arrU_VNChars_ForSearch);
            case Y:
                return isCharInArray(chrFirst, arrY_VNChars_ForSearch);
            case D:
                return isCharInArray(chrFirst, arrD_VNChars_ForSearch);
            default:
                //System.out.println("Default comparing " + Character.toString(kc.toString().toLowerCase().charAt(0)) + " " + Character.toString(chrFirst));
                if (kc.toString().toLowerCase().charAt(0) == chrFirst) return true;
        }
        return false;
    }

    private void processHiraganaSearchKeyEvents(final KeyCode kc, final boolean isShiftDown, final boolean recursive) {
        int iStartPos = 0;
        int iSelPos = lvSecondCol.getSelectionModel().getSelectedIndex();
        if (iSelPos > 0) iStartPos = iSelPos;

        System.out.println("key on hira list: " + kc.toString());

        int iHiraIdx = 0;
        ObservableList<String> hiraItems = lvSecondCol.getItems();
        for (String hiraItem: hiraItems) {
            if (iHiraIdx++ <= iStartPos) continue;
            if (matchHiraKanaFirstChar(kc, hiraItem)) {
                lvSecondCol.scrollTo(iHiraIdx - 1);
                lvSecondCol.getSelectionModel().select(iHiraIdx - 1);
                return;
            }
        }

        if (iStartPos > 0 && recursive) {
            //still not found, re-search from 0
            lvSecondCol.scrollTo(0);
            lvSecondCol.getSelectionModel().select(0);
            processHiraganaSearchKeyEvents(kc, isShiftDown, false /*no recursive*/);
        }
    }

    private void processVietnamSearchKeyEvents(ListView lst, final KeyCode kc, final boolean isShiftDown, final boolean recursive) {
        int iStartPos = 0;
        int iSelPos = lst.getSelectionModel().getSelectedIndex();
        if (iSelPos > 0) iStartPos = iSelPos;

        System.out.println("key on vietnamese list: " + kc.toString());

        int iItemIdx = 0;
        ObservableList<String> vnItems = lst.getItems();
        for (String vnItem: vnItems) {
            if (iItemIdx++ <= iStartPos) continue;
            if (matchVietnamFirstChar(kc, vnItem)) {
                lst.scrollTo(iItemIdx - 1);
                lst.getSelectionModel().select(iItemIdx - 1);
                return;
            }
        }

        if (iStartPos > 0 && recursive) {
            //still not found, re-search from 0
            lst.scrollTo(0);
            lst.getSelectionModel().select(0);
            processVietnamSearchKeyEvents(lst, kc, isShiftDown, false /*no recursive*/);
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
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode();

        if (!this.getDataModel().isTestStarted()) {
            switch (kc) {
                case S:
                    doStartGame();
                    break;
                case L:
                    loadNormalKanjisForTest();
                    break;
                case N:
                    loadNewKanjisForTest();
                    break;
                case P:
                    loadProblematicWordsForTest();
                    break;
                case V:
                    this.getDataModel().printCurrentKanjisWithTest();
                    break;
            }
        }
        else {
        switch (kc) {
                case DIGIT1:
                    chooseKanjiList();
                    break;
                case DIGIT2:
                    chooseHiraganaList();
                    break;
                case DIGIT3:
                    chooseHanVietList();
                    break;
                case DIGIT4:
                    chooseMeaningList();
                    break;
                default:
                   this.processColumnListViewKeyEvent(kc, ke.isShiftDown());
                   break;
            }
        }


    }

    private ListView createSingleSelectStringListView(final int col) {
        ListView<String> listView = new ListView<>();

        if (this.getDataModel() == null) return listView;

        for (int i = 0; i < this.getDataModel().getDataKanjiItems().size(); i++) {
            JBGKanjiItem item = this.getDataModel().getDataKanjiItems().get(i);
            switch (col) {
            case 0:
                listView.getItems().add(item.getKanji());
                break;
            case 1:
                listView.getItems().add(item.getHiragana());
                break;
            case 2:
                listView.getItems().add(item.getHv());
                break;
            case 3:
                listView.getItems().add(item.getMeaning());
                break;
            }
        }

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return listView;
    }

    private String getListSelectedString(ListView lst) {
        String sSelected = (String) lst.getSelectionModel().getSelectedItem();
        if (sSelected != null) {
            System.out.println(sSelected);
        }
        return sSelected;
    }

    public void onShow() {
        if (this.getDataModel().isNeedRefresh()) {

            //unset it
            this.getDataModel().setNeedRefresh(false);

            //System.out.println("data is dirty");
            if (this.getDataModel().isTestStarted()) return;
            btnReloadKanjis.setDisable(false);
            refreshKanjiStats();
            loadNewKanjisForTest();
        }
        else {
            //System.out.println("data is NOT dirty");
            btnReloadKanjis.setDisable(true);
        }
        List<JBGKanjiItem> lstKj = this.getDataModel().getDataKanjiItems();
        if (lstKj.size() < 1) {
            btnStartTest.setDisable(true);
            btnStopTest.setDisable(true);
            btnLoadNewForTest.setDisable(true);
            btnLoadProblematicWordsForTest.setDisable(true);
            btnLoadNormalForTest.setDisable(true);
        }
    }

    private boolean updateWordStat(final String kanji, final String isOK) {
          for (JBGKanjiItem item: this.kanjiList) {
            if (item.getKanji().equals(kanji) ) {
              item.increaseTest(isOK.equals(MATCH_WORD_OK)?true:false);
              return true;
            }
          }
          return false;
    }

    private void clearWordListSelection() {
        lblFirstCol.setText(sWordMatchEmptyValue);
        lblSecondCol.setText(sWordMatchEmptyValue);
        lblThirdCol.setText(sWordMatchEmptyValue);
        lblFourthCol.setText(sWordMatchEmptyValue);

        lvFirstCol.getSelectionModel().clearSelection();
        lvSecondCol.getSelectionModel().clearSelection();
        lvThirdCol.getSelectionModel().clearSelection();
        lvFourthCol.getSelectionModel().clearSelection();

        this.getTopBodyLabel().setText(sWordMatchEmptyValue);
    }

    private void refreshStartButton() {
        if (lvFirstCol.getItems().size() < 1) {
            btnStartTest.setDisable(true);
            btnStopTest.setDisable(false);
            return;
        }
        btnStartTest.setDisable(false);
        btnStopTest.setDisable(true);
    }

    private void doStartGame() {
        if (this.getDataModel().isTestStarted())
          return;

        if (lvFirstCol.getItems().size() < 1) return;

        lblTestStatus.setText("Started!");

        shuffleAllListModels();
        btnLoadNormalForTest.setDisable(true);
        btnLoadNewForTest.setDisable(true);
        btnLoadProblematicWordsForTest.setDisable(true);
        btnStartTest.setDisable(true);
        btnStopTest.setDisable(false);
        clearWordListSelection();

        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        chooseKanjiList();

        this.getDataModel().setTestStarted(true);
    }

    private void doEndGame() {
        if (!this.getDataModel().isTestStarted())
          return;

        btnLoadNormalForTest.setDisable(false);
        btnLoadNewForTest.setDisable(false);
        btnLoadProblematicWordsForTest.setDisable(false);
        btnStartTest.setDisable(true);
        btnStopTest.setDisable(true);
        clearWordListSelection();

        this.getDataModel().setTestStarted(false);
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

    private String[] getContentOfKanjiWord(final String kanji) {
        String[] res = new String[]{"", "", "", ""};

        for (JBGKanjiItem item: this.kanjiList) {
          if (kanji.equals(item.getKanji())) {
            res[0] = item.getKanji();
            res[1] = item.getHiragana();
            res[2] = item.getHv();
            res[3] = item.getMeaning();
            return res;
          }
        }
        return res;
    }

    public void chooseKanjiList() {
        lvFirstCol.getSelectionModel().select(0);
        lvFirstCol.requestFocus();
    }

    public void chooseHiraganaList() {
        lvSecondCol.getSelectionModel().select(0);
        lvSecondCol.requestFocus();
    }

    public void chooseHanVietList() {
        lvThirdCol.getSelectionModel().select(0);
        lvThirdCol.requestFocus();
    }

    public void chooseMeaningList() {
        lvFourthCol.getSelectionModel().select(0);
        lvFourthCol.requestFocus();
    }

    public void showSneakpeek(final String kanjiWord) {
        if (!this.getDataModel().isTestStarted()) return;
        final String[] contentRes = getContentOfKanjiWord(kanjiWord); 
        if (contentRes[0].length() < 1) {
            return;
        }

        setSneekpeekFields(contentRes[0], contentRes[1], contentRes[2], contentRes[3]);

        this.getDataModel().decreaseJCoin(1); //set back to parent
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        noteWord(kanjiWord);

    }

    private void setSneekpeekFields(final String kanji, final String hira, final String hv, final String meaning) {
        lblFirstColSneekpeek.setText(kanji);
        lblSecondColSneekpeek.setText(hira);
        lblThirdColSneekpeek.setText(hv);
        lblFourthColSneekpeek.setText(meaning);
    }

    private String[] isSelectedWordMatched(final String kanji, final String hira, final String hv, final String meaning) {
        String[] res = new String[]{kanji, MATCH_WORD_NG};
        for (JBGKanjiItem item: this.kanjiList) {
          if (kanji.equals(item.getKanji()) &&
              hira.equals(item.getHiragana()) &&
              hv.equals(item.getHv()) && 
              meaning.equals(item.getMeaning())
              ) {
                res[0] = item.getKanji();
                res[1] = MATCH_WORD_OK;
                break;
              }
        }
        return res;
    }

    private void noteWord(final String kanjiWord) {
        if (!this.lstProblematicWords.contains(kanjiWord))
          this.lstProblematicWords.add(kanjiWord);

        if (this.getDataModel().getCurrentWorkMode() == JBGConstants.TEST_WORD_IN_ARTICLE) {
            TFMTTNAData currentTNA = this.getDataModel().getSelectedTNA();
            if (!currentTNA.getProblematicWords().contains(kanjiWord)) {
                currentTNA.getProblematicWords().add(kanjiWord);
            }
        }

        lblProblematicWord.setText(String.valueOf(this.lstProblematicWords.size()));

    }

    private void unnoteWord(final String kanjiWord) {
        if (this.lstProblematicWords.contains(kanjiWord))
          this.lstProblematicWords.remove(kanjiWord);
        if (this.getDataModel().getCurrentWorkMode() == JBGConstants.TEST_WORD_IN_ARTICLE) {
            TFMTTNAData currentTNA = this.getDataModel().getSelectedTNA();
            if (!currentTNA.getProblematicWords().contains(kanjiWord)) {
                currentTNA.getProblematicWords().remove(kanjiWord);
            }
        }

        lblProblematicWord.setText(String.valueOf(this.lstProblematicWords.size()));
    }

    public boolean validateKanjiSelection(final boolean isShiftDown) {
        boolean allFieldSet = true;

        final int iKanjiSel = lvFirstCol.getSelectionModel().getSelectedIndex();
        final int iHiraSel = lvSecondCol.getSelectionModel().getSelectedIndex();
        final int iHvSel = lvThirdCol.getSelectionModel().getSelectedIndex();
        final int iVnSel = lvFourthCol.getSelectionModel().getSelectedIndex();

        if (iKanjiSel == -1) {
          allFieldSet = false;
        }
        if (iHiraSel == -1) {
          allFieldSet = false;
        }
        if (iHvSel == -1) {
          allFieldSet = false;
        }
        if (iVnSel == -1) {
          allFieldSet = false;
        }

        int totalJCoin = this.getDataModel().getJCoin();

        if (allFieldSet) {

          final String kanji = lblFirstCol.getText();
          final String hira = lblSecondCol.getText();
          final String hv = lblThirdCol.getText();
          final String viet = lblFourthCol.getText();

          if (kanji.length() == 0) allFieldSet = false;
          if (hira.length() == 0) allFieldSet = false;
          if (hv.length() == 0) allFieldSet = false;
          if (viet.length() == 0) allFieldSet = false;

          if (allFieldSet) {

            final String[] matchRes = isSelectedWordMatched(kanji, hira, hv, viet);
            if (matchRes[1].equals(MATCH_WORD_OK) && matchRes[0].length() > 0) {

              //reward
              totalJCoin++;

              setSneekpeekFields(kanji, hira, hv, viet);

              if (isShiftDown) {
                //normally, we call noteWord on wrongly selected row
                //but if Shift+ENTER and the selected row is correct, we still note it
                //so the tester can also note this word for later test with "Wrong List"
                noteWord(kanji);
              }

              //remove the correct matched word set from lists
              int iRemainingItems = removeItemFromList(lvFirstCol, kanji);
              removeItemFromList(lvSecondCol, hira);
              removeItemFromList(lvThirdCol, hv);
              removeItemFromList(lvFourthCol, viet);

              if (iRemainingItems > 0) {
                //select first list to start a new word select flow
                chooseKanjiList();
              }
              else {
                //no more word
                doEndGame();
                totalJCoin += calculateBonusAmount();
              }
            }
            else {
              //not correct!
              if (totalJCoin > 0) totalJCoin--;
              noteWord(kanji);
              chooseKanjiList();
            }
            //update the statistic of word
            if (!updateWordStat(matchRes[0], matchRes[1])) {
              //can't update
              System.out.println("cannot update work of" + matchRes[0]);
            }
            else {
              System.out.println("Updated work of" + matchRes[0]);
            }
            this.getDataModel().setJCoin(totalJCoin); //set back to parent
            lblJCoinAmount.setText(String.valueOf(totalJCoin));
            clearWordListSelection();

          }

        }

        return allFieldSet;
    }

    private ObservableList<String> shuffleListModel(ObservableList<String> mdl) {
        for(int i=0;i<mdl.size();i++){
            int swapWith = (int)(Math.random()*(mdl.size()-i))+i;
            if(swapWith==i) continue;
            mdl.add(i, mdl.remove(swapWith));
            mdl.add(swapWith, mdl.remove(i+1));
        }
        return mdl;
    }

    public void shuffleAllListModels() {
        shuffleListModel( (ObservableList<String>) lvFirstCol.getItems() );
        shuffleListModel( (ObservableList<String>) lvSecondCol.getItems() );
        shuffleListModel( (ObservableList<String>) lvThirdCol.getItems() );
        shuffleListModel( (ObservableList<String>) lvFourthCol.getItems() );
    }


}