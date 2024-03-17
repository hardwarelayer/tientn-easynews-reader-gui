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

@JsonRootName(value = "cost-type-def")
@JsonPropertyOrder({ "type", "quantity" })
@Getter
@Setter
public class DefBuildingCostType implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private String type;
  @Getter @Setter private int quantity;

  public DefBuildingCostType(final String type, final int quantity) {
    this.type = type;
    this.quantity = quantity;
  }

  public DefBuildingCostType cloneItem() {
    DefBuildingCostType clone = new DefBuildingCostType(
        this.type,
        this.quantity
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "DefBuildingCostType:" +
      this.type + "|" +
      String.valueOf(this.quantity)
      ).toString();
  }

}