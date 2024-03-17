package tientn.easynews.game.data;

/*Taken from tientn-jboard*/
import java.util.List;
import java.util.ArrayList;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.mysql.cj.ClientPreparedQueryBindValue;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import tientn.easynews.game.data.Constants.QtyWithCap;

@JsonRootName(value = "city")
@JsonPropertyOrder({ "def", "buildQueue", "productionQueue", "inventoryCap", "inventoryItem", "level", "pop", "troop", "owner", "name" })
@Getter
@Setter
public class City implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private DefCity def;
  @Getter @Setter private List<CityBuildQueueItem> buildQueue = null;
  @Getter @Setter private List<CityProductionItem> productionQueue = null;
  @Getter @Setter private List<CityInventoryCapability> inventoryCapability = null;
  @Getter @Setter private List<CityInventoryItem> inventoryItem = null;
  @Getter @Setter private List<CityBuilding> buildingItem = null;
  @Getter @Setter private int level;
  @Getter @Setter private int pop;
  @Getter @Setter private int troop;
  @Getter @Setter private String owner;
  @Getter @Setter private String name;
  @Getter @Setter private int security;

  //city list (with info)
  // city's build queue
  // city's building list
  // city's production cap
  // city's consuming cap
  // city's messages

  public City(final DefCity def, 
    List<CityBuildQueueItem> builds, List<CityProductionItem> prods) {
    this.def = def;
    this.buildQueue = builds;
    this.productionQueue = prods;

    this.inventoryCapability = new ArrayList<CityInventoryCapability>();
    this.inventoryItem = new ArrayList<CityInventoryItem>();
    this.buildingItem = new ArrayList<CityBuilding>();
  
    for (int i=0; i<Constants.basicCityMaterials.length; i++) {
      CityInventoryCapability invCap = new CityInventoryCapability(
        Constants.basicCityMaterials[i], 
        this.def.getCapability().getLand()*Constants.CITY_LAND_BARE_STORAGE_CAP
      );
      this.inventoryCapability.add(invCap);

      CityInventoryItem invItem = new CityInventoryItem(Constants.basicCityMaterials[i], 0);
      this.inventoryItem.add(invItem); 
    }
    this.level = this.def.getLevel();
    this.pop = this.def.getPop();
    this.troop = this.def.getTroop();
    this.owner = this.def.getOwner();
    this.name = this.def.getName();
    this.security = Constants.CITY_DEFAULT_SECURITY_LEVEL;
  }

  public City(final DefCity def, 
    List<CityBuildQueueItem> builds, List<CityProductionItem> prods,
    List<CityInventoryCapability> inventoryCaps, List<CityInventoryItem> inventoryItems) {
    this.def = def;
    this.buildQueue = builds;
    this.productionQueue = prods;
    this.inventoryCapability = inventoryCaps;
    this.inventoryItem = inventoryItems; 
    this.level = this.def.getLevel();
    this.pop = this.def.getPop();
    this.troop = this.def.getTroop();
    this.owner = this.def.getOwner();
    this.name = this.def.getName();
    this.security = 1;
  }

  public City(final DefCity def, 
    final int level, final int pop, final int troop, final String owner, final String name,
    List<CityBuildQueueItem> builds, List<CityProductionItem> prods,
    List<CityInventoryCapability> inventoryCaps, List<CityInventoryItem> inventoryItems,
    final int security) {
    this.def = def;
    this.buildQueue = builds;
    this.productionQueue = prods;
    this.inventoryCapability = inventoryCaps;
    this.inventoryItem = inventoryItems; 
    this.level = level;
    this.pop = pop;
    this.troop = troop;
    this.owner = owner;
    this.name = name;
    this.security = security;
  }

  public int getInvCap(final String name) {
    for (CityInventoryCapability invCap: this.inventoryCapability) {
      if (invCap.getName().equals(name)) {
        return invCap.getQuantity();
      }
    }
    return 0;
  }

  public int getInvQty(final String name) {
    for (CityInventoryItem inv: this.inventoryItem) {
      if (inv.getName().equals(name)) {
        return inv.getQuantity();
      }
    }
    return 0;
  }

  public int reduceInvQty(final String name, final int qty) {
    for (CityInventoryItem inv: this.inventoryItem) {
      if (inv.getName().equals(name)) {
        inv.setQuantity(inv.getQuantity()-qty);
        return inv.getQuantity();
      }
    }
    return 0;
  }

  public QtyWithCap getInventoryItemWithCap(final String name) {
    int cap = getInvCap(name);
    int qty = getInvQty(name);

    return new QtyWithCap(qty, cap);
  }

  private int countUsedLandBlock() {
    int totalBuiltBlock = 0;
    for (CityBuilding bld: this.buildingItem) {
      for (DefBuildingCostType bldCost: bld.getCostLst()) {
        if (bldCost.getType().equals(Constants.CITY_BUILD_BLOCK_NAME)) {
          totalBuiltBlock += (bldCost.getQuantity()*bld.getQty());
        }
      }
    }
    for (CityBuildQueueItem qBld: this.buildQueue) {
      totalBuiltBlock += qBld.getLandBlock();
    }
    return totalBuiltBlock;
  }

  public int getFreeLandBlock() {
    int landBlock = this.def.getCapability().getLand()*Constants.BLOCK_PER_LAND_UNIT;
    int freeLandBlock = landBlock - this.countUsedLandBlock();
    return freeLandBlock;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.def.toString() + "|" +
      String.valueOf(this.level) + "|" +
      String.valueOf(this.pop) + "|" +
      String.valueOf(this.troop) + "|" +
      this.owner + "|" +
      this.name + "|" +
      this.buildQueue.stream().map(CityBuildQueueItem::toString).collect(Collectors.joining(",")) + "|" +
      this.productionQueue.stream().map(CityProductionItem::toString).collect(Collectors.joining(",")) + "|" +
      this.inventoryCapability.stream().map(CityInventoryCapability::toString).collect(Collectors.joining(",")) + "|" +
      this.inventoryItem.stream().map(CityInventoryItem::toString).collect(Collectors.joining(",")) + "|" +
      this.security
      ).toString();
  }

}