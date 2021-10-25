package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ManagementGrammarTableViewItem {

    @Getter @Setter private String id = null;
    @Getter @Setter private String title = null;
    @Getter @Setter private int patterns = 0;    
    @Getter @Setter private int test = 0;
    @Getter @Setter private int correct = 0;

    public ManagementGrammarTableViewItem(final String id, final String title, final int patterns, final int test, final int correct) {
        this.id = id;
        this.title = title;
        this.patterns = patterns;
        this.test = test;
        this.correct = correct;
    }

    public String toString() {
        return this.id + "|" + this.title + "|" + String.valueOf(this.patterns)  + "|" + String.valueOf(this.test) + "|" + String.valueOf(this.correct);
    }
}