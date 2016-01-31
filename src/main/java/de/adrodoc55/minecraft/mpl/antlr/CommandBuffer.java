package de.adrodoc55.minecraft.mpl.antlr;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;

class CommandBuffer {
  private String command;
  private Mode mode;
  private Boolean conditional;
  private Boolean needsRedstone;

  public Command toCommand() {
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

  public Boolean getConditional() {
    return conditional;
  }

  public void setConditional(Boolean conditional) {
    this.conditional = conditional;
  }

  public Boolean getNeedsRedstone() {
    return needsRedstone;
  }

  public void setNeedsRedstone(Boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

}
