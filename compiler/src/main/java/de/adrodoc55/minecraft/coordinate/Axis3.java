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

/**
 * A three dimensional axis.
 *
 * @author Adrodoc55
 */
public enum Axis3 {
  X {
    @Override
    public Direction3 getDirection(boolean negative) {
      return negative ? Direction3.WEST : Direction3.EAST;
    }

    @Override
    public double of(Coordinate3D c) {
      return c.x;
    }

    @Override
    public int of(Coordinate3I c) {
      return c.x;
    }

    @Override
    Coordinate3D plus(Coordinate3D c, double scalar) {
      double x = c.x + scalar;
      return new Coordinate3D(x, c.y, c.z);
    }

    @Override
    Coordinate3I plus(Coordinate3I c, int scalar) {
      int x = c.x + scalar;
      return new Coordinate3I(x, c.y, c.z);
    }
  },
  Y {
    @Override
    public Direction3 getDirection(boolean negative) {
      return negative ? Direction3.DOWN : Direction3.UP;
    }

    @Override
    public double of(Coordinate3D c) {
      return c.y;
    }

    @Override
    public int of(Coordinate3I c) {
      return c.y;
    }

    @Override
    Coordinate3D plus(Coordinate3D c, double scalar) {
      double y = c.y + scalar;
      return new Coordinate3D(c.x, y, c.z);
    }

    @Override
    Coordinate3I plus(Coordinate3I c, int scalar) {
      int y = c.y + scalar;
      return new Coordinate3I(c.x, y, c.z);
    }
  },
  Z {
    @Override
    public Direction3 getDirection(boolean negative) {
      return negative ? Direction3.NORTH : Direction3.SOUTH;
    }

    @Override
    public double of(Coordinate3D c) {
      return c.z;
    }

    @Override
    public int of(Coordinate3I c) {
      return c.z;
    }

    @Override
    Coordinate3D plus(Coordinate3D c, double scalar) {
      double z = c.z + scalar;
      return new Coordinate3D(c.x, c.y, z);
    }

    @Override
    Coordinate3I plus(Coordinate3I c, int scalar) {
      int z = c.z + scalar;
      return new Coordinate3I(c.x, c.y, z);
    }
  };

  /**
   * Return the appropriate {@link Direction3} along this {@link Axis3}.
   *
   * @param negative whether to return the positive or negative {@link Direction3}
   * @return the appropriate {@link Direction3}
   */
  public abstract Direction3 getDirection(boolean negative);

  /**
   * Return the extend of the specified {@link Coordinate3D} along this {@link Axis3}.
   *
   * @param c the {@link Coordinate3D}
   * @return the extend of the {@link Coordinate3D}
   */
  public abstract double of(Coordinate3D c);

  /**
   * Return the extend of the specified {@link Coordinate3I} along this {@link Axis3}.
   *
   * @param c the {@link Coordinate3I}
   * @return the extend of the {@link Coordinate3I}
   */
  public abstract int of(Coordinate3I c);

  abstract Coordinate3D plus(Coordinate3D c, double scalar);

  abstract Coordinate3I plus(Coordinate3I c, int scalar);
}
