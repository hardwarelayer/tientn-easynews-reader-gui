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

    private List<JBGKanjiItem> kanjiList;

    private Label lblArticleTitle;
    private TextArea tafArticleJAContent;
    private TextArea tafArticleENContent;
    private TextArea tafKanjiMeaning;

    private TextField tfSelectedWord;
    private TextField tfSelectedHiragana;
    private TextField tfHv;
    private TextField tfMeaning;

    private Button btnLoadArticle;
    private Button btnBuildSelectedWord;

    private ListView<String> lvTNAKanjiWords;
    private ListView<String> lvTNAKanjis;
    TableView<ArticleWordBuildTableViewItem> kanjiTableView;

    private TFMTTNAData currentTNA = null;
    private boolean isTNAFound = false;
    private TFMTTNAKanjiData currentTNAKanji = null;

    public ArticleWordBuildWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();
    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Build Article Kanji");
        this.addHeaderText(txtFormTitle, 0, 0);

        lblArticleTitle = createLabel("...");
        tafArticleJAContent = new TextArea();
        tafArticleJAContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.2));
        tafArticleJAContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafArticleJAContent.setId("build-word-ja-content");
        tafArticleJAContent.setWrapText(true);

        tafArticleENContent = new TextArea();
        tafArticleENContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.1));
        tafArticleENContent.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafArticleENContent.setId("build-word-en-content");
        tafArticleENContent.setWrapText(true);

        this.addHeaderCtl(lblArticleTitle, 0, 2);
        this.addHeaderCtl(tafArticleJAContent, 0, 3);
        this.addHeaderCtl(tafArticleENContent, 0, 4);
    }

    private void createBodyElements() {

        this.addBodyColumn(13);
        this.addBodyColumn(14);
        this.addBodyColumn(28);
        this.addBodyColumn(45);

        tfSelectedWord = new TextField();
        tfSelectedHiragana = new TextField();
        tfHv = new TextField();
        tfMeaning = new TextField();

        tfSelectedWord.setId("build-word-kanji");
        tfSelectedHiragana.setId("build-word-hiragana");
        tfHv.setId("build-word-hanviet");
        tfMeaning.setId("build-word-meaning");

        EventHandler<ActionEvent> fncLoadArticleButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processLoadArticleClick();
            }
        };
        btnLoadArticle = createButton("Load Article", fncLoadArticleButtonClick);

        EventHandler<ActionEvent> fncBuildWordButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processBuildWordButtonClick();
            }
        };
        btnBuildSelectedWord = createButton("Build Word", fncBuildWordButtonClick);

        lvTNAKanjiWords = createSingleSelectStringListView(0);
        lvTNAKanjis = createSingleSelectStringListView(1);
        tafKanjiMeaning = new TextArea();
        tafKanjiMeaning.setId("build-word-kanji-meaning");
        tafKanjiMeaning.setWrapText(true);

        kanjiTableView = createKanjiTableView(0.5);
        kanjiTableView.setId("management-kanji-list");
        createKanjiTableViewColumn("Kanji", 0.1);
        createKanjiTableViewColumn("Hiragana", 0.2);
        createKanjiTableViewColumn("Hv", 0.3);
        createKanjiTableViewColumn("Meaning", 0.3);
        createKanjiTableViewColumn("Correct", 0.05);
        createKanjiTableViewColumn("Test", 0.05);

        lvTNAKanjiWords.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        lvTNAKanjis.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        tafKanjiMeaning.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        kanjiTableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));

        this.addBodyCtl(new Label("Selected Word"), 0, 0);
        this.addBodyCtl(new Label("Hiragana"), 1, 0);
        this.addBodyCtl(new Label("Hanviet"), 2, 0);
        this.addBodyCtl(new Label("Meaning"), 3, 0);

        this.addBodyCtl(tfSelectedWord, 0, 1);
        this.addBodyCtl(tfSelectedHiragana, 1, 1);
        this.addBodyCtl(tfHv, 2, 1);
        this.addBodyCtl(tfMeaning, 3, 1);

        this.addBodyCtl(btnLoadArticle, 0, 2);
        this.addBodyCtl(btnBuildSelectedWord, 3, 2);

        this.addBodyCtl(lvTNAKanjiWords, 0, 3);
        this.addBodyCtl(lvTNAKanjis, 1, 3);
        this.addBodyCtl(tafKanjiMeaning, 2, 3);
        this.addBodyCtl(kanjiTableView, 3, 3);

    }

    private void clearLists() {
        lvTNAKanjiWords.getItems().clear();
        lvTNAKanjis.getItems().clear();
        tafKanjiMeaning.setText("");
        kanjiTableView.getItems().clear();

        tfSelectedWord.setText("");
        tfSelectedHiragana.setText("");
        tfHv.setText("");
        tfMeaning.setText("");
    }

    private void createKanjiTableViewColumn(final String title, final double width)
    {
        TableColumn<ArticleWordBuildTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(kanjiTableView.widthProperty().multiply(width));
        tcol.setResizable(false);
        kanjiTableView.getColumns().add(tcol);
    }

    private TableView<ArticleWordBuildTableViewItem> createKanjiTableView(final double height) {
        TableView<ArticleWordBuildTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ArticleWordBuildTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ArticleWordBuildTableViewItem rowData = row.getItem();
                    processKanjiTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
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

    private void reloadBuiltWordList() {
        if (currentTNA == null) return;
        kanjiTableView.getItems().clear();
        for (JBGKanjiItem kItem: currentTNA.getKanjisForTest()) {
            ArticleWordBuildTableViewItem showItem = new ArticleWordBuildTableViewItem(
                kItem.getKanji(),
                kItem.getHiragana(),
                kItem.getHv(),
                kItem.getMeaning(),
                kItem.getTestCount(),
                kItem.getCorrectCount()
                );
            kanjiTableView.getItems().add(showItem);
        }

    }

    private void processKanjiTableViewDblClick(ArticleWordBuildTableViewItem rowData) {
        if (rowData == null) return;
        if (currentTNA == null) return;

        tfSelectedWord.setText(rowData.getKanji());
        tfSelectedHiragana.setText(rowData.getHiragana());
        tfHv.setText(rowData.getHv());
        tfMeaning.setText(rowData.getMeaning());

        //System.out.println(rowData.toString());
    }

    private void processArticleWordListClick() {
        String sItem = getListSelectedString(lvTNAKanjiWords);
        if (sItem == null) return;
        if (currentTNA == null) return;

        boolean isArticleKJFound = false;
        for (int i = 0; i < currentTNA.getArticleKanjis().size(); i++) {
            currentTNAKanji = currentTNA.getArticleKanjis().get(i);
            if (currentTNAKanji.getKanji().equals(sItem)) {
                isArticleKJFound = true;
                break;
            }
        }

        if (isArticleKJFound && currentTNAKanji != null) {
            tfSelectedWord.setText(currentTNAKanji.getKanji());
            tfSelectedHiragana.setText(currentTNAKanji.getHiragana());
            if (currentTNAKanji.getHv() != null)
                tfHv.setText(currentTNAKanji.getHv());
            else
                tfHv.setText("");
            tfMeaning.setText("");

            tafKanjiMeaning.setText("");

            List<TFMTTNAKanjiDetailData> lstKanjis = currentTNAKanji.getKanjis();
            lvTNAKanjis.getItems().clear();
            String kanjisOfWord = currentTNAKanji.getKanji();
            //thu tu cua kanji trong ds cua tung word khong theo thu tu trong word
            //nen phai dam bao viec add vao nhu sau:
            for (int i = 0; i < kanjisOfWord.length(); i++) {
                String tmpKJCharacter = Character.toString(kanjisOfWord.charAt(i));
                for (TFMTTNAKanjiDetailData kDtl: lstKanjis) {
                    if (tmpKJCharacter.equals(kDtl.getKanji())) {
                      lvTNAKanjis.getItems().add(kDtl.getKanji());
                      break;
                    }
                }
            }

        }

    }

    private void processKanjiListClick() {
        String sItem = getListSelectedString(lvTNAKanjis);
        if (sItem == null) return;
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
            tfSelectedWord.requestFocus();
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
        }
        reloadTNAWordList();
        //reloadTNAKanjiForTestList();
        reloadBuiltWordList();
    }

    private void processColumnButtonClick(final int iCol) {

        String sItem = null;
        ListView lv = null;

        switch(iCol) {
        case 0:
            processArticleWordListClick();
            break;
        case 1:
            processKanjiListClick();
            break;
        }

        if (lv != null) {
            sItem = getListSelectedString(lv);
            System.out.println(sItem);
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

        String sSelTNAId = this.getDataModel().getSelectedArticleId();
        System.out.println(sSelTNAId);
        List<TFMTTNAData> lstTNAs = this.getDataModel().getDataTNAItems();

        currentTNA = null;
        isTNAFound = false;
        for (int i = 0; i < lstTNAs.size(); i++) {
            currentTNA = lstTNAs.get(i);
            String sId = currentTNA.getId().toString();
            if (sId.equals(sSelTNAId)) {
                isTNAFound = true;
                break;
            }
        }
        if (isTNAFound) {
            String sArticleTitle = currentTNA.getArticleTitle();
            lblArticleTitle.setText(sArticleTitle);
            StringBuilder sb = new StringBuilder();
            StringBuilder sbEng = new StringBuilder();
            for (TFMTTNASentenceData sen: currentTNA.getArticleSentences()) {
                sb.append(sen.getSentence() + "\n");
                String engMeaning = sen.getEnglishMeaning();
                if (engMeaning != null)
                  sbEng.append(sen.getEnglishMeaning() + "\n");
            }
            tafArticleJAContent.setText(sb.toString());
            tafArticleENContent.setText(sbEng.toString());

            reloadTNAWordList();
            //reloadTNAKanjiForTestList();
            reloadBuiltWordList();
        }
    }

    private void reloadTNAWordList() {
        if (currentTNA == null) return;
        lvTNAKanjiWords.getItems().clear();
        for (TFMTTNAKanjiData kjData: currentTNA.getArticleKanjis()) {
            lvTNAKanjiWords.getItems().add(kjData.getKanji());
        }

    }

    @Override
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode(); 
        switch (kc) {
            case DIGIT1:
                break;
        }
    }

    private ListView createSingleSelectStringListView(final int iCol) {
        ListView<String> listView = new ListView<>();

        EventHandler<MouseEvent> lstClickHander = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    processColumnButtonClick(iCol);
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
            System.out.println(sSelected);
        }
        return sSelected;
    }

    public void onShow() {
        System.out.println("OnShow BuildWord");
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

}