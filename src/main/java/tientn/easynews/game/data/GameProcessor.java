package tientn.easynews.game.data;

import java.lang.System;
import java.util.UUID;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.StringBuilder;
import java.time.Instant;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.net.URL;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

//Class for exporting / importing TFMT Work Data
@JsonRootName(value = "game-data")
public class GameProcessor {

  @Getter private GameData gameData;

  @Getter private long epocStartTime = 0;
  @Getter private long gameTimeBySecond = 0;
  @Getter private long lastGameDayChangeSecond = 0;
  @Getter private long gameDay = 0;
  @Getter private List<Constants.GameEvent> lstEvents;

  public GameProcessor(final GameData gd) {
    this.gameData = gd;

    this.epocStartTime = java.time.Instant.now().getEpochSecond();
    this.gameTimeBySecond = 0;
    this.lastGameDayChangeSecond = 0;
    this.gameDay = 0;
    this.lstEvents = new ArrayList<Constants.GameEvent>();
  }

  public void setData(final GameData gd) {
    this.gameData = gd;
  }

  private int randomVal(final int from, final int to) {
      Random rnd = new Random();
      return rnd.nextInt(to-from) + from;
  }

  public City createNewCity(final DefCity def, final String owner, final int x, final int y) {
    City newCity = new City(def, 
      new ArrayList<CityBuildQueueItem>(), new ArrayList<CityProductionItem>(), x, y);
    newCity.setOwner(owner);
    return newCity;
  }

  public void generateCities() {
    int humanCityX = randomVal(0, Constants.MAP_HORZ_TILES);
    int humanCityY = randomVal(0, Constants.MAP_VERT_TILES);
    System.out.println("Human city: "+String.valueOf(humanCityX)+" " + String.valueOf(humanCityY));
    for (int r = 0; r < Constants.MAP_VERT_TILES; r++) {
      for (int c = 0; c < Constants.MAP_HORZ_TILES; c++) {
        int cityTemplateIndex = randomVal(0, gameData.getDefCityItems().size()-1);
        DefCity def = gameData.getDefCityItems().get(cityTemplateIndex);
        City newCity = createNewCity(def, Constants.GAME_PLAYER2, c, r);        
        if (c == humanCityX && r == humanCityY) {
          newCity.setOwner(Constants.GAME_PLAYER1);
          newCity.setName("Boom Town");
          this.gameData.setCurrentCity(newCity);
        }
        else {
          newCity.setName("abandoned");
        }
        this.gameData.addCity(newCity);
      }
    }
  }

  public void clockTick() {
    this.gameTimeBySecond = java.time.Instant.now().getEpochSecond() - this.epocStartTime;

    if (this.gameTimeBySecond - this.lastGameDayChangeSecond > Constants.GAME_TICK_PER_DAY) {
      increaseDate();
      this.lastGameDayChangeSecond = this.gameTimeBySecond;
    }
  }

  private void increaseDate() {
    this.gameDay++;
    for (City c: this.gameData.getCities()) {
      updateProductionByCity(c);
      updateBuildQueueByCity(c);
      updatePopulationByCity(c);
    }
  }

  private void addItemToInventory(final City city, final String itemName, final int qty) {
    CityInventoryCapability itemCap = null;
    CityInventoryItem itemQty = null;
    int maxCap = 0;
    for (CityInventoryCapability invCap: city.getInventoryCapability()) {
      if (maxCap < invCap.getQuantity()) maxCap = invCap.getQuantity();
      if (invCap.getName().equals(itemName)) {
        itemCap = invCap;
        break;
      }
    }
    for (CityInventoryItem inv: city.getInventoryItem()) {
      if (inv.getName().equals(itemName)) {
        itemQty = inv;
        break;
      }
    }

    if (itemCap != null && itemQty != null) {
      int currentQty = itemQty.getQuantity();
      if (currentQty + qty <= itemCap.getQuantity())
        itemQty.setQuantity(currentQty + qty);
      else
        itemQty.setQuantity(itemCap.getQuantity());
    }
    else {
      //add new to stock
      city.getInventoryCapability().add(new CityInventoryCapability(itemName, maxCap));
      city.getInventoryItem().add(new CityInventoryItem(itemName, qty));
    }
  }

