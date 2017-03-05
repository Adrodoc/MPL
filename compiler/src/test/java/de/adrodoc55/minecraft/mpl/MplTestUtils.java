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
package de.adrodoc55.minecraft.mpl;

import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;

public class MplTestUtils extends MplTestBase {

  /**
   * Finds the first {@link Named} element in the given {@code collection}. If no element with the
   * given {@code name} can be found a {@link NoSuchElementException} will be thrown.
   *
   * @param name to search by
   * @param collection the {@link Collection} to search in
   * @return the found element
   * @throws NoSuchElementException if the collection does not contain an element with the given
   *         name
   */
  public static <N extends Named> N findByName(String name, Collection<N> collection)
      throws NoSuchElementException {
    return collection.stream().filter(c -> name.equals(c.getName())).findFirst().get();
  }

  public static List<ChainLink> mapToCommands(List<MplCommand> commands) {
    chainTogether(commands);
    List<ChainLink> result = new ArrayList<>(commands.size());
    for (MplCommand command : commands) {
      if (command.getConditional() == INVERT) {
        result.add(newInvertingCommand(command.getPrevious()));
      }
      CommandPartBuffer cmd = command.getCommandParts();
      result.add(newCommand(cmd, command));
    }
    return result;
  }

  /**
   * Add an unconditional {@link MplCommand} at the start and then chain all {@code commands}
   * together.
   *
   * @param commands to make valid
   * @return a valid list of commands
   */
  public static List<MplCommand> makeValid(List<MplCommand> commands) {
    commands.add(0, some($MplCommand().withConditional(UNCONDITIONAL)));
    return chainTogether(commands);
  }

  public static List<MplCommand> chainTogether(List<MplCommand> commands) {
    MplCommand lastCommand = null;
    for (MplCommand command : commands) {
      if (command.isConditional()) {
        command.setPrevious(lastCommand);
      } else {
        command.setPrevious(null);
      }
      lastCommand = command;
    }
    return commands;
  }

}
