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

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class RecursiveChainComputer implements ChainComputer {
  private static final int MAX_TRIES = 1000000;
  private Coordinate3D min;
  private Coordinate3D max;
  private List<Command> commands;

  private int tries = 0;

  public CommandBlockChain computeOptimalChain(CommandChain chain, Coordinate3D max) {
    this.min = new Coordinate3D();
    this.max = max;
    this.commands = chain.getCommands();
    optimalScore = Integer.MAX_VALUE;
    optimal.clear();
    tries = 0;
    calculateRecursively(null, min);

    CommandBlockChain output = toCommandBlockChain(chain, optimal);
    return output;
  }

  private void calculateRecursively(List<Coordinate3D> previous, Coordinate3D current) {
    tries++;
    if (tries > MAX_TRIES) {
      return;
    }
    if (previous == null) {
      previous = new ArrayList<Coordinate3D>();
    }
    if (previous.contains(current)) {
      return;
    }
    int x = current.getX();
    int y = current.getY();
    int z = current.getZ();
    if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || y > max.getY()
        || z > max.getZ()) {
      return;
    }

    previous.add(current);
    if (optimalScore <= calculateScore(previous)) {
      previous.remove(previous.size() - 1);
      return;
    }
    previous.remove(previous.size() - 1);

    int index = previous.size();
    if (index >= commands.size()) {
      previous.add(current);
      registerPossibility(previous);
      previous.remove(previous.size() - 1);
      return;
    } else {
      Direction3D[] directions;
      Command currentCommand = commands.get(index);
      if (currentCommand != null && currentCommand.isConditional()) {
        if (previous.isEmpty()) {
          throw new IllegalStateException("The first Command can't be conditional!");
        }
        Coordinate3D lastCoordinate = previous.get(previous.size() - 1);
        Coordinate3D relativeCoordinate = current.minus(lastCoordinate);
        Direction3D direction = Direction3D.valueOf(relativeCoordinate);
        directions = new Direction3D[] {direction};
      } else {
        directions = getDirections();
      }

      previous.add(current);
      for (Direction3D direction : directions) {
        calculateRecursively(previous, current.plus(direction.toCoordinate()));
      }
      previous.remove(previous.size() - 1);
    }
  }

  private static Direction3D[] getDirections() {
    return Direction3D.values();
    // return new Coordinate3D[] { Coordinate3D.SOUTH, Coordinate3D.UP,
    // Coordinate3D.NORTH, Coordinate3D.DOWN };
  }

  private int optimalScore = Integer.MAX_VALUE;
  private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

  private void registerPossibility(List<Coordinate3D> possibility) {
    int score = calculateScore(possibility);
    if (score < optimalScore) {
      optimal.clear();
      optimal.addAll(possibility);
      optimalScore = score;
    }
  }

  private int calculateScore(Iterable<Coordinate3D> coordinates) {
    return getMaxLength(coordinates);
  }

  private int getMaxLength(Iterable<Coordinate3D> coordinates) {
    int minX = min.getX();
    int minY = min.getY();
    int minZ = min.getZ();
    int maxX = 0;
    int maxY = 0;
    int maxZ = 0;
    for (Coordinate3D c : coordinates) {
      maxX = Math.max(maxX, c.getX());
      maxY = Math.max(maxY, c.getY());
      maxZ = Math.max(maxZ, c.getZ());
    }
    int x = maxX - minX;
    int y = maxY - minY;
    int z = maxZ - minZ;
    int i = Math.max(x, y);
    i = Math.max(i, z);
    return i;
  }

}
