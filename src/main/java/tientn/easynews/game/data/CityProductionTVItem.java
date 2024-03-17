package tientn.easynews.game.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CityProductionTVItem {

    @Getter @Setter private String buildingName = null;
    @Getter @Setter private int buildingCount = 0;
    @Getter @Setter private String itemType = null;
    @Getter @Setter private String itemName = null;
    @Getter @Setter private int dayCount = 0;
    @Getter @Setter private int dayTotal = 0;
    @Getter @Setter private int outputAmount = 0;

    public CityProductionTVItem(final String buildingName, final int buildingCount, 
        final String itemType, final String itemName, 
        final int dayCount, final int dayTotal, final int outputAmount) {
        this.buildingName = buildingName;
        this.buildingCount = buildingCount;
        this.itemType = itemType;
        this.itemName = itemName;
        this.dayCount = dayCount;
        this.dayTotal = dayTotal;
        this.outputAmount = outputAmount;
    }

    public String toString() {
        return this.buildingName + "|" + String.valueOf(this.buildingCount) + "|" + 
        this.itemType + "|" + this.itemName + "|" + String.valueOf(this.dayCount + "|" + 
        String.valueOf(this.dayCount) + "|" + String.valueOf(this.dayTotal) + "|" + String.valueOf(this.outputAmount) );
    }
}