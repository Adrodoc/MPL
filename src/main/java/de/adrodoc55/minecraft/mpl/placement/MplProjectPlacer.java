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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.adrodoc55.minecraft.coordinate.Axis3D;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.direction.Directions;

/**
 * @author Adrodoc55
 */
public class MplProjectPlacer extends MplChainPlacer {

  private final int[] occupied;

  public MplProjectPlacer(ChainContainer container, CompilerOptions options) {
    super(container, options);
    int size = container.getChains().size() + 2;
    this.occupied = new int[size];
  }

  public List<CommandBlockChain> place() {
    List<CommandChain> chains = new ArrayList<>(container.getChains());
    // start with the longest chain
    chains.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    for (CommandChain chain : chains) {
      addChain(chain);
    }
    addUnInstallation();
    return this.chains;
  }

  private void addChain(CommandChain chain) {
    Coordinate3D start = findStart(chain);
    Directions template = newDirectionsTemplate(getOptimalSize(), getOrientation());
    CommandBlockChain materialized = generateFlat(chain, start, template);
    occupyBlocks(materialized);
    chains.add(materialized);
  }

  private Coordinate3D findStart(CommandChain chain) {
    Orientation3D orientation = getOrientation();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    Coordinate3D bPos = b.toCoordinate();
    Coordinate3D cPos = c.toCoordinate();

    Coordinate3D opt = getOptimalSize();
    int optB = opt.get(b.getAxis());

    int estimatedB = estimateB(chain);
    for (int x = 0; x < occupied.length; x++) {
      int estimatedMax = occupied[x] + Math.abs(estimatedB);
      if (estimatedMax <= Math.abs(optB) || occupied[x] == 0) {
        Coordinate3D start = cPos.mult(x).plus(bPos.mult(occupied[x]));
        return start;
      }
    }
    throw new IllegalStateException("could not find a start for chain " + chain.getName());
  }

  /**
   * Estimate the required size of the chain in the b-Direction. A result of 2 relates to a 2 block
   * height for the default orientation. A result of -2 relates to a 2 block height for an
   * orientation with negative b.
   *
   * @param chain
   * @return
   */
  private int estimateB(CommandChain chain) {
    int chainSize = chain.getCommands().size();
    Coordinate3D opt = getOptimalSize();
    Axis3D aAxis = getOrientation().getA().getAxis();
    int optA = opt.get(aAxis);
    int estimate = (int) Math.ceil((double) chainSize / (double) Math.abs(optA));

    Axis3D bAxis = getOrientation().getB().getAxis();
    if (opt.get(bAxis) < 0) {
      estimate *= -1;
    }

    return estimate;
  }

  private Coordinate3D optimalSize;

  @Override
  public Coordinate3D getOptimalSize() {
    if (optimalSize == null) {
      optimalSize = calculateOptimalSize();
    }
    return optimalSize;
  }

  private Coordinate3D calculateOptimalSize() {
    Orientation3D orientation = getOrientation();
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    int maxA = container.getMax().get(a.getAxis());
    int maxB = container.getMax().get(b.getAxis());
    int maxC = container.getMax().get(c.getAxis());

    int installLength = calculateFutureInstallSize() + calculateFutureUninstallSize();
    int longestProcessLength = container.getChains().stream()
        .map(chain -> chain.getCommands().size()).max(Comparator.naturalOrder()).orElse(0);
    int longestChainLength = Math.max(installLength, longestProcessLength);

    int optB = (int) Math.ceil(Math.sqrt(longestChainLength));
    int optA = Math.max(optB, getLongestSuccessiveConditionalCount() + 3);
    int resultA = Math.min(maxA, optA);
    int resultB = Math.min(maxB, optB);
    // @formatter:off
    Coordinate3D opt = new Coordinate3D()
        .plus(a.toCoordinate().mult(resultA))
        .plus(b.toCoordinate().mult(resultB))
        .plus(c.toCoordinate().mult(maxC));
    // @formatter:on
    return opt;
  }

  private int calculateFutureInstallSize() {
    // Plus 1 for final air block
    return 1 + getInstall().getCommands().size() + container.getChains().size();
  }

  private int calculateFutureUninstallSize() {
    // Plus 1 for final air block
    return 1 + getUninstall().getCommands().size() + container.getChains().size();
  }

  private void occupyBlocks(CommandBlockChain materialized) {
    Orientation3D orientation = getOrientation();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    Coordinate3D max = materialized.getBoundaries(orientation);
    int maxB = max.get(b.getAxis());
    int maxC = max.get(c.getAxis());
    occupied[Math.abs(maxC)] = Math.abs(maxB) + 1;
  }

  private void addUnInstallation() {
    List<ChainLink> install = getInstall().getCommands();
    List<ChainLink> uninstall = getUninstall().getCommands();

    Orientation3D orientation = getOrientation();
    if (!container.getChains().isEmpty() || !install.isEmpty() || !uninstall.isEmpty()) {
      // move all chains by 1 block, if installation or uninstallation is added.
      // if there is at least one process, both installation and unistallation will be added.
      for (CommandBlockChain chain : chains) {
        chain.move(orientation.getC().toCoordinate());
      }
    }

    for (CommandBlockChain chain : chains) {
      if (chain.getName() == null) {
        continue;
      }
      Coordinate3D chainStart = chain.getBlocks().get(0).getCoordinate();
      // TODO: Alle ArmorStands taggen, damit nur ein uninstallation command notwendig
      int index = options.hasOption(TRANSMITTER) ? 2 : 1;
      install
          .add(index,
              new Command("/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString()
                  + ")} {CustomName:" + chain.getName()
                  + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
      uninstall.add(new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_NOTIFY]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_INTERCEPTED]"));
    }
    generateUnInstallation();
  }

}
