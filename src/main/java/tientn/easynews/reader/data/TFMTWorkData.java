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

import tientn.easynews.reader.data.JBGConstants;

//Class for exporting / importing TFMT Work Data
@JsonRootName(value = "tfmt-work")
public class TFMTWorkData {
  @Getter
  @Setter
  private List<JBGKanjiItem> kanjiWorks;
  @Getter
  @Setter
  private List<TFMTTNAData> articleWorks;
  @Getter
  @Setter
  private String lastWorkDate;
  @Getter
  @Setter
  private int totalKanjis = 0;
  @Getter
  @Setter
  private int totalMatchedKanjis = 0;
  @Getter
  @Setter
  private int totalKanjiTests = 0;
  @Getter
  @Setter
  private int jCoin = 0;

  public TFMTWorkData() {
    this.kanjiWorks = new ArrayList<JBGKanjiItem>();
    this.articleWorks = new ArrayList<TFMTTNAData>();
    this.totalKanjis = 0;
    this.totalMatchedKanjis = 0;
    this.totalKanjiTests = 0;
    this.jCoin = 0;
  }

  public void setData(final List<JBGKanjiItem> lstKanjis, final List<TFMTTNAData> lstTNA, final String lastDate, final int iCoin) {
    this.kanjiWorks = lstKanjis;
    this.articleWorks = lstTNA;
    this.lastWorkDate = lastDate;
    this.jCoin = iCoin;

    this.totalMatchedKanjis = 0;
    this.totalKanjiTests = 0;
    this.totalKanjis = lstKanjis.size();
    for (JBGKanjiItem item: lstKanjis) {
      if (item.getCorrectCount() >= JBGConstants.KANJI_MIN_TEST_CORRECT) {
        this.totalMatchedKanjis += 1;
      }
      totalKanjiTests += item.getTestCount();

    }

  }


}