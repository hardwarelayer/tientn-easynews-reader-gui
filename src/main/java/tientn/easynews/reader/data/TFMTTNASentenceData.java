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
@JsonRootName(value = "tfmt-tna-sentence")
public class TFMTTNASentenceData {
  @Getter
  @Setter
  UUID id;
  @Getter
  @Setter
  private String sentence;
  @Getter
  @Setter
  private String englishMeaning;
  @Getter
  @Setter
  private List<String> sentenceKanjis;

  public TFMTTNASentenceData() {
    this.id = UUID.randomUUID();
    this.sentenceKanjis = new ArrayList<String>();
  }

  public TFMTTNASentenceData(final String sentence, final String englishMeaning, List<String> kanjis) {
    this.id = UUID.randomUUID();
    this.sentenceKanjis = new ArrayList<String>();
    setData(sentence, englishMeaning, kanjis);
  }

  public TFMTTNASentenceData(final String id, final String sentence, final String englishMeaning, List<String> kanjis) {
    this.id = UUID.fromString(id);
    this.sentenceKanjis = new ArrayList<String>();
    setData(sentence, englishMeaning, kanjis);
  }

  public void setData(final String sentence, final String englishMeaning, List<String> kanjis) {
    this.sentenceKanjis = kanjis;
    this.sentence = sentence;
    this.englishMeaning = englishMeaning;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(
      this.sentence + "|" +  
      this.englishMeaning + "|");
    if (this.sentenceKanjis.size() < 1)
      sb.append("<no_kanji>");
    else {
      for (String s: this.sentenceKanjis) {
        sb.append(s.toString());
      }
    }
    return sb.toString();
  }

}