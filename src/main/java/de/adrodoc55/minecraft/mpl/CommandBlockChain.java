package de.adrodoc55.minecraft.mpl;

import java.util.Iterator;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class CommandBlockChain {

  private final String name;
  private final List<CommandBlock> commandBlocks;

  public CommandBlockChain(String name, List<CommandBlock> commandBlocks) {
    this.name = name;
    this.commandBlocks = commandBlocks;
  }

  /**
   * Moves this Chain according to the vector
   *
   * @param vector
   */
  public void move(Coordinate3D vector) {
    for (CommandBlock block : commandBlocks) {
      block.setCoordinate(block.getCoordinate().plus(vector));
    }
  }

  public String getName() {
    return name;
  }

  public List<CommandBlock> getCommandBlocks() {
    return commandBlocks;
  }

  public Coordinate3D getMin() {
    Iterator<CommandBlock> it = commandBlocks.iterator();
    if (!it.hasNext()) {
      return new Coordinate3D();
    }
    CommandBlock first = it.next();
    Coordinate3D pos = first.getCoordinate();
    int minX = pos.getX();
    int minY = pos.getY();
    int minZ = pos.getZ();
    while (it.hasNext()) {
      CommandBlock current = it.next();
      Coordinate3D c = current.getCoordinate();
      minX = Math.min(minX, c.getX());
      minY = Math.min(minY, c.getY());
      minZ = Math.min(minZ, c.getZ());
    }
    return new Coordinate3D(minX, minY, minZ);
  }

  public Coordinate3D getMax() {
    Iterator<CommandBlock> it = commandBlocks.iterator();
    if (!it.hasNext()) {
      return new Coordinate3D();
    }
    CommandBlock first = it.next();
    Coordinate3D pos = first.getCoordinate();
    int maxX = pos.getX();
    int maxY = pos.getY();
    int maxZ = pos.getZ();
    while (it.hasNext()) {
      CommandBlock current = it.next();
      Coordinate3D c = current.getCoordinate();
      maxX = Math.max(maxX, c.getX());
      maxY = Math.max(maxY, c.getY());
      maxZ = Math.max(maxZ, c.getZ());
    }
    return new Coordinate3D(maxX, maxY, maxZ);
  }

}
