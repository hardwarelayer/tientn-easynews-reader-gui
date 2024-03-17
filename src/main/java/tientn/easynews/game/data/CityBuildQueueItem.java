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

@JsonRootName(value = "city-build-queue")
@JsonPropertyOrder({ "itemType", "itemName", "landBlock", "dayCount", "dayTotal" })
@Getter
@Setter
public class CityBuildQueueItem  implements Serializable {
    private static final long serialVersionUID = -4598117601238030021L;

    @Getter @Setter private String itemType = null;
    @Getter @Setter private String itemName = null;
    @Getter @Setter private int landBlock = 0;
    @Getter @Setter private int dayCount = 0;
    @Getter @Setter private int dayTotal = 0;
    @Getter @Setter private DefBuilding def;
    @Getter @Setter private long startTime;

    public CityBuildQueueItem(final String itemType, final String itemName, final int landBlock,
        final int dayCount, final int dayTotal, final DefBuilding def, final long tm) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.landBlock = landBlock;
        this.dayCount = dayCount;
        this.dayTotal = dayTotal;
        this.def = def;
        this.startTime = tm;
    }

    public String toString() {
        return this.itemType + "|" + this.itemName + "|" + 
        String.valueOf(this.landBlock) + "|" + this.dayCount + "|" + 
        this.dayTotal + "|" + def.toString() + "|" + String.valueOf(this.startTime);
    }
}