package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ArticleWordBuildTableViewItem {

    @Getter @Setter private String kanji = null;
    @Getter @Setter private String hiragana = null;
    @Getter @Setter private String hv = null;
    @Getter @Setter private String meaning = null;
    @Getter @Setter private int test = 0;
    @Getter @Setter private int correct = 0;

    public ArticleWordBuildTableViewItem(final String kanji, final String hiragana, final String hv, final String meaning) {
        this.kanji = kanji;
        this.hiragana = hiragana;
        this.hv = hv;
        this.meaning = meaning;
        this.test = 0;
        this.correct = 0;
    }

    public ArticleWordBuildTableViewItem(final String kanji, final String hiragana, final String hv, final String meaning, final int test, final int correct) {
        this.kanji = kanji;
        this.hiragana = hiragana;
        this.hv = hv;
        this.meaning = meaning;
        this.test = test;
        this.correct = correct;
    }

    public String toString() {
        return this.kanji + "|" + this.hiragana + "|" + String.valueOf(this.test)  + "|" + String.valueOf(this.correct);
    }
}