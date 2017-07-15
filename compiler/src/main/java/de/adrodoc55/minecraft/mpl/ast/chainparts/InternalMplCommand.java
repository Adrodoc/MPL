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
package de.adrodoc55.minecraft.mpl.ast.chainparts;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.commons.CopyScope.Copyable;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;

/**
 * @author Adrodoc55
 */
public class InternalMplCommand extends ModifiableChainPart {
  private final ImmutableList<Command> chainLinks;

  public InternalMplCommand(MplSource source, List<? extends Command> chainLinks) {
    super(new ModifierBuffer(), source);
    checkArgument(!chainLinks.isEmpty(), "chainlinks must not be empty!");
    this.chainLinks = ImmutableList.copyOf(chainLinks);
  }

  public InternalMplCommand(MplSource source, Command... chainLinks) {
    this(source, Arrays.asList(chainLinks));
  }

  @Deprecated
  protected InternalMplCommand(InternalMplCommand original, CopyScope scope) {
    super(original);
    chainLinks = ImmutableList.copyOf(scope.copy(original.chainLinks));
  }

  @Deprecated
  @Override
  public Copyable createFlatCopy(CopyScope scope) throws NullPointerException {
    return new InternalMplCommand(this, scope);
  }

  @Override
  public String getName() {
    return "internal command";
  }

  public ImmutableList<ChainLink> getChainLinks() {
    Command first = chainLinks.get(0);
    first.setModifier(this);
    return ImmutableList.copyOf(chainLinks);
  }

  @Override
  public <T> T accept(MplAstVisitor<T> visitor) {
    return visitor.visitInternalCommand(this);
  }

}
