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

import tientn.easynews.reader.gui.base.SimpleFormBase;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.JBGConstants;

// SimpleFormBase derived class
public class EmptyWindowTab extends SimpleFormBase {

    private List<JBGKanjiItem> kanjiList;

    private Label lblArticleTitle;
    private TextArea tafArticleContent;
    private TextArea tafKanjiMeaning;

    private TextField tfSelectedWord;
    private TextField tfSelectedHiragana;
    private TextField tfHv;
    private TextField tfMeaning;

    private Button btnBuildSelectedWord;

    private ListView<String> lvTNAKanjiWords;
    private ListView<String> lvTNAKanjis;
    private ListView<String> lvBuiltWords;

    public EmptyWindowTab(final int width, final int height, Desktop desktop, Stage primStage, ReaderModel model) {
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
        tafArticleContent = new TextArea();
        tafArticleContent.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.3));
        tafArticleContent.prefHeightProperty().bind(getPrimaryStage().widthProperty().multiply(1));
        tafArticleContent.setId("build-kanji-kanji");
    }

    private void createBodyElements() {

        this.addBodyColumn(25);
        this.addBodyColumn(25);
        this.addBodyColumn(25);
        this.addBodyColumn(25);

        tfSelectedWord = new TextField();
        tfSelectedHiragana = new TextField();
        tfHv = new TextField();
        tfMeaning = new TextField();

        tfSelectedWord.setId("build-kanji-kanji");
        tfSelectedHiragana.setId("build-kanji-kanji");
        tfHv.setId("build-kanji-kanji");
        tfMeaning.setId("build-kanji-kanji");

        EventHandler<ActionEvent> fncBuildWordButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //refreshKanjiStats();
            }
        };
        btnBuildSelectedWord = createButton("Build Word", fncBuildWordButtonClick);

        lvTNAKanjiWords = createSingleSelectStringListView(0);
        lvTNAKanjis = createSingleSelectStringListView(1);
        tafKanjiMeaning = new TextArea();
        lvBuiltWords = createSingleSelectStringListView(3);

        lvTNAKanjiWords.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        lvTNAKanjis.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        tafKanjiMeaning.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));
        lvBuiltWords.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.5));

        this.addBodyCtl(new Label("Selected Word"), 0, 0);
        this.addBodyCtl(new Label("Hiragana"), 1, 0);
        this.addBodyCtl(new Label("Hanviet"), 2, 0);
        this.addBodyCtl(new Label("Meaning"), 3, 0);

        this.addBodyCtl(tfSelectedWord, 0, 1);
        this.addBodyCtl(tfSelectedHiragana, 1, 1);
        this.addBodyCtl(tfHv, 2, 1);
        this.addBodyCtl(tfMeaning, 3, 1);

        this.addBodyCtl(btnBuildSelectedWord, 3, 2);

        this.addBodyCtl(lvTNAKanjiWords, 0, 3);
        this.addBodyCtl(lvTNAKanjis, 1, 3);
        this.addBodyCtl(tafKanjiMeaning, 2, 3);
        this.addBodyCtl(lvBuiltWords, 3, 3);

    }

    private void clearLists() {
        lvTNAKanjiWords.getItems().clear();
        lvTNAKanjis.getItems().clear();
        tafKanjiMeaning.setText("...");
        lvBuiltWords.getItems().clear();
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

    @Override
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode(); 
        switch (kc) {
            case DIGIT1:
                break;
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