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
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

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
import java.util.Random;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
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
    Canvas mapCanvas;

    Timeline tlMainTimeline = null;

    ContextMenu currentContextMenu = null;
    Menu cmiAdmin = null;
    Menu acmAdminProduce = null;

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

        Label label = new Label(Constants.GUI_LABEL_SELECTED_CITY);
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

        EventHandler<ActionEvent> fncSaveGameClick = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //label.setText("Accepted");
                processGameSaveEvent();
            }
        };
        Button btnSaveGame = createButton("Save", fncSaveGameClick);

        HBox hbControlCommands = new HBox(btnNewGame, btnSaveGame, btnLoadGame);
        HBox hbControlInfo = new HBox(label, this.lblSelectedCityName, 
            new Label("Game Day:"), this.lblGameDay,
            this.lblClockTicking);
        HBox hbControlPane = new HBox(hbControlCommands, hbControlInfo);

        this.tvCityInfo = createCityInfoTableView();
        this.tvCityInventory = createCityInventoryTableView();
        this.tvCityBuildQueue = createCityBuildQueueTableView();
        this.tvCityBuilding = createCityBuildingTableView();
        this.tvCityMessage = createCityMessageTableView();
        this.tvCityProduction = createCityProductionTableView();

        this.mapCanvas = new Canvas(850, 250);
        StackPane canvasContainer = new StackPane(mapCanvas);
        //canvasContainer.getStyleClass().add("canvas");
        
        this.tvCityInfo.setStyle("-fx-font-size: 16px");
        this.tvCityInventory.setStyle("-fx-font-size: 16px");
        this.tvCityBuilding.setStyle("-fx-font-size: 16px");
        this.tvCityMessage.setStyle("-fx-font-size: 16px");
        this.tvCityBuildQueue.setStyle("-fx-font-size: 16px");
        this.tvCityProduction.setStyle("-fx-font-size: 16px");

        this.tvCityInfo.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.55));
        this.tvCityInventory.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.25));
        this.tvCityBuildQueue.prefHeightProperty().bind(getPrimaryStage().heightProperty().multiply(0.2));

        VBox leftPane = new VBox(tvCityInfo, tvCityInventory, tvCityBuildQueue);
        GridPane rightMidPane = new GridPane();
        ColumnConstraints rcol1 = new ColumnConstraints();
        rcol1.setPercentWidth(50);
        ColumnConstraints rcol2 = new ColumnConstraints();
        rcol2.setPercentWidth(50);
        rightMidPane.getColumnConstraints().addAll(rcol1, rcol2);
        rightMidPane.add(tvCityBuilding, 0, 0);
        rightMidPane.add(tvCityProduction, 1, 0);
        VBox rightPane = new VBox(canvasContainer, rightMidPane, tvCityMessage);

        GridPane gpDetail = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(75);
        gpDetail.getColumnConstraints().addAll(col1, col2);
        gpDetail.add(leftPane, 0, 0);
        gpDetail.add(rightPane, 1, 0);

        this.add(hbControlPane, 0, 0);
        this.add(gpDetail, 0, 1);

        drawMap(mapCanvas);
        //GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        //drawShapes(gc);

        mapCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, 
        new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                }
                else if (t.getButton() == MouseButton.PRIMARY) { // && t.getClickCount() > 1) {
                    processMapClick(t.getX(), t.getY());
                }
            }
        });

        /* 
        tvCityInfo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    processContextMenuShow(tvCityInfo, t.getScreenX(), t.getScreenY());
                }
                else if (t.getButton() == MouseButton.PRIMARY) {
                }
            }
        });
*/
    }

