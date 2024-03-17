package tientn.easynews.game.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;

@JsonRootName(value = "city-production")
@JsonPropertyOrder({ "buildingName", "buildingCount", "itemType", "itemName", "dayCount", "dayTotal", "outputAmount" })
@Getter
@Setter
public class CityProductionItem  implements Serializable {
    private static final long serialVersionUID = -4598117601238030021L;

    @Getter @Setter private String buildingName = null;
    @Getter @Setter private int buildingCount = 0;
    @Getter @Setter private String itemType = null;
    @Getter @Setter private String itemName = null;
    @Getter @Setter private int dayCount = 0;
    @Getter @Setter private int dayTotal = 0;
    @Getter @Setter private int outputAmount = 0;

    public CityProductionItem(final String buildingName, final int buildingCount, 
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