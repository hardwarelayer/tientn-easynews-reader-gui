package tientn.easynews.game.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CityBuildQueueTVItem {

    @Getter @Setter private String itemType = null;
    @Getter @Setter private String itemName = null;
    @Getter @Setter private String progressInfo = null;

    public CityBuildQueueTVItem(final String itemType, final String itemName, final String progress) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.progressInfo = progress;
    }

    public String toString() {
        return this.itemType + "|" + this.itemName + "|" + this.progressInfo;
    }
}