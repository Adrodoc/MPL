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
package de.kussm.chain;

import static de.kussm.chain.ChainLinkType.CONDITIONAL;
import static de.kussm.chain.ChainLinkType.NORMAL;
import static de.kussm.chain.ChainLinkType.RECEIVER;
import static de.kussm.chain.ChainLinkType.TRANSMITTER;
import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;

import de.adrodoc55.minecraft.mpl.placement.NotEnoughSpaceException;
import de.kussm.direction.Directions;

/**
 * @author Michael Kuß
 */
public class Scribble {
  private static final Chain EXAMPLE_CHAIN1 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      NORMAL, TRANSMITTER, RECEIVER, NORMAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL,
      NORMAL, CONDITIONAL, TRANSMITTER, RECEIVER, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL,
      CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL);

  private static final Chain EXAMPLE_CHAIN2 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      NORMAL, TRANSMITTER, RECEIVER, NORMAL, NORMAL, NORMAL, CONDITIONAL, CONDITIONAL, CONDITIONAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, CONDITIONAL,
      NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL,
      CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, CONDITIONAL, CONDITIONAL, NORMAL, CONDITIONAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL);

  private static final Chain EXAMPLE_CHAIN3 = Chain.of(TRANSMITTER, RECEIVER, NORMAL, NORMAL,
      CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, CONDITIONAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL,
      NORMAL, NORMAL, NORMAL, NORMAL, NORMAL, NORMAL);


  public static void main(String[] args) throws NotEnoughSpaceException {
    // Directions dirs = $(EAST.repeat(16), NORTH, WEST.repeat(16), NORTH).repeat(1000);
    Directions dirs = $(EAST.repeat(4), NORTH, WEST.repeat(4), NORTH).repeat();
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN1, dirs));
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN2, dirs));
    System.out.println(ChainLayouter.place(EXAMPLE_CHAIN3, dirs));
  }
}
