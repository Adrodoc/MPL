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
package de.adrodoc55.minecraft.mpl.antlr;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;

class CommandBufferFactory {
  private boolean first = true;

  public CommandBuffer create() {
    if (first) {
      first = false;
      return new UnconditionalCommandBuffer();
    } else {
      return new CommandBuffer();
    }
  }

  static class UnconditionalCommandBuffer extends CommandBuffer {
    private UnconditionalCommandBuffer() {}

    @Override
    public void setConditional(Conditional conditional) throws IllegalModifierException {
      if (conditional != Conditional.UNCONDITIONAL) {
        throw new IllegalModifierException(
            "The first command of a chain can only be unconditional.");
      }
      super.setConditional(conditional);
    }
  }

  static class CommandBuffer {

    public static enum Conditional {
      UNCONDITIONAL, CONDITIONAL, INVERT
    }

    private String command;
    private Mode mode;
    private Conditional conditional;
    private Boolean needsRedstone;

    private CommandBuffer() {}

    public Command toCommand() {
      Boolean conditional = isConditional();
      return new Command(command, mode, conditional, needsRedstone);
    }

    public String getCommand() {
      return command;
    }

    public void setCommand(String command) {
      this.command = command;
    }

    public Mode getMode() {
      return mode;
    }

    public void setMode(Mode mode) {
      this.mode = mode;
    }

    public Boolean isConditional() {
      if (conditional == null) {
        return null;
      }
      switch (conditional) {
        case UNCONDITIONAL:
          return false;
        case CONDITIONAL:
        case INVERT:
          return true;
        default:
          return null;
      }
    }

    public Conditional getConditional() {
      return conditional;
    }

    public void setConditional(Conditional conditional) throws IllegalModifierException {
      this.conditional = conditional;
    }

    public Boolean getNeedsRedstone() {
      return needsRedstone;
    }

    public void setNeedsRedstone(Boolean needsRedstone) {
      this.needsRedstone = needsRedstone;
    }

  }
}