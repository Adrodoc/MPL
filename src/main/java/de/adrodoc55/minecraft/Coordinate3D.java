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
package de.adrodoc55.minecraft;

import java.util.ArrayList;
import java.util.List;

public class Coordinate3D {

  public static final Coordinate3D SELF = new Coordinate3D(0, 0, 0);
  public static final Coordinate3D EAST = new Coordinate3D(1, 0, 0);
  public static final Coordinate3D WEST = new Coordinate3D(-1, 0, 0);
  public static final Coordinate3D UP = new Coordinate3D(0, 1, 0);
  public static final Coordinate3D DOWN = new Coordinate3D(0, -1, 0);
  public static final Coordinate3D SOUTH = new Coordinate3D(0, 0, 1);
  public static final Coordinate3D NORTH = new Coordinate3D(0, 0, -1);

  public static enum Axis {
    X, Y, Z
  }

  public static enum Direction {
    // @formatter:off
    EAST(Coordinate3D.EAST, false, Axis.X),
    WEST(Coordinate3D.WEST, true, Axis.X),
    UP(Coordinate3D.UP, false, Axis.Y),
    DOWN(Coordinate3D.DOWN, true, Axis.Y),
    SOUTH(Coordinate3D.SOUTH, false, Axis.Z),
    NORTH(Coordinate3D.NORTH, true, Axis.Z);
    // @formatter:on
    public static Direction valueOf(Coordinate3D coordinate) {
      if (coordinate == null) {
        throw new NullPointerException("coordinate is null");
      }
      for (Direction direction : values()) {
        if (coordinate.equals(direction.toCoordinate())) {
          return direction;
        }
      }
      throw new IllegalArgumentException("No enum constant for coordinate " + coordinate);
    }

    private final Coordinate3D relative;
    private final boolean negative;
    private final Axis axis;

    private Direction(Coordinate3D relative, boolean negative, Axis axis) {
      this.relative = relative;
      this.negative = negative;
      this.axis = axis;
    }

    public Coordinate3D toCoordinate() {
      return relative;
    }

    public boolean isNegative() {
      return negative;
    }

    public Axis getAxis() {
      return axis;
    }
  }

  private final int x;
  private final int y;
  private final int z;

  public Coordinate3D() {
    this(0, 0, 0);
  }

  public Coordinate3D(Coordinate3D other) {
    this(other.x, other.y, other.z);
  }

  public Coordinate3D(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coordinate3D copy() {
    return new Coordinate3D(this);
  }

  public Coordinate3D plus(Coordinate3D other) {
    int x = overflowSaveAddition(this.x, other.x);
    int y = overflowSaveAddition(this.y, other.y);
    int z = overflowSaveAddition(this.z, other.z);
    return new Coordinate3D(x, y, z);
  }

  public Coordinate3D minus(Coordinate3D other) {
    int x = overflowSaveSubstraction(this.x, other.x);
    int y = overflowSaveSubstraction(this.y, other.y);
    int z = overflowSaveSubstraction(this.z, other.z);
    return new Coordinate3D(x, y, z);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public int get(Axis axis) {
    switch (axis) {
      case X:
        return getX();
      case Y:
        return getY();
      case Z:
        return getZ();
      default:
        throw new IllegalArgumentException("axis must not be null");
    }
  }

  public static int overflowSaveAddition(int a, int b) {
    int c;
    try {
      c = Math.addExact(a, b);
    } catch (ArithmeticException ex) {
      if (a < 0) {
        c = Integer.MIN_VALUE;
      } else {
        c = Integer.MAX_VALUE;
      }
    }
    return c;
  }

  public static int overflowSaveSubstraction(int a, int b) {
    return overflowSaveAddition(a, b * -1);
  }

  /**
   * Einfache Skalarmultiplikation. Nicht Überlaufsicher.
   *
   * @param skalar
   * @return
   */
  public Coordinate3D mult(int skalar) {
    int x = this.x * skalar;
    int y = this.y * skalar;
    int z = this.z * skalar;
    return new Coordinate3D(x, y, z);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
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
    Coordinate3D other = (Coordinate3D) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    if (z != other.z)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Coordinate3D [x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  public String toAbsoluteString() {
    return x + " " + y + " " + z;
  }

  public String toRelativeString() {
    return "~" + x + " ~" + y + " ~" + z;
  }

  public List<Coordinate3D> getAdjacent() {
    List<Coordinate3D> directions = getDirections();
    List<Coordinate3D> possibleCoodinates = new ArrayList<Coordinate3D>(directions.size());
    for (Coordinate3D d : directions) {
      Coordinate3D next = plus(d);
      possibleCoodinates.add(next);
    }
    return possibleCoodinates;
  }

  private static final ArrayList<Coordinate3D> directions = new ArrayList<Coordinate3D>(6);

  static {
    directions.add(EAST);
    directions.add(WEST);
    directions.add(UP);
    directions.add(DOWN);
    directions.add(SOUTH);
    directions.add(NORTH);
  }

  private static List<Coordinate3D> getDirections() {
    return directions;
  }
}
