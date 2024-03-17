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

@JsonRootName(value = "city-capability-def")
@JsonPropertyOrder({ "land", "farm", "iron", "stone", "clay", "water", "terrain"})
@Getter
@Setter
public class DefCityCapability implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private int land;
  @Getter @Setter private int farm;
  @Getter @Setter private int iron;
  @Getter @Setter private int stone;
  @Getter @Setter private int clay;
  @Getter @Setter private int water;
  @Getter @Setter private int terrain;

  public DefCityCapability(final int land, final int farm, final int iron, final int stone, final int clay, final int water, final int terrain) {
    this.land = land;
    this.farm = farm;
    this.iron = iron;
    this.stone = stone;
    this.clay = clay;
    this.water = water;
    this.terrain = terrain;
  }

  public DefCityCapability(final String id, final int land, final int farm, final int iron, final int stone, final int clay, final int water, final int terrain) {
    this.id = UUID.fromString(id);
    this.land = land;
    this.farm = farm;
    this.iron = iron;
    this.stone = stone;
    this.clay = clay;
    this.water = water;
    this.terrain = terrain;
  }

  public DefCityCapability cloneItem() {
    DefCityCapability clone = new DefCityCapability(
        this.land,
        this.farm,
        this.iron,
        this.stone,
        this.clay,
        this.water,
        this.terrain
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      String.valueOf(this.land) + "|" + 
      String.valueOf(this.farm) + "|" + 
      String.valueOf(this.iron) + "|" + 
      String.valueOf(this.stone) + "|" + 
      String.valueOf(this.clay) + "|" + 
      String.valueOf(this.water) + "|" + 
      String.valueOf(this.terrain)
      ).toString();
  }

}