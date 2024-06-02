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

@JsonRootName(value = "building-consume-def")
@JsonPropertyOrder({ "type", "quantity", "speed" })
@Getter
@Setter
public class DefBuildingConsume implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private String type;
  @Getter @Setter private int quantity;
  @Getter @Setter private int speed;

  public DefBuildingConsume(final String type, final int quantity, final int speed) {
    this.type = type;
    this.quantity = quantity;
    this.speed = speed;
  }

  public DefBuildingConsume cloneItem() {
    DefBuildingConsume clone = new DefBuildingConsume(
        this.type,
        this.quantity,
        this.speed
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "DefBuildingConsume:" +
      this.type + "|" +
      String.valueOf(this.quantity) + "|" +
      String.valueOf(this.speed)
      ).toString();
  }

}