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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.mpl.ast.ExtendedModifiable;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class MplStop extends ModifiableChainPart {
  private final String selector;

  public MplStop(String selector, @Nonnull MplSource source) {
    this(selector, new ModifierBuffer(), source);
  }

  @GenerateMplPojoBuilder
  public MplStop(String selector, ExtendedModifiable modifier, @Nonnull MplSource source) {
    super(modifier, source);
    this.selector = checkNotNull(selector, "selector == null!");
  }

  public MplStop(String selector, ExtendedModifiable modifier, @Nullable Dependable previous,
      @Nonnull MplSource source) {
    super(modifier, previous, source);
    this.selector = checkNotNull(selector, "selector == null!");
  }

  protected MplStop(MplStop original) {
    super(original);
    selector = original.selector;
  }

  @Deprecated
  @Override
  public MplStop createFlatCopy(CopyScope scope) {
    return new MplStop(this);
  }

  @Override
  public String getName() {
    return "stop";
  }

  @Override
  public List<ChainLink> accept(MplAstVisitor visitor) {
    return visitor.visitStop(this);
  }

  @Override
  public boolean canBeDependedOn() {
    return true;
  }

  @Override
  public Mode getModeForInverting() {
    return getMode();
  }
}
