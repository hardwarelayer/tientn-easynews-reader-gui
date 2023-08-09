package tientn.easynews.reader.gui;

import java.awt.Desktop;
import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
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
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import tientn.easynews.reader.gui.base.SimpleFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.JBGConstants;
import tientn.easynews.reader.data.TFMTWorkData;
import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.TFMTTNASentenceData;
import tientn.easynews.reader.data.TFMTTNAKanjiData;
import tientn.easynews.reader.data.TFMTTNAKanjiDetailData;
import tientn.easynews.reader.data.ArticleWordBuildTableViewItem;

// SimpleFormBase derived class
public class ArticleWordBuildWindowTab extends SimpleFormBase {

    private MainTabbedPane parentPane;

    private Label lblArticleTitle;
    private Label lblJCoinAmount;
    private Label lblTNAWordCount;
    private TextArea tafArticleJAContent;
    private TextArea tafArticleENContent;
    private TextArea tafKanjiMeaning;

    private TextField tfSelectedWord;
    private TextField tfSelectedHiragana;
    private TextField tfHv;
    private TextField tfMeaning;

    private ComboBox cbSelectedWord;
    private Button btnLoadArticle;
    private Button btnBuildSelectedWord;
    private Button btnStartWordMatchTest;
    private Button btnStartArticleRead;

    int WORD_SCORE_MULTIPLIER = JBGConstants.JCOIN_AMOUNT_FOR_ADD_KANJI_WORD;

    private ListView<String> lvTNASentences;
    private ListView<String> lvTNAKanjiWords;
    private ListView<String> lvTNADetailKanjis;
    TableView<ArticleWordBuildTableViewItem> tvBuiltWords;

    private TFMTTNAData currentTNA = null;
    private boolean isTNAFound = false;
    private TFMTTNAKanjiData currentTNAKanji = null;

    private List<JBGKanjiItem> currentBuildWordKanjiSimilarList = null;

