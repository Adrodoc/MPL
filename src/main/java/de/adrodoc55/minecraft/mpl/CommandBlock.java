package de.adrodoc55.minecraft.mpl;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command.Mode;

public class CommandBlock {

  private Command command;
  private Direction direction;
  private Coordinate3D coordinate;

  public CommandBlock(Command command, Direction direction, Coordinate3D coordinate) {
    this.command = command;
    this.direction = direction;
    this.coordinate = coordinate;
  }

  public Command toCommand() {
    return command;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

  public Coordinate3D getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate3D coordinate) {
    this.coordinate = coordinate;
  }

  public String getCommand() {
    return command.getCommand();
  }

  public void setCommand(String command) {
    this.command.setCommand(command);
  }

  public boolean isConditional() {
    return command != null ? command.isConditional() : false;
  }

  public void setConditional(boolean conditional) {
    command.setConditional(conditional);
  }

  public Mode getMode() {
    return command != null ? command.getMode() : null;
  }

  public void setMode(Mode mode) {
    command.setMode(mode);
  }

  public boolean needsRedstone() {
    return command != null ? command.needsRedstone() : false;
  }

  public void setNeedsRedstone(boolean needsRedstone) {
    command.setNeedsRedstone(needsRedstone);
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public int getX() {
    return coordinate.getX();
  }

  public int getY() {
    return coordinate.getY();
  }

  public int getZ() {
    return coordinate.getZ();
  }

}
