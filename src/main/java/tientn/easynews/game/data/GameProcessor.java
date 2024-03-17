package tientn.easynews.game.data;

import java.lang.System;
import java.util.UUID;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
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

  public City createNewCity(final DefCity def) {
    City newCity = new City(def, 
      new ArrayList<CityBuildQueueItem>(), new ArrayList<CityProductionItem>());
    newCity.setOwner("human1");
    this.gameData.addCity(newCity);
    this.gameData.setCurrentCity(newCity);
    return newCity;
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
    updateProduction();
    updateBuildQueue();
    updatePopulation();
  }

  private void addItemToInventory(final City city, final String itemName, final int qty) {
    CityInventoryCapability itemCap = null;
    CityInventoryItem itemQty = null;
    for (CityInventoryCapability invCap: city.getInventoryCapability()) {
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
  }

  private void updatePopulation() {
    City city = this.getGameData().getCurrentCity();
    if (city == null) return;

    int popAllowedByHouse = 0;

    for (CityBuilding bld: city.getBuildingItem()) {
      List<DefBuildingProduce> produceLst = bld.getProduceLst();
      for (DefBuildingProduce bp: produceLst) {
        if (bp.getType().equals(Constants.CITY_BUILDING_PRODUCE_TYPE1)) {
          popAllowedByHouse += (bp.getQuantity()*bld.getQty());
        }
      }
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
      this.addEvent(Constants.CITY_POP_ACTION_TYPE, Constants.CITY_POP_REACT_TRIGGER_DECREASE,
        null, this.getGameData().getTalkLogicReactMessage(Constants.CITY_POP_ACTION_TYPE, Constants.CITY_POP_REACT_TRIGGER_DECREASE));
    }
    city.setPop(newPop);

  }

  private void addEvent(final String type, final String trigger, final String react, final String msg) {
    this.lstEvents.add(new Constants.GameEvent(
      this.gameData.getCurrentCity().getName(),
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

  private int getCityFoodProduceCap(final City city) {
    DefCity defCity = city.getDef();
    DefCityCapability defCityCap = defCity.getCapability();
    int foodProduced = defCityCap.getLand() + (defCityCap.getFarm()*2);
    return foodProduced;
  }

  private int getCityFoodProduce(final City city) {
    int foodProduced = getCityFoodProduceCap(city);
    int foodSpent = city.getPop();
    return foodProduced - foodSpent;
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

    City currentCity = this.getGameData().getCurrentCity();
    addItemToInventory(currentCity, Constants.CITY_BASIC_MATERIAL_1, food);
    addItemToInventory(currentCity, Constants.CITY_BASIC_MATERIAL_2, wood);
    addItemToInventory(currentCity, Constants.CITY_BASIC_MATERIAL_3, iron);
    addItemToInventory(currentCity, Constants.CITY_BASIC_MATERIAL_4, stone);
    addItemToInventory(currentCity, Constants.CITY_BASIC_MATERIAL_5, clay);
  }

  private void updateCityBuildProcess(final City city) {
    if (city == null) return;

    List<Integer> lstRemovable = new ArrayList<Integer>();

    for (int i = 0; i < city.getBuildQueue().size(); i++) {
      CityBuildQueueItem bldItem = city.getBuildQueue().get(i);
      if (this.gameTimeBySecond - bldItem.getStartTime() < Constants.GAME_TICK_PER_DAY) {
        //will not increase date if total seconds not equals one day
      }
      else {
        bldItem.setDayCount(bldItem.getDayCount()+1);
        if (bldItem.getDayCount() >= bldItem.getDayTotal()) {
          lstRemovable.add(i);
          this.processCompleteBuild(bldItem);
        }
      }
    }
    for (Integer iRm: lstRemovable) {
      city.getBuildQueue().remove((int) iRm);
    }

  }

  private void processOnetimeProduceAfterComplete(CityBuilding bld) {
    City city = this.gameData.getCurrentCity();
    if (city == null) return;

  }

  private void processCompleteBuild(CityBuildQueueItem bld) {
    CityBuilding cityBld = new CityBuilding(
      bld.getItemType(), bld.getItemName(), 
      bld.getDef().getValue(), bld.getDef().getValueRange(), bld.getDef().getRequireClass(),
      bld.getDef().getBonusClass(), bld.getDef().getCostLst(), bld.getDef().getProduceLst(),
      bld.getDef().getReaction()
      );
    boolean bFoundBld = false;
    for (CityBuilding cBld: this.gameData.getCurrentCity().getBuildingItem()) {
      if (cityBld.getName().equals(cBld.getName())) {
        cBld.setQty(cBld.getQty()+1);
        processOnetimeProduceAfterComplete(cBld);
        bFoundBld = true;
        break;
      }
    }
    if (!bFoundBld) {
      this.gameData.getCurrentCity().getBuildingItem().add(cityBld);
      processOnetimeProduceAfterComplete(cityBld);
    }
    this.addEvent(Constants.CITY_BUILDER_ACTION_TYPE, 
      Constants.CITY_BUILDING_REACT_TRIGGER_BUILD,
      Constants.CITY_REACT_TRIGGER_COMPLETE, 
      bld.getItemType());
  }

  private void updateProduction() {
    //update current day's produced item list
    if (this.getGameData().getCurrentCity() != null) {
      updateCityProductionOutput(this.getGameData().getCurrentCity());
    }
  }

  private void updateBuildQueue() {
    //use talk trigger of "builder" for completion (Constants.GAME_BUILDER_UNIT map to definition name )
    if (this.getGameData().getCurrentCity() != null) {
      updateCityBuildProcess(this.getGameData().getCurrentCity());
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