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

@JsonRootName(value = "reaction-react-def")
@JsonPropertyOrder({ "confirm", "message" })
@Getter
@Setter
public class DefReactionReact implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private DefReactionReactConfirm confirm;
  @Getter @Setter private String message;

  public DefReactionReact(final DefReactionReactConfirm confirm, final String message) {
    this.confirm = confirm;
    this.message = message;
  }

  public DefReactionReact cloneItem() {
    DefReactionReact clone = new DefReactionReact(
        this.confirm,
        this.message
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      this.confirm.toString() + "|" +
      this.message
      ).toString();
  }

}