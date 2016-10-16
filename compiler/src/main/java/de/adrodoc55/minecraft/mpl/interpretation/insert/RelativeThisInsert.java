package de.adrodoc55.minecraft.mpl.interpretation.insert;

import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;

public class RelativeThisInsert implements Insert {
  private final int relative;

  public RelativeThisInsert(int relative) {
    this.relative = relative;
  }

  @Override
  public String resolve(VariableScope scope) {
    // TODO Auto-generated method stub
    return null;
  }
}
