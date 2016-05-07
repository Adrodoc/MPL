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
package de.adrodoc55.minecraft.mpl.interpretation;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.Token;

import de.adrodoc55.minecraft.mpl.ast.chainparts.Modifiable;
import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import lombok.Getter;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@Getter
// https://github.com/mkarneim/pojobuilder/issues/86
// @Setter
@GenerateMplPojoBuilder
public class ModifierBuffer implements Modifiable {
  private @Nullable Mode mode;
  private @Nullable Conditional conditional;
  private @Nullable Boolean needsRedstone;
  private @Nullable Token modeToken;
  private @Nullable Token conditionalToken;
  private @Nullable Token needsRedstoneToken;

  @Override
  public Boolean isConditional() {
    Conditional conditional = getConditional();
    if (conditional == null) {
      return null;
    }
    return conditional.isConditional();
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public void setConditional(Conditional conditional) {
    this.conditional = conditional;
  }

  public void setNeedsRedstone(Boolean needsRedstone) {
    this.needsRedstone = needsRedstone;
  }

  public void setModeToken(Token modeToken) {
    this.modeToken = modeToken;
  }

  public void setConditionalToken(Token conditionalToken) {
    this.conditionalToken = conditionalToken;
  }

  public void setNeedsRedstoneToken(Token needsRedstoneToken) {
    this.needsRedstoneToken = needsRedstoneToken;
  }

}
