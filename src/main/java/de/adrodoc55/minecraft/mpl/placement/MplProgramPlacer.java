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
package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.ChainLayouter;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLinkType;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Adrodoc55
 */
public class MplProgramPlacer extends MplChainPlacer {

  private final int[] occupied;

  protected MplProgramPlacer(ChainContainer container, CompilerOptions options) {
    super(container, options);
    int size = container.getChains().size() + 2;
    this.occupied = new int[size];
  }

  @Override
  public List<CommandBlockChain> place() {
    List<CommandChain> chains = new ArrayList<>(container.getChains());
    // start with the longest chain
    chains.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    while (true) {
      try {
        for (CommandChain chain : chains) {
          Coordinate3D start = findStart(chain);
          LinkedHashMap<Position, ChainLinkType> placed =
              place(chain, start, newTemplate(getSize().getX()));
          String name = chain.getName();
          List<MplBlock> blocks = toBlocks(chain.getCommands(), placed);
          CommandBlockChain materialized = new CommandBlockChain(name, blocks);
          materialized.move(start);
          occupyBlocks(materialized);
        }
        break;
      } catch (NotEnoughSpaceException e) {
        increaseSize();
        continue;
      }
    }

    return this.chains;
  }

  protected Position getSize() {
    Orientation3D orientation = getOrientation();
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    int maxA = container.getMax().get(a.getAxis());
    int maxB = container.getMax().get(b.getAxis());
    if (maxA >= 0 && maxB >= 0) {
      return Position.at(maxA, maxB);
    }
    if (maxB >= 0) {


      return null;
    }
    int optA;
    if (maxA >= 0) {
      optA = maxA;
    } else {
      int installLength = container.getInstall().getCommands().size()
          + container.getUninstall().getCommands().size() + 2;
      int longestProcessLength = container.getChains().stream()
          .map(chain -> chain.getCommands().size()).max(Comparator.naturalOrder()).orElse(0);
      int longestChainLength = Math.max(installLength, longestProcessLength);

      int length = (int) Math.ceil(Math.sqrt(longestChainLength));
      optA = Math.max(length, getLongestSuccessiveConditionalCount() + 3);
    }

    int optB = 0;
    while (true) {
      for (CommandChain chain : container.getChains()) {
        optB = Math.max(optB, getMinB(chain, newTemplate(optA)));
      }
      if (optA < optB) {
        optA = optB;
        continue;
      }
      break;
    }
    return Position.at(optA, optB);

  }

  /**
   * Finds the next starting coordinate for this chain. This method takes all chains that have been
   * added to {@link #chains} into account. Therefor the starting coordinate is guaranteed to allow
   * a valid placement of the chain without leaving the boundaries of {@link #getSize()}.
   *
   * @param chain
   * @return
   * @throws NotEnoughSpaceException
   */
  protected Coordinate3D findStart(CommandChain chain) throws NotEnoughSpaceException {
    Position size = getSize();
    int minB = getMinB(chain, newTemplate(size.getX()));
    while (true) {
      Coordinate3D start = findStart(minB);
      LinkedHashMap<Position, ChainLinkType> placed = place(chain, start, newTemplate(size.getX()));
      int startB = start.get(getOrientation().getB());
      int actualB = getMaxY(placed.keySet());
      minB = startB + actualB;
      if (minB <= size.getY()) {
        return start;
      }
    }
  }

  /**
   * Finds the next free starting position for a chain with the given height.
   *
   * @param b
   * @return
   * @throws NotEnoughSpaceException
   */
  protected Coordinate3D findStart(int b) throws NotEnoughSpaceException {
    Orientation3D orientation = getOrientation();
    Coordinate3D bPos = orientation.getB().toCoordinate();
    Coordinate3D cPos = orientation.getC().toCoordinate();

    Position size = getSize();
    for (int c = 0; c < occupied.length; c++) {
      int totalB = occupied[c] + Math.abs(b);
      if (totalB <= size.getY()) {
        return cPos.mult(c).plus(bPos.mult(occupied[c]));
      }
    }
    throw new NotEnoughSpaceException();
  }

  /**
   * Returns the b needed to place this chain with this template disregarding forbidden
   * transmitter/receiver positions.
   *
   * @param chain
   * @param template
   * @return
   */
  protected int getMinB(CommandChain chain, Directions template) {
    Chain chainLinkChain = toChainLinkChain(chain.getCommands());
    LinkedHashMap<Position, ChainLinkType> placed = place(chainLinkChain, template);
    Set<Position> keySet = placed.keySet();
    return getMaxY(keySet);
  }

  /**
   * Places the chain according to the template without regarding forbidden transmitter/receiver
   * positions.
   *
   * @param chain
   * @param template
   * @return
   */
  protected LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions template) {
    if (options.hasOption(TRANSMITTER)) {
      // receivers are not allowed at x=0 because the start transmitters of all chains are at x=0
      Predicate<Position> isReceiverAllowed = pos -> pos.getX() != 0;

      // transmitters are not allowed at x=1 because the start receivers of all chains are at x=1
      Predicate<Position> isTransmitterAllowed = pos -> pos.getX() != 1;

      return ChainLayouter.place(chain, template, isReceiverAllowed, isTransmitterAllowed);
    } else {
      return ChainLayouter.place(chain, template);
    }
  }

  /**
   * Returns the greatest y value of the given {@link Position}s.
   *
   * @param positions
   * @return the greatest y value
   */
  protected int getMaxY(Iterable<Position> positions) {
    int y = 0;
    for (Position position : positions) {
      y = Math.max(y, position.getY());
    }
    return y;
  }

  protected void occupyBlocks(CommandBlockChain materialized) {
    Orientation3D orientation = getOrientation();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    Coordinate3D max = materialized.getBoundaries(orientation);
    int maxB = max.get(b.getAxis());
    int maxC = max.get(c.getAxis());
    occupied[Math.abs(maxC)] = Math.abs(maxB) + 1;
  }

  protected static Directions newTemplate(int a) {
    return $(EAST.repeat(a - 1), NORTH, WEST.repeat(a - 1), NORTH).repeat();
  }

}
