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

import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Directions.$;
import static java.util.Comparator.naturalOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLinkType;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Adrodoc55
 */
public class MplProgramPlacer extends MplChainPlacer {

  private final int[] occupied;
  private Position size;

  public MplProgramPlacer(ChainContainer container, CompilerOptions options) {
    super(container, options);
    int size = container.getChains().size() + 2;
    this.occupied = new int[size];
  }

  @Override
  public List<CommandBlockChain> place() throws NotEnoughSpaceException {
    List<CommandChain> chains = new ArrayList<>(container.getChains());
    // start with the longest chain
    chains.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    while (true) {
      this.chains.clear();
      try {
        for (CommandChain chain : chains) {
          addChain(chain);
        }
        addUnInstallation();
        break;
      } catch (NotEnoughSpaceException ex) {
        increaseSize();
        continue;
      }
    }
    return this.chains;
  }

  private void addChain(CommandChain chain) throws NotEnoughSpaceException {
    Coordinate3D start = findStart(chain);
    LinkedHashMap<Position, ChainLinkType> placed =
        place(chain, start, newTemplate(getSize().getX()));
    String name = chain.getName();
    List<MplBlock> blocks = toBlocks(chain.getCommands(), placed);
    CommandBlockChain materialized = new CommandBlockChain(name, blocks);
    materialized.move(start);
    occupyBlocks(materialized);
    this.chains.add(materialized);
  }

  private void increaseSize() throws NotEnoughSpaceException {
    Coordinate3D max = container.getMax();
    Orientation3D o = getOrientation();
    Direction3D a = o.getA();
    Direction3D b = o.getB();
    int maxA = max.get(a.getAxis());
    int maxB = max.get(b.getAxis());
    int x = getSize().getX();
    int y = getSize().getY();
    if (x < maxA || maxA < 0) {
      size = Position.at(x + 1, y);
    } else if (y < maxB || maxB < 0) {
      size = Position.at(x, y + 1);
    } else {
      throw new NotEnoughSpaceException();
    }
  }

  /**
   * Returns the current size to place the program. This is an exclusive, zero based
   * {@link Position}, so (1, 1) refers to a 1 block size.
   *
   * @return
   * @throws NotEnoughSpaceException
   */
  protected Position getSize() throws NotEnoughSpaceException {
    if (size == null) {
      size = calculateInitialSize();
    }
    return size;
  }

  private Position calculateInitialSize() throws NotEnoughSpaceException {
    Orientation3D orientation = getOrientation();
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    int maxA = container.getMax().get(a.getAxis());
    int maxB = container.getMax().get(b.getAxis());
    if (maxA >= 0 && maxB >= 0) {
      return Position.at(maxA, maxB);
    }
    if (maxB >= 0) {
      int minA = estimateMinA();
      return Position.at(minA, maxB);
    }
    int minA;
    if (maxA >= 0) {
      minA = maxA;
    } else {
      minA = estimateMinA();
    }

    int minB = 0;
    while (true) {
      int currentA = minA;
      for (CommandChain c : container.getChains()) {
        minB = Math.max(minB, getMinB(c, newTemplate(currentA)));
      }
      if (minA < minB) {
        minA++;
        continue;
      }
      return Position.at(minA, minB);
    }
  }

  /**
   * Estimates the minimal a required to place all chains efficiently.
   *
   * @return
   */
  private int estimateMinA() {
    int installLength = container.getInstall().getCommands().size()
        + container.getUninstall().getCommands().size() + 2;

    int longestProcessLength = container.getChains().stream()//
        .map(chain -> chain.getCommands().size())//
        .max(naturalOrder()).orElse(0);

    int longestChainLength = Math.max(installLength, longestProcessLength);

    int sqrtLength = (int) Math.ceil(Math.sqrt(longestChainLength));
    return Math.max(sqrtLength, getLongestSuccessiveConditionalCount() + 3);
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
    Direction3D cDir = orientation.getC();
    Coordinate3D bPos = orientation.getB().toCoordinate();
    Coordinate3D cPos = cDir.toCoordinate();

    Position size = getSize();
    for (int c = 0; c < occupied.length; c++) {
      int totalB = occupied[c] + b;
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
   * @throws NotEnoughSpaceException if the template is too small
   */
  protected int getMinB(CommandChain chain, Directions template) throws NotEnoughSpaceException {
    Chain chainLinkChain = toChainLinkChain(chain.getCommands());
    LinkedHashMap<Position, ChainLinkType> placed = place(chainLinkChain, template);
    Set<Position> keySet = placed.keySet();
    return getMaxY(keySet) + 1; // +1 because y is zero based, but we want the actual height
  }

  /**
   * Returns the greatest y value of the given {@link Position}s.
   *
   * @param positions
   * @return the greatest y value
   */
  protected int getMaxY(Collection<Position> positions) {
    return positions.stream().map(p -> p.getY()).max(naturalOrder()).orElse(0);
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

  private void addUnInstallation() throws NotEnoughSpaceException {
    // move all chains by 1 block, if installation or uninstallation is added.
    // if there is at least one process, both installation and unistallation will be added.
    if (!container.getChains().isEmpty()//
        || !getInstall().getCommands().isEmpty()//
        || !getUninstall().getCommands().isEmpty()) {
      for (CommandBlockChain chain : chains) {
        chain.move(getOrientation().getC().toCoordinate());
      }
    }
    generateUnInstall();
  }

  /**
   * This method generates both installation uninstallation of the program at (0, 0, 0) and adds
   * them to {@link #chains}. The resulting chains are in the same flat plane.
   *
   * @see #generateFlat(CommandChain, Coordinate3D, Directions)
   */
  protected void generateUnInstall() throws NotEnoughSpaceException {
    int sizeA = getSize().getX();
    // Bei ungerader Größe hat install einen Block weniger, da install effektiv 2 Reihen mehr hat.
    int installA = sizeA / 2;
    int uninstallA = sizeA - installA;

    CommandChain uninstall = getPopulatedUninstall();
    if (!uninstall.getCommands().isEmpty()) {
      Coordinate3D start = getOrientation().getB().toCoordinate();
      Directions template = newTemplate(installA);
      CommandBlockChain materialised = generateFlat(uninstall, start, template);
      chains.add(materialised);
    }

    CommandChain install = getPopulatedInstall();
    if (!install.getCommands().isEmpty()) {
      Coordinate3D start = new Coordinate3D();
      Directions template = $(EAST.repeat(Math.abs(uninstallA)), newTemplate(uninstallA));
      CommandBlockChain materialised = generateFlat(install, start, template);
      chains.add(materialised);
    }
  }

}
