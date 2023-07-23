package tientn.easynews.reader.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class DictKanjiTableViewItem {

    @Getter @Setter private String id = null;
    @Getter @Setter private String kanji = null;
    @Getter @Setter private String hv = null;
    @Getter @Setter private String hiragana = null;
    @Getter @Setter private String meaning = null;

    public DictKanjiTableViewItem(final String id, final String kanji, final String hv_phonetic, final String on_kun, final String meaning) {
        this.id = id;
        this.kanji = kanji;
        this.hv = hv_phonetic;
        this.hiragana = on_kun;
        this.meaning = meaning;
    }

    public String toString() {
        return this.id + "|" + this.kanji + "|" + this.hv + "|" + this.hiragana + "|" + this.meaning + "|";
    }
}