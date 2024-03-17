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
public class GameData {
  @Getter private List<DefCity> defCityItems;
  @Getter private List<DefTalkLogic> defTalkLogicItems;
  @Getter private List<DefBuilding> defBuildingItems;

  @Getter private List<City> cities;
  @Getter @Setter private City currentCity = null;

  @Getter @Setter private String lastWorkDate;
  @Getter @Setter private int totalCities = 0;

  public GameData(final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds) {
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;

    this.cities = new ArrayList<City>();
  }

  public GameData(final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds,
    final List<City> cities)
  {
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;

    this.cities = cities;
  }

  public void setData(final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds) {
    this.defCityItems = defCities;
    this.defTalkLogicItems = defTalkLogics;
    this.defBuildingItems = defBlds;
  }

  public void setData(final List<DefCity> defCities, final List<DefTalkLogic> defTalkLogics, final List<DefBuilding> defBlds,
    final List<City> cities)
  {
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

}