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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class AStarChainComputer implements ChainComputer {

  @Override
  public CommandBlockChain computeOptimalChain(CommandChain chain, Coordinate3D max) {
    Coordinate3D min = new Coordinate3D();

    LinkedList<PathElement> todos = new LinkedList<PathElement>();

    List<Command> commands = chain.getCommands();
    todos.add(new PathElement(min, 0, commands));

    int totalChainSize = commands.size();
    // int count = 0;
    while (!todos.isEmpty()) {
      todos.sort(Comparator.naturalOrder());
      PathElement current = todos.poll();
      // System.out.println(current);
      // count++;
      // System.out.println(count);

      if (current.getPathLength() > totalChainSize) {
        return toCommandBlockChain(chain.getName(), current);
      }

      if (!current.hasEnoughSpace()) {
        continue;
      }

      Iterable<PathElement> validContinuations = current.getValidContinuations();

      for (PathElement p : validContinuations) {
        if (containsPath(todos, p)) {
          continue;
        }
        Coordinate3D pos = p.getPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || y > max.getY()
            || z > max.getZ()) {
          continue;
        }
        todos.add(p);
      }
    }
    return null;
  }

  private CommandBlockChain toCommandBlockChain(String name, PathElement path) {
    LinkedList<CommandBlock> chain = new LinkedList<CommandBlock>();
    PathElement following = path;
    if (path.getPrevious() != null) {
      chain.push(new CommandBlock(null, null, path.getPos()));
    }
    for (PathElement current = path.getPrevious(); current != null; current =
        current.getPrevious()) {
      Coordinate3D pos = current.getPos();
      List<Command> commands = current.getCommands();
      int index = current.getIndex();
      if (commands.size() > index) {
        Command command = commands.get(index);
        Direction direction = Direction.valueOf(following.getPos().minus(pos));
        CommandBlock block = new CommandBlock(command, direction, pos);
        chain.push(block);
      }
      following = current;
    }
    return new CommandBlockChain(name, chain);
  }

  private static boolean containsPath(Iterable<PathElement> iterable, PathElement p) {
    for (PathElement it : iterable) {
      if (it.pathEquals(p)) {
        return true;
      }
    }
    return false;
  }

}
