/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
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
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.conversion;

import static de.adrodoc55.commons.FileUtils.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.adrodoc55.commons.StringUtils;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;

/**
 * @author Adrodoc55
 */
public class PythonConverter implements MplConverter {
  @Override
  public void write(MplCompilationResult result, String name, OutputStream out) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, UTF_8))) {
      writer.write(convert(result, name));
    }
  }

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
    byte blockId = block.getByteBlockId();
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      String xyz = "(" + x + ", " + y + ", " + z + ")";
      String command = StringUtils.escapeBackslashes(commandBlock.getCommand());
      int damage = commandBlock.getDamageValue();
      String auto = commandBlock.getNeedsRedstone() ? "False" : "True";
      return "create_command_block(level, " + xyz + ", '" + command + "', " + blockId + ", "
          + damage + ", " + auto + ")";
    } else {
      return "level.setBlockAt(" + x + ", " + y + ", " + z + ", " + blockId + ")";
    }
  }

}