  private int getDailyProduceOfBuilding(final CityBuilding bld, final String type) {
    int qty = 0;
    List<DefBuildingProduce> produceLst = bld.getProduceLst();
    for (DefBuildingProduce bp: produceLst) {
      if (bp.getType().equals(type) && bp.getSpeed() == 1/*daily*/) {
        qty += (bp.getQuantity() * bld.getQty());
      }
    }
    return qty;
  }

  private int getOneTimeProduceOfBuilding(final CityBuilding bld, final String type) {
    int qty = 0;
    List<DefBuildingProduce> produceLst = bld.getProduceLst();
    for (DefBuildingProduce bp: produceLst) {
      if (bp.getType().equals(type) && bp.getSpeed() == 0/*one time*/) {
        qty += (bp.getQuantity() * bld.getQty());
      }
    }
    return qty;
  }

  private void updatePopulationByCity(final City city) {
    if (city == null) return;

    int popAllowedByHouse = 0;

    for (CityBuilding bld: city.getBuildingItem()) {
      popAllowedByHouse += getOneTimeProduceOfBuilding(bld, Constants.CITY_BUILDING_PRODUCE_TYPE1);
    }

    int newPop = city.getPop();
    int minusFoodAmt = getMinusFoodInventory(city);
    if (minusFoodAmt == 0) {
      //I want to allow population increase when food is still in stock
      //not care about positive production qty.
      //So it will create pop loss event
      if (popAllowedByHouse > city.getPop())
        newPop = popAllowedByHouse;
    }
    else {
      newPop += this.getCityFoodProduce(city);
    }

    if (city.getPop() > newPop) {
      //pop decrease will cause security issue
      if (city.getSecurity() > 0) {
        city.setSecurity(city.getSecurity()-1);
      }
      this.addEvent(city, Constants.CITY_POP_ACTION_TYPE, Constants.CITY_POP_REACT_TRIGGER_DECREASE,
        null, this.getGameData().getTalkLogicReactMessage(Constants.CITY_POP_ACTION_TYPE, Constants.CITY_POP_REACT_TRIGGER_DECREASE));
    }
    city.setPop(newPop);

  }

  private void addEvent(final City city, final String type, final String trigger, final String react, final String msg) {
    this.lstEvents.add(new Constants.GameEvent(
      city.getName(),
      type, trigger, react, msg));
  }

  //return minus value
  private int getMinusFoodInventory(final City city) {
    for (CityInventoryItem inv: city.getInventoryItem()) {
      if (inv.getName().equals(Constants.CITY_BASIC_MATERIAL_1)) {
        if (inv.getQuantity() < 0) {
          //pop will reduce
          return inv.getQuantity();
        }
      }
    }
    return 0;
  }

  private int getCityDefaultFoodProduce(final City city) {
    DefCity defCity = city.getDef();
    DefCityCapability defCityCap = defCity.getCapability();
    int foodProduced = defCityCap.getLand() + (defCityCap.getFarm()*2); //this is farmland
    return foodProduced;
  }

  private int getCityFarmOutput(final City city) {
    int qty = 0;
    for (CityBuilding bld: city.getBuildingItem()) {
      qty += getDailyProduceOfBuilding(bld, Constants.CITY_BUILDING_PRODUCE_TYPE3);
    }
    return qty;
  }

  private int getCityFoodProduce(final City city) {
    int foodProduced = getCityDefaultFoodProduce(city);
    foodProduced += getCityFarmOutput(city);
    int foodSpent = city.getPop();
    return foodProduced - foodSpent;
  }

