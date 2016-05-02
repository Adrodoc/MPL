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
package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(doNotUseGetters = false)
@ToString(includeFieldNames = true)
@Getter
@Setter
public class Command implements ChainLink {
  protected String command;
  protected Mode mode;
  protected boolean conditional;
  protected boolean needsRedstone;

  public Command() {
    this("");
  }

  public Command(String command) {
    this(command, null, null);
  }

  public Command(String command, Mode mode) {
    this(command, mode, null);
  }

  public Command(String command, Boolean conditional) {
    this(command, null, conditional);
  }

  public Command(String command, Mode mode, Boolean conditional) {
    this(command, mode, conditional, null);
  }

  @GenerateMplPojoBuilder
  public Command(String command, Mode mode, Boolean conditional, Boolean needsRedstone) {
    setCommand(command);

    this.conditional = (conditional != null) ? conditional : false;
    this.mode = (mode != null) ? mode : Mode.CHAIN;

    if (needsRedstone != null) {
      this.needsRedstone = needsRedstone;
    } else {
      this.needsRedstone = (this.mode == Mode.CHAIN) ? false : true;
    }
  }

  public Command(Command command) {
    this(command.getCommand(), command.getMode(), command.isConditional(), command.needsRedstone());
  }

  public void setCommand(String command) {
    if (command != null && command.startsWith("/")) {
      this.command = command.substring(1);
    } else {
      this.command = command;
    }
  }

  public boolean needsRedstone() {
    return isNeedsRedstone();
  }

  @Override
  public MplBlock toBlock(Coordinate3D coordinate) {
    // FIXME: Direction korrigieren
    return new CommandBlock(this, Direction3D.UP, coordinate);
  }

}
