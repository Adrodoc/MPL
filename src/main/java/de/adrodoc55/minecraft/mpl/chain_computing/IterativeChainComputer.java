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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.mpl.ChainPart;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class IterativeChainComputer implements ChainComputer {

  private Coordinate3D min;
  private Coordinate3D max;

  private int optimalScore;
  private List<ChainPart> commands;
  private List<Coordinate3D> path;
  private int i;
  private ChainPart currentCommand;
  private Coordinate3D currentCoordinate;

  private static final int MAX_TRIES = 1000000;

  public CommandBlockChain computeOptimalChain(CommandChain chain, Coordinate3D max) {
    this.min = new Coordinate3D();
    this.max = max;
    optimalScore = Integer.MAX_VALUE;
    optimal.clear();
    this.commands = chain.getCommands();
    @SuppressWarnings("unchecked")
    List<Coordinate3D>[] todos = new List[commands.size() + 1];
    path = new ArrayList<Coordinate3D>(commands.size() + 1);
    path.add(min);
    int tries = 0;
    while (!path.isEmpty()) {
      tries++;
      if (tries > MAX_TRIES) {
        break;
      }
      i = path.size() - 1;
      currentCoordinate = path.get(i);
      if (todos[i] == null) {
        if (!canPathContinue()) {
          todos[i] = null;
          path.remove(i);
          continue;
        }
        if (i >= commands.size()) {
          registerPath(path);
          todos[i] = null;
          path.remove(i);
          continue;
        }
        todos[i] = getNextCoordinates();
      }
      List<Coordinate3D> currentTodos = todos[i];
      if (currentTodos.isEmpty()) {
        todos[i] = null;
        path.remove(i);
        continue;
      } else {
        Coordinate3D currentTodo = currentTodos.get(0);
        currentTodos.remove(0);
        path.add(currentTodo);
        continue;
      }
    }
    if (optimal.isEmpty()) {
      throw new IllegalStateException(
          "Couldn't find a Solution for '" + chain.getName() + "' within " + MAX_TRIES + " tries!");
    }
    CommandBlockChain output = toCommandBlockChain(chain, optimal);
    return output;
  }

  private List<Coordinate3D> getNextCoordinates() {
    Direction3D[] directions;
    currentCommand = commands.get(i);
    if (currentCommand instanceof Command && ((Command) currentCommand).isConditional()) {
      if (path.size() < 2) {
        throw new IllegalStateException("The first Command can't be conditional!");
      }
      Coordinate3D previousCoordinate = path.get(i - 1);
      Direction3D direction = Direction3D.valueOf(currentCoordinate.minus(previousCoordinate));
      directions = new Direction3D[] {direction};
    } else {
      directions = Direction3D.values();
    }
    List<Coordinate3D> coords = getAdjacentCoordinates(currentCoordinate, directions);
    return coords;
  }

  private static List<Coordinate3D> getAdjacentCoordinates(Coordinate3D coordinate,
      Direction3D[] directions) {
    List<Coordinate3D> coords = new ArrayList<Coordinate3D>(directions.length);
    for (Direction3D direction : directions) {
      coords.add(coordinate.plus(direction.toCoordinate()));
    }
    return coords;
  }

  private boolean canPathContinue() {
    if (!isCoordinateValid(currentCoordinate)) {
      return false;
    }

    Set<Coordinate3D> validCoordinates = new HashSet<Coordinate3D>();
    List<Coordinate3D> todos = new ArrayList<Coordinate3D>();
    todos.add(currentCoordinate);
    while (!todos.isEmpty()) {
      Coordinate3D coordinate = todos.get(0);
      todos.remove(0);
      List<Coordinate3D> adjacentCoordinates =
          getAdjacentCoordinates(coordinate, Direction3D.values());
      for (Coordinate3D a : adjacentCoordinates) {
        if (isCoordinateValid(a) && !validCoordinates.contains(a)) {
          validCoordinates.add(a);
          todos.add(a);
        }
      }
    }

    return true;
  }

  protected boolean isCoordinateValid(Coordinate3D coordinate) {
    if (i != path.indexOf(coordinate)) {
      return false;
    }

    int x = coordinate.getX();
    int y = coordinate.getY();
    int z = coordinate.getZ();
    if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || y > max.getY()
        || z > max.getZ()) {
      return false;
    }

    if (optimalScore <= calculateCost(path)) {
      return false;
    }
    return true;
  }

  private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

  private void registerPath(List<Coordinate3D> path) {
    int score = calculateCost(path);
    if (score < optimalScore) {
      optimal.clear();
      optimal.addAll(path);
      optimalScore = score;
    }
  }

  /**
   * Higher Values indicate a higher Cost.
   *
   * @param coordinates
   * @return cost
   */
  protected int calculateCost(List<Coordinate3D> coordinates) {
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
