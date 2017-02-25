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
package de.adrodoc55.minecraft.mpl.interpretation;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.Token;

import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.ExtendedModifiable;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@GenerateMplPojoBuilder
public class ModifierBuffer implements ExtendedModifiable {
  private @Nullable Mode mode;
  private @Nullable Conditional conditional;
  private @Nullable Boolean needsRedstone;
  private @Nullable Token modeToken;
  private @Nullable Token conditionalToken;
  private @Nullable Token needsRedstoneToken;

  @Override
  public Mode getMode() {
    return mode == null ? Mode.DEFAULT : mode;
  }

  public void setMode(Mode mode) {
    this.mode = checkNotNull(mode, "mode == null!");
  }

  @Override
  public Conditional getConditional() {
    return conditional == null ? Conditional.DEFAULT : conditional;
  }

  public void setConditional(Conditional conditional) {
    this.conditional = checkNotNull(conditional, "conditional == null!");
  }

  @Override
  public boolean getNeedsRedstone() {
    return needsRedstone == null ? getMode().getNeedsRedstoneByDefault() : needsRedstone;
  }

  public void setNeedsRedstone(boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

  public @Nullable Token getModeToken() {
    return modeToken;
  }

  public void setModeToken(@Nullable Token modeToken) {
    this.modeToken = modeToken;
  }

  public @Nullable Token getConditionalToken() {
    return conditionalToken;
  }

  public void setConditionalToken(@Nullable Token conditionalToken) {
    this.conditionalToken = conditionalToken;
  }

  public @Nullable Token getNeedsRedstoneToken() {
    return needsRedstoneToken;
  }

  public void setNeedsRedstoneToken(@Nullable Token needsRedstoneToken) {
    this.needsRedstoneToken = needsRedstoneToken;
  }
}
