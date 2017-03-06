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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.coordinate.Axis3D.X;
import static de.adrodoc55.minecraft.coordinate.Axis3D.Y;
import static de.adrodoc55.minecraft.coordinate.Axis3D.Z;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.Collection;
import java.util.function.BinaryOperator;

import javax.annotation.CheckReturnValue;

import de.adrodoc55.minecraft.coordinate.Axis3D;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

/**
 * @author Adrodoc55
 */
public class MplUtils {
  protected MplUtils() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated!");
  }

  public static String commandWithoutLeadingSlash(String command) {
    checkNotNull(command, "command == null!");
    if (command.startsWith("/")) {
      return command.substring(1);
    } else {
      return command;
    }
  }

  @CheckReturnValue
  public static String getStartCommandHeader(CompilerOptions options) {
    return options.hasOption(TRANSMITTER) ? "setblock " : "blockdata ";
  }

  @CheckReturnValue
  public static String getStartCommandTrailer(CompilerOptions options) {
    return options.hasOption(TRANSMITTER) ? " redstone_block" : " {auto:1b}";
  }

  /**
   * Returns a command that starts whatever is at the execution coordinates.
   * 
   * @param options
   * @return a command that starts whatever is at the execution coordinates
   */
  @CheckReturnValue
  public static String getStartCommand(CompilerOptions options) {
    return getStartCommandHeader(options) + "~ ~ ~" + getStartCommandTrailer(options);
  }

  @CheckReturnValue
  public static String getStopCommandHeader(CompilerOptions options) {
    return options.hasOption(TRANSMITTER) ? "setblock " : "blockdata ";
  }

  @CheckReturnValue
  public static String getStopCommandTrailer(CompilerOptions options) {
    if (options.hasOption(TRANSMITTER)) {
      if (options.hasOption(DEBUG)) {
        return " air";
      } else {
        return " stone";
      }
    } else {
      return " {auto:0b}";
    }
  }

  /**
   * Returns a command that stops whatever is at the execution coordinates.
   * 
   * @param options
   * @return a command that stops whatever is at the execution coordinates
   */
  @CheckReturnValue
  public static String getStopCommand(CompilerOptions options) {
    return getStopCommandHeader(options) + "~ ~ ~" + getStopCommandTrailer(options);
  }

  /**
   * Converts a double into a String that can be used in Minecraft commands.
   *
   * @param d double to convert
   * @return string representation
   */
  public static String toString(double d) {
    if (d == (long) d) {
      return String.format("%d", (long) d);
    } else {
      return String.format("%s", d);
    }
  }

  private static double get(Collection<Coordinate3D> coordinates, Axis3D axis,
      BinaryOperator<Double> accumulator) {
    return coordinates.stream().map(c -> c.get(axis)).reduce(accumulator).orElse(0D);
  }

  private static double getBoundary(Collection<Coordinate3D> coordinates, Orientation3D orientation,
      Axis3D axis) {
    boolean negative = orientation.get(axis).isNegative();
    BinaryOperator<Double> accumulator = negative ? Math::min : Math::max;
    return get(coordinates, axis, accumulator);
  }

  /**
   * Return the maximal {@link Coordinate3D} regarding the {@link Orientation3D}
   *
   * @param orientation
   * @param coordinates
   * @return the maximal coordinate
   */
  public static Coordinate3D getBoundaries(Orientation3D orientation,
      Collection<Coordinate3D> coordinates) {
    double x = getBoundary(coordinates, orientation, X);
    double y = getBoundary(coordinates, orientation, Y);
    double z = getBoundary(coordinates, orientation, Z);
    return new Coordinate3D(x, y, z);
  }

  private static Coordinate3D getCoordinate(Collection<Coordinate3D> coordinates,
      BinaryOperator<Double> accumulator) {
    double x = get(coordinates, X, accumulator);
    double y = get(coordinates, Y, accumulator);
    double z = get(coordinates, Z, accumulator);
    return new Coordinate3D(x, y, z);
  }

  public static Coordinate3D getMinCoordinate(Collection<Coordinate3D> coordinates) {
    return getCoordinate(coordinates, Math::min);
  }

  public static Coordinate3D getMaxCoordinate(Collection<Coordinate3D> coordinates) {
    return getCoordinate(coordinates, Math::max);
  }

}