  public void addProduceQueueToBuilding(final City city, final String sBldName) {
    for (CityBuilding bld: city.getBuildingItem()) {
      if (bld.getName().equals(sBldName) && bld.getProduceLst().size() > 0) {
        for (DefBuildingProduce prod: bld.getProduceLst()) {
          if (prod.getSpeed() > 0) {
            for (int i = 0; i < Constants.CITY_BUILDING_PRODUCE_DEFAULT_ORDER_QTY; i++) 
              bld.getQueueLst().add(
                new CityBuildingProduceItem(prod.getType(), 1)
              );
          }
        }
      }
    }
  }

  public List<String> getProduceableBuilding(final City city) {
    List<String> lstRes = new ArrayList<String>();
    for (CityBuilding bld: city.getBuildingItem()) {
      if (bld.getProduceLst().size() > 0) {
        for (DefBuildingProduce prod: bld.getProduceLst()) {
          if (prod.getSpeed() > 0) {
            lstRes.add(bld.getName());
          }
        }
      }
    }
    return lstRes;
  }

  private void updateCityProductionOutput(final City city) {
    if (city == null) return;

    DefCity defCity = city.getDef();
    DefCityCapability defCityCap = defCity.getCapability();
    int food = getCityFoodProduce(city);
    int iron = defCityCap.getIron();
    int stone = defCityCap.getStone();
    int clay = defCityCap.getClay();
    int wood = defCityCap.getLand();

    addItemToInventory(city, Constants.CITY_BASIC_MATERIAL_1, food);
    addItemToInventory(city, Constants.CITY_BASIC_MATERIAL_2, wood);
    addItemToInventory(city, Constants.CITY_BASIC_MATERIAL_3, iron);
    addItemToInventory(city, Constants.CITY_BASIC_MATERIAL_4, stone);
    addItemToInventory(city, Constants.CITY_BASIC_MATERIAL_5, clay);

    //calculate consume and produce by buildings
    for (CityBuilding bld: city.getBuildingItem()) {
      if (bld.getQueueLst().size() < 1) continue;

      //check is the production consuming materials enough
      //if not enough, building stop producing in this turn
      boolean bConsumeOK = true;
      if (bld.getConsumeLst().size() > 0) {
        //deduct inventory
        for (DefBuildingConsume cons: bld.getConsumeLst()) {
          boolean bFoundInvForCons = false;
          for (CityInventoryItem inv: city.getInventoryItem()) {
            if (inv.getName().equals(cons.getType())) {
              bFoundInvForCons = true;
              if (inv.getQuantity() < (cons.getQuantity()*bld.getQty())) {
                bConsumeOK = false;
                break;
              }
            }
          }
          if (!bFoundInvForCons) bConsumeOK = false;
          if (!bConsumeOK) break;
        }
      }

      if (!bConsumeOK) {
          this.addEvent(city, Constants.CITY_PRODUCE_ACTION_TYPE, 
            Constants.CITY_BUILDING_REACT_TRIGGER_PRODUCE,
            null, 
            bld.getName());
          continue; //not enough material for consuming, skip producing in this building
      }

      //real deduction here, may be during this another process deducted the inventory, but we accept it
      for (DefBuildingConsume cons: bld.getConsumeLst()) {
        for (CityInventoryItem inv: city.getInventoryItem()) {
          if (inv.getName().equals(cons.getType())) {
            if (inv.getQuantity() >= (cons.getQuantity()*bld.getQty())) {
              inv.setQuantity(inv.getQuantity()-(cons.getQuantity()*bld.getQty()));
            }
          }
        }
      }

      if (bld.getProduceLst().size() > 0) {
        //increase inventory
        for (DefBuildingProduce prod: bld.getProduceLst()) {
          boolean foundInvForProd = false;
          for (CityInventoryItem inv: city.getInventoryItem()) {
            if (prod.getSpeed() == 1) {
              if (inv.getName().equals(prod.getType())) {
                inv.setQuantity(inv.getQuantity()+(prod.getQuantity()*bld.getQty()));
                foundInvForProd = true;
                break;
              }
            }
          }
          if (!foundInvForProd) {
            addItemToInventory(city, prod.getType(), (prod.getQuantity()*bld.getQty()));
          }
        }
      }

      if (bld.getQueueLst().size() > bld.getQty())
        bld.getQueueLst().remove(bld.getQueueLst().size()-bld.getQty()); //outputed, remove one item
      else
        bld.getQueueLst().clear();
    }
  }

