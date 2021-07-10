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
import java.util.concurrent.ThreadLocalRandom;
import java.lang.StringBuilder;
import java.time.Instant;

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

public class ReaderModel {

  @Getter @Setter private int currentWorkMode = JBGConstants.TEST_WORD_IN_MAJOR_LIST;

  @Getter
  private List<JBGKanjiItem> dataKanjiItems;
  @Getter
  private List<TFMTTNAData> dataTNAItems;
  @Getter private List<JBGKanjiItem> subsetRecords = null;
  @Getter @Setter private boolean needRefresh;
  @Getter @Setter private boolean testStarted;
  @Getter @Setter private boolean readStarted;
  @Getter private int totalKanjis = 0;
  @Getter private int totalMatchedKanjis = 0;
  @Getter private int totalKanjiTests = 0;
  @Getter private boolean tfmtLoaded = false;
  @Getter private String selectedArticleId;

  @Getter @Setter private int jCoin = 0;

  public ReaderModel() {
    initKanjis();

    needRefresh = false;
    testStarted = false;
  }

  private void initKanjis() {
    this.dataKanjiItems = new ArrayList<JBGKanjiItem>();
    this.dataTNAItems = new ArrayList<TFMTTNAData>();
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

  public void setSelectedArticleId(final String s) {
    if (!s.equals(this.selectedArticleId)) {
      this.needRefresh = true;
    }
    this.selectedArticleId = s;
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

  public List<JBGKanjiItem> getSimilarKanjiFromMainKanjiList(final String kanji) {
    List<JBGKanjiItem> lst = new ArrayList<JBGKanjiItem>();
    for (JBGKanjiItem item: this.dataKanjiItems) {
      if (item.getKanji().equals(kanji) || item.getKanji().contains(kanji) || kanji.contains(item.getKanji())) {
        lst.add(item.cloneItem());
      }
    }
    return lst;
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
        this.subsetRecords = currentTNA.getKanjisForTest();
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
        this.subsetRecords = currentTNA.getKanjisForTest();
        return this.subsetRecords;
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
      tfmt.setData(this.dataKanjiItems, this.dataTNAItems, this.jCoin);
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

  private int getIntFromJson(JSONObject obj, final String fld) {
    Long lVal = (Long) obj.get(fld);
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

  private boolean addBuiltWordToGrandKanjiList(JBGKanjiItem kItem) {
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

      //System.out.println("Total load kanjis: " + String.valueOf(kanjiList.size()));
      //System.out.println("Total load articles: " + String.valueOf(articleList.size()));
      //System.out.println("Open with jcoin " + String.valueOf(jsonObject.get("jcoin")));

      if (kanjiList.size() < 1) return false;

      boolean hasNewKanjiFromTNA = false;

      this.totalKanjis = 0;
      this.totalMatchedKanjis = 0;
      this.totalKanjiTests = 0;
      this.dataKanjiItems.clear();
      this.dataTNAItems.clear();
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

        System.out.println("  Loading article " + sArticleId + " " + sArticleTitle);
        System.out.println("    Total sentences: " + String.valueOf(sentenceList.size()));
        System.out.println("    Total kanjis: " + String.valueOf(articleKanjiList.size()));
        System.out.println("    Total built words for test: " + String.valueOf(lstBuiltWords.size()));
        if (lstProblematicWords != null)
          System.out.println("    Total problematic words: " + String.valueOf(lstProblematicWords.size()));

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

      if (hasNewKanjiFromTNA) {
        recountGrandKanjiTests();
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
      JSONArray sentenceList = (JSONArray) dataPart.get("sentences");
      JSONObject kanjiDict = (JSONObject) dataPart.get("kanjis");
      JSONArray lstBuiltWords = (JSONArray) dataPart.get("kanjisForTest");
      int ttlTNATests = 0; //getIntFromJson(dataPart, "totalTests");
      int ttlTNATestCorrects = 0; //getIntFromJson(dataPart, "totalCorrectTests");
      int ttlTNAKanjiTests = 0; //getIntFromJson(dataPart, "testTotalOfKanjiForTest");

      System.out.println(sArticleTitle); 
      System.out.println("Total loaded kanjis: " + String.valueOf(kanjiDict.size()));
      System.out.println("Total loaded sentences: " + String.valueOf(sentenceList.size()));

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

                  String sDetailHvPhonetic = (String) kanjiDetailItem.get("h_phonetic");
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

  public void increaseJCoin(final int step) {
    this.jCoin += step;
  }

  public boolean decreaseJCoin(final int step) {
    if (this.jCoin - step < 0) return false;
    this.jCoin -= step;
    return true;
  }
}