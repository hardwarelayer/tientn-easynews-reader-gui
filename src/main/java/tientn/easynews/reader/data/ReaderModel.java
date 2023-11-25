package tientn.easynews.reader.data;

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

import tientn.easynews.reader.data.JBGConstants;
import java.sql.*;

public class ReaderModel {

  @Getter @Setter private int currentWorkMode = JBGConstants.TEST_WORD_IN_MAJOR_LIST;

  @Getter
  private List<JBGKanjiItem> dataKanjiItems;
  @Getter
  private List<TFMTTNAData> dataTNAItems;
  @Getter
  private List<TFMTTNGData> dataTNGItems;
  @Getter
  private String lastWorkDate;
  private static final String DATE_FORMAT_NOW = "yyyy-MM-dd"; // HH:mm:ss";

  @Getter private List<JBGKanjiItem> subsetRecords = null;
  @Getter @Setter private boolean needRefresh;
  @Getter @Setter private boolean testStarted;
  @Getter @Setter private boolean readStarted;
  @Getter private int totalKanjis = 0;
  @Getter private int totalMatchedKanjis = 0;
  @Getter private int totalKanjiTests = 0;
  @Getter private boolean tfmtLoaded = false;
  @Getter private String selectedArticleId;
  @Getter private String selectedGrammarId;

  //for kanji subset select
  private int kanjiSubsetStart = 0;
  @Getter @Setter private int kanjiSubsetSize = JBGConstants.DEFAULT_KANJI_SUBSET_SIZE;

  @Setter private String grammarMP3FolderPath;
  @Setter @Getter private String articleMP3FolderPath;

  @Getter @Setter private int jCoin = 0;

  //for transfering between auto-kanji-show and wordmatchs
  @Getter @Setter private boolean transfering;
  @Getter @Setter private int transferSortOrder = 0;
  @Getter @Setter private int transferStartIdx = 0;
  @Getter @Setter private int transferEndIdx = 0;

  public ReaderModel() {
    initData();

    needRefresh = false;
    testStarted = false;
    transfering = false;
  }

  private void initData() {
    this.dataKanjiItems = new ArrayList<JBGKanjiItem>();
    this.dataTNAItems = new ArrayList<TFMTTNAData>();
    this.dataTNGItems = new ArrayList<TFMTTNGData>();
    /*
    for (int i = 0; i < 10; i++) {
      JBGKanjiItem item = new JBGKanjiItem(
        "Kanji"+String.valueOf(i), "Hiragana"+String.valueOf(i), 
        "Hv"+String.valueOf(i), "Meaning"+String.valueOf(i));
      this.dataKanjiItems.add(item);
    }
    */
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.dataKanjiItems.size(); i++) {
      JBGKanjiItem item = this.dataKanjiItems.get(i);
      sb.append(item.toString());
      sb.append("\n");
    }
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

  public void setSelectedArticleId(final String s) {
    if (!s.equals(this.selectedArticleId)) {
      this.needRefresh = true;
    }
    this.selectedArticleId = s;
  }

  public void setSelectedGrammarId(final String s) {
    if (!s.equals(this.selectedGrammarId)) {
      this.needRefresh = true;
    }
    this.selectedGrammarId = s;
  }

  private InputStreamReader getInputStream(final File f) {
    InputStreamReader stream = null;

    URL url = null;
    try {
      url = f.toURI().toURL();
    }
    catch (final MalformedURLException e) {
      System.out.println("getInputStream: Cannot get URL from file object");
    }
    if (url != null) {
      final Optional<InputStream> inputStream = UrlStreams.openStream(url);
      if (inputStream.isPresent()) {
        try {
          //TienTN: load UTF8 tooltips, because default load is ISO-8859-1
          stream = new InputStreamReader(inputStream.get(), Charset.forName("UTF-8"));
        } catch (final Exception e) {
          System.out.println("Error opening stream: " + f.getName() + "exception: " + e.getMessage());
        }
      }
    }
    else {
      System.out.println("Can't get url for stream");
    }
    return stream;
  }

  public List<JBGKanjiItem> loadKanjiFromInJBoardSaved(final File f) {

    int iOriginKanjisCount = this.dataKanjiItems.size();

    InputStreamReader s = getInputStream(f);
    if (s != null) {
      try (BufferedReader br = new BufferedReader(s)) {
          String line;
          while ((line = br.readLine()) != null) {
          //System.out.println(line);
            String[] values = line.split(JBGConstants.KANJI_COMMA_DELIMITER, 8);
            if (values.length != 8) {
              System.out.println("Invalid line: " + line);
            }
            else {
              JBGKanjiItem item = new JBGKanjiItem(
                values[0], //id in string format
                values[1], values[2], values[3],
                values[4], 
                Integer.parseInt(values[5]), Integer.parseInt(values[6]),
                Integer.parseInt(values[7]));
              this.dataKanjiItems.add(item);
            }
          }
      }
      catch (final IOException e) {
        System.out.println("Error reading file "+f.getName() + " exception:" + e.getMessage());
      }
    }
    else {
      System.out.println("Invalid input stream");
    }

    if (this.dataKanjiItems.size() != iOriginKanjisCount) {
      this.needRefresh = true;
    }
    return dataKanjiItems;
  }

  public List<JBGKanjiItem> loadKanjiFromCSV(final File f) {
    int iOriginKanjisCount = this.dataKanjiItems.size();

    InputStreamReader s = getInputStream(f);
    if (s != null) {
      try (BufferedReader br = new BufferedReader(s)) {
          String line;
          while ((line = br.readLine()) != null) {
            //System.out.println(line); 
            String[] values = line.split(JBGConstants.KANJI_COMMA_DELIMITER, 8);
            //id of item will be auto generated on object creation
            JBGKanjiItem item = new JBGKanjiItem(values[0], values[1], values[2], values[3]);
            dataKanjiItems.add(item);
          }
      }
      catch (final IOException e) {
        System.out.println("Error reading file "+f.getName());
      }
    }
    else {
      System.out.println("Invalid input stream");
    }

    if (this.dataKanjiItems.size() != iOriginKanjisCount) {
      this.needRefresh = true;
    }
    return dataKanjiItems;
  }

  public boolean isDataLoaded() {
    if ( this.dataKanjiItems.size() > 0 ) return true;
    return false;
  }


