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
import javafx.scene.input.KeyCode;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 
import com.fasterxml.jackson.core.JsonParser;
import java.nio.charset.Charset;
import java.io.FileNotFoundException;

import tientn.easynews.reader.data.JBGKanjiItem;
import tientn.easynews.reader.data.ReaderModel;
import tientn.easynews.reader.data.TFMTWorkData;
import tientn.easynews.reader.data.TFMTTNAData;
import tientn.easynews.reader.data.TFMTTNGData;
import tientn.easynews.reader.gui.base.GridPaneBase;
import tientn.easynews.reader.data.JBGConstants;
import tientn.easynews.reader.data.ManagementStatusTableViewItem;
import tientn.easynews.reader.data.ManagementKanjiTableViewItem;
import tientn.easynews.reader.data.ManagementArticleTableViewItem;
import tientn.easynews.reader.data.ManagementGrammarTableViewItem;

// Simple Hello World JavaFX program
public class ManagementWindowTab extends GridPaneBase {

    private MainTabbedPane parentPane;
    private ReaderModel dataModel;
    private String currentKJFilePath;
    private String currentTFMTSaveFile;
    private String currentJCoinSaveFile;
    private String DEFAULT_TFMT_SAVE_FILE_NAME = "current_work.tfmt";
    private String DEFAULT_JCOIN_EXPORT_FILE_NAME = "current_point.tfmt";

    TableView<ManagementStatusTableViewItem> statusTableView;
    TableView<ManagementKanjiTableViewItem> kanjiTableView;
    TableView<ManagementArticleTableViewItem> articleTableView;
    TableView<ManagementGrammarTableViewItem> grammarTableView;

    public ManagementWindowTab(final String title, Desktop desktop, Stage primStage, final ReaderModel model, MainTabbedPane parent) {
        super(title, desktop, primStage);
        this.dataModel = model;
        this.parentPane = parent;
    }

    @Override
    protected void initForm() {

        //this.setMinWidth(1000);
        //this.setMaxWidth(900);

        Text txtFormTitle = new Text("Learning Material Management");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        this.getColumnConstraints().addAll(col1, col2);

        //this.getChildren().clear();
        showBorder();

        this.add(txtFormTitle, 0, 0);
        this.setValignment(txtFormTitle, VPos.TOP);

        createFormElements();
    }

