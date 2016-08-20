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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.mpl.ast.ExtendedModifiable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
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
public class MplContinue extends ModifiableChainPart {
  private final @Nullable String label;
  private final @Nonnull MplWhile loop;

  public MplContinue(@Nullable String label, MplWhile loop, @Nonnull MplSource source) {
    this(label, loop, new ModifierBuffer(), source);
  }

  @GenerateMplPojoBuilder
  public MplContinue(@Nullable String label, MplWhile loop, ExtendedModifiable modifier,
      @Nonnull MplSource source) {
    super(modifier, source);
    this.label = label;
    this.loop = checkNotNull(loop, "loop == null!");
  }

  public MplContinue(@Nullable String label, MplWhile loop, ExtendedModifiable modifier,
      @Nullable Dependable previous, @Nonnull MplSource source) {
    super(modifier, previous, source);
    this.label = label;
    this.loop = checkNotNull(loop, "loop == null!");
  }

  @Deprecated
  protected MplContinue(MplContinue original, CopyScope scope) {
    super(original);
    label = original.label;
    loop = scope.copy(original.loop);
  }

  @Deprecated
  @Override
  public MplContinue createFlatCopy(CopyScope scope) {
    return new MplContinue(this, scope);
  }

  @Override
  public String getName() {
    return "continue";
  }

  @Override
  public List<ChainLink> accept(MplAstVisitor visitor) {
    return visitor.visitContinue(this);
  }
}
