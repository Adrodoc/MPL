package de.adrodoc55.minecraft.mpl.interpretation.insert;

import de.adrodoc55.minecraft.mpl.ast.variable.Insertable;
import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;

public class LocalVariableInsert implements Insert {
  private final Insertable insertable;

  public LocalVariableInsert(Insertable insertable) {
    this.insertable = insertable;
  }

  @Override
  public String resolve(VariableScope scope) {
    return insertable.toInsert();
  }
}
