package tientn.easynews.game.data;

import lombok.Getter;
import lombok.Setter;

public class CityBuildingTVItem {

    @Getter @Setter private String type;
    @Getter @Setter private String name;
    @Getter @Setter private int qty;

    public CityBuildingTVItem(final String type, final String name, final int qty) {
        this.type = type;
        this.name = name;
        this.qty = qty;
    }

    public String toString() {
        return this.type + "|" + this.name + "|" + String.valueOf(this.qty);
    }
}