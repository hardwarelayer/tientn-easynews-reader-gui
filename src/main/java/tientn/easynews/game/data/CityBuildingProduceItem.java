package tientn.easynews.game.data;

import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonRootName(value = "city-inventory-item")
@JsonPropertyOrder({ "id", "name", "quantity"})
@Getter
@Setter
public class CityBuildingProduceItem implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private String name;
  @Getter @Setter private int quantity;

  public CityBuildingProduceItem(final String name, final int qty) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.quantity = qty;
  }

  public CityBuildingProduceItem(final String id, final String name, final int qty) {
    this.id = UUID.fromString(id);
    this.name = name;
    this.quantity = qty;
  }

  public CityBuildingProduceItem cloneItem() {
    CityBuildingProduceItem clone = new CityBuildingProduceItem(
      this.name,
      this.quantity
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.name + "|" +  
      this.quantity
      ).toString();
  }

}