  //used for article word build, lookup existing content for auto fill
  private JBGKanjiItem getKanjiContentFromMainKanjiList(final String kanji) {
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kanji)) {
        return item;
      }
    }
    return null;
  }

  //use for article loading, add to main kanjilist if not yet avail in this main list
  private boolean addKanjiToMainKanjiList(final JBGKanjiItem kj) {
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kj.getKanji()))
        return false;
    }
    this.increaseJCoin(JBGConstants.JCOIN_AMOUNT_FOR_ADD_KANJI_WORD);
    this.dataKanjiItems.add(kj.cloneItem());
    return true;
  }

  public List<JBGKanjiItem> getSimilarKanjiFromMainKanjiList(final String kanji) {
    List<JBGKanjiItem> lst = new ArrayList<JBGKanjiItem>();
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kanji) || item.getKanji().contains(kanji) || kanji.contains(item.getKanji())) {
        lst.add(item.cloneItem());
      }
    }
    return lst;
  }

  public JBGKanjiItem getKanjiFromMainKanjiList(final String kanji) {
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kanji)) {
        return item;
      }
    }
    return null;
  }

  public List<JBGKanjiItem> getRelatedKanjiFromMainKanjiList(final String kanji) {
    List<JBGKanjiItem> lstRes = new ArrayList<JBGKanjiItem>();
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().contains(kanji) || kanji.contains(item.getKanji())) {
        if (!item.getKanji().equals(kanji)) //not include same kanji
          lstRes.add(item);
      }
    }
    return lstRes;
  }

  private boolean isKanjiInList(final String kanji, List<JBGKanjiItem> lst) {
    for (JBGKanjiItem item: lst) {
      if (item.getKanji().equals(kanji)) {
        return true;
      }
    }
    return false;
  }

  //kanji subset build for load in WordMatch game
  // 
  private void sortKanjisByCorrectCount(List<JBGKanjiItem> lst, boolean bReverse) {
    //java 8+
    if (!bReverse)
      lst.sort((r1, r2) -> r1.getCorrectCount() - r2.getCorrectCount());
    else
      lst.sort((r1, r2) -> r2.getCorrectCount() - r1.getCorrectCount());
  }

  //Random.nextBoolean is not working
  private boolean randomBoolean(){
    return Math.random() < 0.5;
  }

  private int randomBetween(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  //JBGConstants.KANJI_MIN_TEST_CORRECT
  private void buildSubSetRecords(final int iMinTestCorrect) {
    List<JBGKanjiItem> lstKnownItems = new ArrayList<>();
    List<JBGKanjiItem> lstSelectedKnownItems = new ArrayList<>();
    List<JBGKanjiItem> lstNewItems = new ArrayList<>();
    int iKnownItemsCount = randomBetween(5, 10);

    this.subsetRecords = new ArrayList<>();
    //sort desc by correct count, so we'll traverse top down when load
    sortKanjisByCorrectCount(this.dataKanjiItems, true);
    int iCount = 0;
    //reset 2nd purpose(stats)
    this.totalKanjis = 0;
    this.totalMatchedKanjis = 0;
    this.totalKanjiTests = 0;
    totalKanjis = this.dataKanjiItems.size();
    for (JBGKanjiItem item: this.dataKanjiItems) {
      //2nd purpose
      if (item.getCorrectCount() >= JBGConstants.KANJI_MIN_TEST_CORRECT) {
        totalMatchedKanjis += 1;
      }
      totalKanjiTests += item.getTestCount();

      //first purpose of this loop
      if (iCount <= JBGConstants.KANJI_TOTAL_SUBSET_SIZE) {

        if (item.getCorrectCount() < iMinTestCorrect) {
          lstNewItems.add(item);
          iCount++;
        }
        else {
          //get all known items into one list
          lstKnownItems.add(item);
        }

      }
      else {
        //if first purpose of the loop is done, now checking for break point after 2nd purpose (count stats)
        if (item.getTestCount() < 1) {
          //chua test bao gio
          break;
        }
      }

    }

    //at the beginning, this is small to zero number
    if (lstKnownItems.size() < iKnownItemsCount) {
      iKnownItemsCount = lstKnownItems.size();
    }
    //sort this list from lowest correct to highest
    //so we can get newly known items first, to repeat studying
    sortKanjisByCorrectCount(lstKnownItems, false);

    List<Integer> selectedKnownIndexes = new ArrayList<Integer>();
    for (int i=0; i < iKnownItemsCount; i++) {
      int iRndIndex = randomBetween(0, iKnownItemsCount);
      while (selectedKnownIndexes.contains(iRndIndex)) {
        iRndIndex = randomBetween(0, iKnownItemsCount);
      }

      selectedKnownIndexes.add(iRndIndex);
    }
    for (int i=0; i < iKnownItemsCount; i++) {
      int iRndSlot = selectedKnownIndexes.get(i);
      this.subsetRecords.add(lstKnownItems.get(iRndSlot)); 
    }

    //new words
    for (int i=0; i < JBGConstants.KANJI_TOTAL_SUBSET_SIZE-iKnownItemsCount; i++) {
      this.subsetRecords.add(lstNewItems.get(i));
    }

  }

  private void buildSpecificRecords(final List<String> lstKanjiWords, List<JBGKanjiItem> lst) {
    int iTotalSpecificItems = lstKanjiWords.size();

    this.subsetRecords = new ArrayList<>();

    for (JBGKanjiItem item: lst) {
      if (lstKanjiWords.contains(item.getKanji())) {
        this.subsetRecords.add(item);
        if (this.subsetRecords.size() >= iTotalSpecificItems)
          break;
      }
    }

  }

  public List<JBGKanjiItem> getNormalKJSubset() {
    if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_MAJOR_LIST) {
      //System.out.println("total rows: " + String.valueOf(dataKanjiItems.size()));
      buildSubSetRecords(JBGConstants.KANJI_MIN_TEST_CORRECT);
      return this.subsetRecords;
    }
    else if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      if (currentTNA != null) {
        int iSize = currentTNA.getKanjisForTest().size();
        if (this.kanjiSubsetStart > 0 && this.kanjiSubsetStart < iSize)
          this.kanjiSubsetStart--;
        else
          this.kanjiSubsetStart = 0;
        int iToIdx = this.kanjiSubsetStart + this.kanjiSubsetSize;
        if (iToIdx >= iSize)
          iToIdx = iSize - 1;
        this.subsetRecords = currentTNA.getKanjisForTest().subList(this.kanjiSubsetStart, iToIdx);
        return this.subsetRecords;
      }
    }
    return null;

  }

  public List<JBGKanjiItem> getNewKJSubset() {

    if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_MAJOR_LIST) {
      //System.out.println("total rows: " + String.valueOf(dataKanjiItems.size()));
      buildSubSetRecords(5); //lay cac tu dung tu 5 lan tro xuong cho toi 0 (chua hoc)
      return this.subsetRecords;
    }
    else if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      if (currentTNA != null) {
        int iSize = currentTNA.getKanjisForTest().size();
        if ((this.kanjiSubsetStart + this.kanjiSubsetSize) < iSize) {
          this.kanjiSubsetStart++;
          int iToIdx = this.kanjiSubsetStart + this.kanjiSubsetSize;
          if (iToIdx >= iSize)
            iToIdx = iSize;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(this.kanjiSubsetStart, iToIdx);
        }
        else {
          this.kanjiSubsetStart = 0;
          if (this.kanjiSubsetSize >= iSize)
            this.kanjiSubsetSize = iSize - 1;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(0, this.kanjiSubsetSize);
        }
        return this.subsetRecords;
      }
    }

    return null;

  }

  public List<JBGKanjiItem> getAllNewKJSubset() {

    if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_MAJOR_LIST) {
      //System.out.println("total rows: " + String.valueOf(dataKanjiItems.size()));
      buildSubSetRecords(5); //lay cac tu dung tu 5 lan tro xuong cho toi 0 (chua hoc)
      return this.subsetRecords;
    }
    else if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      if (currentTNA != null) {
        int iSize = currentTNA.getKanjisForTest().size();
        if ((this.kanjiSubsetStart + this.kanjiSubsetSize) < iSize) {
          this.kanjiSubsetStart += this.kanjiSubsetSize;
          int iToIdx = this.kanjiSubsetStart + this.kanjiSubsetSize;
          if (iToIdx >= iSize)
            iToIdx = iSize - 1;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(this.kanjiSubsetStart, iToIdx);
        }
        else {
          this.kanjiSubsetStart = 0;
          if (this.kanjiSubsetSize >= iSize)
            this.kanjiSubsetSize = iSize - 1;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(0, this.kanjiSubsetSize);
        }
        return this.subsetRecords;
      }
    }

    return null;

  }

  /*
  find the newest learn kanji, and load a subset from it back to previous kanji
  This could be useful for resume learning last session.
  Now only work for article words
  */
  public List<JBGKanjiItem> getNewestLearnKJSubset() {
    boolean bFoundUnlearn = false;

    if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      int iSize = currentTNA.getKanjisForTest().size();
      if (currentTNA != null) {
        //first, find the latest learn kanji
        int iLatestLearn = -1;
        int iKjIdxCount = 0;
        for (JBGKanjiItem kj: currentTNA.getKanjisForTest()) {
          if (kj.getTestCount() == 0) {
            bFoundUnlearn = true;
            break;
          }
          iKjIdxCount++;
        }

        if (!bFoundUnlearn) {
          //if every kanji got learnt, we load the last ones in the list
          this.kanjiSubsetStart = iSize - this.kanjiSubsetSize;
          if (this.kanjiSubsetStart < 0) this.kanjiSubsetStart = 0;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(this.kanjiSubsetStart, iSize);
          return this.subsetRecords;
        }

        if (iKjIdxCount + 1 < currentTNA.getKanjisForTest().size()) {
          iKjIdxCount -= 1; //get the first kj with testcount > 0
        } //otherwise just get end of list

        if (iKjIdxCount - this.kanjiSubsetSize <= 0) {
          this.kanjiSubsetStart = 0;
        }
        else {
          this.kanjiSubsetStart = iKjIdxCount - this.kanjiSubsetSize;
        }

        if ((this.kanjiSubsetStart + 1 + this.kanjiSubsetSize) < iSize) {
          this.kanjiSubsetStart++;
          int iToIdx = this.kanjiSubsetStart + this.kanjiSubsetSize;
          if (iToIdx >= iSize)
            iToIdx = iSize - 1;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(this.kanjiSubsetStart, iToIdx);
        }
        else {
          this.kanjiSubsetStart = 0;
          if (this.kanjiSubsetSize >= iSize)
            this.kanjiSubsetSize = iSize - 1;
          this.subsetRecords = currentTNA.getKanjisForTest().subList(0, this.kanjiSubsetSize);
        }
        return this.subsetRecords;
       }
     }

    return null;

  }

  public String getArticleKJSubsetStat() {
   if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      if (currentTNA != null) {
        int iTotalKJ = currentTNA.getKanjisForTest().size();

        return new StringBuilder(
            String.valueOf(this.kanjiSubsetStart) +
            "/" +
            String.valueOf(iTotalKJ)
            ).toString();
      }
    }
    return null;
  }

  public List<JBGKanjiItem> getSpecificKJSubset(List<String> lstKanjiWords) {
    if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_MAJOR_LIST) {
      //only load the kanjis in the string list
      buildSpecificRecords(lstKanjiWords, this.dataKanjiItems);
      return this.subsetRecords;
    }
    else if (this.currentWorkMode == JBGConstants.TEST_WORD_IN_ARTICLE) {
      TFMTTNAData currentTNA = getSelectedTNA();
      if (currentTNA != null) {
        buildSpecificRecords(lstKanjiWords, currentTNA.getKanjisForTest());
        return this.subsetRecords;
      }
    }

    return null;
  }

  public void printCurrentKanjisWithTest() {

    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getTestCount() > 0) {
        System.out.println(item.toString());
      }
    }

  }

  public String getJsonDataAsString() {
    try {

      TFMTWorkData tfmt = new TFMTWorkData();
      //reset it
      this.lastWorkDate = getTodayString();
      tfmt.setData(this.dataKanjiItems, this.dataTNAItems, this.dataTNGItems, this.lastWorkDate, this.jCoin);
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String json = ow.writeValueAsString(tfmt);
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

  private void recountGrandKanjiTests() {
    this.totalKanjis = 0;
    this.totalMatchedKanjis = 0;
    this.totalKanjiTests = 0;
    totalKanjis = this.dataKanjiItems.size();

    for (JBGKanjiItem item: this.dataKanjiItems) {
      //2nd purpose
      if (item.getCorrectCount() >= JBGConstants.KANJI_MIN_TEST_CORRECT) {
        totalMatchedKanjis += 1;
      }
      totalKanjiTests += item.getTestCount();
    }
  }

  public boolean addBuiltWordToGrandKanjiList(JBGKanjiItem kItem) {
    boolean wordExists = false;
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kItem.getKanji())) {
        //update the test count from article kanji for test
        //not exact if we test in both grand list and TNA, but acceptable for now
        if (item.getTestCount() < kItem.getTestCount()) {
          item.setTestCount(kItem.getTestCount());
        }
        if (item.getCorrectCount() < kItem.getCorrectCount()) {
          item.setCorrectCount(kItem.getCorrectCount());
        }
        wordExists = true;
        break;
      }
    }
    if (!wordExists) {
      this.dataKanjiItems.add(kItem);
    }
    return (!wordExists);
  }

  public boolean loadTFMTJsonFromFile(final String fileName) {
    if (this.testStarted) return false;

    try {
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(new FileReader(fileName));
      JSONObject jsonObject = (JSONObject) obj;
      JSONArray kanjiList = (JSONArray) jsonObject.get("kanjiWorks");
      JSONArray articleList = (JSONArray) jsonObject.get("articleWorks");
      JSONArray grammarList = (JSONArray) jsonObject.get("grammarWorks");

      //System.out.println("Total kanjis to load: " + String.valueOf(kanjiList.size()));
      //System.out.println("Total articles to load: " + String.valueOf(articleList.size()));
      //System.out.println("Open with jcoin " + String.valueOf(jsonObject.get("jcoin")));
      if (grammarList != null)
        System.out.println("Total grammars  to load: " + String.valueOf(grammarList.size()));

      if (kanjiList.size() < 1) return false;

      boolean hasNewKanjiFromTNA = false;

      this.totalKanjis = 0;
      this.totalMatchedKanjis = 0;
      this.totalKanjiTests = 0;
      this.dataKanjiItems.clear();
      this.dataTNAItems.clear();
      this.dataTNGItems.clear();
      for (Object kObj: kanjiList) {
        //System.out.println(kObj.toString());
        JSONObject jsonKObj = (JSONObject) kObj;

        JBGKanjiItem item = new JBGKanjiItem(
          (String) jsonKObj.get("id"),
          (String) jsonKObj.get("kanji"),
          (String) jsonKObj.get("hiragana"),
          (String) jsonKObj.get("hv"),
          (String) jsonKObj.get("meaning"),
          getIntFromJson(jsonKObj, "testCount"),
          getIntFromJson(jsonKObj, "correctCount"),
          getIntFromJson(jsonKObj, "weightValue")
          );
        //System.out.println(item.toString());
        this.dataKanjiItems.add(item);
      }
      this.needRefresh = true;
      this.totalKanjis = getIntFromJson(jsonObject, "totalKanjis");
      this.totalMatchedKanjis = getIntFromJson(jsonObject, "totalMatchedKanjis");
      this.totalKanjiTests = getIntFromJson(jsonObject, "totalKanjiTests");
      this.jCoin = getIntFromJson(jsonObject, "jcoin");
      this.lastWorkDate = getStringFromJson(jsonObject, "lastWorkDate");

      for (Object aJ: articleList) {
        JSONObject articleItem = (JSONObject) aJ;
        String sArticleId = (String) articleItem.get("id");
        String sArticleTitle = (String) articleItem.get("articleTitle");
        JSONArray sentenceList = (JSONArray) articleItem.get("articleSentences");
        JSONArray articleKanjiList = (JSONArray) articleItem.get("articleKanjis");
        JSONArray lstBuiltWords = (JSONArray) articleItem.get("kanjisForTest");
        JSONArray lstProblematicWords = null;
        if (articleItem.containsKey(("problematicWords"))) {
          lstProblematicWords = (JSONArray) articleItem.get("problematicWords");
        }
        int ttlTNATests = getIntFromJson(articleItem, "totalTests");
        int ttlTNATestCorrects = getIntFromJson(articleItem, "totalCorrectTests");
        int ttlTNAKanjiTests = getIntFromJson(articleItem, "testTotalOfKanjiForTest");

        //System.out.println("  Loading article " + sArticleId + " " + sArticleTitle);
        //System.out.println("    Total sentences: " + String.valueOf(sentenceList.size()));
        //System.out.println("    Total kanjis: " + String.valueOf(articleKanjiList.size()));
        //System.out.println("    Total built words for test: " + String.valueOf(lstBuiltWords.size()));
        //if (lstProblematicWords != null)
          //System.out.println("    Total problematic words: " + String.valueOf(lstProblematicWords.size()));

        List<TFMTTNASentenceData> lstTNASentences = new ArrayList<TFMTTNASentenceData>();
        if (sentenceList != null) {
          for (Object sObj: sentenceList) {
            //System.out.println(kObj.toString());
            JSONObject jsonSObj = (JSONObject) sObj;
            String sSentenceId = (String) jsonSObj.get("id");
            String sSentence = (String) jsonSObj.get("sentence");
            String sEnglishMeaning = (String) jsonSObj.get("englishMeaning");

            /*
            System.out.println(sSentence);
            if (sEnglishMeaning != null)
              System.out.println(sEnglishMeaning);
              */

            List<String> lstSentenceKanjis = new ArrayList<String>();
            JSONArray lstKanjisOfSentence = (JSONArray) jsonSObj.get("sentenceKanjis");
            if (lstKanjisOfSentence != null) {
              for (Object kObj: lstKanjisOfSentence) {
                String sKanjiWord = (String) kObj;
                lstSentenceKanjis.add(sKanjiWord);
              }
            }

            TFMTTNASentenceData sentenceItem = new TFMTTNASentenceData(sSentenceId, sSentence, sEnglishMeaning, lstSentenceKanjis);
            lstTNASentences.add(sentenceItem);
          }
        }

        List<TFMTTNAKanjiData> lstArticleKanjis = new ArrayList<TFMTTNAKanjiData>();
        if (articleKanjiList != null) {
          for (Object k : articleKanjiList) {
            JSONObject kanjiItem = (JSONObject) k;
            String kanji = (String) kanjiItem.get("kanji");
            if (kanjiItem != null) {
              //System.out.println(kanji);
              String sHiragana = (String) kanjiItem.get("hiragana");
              String sHvPhonetic = (String) kanjiItem.get("hv");

              List<TFMTTNAKanjiDetailData> lstKanjiDetailData = new ArrayList<TFMTTNAKanjiDetailData>();

              JSONArray kanjiDetailList = (JSONArray) kanjiItem.get("kanjis");
              if (kanjiDetailList != null) {
                for (Object d: kanjiDetailList) {
                  JSONObject kanjiDetailItem = (JSONObject) d;
                  String sDetailChar = (String) kanjiDetailItem.get("kanji"); 
                  if (kanjiDetailItem != null) {

                    String sDetailId = (String) kanjiDetailItem.get("id");
                    String sDetailHvPhonetic = (String) kanjiDetailItem.get("hv");
                    String sDetailOnKun = (String) kanjiDetailItem.get("onkun");
                    String sDetailMeaning = (String) kanjiDetailItem.get("meaning");

                    lstKanjiDetailData.add(
                      new TFMTTNAKanjiDetailData(sDetailId, sDetailChar, sDetailHvPhonetic, sDetailOnKun, sDetailMeaning)
                      );

                  }
                }
              }

              TFMTTNAKanjiData kanjiDataObject = new TFMTTNAKanjiData(kanji, sHiragana, sHvPhonetic, lstKanjiDetailData);
              lstArticleKanjis.add(kanjiDataObject);

            }
          }
        }

        List<JBGKanjiItem> lstBuiltWordForTest = new ArrayList<JBGKanjiItem>();
        if (lstBuiltWords != null) {
          for (Object kObj: lstBuiltWords) {
            JSONObject wordItem = (JSONObject) kObj;
            if (wordItem != null) {
              String sId = (String) wordItem.get("id");
              String sKanji = (String) wordItem.get("kanji");
              String sHiragana = (String) wordItem.get("hiragana");
              String sHv = (String) wordItem.get("hv");
              String sMeaning = (String) wordItem.get("meaning");
              int iTestCount = getIntFromJson(wordItem, "testCount");
              int iCorrectCount = getIntFromJson(wordItem, "correctCount");
              int iWeightValue = getIntFromJson(wordItem, "weightValue");

              JBGKanjiItem kjItem = new JBGKanjiItem(sId, sKanji, sHiragana, sHv, sMeaning, iTestCount, iCorrectCount, iWeightValue);
              lstBuiltWordForTest.add(kjItem);

              //if this word is not in grand kanji list yet, add it
              if (addBuiltWordToGrandKanjiList(kjItem)) {
                hasNewKanjiFromTNA = true;
              }
            }
          }

        }

        List<String> lstProblematicWordsForTest = new ArrayList<String>();
        if (lstProblematicWords != null) {
          for (Object sW: lstProblematicWords) {
            String sTmp = (String) sW;
            if (sTmp != null) {
              lstProblematicWordsForTest.add(sTmp);
            }
          }
        }

        //this feature is not the same as in loadTNAJsonFromFile (always rescan)
        //in this, it only rescan if no word built for test, yet
        //to avoiding reimporting deleted builtwords from article
        if (lstBuiltWordForTest.size() < 1) {
          //rescan lstArticleKanjis and lstKanjiForTest
          //neu nhu kanji nao nam trong lstArticleKanjis, da co san trong dataKanjiItems
          // ma chua duoc add vao lstKanjiForTest thi tu dong add vao lstKanjiForTest
          for (TFMTTNAKanjiData articleWord: lstArticleKanjis) {
            JBGKanjiItem kItem = getKanjiContentFromMainKanjiList(articleWord.getKanji());
            if (kItem != null && !isKanjiInList(articleWord.getKanji(), lstBuiltWordForTest) ) {
              lstBuiltWordForTest.add(kItem.cloneItem());
            }
          }
        }

        TFMTTNAData tna = null;
        if (lstProblematicWords == null) {
          tna = new TFMTTNAData(sArticleId, sArticleTitle, lstTNASentences, lstArticleKanjis, lstBuiltWordForTest,
            ttlTNATests, ttlTNATestCorrects); //not use ttlTNAKanjiTests
        }
        else {
          tna = new TFMTTNAData(sArticleId, sArticleTitle, lstTNASentences, lstArticleKanjis, lstBuiltWordForTest,
            lstProblematicWordsForTest,
            ttlTNATests, ttlTNATestCorrects); //not use ttlTNAKanjiTests
        }
        if (tna != null)
          this.dataTNAItems.add(tna);

      }

      if (grammarList != null)
        for (Object gJ: grammarList) {
          JSONObject grammarItem = (JSONObject) gJ;
          String sGrammarId = (String) grammarItem.get("id");
          String sGrammarTitle = (String) grammarItem.get("grammarTitle");
          JSONArray patternList = (JSONArray) grammarItem.get("grammarPattern");
          JSONArray grammarProblematicIdList = null;
          if (grammarItem.containsKey(("problematicPatternId"))) {
            grammarProblematicIdList = (JSONArray) grammarItem.get("problematicPatternId");
          }
          int ttlTNGTests = getIntFromJson(grammarItem, "totalTests");
          int ttlTNGTestCorrects = getIntFromJson(grammarItem, "totalCorrectTests");

          //System.out.println("  Loading grammar " + sGrammarId + " " + sGrammarTitle);
          //System.out.println("    Total grammar patterns: " + String.valueOf(patternList.size()));
          //if (grammarProblematicIdList != null)
            //System.out.println("    Total problematic patterns: " + String.valueOf(grammarProblematicIdList.size()));

          List<TFMTTNGPatternData> lstTNGPatterns = new ArrayList<TFMTTNGPatternData>();
          if (patternList != null) {
            for (Object sObj: patternList) {
              //System.out.println(kObj.toString());
              JSONObject objPattern = (JSONObject) sObj;
              String sPatternId = (String) objPattern.get("id");
              String sTitle = (String) objPattern.get("title");
              JSONArray descriptionList = (JSONArray) objPattern.get("description");
              JSONArray sentenceList = (JSONArray) objPattern.get("sentence");

              List<String> lstDescriptions = new ArrayList<String>();
              if (descriptionList != null) {
                for (Object kObj: descriptionList) {
                  String sDesc = (String) kObj;
                  lstDescriptions.add(sDesc);
                }
              }

              List<TFMTTNGPatternSentence> lstSentences = new ArrayList<TFMTTNGPatternSentence>();
              for (Object oSentence: sentenceList) {
                JSONObject jsonSentence = (JSONObject) oSentence;
                String sId = (String) jsonSentence.get("id");
                String sSentence = (String) jsonSentence.get("sentence");
                int ttlCorrects = getIntFromJson(jsonSentence, "correctTests");
                int ttlTests = getIntFromJson(jsonSentence, "totalTests");
                JSONArray meaningLst = (JSONArray) jsonSentence.get("meaning");
                List<String> lstMeanings = new ArrayList<String>();
                for (Object oM: meaningLst) {
                  lstMeanings.add((String) oM);
                }
                TFMTTNGPatternSentence sen = null;
                if (sId != null)
                  sen = new TFMTTNGPatternSentence(sId, sSentence, lstMeanings, ttlCorrects, ttlTests);
                else
                  sen = new TFMTTNGPatternSentence(sSentence, lstMeanings);
                lstSentences.add(sen);
              }
              TFMTTNGPatternData patternItem = new TFMTTNGPatternData(sPatternId, sTitle, lstDescriptions, lstSentences);
              lstTNGPatterns.add(patternItem);
            }
          }

          List<String> lstProblematicPatternIds = new ArrayList<String>();
          if (grammarProblematicIdList != null) {
            for (Object sW: grammarProblematicIdList) {
              String sTmp = (String) sW;
              if (sTmp != null) {
                lstProblematicPatternIds.add(sTmp);
              }
            }
          }

          TFMTTNGData tng = null;
          if (grammarProblematicIdList == null) {
            tng = new TFMTTNGData(sGrammarId, sGrammarTitle, lstTNGPatterns,  
              ttlTNGTests, ttlTNGTestCorrects);
          }
          else {
            tng = new TFMTTNGData(sGrammarId, sGrammarTitle, lstTNGPatterns, lstProblematicPatternIds, 
              ttlTNGTests, ttlTNGTestCorrects);
          }
          if (tng != null)
            this.dataTNGItems.add(tng);

        }

      this.tfmtLoaded = true;

      return true;
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

    return false;
  }

  public boolean loadTNAJsonFromFile(final String fileName) {
    if (this.testStarted) return false;
    if (!this.tfmtLoaded) return false;

    try {

      JSONParser parser = new JSONParser();
      Object obj = parser.parse(new FileReader(fileName));
      JSONObject jsonObject = (JSONObject) obj;
      JSONObject dataPart = (JSONObject) jsonObject.get("tientn_nhkeasynews_format");
      String sArticleId = (String) dataPart.get("id");
      String sArticleTitle = (String) dataPart.get("title");
      JSONArray sentenceList = (JSONArray) dataPart.get("sentence");
      JSONObject kanjiDict = (JSONObject) dataPart.get("kanjis");
      JSONArray lstBuiltWords = (JSONArray) dataPart.get("kanjisForTest");
      int ttlTNATests = 0; //getIntFromJson(dataPart, "totalTests");
      int ttlTNATestCorrects = 0; //getIntFromJson(dataPart, "totalCorrectTests");
      int ttlTNAKanjiTests = 0; //getIntFromJson(dataPart, "testTotalOfKanjiForTest");

      //System.out.println(sArticleTitle); 
      //System.out.println("Total loaded kanjis: " + String.valueOf(kanjiDict.size()));
      //System.out.println("Total loaded sentences: " + String.valueOf(sentenceList.size()));

      //if (kanjiDict.size() < 1) return false;

      List<TFMTTNASentenceData> lstTNASentences = new ArrayList<TFMTTNASentenceData>();
      if (sentenceList != null) {
        for (Object sObj: sentenceList) {
          //System.out.println(kObj.toString());
          JSONObject jsonSObj = (JSONObject) sObj;
          String sSentence = (String) jsonSObj.get("sentence");
          String sEnglishMeaning = (String) jsonSObj.get("english_meaning");

          //System.out.println(sSentence);
          //if (sEnglishMeaning != null)
            //System.out.println(sEnglishMeaning);

          List<String> lstSentenceKanjis = new ArrayList<String>();
          JSONArray lstKanjisOfSentence = (JSONArray) jsonSObj.get("kanjis");
          if (lstKanjisOfSentence != null) {
            for (Object kObj: lstKanjisOfSentence) {
              String sKanjiWord = (String) kObj;
              lstSentenceKanjis.add(sKanjiWord);
            }
          }

          TFMTTNASentenceData sentenceItem = new TFMTTNASentenceData(sSentence, sEnglishMeaning, lstSentenceKanjis);
          lstTNASentences.add(sentenceItem);
        }
      }

      List<TFMTTNAKanjiData> lstArticleKanjis = new ArrayList<TFMTTNAKanjiData>();
      if (kanjiDict != null) {
        for (Object k : kanjiDict.keySet()) {
          String kanji = (String) k;
          JSONObject kanjiItem = (JSONObject) kanjiDict.get(kanji);
          if (kanjiItem != null) {
            //System.out.println(kanji);
            String sHiragana = (String) kanjiItem.get("hiragana");
            String sHvPhonetic = (String) kanjiItem.get("hv_phonetic");

            List<TFMTTNAKanjiDetailData> lstKanjiDetailData = new ArrayList<TFMTTNAKanjiDetailData>();

            JSONObject kanjiDetailDict = (JSONObject) kanjiItem.get("kanji_detail");
            if (kanjiDetailDict != null) {
              for (Object d: kanjiDetailDict.keySet()) {
                String sDetailChar = (String) d; 
                JSONObject kanjiDetailItem = (JSONObject) kanjiDetailDict.get(sDetailChar);
                if (kanjiDetailItem != null) {

                  String sDetailHvPhonetic = (String) kanjiDetailItem.get("hv_phonetic");
                  String sDetailOnKun = (String) kanjiDetailItem.get("on_kun");
                  String sDetailMeaning = (String) kanjiDetailItem.get("meaning");

                  lstKanjiDetailData.add(
                    new TFMTTNAKanjiDetailData(sDetailChar, sDetailHvPhonetic, sDetailOnKun, sDetailMeaning)
                    );

                }
              }
            }

            TFMTTNAKanjiData kanjiDataObject = new TFMTTNAKanjiData(kanji, sHiragana, sHvPhonetic, lstKanjiDetailData);
            lstArticleKanjis.add(kanjiDataObject);

          }
        }
      }

      List<JBGKanjiItem> lstBuiltWordForTest = new ArrayList<JBGKanjiItem>();

      if (lstBuiltWords != null) {
        for (Object kObj: lstBuiltWords) {
          JSONObject wordItem = (JSONObject) kObj;
          if (wordItem != null) {
            String sId = (String) wordItem.get("id");
            String sKanji = (String) wordItem.get("kanji");
            String sHiragana = (String) wordItem.get("hiragana");
            String sHv = (String) wordItem.get("hv");
            String sMeaning = (String) wordItem.get("meaning");
            int iTestCount = getIntFromJson(wordItem, "testCount");
            int iCorrectCount = getIntFromJson(wordItem, "correctCount");
            int iWeightValue = getIntFromJson(wordItem, "weightValue");

            JBGKanjiItem kjItem = new JBGKanjiItem(sId, sKanji, sHiragana, sHv, sMeaning, iTestCount, iCorrectCount, iWeightValue);
            lstBuiltWordForTest.add(kjItem);

            addKanjiToMainKanjiList(kjItem);
          }
        }

      }

      //rescan lstArticleKanjis and lstKanjiForTest
      //neu nhu kanji nao nam trong lstArticleKanjis, da co san trong dataKanjiItems
      // ma chua duoc add vao lstKanjiForTest thi tu dong add vao lstKanjiForTest
      for (TFMTTNAKanjiData articleWord: lstArticleKanjis) {
        JBGKanjiItem kItem = getKanjiContentFromMainKanjiList(articleWord.getKanji());
        if (kItem != null && !isKanjiInList(articleWord.getKanji(), lstBuiltWordForTest) ) {
          lstBuiltWordForTest.add(kItem.cloneItem());
        }
      }

      TFMTTNAData tna = new TFMTTNAData(sArticleId, sArticleTitle, lstTNASentences, lstArticleKanjis, lstBuiltWordForTest,
        ttlTNATests, ttlTNATestCorrects); //not use ttlTNAKanjiTests yet

      this.dataTNAItems.add(tna);

      return true;

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

    return false;
  }

  public void increaseKanjiTestCorrect(final String kanji) {
    for (JBGKanjiItem kj: this.dataKanjiItems) {
      if (kj.getKanji().equals(kanji)) {
        kj.setTestCount(kj.getTestCount() + 1);
        kj.setCorrectCount(kj.getCorrectCount() + 1);
        break;
      }
    }
    TFMTTNAData currentTNA = getSelectedTNA();
    if (currentTNA != null) {
      for (JBGKanjiItem kj: currentTNA.getKanjisForTest()) {
        if (kj.getKanji().equals(kanji)) {
          kj.setTestCount(kj.getTestCount() + 1);
          kj.setCorrectCount(kj.getCorrectCount() + 1);
          break;
        }
      }
    }
  }

  public TFMTTNAData getSelectedTNA() {
    if (this.selectedArticleId == null) return null;
    return getTNAById(this.selectedArticleId);
  }

  public TFMTTNAData getTNAById(final String sSelTNAId) {
    TFMTTNAData currentTNA = null;
    for (int i = 0; i < dataTNAItems.size(); i++) {
        currentTNA = dataTNAItems.get(i);
        String sId = currentTNA.getId().toString();
        if (sId.equals(sSelTNAId)) {
          return currentTNA;
        }
    }
    return null;
  }

  public boolean deleteTNAById(final String sSelTNAId) {
    boolean deleted = false;
    TFMTTNAData currentTNA = null;
    for (int i = 0; i < dataTNAItems.size(); i++) {
        currentTNA = dataTNAItems.get(i);
        String sId = currentTNA.getId().toString();
        if (sId.equals(sSelTNAId)) {
          dataTNAItems.remove(i);
          deleted = true;
          break;
        }
    }
    return deleted;
  }

  //bring an article to top of the list
  public void setPriorityTNAById(final String sSelTNAId) {
    List<TFMTTNAData> newDataTNAItems = new ArrayList<TFMTTNAData>();
    boolean foundItem = false;
    TFMTTNAData currentTNA = null;
    for (int i = 0; i < dataTNAItems.size(); i++) {
        currentTNA = dataTNAItems.get(i);
        String sId = currentTNA.getId().toString();
        if (sId.equals(sSelTNAId)) {
          newDataTNAItems.add(currentTNA);
          foundItem = true;
          break;
        }
    }
    if (!foundItem) return;
    for (int i = 0; i < dataTNAItems.size(); i++) {
        currentTNA = dataTNAItems.get(i);
        String sId = currentTNA.getId().toString();
        if (!sId.equals(sSelTNAId)) {
          newDataTNAItems.add(currentTNA);
        }
    }
    dataTNAItems = newDataTNAItems;
  }

  public TFMTTNGData getSelectedTNG() {
    if (this.selectedGrammarId == null) return null;
    return getTNGById(this.selectedGrammarId);
  }

  public TFMTTNGData getTNGById(final String sSelTNGId) {
    TFMTTNGData currentTNG = null;
    for (int i = 0; i < dataTNGItems.size(); i++) {
        currentTNG = dataTNGItems.get(i);
        String sId = currentTNG.getId().toString();
        if (sId.equals(sSelTNGId)) {
          return currentTNG;
        }
    }
    return null;
  }

  public boolean moveTNGItemToTail(final String id) {
    TFMTTNGData tng = this.getSelectedTNG();
    if (tng == null) return false;

    TFMTTNGPatternData pMovedItem = null;
    List<TFMTTNGPatternData> lstNewOrderedPatterns = new ArrayList<TFMTTNGPatternData>();
    for (TFMTTNGPatternData pItem: tng.getGrammarPattern()) {
      if (!pItem.getId().toString().equals(id)) {
        lstNewOrderedPatterns.add(pItem);
      }
      else {
        pMovedItem = pItem;
      }
    }
    if (pMovedItem != null) {
      lstNewOrderedPatterns.add(pMovedItem);
    }
    else {
      return false;
    }

    tng.setGrammarPattern(lstNewOrderedPatterns);

    if (tng.getGrammarPattern().size() == lstNewOrderedPatterns.size())
      return true;
    return false;
  }

  public boolean loadTNGJsonFromFile(final String fileName) {
    if (this.testStarted) return false;
    if (!this.tfmtLoaded) return false;

    try {

      JSONParser parser = new JSONParser();
      Object obj = parser.parse(new FileReader(fileName));
      JSONArray patternList = (JSONArray) obj;
      String sGrammarTitle = fileName.substring(fileName.lastIndexOf('/')+1, fileName.lastIndexOf('.'));
      int ttlTNGTests = 0; //getIntFromJson(dataPart, "totalTests");
      int ttlTNGTestCorrects = 0; //getIntFromJson(dataPart, "totalCorrectTests");

      System.out.println(sGrammarTitle); 
      System.out.println("Prepare loading: " + String.valueOf(patternList.size()) + " patterns");

      if (patternList.size() < 1) return false;

        //System.out.println("  Loading grammar: " + sGrammarTitle);
        //System.out.println("    Total grammar patterns: " + String.valueOf(patternList.size()));

        List<TFMTTNGPatternData> lstTNGPatterns = new ArrayList<TFMTTNGPatternData>();
        if (patternList != null) {
          for (Object sObj: patternList) {
            //System.out.println(kObj.toString());
            JSONObject objPattern = (JSONObject) sObj;
            String sPatternId = (String) objPattern.get("id");
            String sTitle = (String) objPattern.get("title");
            JSONArray descriptionList = (JSONArray) objPattern.get("description");
            JSONArray sentenceList = (JSONArray) objPattern.get("sentence");

            List<String> lstDescriptions = new ArrayList<String>();
            if (descriptionList != null) {
              for (Object kObj: descriptionList) {
                String sDesc = (String) kObj;
                System.out.println(sDesc);
                lstDescriptions.add(sDesc);
              }
            }

            List<TFMTTNGPatternSentence> lstSentences = new ArrayList<TFMTTNGPatternSentence>();

            for (Object oSentence: sentenceList) {
              JSONObject jsonSentence = (JSONObject) oSentence;
              String sId = (String) jsonSentence.get("id");
              String sSentence = (String) jsonSentence.get("sentence");
              JSONArray meaningLst = (JSONArray) jsonSentence.get("meaning");
              List<String> lstMeanings = new ArrayList<String>();
              for (Object oM: meaningLst) {
                lstMeanings.add((String) oM);
              }
              TFMTTNGPatternSentence sen = null;
              if (sId != null)
                sen = new TFMTTNGPatternSentence(sId, sSentence, lstMeanings);
              else
                sen = new TFMTTNGPatternSentence(sSentence, lstMeanings);
              lstSentences.add(sen);
            }

            TFMTTNGPatternData patternItem = new TFMTTNGPatternData(sPatternId, sTitle, lstDescriptions, lstSentences);
            lstTNGPatterns.add(patternItem);
          }
        }

        TFMTTNGData tng = new TFMTTNGData(sGrammarTitle, lstTNGPatterns);
        this.dataTNGItems.add(tng);

      return true;

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

    return false;
  }

  public String getGrammarMP3FileName(final String patternId, final String sentenceId) {
      StringBuilder sb = new StringBuilder(this.grammarMP3FolderPath + "/" + patternId + "/" + sentenceId + ".mp3");
      return sb.toString();
  }

  public String getArticleMP3FileName(final String articleId) {
      StringBuilder sb = new StringBuilder(this.articleMP3FolderPath + "/" + articleId + ".mp3");
      return sb.toString();
  }

  //if lastWorkDate is today or yesterday, it is OK
  public boolean isPenaltyApplied() {
    String s = getYesterdayString();
    if (s.equals(this.lastWorkDate)) return false;
    if (getTodayString().equals(this.lastWorkDate)) return false;
    return true;
  }

  public void increaseJCoin(final int step) {
    if (!isPenaltyApplied()) 
      this.jCoin += step;
    else
      this.jCoin += 1; //penalty
  }

  public boolean decreaseJCoin(final int step) {
    if (this.jCoin - step < 0) return false;
    this.jCoin -= step;
    return true;
  }

  public int dbKanjisCount() {
      int iVal = 0;
      try {
          Class.forName(JBGConstants.JDBC_DRIVER);
          Connection con = DriverManager.getConnection(JBGConstants.JDBC_CON_STR, 
            JBGConstants.DB_USER, JBGConstants.DB_PASSWORD);
          Statement stmt=con.createStatement();  
          ResultSet rs=stmt.executeQuery("select count(*) from consolidated_kanji_dict");  
          if (rs.next())
              iVal = rs.getInt(1);  
          con.close();
      }
      catch(Exception e) {
          System.out.println(e);
      }
      return iVal;
  }

  public List<JBGKanjiItem> dbKanjiSearch(final String sKanji) {
      List<JBGKanjiItem> lstRes = new ArrayList<JBGKanjiItem>();
      int iRecId = 0;
      try {
          Class.forName(JBGConstants.JDBC_DRIVER);
          Connection con = DriverManager.getConnection(JBGConstants.JDBC_CON_STR, 
            JBGConstants.DB_USER, JBGConstants.DB_PASSWORD);
          Statement stmt=con.createStatement();  
          ResultSet rs=stmt.executeQuery(
              String.format("SELECT * FROM consolidated_kanji_dict WHERE kanji_char='%s'", sKanji));  
          while (rs.next()) {
              iRecId = rs.getInt(1);

              JBGKanjiItem kjItem = new JBGKanjiItem( 
                  rs.getString("kanji_char"), rs.getString("hv_phonetic"), rs.getString("on_kun"), 
                  rs.getString("meaning"));
              lstRes.add(kjItem);

          }
          con.close();
      }
      catch(Exception e) {
          System.out.println(e);
      }
      return lstRes;
  }

  public List<JBGKanjiItem> dbKanjiSearchString(final String sKanjiString) {
      List<JBGKanjiItem> lstRes = new ArrayList<JBGKanjiItem>();
      for (String s: sKanjiString.split("")) {
        String sT = s.trim();
        List<JBGKanjiItem> lst = dbKanjiSearch(sT);
        if (lst.size() > 0) {
          for (JBGKanjiItem item: lst)
            lstRes.add(item);
        }
      }
      return lstRes;
  }

  public String lookupKanjiValues(final String selText) {
      boolean isArticleKJFound = false;
      TFMTTNAData currentTNA = getSelectedTNA();
      TFMTTNAKanjiData currentTNAKanji = null;
      StringBuilder sb = new StringBuilder();

      if (currentTNA != null) {
        for (int i = 0; i < currentTNA.getArticleKanjis().size(); i++) {
            currentTNAKanji = currentTNA.getArticleKanjis().get(i);
            if (currentTNAKanji.getKanji().equals(selText)) {
                isArticleKJFound = true;
                break;
            }
        }

        if (isArticleKJFound && currentTNAKanji != null) {
            sb.append(
                "In article:" +
                currentTNAKanji.getKanji() + " / " +
                currentTNAKanji.getHv() + "\n"
                );
        }

      }

      JBGKanjiItem mainItem = this.getKanjiFromMainKanjiList(selText);

      if (mainItem != null) {
          sb.append(
              "Main dictionary:" +
              mainItem.getKanji() + " / " +
              mainItem.getHiragana() + "/ " +
              mainItem.getHv() + " / " +
              mainItem.getMeaning() + "\n"
              );
      }
      
      List<JBGKanjiItem> lstRelatedKanjis = this.getRelatedKanjiFromMainKanjiList(selText);
      if (lstRelatedKanjis.size() > 0) {
          sb.append("Related:\n");
          for (JBGKanjiItem item: lstRelatedKanjis) {
              sb.append(item.getKanji() + " / " +
                  item.getHiragana() + " / " +
                  item.getHv() + " / " +
                  item.getMeaning() + "\n"
                  );
          }
      }

      List<JBGKanjiItem> lstRelatedKanjisInDb = this.dbKanjiSearchString(selText);
      if (lstRelatedKanjisInDb.size() > 0) {
          sb.append("KanjiDict:\n");
          for (JBGKanjiItem item: lstRelatedKanjisInDb) {
              sb.append(item.getKanji() + " / " +
                  item.getHiragana() + " / " +
                  item.getHv() + "\n" //+
                  //item.getMeaning().replace("<br>", "\n") + "\n"
                  );
          }
      }
      return sb.toString().trim();
  }

}