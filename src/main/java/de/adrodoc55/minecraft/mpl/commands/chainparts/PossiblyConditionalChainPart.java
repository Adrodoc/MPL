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

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

public abstract class PossiblyConditionalChainPart implements ChainPart {

  protected Mode previousMode;
  protected Conditional conditional;

  public Mode getPreviousMode() {
    return previousMode;
  }

  // FIXME: Muss aufgerufen werden, bevor toCommands aufgerufen wird
  public void setPreviousMode(Mode previousMode) {
    this.previousMode = previousMode;
  }

  public Conditional getConditional() {
    return conditional;
  }

  public void setConditional(Conditional conditional) {
    this.conditional = conditional;
  }

  public boolean isConditional() {
    if (conditional == null) {
      return false;
    }
    switch (conditional) {
      case UNCONDITIONAL:
        return false;
      case CONDITIONAL:
      case INVERT:
        return true;
      default:
        return false;
    }
  }

  public List<ChainLink> toCommands() throws IllegalModifierException {
    ArrayList<ChainLink> commands = new ArrayList<>();
    if (conditional == Conditional.INVERT) {
      if (previousMode == null) {
        throw new IllegalModifierException("The first command of a chain must be unconditional!");
      }
      InvertingCommand e = new InvertingCommand(previousMode);
      commands.add(e);
    }
    return commands;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((conditional == null) ? 0 : conditional.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PossiblyConditionalChainPart other = (PossiblyConditionalChainPart) obj;
    if (conditional != other.conditional)
      return false;
    return true;
  }

}