  private void updateCityBuildProcess(final City city) {
    if (city == null) return;

    List<CityBuildQueueItem> lstRemovable = new ArrayList<CityBuildQueueItem>();

    for (int i = 0; i < city.getBuildQueue().size(); i++) {
      CityBuildQueueItem bldItem = city.getBuildQueue().get(i);
      if (this.gameTimeBySecond - bldItem.getStartTime() < Constants.GAME_TICK_PER_DAY) {
        //will not increase date if total seconds not equals one day
      }
      else {
        bldItem.setDayCount(bldItem.getDayCount()+1);
        if (bldItem.getDayCount() >= bldItem.getDayTotal()) {
          lstRemovable.add(bldItem);
          this.processCompleteBuild(city, bldItem);
        }
      }
    }
    for (CityBuildQueueItem iRm: lstRemovable) {
      city.getBuildQueue().remove(iRm);
    }

  }

  private void processOnetimeProduceAfterComplete(final City city, final CityBuilding bld) {
    if (city == null) return;

    if (bld.getType().equals(Constants.CITY_BUILDING_WAREHOUSE_TYPE)) {
      int newStorage = getOneTimeProduceOfBuilding(bld, Constants.CITY_BUILDING_PRODUCE_TYPE2);
      city.resetAllInvCap(newStorage);
    }
    else if (bld.getType().equals(Constants.CITY_BUILDING_STOCKADE_TYPE)) {
      int newDefense = getOneTimeProduceOfBuilding(bld, Constants.CITY_BUILDING_PRODUCE_TYPE7);
      city.setDefense(newDefense);
    }

  }

  private void processCompleteBuild(final City city, final CityBuildQueueItem bld) {
    CityBuilding cityBld = new CityBuilding(
      bld.getItemType(), bld.getItemName(), 
      bld.getDef().getValue(), bld.getDef().getValueRange(), bld.getDef().getRequireClass(),
      bld.getDef().getBonusClass(), bld.getDef().getCostLst(), 
      bld.getDef().getConsumeLst(), bld.getDef().getProduceLst(),
      bld.getDef().getReaction()
      );
    boolean bFoundBld = false;
    for (CityBuilding cBld: city.getBuildingItem()) {
      if (cityBld.getName().equals(cBld.getName())) {
        cBld.setQty(cBld.getQty()+1);
        processOnetimeProduceAfterComplete(city, cBld);
        bFoundBld = true;
        break;
      }
    }
    if (!bFoundBld) {
      city.getBuildingItem().add(cityBld);
      processOnetimeProduceAfterComplete(city, cityBld);
    }
    this.addEvent(city, Constants.CITY_BUILDER_ACTION_TYPE, 
      Constants.CITY_BUILDING_REACT_TRIGGER_BUILD,
      Constants.CITY_REACT_TRIGGER_COMPLETE, 
      bld.getItemType());
  }

  private void updateProductionByCity(final City city) {
    //update current day's produced item list
    if (city != null) {
      updateCityProductionOutput(city);
    }
  }

  private void updateBuildQueueByCity(final City city) {
    //use talk trigger of "builder" for completion (Constants.GAME_BUILDER_UNIT map to definition name )
    if (city != null) {
      updateCityBuildProcess(city);
    }

  }

  public void startBuildBuilding() {
    //check inventory
    //check queue
    //use trigger of "builder" for start build (Constants.GAME_BUILDER_UNIT map to definition name )
  }

  public void startUpgradeBuilding() {
    //check inventory
    //check queue
    //use trigger of "builder" for start upgrade (Constants.GAME_BUILDER_UNIT map to definition name )
  }

}