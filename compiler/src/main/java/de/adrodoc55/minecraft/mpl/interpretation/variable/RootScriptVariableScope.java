package de.adrodoc55.minecraft.mpl.interpretation.variable;

public class RootScriptVariableScope extends AbstractVariableScope {
  @Override
  public boolean mayChildDeclareLocalVariable(String identifier) {
    return mayDeclareVariable(identifier);
  }
}
