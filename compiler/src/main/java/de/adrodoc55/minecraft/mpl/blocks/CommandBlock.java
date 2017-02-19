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
package de.adrodoc55.minecraft.mpl.blocks;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import com.google.common.collect.Collections2;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Setter
public class CommandBlock extends MplBlock {
  private @Nonnull Command command;
  private @Nonnull Direction3D direction;

  public CommandBlock(Command command, Direction3D direction, Coordinate3D coordinate) {
    super(coordinate);
    this.command = checkNotNull(command, "command == null!");
    this.direction = checkNotNull(direction, "direction == null!");
  }

  public Command toCommand() {
    return command;
  }

  @SuppressWarnings("deprecation")
  public String getCommand() {
    return command.getCommand();
  }

  public void setCommand(String command) {
    this.command.setCommand(command);
  }

  public boolean isConditional() {
    return command != null ? command.isConditional() : false;
  }

  public void setConditional(boolean conditional) {
    command.setConditional(conditional);
  }

  public Mode getMode() {
    return command != null ? command.getMode() : null;
  }

  public void setMode(Mode mode) {
    command.setMode(mode);
  }

  public boolean getNeedsRedstone() {
    return command != null ? command.getNeedsRedstone() : false;
  }

  public void setNeedsRedstone(boolean needsRedstone) {
    command.setNeedsRedstone(needsRedstone);
  }

  @Override
  public byte getByteBlockId() {
    return getMode().getByteBlockId();
  }

  @Override
  public String getStringBlockId() {
    return getMode().getStringBlockId();
  }

  public byte getDamageValue() {
    byte damage = getDamageValue(getDirection());
    if (isConditional()) {
      damage += 8;
    }
    return damage;
  }

  public static byte getDamageValue(Direction3D direction) {
    if (direction == null) {
      throw new NullPointerException("direction == null");
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

  @Override
  public boolean isInternal() {
    return command.isInternal();
  }

  @Override
  public void resolveThisInserts(Collection<MplBlock> blocks) {
    for (RelativeThisInsert insert : command.getCommandParts().getThisInserts()) {
      int relative = insert.getRelative();
      if (relative == 0) {
        insert.setCoordinate(new Coordinate3D());
        return;
      }
      Collection<MplBlock> nonInternalBlocks = Collections2.filter(blocks, b -> !b.isInternal());

      Iterator<MplBlock> it;
      if (relative > 0) {
        it = nonInternalBlocks.iterator();
      } else {
        it = new ArrayDeque<>(nonInternalBlocks).descendingIterator();
      }

      while (it.hasNext()) {
        MplBlock thisBlock = it.next();
        if (this == thisBlock) {
          MplBlock target = null;
          for (int r = 0; r < Math.abs(relative); r++) {
            if (it.hasNext()) {
              target = it.next();
            } else {
              if (relative > 0) {
                // after chain
              } else {
                // before chain
              }
            }
          }
          insert.setCoordinate(target.getCoordinate().minus(getCoordinate()));
        }
        throw new IllegalArgumentException(
            "Failed to resolve insert, target is not contained in chain");
      }
      throw new IllegalArgumentException("This CommandBlock is not contained in the chain");
    }
  }

}
