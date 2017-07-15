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
package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInternalCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckReturnValue;

import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import de.adrodoc55.minecraft.mpl.interpretation.insert.TargetedThisInsert;

/**
 * @author Adrodoc55
 */
public class ProcessCommandsHelper {
  protected final CompilerOptions options;

  public ProcessCommandsHelper(CompilerOptions options) {
    this.options = checkNotNull(options, "options == null!");
  }

  @CheckReturnValue
  public String getStartCommandHeader() {
    return options.hasOption(TRANSMITTER) ? "setblock " : "blockdata ";
  }

  @CheckReturnValue
  public String getStartCommandTrailer() {
    return options.hasOption(TRANSMITTER) ? " redstone_block" : " {auto:1b}";
  }

  /**
   * Returns a command that starts whatever is at the execution coordinates.
   *
   * @return a command that starts whatever is at the execution coordinates
   */
  @CheckReturnValue
  public String getStartCommand() {
    return getStartCommandHeader() + "~ ~ ~" + getStartCommandTrailer();
  }

  @CheckReturnValue
  protected CommandPartBuffer getStartCommand(TargetedThisInsert insert) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStartCommandHeader());
    result.add(insert);
    result.add(getStartCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected CommandPartBuffer getStartCommand(ChainLink target) {
    return getStartCommand(new TargetedThisInsert(target));
  }

  @CheckReturnValue
  public String getStopCommandHeader() {
    return options.hasOption(TRANSMITTER) ? "setblock " : "blockdata ";
  }

  @CheckReturnValue
  public String getStopCommandTrailer() {
    if (options.hasOption(TRANSMITTER)) {
      if (options.hasOption(DEBUG)) {
        return " air";
      } else {
        return " stone";
      }
    } else {
      return " {auto:0b}";
    }
  }

  /**
   * Returns a command that stops whatever is at the execution coordinates.
   *
   * @return a command that stops whatever is at the execution coordinates
   */
  @CheckReturnValue
  public String getStopCommand() {
    return getStopCommandHeader() + "~ ~ ~" + getStopCommandTrailer();
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(TargetedThisInsert insert) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStopCommandHeader());
    result.add(insert);
    result.add(getStopCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(ChainLink target) {
    return getStopCommand(new TargetedThisInsert(target));
  }

  @CheckReturnValue
  protected CommandPartBuffer getStopCommand(int relative) {
    CommandPartBuffer result = new CommandPartBuffer();
    result.add(getStopCommandHeader());
    result.add(new RelativeThisInsert(relative));
    result.add(getStopCommandTrailer());
    return result;
  }

  @CheckReturnValue
  protected List<ChainLink> getRestartBackref(ChainLink target, boolean conditional) {
    List<ChainLink> result = new ArrayList<>(2);
    result.add(
        newInternalCommand(getStopCommand(target), modifier(Conditional.valueOf(conditional))));
    result.add(newInternalCommand(getStartCommand(target), modifier(CONDITIONAL)));
    return result;
  }

  @CheckReturnValue
  protected List<ChainLink> newJumpDestination(boolean internal) {
    if (options.hasOption(TRANSMITTER)) {
      List<ChainLink> result = new ArrayList<>(2);
      result.add(new MplSkip(internal));
      result.add(newInternalCommand(getStopCommand(-1), modifier(IMPULSE)));
      return result;
    } else {
      List<ChainLink> result = new ArrayList<>(1);
      if (internal) {
        result.add(newInternalCommand(getStopCommand(), modifier(IMPULSE)));
      } else {
        result.add(newCommand(getStopCommand(), modifier(IMPULSE)));
      }
      return result;
    }
  }
}
