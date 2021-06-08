package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class TableViewItem {

    @Getter @Setter private String category = null;
    @Getter @Setter private String value = null;

    public TableViewItem(final String category, final String value) {
        this.category = category;
        this.value = value;
    }

    public String toString() {
        return this.category + "|" + this.value;
    }
}