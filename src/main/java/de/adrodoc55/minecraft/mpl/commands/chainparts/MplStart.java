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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

/**
 * @author Adrodoc55
 */
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true, includeFieldNames = true)
public class MplStart extends PossiblyConditionalChainPart implements ModeOwner {

  private final @Nonnull String process;

  public MplStart(@Nonnull String process) {
    this(process, null);
  }

  public MplStart(@Nonnull String process, @Nullable Conditional conditional) {
    super(conditional);
    this.process = checkNotNull(process, "process == null!");
  }

  public MplStart(@Nonnull String process, @Nullable Conditional conditional,
      @Nullable ModeOwner previous) {
    super(conditional, previous);
    this.process = checkNotNull(process, "process == null!");
  }

  @Override
  public String getName() {
    return "start";
  }

  @Override
  public List<ChainLink> toCommands(CompilerOptions options) throws IllegalModifierException {
    List<ChainLink> commands = super.toCommands();
    String command;
    if (options.hasOption(TRANSMITTER)) {
      command = "execute @e[name=" + process + "] ~ ~ ~ setblock ~ ~ ~ redstone_block";
    } else {
      command = "execute @e[name=" + process + "] ~ ~ ~ blockdata ~ ~ ~ {auto:1}";
    }
    commands.add(new Command(command, isConditional()));
    return commands;
  }

  @Override
  public Mode getMode() {
    return CHAIN;
  }

}
