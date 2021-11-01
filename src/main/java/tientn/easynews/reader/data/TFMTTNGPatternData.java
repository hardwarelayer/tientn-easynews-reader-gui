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
import java.util.stream.Collectors;

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
//Note: never use property name like "descriptions"/"sentences" for List type, because it will have issue with object factory and getter
@JsonRootName(value = "tfmt-tng-pattern")
public class TFMTTNGPatternData {
  @Getter
  @Setter
  UUID id;
  @Getter
  @Setter
  private String title;
  @Getter
  @Setter
  private List<String> description;
  @Getter
  @Setter
  private List<TFMTTNGPatternSentence> sentence;

  public TFMTTNGPatternData() {
    this.id = UUID.randomUUID();
    this.title = "";
    this.description = new ArrayList<String>();
    this.sentence = new ArrayList<TFMTTNGPatternSentence>();
  }

  public TFMTTNGPatternData(final String title, List<String> description, List<TFMTTNGPatternSentence> sentence) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.description = description;
    this.sentence = sentence;
  }

  public TFMTTNGPatternData(final String id, final String title, List<String> description, List<TFMTTNGPatternSentence> sentence) {
    this.id = UUID.fromString(id);
    this.title = title;
    this.description = description;
    this.sentence = sentence;
  }

  public void setData(final String title, List<String> description, List<TFMTTNGPatternSentence> sentence) {
    this.title = title;
    this.description = description;
    this.sentence = sentence;
  }

  public int getTotalTests() {
    int iTtl = 0;
    for (TFMTTNGPatternSentence s: sentence) {
      iTtl += s.getTotalTests();
    }
    return iTtl;
  }

  public int getTotalCorrectTests() {
    int iTtl = 0;
    for (TFMTTNGPatternSentence s: sentence) {
      iTtl += s.getCorrectTests();
    }
    return iTtl;
  }

  public String getDescriptionAsString(final String sepChar) {
    return getDescription().stream()
                .map(Object::toString)
                .collect(Collectors.joining(sepChar));
  }

  public String getSentenceAsString(final String sepChar) {
    StringBuilder sb = new StringBuilder();
    for (TFMTTNGPatternSentence sen: this.sentence) {
      sb.append(sen.getSentence())
        .append(sepChar);
    }
    return sb.toString();
  }

  public String getSentenceAndMeaningAsString(final String sepChar) {
    StringBuilder sb = new StringBuilder();
    for (TFMTTNGPatternSentence sen: this.sentence) {
      sb.append(sen.getSentence())
        .append(sepChar)
        .append(sen.getMeaningAsString(sepChar))
        .append("\n");
    }
    return sb.toString();
  }

  public String getIdStrFromSentence(final String sentence) {
    for (TFMTTNGPatternSentence sen: this.sentence) {
      if (sen.getSentence().equals(sentence)) {
        return sen.getId().toString();
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      String.valueOf(this.id) + "|" +  
      this.title + "|" +
      getDescriptionAsString("-") + "|" +
      String.valueOf(this.sentence.size())
      ).toString();
  }

}