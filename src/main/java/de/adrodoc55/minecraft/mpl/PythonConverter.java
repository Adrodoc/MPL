package de.adrodoc55.minecraft.mpl;

import java.util.List;

public class PythonConverter extends MplConverter {

  private static final String INDENT = "    ";

  public static String convert(List<CommandBlockChain> chains, String name) {
    StringBuilder sb = new StringBuilder(getPythonHeader(name));
    for (CommandBlockChain chain : chains) {
      List<CommandBlock> blocks = chain.getCommandBlocks();
      for (CommandBlock block : blocks) {
        sb.append(INDENT + convert(block) + "\n");
      }
    }
    return sb.toString();
  }

  private static String getPythonHeader(String name) {
    // @formatter:off
    String pythonHeader = "from pymclevel.entity import TileEntity\n"
        + "from pymclevel.nbt import TAG_String\n"
        + "from pymclevel.nbt import TAG_Byte\n"
        + "\n"
        + "displayName = 'Generate " + name + "'\n"
        + "\n"
        + "def create_command_block(level, xyz, command, blockId=137, damage=0, auto=True):\n"
        + "    x, y, z = xyz\n"
        + "    level.setBlockAt(x, y, z, blockId)\n"
        + "\n"
        + "    level.setBlockDataAt(x, y, z, damage)\n"
        + "    control = TileEntity.Create('Control', xyz)\n"
        + "    control['Command'] = TAG_String(command)\n"
        + "    control['auto'] = TAG_Byte(auto)\n"
        + "    level.addTileEntity(control)\n"
        + "\n"
        + "def perform(level, box, options):\n";
    // @formatter:on
    return pythonHeader;
  }

  private static String convert(CommandBlock block) {
    String x = "box.minx + " + block.getX();
    String y = "box.miny + " + block.getY();
    String z = "box.minz + " + block.getZ();
    if (block.toCommand() == null) {
      return "level.setBlockAt(" + x + ", " + y + ", " + z + ", 1)";
    } else {
      String xyz = "(" + x + ", " + y + ", " + z + ")";
      String command = block.getCommand();
      int blockId = toIntBlockId(block.getMode());
      int damage = toDamageValue(block);
      String auto = block.needsRedstone() ? "False" : "True";
      return "create_command_block(level, " + xyz + ", '" + command + "', " + blockId + ", "
          + damage + ", " + auto + ")";
    }
  }

}
