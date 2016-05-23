package de.adrodoc55.minecraft.mpl.ast.chainparts;

import de.adrodoc55.minecraft.mpl.commands.Mode;

public interface Modifiable {
  Mode getMode();

  Boolean isConditional();

  Boolean getNeedsRedstone();
}
