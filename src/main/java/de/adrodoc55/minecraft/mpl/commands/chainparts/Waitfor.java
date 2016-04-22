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

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.List;

import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.IllegalModifierException;

public class Waitfor extends PossiblyConditionalChainPart {
  private String event;

  public Waitfor(String event) {
    this(event, null);
  }

  public Waitfor(String event, Conditional conditional) {
    this.event = event;
    this.conditional = conditional;
  }

  @Override
  public List<ChainLink> toCommands(CompilerOptions options) throws IllegalModifierException {
    List<ChainLink> commands = super.toCommands();
    if (isConditional()) {
      commands.add(new InternalCommand("summon ArmorStand ${this + 3} {CustomName:" + event
          + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true));
      commands.add(new InvertingCommand(Mode.CHAIN));
      if (options.hasOption(TRANSMITTER)) {
        commands.add(new InternalCommand("setblock ${this + 1} redstone_block", true));
      } else {
        commands.add(new InternalCommand("blockdata ${this + 1} {auto:1}", true));
      }
    } else {
      commands.add(new InternalCommand("summon ArmorStand ${this + 1} {CustomName:" + event
          + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    }
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new Skip(false));
      commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commands.add(new InternalCommand("entitydata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
    }
    return commands;
  }

}
