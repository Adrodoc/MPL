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
package de.adrodoc55.minecraft.mpl.placement;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL;
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
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
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
        addUnInstall();
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
    CommandBlockChain materialized = generateFlat(chain, start, newTemplate(getSize().getX()));
    occupyBlocks(materialized);
    chains.add(materialized);
  }

  private void increaseSize() throws NotEnoughSpaceException {
    Coordinate3D max = container.getMax();
    Orientation3D o = getOrientation();
    Direction3D a = o.getA();
    Direction3D b = o.getB();
    int maxA = (int) max.get(a.getAxis());
    int maxB = (int) max.get(b.getAxis());
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
   * @return the current size to place the program
   * @throws NotEnoughSpaceException if the maximum coordinate is to small to place the entire
   *         program
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
    int maxA = (int) container.getMax().get(a.getAxis());
    int maxB = (int) container.getMax().get(b.getAxis());
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
      minB = Math.max(maxB, getMinUnInstallB());
      if (minA < minB) {
        minA++;
        continue;
      }
      return Position.at(minA, minB);
    }
  }

  private int getMinUnInstallB() {
    return (int) Math.ceil(Math.sqrt(getUnInstallLength()));
  }

  /**
   * Estimates the minimal length in the a direction required to place all chains efficiently.
   *
   * @return the minimal length in the a direction
   */
  private int estimateMinA() {
    int unInstallLength = getUnInstallLength();

    int longestProcessLength = container.getChains().stream()//
        .map(chain -> chain.getCommands().size())//
        .max(naturalOrder()).orElse(0);

    int longestChainLength = Math.max(unInstallLength, longestProcessLength);

    int sqrtLength = (int) Math.ceil(Math.sqrt(longestChainLength));
    return Math.max(sqrtLength, getLongestSuccessiveConditionalCount() + 3);
  }

  private int getUnInstallLength() {
    int installLength = 3 + container.getChains().size()//
        + container.getInstall().getCommands().size()//
        + container.getUninstall().getCommands().size();
    return installLength;
  }

  /**
   * Finds the next free starting coordinate for this chain. This method takes all chains that have
   * been added to {@link #chains} into account. Therefore the starting coordinate is guaranteed to
   * allow a valid placement of the chain without leaving the boundaries of {@link #getSize()}.
   *
   * @param chain the {@link CommandChain} to search a start for
   * @return a free coordinate to start the chain placement at
   * @throws NotEnoughSpaceException if the current size is to small to place the chain
   */
  protected Coordinate3D findStart(CommandChain chain) throws NotEnoughSpaceException {
    Position size = getSize();
    int minB = getMinB(chain, newTemplate(size.getX()));
    while (true) {
      Coordinate3D start = findStart(minB);
      LinkedHashMap<Position, ChainLinkType> placed = place(chain, start, newTemplate(size.getX()));
      int startB = (int) start.get(getOrientation().getB());
      int actualB = getMaxY(placed.keySet());
      minB = startB + actualB;
      if (minB <= size.getY()) {
        return start;
      }
    }
  }

  /**
   * Finds the next free starting coordinate for a chain with the given height in the b direction.
   *
   * @param b the height of the chain
   * @return a free coordinate to start the chain placement at
   * @throws NotEnoughSpaceException if the current size is to small to place the chain
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
   * Returns the minimal height in the b direction needed to place the chain along the template
   * disregarding forbidden transmitter and receiver positions.
   *
   * @param chain the {@link CommandChain} to place
   * @param template along which the chain will be placed
   * @return the minimal height in the b direction
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
   * @param positions {@link Collection} of {@link Position}s
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
    int maxB = (int) max.get(b.getAxis());
    int maxC = (int) max.get(c.getAxis());
    occupied[Math.abs(maxC)] = Math.abs(maxB) + 1;
  }

  private void addUnInstall() throws NotEnoughSpaceException {
    // move all chains by 1 block, if install or uninstall is added.
    // if there is at least one process, both install and uninstall will be added.

    // TODO: This line is incorrect, as a script has 1 chain.
    // Preferred solution: build install in visitor and reference other processes via ${other}
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
   * This method generates both install uninstall of the program at (0, 0, 0) and adds them to
   * {@link #chains}. The resulting chains are in the same flat plane.
   *
   * @throws NotEnoughSpaceException if the current size is to small to generate install or
   *         uninstall
   * @see #generateFlat(CommandChain, Coordinate3D, Directions)
   */
  protected void generateUnInstall() throws NotEnoughSpaceException {
    CommandChain uninstall = getPopulatedUninstall();
    Command deleteOnUninstall = null;
    if (options.hasOption(DELETE_ON_UNINSTALL)) {
      deleteOnUninstall = new Command();
      uninstall.addCommand(deleteOnUninstall);
    }

    if (!uninstall.getCommands().isEmpty()) {
      Coordinate3D start = getOrientation().getB().toCoordinate();
      Directions template = newUninstallTemplate();
      CommandBlockChain generated = generateFlat(uninstall, start, template);
      chains.add(generated);
    }

    CommandChain install = getPopulatedInstall();
    if (!install.getCommands().isEmpty()) {
      Coordinate3D start = new Coordinate3D();
      Directions template = newInstallTemplate();
      CommandBlockChain generated = generateFlat(install, start, template);
      chains.add(generated);
    }

    if (deleteOnUninstall != null) {
      deleteOnUninstall.setCommand(getDeleteCommand());
    }
  }

  private Directions newInstallTemplate() throws NotEnoughSpaceException {
    return $(EAST.repeat(Math.abs(getUninstallA())), newTemplate(getInstallA()));
  }

  private Directions newUninstallTemplate() throws NotEnoughSpaceException {
    return newTemplate(getUninstallA());
  }

  private int getInstallA() throws NotEnoughSpaceException {
    // Bei ungerader Größe hat install einen Block weniger, da install effektiv 2 Reihen mehr hat.
    return getSize().getX() / 2;
  }

  private int getUninstallA() throws NotEnoughSpaceException {
    return getSize().getX() - getInstallA();
  }

}
