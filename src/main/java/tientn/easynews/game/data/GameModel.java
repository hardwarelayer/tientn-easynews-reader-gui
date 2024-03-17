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
import java.util.Iterator;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.StringBuilder;
import java.time.Instant;
import java.text.SimpleDateFormat;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import tientn.easynews.game.data.Constants;
import java.sql.*;

public class GameModel {

  @Getter @Setter private int jCoin = 0;
  @Getter @Setter private String lastWorkDate;
  @Getter private boolean dataLoaded = false;
  private static final String DATE_FORMAT_NOW = "yyyy-MM-dd"; // HH:mm:ss";

  public GameModel() {
    initData();
  }

  private void initData() {
    this.dataLoaded = false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    /*
    for (int i = 0; i < this.dataKanjiItems.size(); i++) {
      JBGKanjiItem item = this.dataKanjiItems.get(i);
      sb.append(item.toString());
      sb.append("\n");
    }
    */
    return sb.toString();
  }

  public static String getTodayString() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());
  }

  public static String getYesterdayString() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    cal.add(Calendar.DAY_OF_MONTH, -1);
    return sdf.format(cal.getTime());
  }

  //Random.nextBoolean is not working
  private boolean randomBoolean(){
    return Math.random() < 0.5;
  }

  private int randomBetween(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public String getJsonDataAsString(GameData gd) {
    try {

      //reset it
      this.lastWorkDate = getTodayString();
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String json = ow.writeValueAsString(gd);
      return json;
    }
    catch (JsonProcessingException ex) {
      System.out.println("ERROR in getJsonData");
      System.out.println(ex.getMessage());      
    }
    return null;
  }

  private String getStringFromJson(JSONObject obj, final String fld) {
    String sVal = (String) obj.get(fld);
    if (sVal == null)  sVal = getTodayString();
    return sVal;
  }

  private int getIntFromJson(JSONObject obj, final String fld) {
    Long lVal = (Long) obj.get(fld);
    if (lVal == null) lVal = 0L;
    return lVal.intValue();
  }

  public GameData createGameFromTemplate(final String fileName) {
    if (this.isDataLoaded()) return null;

    List<DefCity> defCityItems = new ArrayList<DefCity>();
    List<DefTalkLogic> defTalkLogicItems = new ArrayList<DefTalkLogic>();
    List<DefBuilding> defBuildingItems = new ArrayList<DefBuilding>();

    try {
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(new FileReader(fileName));
      JSONObject jsonObject = (JSONObject) obj;
      JSONArray jsonDefCityList = (JSONArray) jsonObject.get("cities");
      JSONArray jsonDefTalkLogicList = (JSONArray) jsonObject.get("talk_logics");
      JSONArray jsonDefBuildingList = (JSONArray) jsonObject.get("buildings");

      System.out.println("Total cities to load: " + String.valueOf(jsonDefCityList.size()));
      System.out.println("Total talk logics to load: " + String.valueOf(jsonDefTalkLogicList.size()));

      if (jsonDefCityList.size() < 1) return null;
      if (jsonDefTalkLogicList.size() < 1) return null;

      for (Object kObj: jsonDefCityList) {
        System.out.println(kObj.toString());
        JSONObject jsonKObj = (JSONObject) kObj;

        JSONObject jsonCap = (JSONObject) jsonKObj.get("capability");
        DefCityCapability capObj = new DefCityCapability(
            getIntFromJson(jsonCap, "land"),
            getIntFromJson(jsonCap, "farm"),
            getIntFromJson(jsonCap, "iron"),
            getIntFromJson(jsonCap, "stone"),
            getIntFromJson(jsonCap, "clay"),
            getIntFromJson(jsonCap, "water"),
            getIntFromJson(jsonCap, "terrain")
          );
        String id = (String) jsonKObj.get("id");
        DefCity item = null;
        if (id != null && id.length() > 0) {
          item = new DefCity(
            (String) jsonKObj.get("id"),
            (String) jsonKObj.get("name"),
            getIntFromJson(jsonKObj, "level"),
            capObj,
            getIntFromJson(jsonKObj, "pop"),
            getIntFromJson(jsonKObj, "troop"),
            (String) jsonKObj.get("owner"),
            getIntFromJson(jsonKObj, "x"),
            getIntFromJson(jsonKObj, "y")
            );
        }
        else {
          item = new DefCity(
            (String) jsonKObj.get("name"),
            getIntFromJson(jsonKObj, "level"),
            capObj,
            getIntFromJson(jsonKObj, "pop"),
            getIntFromJson(jsonKObj, "troop"),
            (String) jsonKObj.get("owner"),
            getIntFromJson(jsonKObj, "x"),
            getIntFromJson(jsonKObj, "y")
            );
        }
        System.out.println(item.toString());
        defCityItems.add(item);
      }

      for (Object rawTlObj: jsonDefTalkLogicList) {
        //System.out.println(tlObj.toString());
        JSONObject jsonMain = (JSONObject) rawTlObj;

        JSONArray jsonValueList = (JSONArray) jsonMain.get("value");
        JSONObject jsonValueRange = (JSONObject) jsonMain.get("value_range");
        JSONArray requireClassList = (JSONArray) jsonMain.get("require_class");
        JSONArray bonusClassList = (JSONArray) jsonMain.get("bonus_class");
        JSONArray jsonDefReactionList = (JSONArray) jsonMain.get("reaction");

        DefTalkLogicValueRange vRngObj = new DefTalkLogicValueRange(getIntFromJson(jsonValueRange, "minimum"));
        System.out.println(vRngObj);

        List<String> tlValList = new ArrayList<String>();
        for (Object tlVal : jsonValueList) {
          tlValList.add((String) tlVal);
        }

        List<String> reqList = new ArrayList<String>();
        for (Object reqCls : requireClassList) {
          reqList.add((String) reqCls);
        }

        List<String> bonusList = new ArrayList<String>();
        for (Object bonusCls : bonusClassList) {
          bonusList.add((String) bonusCls);
        }

        List<DefReaction> reactionList = new ArrayList<DefReaction>();

        for (Object objRA : jsonDefReactionList) {
          JSONObject reactCls = (JSONObject) objRA;
          JSONObject jsonReact = (JSONObject) reactCls.get("react");
          JSONObject jsonReactConfirm = (JSONObject) jsonReact.get("confirm");

          DefReactionReactConfirm rConfirm = new DefReactionReactConfirm();
          if (jsonReactConfirm.get("prompt") != null) {
            rConfirm.setPrompt((String)jsonReactConfirm.get("prompt"));
            rConfirm.setConfirm((String)jsonReactConfirm.get("confirm"));
            rConfirm.setCondition((String)jsonReactConfirm.get("condition"));
            rConfirm.setComplete((String)jsonReactConfirm.get("complete"));
          }
          DefReactionReact rrObj = new DefReactionReact(rConfirm, (String) jsonReact.get("message"));
          //System.out.println("message of rr: "+ (String) jsonReact.get("message"));
          DefReaction rObj = new DefReaction((String) reactCls.get("trigger"), rrObj);
          System.out.println(rObj.toString());
          reactionList.add(rObj);
        }

        DefTalkLogic tlObj = new DefTalkLogic(
          (String) jsonMain.get("type"),
          (String) jsonMain.get("name"),
          tlValList,
          vRngObj,
          reqList,
          bonusList,
          reactionList);

        System.out.println(tlObj);
        defTalkLogicItems.add(tlObj);

      }

      for (Object rawTlObj: jsonDefBuildingList) {
        JSONObject jsonMain = (JSONObject) rawTlObj;

        JSONArray jsonValueList = (JSONArray) jsonMain.get("value");
        JSONObject jsonValueRange = (JSONObject) jsonMain.get("value_range");
        JSONArray jsonCostList = (JSONArray) jsonMain.get("cost");
        JSONArray jsonProduceList = (JSONArray) jsonMain.get("produce");
        JSONArray requireClassList = (JSONArray) jsonMain.get("require_class");
        JSONArray bonusClassList = (JSONArray) jsonMain.get("bonus_class");
        JSONArray jsonDefReactionList = (JSONArray) jsonMain.get("reaction");

        DefTalkLogicValueRange vRngObj = new DefTalkLogicValueRange(getIntFromJson(jsonValueRange, "minimum"));
        System.out.println(vRngObj);

        List<String> tlValList = new ArrayList<String>();
        for (Object tlVal : jsonValueList) {
          tlValList.add((String) tlVal);
        }

        List<String> reqList = new ArrayList<String>();
        for (Object reqCls : requireClassList) {
          reqList.add((String) reqCls);
        }

        List<String> bonusList = new ArrayList<String>();
        for (Object bonusCls : bonusClassList) {
          bonusList.add((String) bonusCls);
        }

        List<DefBuildingCostType> bldCostTypeList = new ArrayList<DefBuildingCostType>();
        for (Object objBldCostType : jsonCostList) {
          JSONObject bldCostTypeCls = (JSONObject) objBldCostType;
          DefBuildingCostType bldCostTypeObj = new DefBuildingCostType(
            (String) bldCostTypeCls.get("type"), getIntFromJson(bldCostTypeCls, "quantity"));
          bldCostTypeList.add(bldCostTypeObj);
        }

        List<DefBuildingProduce> bldProduceList = new ArrayList<DefBuildingProduce>();
        for (Object objBldProduce : jsonProduceList) {
          JSONObject bldProduceCls = (JSONObject) objBldProduce;
          DefBuildingProduce bldProduceObj = new DefBuildingProduce(
              (String)bldProduceCls.get("type"),
              getIntFromJson(bldProduceCls, "quantity"),
              getIntFromJson(bldProduceCls, "speed")
            );
          bldProduceList.add(bldProduceObj);
        }

        List<DefReaction> reactionList = new ArrayList<DefReaction>();
        for (Object objRA : jsonDefReactionList) {
          JSONObject reactCls = (JSONObject) objRA;
          JSONObject jsonReact = (JSONObject) reactCls.get("react");
          JSONObject jsonReactConfirm = (JSONObject) jsonReact.get("confirm");

          DefReactionReactConfirm rConfirm = new DefReactionReactConfirm();
          if (jsonReactConfirm.get("prompt") != null) {
            rConfirm.setPrompt((String)jsonReactConfirm.get("prompt"));
            rConfirm.setConfirm((String)jsonReactConfirm.get("confirm"));
            rConfirm.setCondition((String)jsonReactConfirm.get("condition"));
            rConfirm.setComplete((String)jsonReactConfirm.get("complete"));
          }
          DefReactionReact rrObj = new DefReactionReact(rConfirm, (String) jsonReact.get("message"));
          //System.out.println("message of rr: "+ (String) jsonReact.get("message"));
          DefReaction rObj = new DefReaction((String) reactCls.get("trigger"), rrObj);
          System.out.println(rObj.toString());
          reactionList.add(rObj);
        }

        DefBuilding bldObj = new DefBuilding(
          (String) jsonMain.get("type"),
          (String) jsonMain.get("name"),
          tlValList,
          vRngObj,
          reqList,
          bonusList,
          bldCostTypeList,
          bldProduceList,
          reactionList);

        System.out.println(bldObj);
        defBuildingItems.add(bldObj);

      }

      if (defCityItems.size() > 0 && 
        defTalkLogicItems.size() > 0 && 
        defBuildingItems.size() > 0) {
          GameData gmData = new GameData(defCityItems, defTalkLogicItems, defBuildingItems);
          return gmData;
      }

    }
    catch (FileNotFoundException ex) {
      System.out.println("ERROR not found " + ex.toString());
      ex.printStackTrace(System.out);
    }
    catch (ParseException ex) {
      System.out.println("ERROR parse error " + ex.toString());
      ex.printStackTrace(System.out);
     }
    catch (IOException ex) {
      System.out.println("ERROR IO " + ex.toString());
      ex.printStackTrace(System.out);
     }
    catch (Exception ex) {
      System.out.println("ERROR unknown " + ex.toString());      
      ex.printStackTrace(System.out);
     }

    return null;

  }

  public void increaseJCoin(final int step) {
      this.jCoin += step;
  }

  public boolean decreaseJCoin(final int step) {
    if (this.jCoin - step < 0) return false;
    this.jCoin -= step;
    return true;
  }

}