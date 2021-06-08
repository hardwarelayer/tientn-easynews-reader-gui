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
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;
import java.lang.StringBuilder;
import java.time.Instant;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.net.URL;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import tientn.easynews.reader.data.JBGConstants;

//Class for exporting / importing TFMT Work Data
@JsonRootName(value = "tfmt-tna")
public class TFMTTNAData {
  @Getter
  @Setter
  UUID id;
  @Getter
  @Setter
  private String articleTitle;
  @Getter
  @Setter
  private List<TFMTTNASentenceData> articleSentences;
  @Getter
  @Setter
  private List<TFMTTNAKanjiData> articleKanjis;
  @Getter
  @Setter
  private List<JBGKanjiItem> kanjisForTest;
  @Getter
  @Setter
  private int totalTests = 0;
  @Getter
  @Setter
  private int totalCorrectTests = 0;

  public TFMTTNAData() {
    this.id = UUID.randomUUID();
    this.articleSentences = new ArrayList<TFMTTNASentenceData>();
    this.articleKanjis = new ArrayList<TFMTTNAKanjiData>();
    this.kanjisForTest = new ArrayList<JBGKanjiItem>();
    this.articleTitle = "";
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  //use on loading the original TNA file, without kanjis for tests
  public TFMTTNAData(final String title, final List<TFMTTNASentenceData> lstSentences, final List<TFMTTNAKanjiData> lstKanjis) {
    this.id = UUID.randomUUID();
    this.articleSentences = lstSentences;
    this.articleKanjis = lstKanjis;
    this.kanjisForTest = new ArrayList<JBGKanjiItem>();
    this.articleTitle = title;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  public TFMTTNAData(final String id, final String title, final List<TFMTTNASentenceData> lstSentences, final List<TFMTTNAKanjiData> lstKanjis) {
    this.id = UUID.fromString(id);
    this.articleSentences = lstSentences;
    this.articleKanjis = lstKanjis;
    this.kanjisForTest = new ArrayList<JBGKanjiItem>();
    this.articleTitle = title;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  public TFMTTNAData(final String id, final String title, final List<TFMTTNASentenceData> lstSentences, final List<TFMTTNAKanjiData> lstKanjis, final List<JBGKanjiItem> lstBuiltWords) {
    this.id = UUID.fromString(id);
    this.articleSentences = lstSentences;
    this.articleKanjis = lstKanjis;
    this.kanjisForTest = lstBuiltWords;
    this.articleTitle = title;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  public void setData(final String title, final List<TFMTTNASentenceData> lstSentences, final List<TFMTTNAKanjiData> lstKanjis, final List<JBGKanjiItem> lstKanjisForTest, final int ttlTest, final int ttlCorrect) {
    this.articleTitle = title;
    this.articleSentences = lstSentences;
    this.articleKanjis = lstKanjis;
    this.kanjisForTest = lstKanjisForTest;
    this.totalTests = ttlTest;
    this.totalCorrectTests = ttlCorrect;
  }

  public void setData(final String title, final List<TFMTTNASentenceData> lstSentences, final List<TFMTTNAKanjiData> lstKanjis, final List<JBGKanjiItem> lstKanjisForTest) {
    this.articleTitle = title;
    this.articleSentences = lstSentences;
    this.articleKanjis = lstKanjis;
    this.kanjisForTest = lstKanjisForTest;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

}