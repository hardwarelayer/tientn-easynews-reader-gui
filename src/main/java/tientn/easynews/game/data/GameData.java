package tientn.easynews.game.data;

import java.lang.System;
import java.util.Dictionary;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

//Class for exporting / importing TFMT Work Data
@JsonRootName(value = "game-data")
public class GameData {
  @Getter private Dictionary<String, String> translations;
  @Getter private List<DefCity> defCityItems;
  @Getter private List<DefTalkLogic> defTalkLogicItems;
  @Getter private List<DefBuilding> defBuildingItems;

  private List<City> cities;
  @Getter @Setter private City currentCity = null;

  @Getter @Setter private String lastWorkDate;
  @Getter @Setter private int totalCities = 0;

  public GameData(final Dictionary<String, String> trans, 
    final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds) {
    this.translations = trans;
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;

    this.cities = new ArrayList<City>();
  }

  public GameData(final Dictionary<String, String> trans, 
  final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds,
    final List<City> cities)
  {
    this.translations = trans;
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;

    this.cities = cities;
  }

  public void setData(final Dictionary<String, String> trans, 
    final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds) {
    this.translations = trans;
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;
  }

  public void setData(final Dictionary<String, String> trans, 
    final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds,
    final List<City> cities)
  {
    this.translations = trans;
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;

    this.cities = cities;
  }

  public DefReaction getTalkLogicReact(final String type, final String reactionTrigger) {
    DefTalkLogic defTalkLogic = null;
    for (DefTalkLogic dtlogic: this.defTalkLogicItems) {
        if (dtlogic.getType().equals(type)) {
            defTalkLogic = dtlogic;
            break;
        }
    }
    if (defTalkLogic == null) return null;
    DefReaction defReact = null;
    for (DefReaction dreact: defTalkLogic.getReaction()) {
        if (dreact.getTrigger().equals(reactionTrigger)) {
            defReact = dreact;
            break;
        }
    }
    return defReact;
  }

  public String getTalkLogicReactMessage(final String type, final String reactionTrigger) {
    DefReaction defReact = this.getTalkLogicReact(type, reactionTrigger);
    if (defReact == null) return null;
    return defReact.getReact().getMessage();
  }

  public void addCity(final City city) {
    if (this.cities == null) return;
    this.cities.add(city);
  }

  public List<City> getCities() {
    return this.cities;
  }

  public City getCityByLocation(final int x, final int y) {
    int idx = x+(y*Constants.MAP_HORZ_TILES);
    City cty = this.cities.get(idx);
    if (cty.getX() == x && cty.getY() == y)
      return cty;
    else {
      System.out.println("getCityByLocation: scanning");
      for (City c: this.cities) {
        if (c.getX() == x && c.getY() == y)
          return c;
        }
    }
    return null;
  }
}