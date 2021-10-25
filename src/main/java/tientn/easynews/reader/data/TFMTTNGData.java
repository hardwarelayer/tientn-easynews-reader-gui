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
//Note: never use property name like "grammarPatterns"/"problematicPatternIds" for List type, because it will have issue with object factory and getter
@JsonRootName(value = "tfmt-tng")
public class TFMTTNGData {
  @Getter
  @Setter
  UUID id;
  @Getter
  @Setter
  private String grammarTitle;
  @Getter
  @Setter
  private List<TFMTTNGPatternData> grammarPattern;
  @Getter
  @Setter
  private List<String> problematicPatternId;
  @Getter
  @Setter
  private int totalTests = 0;
  @Getter
  @Setter
  private int totalCorrectTests = 0;

  public TFMTTNGData(final String sTitle) {
    this.id = UUID.randomUUID();
    this.grammarPattern = new ArrayList<TFMTTNGPatternData>();
    this.problematicPatternId = new ArrayList<String>();
    this.grammarTitle = sTitle;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  //use on loading the original TNA file, without kanjis for tests
  public TFMTTNGData(final String sTitle, final List<TFMTTNGPatternData> lstPatterns) {
    this.id = UUID.randomUUID();
    this.grammarPattern = lstPatterns;
    this.problematicPatternId = new ArrayList<String>();
    this.grammarTitle = sTitle;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  public TFMTTNGData(final String id, final String sTitle, final List<TFMTTNGPatternData> lstPatterns) {
    this.id = UUID.fromString(id);
    this.grammarPattern = lstPatterns;
    this.problematicPatternId = new ArrayList<String>();
    this.grammarTitle = sTitle;
    this.totalTests = 0;
    this.totalCorrectTests = 0;
  }

  public TFMTTNGData(final String id, final String sTitle, final List<TFMTTNGPatternData> lstPatterns, final int ttlTest, final int ttlCorrect) {
    this.id = UUID.fromString(id);
    this.grammarPattern = lstPatterns;
    this.problematicPatternId = new ArrayList<String>();
    this.grammarTitle = sTitle;
    this.totalTests = ttlTest;
    this.totalCorrectTests = ttlCorrect;
  }

  public TFMTTNGData(final String id, final String sTitle, final List<TFMTTNGPatternData> lstPatterns, final List<String> lstProblematicPatternIds, final int ttlTest, final int ttlCorrect) {
    this.id = UUID.fromString(id);
    this.grammarPattern = lstPatterns;
    this.problematicPatternId = lstProblematicPatternIds;
    this.grammarTitle = sTitle;
    this.totalTests = ttlTest;
    this.totalCorrectTests = ttlCorrect;
  }

  public void setData(final String sTitle, final List<TFMTTNGPatternData> lstPatterns, final List<String> lstProblematicPatternIds, final int ttlTest, final int ttlCorrect) {
    this.grammarPattern = lstPatterns;
    this.problematicPatternId = lstProblematicPatternIds;
    this.grammarTitle = sTitle;
    this.totalTests = ttlTest;
    this.totalCorrectTests = ttlCorrect;
  }

  public TFMTTNGPatternData findPatternById(final String id) {
    for (TFMTTNGPatternData pat: grammarPattern) {
      if (pat.getId().toString().equals(id)) {
        return pat;
      }
    }
    return null;
  }

}