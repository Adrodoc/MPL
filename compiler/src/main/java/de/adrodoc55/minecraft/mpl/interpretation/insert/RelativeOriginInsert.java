package de.adrodoc55.minecraft.mpl.interpretation.insert;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.interpretation.VariableScope;

public class RelativeOriginInsert implements Insert {
  private final Coordinate3D relative;

  public RelativeOriginInsert(Coordinate3D relative) {
    this.relative = relative;
  }

  @Override
  public String resolve(VariableScope scope) {
    // TODO Auto-generated method stub
    return null;
  }

}
