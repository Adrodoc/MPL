package de.adrodoc55.minecraft.mpl.compilation.interpretation;

import org.antlr.v4.runtime.Token;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;

@lombok.Getter
@lombok.Setter
public class ModifierBuffer {
  private Mode mode;
  private Conditional conditional;
  private Boolean needsRedstone;
  private Token modeToken;
  private Token conditionalToken;
  private Token needsRedstoneToken;

  public Boolean isConditional() {
    if (conditional == null) {
      return null;
    }
    switch (conditional) {
      case UNCONDITIONAL:
        return false;
      case CONDITIONAL:
      case INVERT:
        return true;
      default:
        return null;
    }
  }

}
