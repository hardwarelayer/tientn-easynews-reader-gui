package tientn.easynews.game.data;

/*Taken from tientn-jboard*/
import java.util.List;
import java.lang.System;
import java.util.UUID;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonRootName(value = "reaction-def")
@JsonPropertyOrder({ "trigger", "react" })
@Getter
@Setter
public class DefReaction implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private String trigger;
  @Getter @Setter private DefReactionReact react;

  public DefReaction(final String trigger, final DefReactionReact react) {
    this.trigger = trigger;
    this.react = react;
  }

  public DefReaction cloneItem() {
    DefReaction clone = new DefReaction(
        this.trigger,
        this.react
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "DefReaction:" +
      this.trigger + "|" +
      this.react.toString()
      ).toString();
  }

}