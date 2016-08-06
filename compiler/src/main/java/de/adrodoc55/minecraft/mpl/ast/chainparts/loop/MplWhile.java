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
package de.adrodoc55.minecraft.mpl.ast.chainparts.loop;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.interpretation.ChainPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(callSuper = true, of = {"trailing", "condition"})
@ToString(callSuper = true, of = {"trailing", "condition"})
public class MplWhile extends ModifiableChainPart implements ChainPartBuffer {
  private final @Nullable ChainPartBuffer parent;

  @Getter
  private final @Nullable String label;
  @Getter
  private final boolean not;
  @Getter
  private final boolean trailing;
  @Getter
  private final @Nullable String condition;

  private final Deque<ChainPart> chainParts = new ArrayDeque<>();

  public MplWhile(boolean not, boolean trailing, @Nullable String condition) {
    this(null, not, trailing, condition);
  }

  @GenerateMplPojoBuilder
  public MplWhile(@Nullable String label, boolean not, boolean trailing,
      @Nullable String condition) {
    this(null, label, not, trailing, condition);
  }

  public MplWhile(@Nullable ChainPartBuffer parent, @Nullable String label, boolean not,
      boolean trailing, @Nullable String condition) {
    super(new ModifierBuffer());
    this.parent = parent;
    this.label = label;
    this.not = not;
    this.trailing = trailing;
    this.condition = condition;
  }

  protected MplWhile(MplWhile original) {
    super(original);
    parent = original.parent;
    label = original.label;
    not = original.not;
    trailing = original.trailing;
    condition = original.condition;
  }

  @Deprecated
  @Override
  public MplWhile createFlatCopy(CopyScope scope) {
    return new MplWhile(this);
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) throws NullPointerException {
    super.completeDeepCopy(scope);
    MplWhile original = scope.getCache().getOriginal(this);
    chainParts.addAll(scope.copy(original.chainParts));
  }

  @Override
  public void add(ChainPart cp) {
    chainParts.add(cp);
  }

  @Override
  public Deque<ChainPart> getChainParts() {
    return new ArrayDeque<>(chainParts);
  }

  @VisibleForTesting
  public void setChainParts(Collection<ChainPart> chainParts) {
    this.chainParts.clear();
    this.chainParts.addAll(chainParts);
  }

  public @Nullable ChainPartBuffer exit() {
    return parent;
  }

  @Override
  public String getName() {
    return "while";
  }

  @Override
  public void accept(MplAstVisitor visitor) {
    visitor.visitWhile(this);
  }
}
