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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import de.adrodoc55.commons.CopyScope;
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
@EqualsAndHashCode(callSuper = true, of = {"not", "condition"})
@ToString(callSuper = true, of = {"not", "condition"})
public class MplIf extends ModifiableChainPart implements ChainPartBuffer {
  private final @Nullable ChainPartBuffer parent;

  @Getter
  private final boolean not;
  @Getter
  private final @Nullable String condition;

  private Deque<ChainPart> thenParts = new ArrayDeque<>();
  private Deque<ChainPart> elseParts = new ArrayDeque<>();
  private boolean inElse;

  @GenerateMplPojoBuilder
  public MplIf(boolean not, String condition) {
    this(null, not, condition);
  }

  public MplIf(@Nullable ChainPartBuffer parent, boolean not, @Nullable String condition) {
    super(new ModifierBuffer());
    this.parent = parent;
    this.not = not;
    this.condition = condition;
  }

  protected MplIf(MplIf original, CopyScope scope) {
    super(original);
    parent = scope.copyObject(original.parent);
    not = original.not;
    condition = original.condition;
    inElse = original.inElse;
  }

  @Deprecated
  @Override
  public MplIf createFlatCopy(CopyScope scope) {
    return new MplIf(this, scope);
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) throws NullPointerException {
    super.completeDeepCopy(scope);
    MplIf original = scope.getCache().getOriginal(this);
    thenParts.addAll(scope.copy(original.thenParts));
    elseParts.addAll(scope.copy(original.elseParts));
  }

  @Override
  public void add(ChainPart cp) {
    if (!inElse) {
      thenParts.add(cp);
    } else {
      elseParts.add(cp);
    }
  }

  @Override
  public Deque<ChainPart> getChainParts() {
    if (!inElse) {
      return thenParts;
    } else {
      return elseParts;
    }
  }

  public void enterThen() {
    inElse = false;
  }

  public void enterElse() {
    inElse = true;
  }

  public @Nullable ChainPartBuffer exit() {
    return parent;
  }

  public Deque<ChainPart> getThenParts() {
    return new ArrayDeque<>(thenParts);
  }

  @VisibleForTesting
  void setThenParts(Collection<ChainPart> thenParts) {
    this.thenParts.clear();
    this.thenParts.addAll(thenParts);
  }

  public Deque<ChainPart> getElseParts() {
    return new ArrayDeque<>(elseParts);
  }

  @VisibleForTesting
  void setElseParts(Collection<ChainPart> elseParts) {
    this.elseParts.clear();
    this.elseParts.addAll(elseParts);
  }

  @Override
  public String getName() {
    return "if";
  }

  @Override
  public void accept(MplAstVisitor visitor) {
    visitor.visitIf(this);
  }

  public void switchThenAndElse() {
    Deque<ChainPart> oldThen = this.thenParts;
    thenParts = elseParts;
    elseParts = oldThen;
  }
}
