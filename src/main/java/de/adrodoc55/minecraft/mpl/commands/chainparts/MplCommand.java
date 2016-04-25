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
package de.adrodoc55.minecraft.mpl.commands.chainparts;

import java.util.List;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true, includeFieldNames = true)
public class MplCommand extends PossiblyConditionalChainPart implements ModeOwner {

  private String command;
  private Mode mode;
  private boolean needsRedstone;

  public MplCommand() {
    this("");
  }

  public MplCommand(String command) {
    this(command, null);
  }

  public MplCommand(String command, Conditional conditional) {
    this(command, null, conditional);
  }

  public MplCommand(String command, Mode mode, Conditional conditional) {
    this(command, mode, conditional, null);
  }

  @GenerateMplPojoBuilder
  public MplCommand(String command, Mode mode, Conditional conditional, Boolean needsRedstone) {
    super(conditional);
    setCommand(command);
    this.mode = (mode != null) ? mode : Mode.CHAIN;
    if (needsRedstone != null) {
      this.needsRedstone = needsRedstone;
    } else {
      this.needsRedstone = (this.mode == Mode.CHAIN) ? false : true;
    }
  }

  public MplCommand(MplCommand command) {
    this(command.getCommand(), command.getMode(), command.getConditional(),
        command.needsRedstone());
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Override
  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public boolean needsRedstone() {
    return needsRedstone;
  }

  public void setNeedsRedstone(boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

  @Override
  public String getName() {
    return "command";
  }

  @Override
  public List<ChainLink> toCommands(CompilerOptions options) throws IllegalModifierException {
    List<ChainLink> commands = super.toCommands();
    commands.add(new Command(getCommand(), getMode(), isConditional(), needsRedstone()));
    return commands;
  }

}
