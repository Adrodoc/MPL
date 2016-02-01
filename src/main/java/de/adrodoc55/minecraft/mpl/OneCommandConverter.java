package de.adrodoc55.minecraft.mpl;

import java.util.Iterator;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class OneCommandConverter extends MplConverter {

  private static final String HEADER =
      "/summon FallingSand ~ ~1 ~ {Block:redstone_block,Time:1,Passengers:[{id:FallingSand,Block:activator_rail,Time:1,Passengers:[";
  private static final String TAIL =
      "{id:MinecartCommandBlock,Command:setblock ~ ~2 ~ command_block 0 replace {Command:fill ~ ~-4 ~ ~ ~ ~ air}},{id:MinecartCommandBlock,Command:setblock ~ ~1 ~ redstone_block},{id:MinecartCommandBlock,Command:kill @e[type=MinecartCommandBlock,r=0]}]}]}";
  private static final Coordinate3D OFFSET = new Coordinate3D(1, -2, 1);
  private static final String COMMAND_HEADER = "{id:MinecartCommandBlock,Command:";
  private static final String COMMAND_TAIL = "},";

  public static String convert(List<CommandBlockChain> chains) {
    StringBuilder sb = new StringBuilder(HEADER);
    // Appending initial fill Command to clear the required Area
    Coordinate3D max = getMaxCoordinate(chains);
    if (max != null) {
      sb.append(COMMAND_HEADER);
      sb.append("fill ");
      sb.append(new Coordinate3D().plus(OFFSET).toRelativeString()).append(' ');
      sb.append(max.plus(OFFSET).toRelativeString()).append(' ');
      sb.append("air");
      sb.append(COMMAND_TAIL);
    }
    // Appending setblock for all Commands
    for (CommandBlockChain chain : chains) {
      List<CommandBlock> blocks = chain.getCommandBlocks();
      for (CommandBlock block : blocks) {
        if (block.toCommand() == null) {
          continue;
        }
        sb.append(convert(block));
      }
    }
    sb.append(TAIL);
    return sb.toString();
  }

  private static Coordinate3D getMaxCoordinate(List<CommandBlockChain> chains) {
    Iterator<CommandBlockChain> it = chains.iterator();
    if (!it.hasNext()) {
      return null;
    }
    CommandBlockChain first = it.next();
    Coordinate3D pos = first.getMax();
    int maxX = pos.getX();
    int maxY = pos.getY();
    int maxZ = pos.getZ();
    while (it.hasNext()) {
      CommandBlockChain current = it.next();
      Coordinate3D c = current.getMax();
      maxX = Math.max(maxX, c.getX());
      maxY = Math.max(maxY, c.getY());
      maxZ = Math.max(maxZ, c.getZ());
    }
    return new Coordinate3D(maxX, maxY, maxZ);

  }

  private static StringBuilder convert(CommandBlock block) {
    String coordinate = block.getCoordinate().plus(OFFSET).toRelativeString();
    String blockId = toBlockId(block.getMode());
    int damage = toDamageValue(block);
    StringBuilder sb = new StringBuilder(COMMAND_HEADER);
    sb.append("setblock ");
    sb.append(coordinate).append(' ');
    sb.append(blockId).append(' ');
    sb.append(damage).append(' ');
    sb.append("replace").append(' ');
    sb.append('{');
    if (!block.needsRedstone()) {
      sb.append("auto:1,");
    }
    sb.append("Command:");
    sb.append(block.getCommand());
    sb.append('}');
    sb.append(COMMAND_TAIL);
    return sb;
  }

}
