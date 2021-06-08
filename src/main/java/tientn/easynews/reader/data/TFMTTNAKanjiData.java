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

@JsonRootName(value = "tfmt-tna-kanji")
@Getter
@Setter
public class TFMTTNAKanjiData implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private String kanji;
  @Getter @Setter private String hiragana;
  @Getter @Setter private String hv;
  @Getter @Setter private List<TFMTTNAKanjiDetailData> kanjis;

  public TFMTTNAKanjiData(final String kanji, final String hira, final String hv, final List<TFMTTNAKanjiDetailData> lst) {
    this.id = UUID.randomUUID();
    this.kanji = kanji;
    this.hiragana = hira;
    this.hv = hv;
    this.kanjis = lst;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.kanji + "|" +  
      this.hiragana + "|" +  
      this.hv
      ).toString();
  }

}