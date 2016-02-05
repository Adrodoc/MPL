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
package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public interface ChainComputer {

  public default CommandBlockChain computeOptimalChain(CommandChain input) {
    return computeOptimalChain(input,
        new Coordinate3D(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
  }

  public abstract CommandBlockChain computeOptimalChain(CommandChain input, Coordinate3D max);

  public default CommandBlockChain toCommandBlockChain(CommandChain input,
      List<Coordinate3D> coordinates) {
    List<Command> commands = input.getCommands();
    if (commands.size() >= coordinates.size()) {
      throw new IllegalArgumentException(
          "To generate a CommandBlockChain one additional Coordinate is needed!");
    }
    List<CommandBlock> commandBlocks = new ArrayList<CommandBlock>(commands.size());
    for (int a = 0; a < commands.size(); a++) {
      Command currentCommand = commands.get(a);
      Coordinate3D currentCoordinate = coordinates.get(a);
      if (currentCommand == null) {
        commandBlocks.add(new CommandBlock(null, null, currentCoordinate));
        continue;
      }

      Coordinate3D nextCoordinate = coordinates.get(a + 1);
      Coordinate3D directionalCoordinate = nextCoordinate.minus(currentCoordinate);
      Direction direction = Direction.valueOf(directionalCoordinate);

      CommandBlock currentCommandBlock =
          new CommandBlock(currentCommand, direction, currentCoordinate);
      commandBlocks.add(currentCommandBlock);
    }
    if (!commandBlocks.isEmpty()) {
      commandBlocks.add(new CommandBlock(null, null, coordinates.get(coordinates.size() - 1)));
    }
    CommandBlockChain output = new CommandBlockChain(input.getName(), commandBlocks);
    return output;
  }

}
