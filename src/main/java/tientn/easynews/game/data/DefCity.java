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

@JsonRootName(value = "city-def")
@JsonPropertyOrder({ "id", "name", "level", "capability", "pop", "troop", "owner", "x", "y"})
@Getter
@Setter
public class DefCity implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private UUID id;
  @Getter @Setter private String name;
  @Getter @Setter private int level;
  @Getter @Setter private DefCityCapability capability;
  @Getter @Setter private int pop;
  @Getter @Setter private int troop;
  @Getter @Setter private String owner;
  @Getter @Setter private int x;
  @Getter @Setter private int y;


  public DefCity(final String name, final int level, final DefCityCapability cap, 
    final int pop, final int troop, final String owner) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.level = level;
    this.capability = cap;
    this.pop = pop;
    this.troop = troop;
    this.owner = owner;
    this.x = 0;
    this.y = 0;
  }

  public DefCity(final String name, final int level, final DefCityCapability cap, final int pop, final int troop, final String owner, final int x, final int y) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.level = level;
    this.capability = cap;
    this.pop = pop;
    this.troop = troop;
    this.owner = owner;
    this.x = x;
    this.y = y;
  }

  public DefCity(final String id, final String name, final int level, final DefCityCapability cap, final int pop, final int troop, final String owner) {
    this.id = UUID.fromString(id);
    this.name = name;
    this.level = level;
    this.capability = cap;
    this.pop = pop;
    this.troop = troop;
    this.owner = owner;
    this.x = 0;
    this.y = 0;
  }

  public DefCity(final String id, final String name, final int level, final DefCityCapability cap, final int pop, final int troop, final String owner, final int x, final int y) {
    this.id = UUID.fromString(id);
    this.name = name;
    this.level = level;
    this.capability = cap;
    this.pop = pop;
    this.troop = troop;
    this.owner = owner;
    this.x = x;
    this.y = y;
  }

  public DefCity cloneItem() {
    DefCity clone = new DefCity(
      this.name,
      this.level,
      this.capability,
      this.pop,
      this.troop,
      this.owner
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.name + "|" +  
      this.owner + "|" +  
      String.valueOf(this.level) + "|" + 
      this.capability.toString() + "|" +
      String.valueOf(this.pop) + "|" + 
      String.valueOf(this.troop) + "|" + 
      String.valueOf(this.x) + "|" + 
      String.valueOf(this.y)
      ).toString();
  }

}