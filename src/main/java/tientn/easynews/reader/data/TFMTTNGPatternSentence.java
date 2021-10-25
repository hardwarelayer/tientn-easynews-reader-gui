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
//Note: never use property name like "meanings" for List type, because it will have issue with object factory and getter
@JsonRootName(value = "tfmt-tng-pattern-sentence")
public class TFMTTNGPatternSentence {
  @Getter
  @Setter
  UUID id;
  @Getter
  @Setter
  private String sentence;
  @Getter
  @Setter
  private List<String> meaning;
  @Getter
  @Setter
  private int correctTests;
  @Getter
  @Setter
  private int totalTests;

  public TFMTTNGPatternSentence() {
    this.id = UUID.randomUUID();
    this.sentence = "";
    this.meaning = new ArrayList<String>();
    this.correctTests = 0;
    this.totalTests = 0;
  }

  public TFMTTNGPatternSentence(final String sentence, List<String> meaning) {
    this.id = UUID.randomUUID();
    this.sentence = sentence;
    this.meaning = meaning;
    this.correctTests = 0;
    this.totalTests = 0;
  }

  public TFMTTNGPatternSentence(final String id, final String sentence, List<String> meaning) {
    this.id = UUID.fromString(id);
    this.sentence = sentence;
    this.meaning = meaning;
    this.correctTests = 0;
    this.totalTests = 0;
  }

  public TFMTTNGPatternSentence(final String id, final String sentence, List<String> meaning, final int correctTests, final int totalTests) {
    this.id = UUID.fromString(id);
    this.sentence = sentence;
    this.meaning = meaning;
    this.correctTests = correctTests;
    this.totalTests = totalTests;
  }

  public void setData(final String sentence, List<String> meaning) {
    this.sentence = sentence;
    this.meaning = meaning;
  }

  public void setData(final String sentence, List<String> meaning, final int correctTests, final int totalTests) {
    this.sentence = sentence;
    this.meaning = meaning;
    this.correctTests = correctTests;
    this.totalTests = totalTests;
  }

  public String getMeaningAsString(final String sepChar) {
    return this.meaning.stream()
                .map(Object::toString)
                .collect(Collectors.joining(sepChar));
  }
}