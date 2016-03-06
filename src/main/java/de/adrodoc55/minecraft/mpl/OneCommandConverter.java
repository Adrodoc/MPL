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

import java.util.Iterator;
import java.util.List;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;

public class OneCommandConverter extends MplConverter {

  private static final String HEADER =
      "summon FallingSand ~ ~1 ~ {Block:redstone_block,Time:1,Passengers:[{id:FallingSand,Block:activator_rail,Time:1,Passengers:[";
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
      List<MplBlock> blocks = chain.getCommandBlocks();
      for (MplBlock block : blocks) {
        if (!(block instanceof CommandBlock)) {
          continue;
        }
        sb.append(convert((CommandBlock) block));
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
