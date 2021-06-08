package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ManagementStatusTableViewItem {

    @Getter @Setter private String category = null;
    @Getter @Setter private String value = null;

    public ManagementStatusTableViewItem(final String category, final String value) {
        this.category = category;
        this.value = value;
    }

    public String toString() {
        return this.category + "|" + this.value;
    }
}