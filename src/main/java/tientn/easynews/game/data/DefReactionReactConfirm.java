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

@JsonRootName(value = "reaction-react-confirm")
@JsonPropertyOrder({ "prompt", "confirm", "condition", "complete" })
@Getter
@Setter
public class DefReactionReactConfirm implements Serializable {
  private static final long serialVersionUID = -4598117601238030021L;

  @Getter @Setter private String prompt;
  @Getter @Setter private String confirm;
  @Getter @Setter private String condition;
  @Getter @Setter private String complete;

  public DefReactionReactConfirm() {
  }

  public DefReactionReactConfirm(final String prompt, final String confirm, final String condition, final String complete) {
    this.prompt = prompt;
    this.confirm = confirm;
    this.condition = condition;
    this.complete = complete;
  }

  public DefReactionReactConfirm cloneItem() {
    DefReactionReactConfirm clone = new DefReactionReactConfirm(
      this.prompt,
      this.confirm,
      this.condition,
      this.complete 
      );
    return clone;
  }

  @Override
  public String toString() {
    return new StringBuilder(
      "DefReactionReactConfirm:" +
      this.prompt + "|" +
      this.confirm + "|" +
      this.condition + "|" +
      this.complete
      ).toString();
  }

}