    public ArticleWordBuildWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model, MainTabbedPane parent) {
        super(width, height, desktop, primStage, model);
        this.parentPane = parent;
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();

        btnBuildSelectedWord.setDisable(true);
    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Build Article Kanji");
        this.addHeaderText(txtFormTitle, 0, 0);

        lblArticleTitle = createLabel("...");
        lblJCoinAmount = createLabel("0");
        lblJCoinAmount.setId("wordmatch-coin-amount");

        EventHandler<ActionEvent> fncLoadArticleButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processLoadArticleClick();
            }
        };
        btnLoadArticle = createButton("Load Article", fncLoadArticleButtonClick);
        HBox firstSection = new HBox(btnLoadArticle, lblArticleTitle, new Label("Current JCoin:"), lblJCoinAmount);
        firstSection.setAlignment(Pos.CENTER);
        //this.addHeaderCtl(lblArticleTitle, 0, 2);
        this.addHeaderPane(firstSection, 0, 2);

        lvTNASentences = createSingleSelectStringListView(0);
        lvTNASentences.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.4));
        lvTNASentences.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        lvTNASentences.setId("article-build-word-sentence-list");
        this.addHeaderCtl(lvTNASentences, 0, 3);

        tafArticleJAContent = new TextArea();
        tafArticleJAContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.13));
        tafArticleJAContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.5));
        tafArticleJAContent.setId("build-word-ja-content");
        tafArticleJAContent.setWrapText(true);
        tafArticleJAContent.getStyleClass().add("bright_background_textfield");

        tafArticleENContent = new TextArea();
        tafArticleENContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.13));
        tafArticleENContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.5));
        tafArticleENContent.setId("build-word-en-content");
        tafArticleENContent.setWrapText(true);

        HBox hbContents = new HBox(tafArticleJAContent, tafArticleENContent);
        //hbContents.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        this.addHeaderPane(hbContents, 0, 4);
    }

    private void createBodyElements() {

        this.addBodyColumn(13);
        this.addBodyColumn(10);
        this.addBodyColumn(25);
        this.addBodyColumn(52);

        tfSelectedWord = new TextField();
        tfSelectedHiragana = new TextField();
        tfHv = new TextField();
        tfMeaning = new TextField();

        tfSelectedWord.setId("build-word-kanji");
        tfSelectedHiragana.setId("build-word-hiragana");
        tfHv.setId("build-word-hanviet");
        tfMeaning.setId("build-word-meaning");

        //if HV has value and meaning not has any yet, set meaning as Hv value
        //can be useful when creating new words
        ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (tfHv.getText().length() > 0 && tfMeaning.getText().length() < 1)
                        tfMeaning.setText(tfHv.getText());
                }
            }
        };
        tfHv.focusedProperty().addListener(focusListener);

        cbSelectedWord = new ComboBox();
        cbSelectedWord.setId("build-word-kanji-ref-combo");
        cbSelectedWord.setEditable(false);
        cbSelectedWord.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        cbSelectedWord.setOnAction((e) -> {
            //on select, distribute the values to related fields
            processSelectWordKanjiCombo((String)cbSelectedWord.getValue());
        });
        tfSelectedWord.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) { 
                //System.out.println(cbSelectedWord.getValue());
                //load reference combo based on entered text
                processLoadWordKanjiCombo(tfSelectedWord.getText());
            }
        });

        tfMeaning.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    //System.out.println("Textfield out focus");
                    checkEnoughForWordBuild();
                }
            }
        });

        EventHandler<ActionEvent> fncBuildWordButtonClick = new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                processBuildWordButtonClick();
            }
        };
        btnBuildSelectedWord = createButton("Build Word", fncBuildWordButtonClick);

        EventHandler<ActionEvent> fncStartWordMatchButtonClick = new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                processStartWordMatchButtonClick();
            }
        };
        btnStartWordMatchTest = createButton("WordMatch", fncStartWordMatchButtonClick);

        EventHandler<ActionEvent> fncStartArticleReadButtonClick = new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                processStartArticleReadButtonClick();
            }
        };
        btnStartArticleRead = createButton("ArticleRead", fncStartArticleReadButtonClick);

        lblTNAWordCount = createLabel("0");
        lvTNAKanjiWords = createSingleSelectStringListView(1);
        lvTNAKanjiWords.setId("build-word-list");
        lvTNADetailKanjis = createSingleSelectStringListView(2);
        lvTNADetailKanjis.setId("build-word-detail-kanji");
        tafKanjiMeaning = new TextArea();
        tafKanjiMeaning.setId("build-word-kanji-meaning");
        tafKanjiMeaning.setWrapText(true);

        tvBuiltWords = createKanjiTableView(0.5);
        tvBuiltWords.setId("management-kanji-list");
        createKanjiTableViewColumn("Kanji", 0.15);
        createKanjiTableViewColumn("Hiragana", 0.2);
        createKanjiTableViewColumn("Hv", 0.25);
        createKanjiTableViewColumn("Meaning", 0.25);
        createKanjiTableViewColumn("Correct", 0.05);
        createKanjiTableViewColumn("Test", 0.05);

        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Delete Word");
        cm.getItems().add(mi1);
        MenuItem mi2 = new MenuItem("Delete ALL Words");
        cm.getItems().add(mi2);
        MenuItem mi3 = new MenuItem("Merge to main kanjis");
        cm.getItems().add(mi3);

        mi1.setOnAction((ActionEvent event) -> {
            this.removeKanjiItemFromBuiltWordList();
        });
        mi2.setOnAction((ActionEvent event) -> {
            this.removeAllBuiltWordList();
        });
        mi3.setOnAction((ActionEvent event) -> {
            this.mergeAllBuiltWordList();
        });
        tvBuiltWords.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(tvBuiltWords, t.getScreenX(), t.getScreenY());
                }
            }
        });

        lvTNAKanjiWords.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.28));
        lvTNADetailKanjis.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.28));
        tafKanjiMeaning.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.28));
        tvBuiltWords.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.28));

        this.addBodyCtl(new Label("Selected Word"), 0, 0);
        this.addBodyCtl(new Label("Hiragana"), 1, 0);
        this.addBodyCtl(new Label("Hanviet"), 2, 0);
        this.addBodyCtl(new Label("Meaning"), 3, 0);

        this.addBodyCtl(tfSelectedWord, 0, 1);
        this.addBodyCtl(tfSelectedHiragana, 1, 1);
        this.addBodyCtl(tfHv, 2, 1);
        this.addBodyCtl(tfMeaning, 3, 1);

        this.addBodyCtl(cbSelectedWord, 0, 2);
        HBox wordCmdPane = new HBox(btnBuildSelectedWord, btnStartWordMatchTest, btnStartArticleRead, lblTNAWordCount);
        wordCmdPane.setAlignment(Pos.CENTER);
        this.addBodyPane(wordCmdPane, 3, 2);

        this.addBodyCtl(lvTNAKanjiWords, 0, 3);
        this.addBodyCtl(lvTNADetailKanjis, 1, 3);
        this.addBodyCtl(tafKanjiMeaning, 2, 3);
        this.addBodyCtl(tvBuiltWords, 3, 3);

    }

    private void clearLists() {
        lvTNASentences.getItems().clear();
        lvTNAKanjiWords.getItems().clear();
        lvTNADetailKanjis.getItems().clear();
        tafKanjiMeaning.clear();
        tvBuiltWords.getItems().clear();

        tfSelectedWord.clear();
        cbSelectedWord.getItems().clear();

        tfSelectedHiragana.clear();
        tfHv.clear();
        tfMeaning.clear();
    }

    private void createKanjiTableViewColumn(final String title, final double width)
    {
        TableColumn<ArticleWordBuildTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(tvBuiltWords.widthProperty().multiply(width));
        tcol.setResizable(false);
        tvBuiltWords.getColumns().add(tcol);
    }

    private TableView<ArticleWordBuildTableViewItem> createKanjiTableView(final double height) {
        TableView<ArticleWordBuildTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ArticleWordBuildTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ArticleWordBuildTableViewItem rowData = row.getItem();
                    processKanjiTableViewDblClick(rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<ArticleWordBuildTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;        
    }

    private void checkEnoughForWordBuild() {
        if (currentTNA == null) return;

        String sWordKJ = tfSelectedWord.getText();
        String sWordHira = tfSelectedHiragana.getText();
        String sWordHv = tfHv.getText();
        String sWordMeaning = tfMeaning.getText();

        if (sWordKJ != null && sWordHira != null && sWordHv != null && sWordMeaning != null) {
            if (sWordKJ.length() > 0 && sWordHira.length() > 0 && sWordHv.length() > 0 && sWordMeaning.length() > 0) {
                btnBuildSelectedWord.setDisable(false);
            }
        }
    }

    private void reloadBuiltWordList() {
        if (currentTNA == null) return;
        tvBuiltWords.getItems().clear();
        lblTNAWordCount.setText(String.valueOf(currentTNA.getKanjisForTest().size()));

        for (JBGKanjiItem kItem: currentTNA.getKanjisForTest()) {
            ArticleWordBuildTableViewItem showItem = new ArticleWordBuildTableViewItem(
                kItem.getKanji(),
                kItem.getHiragana(),
                kItem.getHv(),
                kItem.getMeaning(),
                kItem.getTestCount(),
                kItem.getCorrectCount()
                );
            tvBuiltWords.getItems().add(showItem);
        }

    }

    private void mergeAllBuiltWordList() {
        if (currentTNA == null) return;
        if (!this.showQuestion("Merge ALL?", "Merge ALL BuiltWord to Main List", "Do you want to merge ALL built words???"))
            return;

        List<JBGKanjiItem> kanjisForTest = currentTNA.getKanjisForTest();
        int totalBuiltWord = kanjisForTest.size();
        int totalMergeWord = 0;
        for (JBGKanjiItem kItem: kanjisForTest) {
            if (this.getDataModel().addBuiltWordToGrandKanjiList(kItem)) {
                totalMergeWord++;
            }
        }
        String msg = String.format("Words merged: %d/%d", totalMergeWord, totalBuiltWord);
        this.showInformation("Merge result", msg);

    }

    private void removeAllBuiltWordList() {
        if (currentTNA == null) return;
        if (!this.showQuestion("Delete ALL?", "ALL BuiltWord Delete Confim", "Do you want to delete ALL built words???"))
            return;
        currentTNA.getKanjisForTest().clear();
        lblTNAWordCount.setText(String.valueOf(currentTNA.getKanjisForTest().size()));
    }

    private void removeKanjiItemFromBuiltWordList() {
        if (currentTNA == null) return;

        ArticleWordBuildTableViewItem item = tvBuiltWords.getSelectionModel().getSelectedItem();
        if (item != null) {

            if (!this.showQuestion("Delete?", "BuiltWord Delete Confim", "Do you want to delete this word: " + item.getKanji() + "?"))
                return;

            boolean isRemovedInMemory = false;
            Iterator itr = currentTNA.getKanjisForTest().iterator();
            while (itr.hasNext()) {
                JBGKanjiItem kItem = (JBGKanjiItem) itr.next();
                if (kItem.getKanji().equals(item.getKanji())) {
                    //System.out.println("remove item from TNA test words");
                    //System.out.println(kItem);
                    itr.remove();
                    isRemovedInMemory = true;
                    break;
                }
            }

            if (isRemovedInMemory) {
                tvBuiltWords.getItems().removeAll(item);
                lblTNAWordCount.setText(String.valueOf(currentTNA.getKanjisForTest().size()));
            }
        }

    }

    private void processSelectWordKanjiCombo(final String sComboItem) {
        if (sComboItem == null) return;
        String[] sItemParts = sComboItem.split("\\|", -1);
        if (sItemParts.length == 4) {
            tfSelectedWord.setText(sItemParts[0]);
            tfSelectedHiragana.setText(sItemParts[1]);
            tfHv.setText(sItemParts[2]);
            tfMeaning.setText(sItemParts[3]);
            checkEnoughForWordBuild();
        }
    }

    private String simplifyArticleKanjiForSelect(TFMTTNAKanjiData kj) {
        StringBuilder sb = new StringBuilder();
        sb.append(kj.getKanji());
        sb.append("||");
        sb.append(kj.getHv());
        sb.append("|");
        return sb.toString();
    }
    //assume that all kanjis is attached with the articles
    private String buildDynamicFromArticleKanjis(final String sWord) {
        StringBuilder sbKanjis = new StringBuilder("");
        //scan whole word
        for (TFMTTNAKanjiData kjData: currentTNA.getArticleKanjis()) {
            if (kjData.getKanji().equals(sWord)) {
                sbKanjis.append(simplifyArticleKanjiForSelect(kjData));
            }
        }
        //scan each char
        for (char ch: sWord.toCharArray()) {
            for (TFMTTNAKanjiData kjData: currentTNA.getArticleKanjis()) {
                if (kjData.getKanji().equals(ch)) {
                    sbKanjis.append(simplifyArticleKanjiForSelect(kjData));
                }
            }
        }
        return sbKanjis.toString();
    }

    private void processLoadWordKanjiCombo(final String sKanji) {
        cbSelectedWord.getItems().clear();
        currentBuildWordKanjiSimilarList = this.getDataModel().getSimilarKanjiFromMainKanjiList(sKanji);
        if (currentBuildWordKanjiSimilarList != null && currentBuildWordKanjiSimilarList.size() > 0) {
            for (JBGKanjiItem item: currentBuildWordKanjiSimilarList) {
                StringBuilder sb = new StringBuilder(
                    item.getKanji() + "|" + item.getHiragana() + "|" + item.getHv() + "|" + item.getMeaning()
                    );
                cbSelectedWord.getItems().add(sb.toString());
            }
        }

        String sDynKanjis = buildDynamicFromArticleKanjis(sKanji);
        if (sDynKanjis.length() > 0) {
            cbSelectedWord.getItems().add(sDynKanjis);
        }
        if (cbSelectedWord.getItems().size() > 0) {
            //cbSelectedWord.show();
        }
    }

    private void processKanjiTableViewDblClick(ArticleWordBuildTableViewItem rowData) {
        if (rowData == null) return;
        if (currentTNA == null) return;

        setBuildWordComboValue(rowData.getKanji());

        tfSelectedHiragana.setText(rowData.getHiragana());
        tfHv.setText(rowData.getHv());
        tfMeaning.setText(rowData.getMeaning());

        btnBuildSelectedWord.setDisable(false);

        //System.out.println(rowData.toString());
    }

    private void processStartArticleReadButtonClick() {
        String sItem = getListSelectedString(lvTNAKanjiWords);
        if (currentTNA == null) return;

        this.getDataModel().setNeedRefresh(true);
        this.parentPane.switchToTab(3);
    }

    private void processArticleSentenceListClick() {
        String sItem = getListSelectedString(lvTNASentences);
        if (sItem == null) return;
        if (currentTNA == null) return;

        //System.out.println(sItem);
        loadSentenceDetail(sItem);
    }

    private void processArticleWordListClick() {
        String sItem = getListSelectedString(lvTNAKanjiWords);
        if (sItem == null) return;
        if (currentTNA == null) return;
        loadSelectedTNAKanjiWordDetail(sItem);
        selectBuiltWordIfExists(sItem);
    }

    private void loadSelectedTNAKanjiWordDetail(final String sItem) {
        boolean isArticleKJFound = false;
        for (int i = 0; i < currentTNA.getArticleKanjis().size(); i++) {
            currentTNAKanji = currentTNA.getArticleKanjis().get(i);
            if (currentTNAKanji.getKanji().equals(sItem)) {
                isArticleKJFound = true;
                break;
            }
        }

        JBGKanjiItem mainItem = this.getDataModel().getKanjiFromMainKanjiList(sItem);
        if (isArticleKJFound && currentTNAKanji != null) {
            setBuildWordComboValue(currentTNAKanji.getKanji());

            if (mainItem != null) {
                tfSelectedHiragana.setText(mainItem.getHiragana());
                tfHv.setText(mainItem.getHv());
                tfMeaning.setText(mainItem.getMeaning());
            }
            else {
                tfSelectedHiragana.setText(currentTNAKanji.getHiragana());
                if (currentTNAKanji.getHv() != null)
                    tfHv.setText(currentTNAKanji.getHv());
                else
                    tfHv.clear();
                tfMeaning.clear();
            }

            tafKanjiMeaning.clear();

            List<TFMTTNAKanjiDetailData> lstKanjis = currentTNAKanji.getKanjis();
            lvTNADetailKanjis.getItems().clear();
            String kanjisOfWord = currentTNAKanji.getKanji();
            //thu tu cua kanji trong ds cua tung word khong theo thu tu trong word
            //nen phai dam bao viec add vao nhu sau:
            for (int i = 0; i < kanjisOfWord.length(); i++) {
                String tmpKJCharacter = Character.toString(kanjisOfWord.charAt(i));
                for (TFMTTNAKanjiDetailData kDtl: lstKanjis) {
                    if (tmpKJCharacter.equals(kDtl.getKanji())) {
                      lvTNADetailKanjis.getItems().add(kDtl.getKanji());
                      break;
                    }
                }
            }
            String sFirstDetailKanji = (String) lvTNADetailKanjis.getItems().get(0);
            loadDetailKanjiItemMeaning(sFirstDetailKanji);

        }

        btnBuildSelectedWord.setDisable(false);
    }

    private int getKanjiBuiltIndexInTNA(final String kj) {
        int iRes = 0;
        List<JBGKanjiItem> kanjisForTest = currentTNA.getKanjisForTest();
        for (JBGKanjiItem kItem: kanjisForTest) {
            if (kItem.getKanji().equals(kj))
                return iRes;
            iRes++;
        }
        return -1;
    }

    //select and show the kanji if it is already in built list of TNA
    //this is to avoid duplication process (not data, because data is unique)
    private void selectBuiltWordIfExists(final String kj) {
        int iBuilt = getKanjiBuiltIndexInTNA(kj);
        //System.out.println("selectBuiltWordIfExists:" + kj + " pos: " + String.valueOf(iBuilt));
        if (iBuilt >= 0) {
            tvBuiltWords.requestFocus();
            tvBuiltWords.getSelectionModel().select(iBuilt);
            tvBuiltWords.getFocusModel().focus(iBuilt);
            tvBuiltWords.scrollTo(iBuilt);
        }
        else {
            tvBuiltWords.getSelectionModel().clearSelection();
            tvBuiltWords.getFocusModel().focus(0);
            tvBuiltWords.scrollTo(0);
        }
    }

    private void setBuildWordComboValue(final String sVal) {
        tfSelectedWord.setText(sVal);
        processLoadWordKanjiCombo(sVal);
    }

    private void processKanjiListClick() {
        String sItem = getListSelectedString(lvTNADetailKanjis);
        if (sItem == null) return;

        loadDetailKanjiItemMeaning(sItem);
    }

    private void loadDetailKanjiItemMeaning(final String sItem) {
        if (currentTNA == null) return;
        if (currentTNAKanji == null) return;
        List<TFMTTNAKanjiDetailData> lstKanjis = currentTNAKanji.getKanjis();
        for (TFMTTNAKanjiDetailData kDtl: lstKanjis) {
            if (kDtl.getKanji().equals(sItem)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Kanji: ");
                sb.append(sItem);
                sb.append("\nOn-kun phonetic:\n");
                sb.append(kDtl.getOnkun());
                sb.append("\nMeaning: ");
                sb.append("\n" + kDtl.getMeaning().replace("<br>", "\n"));
                tafKanjiMeaning.setText(sb.toString());
                break;
            }
        }
    }

    private void processBuildWordButtonClick() {
        final String sWord = tfSelectedWord.getText().strip();
        final String sHiragana = tfSelectedHiragana.getText().strip();
        final String sHanviet = tfHv.getText().strip();
        final String sMeaning = tfMeaning.getText().strip();
        if (sWord.length() < 1) {
            cbSelectedWord.requestFocus();
            return;
        }
        if (sHiragana.length() < 1) {
            tfSelectedHiragana.requestFocus();
            return;
        }
        if (sHanviet.length() < 1) {
            tfHv.requestFocus();
            return;
        }
        if (sMeaning.length() < 1) {
            tfMeaning.requestFocus();
            return;
        }
        if (currentTNA == null) return;

        List<JBGKanjiItem> kanjisForTest = currentTNA.getKanjisForTest();
        boolean kanjiExists = false;
        for (JBGKanjiItem kItem: kanjisForTest) {
            if (kItem.getKanji().equals(sWord)) {
                //update it if exists
                kItem.setHiragana(sHiragana);
                kItem.setHv(sHanviet);
                kItem.setMeaning(sMeaning);
                kanjiExists = true;
                break;
            }
        }
        if (!kanjiExists) {
            JBGKanjiItem kItem = new JBGKanjiItem(sWord, sHiragana, sHanviet, sMeaning);
            kanjisForTest.add(kItem);

            this.getDataModel().increaseJCoin(sWord.length()*WORD_SCORE_MULTIPLIER);
            lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        }
        //reloadTNAWordList("");
        reloadBuiltWordList();

        btnBuildSelectedWord.setDisable(true);

    }

    private void processStartWordMatchButtonClick() {
        if (currentTNA == null) return;
        if (this.getDataModel().isTestStarted()) {
            this.showInformation("Cannot switch tab!", "You are currently in a match, complete or stop it first!");
            return;
        }

        this.getDataModel().setCurrentWorkMode(JBGConstants.TEST_WORD_IN_ARTICLE);
        this.getDataModel().setNeedRefresh(true);
        this.parentPane.switchToTab(2);

    }

    private void processLVColumnButtonClick(final int iCol) {

        String sItem = null;
        ListView lv = null;

        switch(iCol) {
        case 0:
            processArticleSentenceListClick();
            break;
        case 1:
            processArticleWordListClick();
            break;
        case 2:
            processKanjiListClick();
            break;
        }

        if (lv != null) {
            sItem = getListSelectedString(lv);
            //System.out.println(sItem);
        }
    }

    private void processColumnListViewKeyEvent(final KeyCode kc, final boolean isShiftDown ) {
        String sItem = null;
        ListView lv = null;

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

    private void processLoadArticleClick() {
        clearLists();

        currentTNA = this.getDataModel().getSelectedTNA();
        if (currentTNA != null) {
            reloadTNASentenceList();
            reloadTNAWordList("");
            reloadBuiltWordList();
            lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));
        }
    }

    private void loadSentenceDetail(final String selectedSentence) {
        currentTNA = this.getDataModel().getSelectedTNA();
        if (currentTNA != null) {
            String sArticleTitle = currentTNA.getArticleTitle();
            lblArticleTitle.setText(sArticleTitle);
            StringBuilder sb = new StringBuilder();
            StringBuilder sbEng = new StringBuilder();
            for (TFMTTNASentenceData sen: currentTNA.getArticleSentences()) {
                if (sen.getSentence().equals(selectedSentence)) {
                    sb.append(sen.getSentence() + "\n");
                    String engMeaning = sen.getEnglishMeaning();
                    if (engMeaning != null)
                      sbEng.append(sen.getEnglishMeaning() + "\n");
                    break;
                }
            }
            String jaText = sb.toString().strip().replace("\n", "");
            tafArticleJAContent.setText(jaText);
            tafArticleENContent.setText(sbEng.toString());

            cbSelectedWord.getItems().clear();
            lvTNADetailKanjis.getItems().clear();
            tafKanjiMeaning.setText("");
            tfSelectedWord.setText("");
            tfSelectedHiragana.setText("");
            tfHv.setText("");
            tfMeaning.setText("");
            //load kanji list for the sentence
            int iLoadedCount = reloadTNAWordList(jaText);
            //System.out.println("loaded sen: " + jaText + " " + String.valueOf(iLoadedCount) + " " + String.valueOf(jaText.length()));
            if (iLoadedCount < 1) {// && jaText.length() < 3) {
                selectBuiltWordIfExists(jaText);
            }

            /*
            //if first kanji in the TNAKanjiWords is the beginning of sentence, or sentence equals to it
            //auto load the first kanji
            if (lvTNAKanjiWords.getItems().size() > 0) {
                String firstTNAKanji = lvTNAKanjiWords.getItems().get(0);
                if (jaText.indexOf(firstTNAKanji) == 0) {
                    loadSelectedTNAKanjiWordDetail(firstTNAKanji);
                    //lvTNADetailKanjis.getItems().add(firstTNAKanji);
                    if (jaText.length() <= 3) {
                        tfMeaning.setText(sbEng.toString());
                    }
                }
            }
            */
        }
    }

    private void reloadTNASentenceList() {
        if (currentTNA == null) return;
        lvTNASentences.getItems().clear();
        for (TFMTTNASentenceData sen: currentTNA.getArticleSentences()) {
            lvTNASentences.getItems().add(sen.getSentence());
        }
    }

    private int reloadTNAWordList(final String sPromptText) {
        int iTotalLoad = 0;
        boolean bNeedPrompt = true;
        if (currentTNA == null) return 0;
        if (sPromptText.length() < 1) bNeedPrompt = false;
        lvTNAKanjiWords.getItems().clear();
        for (TFMTTNAKanjiData kjData: currentTNA.getArticleKanjis()) {
            if (!bNeedPrompt)
                lvTNAKanjiWords.getItems().add(kjData.getKanji());
            else {
                String[] subSenWords = sPromptText.split("\\s+");
                for (String wrd : subSenWords) {
                    //don't add already built TNA kanji here
                    if (getKanjiBuiltIndexInTNA(wrd) >= 0) continue;

                    if (kjData.getKanji().contains(wrd) || wrd.contains(kjData.getKanji())) {
                        if (wrd.length() > 1) {
                            if (kjData.getKanji().length() > 1) {
                              lvTNAKanjiWords.getItems().add(kjData.getKanji());
                              iTotalLoad++;
                            }
                        }
                        else {
                            lvTNAKanjiWords.getItems().add(kjData.getKanji());
                            iTotalLoad++;
                        }
                    }
                }
            }
        }
        return iTotalLoad;
    }

    @Override
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode(); 
        switch (kc) {
            case DELETE:
                if (tvBuiltWords.isFocused()) {
                    removeKanjiItemFromBuiltWordList();
                }
                break;
        }
    }

    private ListView createSingleSelectStringListView(final int iCol) {
        ListView<String> listView = new ListView<>();

        EventHandler<MouseEvent> lstClickHander = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 1) {
                    processLVColumnButtonClick(iCol);
                }
            }
        };

        listView.setOnMouseClicked(lstClickHander);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        return listView;
    }

    private String getListSelectedString(ListView lst) {
        String sSelected = (String) lst.getSelectionModel().getSelectedItem();
        if (sSelected != null) {
            //System.out.println(sSelected);
        }
        return sSelected;
    }

    public void onShow() {
        tafArticleJAContent.lookup(".content").setStyle("-fx-background-color: lightgray;");
        tafArticleJAContent.setStyle("-fx-font-size: 14pt;-fx-font-family: Arial;");
        tafArticleENContent.setStyle("-fx-font-size: 14pt;-fx-font-family: Arial;");

        //always refresh this
        lblJCoinAmount.setText(String.valueOf(this.getDataModel().getJCoin()));

        if (this.getDataModel().isNeedRefresh()) {
            //unset it
            this.getDataModel().setNeedRefresh(false);

            processLoadArticleClick();
        }
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