/*
    private void processContextMenuShow(Node anchor, final double dblX, final double dblY) {
        if (this.gameProcessor == null) return;

        if (this.currentContextMenu == null) {
            this.currentContextMenu = createContextMenu();
            this.currentContextMenu.setAutoHide(true);
        }

        this.currentContextMenu.show(anchor, dblX, dblY);
    }
 */

    private void selectMapTile(final int x, final int y) {
        selectCity(x, y);
    }

    private void processMapClick(final double x, final double y) {
        //System.out.println("X: "+String.valueOf(x) + " Y:" + String.valueOf(y) );
        int xTile = (int) (x/Constants.TILE_WIDTH);
        int yTile = (int) (y/Constants.TILE_HEIGHT);
        //System.out.println("X: "+String.valueOf(xTile) + " Y:" + String.valueOf(yTile) );
        selectMapTile(xTile, yTile);
    }

    private void drawCity(GraphicsContext gc, final String type, final int x, final int y) {

        if (type.equals(Constants.GAME_PLAYER1)) {
            gc.setFill(Color.LIGHTSALMON);
            gc.fillRoundRect(
                ((x+1)*Constants.TILE_WIDTH)-((int)(Constants.TILE_WIDTH/2)+(int)(Constants.CITY_ICON_WIDTH/2)),
                ((y+1)*Constants.TILE_HEIGHT)-((int)(Constants.TILE_HEIGHT/2)+(int)(Constants.CITY_ICON_HEIGHT/2)),
                Constants.CITY_ICON_WIDTH, Constants.CITY_ICON_HEIGHT, 2, 2);
        }
        else {
            PixelWriter pr = gc.getPixelWriter();
            pr.setColor(
                ((x+1)*Constants.TILE_WIDTH)-(int)(Constants.TILE_WIDTH/2),
                ((y+1)*Constants.TILE_HEIGHT)-(int)(Constants.TILE_HEIGHT/2),
                Color.GREEN
            );
        }

    }

    private void drawMap(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(1.0);

        gc.setStroke(Color.GRAY);
        for (int col = 0; col < Constants.MAP_HORZ_TILES; col++) {
            gc.moveTo(col*Constants.TILE_WIDTH, 0);
            gc.lineTo(col*Constants.TILE_WIDTH, Constants.MAP_HEIGHT);
            gc.stroke();
        }
        for (int row = 0; row < Constants.MAP_VERT_TILES; row++) {
            gc.moveTo(0, row*Constants.TILE_HEIGHT);
            gc.lineTo(Constants.MAP_WIDTH, row*Constants.TILE_HEIGHT);
            gc.stroke();
        }

        if (this.gameProcessor != null && this.gameProcessor.getGameData().getCurrentCity() != null) {
            for (City c: this.gameProcessor.getGameData().getCities()) {
                drawCity(gc, c.getOwner(), c.getX(), c.getY());
            }
        }
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                       new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                         new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                          new double[]{210, 210, 240, 240}, 4);
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
        if (cmiBuilds != null)
            cmObj.getItems().add(cmiBuilds);

        this.cmiAdmin = createAdminMenu();
        cmObj.getItems().add(cmiAdmin);
        SeparatorMenuItem sep = new SeparatorMenuItem();
        cmObj.getItems().add(sep);
        Menu cmiMil = createMillitaryMenu();
        if (cmiMil != null)
            cmObj.getItems().add(cmiMil);
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        cmObj.getItems().add(sep2);

        cmObj.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu mnu = (ContextMenu) event.getSource();
                    int iCount = mnu.getItems().size();
                    for (int i = 0; i < iCount; i++) {
                        if (mnu.getItems().get(i) instanceof SeparatorMenuItem)
                            continue;

                        if (mnu.getItems().get(i) instanceof MenuItem) {
                            Menu m = (Menu) mnu.getItems().get(i);
                            System.out.println("this  is a Menu: " + m.getText());
                            if (m.getItems().size() > 0) {
                                for (int iSub = 0; iSub < m.getItems().size(); iSub++) {
                                    System.out.println("    " + m.getItems().get(iSub).getText());
                                }
                            }
                        }
                    }
                    event.consume();
                }
            }
        });

        return cmObj;
    }

    private void updateContextMenuProduceCommands() {
        List<String> lstProduceable = this.gameProcessor.getProduceableBuilding(this.gameProcessor.getGameData().getCurrentCity());
        if (lstProduceable.size() > 0 && this.cmiAdmin != null) {
            if (this.acmAdminProduce == null) {
                Menu mnuProduce = createMenuAdminProduceBuilding();
                if (mnuProduce != null && this.cmiAdmin != null) {
                    this.cmiAdmin.getItems().add(mnuProduce);
                    this.acmAdminProduce = mnuProduce;
                }
            }
            else {
                if (this.acmAdminProduce.getItems().size() != lstProduceable.size()) {
                    //recreate
                    createMenuAdminProduceBuilding();
                }
            }
        }
    }

    private Menu createBuildMenu() {
        if (this.gameProcessor == null) return null;

        Menu acmMi1 = new Menu(transWord(Constants.GUI_LABEL_CONSTRUCTION));

        for (DefBuilding bd : this.gameProcessor.getGameData().getDefBuildingItems()) {
            MenuItem sub1 = new MenuItem(transWord(bd.getName()));

            sub1.setOnAction((ActionEvent event) -> {
                this.processBuildCommand(bd.getName());
            });
    
            acmMi1.getItems().add(sub1);
        }
        return acmMi1;
    }

    private Menu createMillitaryMenu() {
        if (this.gameProcessor == null) return null;

        Menu acmMi1 = new Menu(transWord(Constants.GUI_LABEL_MILLITARY));
        MenuItem sub1 = new MenuItem(transWord(Constants.GUI_LABEL_MIL_PATROL));

        sub1.setOnAction((ActionEvent event) -> {
            this.processMilCommand(Constants.GUI_LABEL_MIL_PATROL);
        });
        acmMi1.getItems().add(sub1);
        return acmMi1;
    }

    private Menu createAdminMenu() {
        if (this.gameProcessor == null) return null;

        Menu acmAdmin = new Menu(transWord(Constants.GUI_LABEL_ADMINISTRATION));
        if (this.acmAdminProduce == null) {
            Menu mnuProduce = createMenuAdminProduceBuilding();
            if (mnuProduce != null) {
                acmAdmin.getItems().add(mnuProduce);
                this.acmAdminProduce = mnuProduce;
            }
        }
        return acmAdmin;
    }

    private Menu createMenuAdminProduceBuilding() {
        Menu mnu = null;
        if (this.acmAdminProduce == null)
          mnu = new Menu(transWord(Constants.GUI_LABEL_ADMIN_PRODUCE));
        else
          mnu = this.acmAdminProduce;
        
        mnu.getItems().clear();

        List<String> lstProduceable = this.gameProcessor.getProduceableBuilding(this.gameProcessor.getGameData().getCurrentCity());
        if (lstProduceable.size() < 1) {
            return null;
        }
        for (String sBldName: lstProduceable) {
            MenuItem sub1 = new MenuItem(sBldName);
            sub1.setOnAction((ActionEvent event) -> {
                this.processAdminBuildingProduceCommand(sBldName);
            });
            mnu.getItems().add(sub1);
        }
        return mnu;
    }

    private void processAdminBuildingProduceCommand(final String sBldName) {
        if (this.gameProcessor == null) return;

        DefBuilding bldDef = getDefBuilding(sBldName);
        if (bldDef == null) return;

        DefReaction react = getBuildingDefReact(bldDef, Constants.CITY_BUILDING_PRODUCE);
        if (react != null) {
            String msg = String.format("%s\n%s", 
                react.getReact().getConfirm().getPrompt(),
                getBuildingProduceConsumeString(this.gameProcessor.getGameData().getCurrentCity(), bldDef)
            );
            showMessage("Start building produce", "Produce", msg);
        }
        this.gameProcessor.addProduceQueueToBuilding(this.gameProcessor.getGameData().getCurrentCity(), sBldName);
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

    private String getBuildingProduceConsumeString(City curCity, DefBuilding bld) {
        StringBuilder sb = new StringBuilder();
        sb.append(transWord("Consume"));
        sb.append(":");
        for (DefBuildingConsume bldCons: bld.getConsumeLst()) {
            if (bldCons.getQuantity() < 1) continue; //skip it

            sb.append(" ");
            sb.append(transWord(bldCons.getType()));
            sb.append(": ");
            sb.append(String.valueOf(bldCons.getQuantity()*Constants.CITY_BUILDING_PRODUCE_DEFAULT_ORDER_QTY));
            sb.append("/");
            sb.append(String.valueOf(curCity.getInvQty(bldCons.getType())));
        }
        return sb.toString();
    }

    private String getBuildingCostString(City curCity, DefBuilding bld) {
        StringBuilder sb = new StringBuilder();
        sb.append(transWord("Cost"));
        sb.append(":");
        for (DefBuildingCostType bldCost: bld.getCostLst()) {
            if (bldCost.getQuantity() < 1) continue; //skip it

            sb.append(" ");
            sb.append(transWord(bldCost.getType()));
            sb.append(": ");
            sb.append(String.valueOf(bldCost.getQuantity()));
            sb.append("/");
            if (bldCost.getType().equals(Constants.CITY_BUILD_BLOCK_NAME))
                sb.append(String.valueOf(curCity.getFreeLandBlock()));
            else if (bldCost.getType().equals(Constants.CITY_BUILD_TIME_NAME)) 
                sb.append(String.valueOf(bldCost.getQuantity()));
            else
                sb.append(String.valueOf(curCity.getInvQty(bldCost.getType())));
        }
        return sb.toString();
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
                if (bldCost.getQuantity() < 1) continue; //skip it
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
                showMessageWithStyle("Cannot build!", 
                    String.format("%s\n%s", 
                        defReact.getReact().getConfirm().getCondition(), 
                        this.getBuildingCostString(curCity, bld)),
                    "game_dialog.css");  
                return;
            }
            else {
                if (!showQuestionWithStyle("Are you sure to spend resources?", 
                    String.format("%s\n%s", 
                        defReact.getReact().getConfirm().getCondition(), 
                        this.getBuildingCostString(curCity, bld)),
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

    private DefReaction getBuildingDefReact(DefBuilding bldDef, final String trigger) {
        for (DefReaction react: bldDef.getReaction()) {
            if (react.getTrigger().equals(trigger)) {
                return react;
            }
        }
        return null;
    }

    private void processBuildCommand(final String buildingName) {
        DefBuilding objBuildDef = getDefBuilding(buildingName);
        if (objBuildDef == null) return;

        processCityLogics(
            Constants.CITY_BUILDER_ACTION_TYPE,
            Constants.CITY_BUILDING_REACT_TRIGGER_BUILD, 
            objBuildDef);
    }

    private void processMilCommand(final String cmdName) {
    }

    private void processTableViewDblClick(TableViewItem rowData) {
        if (this.lblSelectedCityName != null)
          this.lblSelectedCityName.setText(rowData.toString());
        else
          System.out.println("label is null");
    }

    private TableView<TableViewItem> createProduceTableView() {
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

    private String transWord(final String word) {
        if (this.gameProcessor == null) return word;
        String sTrans = this.gameProcessor.getGameData().getTranslations().get(word);
        if (sTrans == null || sTrans.isEmpty()) return word;
        return sTrans;
    }

    private void updateCityInfoTV(TableView<CityInfoTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        String sPop = String.valueOf(curCity.getPop());
        String sFreeSpaces = String.valueOf(curCity.getFreeLandBlock());
        String sSecurity = String.valueOf(curCity.getSecurity());
        String sHappiness = String.valueOf(curCity.getHappiness());
        String sDefense = String.valueOf(curCity.getDefense());
        for (CityInfoTVItem cInf: tvObj.getItems()) {
            if (cInf.getItemGroup().equals(transWord(Constants.CITY_BASIC_INFO_POP))) {
                String sValue = cInf.getValue();
                if (!sPop.equals(sValue)) {
                    cInf.setValue(sPop);
                }
            }
            if (cInf.getItemGroup().equals(transWord(Constants.CITY_BASIC_INFO_SECURITY))) {
                String sValue = cInf.getValue();
                if (!sSecurity.equals(sValue)) {
                    cInf.setValue(sSecurity);
                }
            }
            if (cInf.getItemGroup().equals(transWord(Constants.CITY_BASIC_INFO_HAPPINESS))) {
                String sValue = cInf.getValue();
                if (!sHappiness.equals(sValue)) {
                    cInf.setValue(sHappiness);
                }
            }
            if (cInf.getItemGroup().equals(transWord(Constants.CITY_BASIC_INFO_DEFENSE))) {
                String sValue = cInf.getValue();
                if (!sDefense.equals(sValue)) {
                    cInf.setValue(sDefense);
                }
            }    
            if (cInf.getItemGroup().isEmpty() && cInf.getItem().equals(transWord(Constants.CITY_BASIC_INFO_FREE_SPACE))) {
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

        tvObj.getItems().clear();

        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_OWNER), "", String.valueOf(curCity.getOwner())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_LEVEL), "", String.valueOf(curCity.getLevel())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_POP), "", String.valueOf(curCity.getPop())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_TROOP), "", String.valueOf(curCity.getTroop())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_SECURITY), "", String.valueOf(curCity.getSecurity())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_HAPPINESS), "", String.valueOf(curCity.getHappiness())));
        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_DEFENSE), "", String.valueOf(curCity.getDefense())));

        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_CAPABILITY), "", ""));
        DefCityCapability cap = curCity.getDef().getCapability();
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_LAND), String.valueOf(cap.getLand())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_FREE_SPACE), String.valueOf(curCity.getFreeLandBlock())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_FARM), String.valueOf(cap.getFarm())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_IRON), String.valueOf(cap.getIron())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_STONE), String.valueOf(cap.getStone())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_CLAY), String.valueOf(cap.getClay())));
        tvObj.getItems().add(new CityInfoTVItem("", transWord(Constants.CITY_BASIC_INFO_CAP_WATER), String.valueOf(cap.getWater())));

        tvObj.getItems().add(new CityInfoTVItem(transWord(Constants.CITY_BASIC_INFO_COORDINATE), 
            String.valueOf(curCity.getX()), 
            String.valueOf(curCity.getY())));
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
        TableColumn<CityBuildingTVItem, String> tcol4 = new TableColumn<>("Status");
        tcol4.setCellValueFactory(new PropertyValueFactory<>("status"));

        tcol1.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.25));
        tcol2.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.25));
        tcol3.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.25));
        tcol4.prefWidthProperty().bind(tvObj.widthProperty().multiply(0.25));
        tcol1.setResizable(false);
        tcol2.setResizable(false);
        tcol3.setResizable(false);
        tcol4.setResizable(false);

        tvObj.getColumns().add(tcol1);
        tvObj.getColumns().add(tcol2);
        tvObj.getColumns().add(tcol3);
        tvObj.getColumns().add(tcol4);

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
        if (curCity == null) {
            tvObj.getItems().clear();
            return;
        }

        //because the city can be changed, so this list may contains items that 
        //not in the new city's inventory
        List<CityInfoTVItem> removalItems = new ArrayList<CityInfoTVItem>();
        for (CityInfoTVItem tvItem: tvObj.getItems()) {
            //current city has this item?
            boolean cityHasItem = false;
            for (CityInventoryItem invItem: curCity.getInventoryItem()) {
                if (transWord(tvItem.getItem()).equals(transWord(invItem.getName()))) {
                    cityHasItem = true;
                    break;
                }
            }
            if (!cityHasItem) removalItems.add(tvItem);
        }
        if (removalItems.size() > 0) {
            for (CityInfoTVItem itm: removalItems)
                tvObj.getItems().remove(itm);
        }

        for (CityInventoryItem invItem: curCity.getInventoryItem()) {
            Constants.QtyWithCap qtyCap = curCity.getInventoryItemWithCap(invItem.getName());
            if (qtyCap == null) continue;
            String sQtyWCap = qtyCap.toString();
            boolean foundItem = false;
            for (CityInfoTVItem tvItem: tvObj.getItems()) {
                if (transWord(tvItem.getItem()).equals(transWord(invItem.getName()))) {
                    if (!tvItem.getValue().equals(sQtyWCap))
                        tvItem.setValue(sQtyWCap);
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                //add new
                tvObj.getItems().add(new CityInfoTVItem("", transWord(invItem.getName()), sQtyWCap));
            }
        }
        tvObj.refresh();
    }

    private void fillCityBuildingTV(TableView<CityBuildingTVItem> tvObj) {
        if (this.gameProcessor == null) return;
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) {
            tvObj.getItems().clear();
            return;
        }

        if (curCity.getBuildingItem().size() < 1) {
            tvObj.getItems().clear();
            return;
        }

        for (CityBuilding bldItem: curCity.getBuildingItem()) {
            boolean foundItem = false;
            for (CityBuildingTVItem tvItem: tvObj.getItems()) {
                if (transWord(tvItem.getName()).equals(transWord(bldItem.getName()))) {
                    if (bldItem.isActiveProduce()) {
                        if (bldItem.getQueueLst().size() > 0) {
                            tvItem.setStatus(String.format("%s %d", 
                            transWord(Constants.CITY_BUILDING_STATUS_PRODUCE), bldItem.getQueueLst().size()*bldItem.getQty()));
                        }
                        else {
                            tvItem.setStatus(Constants.CITY_BUILDING_STATUS_IDLING);
                        }
                    }
                    if (tvItem.getQty() != bldItem.getQty())
                      tvItem.setQty(bldItem.getQty());
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                //add new
                tvObj.getItems().add(new CityBuildingTVItem(transWord(bldItem.getType()), 
                    transWord(bldItem.getName()), bldItem.getQty()));
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

        tvObj.getItems().clear();
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        if (curCity == null) return;

        for (CityBuildQueueItem buildQueueItem: curCity.getBuildQueue()) {
            tvObj.getItems().add(
                new CityBuildQueueTVItem(transWord(Constants.GUI_LABEL_BUILDING), transWord(buildQueueItem.getItemName()), 
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
                sMsg = String.format("%s %s", transWord(e.getMsg()), defR.getReact().getMessage());
            }
            else {
                if (e.getReact().equals(Constants.CITY_REACT_TRIGGER_COMPLETE))
                  sMsg = defR.getReact().getConfirm().getComplete();
            }
        }
        else {
            System.out.println("Cannot find getDefReactionByTriggerName " + e.getTrigger());
        }
        return sMsg;
    }

    private void fillCityMessageTV(TableView<CityMessageTVItem> tvObj) {
        if (this.gameProcessor.getLstEvents().size() < 1) {
            //tvObj.getItems().clear();
        }
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
            GameData gameData = this.gameModel.createGameFromTemplate(file);
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

    private void processGameSaveEvent() {
        if (this.gameProcessor == null || this.gameProcessor.getGameData() == null) return;

        String gmContent = this.gameModel.getJsonDataAsString(this.gameProcessor.getGameData());
        System.out.println(gmContent);
        saveGameToFile(this.gameModel.getCurrentGameSaveFile(), gmContent);
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
            updateContextMenuProduceCommands();
            fillCityMessageTV(this.tvCityMessage);
            fillCityBuildingTV(this.tvCityBuilding);
            updateCityInfoTV(this.tvCityInfo);
        }
    }

    private void selectCity(final int x, final int y) {
        City city = this.gameProcessor.getGameData().getCityByLocation(x, y);
        if (city == null) return;

        this.gameProcessor.getGameData().setCurrentCity(city);
        this.currentCityDef = this.gameProcessor.getGameData().getCurrentCity().getDef();
        this.currentCityName = gameProcessor.getGameData().getCurrentCity().getName();
        this.lblSelectedCityName.setText(this.currentCityName);
        fillCityInfoTV(this.tvCityInfo); //always fill, not update
        fillCityInventoryListTV(this.tvCityInventory);
        fillCityBuildQueue(this.tvCityBuildQueue);
        fillCityMessageTV(this.tvCityMessage);
        fillCityBuildingTV(this.tvCityBuilding);

        this.acmAdminProduce = null;
        this.currentContextMenu = createContextMenu();
        this.currentContextMenu.setAutoHide(true);

        tvCityInfo.setContextMenu(this.currentContextMenu);
    }

    private void doStartGame(GameData gd) {
        if (this.gameProcessor != null) return;

        this.gameProcessor = new GameProcessor(gd);

        this.gameProcessor.generateCities();
        City curCity = this.gameProcessor.getGameData().getCurrentCity();
        selectCity(curCity.getX(), curCity.getY());

        this.drawMap(this.mapCanvas);

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