    private void createFormElements() {

        GridPane paneLeft = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        paneLeft.getColumnConstraints().addAll(col1, col2);

        Button btnKJLoad = createKJLoadButton();
        Button btnCSVLoad = createCSVLoadButton();
        Button btnTFMTLoad = createTFMTLoadButton();

        EventHandler<ActionEvent> fncLoadTNGButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processLoadTNGButtonEvent();
            }
        };
        Button btnTNGLoad = createButton("TNG Format", fncLoadTNGButtonClick);

        EventHandler<ActionEvent> fncLoadTNAButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processLoadTNAButtonEvent();
            }
        };
        Button btnTNALoad = createButton("TNA Format", fncLoadTNAButtonClick);

        EventHandler<ActionEvent> fncSaveTFMTButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processSaveTFMTButtonEvent();
            }
        };
        Button btnTFMTSave = createButton("Save TFMT", fncSaveTFMTButtonClick);

        EventHandler<ActionEvent> fncExportJCoinButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processExportJCoinButtonEvent();
            }
        };
        Button btnExportJCoin = createButton("Export JCoin", fncExportJCoinButtonClick);

        paneLeft.add(new Label("Load Learning Process"), 0, 1);
        paneLeft.add(btnTFMTLoad, 1, 1);

        paneLeft.add(new Label("Save Learning Process"), 0, 2);
        paneLeft.add(btnTFMTSave, 1, 2);

        paneLeft.add(new Label("Export jCoin"), 0, 3);
        paneLeft.add(btnExportJCoin, 1, 3);

        paneLeft.add(new Label("Load Kanji (CSV)"), 0, 4);
        paneLeft.add(btnCSVLoad, 1, 4);

        paneLeft.add(new Label("Load Kanji (KJ)"), 0, 5);
        paneLeft.add(btnKJLoad, 1, 5);

        paneLeft.add(new Label("Load Grammar"), 0, 6);
        paneLeft.add(btnTNGLoad, 1, 6);

        paneLeft.add(new Label("Load Article"), 0, 7);
        paneLeft.add(btnTNALoad, 1, 7);

        statusTableView = createStatusTableView(0.3);
        statusTableView.setId("management-status-list");
        createStatusTableViewColumn("Category", 0.15);
        createStatusTableViewColumn("Value", 0.85);
        //this.setVgrow(tableView, Priority.ALWAYS);

        kanjiTableView = createKanjiTableView(0.7);
        kanjiTableView.setId("management-kanji-list");
        createKanjiTableViewColumn("Kanji", 0.3);
        createKanjiTableViewColumn("Hiragana", 0.3);
        createKanjiTableViewColumn("Correct", 0.2);
        createKanjiTableViewColumn("Test", 0.2);

        articleTableView = createArticleTableView(0.7);
        articleTableView.setId("management-article-list");
        createArticleTableViewColumn("Id", 0.05);
        createArticleTableViewColumn("Title", 0.75);
        createArticleTableViewColumn("Sentences", 0.05);
        createArticleTableViewColumn("Kanjis", 0.05);
        createArticleTableViewColumn("Test", 0.05);
        createArticleTableViewColumn("Correct", 0.05);

        grammarTableView = createGrammarTableView(0.3);
        grammarTableView.setId("management-grammar-list");
        createGrammarTableViewColumn("Id", 0.05);
        createGrammarTableViewColumn("Title", 0.7);
        createGrammarTableViewColumn("Patterns", 0.05);
        createGrammarTableViewColumn("Test", 0.05);
        createGrammarTableViewColumn("Correct", 0.05);

        VBox articleAndGrammarBox = new VBox(grammarTableView, articleTableView);
        //hbox.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.7));

        //this.add(mainBox, 0, 1);
        this.add(paneLeft, 0, 1);
        this.add(statusTableView, 1, 1);

        this.add(kanjiTableView, 0, 2);
        this.add(articleAndGrammarBox, 1, 2);
        //this.add(new Label("Label2"), 0, 4);
        //this.add(new Label("Label3"), 1, 4);

    }

    private void processLoadTNGButtonEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("TNG Files", "*." + JBGConstants.TNG_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's Grammar File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            openTNGFile(file);
        }
    }

    private void processLoadTNAButtonEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("TNA Files", "*." + JBGConstants.TNA_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's NHKEasyNews Article File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            openTNAFile(file);
        }
    }

    private void processSaveTFMTButtonEvent() {
        //this.dataModel.printCurrentKanjisWithTest();
        final String sContent = this.dataModel.getJsonDataAsString();
        //System.out.println(sContent);
        saveTFMTToFile(this.currentTFMTSaveFile, sContent);
    }

    private void processExportJCoinButtonEvent() {
        if (!showQuestion("Export JCoin", "JCoin export and reset JCoin in current work", "Are you sure to export JCoin from to existing work?")) {
            return;
        }

        exportJCoinToFile(this.currentJCoinSaveFile, this.dataModel.getJCoin());

        this.dataModel.setJCoin(0);
        statusTableView.getItems().add(new ManagementStatusTableViewItem("Reset JCoin to: ", "0"));

        processSaveTFMTButtonEvent();
    }

    private void processCSVLoadEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("CSV Files", "*." + JBGConstants.KANJI_SOURCE_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's CSV File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            openCSVFile(file);
        }
    }

    private void processKJLoadEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("KJ Files", "*." + JBGConstants.KJ_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's KJ File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            openKJFile(file);
        }
    }

    private void processTFMTLoadEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("Kanjis TFMT", "*." + JBGConstants.TFMT_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's TFMT File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            openTFMTFile(file);
        }
    }

    private void saveTFMTToFile(final String fileName, final String content) {
          System.out.println("Saving to file " + fileName);
          File fOutputFile = new File(fileName);
          final String sMessage = "Saved " + String.valueOf(content.length()) + " bytes to: " + fileName;

          try {
              PrintWriter pw = new PrintWriter(fOutputFile, Charset.forName("UTF-8"));
              pw.println(content);
              pw.flush(); //if not, PrintWriter may not write the whole data 
              statusTableView.getItems().add(new ManagementStatusTableViewItem(sMessage, fileName));
          } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
          }
          catch (IOException e) {
            System.out.println("saveTFMTToFile error: " + e.getMessage());
          }
          finally {
            showMessage("Work save", "Save complete!", sMessage);
          }
    }

    private void exportJCoinToFile(final String fileName, final int iCoin) {
          System.out.println("Saving to file " + fileName);
          File fOutputFile = new File(fileName);
          try {
              PrintWriter pw = new PrintWriter(fOutputFile, Charset.forName("UTF-8"));
              pw.println(String.valueOf(iCoin));
              pw.flush(); //if not, PrintWriter may not write the whole data 
              statusTableView.getItems().add(new ManagementStatusTableViewItem("Saved " + String.valueOf(iCoin) + " jCoin to: ", fileName));
          } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
          }
          catch (IOException e) {
            System.out.println("exportPointToFile error: " + e.getMessage());
          }
    }

    private Button createKJLoadButton() {
        String message = "KJ Format";

        Button btn = new Button();
        btn.setText(message);

        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processKJLoadEvent();
            }
        };
        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(null);
                }
        };

        btn.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btn;
    }

    private Button createCSVLoadButton() {
        String message = "CSV Format";

        Button btn = new Button();
        btn.setText(message);

        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processCSVLoadEvent();
            }
        };
        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(null);
                }
        };

        btn.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btn;
    }

    private Button createTFMTLoadButton() {
        String message = "TFMT format";

        Button btn = new Button();
        btn.setText(message);

        DropShadow shadow = new DropShadow();

        EventHandler<ActionEvent> fncButtonClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                processTFMTLoadEvent();
            }
        };
        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(null);
                }
        };

        btn.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btn;
    }

    private Button createButton(final String title, EventHandler<ActionEvent> fncHandler) {
        String message = title;

        Button btn = new Button();
        btn.setText(message);

        DropShadow shadow = new DropShadow();

        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btn.setEffect(null);
                }
        };

        btn.setOnAction(fncHandler);

        //Adding the shadow when the mouse cursor is on
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btn;
    }

    private void openCSVFile(File file) {
        try {
            System.out.println("loading file:" + file.getPath());
            List<JBGKanjiItem> lstKanjis = null;
            if (this.dataModel.isDataLoaded()) {
                if (!showQuestion("Load Confirmation", "Kanjis are already loaded?", "Are you sure to merge new kanjis in to existing list?")) {
                    return;
                }
            }
            lstKanjis = this.dataModel.loadKanjiFromCSV(file);

            statusTableView.getItems().add(new ManagementStatusTableViewItem("Loaded from CSV", file.getPath()));            
            statusTableView.getItems().add(new ManagementStatusTableViewItem("Kanjis from CSV", String.valueOf(lstKanjis.size())));
        } catch (Exception ex) {
            System.out.println("ERROR openFile: " + ex.getMessage());
        }
    }

    private void openKJFile(File file) {
        try {
            System.out.println("loading file:" + file.getPath());
            List<JBGKanjiItem> lstKanjis = null;
            if (this.dataModel.isDataLoaded()) {
                if (!showQuestion("Load Confirmation", "Kanjis are already loaded?", "Are you sure to merge new kanjis in to existing list?")) {
                    return;
                }
            }
            lstKanjis = this.dataModel.loadKanjiFromInJBoardSaved(file);
            int iTotalKanjis = lstKanjis.size();
            int iTotalTests = 0;
            int iTotalCorrect = 0;
            for (int i = 0; i < iTotalKanjis; i++) {
                  JBGKanjiItem item = lstKanjis.get(i);
                  if (item.getCorrectCount() >= JBGConstants.KANJI_MIN_TEST_CORRECT) {
                    iTotalCorrect++;
                  }
                  iTotalTests += item.getTestCount();
            }
            statusTableView.getItems().add(new ManagementStatusTableViewItem("Loaded from KJ", file.getPath()));            
            statusTableView.getItems().add(new ManagementStatusTableViewItem("Kanjis from KJ", String.valueOf(iTotalKanjis)));
            statusTableView.getItems().add(new ManagementStatusTableViewItem("Tests from KJ", String.valueOf(iTotalTests)));
            statusTableView.getItems().add(new ManagementStatusTableViewItem("Correct from KJ", String.valueOf(iTotalCorrect)));

            this.currentKJFilePath = file.getParent();
            this.currentTFMTSaveFile = this.currentKJFilePath + "/" + DEFAULT_TFMT_SAVE_FILE_NAME;
            this.currentJCoinSaveFile = this.currentKJFilePath + "/" + DEFAULT_JCOIN_EXPORT_FILE_NAME;
            statusTableView.getItems().add(new ManagementStatusTableViewItem("TFMT Target File", this.currentTFMTSaveFile));
            statusTableView.getItems().add(new ManagementStatusTableViewItem("JCoin Target File", this.currentJCoinSaveFile));

        } catch (Exception ex) {
            System.out.println("ERROR openFile: " + ex.getMessage());
        }
    }

    private void openTFMTFile(File file) {
        try {
            if (this.dataModel.loadTFMTJsonFromFile(file.getPath())) {
                this.currentTFMTSaveFile = file.getPath();

                this.currentKJFilePath = file.getParent();
                this.dataModel.setGrammarMP3FolderPath(this.currentKJFilePath + "/mp3");
                this.dataModel.setArticleMP3FolderPath(this.currentKJFilePath + "/mp3/articles");
                this.currentJCoinSaveFile = this.currentKJFilePath + "/" + DEFAULT_JCOIN_EXPORT_FILE_NAME;

                statusTableView.getItems().add(new ManagementStatusTableViewItem("TFMT Target File", this.currentTFMTSaveFile));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("JCoin Target File", this.currentJCoinSaveFile));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("TFMT Kanjis:", String.valueOf(this.dataModel.getDataKanjiItems().size()) + " kanjis"));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("TFMT Kanji Tests:", String.valueOf(this.dataModel.getTotalKanjiTests()) + " done"));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("TFMT Kanji Corrects:", String.valueOf(this.dataModel.getTotalMatchedKanjis()) + " qualified"));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("JCoin loaded:", String.valueOf(this.dataModel.getJCoin())));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("Last work date:", String.valueOf(this.dataModel.getLastWorkDate())));
                statusTableView.getItems().add(new ManagementStatusTableViewItem("Penalty applied:", String.valueOf(this.dataModel.isPenaltyApplied())));

                reloadKanjiList();
                reloadTNAList();
                reloadTNGList();
            }
            else {
                System.out.println("cannot load object from TFMT");
            }
        } catch (Exception ex) {
            System.out.println("ERROR openFile: " + ex.getMessage());
        }

        if (this.dataModel.isPenaltyApplied()) {
            showMessage("Yesterday you didn't work?", 
                "jCoin earning penalty will be applied to this session of work!",
                "Last work date: "+this.dataModel.getLastWorkDate()
                );
        }
    }

    private void openTNAFile(File file) {
        try {
            if (this.dataModel.loadTNAJsonFromFile(file.getPath())) {
                statusTableView.getItems().add(new ManagementStatusTableViewItem("TNA loaded:", file.getPath()));
                reloadTNAList();
            }
            else {
                System.out.println("cannot load object from TNA");
            }
        } catch (Exception ex) {
            System.out.println("ERROR openFile: " + ex.getMessage());
        }
    }

    private void openTNGFile(File file) {
        try {
            if (this.dataModel.loadTNGJsonFromFile(file.getPath())) {
                statusTableView.getItems().add(new ManagementStatusTableViewItem("TNG loaded:", file.getPath()));
                reloadTNGList();
            }
            else {
                System.out.println("cannot load object from TNG");
            }
        } catch (Exception ex) {
            System.out.println("ERROR openFile: " + ex.getMessage());
        }
    }

    private void reloadKanjiList() {
        kanjiTableView.getItems().clear();
        for (JBGKanjiItem kItem: this.dataModel.getDataKanjiItems()) {
            ManagementKanjiTableViewItem showItem = new ManagementKanjiTableViewItem(
                kItem.getKanji(),
                kItem.getHiragana(),
                kItem.getTestCount(),
                kItem.getCorrectCount()>JBGConstants.KANJI_MIN_TEST_CORRECT?true:false
                );
            kanjiTableView.getItems().add(showItem);
        }
    }
    private void reloadTNAList() {
        articleTableView.getItems().clear();
        for (TFMTTNAData aItem: this.dataModel.getDataTNAItems()) {
            ManagementArticleTableViewItem showItem = new ManagementArticleTableViewItem(
                aItem.getId().toString(),
                aItem.getArticleTitle(),
                aItem.getArticleSentences().size(),
                aItem.getKanjisForTest().size(),
                aItem.getTestTotalOfKanjiForTest(),
                aItem.getTotalCorrectTests()
                );
            articleTableView.getItems().add(showItem);
        }
    }
    private void reloadTNGList() {
        grammarTableView.getItems().clear();
        for (TFMTTNGData aItem: this.dataModel.getDataTNGItems()) {
            System.out.println(aItem.getGrammarTitle());
            ManagementGrammarTableViewItem showItem = new ManagementGrammarTableViewItem(
                aItem.getId().toString(),
                aItem.getGrammarTitle(),
                aItem.getGrammarPattern().size(),
                aItem.getTotalTests(),
                aItem.getTotalCorrectTests()
                );
            grammarTableView.getItems().add(showItem);
        }
    }

    private void processStatusTableViewDblClick(ManagementStatusTableViewItem rowData) {
        System.out.println(rowData.toString());
    }

    private void processKanjiTableViewDblClick(ManagementKanjiTableViewItem rowData) {
        System.out.println(rowData.toString());

        if (this.dataModel.isTestStarted()) return;

        this.dataModel.setCurrentWorkMode(JBGConstants.TEST_WORD_IN_MAJOR_LIST);

        //unset it
        this.dataModel.setNeedRefresh(true);

        this.parentPane.switchToTab(2);
    }

    private void processArticleTableViewDblClick(ManagementArticleTableViewItem rowData) {
        System.out.println(rowData.toString());
        String sTNAId = rowData.getId().toString();
        this.dataModel.setSelectedArticleId(sTNAId); //this will also set needRefresh
        this.parentPane.switchToTab(1);
    }

    private void processGrammarTableViewDblClick(ManagementGrammarTableViewItem rowData) {
        System.out.println(rowData.toString());
        String sTNGId = rowData.getId().toString();
        this.dataModel.setSelectedGrammarId(sTNGId); //this will also set needRefresh
        this.dataModel.setNeedRefresh(true);

        boolean grammarFeature = this.showQuestionWithCustomOptions("Choose feature", "Read or Listen", "Do you want to read or listen", "Read", "Listen");
        if (grammarFeature)
            this.parentPane.switchToTab(4);
        else
            this.parentPane.switchToTab(5);
    }

    private void createStatusTableViewColumn(final String title, final double width)
    {
        TableColumn<ManagementStatusTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(statusTableView.widthProperty().multiply(width));
        tcol.setResizable(false);
        statusTableView.getColumns().add(tcol);
    }
    private void createKanjiTableViewColumn(final String title, final double width)
    {
        TableColumn<ManagementKanjiTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(kanjiTableView.widthProperty().multiply(width));
        tcol.setResizable(false);
        kanjiTableView.getColumns().add(tcol);
    }
    private void createArticleTableViewColumn(final String title, final double width)
    {
        TableColumn<ManagementArticleTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(articleTableView.widthProperty().multiply(width));
        tcol.setResizable(false);
        articleTableView.getColumns().add(tcol);
    }
    private void createGrammarTableViewColumn(final String title, final double width)
    {
        TableColumn<ManagementGrammarTableViewItem, String> tcol = new TableColumn<>(title);
        tcol.setCellValueFactory(new PropertyValueFactory<>(title.toLowerCase()));
        tcol.prefWidthProperty().bind(grammarTableView.widthProperty().multiply(width));
        tcol.setResizable(false);
        grammarTableView.getColumns().add(tcol);
    }

    private TableView<ManagementStatusTableViewItem> createStatusTableView(final double height) {
        TableView<ManagementStatusTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ManagementStatusTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ManagementStatusTableViewItem rowData = row.getItem();
                    processStatusTableViewDblClick(rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<ManagementStatusTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;        
    }

    private TableView<ManagementKanjiTableViewItem> createKanjiTableView(final double height) {
        TableView<ManagementKanjiTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ManagementKanjiTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ManagementKanjiTableViewItem rowData = row.getItem();
                    processKanjiTableViewDblClick(rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<ManagementKanjiTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;        
    }

    private TableView<ManagementArticleTableViewItem> createArticleTableView(final double height) {
        TableView<ManagementArticleTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ManagementArticleTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ManagementArticleTableViewItem rowData = row.getItem();
                    processArticleTableViewDblClick(rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<ManagementArticleTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;
    }

    private TableView<ManagementGrammarTableViewItem> createGrammarTableView(final double height) {
        TableView<ManagementGrammarTableViewItem> tableView = new TableView<>();
        tableView.setRowFactory(tv -> {
            TableRow<ManagementGrammarTableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ManagementGrammarTableViewItem rowData = row.getItem();
                    processGrammarTableViewDblClick(rowData);
                    //System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        //when empty
        tableView.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<ManagementGrammarTableViewItem> selectionModel = tableView.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        tableView.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(height));

        return tableView;
    }

    private void removeArticleFromList() {

        ManagementArticleTableViewItem item = articleTableView.getSelectionModel().getSelectedItem();
        System.out.println(item.toString());
        String sTNAId = item.getId().toString();

        if (item != null) {

            if (!this.showQuestion("Delete?", "Do you want to delete this article?", item.getTitle()))
                return;
        }

        if (this.dataModel.deleteTNAById(sTNAId)) {
            reloadTNAList();
        }
        else {
            System.out.println("ERROR: cannot delete item!");
        }
    }

    @Override
    protected void processKeyPress(final KeyEvent ke) {
        KeyCode kc = ke.getCode(); 
        switch (kc) {
            case DELETE:
            case BACK_SPACE:
                if (articleTableView.isFocused()) {
                    removeArticleFromList();
                }
                break;
        }
    }

    public void onShow() {}
}