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
package de.adrodoc55.minecraft.coordinate;

public enum Direction3D {
  // @formatter:off
  EAST(Coordinate3D.EAST, false, Axis3D.X),
  WEST(Coordinate3D.WEST, true, Axis3D.X),
  UP(Coordinate3D.UP, false, Axis3D.Y),
  DOWN(Coordinate3D.DOWN, true, Axis3D.Y),
  SOUTH(Coordinate3D.SOUTH, false, Axis3D.Z),
  NORTH(Coordinate3D.NORTH, true, Axis3D.Z);
  // @formatter:on

  public static Direction3D valueOf(Coordinate3D coordinate) {
    if (coordinate == null) {
      throw new NullPointerException("coordinate is null");
    }
    for (Direction3D direction : values()) {
      if (coordinate.equals(direction.toCoordinate())) {
        return direction;
      }
    }
    throw new IllegalArgumentException("No enum constant for coordinate " + coordinate);
  }

  public static Direction3D valueOf(Axis3D axis, boolean negative) {
    if (axis == null) {
      throw new NullPointerException("axis is null");
    }
    for (Direction3D direction : values()) {
      if (axis.equals(direction.getAxis()) && negative == direction.negative) {
        return direction;
      }
    }
    throw new InternalError(
        "This can never happen, because there must be a direction for every axis & negative combination!");
  }

  private final Coordinate3D relative;
  private final boolean negative;
  private final Axis3D axis;

  private Direction3D(Coordinate3D relative, boolean negative, Axis3D axis) {
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

  public Axis3D getAxis() {
    return axis;
  }
}
