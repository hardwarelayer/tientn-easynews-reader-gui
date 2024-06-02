package tientn.easynews.game.data;

import java.util.ArrayList;
/*Taken from tientn-jboard*/
import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonRootName(value = "city-building")
@JsonPropertyOrder({ "class", "name", "value", "value_range", "require_class", "bonus_class", "cost", "produce", "reactions" })
@Getter
@Setter
public class CityBuilding implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private String type;
  @Getter @Setter private String name;
  @Getter @Setter private int qty;
  @Getter @Setter private List<String> value;
  @Getter @Setter private DefTalkLogicValueRange valueRange;
  @Getter @Setter private List<String> requireClass;
  @Getter @Setter private List<String> bonusClass;
  @Getter @Setter private List<DefBuildingCostType> costLst;
  @Getter @Setter private List<DefBuildingConsume> consumeLst;
  @Getter @Setter private List<DefBuildingProduce> produceLst;
  @Getter @Setter private List<CityBuildingProduceItem> queueLst;
  @Getter @Setter private List<DefReaction> reaction;
  @Getter @Setter private boolean activeProduce;

  public CityBuilding(final String type, final String name, final List<String> value, final DefTalkLogicValueRange vRange, 
    final List<String> requireClass, final List<String> bonusClass, 
    final List<DefBuildingCostType> costLst, 
    final List<DefBuildingConsume> consumeLst, 
    final List<DefBuildingProduce> produceLst, 
    final List<DefReaction> reaction) {
    this.type = type;
    this.name = name;
    this.value = value;
    this.valueRange = vRange;
    this.requireClass = requireClass;
    this.bonusClass = bonusClass;
    this.costLst = costLst;
    this.consumeLst = consumeLst;
    this.produceLst = produceLst;
    this.queueLst = new ArrayList<CityBuildingProduceItem>();
    this.reaction = reaction;
    this.qty = 1;
    checkActiveProduceable();
  }

  public CityBuilding(final String type, final String name, final List<String> value, final DefTalkLogicValueRange vRange, 
    final List<String> requireClass, final List<String> bonusClass, 
    final List<DefBuildingCostType> costLst, 
    final List<DefBuildingConsume> consumeLst, 
    final List<DefBuildingProduce> produceLst, 
    final List<CityBuildingProduceItem> queueLst,
    final List<DefReaction> reaction) {
    this.type = type;
    this.name = name;
    this.value = value;
    this.valueRange = vRange;
    this.requireClass = requireClass;
    this.bonusClass = bonusClass;
    this.costLst = costLst;
    this.consumeLst = consumeLst;
    this.produceLst = produceLst;
    this.queueLst = queueLst;
    this.reaction = reaction;
    this.qty = 1;
    checkActiveProduceable();
  }

  private void checkActiveProduceable() {
    for (DefBuildingProduce pr: this.produceLst) {
      if (pr.getSpeed() > 0) {
        this.activeProduce = true;
        break;
      }
    }
  }
  public CityBuilding cloneItem() {
    CityBuilding clone = new CityBuilding(
        this.type,
        this.name,
        this.value,
        this.valueRange,
        this.requireClass,
        this.bonusClass,
        this.costLst,
        this.consumeLst,
        this.produceLst,
        this.queueLst,
        this.reaction
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "CityBuilding:" +
      this.type + "|" +
      this.name + "|" +
      String.join(",", this.value) + "|" +
      valueRange + "|" +
      String.join(",", requireClass) + "|" +
      String.join(",", this.bonusClass) + "|" +
      String.join(",", this.costLst.toString()) + "|" +
      String.join(",", this.consumeLst.toString()) + "|" +
      String.join(",", this.produceLst.toString()) + "|" +
      String.join(",", this.queueLst.toString()) + "|" +
      String.join(",", this.reaction.toString()) + "|" +
      String.valueOf(this.qty)
      ).toString();
  }

}