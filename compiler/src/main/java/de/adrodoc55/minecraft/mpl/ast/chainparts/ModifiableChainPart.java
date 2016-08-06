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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.ExtendedModifiable;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
public abstract class ModifiableChainPart implements ExtendedModifiable, ChainPart {
  protected @Nonnull Mode mode;
  protected @Nonnull Conditional conditional;
  protected boolean needsRedstone;

  protected @Nullable Dependable previous;

  public ModifiableChainPart(ExtendedModifiable modifier) {
    this(modifier, null);
  }

  public ModifiableChainPart(ExtendedModifiable modifier, @Nullable Dependable previous) {
    setModifier(modifier);
    this.previous = previous;
  }

  private void setModifier(ExtendedModifiable modifier) {
    Mode mode = modifier.getMode();
    Conditional conditional = modifier.getConditional();
    Boolean needsRedstone = modifier.getNeedsRedstone();

    this.mode = (mode != null) ? mode : Mode.DEFAULT;
    this.conditional = (conditional != null) ? conditional : Conditional.DEFAULT;
    if (needsRedstone != null) {
      this.needsRedstone = needsRedstone;
    } else {
      this.needsRedstone = (this.mode == Mode.CHAIN) ? false : true;
    }
  }

  protected ModifiableChainPart(ModifiableChainPart original) {
    mode = original.mode;
    conditional = original.conditional;
    needsRedstone = original.needsRedstone;
  }

  @Deprecated
  @Override
  public void completeDeepCopy(CopyScope scope) throws NullPointerException {
    ModifiableChainPart original = scope.getCache().getOriginal(this);
    previous = scope.copyObject(original.previous);
  }

  @Override
  public @Nonnull Boolean isConditional() {
    return getConditional().isConditional();
  }

  @Override
  public @Nonnull Boolean getNeedsRedstone() {
    return needsRedstone;
  }

  /*
   * Kann sonst nicht aus javadoc referenziert werden
   */
  public @Nullable Dependable getPrevious() {
    return previous;
  }

  /*
   * see https://github.com/mkarneim/pojobuilder/issues/86
   */
  public void setPrevious(@Nullable Dependable previous) {
    this.previous = previous;
  }

  /*
   * see https://github.com/mkarneim/pojobuilder/issues/86
   */
  public void setConditional(Conditional conditional) {
    this.conditional = checkNotNull(conditional, "conditional == null!");
  }
}
