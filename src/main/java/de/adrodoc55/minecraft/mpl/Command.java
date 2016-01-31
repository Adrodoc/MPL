package de.adrodoc55.minecraft.mpl;

import net.karneim.pojobuilder.Builder;
import net.karneim.pojobuilder.GeneratePojoBuilder;

public class Command {

  private String command;
  private Mode mode;
  private boolean conditional;
  private boolean needsRedstone;

  public Command() {
    this(null);
  }

  public Command(String command) {
    this(command, null);
  }

  public Command(String command, Boolean conditional) {
    this(command, null, conditional);
  }

  public Command(String command, Mode mode, Boolean conditional) {
    this(command, mode, conditional, null);
  }

  @GeneratePojoBuilder(withBuilderInterface = Builder.class)
  public Command(String command, Mode mode, Boolean conditional, Boolean needsRedstone) {
    this.command = command;

    this.conditional = (conditional != null) ? conditional : false;
    this.mode = (mode != null) ? mode : Mode.CHAIN;

    if (needsRedstone != null) {
      this.needsRedstone = needsRedstone;
    } else {
      this.needsRedstone = (this.mode == Mode.CHAIN) ? false : true;
    }
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

  public static enum Mode {
    IMPULSE, CHAIN, REPEAT;
  }

  public boolean isConditional() {
    return conditional;
  }

  public void setConditional(boolean conditional) {
    this.conditional = conditional;
  }

  public boolean needsRedstone() {
    return needsRedstone;
  }

  public void setNeedsRedstone(boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

  @Override
  public String toString() {
    return "Command [command='" + command + "', mode=" + mode + ", conditional=" + conditional
        + ", needsRedstone=" + needsRedstone + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((command == null) ? 0 : command.hashCode());
    result = prime * result + (conditional ? 1231 : 1237);
    result = prime * result + ((mode == null) ? 0 : mode.hashCode());
    result = prime * result + (needsRedstone ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Command other = (Command) obj;
    if (command == null) {
      if (other.command != null)
        return false;
    } else if (!command.equals(other.command))
      return false;
    if (conditional != other.conditional)
      return false;
    if (mode != other.mode)
      return false;
    if (needsRedstone != other.needsRedstone)
      return false;
    return true;
  }

}
