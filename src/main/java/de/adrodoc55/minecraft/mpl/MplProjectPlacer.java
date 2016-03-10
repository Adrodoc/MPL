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
package de.adrodoc55.minecraft.mpl;

import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.adrodoc55.minecraft.coordinate.Axis3D;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.antlr.MplProcess;
import de.adrodoc55.minecraft.mpl.antlr.MplProject;
import de.adrodoc55.minecraft.mpl.antlr.commands.InternalCommand;
import de.adrodoc55.minecraft.mpl.chain_computing.IterativeChainComputer;
import de.kussm.ChainLayouter;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLink;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

public class MplProjectPlacer extends MplChainPlacer {

  private final LinkedList<MplProcess> processes;
  private final int[] occupied;

  public MplProjectPlacer(MplProject project) {
    super(project);
    processes = new LinkedList<>(project.getProcesses());
    occupied = new int[processes.size()];

    // The first block of each chain that start's with a RECIEVER must be a TRANSMITTER
    for (MplProcess process : processes) {
      List<ChainPart> commands = process.getCommands();
      if (commands.isEmpty() || !isReciever(commands.get(0))) {
        continue;
      }
      commands.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }
  }

  public List<CommandBlockChain> place() {
    // start with the longest chain
    processes.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    for (MplProcess process : processes) {
      addChain(process);
    }
    addUnInstallation();
    return result;
  }

  private void addChain(MplProcess process) {
    Coordinate3D start = estimateStart(process);

    Set<Position> forbiddenTransmitter = new HashSet<>();
    Set<Position> forbiddenReciever = new HashSet<>();
    fillForbiddenPositions(start, forbiddenTransmitter, forbiddenReciever);

    CommandBlockChain optimal = compute(process, forbiddenReciever, forbiddenTransmitter);
    optimal.move(start);
    occupyBlocks(optimal);
    result.add(optimal);
  }

  private Coordinate3D estimateStart(MplProcess process) {
    Orientation3D orientation = getOrientation();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    Coordinate3D bPos = b.toCoordinate();
    Coordinate3D cPos = c.toCoordinate();

    Coordinate3D opt = getOptimalSize();
    int optB = opt.get(b.getAxis());

    // int localMaxB = optimal.getMax().get(b.getAxis()) + 1;
    int estimatedB = estimateB(process);
    for (int x = 0; x < occupied.length; x++) {
      int estimatedMax = occupied[x] + estimatedB;
      if (estimatedMax <= optB || occupied[x] == 0) {
        Coordinate3D start = cPos.mult(x).plus(bPos.mult(occupied[x]));
        return start;
      }
    }
    throw new IllegalStateException("could not find a start for chain " + process.getName());
  }

  private int estimateB(CommandChain chain) {
    Direction3D a = getOrientation().getA();
    Coordinate3D opt = getOptimalSize();
    int estimate = chain.getCommands().size() / opt.get(a.getAxis());
    return estimate + 1;
  }

  private void fillForbiddenPositions(Coordinate3D start, Set<Position> forbiddenTransmitter,
      Set<Position> forbiddenReciever) {
    Orientation3D orientation = getOrientation();
    Axis3D cAxis = orientation.getC().getAxis();
    int startC = start.get(cAxis);
    for (CommandBlockChain materialized : result) {
      for (MplBlock block : materialized.getBlocks()) {
        Coordinate3D currentCoord = block.getCoordinate();
        int currentC = currentCoord.get(cAxis);

        Position pos;
        if (startC - 1 == currentC || currentC == startC + 1) {
          pos = toPosition(currentCoord.minus(start), orientation);
        } else if (startC == currentC) {
          // Ketten in der selben c Ebene sind (in b richtung) unter der zu bauenden kette. Daher
          // muss einmal b aufaddiert werden.
          Coordinate3D b = orientation.getB().toCoordinate();
          pos = toPosition(currentCoord.minus(start).plus(b), orientation);
        } else {
          continue;
        }
        // Only look at positive positions
        if (pos.getX() >= 0 && pos.getY() >= 0) {
          if (isTransmitter(block)) {
            forbiddenReciever.add(pos);
          } else if (isReciever(block)) {
            forbiddenTransmitter.add(pos);
          }
        }
      }
    }
  }

  private CommandBlockChain compute(MplProcess chain, Set<Position> forbiddenReceivers,
      Set<Position> forbiddenTransmitters) {
    List<ChainPart> commands = chain.getCommands();
    Chain linkChain = toChainLinkChain(commands);

    LinkedHashMap<Position, ChainLink> placed =
        place(linkChain, forbiddenReceivers, forbiddenTransmitters);

    List<MplBlock> blocks = toBlocks(commands, placed);
    return new CommandBlockChain(chain.getName(), blocks);
  }

  private LinkedHashMap<Position, ChainLink> place(Chain linkChain,
      Set<Position> forbiddenReceivers, Set<Position> forbiddenTransmitters) {
    // recievers are not allowed at x=0 because the start transmitters of all chains are at x=0
    Predicate<Position> isRecieverAllowed =
        pos -> !forbiddenReceivers.contains(pos) && pos.getX() != 0;

    // transmitters are not allowed at x=1 because the start recievers of all chains are at x=1
    Predicate<Position> isTransmitterAllowed =
        pos -> !forbiddenTransmitters.contains(pos) && pos.getX() != 1;

    LinkedHashMap<Position, ChainLink> placed = ChainLayouter.place(linkChain,
        newDirectionsTemplate(), isRecieverAllowed, isTransmitterAllowed);
    return placed;
  }

  private Directions newDirectionsTemplate() {
    int optA = getOptimalSize().get(getOrientation().getA().getAxis());
    Directions dirs = $(EAST.repeat(optA), NORTH, WEST.repeat(optA), NORTH).repeat();
    return dirs;
  }

