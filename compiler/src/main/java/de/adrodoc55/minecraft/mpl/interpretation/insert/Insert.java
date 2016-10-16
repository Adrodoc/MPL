package de.adrodoc55.minecraft.mpl.interpretation.insert;

import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;

public interface Insert {
  String resolve(VariableScope scope);
}
