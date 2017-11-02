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
import java.util.Collection;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Collections2;

import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * A three dimensional {@code int} coordinate.
 *
 * @author Adrodoc55
 */
@Immutable
public class Coordinate3I implements Cloneable {
  public static final Coordinate3I SELF = new Coordinate3I(0, 0, 0);
  public static final Coordinate3I EAST = new Coordinate3I(1, 0, 0);
  public static final Coordinate3I WEST = new Coordinate3I(-1, 0, 0);
  public static final Coordinate3I UP = new Coordinate3I(0, 1, 0);
  public static final Coordinate3I DOWN = new Coordinate3I(0, -1, 0);
  public static final Coordinate3I SOUTH = new Coordinate3I(0, 0, 1);
  public static final Coordinate3I NORTH = new Coordinate3I(0, 0, -1);
  private static final Collection<Coordinate3I> DIRECTIONS = new ArrayList<Coordinate3I>(6);

  static {
    DIRECTIONS.add(EAST);
    DIRECTIONS.add(WEST);
    DIRECTIONS.add(UP);
    DIRECTIONS.add(DOWN);
    DIRECTIONS.add(SOUTH);
    DIRECTIONS.add(NORTH);
  }

  public static Optional<Coordinate3I> getMin(Collection<Coordinate3I> coordinates) {
    return coordinates.stream().reduce(getBinaryOperator(Math::min));
  }

  public static Optional<Coordinate3I> getMax(Collection<Coordinate3I> coordinates) {
    return coordinates.stream().reduce(getBinaryOperator(Math::max));
  }

  public static BinaryOperator<Coordinate3I> getBinaryOperator(IntBinaryOperator op) {
    return (a, b) -> {
      int x = op.applyAsInt(a.x, b.x);
      int y = op.applyAsInt(a.y, b.y);
      int z = op.applyAsInt(a.z, b.z);
      return new Coordinate3I(x, y, z);
    };
  }

  public final int x;
  public final int y;
  public final int z;

  public Coordinate3I() {
    this(0, 0, 0);
  }

  @GenerateMplPojoBuilder
  public Coordinate3I(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coordinate3I(Coordinate3I other) {
    this(other.x, other.y, other.z);
  }

  public Coordinate3I copy() {
    return new Coordinate3I(this);
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError(ex);
    }
  }

  public Coordinate3D to3D() {
    return new Coordinate3D(x, y, z);
  }

  /**
   * Returns the value of {@link #x}.
   *
   * @return the value of {@link #x}
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the value of {@link #y}.
   *
   * @return the value of {@link #y}
   */
  public int getY() {
    return y;
  }

  /**
   * Returns the value of {@link #z}.
   *
   * @return the value of {@link #z}
   */
  public int getZ() {
    return z;
  }

  public Coordinate3I plus(Coordinate3I other) {
    int x = this.x + other.x;
    int y = this.y + other.y;
    int z = this.z + other.z;
    return new Coordinate3I(x, y, z);
  }

  public Coordinate3I minus(Coordinate3I other) {
    int x = this.x - other.x;
    int y = this.y - other.y;
    int z = this.z - other.z;
    return new Coordinate3I(x, y, z);
  }

  public int get(Axis3 axis) {
    return axis.of(this);
  }

  public double get(Direction3 d) {
    double value = get(d.getAxis());
    if (d.isNegative()) {
      return -value;
    } else {
      return value;
    }
  }

  public Coordinate3I plus(int scalar, Direction3 direction) {
    scalar = direction.isNegative() ? -scalar : scalar;
    return plus(scalar, direction.getAxis());
  }

  public Coordinate3I plus(int scalar, Axis3 axis) {
    return axis.plus(this, scalar);
  }

  public Coordinate3I minus(int scalar, Direction3 direction) {
    scalar = direction.isNegative() ? -scalar : scalar;
    return minus(scalar, direction.getAxis());
  }

  public Coordinate3I minus(int scalar, Axis3 axis) {
    return plus(-scalar, axis);
  }

  /**
   * Simple scalar multiplication.
   *
   * @param scalar to multiply this with
   * @return a copy of this coordinate that is multiplied with the scalar
   */
  public Coordinate3I mult(int scalar) {
    int x = this.x * scalar;
    int y = this.y * scalar;
    int z = this.z * scalar;
    return new Coordinate3I(x, y, z);
  }

  public String toAbsoluteString() {
    return x + " " + y + " " + z;
  }

  public String toRelativeString() {
    return "~" + toStringNoZero(x) + " ~" + toStringNoZero(y) + " ~" + toStringNoZero(z);
  }

  private String toStringNoZero(int d) {
    if (d == 0) {
      return "";
    }
    return String.valueOf(d);
  }

  public Collection<Coordinate3I> getAdjacent() {
    return Collections2.transform(DIRECTIONS, this::plus);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    Coordinate3I other = (Coordinate3I) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Coordinate3D [x=" + x + ", y=" + y + ", z=" + z + "]";
  }
}
