/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl;

public class PythonConverter extends MplConverter {

  private static final String INDENT = "    ";

  public static String convert(MplCompilationResult result, String name) {
    StringBuilder sb = new StringBuilder(getPythonHeader(name));
    for (MplBlock block : result.getBlocks().values()) {
      sb.append(INDENT + convert(block) + "\n");
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

  private static String convert(MplBlock block) {
    String x = "box.minx + " + block.getX();
    String y = "box.miny + " + block.getY();
    String z = "box.minz + " + block.getZ();
    if (block instanceof AirBlock) {
      return "level.setBlockAt(" + x + ", " + y + ", " + z + ", 0)";
    } else if (block instanceof Transmitter) {
      return "level.setBlockAt(" + x + ", " + y + ", " + z + ", 1)";
    } else if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      String xyz = "(" + x + ", " + y + ", " + z + ")";
      String command = commandBlock.getCommand();
      int blockId = toIntBlockId(commandBlock.getMode());
      int damage = toDamageValue(commandBlock);
      String auto = commandBlock.needsRedstone() ? "False" : "True";
      return "create_command_block(level, " + xyz + ", '" + command + "', " + blockId + ", "
          + damage + ", " + auto + ")";
    } else {
      throw new IllegalArgumentException(
          "Can't convert block of type " + block.getClass() + " to python!");
    }
  }

}
