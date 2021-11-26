package tientn.easynews.reader.data;

/*Taken from tientn-jboard*/
import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonRootName(value = "kanji-work")
@JsonPropertyOrder({ "id", "kanji", "hira", "hv", "meaning", "test", "correct", "weight"})
@Getter
@Setter
public class JBGKanjiItem implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private String kanji;
  @Getter @Setter private String hiragana;
  @Getter @Setter private String hv;
  @Getter @Setter private String meaning;
  @Getter @Setter private int testCount;
  @Getter @Setter private int correctCount;
  @Getter @Setter private int weightValue;


  public JBGKanjiItem(final String kanji, final String hiragana, final String hv, final String meaning) {
    this.id = UUID.randomUUID();
    this.kanji = kanji;
    this.hiragana = hiragana;
    this.hv = hv;
    this.meaning = meaning;
    this.testCount = 0;
    this.correctCount = 0;
    this.weightValue = 0;
  }

  public JBGKanjiItem(final String id, final String kanji, final String hiragana, final String hv, final String meaning, final int testCount, final int correctCount, final int weightValue) {
    this.id = UUID.fromString(id);
    this.kanji = kanji;
    this.hiragana = hiragana;
    this.hv = hv;
    this.meaning = meaning;
    this.testCount = testCount;
    this.correctCount = correctCount;
    this.weightValue = weightValue;
  }

  public JBGKanjiItem(final String kanji, final String hiragana, final String hv, final String meaning, final int testCount, final int correctCount, final int weightValue) {
    this.id = UUID.randomUUID();
    this.kanji = kanji;
    this.hiragana = hiragana;
    this.hv = hv;
    this.meaning = meaning;
    this.testCount = testCount;
    this.correctCount = correctCount;
    this.weightValue = weightValue;
  }

  public void increaseTest(final boolean isOK) {
    this.testCount++;
    if (isOK) {
      this.correctCount++;
    }
    else {
      //neu 1 tu da OK nhung sau do nhac lai ma bi sai thi correctCount se giam xuong
      //no se duoc dua ra test thuong xuyen hon cac tu OK khac
      if (this.correctCount > 0)
        this.correctCount--;
    }
  }

  public JBGKanjiItem cloneItem() {
    JBGKanjiItem clone = new JBGKanjiItem(
        this.kanji,
        this.hiragana,
        this.hv,
        this.meaning
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.kanji + "|" +  
      this.hiragana + "|" +  
      this.hv + "|" +  
      this.meaning + "|" +  
      String.valueOf(this.testCount) + "|" + String.valueOf(this.correctCount) + "|" + String.valueOf(this.weightValue)
      ).toString();
  }

}