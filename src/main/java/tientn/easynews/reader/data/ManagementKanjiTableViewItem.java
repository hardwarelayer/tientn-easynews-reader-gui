package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ManagementKanjiTableViewItem {

    @Getter @Setter private String kanji = null;
    @Getter @Setter private String hiragana = null;    
    @Getter @Setter private int test = 0;
    @Getter @Setter private boolean correct = false;

    public ManagementKanjiTableViewItem(final String kanji, final String hiragana, final int test, final boolean correct) {
        this.kanji = kanji;
        this.hiragana = hiragana;
        this.test = test;
        this.correct = correct;
    }

    public String toString() {
        return this.kanji + "|" + this.hiragana + "|" + String.valueOf(this.test)  + "|" + String.valueOf(this.correct);
    }
}