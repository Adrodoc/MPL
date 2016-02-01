package de.adrodoc55.minecraft.mpl;

import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class OneCommandConverter extends MplConverter {

  private static final String HEADER =
      "/summon FallingSand ~ ~1 ~ {Block:redstone_block,Time:1,Passengers:[{id:FallingSand,Block:activator_rail,Time:1,Passengers:[";
  private static final String TAIL =
      "{id:MinecartCommandBlock,Command:setblock ~ ~2 ~ command_block 0 replace {Command:fill ~ ~-4 ~ ~ ~ ~ air}},{id:MinecartCommandBlock,Command:setblock ~ ~1 ~ redstone_block},{id:MinecartCommandBlock,Command:kill @e[type=MinecartCommandBlock,r=0]}]}]}";
  private static Coordinate3D OFFSET = new Coordinate3D(1, -2, 1);

  public static String convert(List<CommandBlockChain> chains) {
    StringBuilder sb = new StringBuilder(HEADER);
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

  private static StringBuilder convert(CommandBlock block) {
    String coordinate = block.getCoordinate().plus(OFFSET).toRelativeString();
    String blockId = toBlockId(block.getMode());
    int damage = toDamageValue(block);
    StringBuilder sb = new StringBuilder("{id:MinecartCommandBlock,Command:");
    sb.append("setblock ");
    sb.append(coordinate).append(' ');
    sb.append(blockId).append(' ');
    sb.append(damage).append(' ');
    sb.append("replace").append(' ');
    sb.append("{");
    if (!block.needsRedstone()) {
      sb.append("auto:1,");
    }
    sb.append("Command:");
    sb.append(block.getCommand());
    sb.append("}},");
    return sb;
  }

}
