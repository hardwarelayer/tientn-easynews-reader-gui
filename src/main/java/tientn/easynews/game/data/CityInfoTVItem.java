package tientn.easynews.game.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CityInfoTVItem {

    @Getter @Setter private String itemGroup = null;
    @Getter @Setter private String item = null;
    @Getter @Setter private String value = null;

    public CityInfoTVItem(final String itemGroup, final String item, final String value) {
        this.itemGroup = itemGroup;
        this.item = item;
        this.value = value;
    }

    public String toString() {
        return this.itemGroup + "|" + this.item + "|" + this.value;
    }
}