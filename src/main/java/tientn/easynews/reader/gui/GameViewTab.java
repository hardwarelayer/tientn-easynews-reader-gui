package tientn.easynews.reader.gui;

import java.awt.Desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text; 
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;

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

import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
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

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;

import tientn.easynews.reader.gui.base.GridPaneBase;
import tientn.easynews.reader.data.TableViewItem;
import tientn.easynews.game.data.*;
import lombok.Getter;
import lombok.Setter;

public class GameViewTab extends GridPaneBase {

    @Getter private GameProcessor gameProcessor = null;
    @Getter private GameModel gameModel;

    Label lblSelectedCityName; //if = null here then in event handler it will always be null
    Label lblClockTicking, lblGameDay;
    String currentCityName = "";
    DefCity currentCityDef = null;

    Timeline tlMainTimeline = null;

    ContextMenu currentContextMenu = null;

    TableView<CityInfoTVItem> tvCityInfo;
    TableView<CityInfoTVItem> tvCityInventory;
    TableView<CityBuildingTVItem> tvCityBuilding;
    TableView<CityMessageTVItem> tvCityMessage;
    TableView<CityBuildQueueTVItem> tvCityBuildQueue;
    TableView<CityProductionTVItem> tvCityProduction;

    public GameViewTab(final String title, Desktop desktop, Stage primStage, GameModel gm) {
        super(title, desktop, primStage);
        this.gameModel = gm;
        this.gameProcessor = null;
    }

    @Override
    protected void initForm() {

        //Text txtFormTitle = new Text("Text Game");
        //this.setValignment(txtFormTitle, VPos.TOP);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        this.getColumnConstraints().addAll(col1);

        //this.add(txtFormTitle, 0, 0);

        createFormElements();
    }

