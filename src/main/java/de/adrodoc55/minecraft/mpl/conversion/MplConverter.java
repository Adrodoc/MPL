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
package de.adrodoc55.minecraft.mpl.conversion;

import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.commands.Command.Mode;

public abstract class MplConverter {

  public static String toBlockId(Mode mode) {
    if (mode == null) {
      throw new NullPointerException("mode == null");
    }
    switch (mode) {
      case IMPULSE:
        return "command_block";
      case CHAIN:
        return "chain_command_block";
      case REPEAT:
        return "repeating_command_block";
    }
    throw new IllegalArgumentException("Unknown Mode: " + mode);
  }

  protected static int toIntBlockId(Mode mode) {
    if (mode == null) {
      throw new NullPointerException("mode == null");
    }
    switch (mode) {
      case IMPULSE:
        return 137;
      case CHAIN:
        return 211;
      case REPEAT:
        return 210;
    }
    throw new IllegalArgumentException("Unknown Mode: " + mode);
  }

  protected static int toDamageValue(CommandBlock block) {
    int damage = toDamageValue(block.getDirection());
    if (block.isConditional()) {
      damage += 8;
    }
    return damage;
  }

  private static int toDamageValue(Direction3D direction) {
    if (direction == null) {
      throw new NullPointerException("mode == null");
    }
    switch (direction) {
      case DOWN:
        return 0;
      case UP:
        return 1;
      case NORTH:
        return 2;
      case SOUTH:
        return 3;
      case WEST:
        return 4;
      case EAST:
        return 5;
    }
    throw new IllegalArgumentException("Unknown Direction: " + direction);
  }
}
