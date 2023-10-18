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
import javax.swing.KeyStroke;

import tientn.easynews.reader.gui.base.SimpleStackedFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.JBGConstants;
import tientn.easynews.reader.data.DictKanjiTableViewItem;

import java.sql.*;

// SimpleFormBase derived class
public class KanjiDictionaryTab extends SimpleStackedFormBase {

    private List<JBGKanjiItem> kanjiList;

    private Label lblFormTitle;

    private Label lblTotalKanjis;
    private Label lblTotalDictKanjis;

    private TableView<DictKanjiTableViewItem> lvFirstCol;
    private TableView<DictKanjiTableViewItem> lvSecondCol;

    private Button btnReloadKanjis;

    private Button btnSearchKanji;

    private static final String SEARCH_BTN_LABEL = "Search";

    private TextField tfDictSearchWord;
    private TextArea tafKanjiMeaning;
    private TextArea tafDictKanjiMeaning;

    public KanjiDictionaryTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
        super(width, height, desktop, primStage, model);

        this.getTopBodyLabel().setId("wordmatch-top-kanji-label");
        this.getMidBodyLabel().setId("wordmatch-middle-kanji-label");
        this.getBottomBodyLabel().setId("wordmatch-bottom- kanji-label");
    }

    @Override
    protected void initForm() {
        createHeaderElements();
        createBodyElements();
    }

    private void createHeaderElements() {
        this.addHeaderColumn(100);
        Text txtFormTitle = new Text("Tien's Kanji Dictionary");
        this.addHeaderText(txtFormTitle, 0, 0);
    }

    private void createBodyElements() {

        this.addBodyColumn(50);
        this.addBodyColumn(50);

        Label lblLoadedKanjis = new Label("Total Kanjis Loaded:");
        lblTotalKanjis = createLabel(String.format("%d/%d", 0, this.getDataModel().getDataKanjiItems().size()));
        Label lblDictKanjis = new Label("Total Kanjis in Dictionary:");
        lblTotalDictKanjis = createLabel("0/0");

        EventHandler<ActionEvent> fncSearchKanjiClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                doSearchKanji();
            }
        };
        btnSearchKanji = createButton(SEARCH_BTN_LABEL, fncSearchKanjiClick);

        tfDictSearchWord = new TextField("");
        tfDictSearchWord.setPromptText("Enter Kanji here ...");
        tfDictSearchWord.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.25));

        tafKanjiMeaning = new TextArea();
        tafKanjiMeaning.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.25));
        tafKanjiMeaning.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafKanjiMeaning.setId("kanji-content-view");
        tafKanjiMeaning.setWrapText(true);
        tafKanjiMeaning.setEditable(false);
        tafKanjiMeaning.setPromptText("");

        tafDictKanjiMeaning = new TextArea();
        tafDictKanjiMeaning.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.6));
        tafDictKanjiMeaning.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafDictKanjiMeaning.setId("dict-kanji-content-view");
        tafDictKanjiMeaning.setWrapText(true);
        tafDictKanjiMeaning.setEditable(false);
        tafDictKanjiMeaning.setPromptText("");

        lvFirstCol = createTableView(0, 0.7);
        lvFirstCol.setId("loaded-kanji-column");
        createTableViewColumn(lvFirstCol, "Id", 0.05);
        createTableViewColumn(lvFirstCol, "Kanji", 0.2);
        createTableViewColumn(lvFirstCol, "HV", 0.2);
        createTableViewColumn(lvFirstCol, "Hiragana", 0.2);
        createTableViewColumn(lvFirstCol, "Meaning", 0.3);

        lvSecondCol = createTableView(1, 0.7);
        lvSecondCol.setId("dict-kanji-column");
        createTableViewColumn(lvSecondCol, "Id", 0.04);
        createTableViewColumn(lvSecondCol, "Kanji", 0.04);
        createTableViewColumn(lvSecondCol, "HV", 0.3);
        createTableViewColumn(lvSecondCol, "Hiragana", 0.3);
        createTableViewColumn(lvSecondCol, "Meaning", 0.3);

        lvFirstCol.getStyleClass().add("wordmatch-scroll-bar");
        lvSecondCol.getStyleClass().add("wordmatch-scroll-bar");

        lvFirstCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.25));
        lvSecondCol.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.6));


        this.addBodyPane(new HBox(lblLoadedKanjis, lblTotalKanjis), 0, 1);
        this.addBodyPane(new HBox(lblDictKanjis, lblTotalDictKanjis), 1, 1);

        HBox bxLoadNextWords = new HBox(tfDictSearchWord, btnSearchKanji);
        this.addBodyPane(bxLoadNextWords, 0, 0);

        //this.addBodyCtl(lblFirstCol, 0, 3);
        //this.addBodyCtl(lblSecondCol, 1, 3);

        this.addBodyCtl(lvFirstCol, 0, 2);
        this.addBodyCtl(tafKanjiMeaning, 1, 2);
        this.addBodyCtl(lvSecondCol, 0, 3);
        this.addBodyCtl(tafDictKanjiMeaning, 1, 3);

        this.getTopBodyLabel().setId("dict-top-kanji-label");
        this.getMidBodyLabel().setId("dict-mid-kanji-label");
        this.getBottomBodyLabel().setId("dict-bottom-kanji-label");

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
            tafKanjiMeaning.setText(rowData.toString().replace("|", "\n"));
        }
        else {
            this.getBottomBodyLabel().setText(rowData.getKanji());
            tafDictKanjiMeaning.setText(rowData.toString().replace("|", "\n").replace("<br>", "\n"));
        }
        //this.dataModel.setSelectedArticleId(sTNAId); //this will also set needRefresh
        //this.parentPane.switchToTab(1);
    }

    private void doSearchKanji() {
        //reloadKanjiList();
        int iTotalDbKanjis = this.getDataModel().dbKanjisCount();
        lblTotalDictKanjis.setText(String.format("%d/%d", 0, iTotalDbKanjis));

        String sSearchWord = tfDictSearchWord.getText();
        if (sSearchWord.length() > 0) {

            final String val = tfDictSearchWord.getText();
            this.getMidBodyLabel().setText(val);

            lvFirstCol.getItems().clear();
            lvSecondCol.getItems().clear();

            for (String s : sSearchWord.split("")) {
                searchKanjiInMemory(s);
                searchKanjiInDb(s);
            }
        }
    }

    private void addWordToKanjiList(final TableView<DictKanjiTableViewItem> tblView, final String id, final String kanji, final String hv, final String hira, final String meaning) {
        DictKanjiTableViewItem showItem = new DictKanjiTableViewItem(
            id, kanji, hv, hira, meaning
            );
        tblView.getItems().add(showItem);
    }

    private void reloadKanjiList() {

        lblTotalKanjis.setText(String.format("%d/%d", 0, this.getDataModel().getDataKanjiItems().size()));
        lvFirstCol.getItems().clear();
        for (JBGKanjiItem kItem: this.getDataModel().getDataKanjiItems()) {
            addWordToKanjiList(lvFirstCol, 
                kItem.getId().toString(), kItem.getKanji(), kItem.getHv(), kItem.getHiragana(), kItem.getMeaning());
        }
    }

    private void searchKanjiInMemory(final String sKanji) {
        int iTotalFound = 0;
        for (JBGKanjiItem kItem: this.getDataModel().getDataKanjiItems()) {
            if (kItem.getKanji().indexOf(sKanji) >= 0) {
                iTotalFound++;
                addWordToKanjiList(lvFirstCol, 
                    kItem.getId().toString(), kItem.getKanji(), kItem.getHv(), kItem.getHiragana(), kItem.getMeaning());
            }
        }
        lblTotalKanjis.setText(String.format("%d/%d", iTotalFound, this.getDataModel().getDataKanjiItems().size()));
    }

    private void searchKanjiInDb(final String sKanji) {
        int iRecId = 0;
        List<JBGKanjiItem> lstRes = this.getDataModel().dbKanjiSearch(sKanji);
        if (lstRes.size() > 0) {
            for (JBGKanjiItem item: lstRes) {
                addWordToKanjiList(lvSecondCol, item.getId().toString(), 
                    item.getKanji(), item.getHv(), item.getHiragana(), item.getMeaning());
            }
        }
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
            doSearchKanji();
        }
    }

    public void onShow() {
        //always refresh this
        reloadKanjiList();
        return;
    }

    public void onStopShow() {}

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