    private void createFormElements() {

        Label label = new Label("Selected City:");
        this.lblClockTicking = new Label("");
        this.lblGameDay = new Label("");
        this.currentCityName = "Test";
        this.lblSelectedCityName = new Label(this.currentCityName);

        EventHandler<ActionEvent> fncNewGameClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //label.setText("Accepted");
                processGameNewEvent();
            }
        };
        Button btnNewGame = createButton("New", fncNewGameClick);

        EventHandler<ActionEvent> fncLoadGameClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //label.setText("Accepted");
                processGameLoadEvent();
            }
        };
        Button btnLoadGame = createButton("Load", fncLoadGameClick);

        HBox hbControlCommands = new HBox(btnNewGame, btnLoadGame);
        HBox hbControlInfo = new HBox(label, this.lblSelectedCityName, 
            new Label("Game Day:"), this.lblGameDay,
            this.lblClockTicking);
        HBox hbControlPane = new HBox(hbControlCommands, hbControlInfo);

        this.tvCityInfo = createCityInfoTableView();
        this.tvCityInventory = createCityInventoryTableView();
        this.tvCityBuilding = createCityBuildingTableView();
        this.tvCityMessage = createCityMessageTableView();
        this.tvCityBuildQueue = createCityBuildQueueTableView();
        this.tvCityProduction = createCityProductionTableView();

        this.tvCityInfo.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.4));
        this.tvCityInventory.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.3));
        this.tvCityBuilding.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.3));

        VBox leftPane = new VBox(tvCityInfo, tvCityInventory, tvCityBuilding);
        VBox rightPane = new VBox(tvCityBuildQueue, tvCityProduction, tvCityMessage);

        GridPane gpDetail = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        gpDetail.getColumnConstraints().addAll(col1, col2);
        gpDetail.add(leftPane, 0, 0);
        gpDetail.add(rightPane, 1, 0);

        this.add(hbControlPane, 0, 0);
        this.add(gpDetail, 0, 1);

        tvCityInfo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    processContextMenuShow(tvCityInfo, t.getScreenX(), t.getScreenY());
                }
            }
        });

    }

    private void processContextMenuShow(Node anchor, final double dblX, final double dblY) {
        if (this.gameProcessor == null) return;

        if (this.currentContextMenu == null) {
            this.currentContextMenu = createContextMenu();
        }

        this.currentContextMenu.show(anchor, dblX, dblY);
    }

    private Button createButton(final String sText, final EventHandler<ActionEvent> fncButtonClick) {
        Button btnObj = new Button();
        DropShadow shadow = new DropShadow();

        EventHandler<MouseEvent> fncBtnMouseEntered = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnObj.setEffect(shadow);
                }
        };

        EventHandler<MouseEvent> fncBtnMouseExited = new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    btnObj.setEffect(null);
                }
        };

        btnObj.setText(sText);
        btnObj.setOnAction(fncButtonClick);

        //Adding the shadow when the mouse cursor is on
        btnObj.addEventHandler(MouseEvent.MOUSE_ENTERED, fncBtnMouseEntered);
        //Removing the shadow when the mouse cursor is off
        btnObj.addEventHandler(MouseEvent.MOUSE_EXITED, fncBtnMouseExited);

        return btnObj;
    }

    private ContextMenu createContextMenu() {
        ContextMenu cmObj = new ContextMenu();

        Menu cmiBuilds = createBuildMenu();
/*         Menu acmMi1 = new Menu("Build");

        MenuItem sub1 = new MenuItem("house");
        MenuItem sub2 = new MenuItem("farm");
        acmMi1.getItems().addAll(sub1, sub2);
 */
        if (cmiBuilds != null)
            cmObj.getItems().add(cmiBuilds);
        MenuItem acmMi2 = new MenuItem("Administration");
        cmObj.getItems().add(acmMi2);
        SeparatorMenuItem sep = new SeparatorMenuItem();
        cmObj.getItems().add(sep);
        MenuItem acmMi3 = new MenuItem("Army");
        cmObj.getItems().add(acmMi3);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        cmObj.getItems().add(sep2);
        MenuItem acmMi4 = new MenuItem("Transfer to WordMatch2");
        cmObj.getItems().add(acmMi4);
/*
        acmMi1.setOnAction((ActionEvent event) -> {
            this.setStartAnchor();
        });
        acmMi2.setOnAction((ActionEvent event) -> {
            this.setEndAnchor();
        });
        acmMi3.setOnAction((ActionEvent event) -> {
            this.clearAnchors();
        });
        acmMi4.setOnAction((ActionEvent event) -> {
            this.transferToWordMatch2();
        });
 */
        return cmObj;
    }

    private Menu createBuildMenu() {
        if (this.gameProcessor == null) return null;

        Menu acmMi1 = new Menu("Build");

        for (DefBuilding bd : this.gameProcessor.getGameData().getDefBuildingItems()) {
            MenuItem sub1 = new MenuItem(bd.getName());

            sub1.setOnAction((ActionEvent event) -> {
                this.processBuildCommand(bd.getName());
            });
    
            acmMi1.getItems().add(sub1);
        }
        return acmMi1;
    }

    private DefBuilding getDefBuilding(final String name) {
        for (DefBuilding bd : this.gameProcessor.getGameData().getDefBuildingItems()) {
            if (bd.getName().equals(name)) {
                return bd;
            }
        }
        return null;
    }

    private boolean isBasicMaterial(final String name) {
        for (String s: Constants.basicCityMaterials) {
            if (s.equals(name))
                return true;
        }
        return false;
    }

    private void processCityLogics(final String logic, final String subLogic, final DefBuilding bld) {
      
        if (logic.equals(Constants.CITY_BUILDER_ACTION_TYPE)) {
            DefReaction defReact = this.gameProcessor.getGameData().getTalkLogicReact(
                Constants.CITY_BUILDER_ACTION_TYPE, subLogic);
            if (defReact == null) return;

            //show msg
            String msg = defReact.getReact().getMessage();
            System.out.println("The message is:" + msg);
            showMessageWithStyle("Build", msg, "game_dialog.css");
            if (!showQuestionWithStyle("build", 
                defReact.getReact().getConfirm().getPrompt(), 
                "game_dialog.css")) {
                    return;
                }

            if (this.currentCityDef == null) return;
            City curCity = this.gameProcessor.getGameData().getCurrentCity();
            if (curCity == null) return;

            //check resources
            if (bld == null) return;

            int iLandBlockForThis = 0;
            int iTimeForThis = 0;
            boolean bEnoughResource = true;
            for (DefBuildingCostType bldCost: bld.getCostLst()) {
                System.out.println("Cost:");
                System.out.println(" type:"+bldCost.getType());
                System.out.println(" qty:"+String.valueOf(bldCost.getQuantity()));
                if (isBasicMaterial(bldCost.getType())) {
                    System.out.println(" inventory:"+String.valueOf(curCity.getInvQty(bldCost.getType())));
                    if (bldCost.getQuantity() > curCity.getInvQty(bldCost.getType())) {
                        bEnoughResource = false;
                        break;
                    }
                }
                else {
                    if (bldCost.getType().equals(Constants.CITY_BUILD_BLOCK_NAME)) {
                        System.out.println(" free land block:"+String.valueOf(curCity.getFreeLandBlock()));
                        iLandBlockForThis = bldCost.getQuantity();
                        if (bldCost.getQuantity() > curCity.getFreeLandBlock()) {
                            bEnoughResource = false;
                            break;
                        }
                    }
                    else if (bldCost.getType().equals(Constants.CITY_BUILD_TIME_NAME)) {
                        //always have time
                        iTimeForThis = bldCost.getQuantity();
                    }
                    else { //other materials
                        System.out.println(" inventory:"+String.valueOf(curCity.getInvQty(bldCost.getType())));
                        if (bldCost.getQuantity() > curCity.getInvQty(bldCost.getType())) {
                            bEnoughResource = false;
                            break;
                        }
                    }
                }
            }
            if (!bEnoughResource) {
                showMessageWithStyle("Cannot build!", defReact.getReact().getConfirm().getCondition(), "game_dialog.css");  
                return;
            }
            else {
                if (!showQuestionWithStyle("Are you sure to spend resources?", 
                    defReact.getReact().getConfirm().getCondition(), 
                    "game_dialog.css")) {
                        return;
                     }
                CityBuildQueueItem bldItem = new CityBuildQueueItem(
                    bld.getType(), bld.getName(), iLandBlockForThis, 0, iTimeForThis, 
                    bld, this.gameProcessor.getGameTimeBySecond());

                if (bld.getCostLst() == null) {
                    System.out.println("Cost list is null for "+bldItem.getItemName());
                }
                else {
                    for (DefBuildingCostType bldCost: bld.getCostLst()) {
                        if (!bldCost.getType().equals(Constants.CITY_BUILD_BLOCK_NAME) &&
                            !bldCost.getType().equals(Constants.CITY_BUILD_TIME_NAME)
                            ) 
                        {
                            curCity.reduceInvQty(bldCost.getType(), bldCost.getQuantity());
                        }
                    }
                    fillCityInventoryListTV(tvCityInventory);
                    curCity.getBuildQueue().add(bldItem); 
                    fillCityBuildQueue(this.tvCityBuildQueue);
                }
            }

        }

    }

    private void processBuildCommand(final String buildingName) {
        DefBuilding objBuildDef = getDefBuilding(buildingName);
        if (objBuildDef == null) return;

        processCityLogics(
            Constants.CITY_BUILDER_ACTION_TYPE,
            Constants.CITY_BUILDING_REACT_TRIGGER_BUILD, 
            objBuildDef);

/*
        for ()
 String type;
 String name;
 List<String> value;
 DefTalkLogicValueRange valueRange;
 List<String> requireClass;
 List<String> bonusClass;
 List<DefBuildingCostType> costLst;
 List<DefBuildingProduce> produceLst;
 List<DefReaction> reaction;


 Constants.CITY_TALK_LOGICS = "talk_logics";
 Constants.CITY_REACT_TRIGGER_PROMPT = "prompt";
 Constants.CITY_REACT_TRIGGER_CONFIRM = "confirm";
 Constants.CITY_REACT_TRIGGER_CONDITION = "condition";
 Constants.CITY_REACT_TRIGGER_COMPLETE = "complete";

 Constants.CITY_BUILDER_ACTION_TYPE = "builder";
 Constants.CITY_POP_ACTION_TYPE = "pop";

 Constants.CITY_BUILDING_COST = "cost";
 Constants.CITY_BUILDING_PRODUCE = "produce";

 Constants.CITY_BUILDING_REACTION = "reaction";
 Constants.CITY_BUILDING_REACT_TYPE = "builder";
 Constants.CITY_BUILDING_REACT_TRIGGER_BUILD = "build";
 Constants.CITY_BUILDING_REACT_TRIGGER_REMOVE = "remove";

 */

    }

    private void processTableViewDblClick(TableViewItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private TableView<TableViewItem> createTableView() {
        TableView<TableViewItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<TableViewItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TableViewItem rowData = row.getItem();
                    processTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<TableViewItem, String> tcol1 = new TableColumn<>("Category");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<TableViewItem, String> tcol2 = new TableColumn<>("Value");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("value"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.4));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.6));
        tcol1.setResizable(false);
        tcol2.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<TableViewItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        tvObj.getItems().add(new TableViewItem("John", "Doe"));
        tvObj.getItems().add(new TableViewItem("Jane", "Deer"));

        return tvObj;
    }

    private void processCityInfoTableViewDblClick(CityInfoTVItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private void updateCityInfoTV(TableView<CityInfoTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        String sPop = String.valueOf(curCity.getPop());
        String sFreeSpaces = String.valueOf(curCity.getFreeLandBlock());
        String sSecurity = String.valueOf(curCity.getSecurity());
        for (CityInfoTVItem cInf: tvObj.getItems()) {
            if (cInf.getItemGroup().equals(Constants.CITY_BASIC_INFO_POP)) {
                String sValue = cInf.getValue();
                if (!sPop.equals(sValue)) {
                    cInf.setValue(sPop);
                }
            }
            if (cInf.getItemGroup().equals(Constants.CITY_BASIC_INFO_SECURITY)) {
                String sValue = cInf.getValue();
                if (!sSecurity.equals(sValue)) {
                    cInf.setValue(sSecurity);
                }
            }
            if (cInf.getItemGroup().isEmpty() && cInf.getItem().equals(Constants.CITY_BASIC_INFO_FREE_SPACE)) {
                String sValue = cInf.getValue();
                if (!sFreeSpaces.equals(sValue)) {
                    cInf.setValue(sFreeSpaces);
                }
            }
        }
        tvObj.refresh();
    }

    private void fillCityInfoTV(TableView<CityInfoTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        tvObj.getItems().add(new CityInfoTVItem("Owner", "", String.valueOf(curCity.getOwner())));
        tvObj.getItems().add(new CityInfoTVItem("Level", "", String.valueOf(curCity.getLevel())));
        tvObj.getItems().add(new CityInfoTVItem(Constants.CITY_BASIC_INFO_POP, "", String.valueOf(curCity.getPop())));
        tvObj.getItems().add(new CityInfoTVItem("Troop", "", String.valueOf(curCity.getTroop())));
        tvObj.getItems().add(new CityInfoTVItem(Constants.CITY_BASIC_INFO_SECURITY, "", String.valueOf(curCity.getSecurity())));

        tvObj.getItems().add(new CityInfoTVItem("Capability", "", ""));
        DefCityCapability cap = curCity.getDef().getCapability();
        tvObj.getItems().add(new CityInfoTVItem("", "Land", String.valueOf(cap.getLand())));
        tvObj.getItems().add(new CityInfoTVItem("", Constants.CITY_BASIC_INFO_FREE_SPACE, String.valueOf(curCity.getFreeLandBlock())));
        tvObj.getItems().add(new CityInfoTVItem("", "Farm", String.valueOf(cap.getFarm())));
        tvObj.getItems().add(new CityInfoTVItem("", "Iron", String.valueOf(cap.getIron())));
        tvObj.getItems().add(new CityInfoTVItem("", "Stone", String.valueOf(cap.getStone())));
        tvObj.getItems().add(new CityInfoTVItem("", "Clay", String.valueOf(cap.getClay())));
        tvObj.getItems().add(new CityInfoTVItem("", "Water", String.valueOf(cap.getWater())));

        tvObj.getItems().add(new CityInfoTVItem("Coordinate", 
            String.valueOf(curCity.getDef().getX()), 
            String.valueOf(curCity.getDef().getY())));
    }

    private TableView<CityInfoTVItem> createCityInfoTableView() {
        TableView<CityInfoTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityInfoTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityInfoTVItem rowData = row.getItem();
                    processCityInfoTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row;
        });

        TableColumn<CityInfoTVItem, String> tcol1 = new TableColumn<>("Item");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("itemGroup"));
        TableColumn<CityInfoTVItem, String> tcol2 = new TableColumn<>("");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("item"));
        TableColumn<CityInfoTVItem, String> tcol3 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("value"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityInfoTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        return tvObj;
    }

    private void processCityInventoryTableViewDblClick(CityInfoTVItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private TableView<CityInfoTVItem> createCityInventoryTableView() {
        TableView<CityInfoTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityInfoTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityInfoTVItem rowData = row.getItem();
                    processCityInventoryTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<CityInfoTVItem, String> tcol1 = new TableColumn<>("Group");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("itemGroup"));
        TableColumn<CityInfoTVItem, String> tcol2 = new TableColumn<>("Item");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("item"));
        TableColumn<CityInfoTVItem, String> tcol3 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("value"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityInfoTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        return tvObj;
    }

    private TableView<CityBuildingTVItem> createCityBuildingTableView() {
        TableView<CityBuildingTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityBuildingTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityBuildingTVItem rowData = row.getItem();
                    //processCityInventoryTableViewDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<CityBuildingTVItem, String> tcol1 = new TableColumn<>("Type");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<CityBuildingTVItem, String> tcol2 = new TableColumn<>("Name");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<CityBuildingTVItem, String> tcol3 = new TableColumn<>("Qty");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("qty"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityBuildingTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        return tvObj;
    }

    private void fillCityInventoryListTV(TableView<CityInfoTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        for (CityInventoryItem invItem: curCity.getInventoryItem()) {
            Constants.QtyWithCap qtyCap = curCity.getInventoryItemWithCap(invItem.getName());
            if (qtyCap == null) continue;
            String sQtyWCap = qtyCap.toString();
            boolean foundItem = false;
            for (CityInfoTVItem tvItem: tvObj.getItems()) {
                if (tvItem.getItem().equals(invItem.getName())) {
                    if (!tvItem.getValue().equals(sQtyWCap))
                        tvItem.setValue(sQtyWCap);
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                //add new
                tvObj.getItems().add(new CityInfoTVItem("", invItem.getName(), sQtyWCap));
            }
        }
        tvObj.refresh();
    }

    private void fillCityBuildingTV(TableView<CityBuildingTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        for (CityBuilding bldItem: curCity.getBuildingItem()) {
            boolean foundItem = false;
            for (CityBuildingTVItem tvItem: tvObj.getItems()) {
                if (tvItem.getName().equals(bldItem.getName())) {
                    if (tvItem.getQty() != bldItem.getQty())
                      tvItem.setQty(bldItem.getQty());
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                //add new
                tvObj.getItems().add(new CityBuildingTVItem(bldItem.getType(), bldItem.getName(), bldItem.getQty()));
            }
        }
        tvObj.refresh();
    }

    private void processCityBuildQueueDblClick(CityBuildQueueTVItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private void fillCityBuildQueue(TableView<CityBuildQueueTVItem> tvObj) {

        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        tvObj.getItems().clear();
        for (CityBuildQueueItem buildQueueItem: curCity.getBuildQueue()) {
            tvObj.getItems().add(
                new CityBuildQueueTVItem("Building", buildQueueItem.getItemName(), 
                    new StringBuilder(buildQueueItem.getDayCount() +"/"+buildQueueItem.getDayTotal()).toString())
                );
        }
    }

    private TableView<CityBuildQueueTVItem> createCityBuildQueueTableView() {
        TableView<CityBuildQueueTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityBuildQueueTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityBuildQueueTVItem rowData = row.getItem();
                    processCityBuildQueueDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<CityBuildQueueTVItem, String> tcol1 = new TableColumn<>("Item");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        TableColumn<CityBuildQueueTVItem, String> tcol2 = new TableColumn<>("");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        TableColumn<CityBuildQueueTVItem, String> tcol3 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("progressInfo"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityBuildQueueTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        return tvObj;
    }

    private void processCityProductionDblClick(CityProductionTVItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private TableView<CityProductionTVItem> createCityProductionTableView() {
        TableView<CityProductionTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityProductionTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityProductionTVItem rowData = row.getItem();
                    processCityProductionDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });
    
        TableColumn<CityProductionTVItem, String> tcol1 = new TableColumn<>("Item");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("buildingName"));
        TableColumn<CityProductionTVItem, String> tcol2 = new TableColumn<>("");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("buildingCount"));
        TableColumn<CityProductionTVItem, String> tcol3 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        TableColumn<CityProductionTVItem, String> tcol4 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        TableColumn<CityProductionTVItem, String> tcol5 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("dayCount"));
        TableColumn<CityProductionTVItem, String> tcol6 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("dayTotal"));
        TableColumn<CityProductionTVItem, String> tcol7 = new TableColumn<>("Value");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("outputAmount"));
