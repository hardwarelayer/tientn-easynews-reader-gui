package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class GrammarPatternTableViewItem {
    //note: avoid property like descriptions, because it will be error in this object factory
    @Getter @Setter private String id = null;
    @Getter @Setter private String title = null;
    @Getter @Setter private String description = null;
    @Getter @Setter private int test = 0;
    @Getter @Setter private int correct = 0;

    public GrammarPatternTableViewItem(final String id, final String title, final String description, final int test, final int correct) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.test = test;
        this.correct = correct;
    }

    public String toString() {
        return this.id + "|" + this.title + "|" + this.description  + "|" + String.valueOf(this.test) + "|" + String.valueOf(this.correct);
    }
}