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

import static com.google.common.base.Charsets.UTF_8;
import static de.adrodoc55.minecraft.mpl.MplUtils.getBoundaries;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.commons.StringUtils;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class CommandConverter implements MplConverter {
  @Override
  public void write(MplCompilationResult result, String name, OutputStream out,
      MinecraftVersion version) throws IOException {
    List<String> converted = CommandConverter.convert(result, version);
    int i = 0;
    for (String string : converted) {
      out.write(("Command " + (++i) + ":\r\n").getBytes(UTF_8));
      out.write(string.getBytes(UTF_8));
      out.write("\r\n".getBytes(UTF_8));
    }
    out.close();
  }

  /**
   * Instead of the literal "replace" a setblock command can contain any character and even an empty
   * String. See <a href="https://github.com/Adrodoc55/MPL/issues/48">#48</a>
   */
  private static final String REPLACE = "";
  public static final int MAX_COMMAND_LENGTH = 32500;

  private static final String header(MinecraftVersion v) {
    return "summon " + v.fallingBlock() + " ~ ~1 ~ {Block:redstone_block,Time:1,Passengers:["
        + "{id:" + v.fallingBlock() + ",Block:activator_rail,Time:1},";
  }

  private static final String tail(MinecraftVersion v) {
    return "{id:" + v.commandBlockMinecart()
        + ",Command:\"blockdata ~ ~-2 ~ {auto:0,Command:\\\"\\\"}\"},"//
        + "{id:" + v.commandBlockMinecart() + ",Command:\"setblock ~ ~2 ~ command_block 0 "
        + REPLACE + " {Command:\\\"fill ~ ~-3 ~ ~ ~ ~ air\\\"}\"},"//
        + "{id:" + v.commandBlockMinecart() + ",Command:\"setblock ~ ~1 ~ redstone_block\"},"//
        + "{id:" + v.commandBlockMinecart() + ",Command:\"kill @e[type=" + v.commandBlockMinecart()
        + ",r=0]\"}]}";
  }

  private static final String commandHeader(MinecraftVersion v) {
    return "{id:" + v.commandBlockMinecart() + ",Command:\"";
  }

  private static final String commandTail(MinecraftVersion v) {
    return "\"},";
  }

  public static Coordinate3D getOffset(Orientation3D orientation) {
    Coordinate3D a = orientation.getA().toCoordinate3D();
    Coordinate3D c = orientation.getC().toCoordinate3D();

    Direction3 bd = orientation.getB();
    bd = Direction3.valueOf(bd.getAxis(), false);
    Coordinate3D b = bd.toCoordinate3D();

    // @formatter:off
    return new Coordinate3D().plus(a.mult(1)).plus(b.mult(-2)).plus(c.mult(1));
    // @formatter:on
  }

  public static List<String> convert(MplCompilationResult result, MinecraftVersion version) {
    List<String> commands = new ArrayList<>();
    Orientation3D orientation = result.getOrientation();

    StringBuilder sb = new StringBuilder(header(version));
    // Appending initial fill Command to clear the required Area
    Coordinate3D max = getBoundaries(orientation, result.getBlocks().keySet());
    sb.append(commandHeader(version));
    sb.append("fill ");
    sb.append(new Coordinate3D().plus(getOffset(orientation)).toRelativeString()).append(' ');
    sb.append(max.plus(getOffset(orientation)).toRelativeString()).append(' ');
    sb.append("air");
    sb.append(commandTail(version));
    // Appending setblock for all Commands
    for (MplBlock block : result.getBlocks().values()) {
      if (!(block instanceof CommandBlock)) {
        continue;
      }
      StringBuilder convert = convert((CommandBlock) block, orientation, version);
      int totalLength = sb.length() + convert.length() + tail(version).length();
      if (totalLength > MAX_COMMAND_LENGTH) {
        sb.append(tail(version));
        commands.add(sb.toString());
        sb = new StringBuilder(header(version));
      }
      sb.append(convert);
    }
    sb.append(tail(version));
    commands.add(sb.toString());
    sb = new StringBuilder(header(version));

    return commands;
  }

  private static StringBuilder convert(CommandBlock block, Orientation3D orientation,
      MinecraftVersion version) {
    String coordinate = block.getCoordinate().plus(getOffset(orientation)).toRelativeString();
    String blockId = block.getStringBlockId();
    int damage = block.getDamageValue();
    StringBuilder sb = new StringBuilder(commandHeader(version));
    sb.append("setblock ");
    sb.append(coordinate).append(' ');
    sb.append(blockId).append(' ');
    sb.append(damage).append(' ');
    sb.append(REPLACE).append(' ');
    sb.append('{');
    if (!block.getNeedsRedstone()) {
      sb.append("auto:1,");
    }
    sb.append("Command:\\\"");
    sb.append(escapeCommandTwice(block.getCommand()));
    sb.append("\\\"}");
    sb.append(commandTail(version));
    return sb;
  }

  /**
   * A command must be escaped twice, because an install command is nested twice: a command block in
   * a command block minecraft in a command block.
   *
   * @param command
   * @return
   */
  private static String escapeCommandTwice(String command) {
    return StringUtils.escapeCommand(StringUtils.escapeCommand(command));
  }

}
