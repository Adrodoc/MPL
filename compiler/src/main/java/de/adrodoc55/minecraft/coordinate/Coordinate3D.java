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
package de.adrodoc55.minecraft.coordinate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.adrodoc55.minecraft.mpl.MplUtils;
import lombok.Data;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@Immutable
@Data
public class Coordinate3D {
  public static final Coordinate3D SELF = new Coordinate3D(0, 0, 0);
  public static final Coordinate3D EAST = new Coordinate3D(1, 0, 0);
  public static final Coordinate3D WEST = new Coordinate3D(-1, 0, 0);
  public static final Coordinate3D UP = new Coordinate3D(0, 1, 0);
  public static final Coordinate3D DOWN = new Coordinate3D(0, -1, 0);
  public static final Coordinate3D SOUTH = new Coordinate3D(0, 0, 1);
  public static final Coordinate3D NORTH = new Coordinate3D(0, 0, -1);

  private final double x;
  private final double y;
  private final double z;

  public Coordinate3D() {
    this(0, 0, 0);
  }

  public Coordinate3D(Coordinate3D other) {
    this(other.x, other.y, other.z);
  }

  @GenerateMplPojoBuilder
  public Coordinate3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coordinate3D copy() {
    return new Coordinate3D(this);
  }

  public Coordinate3D plus(Coordinate3D other) {
    double x = this.x + other.x;
    double y = this.y + other.y;
    double z = this.z + other.z;
    return new Coordinate3D(x, y, z);
  }

  public Coordinate3D minus(Coordinate3D other) {
    double x = this.x - other.x;
    double y = this.y - other.y;
    double z = this.z - other.z;
    return new Coordinate3D(x, y, z);
  }

  public double get(Axis3D axis) {
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

  public double get(Direction3D d) {
    double value = get(d.getAxis());
    if (d.isNegative()) {
      return -value;
    } else {
      return value;
    }
  }

  public Coordinate3D plus(double skalar, Direction3D direction) {
    skalar = direction.isNegative() ? -skalar : skalar;
    return plus(skalar, direction.getAxis());
  }

  public Coordinate3D plus(double skalar, Axis3D axis) {
    switch (axis) {
      case X:
        double x = this.x + skalar;
        return new Coordinate3D(x, this.y, this.z);
      case Y:
        double y = this.y + skalar;
        return new Coordinate3D(this.x, y, this.z);
      case Z:
        double z = this.z + skalar;
        return new Coordinate3D(this.x, this.y, z);
      default:
        throw new IllegalArgumentException("axis must not be null");
    }
  }

  public Coordinate3D minus(double skalar, Direction3D direction) {
    skalar = direction.isNegative() ? -skalar : skalar;
    return minus(skalar, direction.getAxis());
  }

  public Coordinate3D minus(double skalar, Axis3D axis) {
    switch (axis) {
      case X:
        double x = this.x - skalar;
        return new Coordinate3D(x, this.y, this.z);
      case Y:
        double y = this.y - skalar;
        return new Coordinate3D(this.x, y, this.z);
      case Z:
        double z = this.z - skalar;
        return new Coordinate3D(this.x, this.y, z);
      default:
        throw new IllegalArgumentException("axis must not be null");
    }
  }

  /**
   * Simple scalar multiplication.
   *
   * @param scalar to multiply this with
   * @return a copy of this coordinate that is multiplied with the scalar
   */
  public Coordinate3D mult(double scalar) {
    double x = this.x * scalar;
    double y = this.y * scalar;
    double z = this.z * scalar;
    return new Coordinate3D(x, y, z);
  }

  private String dts(double d) {
    return MplUtils.toString(d);
  }

  /**
   * Double to String no zero. This method converts a double into a String, if the double is zero it
   * will return an empty string.
   *
   * @param d double to convert
   * @return string representation
   */
  private String dtsnz(double d) {
    String result = dts(d);
    if ("0".equals(result))
      return "";
    return result;
  }

  public String toAbsoluteString() {
    return dts(x) + " " + dts(y) + " " + dts(z);
  }

  public String toRelativeString() {
    return "~" + dtsnz(x) + " ~" + dtsnz(y) + " ~" + dtsnz(z);
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
