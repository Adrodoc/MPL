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
import static de.adrodoc55.minecraft.mpl.commands.chainparts.MplNotify.NOTIFY;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true, includeFieldNames = true)
public class MplBreakpoint extends PossiblyConditionalChainPart {

  private final @Nonnull String source;

  public MplBreakpoint(@Nonnull String source) {
    this(source, null);
  }

  public MplBreakpoint(@Nonnull String source, @Nullable Conditional conditional) {
    super(conditional);
    this.source = checkNotNull(source, "source == null!");
  }

  public MplBreakpoint(@Nonnull String source, @Nullable Conditional conditional,
      @Nullable Mode previousMode) {
    super(conditional, previousMode);
    this.source = checkNotNull(source, "source == null!");
  }

  @Override
  public List<? extends ChainLink> toCommands(CompilerOptions options)
      throws IllegalModifierException {
    List<ChainLink> commands = super.toCommands();
    Conditional conditional = isConditional() ? Conditional.CONDITIONAL : Conditional.UNCONDITIONAL;
    commands.add(new InternalCommand("say encountered breakpoint " + source, isConditional()));
    commands.addAll(new MplStart("breakpoint", conditional).toCommands(options));
    commands.addAll(new MplWaitfor("breakpoint" + NOTIFY, conditional).toCommands(options));
    return commands;
  }

  @Override
  public Mode getModeToInvert() throws IllegalModifierException {
    return CHAIN;
  }

}
