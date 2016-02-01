package de.adrodoc55.minecraft.mpl.antlr;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;

class CommandBuffer {

  public static enum Conditional {
    UNCONDITIONAL, CONDITIONAL, INVERT
  }

  private String command;
  private Mode mode;
  private Conditional conditional;
  private Boolean needsRedstone;

  public Command toCommand() {
    Boolean conditional = isConditional();
    return new Command(command, mode, conditional, needsRedstone);
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

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

  public Conditional getConditional() {
    return conditional;
  }

  public void setConditional(Conditional conditional) {
    this.conditional = conditional;
  }

  public Boolean getNeedsRedstone() {
    return needsRedstone;
  }

  public void setNeedsRedstone(Boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

}
