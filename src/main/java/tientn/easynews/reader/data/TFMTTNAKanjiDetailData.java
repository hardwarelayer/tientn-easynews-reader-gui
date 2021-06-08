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

@JsonRootName(value = "tfmt-tna-kanji-detail")
@Getter
@Setter
public class TFMTTNAKanjiDetailData implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private String kanji;
  @Getter @Setter private String hv;
  @Getter @Setter private String onkun;
  @Getter @Setter private String meaning;

  public TFMTTNAKanjiDetailData(final String kanji, final String hv, final String ok, final String meaning) {
    this.id = UUID.randomUUID();
    this.kanji = kanji;
    this.hv = hv;
    this.onkun = ok;
    this.meaning = meaning;
  }

  public TFMTTNAKanjiDetailData(final String id, final String kanji, final String hv, final String ok, final String meaning) {
    this.id = UUID.fromString(id);
    this.kanji = kanji;
    this.hv = hv;
    this.onkun = ok;
    this.meaning = meaning;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.kanji + "|" +  
      this.hv + "|" +
      this.onkun + "|" +
      this.meaning
      ).toString();
  }

}