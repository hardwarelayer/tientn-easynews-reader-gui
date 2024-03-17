package tientn.easynews.game.data;

/*Taken from tientn-jboard*/
import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonRootName(value = "talk-logic-value-range-def")
@JsonPropertyOrder({ "minimum"})
@Getter
@Setter
public class DefTalkLogicValueRange implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private int minimum;

  public DefTalkLogicValueRange(final int minimum) {
    this.minimum = minimum;
  }

  public DefTalkLogicValueRange cloneItem() {
    DefTalkLogicValueRange clone = new DefTalkLogicValueRange(
        this.minimum
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "ValueRange:" +
      String.valueOf(this.minimum)
      ).toString();
  }

}