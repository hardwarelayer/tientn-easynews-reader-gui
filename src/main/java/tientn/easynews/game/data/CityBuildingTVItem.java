package tientn.easynews.game.data;

import lombok.Getter;
import lombok.Setter;

public class CityBuildingTVItem {

    @Getter @Setter private String type;
    @Getter @Setter private String name;
    @Getter @Setter private int qty;
    @Getter @Setter private String status;

    public CityBuildingTVItem(final String type, final String name, final int qty) {
        this.type = type;
        this.name = name;
        this.qty = qty;
        this.status = "";
    }

    public CityBuildingTVItem(final String type, final String name, final int qty, final String status) {
        this.type = type;
        this.name = name;
        this.qty = qty;
        this.status = status;
    }

    public String toString() {
        return this.type + "|" + this.name + "|" + String.valueOf(this.qty) + "|" + String.valueOf(this.status);
    }
}