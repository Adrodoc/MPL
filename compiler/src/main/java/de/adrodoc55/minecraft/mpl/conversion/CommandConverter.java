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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.adrodoc55.commons.StringUtils;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;

/**
 * @author Adrodoc55
 */
public class CommandConverter extends MplConverter {
  public static final int MAX_COMMAND_LENGTH = 32500;

  private static final String HEADER =
      "summon FallingSand ~ ~1 ~ {Block:redstone_block,Time:1,Passengers:[{id:FallingSand,Block:activator_rail,Time:1,Passengers:[";
  private static final String TAIL =
      "{id:MinecartCommandBlock,Command:setblock ~ ~2 ~ command_block 0 replace {Command:fill ~ ~-4 ~ ~ ~ ~ air}},{id:MinecartCommandBlock,Command:setblock ~ ~1 ~ redstone_block},{id:MinecartCommandBlock,Command:kill @e[type=MinecartCommandBlock,r=0]}]}]}";
  private static final String COMMAND_HEADER = "{id:MinecartCommandBlock,Command:";
  private static final String COMMAND_TAIL = "},";

  public static Coordinate3D getOffset(Orientation3D orientation) {
    Coordinate3D a = orientation.getA().toCoordinate();
    Coordinate3D c = orientation.getC().toCoordinate();

    Direction3D bd = orientation.getB();
    bd = Direction3D.valueOf(bd.getAxis(), false);
    Coordinate3D b = bd.toCoordinate();

    // @formatter:off
    return new Coordinate3D()
        .plus(a.mult(1))
        .plus(b.mult(-2))
        .plus(c.mult(1));
    // @formatter:on
  }

  public static List<String> convert(MplCompilationResult result) {
    List<String> commands = new ArrayList<>();
    Orientation3D orientation = result.getOrientation();

    StringBuilder sb = new StringBuilder(HEADER);
    // Appending initial fill Command to clear the required Area
    Coordinate3D max = getMaxCoordinate(result.getBlocks().keySet());
    if (max != null) {
      sb.append(COMMAND_HEADER);
      sb.append("fill ");
      sb.append(new Coordinate3D().plus(getOffset(orientation)).toRelativeString()).append(' ');
      sb.append(max.plus(getOffset(orientation)).toRelativeString()).append(' ');
      sb.append("air");
      sb.append(COMMAND_TAIL);
    }
    // Appending setblock for all Commands
    for (MplBlock block : result.getBlocks().values()) {
      if (!(block instanceof CommandBlock)) {
        continue;
      }
      StringBuilder convert = convert((CommandBlock) block, orientation);
      int totalLength = sb.length() + convert.length() + TAIL.length();
      if (totalLength > MAX_COMMAND_LENGTH) {
        sb.append(TAIL);
        commands.add(sb.toString());
        sb = new StringBuilder(HEADER);
      }
      sb.append(convert);
    }
    sb.append(TAIL);
    commands.add(sb.toString());
    sb = new StringBuilder(HEADER);

    return commands;
  }

  private static Coordinate3D getMaxCoordinate(ImmutableSet<Coordinate3D> immutableSet) {
    Iterator<Coordinate3D> it = immutableSet.iterator();
    if (!it.hasNext()) {
      return null;
    }
    Coordinate3D pos = it.next();
    int maxX = pos.getX();
    int maxY = pos.getY();
    int maxZ = pos.getZ();
    while (it.hasNext()) {
      Coordinate3D c = it.next();
      maxX = Math.max(maxX, c.getX());
      maxY = Math.max(maxY, c.getY());
      maxZ = Math.max(maxZ, c.getZ());
    }
    return new Coordinate3D(maxX, maxY, maxZ);
  }

  private static StringBuilder convert(CommandBlock block, Orientation3D orientation) {
    String coordinate = block.getCoordinate().plus(getOffset(orientation)).toRelativeString();
    String blockId = block.getMode().getStringBlockId();
    int damage = toDamageValue(block);
    StringBuilder sb = new StringBuilder(COMMAND_HEADER);
    sb.append("setblock ");
    sb.append(coordinate).append(' ');
    sb.append(blockId).append(' ');
    sb.append(damage).append(' ');
    sb.append("replace").append(' ');
    sb.append('{');
    if (!block.getNeedsRedstone()) {
      sb.append("auto:1,");
    }
    sb.append("Command:");
    sb.append(escapeCommand(block.getCommand()));
    sb.append('}');
    sb.append(COMMAND_TAIL);
    return sb;
  }

  /**
   * A command must be escaped twice, because a OneCommand is nested twice: a commandblock in a
   * commandblock minecraft in a commandblock.
   *
   * @param command
   * @return
   */
  private static String escapeCommand(String command) {
    command = StringUtils.escapeBackslashes(command);
    command = StringUtils.escapeBackslashes(command);
    return command;
  }

}
