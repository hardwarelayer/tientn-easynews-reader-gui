package tientn.easynews.game.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CityMessageTVItem {

    @Getter @Setter private String msgType = null;
    @Getter @Setter private String msgTime = null;
    @Getter @Setter private String msgContent = null;

    public CityMessageTVItem(final String msgType, final String msgTime, final String msgContent) {
        this.msgType = msgType;
        this.msgTime = msgTime;
        this.msgContent = msgContent;
    }

    public String toString() {
        return this.msgTime + "|" + this.msgType + "|" + this.msgContent;
    }
}