/*
        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);
*/
        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);
        tvObj.getColumns().add(tcol4);
        tvObj.getColumns().add(tcol5);
        tvObj.getColumns().add(tcol6);
        tvObj.getColumns().add(tcol7);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityProductionTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        tvObj.getItems().add(new CityProductionTVItem("Factory", 1, "", "", 0, 2, 2));

        return tvObj;
    }

    private void processCityMessageDblClick(CityMessageTVItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private String getEventMessage(Constants.GameEvent e) {
        String sMsg = "";
        DefTalkLogic firstTalkLogic = null;
        DefTalkLogic matchedTalkLogic = null;

        for (DefTalkLogic dtlogic: this.gameProcessor.getGameData().getDefTalkLogicItems()) {
            if (dtlogic.getType().equals(e.getType())) {

                if (firstTalkLogic == null) firstTalkLogic = dtlogic;
                if (dtlogic.getName().equals(e.getMsg())) {
                    matchedTalkLogic = dtlogic;
                    break;
                }
            }
        }
        DefReaction defR = null;
        if (matchedTalkLogic != null) {
            defR = matchedTalkLogic.getDefReactionByTriggerName(e.getTrigger());
        }
        else if (firstTalkLogic != null) {
            defR = firstTalkLogic.getDefReactionByTriggerName(e.getTrigger());
        }

        if (defR != null) {
            if (e.getReact() == null) {
                //simple event, contains message directly
                sMsg = e.getMsg();
            }
            else {
                if (e.getReact().equals(Constants.CITY_REACT_TRIGGER_COMPLETE))
                  sMsg = defR.getReact().getConfirm().getComplete();
            }
        }
        return sMsg;
    }

    private void fillCityMessageTV(TableView<CityMessageTVItem> tvObj) {
        if (this.gameProcessor.getLstEvents().size() > 0) {
            List<Constants.GameEvent> lstRm = new ArrayList<Constants.GameEvent>();
            for (Constants.GameEvent ge: this.gameProcessor.getLstEvents()) {
                if (ge.getCity().equals(this.gameProcessor.getGameData().getCurrentCity().getName())) {
                    tvObj.getItems().add(
                        new CityMessageTVItem(ge.getType(), ge.getTrigger(), getEventMessage(ge)));
                    lstRm.add(ge);
                }
            }
            for (Constants.GameEvent e: lstRm) {
                this.gameProcessor.getLstEvents().remove(e);
            }
        }
    }

    private TableView<CityMessageTVItem> createCityMessageTableView() {
        TableView<CityMessageTVItem> tvObj = new TableView<>();
        tvObj.setRowFactory(tv -> {
            TableRow<CityMessageTVItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CityMessageTVItem rowData = row.getItem();
                    processCityMessageDblClick(rowData);
                    System.out.println("Double click on: "+rowData.toString());
                }
            });
            return row ;
        });

        TableColumn<CityMessageTVItem, String> tcol1 = new TableColumn<>("Time");
        tcol1.setCellValueFactory(new PropertyValueFactory<>("msgTime"));
        TableColumn<CityMessageTVItem, String> tcol2 = new TableColumn<>("Type");
        tcol2.setCellValueFactory(new PropertyValueFactory<>("msgType"));
        TableColumn<CityMessageTVItem, String> tcol3 = new TableColumn<>("Content");
        tcol3.setCellValueFactory(new PropertyValueFactory<>("msgContent"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.3));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);

        //when empty
        tvObj.setPlaceholder(new Label("No data"));

        TableViewSelectionModel<CityMessageTVItem> selectionModel = tvObj.getSelectionModel();
        // set selection mode to only 1 row
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        return tvObj;        
    }

    private void processGameNewEvent() {
        if (this.gameProcessor != null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("Text Game", "*." + Constants.TTG_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's TextGame File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            GameData gameData = this.gameModel.createGameFromTemplate(file.getPath());
            if (gameData != null) {
                doStartGame(gameData);
            }
        }

    }

    private void processGameLoadEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("Text Game", "*." + Constants.TTG_EXTENSION),
             new FileChooser.ExtensionFilter("TMP Files", "*.tmp")
        );
        Button btnFileChoose = new Button("Select File");
        btnFileChoose.setOnAction(onActEvent -> {
            File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
        });
        fileChooser.setTitle("Open Tien's TextGame File");
        File file = fileChooser.showOpenDialog(getPrimaryStage());
        if (file != null) {
            System.out.println("Selected file: " + file.getPath());
            //this.gameModel.loadGameJsonFromFile(file.getPath());
            //openGameFile(file);
        }
    }

    private void saveGameToFile(final String fileName, final String content) {
          System.out.println("Saving to file " + fileName);
          File fOutputFile = new File(fileName);
          final String sMessage = "Saved " + String.valueOf(content.length()) + " bytes to: " + fileName;

          try {
              PrintWriter pw = new PrintWriter(fOutputFile, Charset.forName("UTF-8"));
              pw.println(content);
              pw.flush(); //if not, PrintWriter may not write the whole data 
              //statusTableView.getItems().add(new ManagementStatusCityInfoTVItem(sMessage, fileName));
          } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
          }
          catch (IOException e) {
            System.out.println("saveGameToFile error: " + e.getMessage());
          }
          finally {
            showMessage("Work save", "Save complete!", sMessage);
          }
    }

    private void fncAniKeyFrame(final int step) {
    }

    private void fncMainKeyFrame(final int step) {
        if (this.gameProcessor == null) return;

        this.gameProcessor.clockTick();

        String sTickLbl = this.lblClockTicking.getText();
        if (sTickLbl.substring(0).equals("*")) {
            sTickLbl = String.format("**");
        }
        else sTickLbl = String.format("*");
        this.lblClockTicking.setText(sTickLbl);

        String sOldGameDay = this.lblGameDay.getText();
        String sNewGameDay = String.valueOf(this.gameProcessor.getGameDay());
        if (!sOldGameDay.equals(sNewGameDay)) {
            //change day
            this.lblGameDay.setText(sNewGameDay);
            fillCityInventoryListTV(this.tvCityInventory);
            fillCityBuildQueue(this.tvCityBuildQueue);
            fillCityMessageTV(this.tvCityMessage);
            fillCityBuildingTV(this.tvCityBuilding);
            updateCityInfoTV(this.tvCityInfo);
        }
    }

    private void doStartGame(GameData gd) {
        if (this.gameProcessor != null) return;

        this.gameProcessor = new GameProcessor(gd);

        this.currentCityDef = gd.getDefCityItems().get(0);
        this.gameProcessor.createNewCity(this.currentCityDef);
        this.currentCityName = gameProcessor.getGameData().getCurrentCity().getName();
        this.lblSelectedCityName.setText(this.currentCityName);
        fillCityInfoTV(this.tvCityInfo);
        fillCityInventoryListTV(this.tvCityInventory);

        if (this.tlMainTimeline == null) {
            this.tlMainTimeline = new Timeline(
               new KeyFrame(Duration.millis(100), evt -> fncAniKeyFrame(0)), //end duration event
               new KeyFrame(Duration.millis(1000), evt -> fncMainKeyFrame(1))
               );
            tlMainTimeline.setCycleCount(Animation.INDEFINITE);
        }
        tlMainTimeline.play();
    }

    private void doEndGame() {
        if (this.tlMainTimeline != null) tlMainTimeline.stop();
    }

    public void onStopShow() {}

    public void onShow() {}

}