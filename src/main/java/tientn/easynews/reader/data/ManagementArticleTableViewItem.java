package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ManagementArticleTableViewItem {

    @Getter @Setter private String id = null;
    @Getter @Setter private String title = null;
    @Getter @Setter private int sentences = 0;    
    @Getter @Setter private int kanjis = 0;
    @Getter @Setter private int test = 0;
    @Getter @Setter private int correct = 0;

    public ManagementArticleTableViewItem(final String id, final String title, final int sentences, final int kanjis, final int test, final int correct) {
        this.id = id;
        this.title = title;
        this.sentences = sentences;
        this.kanjis = kanjis;
        this.test = test;
        this.correct = correct;
    }

    public String toString() {
        return this.id + "|" + this.title + "|" + String.valueOf(this.sentences)  + "|" + String.valueOf(this.kanjis)  + "|" + String.valueOf(this.test) + "|" + String.valueOf(this.correct);
    }
}