  private Coordinate3D optimalSize;

  /**
   * Calculates the optimal boundaries for this Program. The optimal boundaries must be smaller than
   * {@code program.getMax()}, but should leave enough space for conditional chains.
   *
   * @param program
   * @return opt the optimal boundaries
   */
  public Coordinate3D getOptimalSize() {
    if (optimalSize == null) {
      optimalSize = calculateOptimalSize();
    }
    return optimalSize;
  }

  /**
   * Calculates the optimal boundaries for this Program. The optimal boundaries must be smaller than
   * {@code program.getMax()}, but should leave enough space for conditional chains.
   *
   * @param program
   * @return opt the optimal boundaries
   */
  private Coordinate3D calculateOptimalSize() {
    Orientation3D orientation = getOrientation();
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    Direction3D c = orientation.getC();
    int maxA = program.getMax().get(a.getAxis());
    int maxB = program.getMax().get(b.getAxis());
    int maxC = program.getMax().get(c.getAxis());

    int installLength = getInstallation().size();
    int uninstallLength = getUninstallation().size();
    int longestProcessLength =
        processes.stream().map(p -> p.commands.size()).max(Comparator.naturalOrder()).orElse(0);
    int longestChainLength =
        Math.max(Math.max(installLength, uninstallLength), longestProcessLength);

    int optB = 1 + (int) Math.sqrt(longestChainLength);
    int optA = Math.max(optB, getLongestSuccessiveConditionalCount() + 2);
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

  public final int getLongestSuccessiveConditionalCount() {
    return Stream
        .concat(processes.stream().map(p -> p.commands),
            Stream.of(getInstallation(), getUninstallation()))
        .map(chainParts -> getLongestSuccessiveConditionalCount(chainParts))
        .max(Comparator.naturalOrder()).orElse(0);
  }

  private List<MplBlock> toBlocks(List<ChainPart> commands,
      LinkedHashMap<Position, ChainLink> placed) {
    LinkedList<ChainPart> chainParts = new LinkedList<>(commands);

    Orientation3D orientation = getOrientation();

    LinkedList<Entry<Position, ChainLink>> entries =
        placed.entrySet().stream().collect(Collectors.toCollection(LinkedList::new));

    List<MplBlock> blocks = new LinkedList<>();
    while (entries.size() > 1) {
      Entry<Position, ChainLink> entry = entries.pop();

      Position pos = entry.getKey();
      Position nextPos = entries.peek().getKey();

      Direction3D d = getDirection(pos, nextPos, orientation);
      Coordinate3D coord = toCoordinate(pos, orientation);

      if (entry.getValue() == ChainLink.NO_OPERATION) {
        blocks.add(new CommandBlock(new InternalCommand(), d, coord));
      } else {
        ChainPart chainPart = chainParts.pop();
        if (chainPart instanceof Command) {
          blocks.add(new CommandBlock((Command) chainPart, d, coord));
        } else if (chainPart instanceof Skip) {
          blocks.add(new Transmitter(((Skip) chainPart).isInternal(), coord));
        }
      }
    }

    // last block is always air
    Position lastPos = entries.pop().getKey();
    blocks.add(new AirBlock(toCoordinate(lastPos, orientation)));
    return blocks;
  }

  private void occupyBlocks(CommandBlockChain materialized) {
    Direction3D b = getOrientation().getB();
    Direction3D c = getOrientation().getC();
    Coordinate3D max = materialized.getMax();
    occupied[max.get(c.getAxis())] = max.get(b.getAxis()) + 1;
  }

  private void addUnInstallation() {
    List<ChainPart> installation = program.getInstallation();
    List<ChainPart> uninstallation = program.getUninstallation();

    if (!processes.isEmpty() || !installation.isEmpty() || !uninstallation.isEmpty()) {
      // move all chains by 1 block, if installation or uninstallation is added.
      // if there is at least one process, both installation and unistallation will be added.
      for (CommandBlockChain chain : result) {
        chain.move(getOrientation().getC().toCoordinate());
      }
    }

    for (CommandBlockChain chain : result) {
      Coordinate3D chainStart = chain.getBlocks().get(0).getCoordinate();
      // TODO: Alle ArmorStands taggen, damit nur ein uninstallation command notwendig
      installation
          .add(0,
              new Command("/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString()
                  + ")} {CustomName:" + chain.getName()
                  + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
      uninstallation.add(new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_NOTIFY]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + name + "_INTERCEPTED]"));
    }

    if (!installation.isEmpty()) {
      installation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      installation.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }
    if (!uninstallation.isEmpty()) {
      uninstallation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      uninstallation.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }

    CommandBlockChain materialisedUninstallation = new IterativeChainComputer().computeOptimalChain(
        new NamedCommandChain("uninstall", uninstallation), new Coordinate3D(100, 100, 0));
    materialisedUninstallation.move(getOrientation().getB().toCoordinate());
    CommandBlockChain materialisedInstallation = new IterativeChainComputer() {
      protected boolean isCoordinateValid(Coordinate3D coordinate) {
        if (!super.isCoordinateValid(coordinate)) {
          return false;
        }
        List<MplBlock> commandBlocks = materialisedUninstallation.getBlocks();
        for (MplBlock block : commandBlocks) {
          if (block.getCoordinate().equals(coordinate)) {
            return false;
          }
        }
        return true;
      };
    }.computeOptimalChain(new NamedCommandChain("install", installation),
        new Coordinate3D(100, 100, 0));
    if (!materialisedInstallation.getBlocks().isEmpty()) {
      result.add(materialisedInstallation);
    }
    if (!materialisedUninstallation.getBlocks().isEmpty()) {
      result.add(materialisedUninstallation);
    }
  }

}
