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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.antlr.commands.InternalCommand;
import de.adrodoc55.minecraft.mpl.chain_computing.IterativeChainComputer;
import de.kussm.ChainLayouter;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLink;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

public class MplChainPlacer {

  public static List<CommandBlockChain> place(Program program) {
    return new MplChainPlacer(program).place();
  }

  private final Program program;
  private final MplOrientation orientation;
  private final LinkedList<CommandChain> chains;
  private final int[] occupied;
  private final List<CommandBlockChain> result = new LinkedList<CommandBlockChain>();
  private Coordinate3D optimalSize;

  private MplChainPlacer(Program program) {
    this.program = program;
    this.orientation = program.getOrientation();
    this.chains = program.getChains();
    for (CommandChain chain : chains) {
      // The first block of each chain that start's with a RECIEVER must be a TRANSMITTER
      List<Command> commands = chain.getCommands();
      if (commands.isEmpty() || !isReciever(commands.get(0))) {
        continue;
      }
      commands.add(0, null);
    }
    occupied = new int[chains.size()];
  }

  public List<CommandBlockChain> place() {
    // start with the longest chain
    chains.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    for (CommandChain chain : chains) {
      addChain(chain);
    }
    addUnInstallation();
    return result;
  }

  private void addChain(CommandChain chain) {
    Coordinate3D bPos = orientation.getB().toCoordinate();
    Direction c = orientation.getC();

    Coordinate3D start = getStart(chain);
    int startC = start.get(c.getAxis());

    Set<Position> forbiddenTransmitter = new HashSet<>();
    Set<Position> forbiddenReciever = new HashSet<>();
    for (CommandBlockChain materialized : result) {
      List<CommandBlock> blocks = materialized.getCommandBlocks();
      for (CommandBlock block : blocks) {
        Coordinate3D currentCoord = block.getCoordinate();
        int currentC = currentCoord.get(c.getAxis());

        Position pos = null;
        if (startC - 1 == currentC || currentC == startC + 1) {
          pos = toPosition(currentCoord.minus(start), orientation);
        } else if (startC == currentC) {
          // Ketten in der selben c Ebene sind (in b richtung) unter der zu bauenden kette. Daher
          // muss einmal b aufaddiert werden.
          pos = toPosition(currentCoord.minus(start).plus(bPos), orientation);
        }
        if (pos == null) {
          continue;
        }
        if (pos.getX() >= 0 && pos.getY() >= 0) {
          if (isTransmitter(block)) {
            forbiddenReciever.add(pos);
          } else if (isReciever(block)) {
            forbiddenTransmitter.add(pos);
          }
        }

      }
    }
    CommandBlockChain optimal = compute(chain, forbiddenReciever, forbiddenTransmitter);
    optimal.move(start);
    occupyBlocks(optimal);
    result.add(optimal);
  }

  private void occupyBlocks(CommandBlockChain optimal) {
    Direction b = orientation.getB();
    Direction c = orientation.getC();

    Coordinate3D max = optimal.getMax();
    occupied[max.get(c.getAxis())] = max.get(b.getAxis()) + 1;
  }

  private boolean isTransmitter(CommandBlock block) {
    return block.toCommand() == null && !(block instanceof AirBlock);
  }

  private boolean isReciever(CommandBlock block) {
    return isReciever(block.toCommand());
  }

  private boolean isReciever(Command command) {
    return command != null && command.getMode() != Mode.CHAIN;
  }

  private Coordinate3D getStart(CommandChain chain) {
    Direction b = orientation.getB();
    Direction c = orientation.getC();
    Coordinate3D bPos = b.toCoordinate();
    Coordinate3D cPos = c.toCoordinate();

    Coordinate3D opt = getOptimalSize();
    int optB = opt.get(b.getAxis());

    // int localMaxB = optimal.getMax().get(b.getAxis()) + 1;
    int estimatedB = estimateB(chain);
    for (int x = 0; x < occupied.length; x++) {
      int estimatedMax = occupied[x] + estimatedB;
      if (estimatedMax <= optB || occupied[x] == 0) {
        Coordinate3D start = cPos.mult(x).plus(bPos.mult(occupied[x]));
        return start;
      }
    }
    throw new IllegalStateException("could not find a start for chain " + chain.getName());
  }

  private int estimateB(CommandChain chain) {
    Direction a = orientation.getA();
    Coordinate3D opt = getOptimalSize();
    int estimate = chain.getCommands().size() / opt.get(a.getAxis());
    return estimate + 1;
  }

  private Coordinate3D getOptimalSize() {
    if (optimalSize == null) {
      optimalSize = calculateOptimalSize();
    }
    return optimalSize;
  }

  /**
   * Calculates the optimal boundaries for the given Program. The optimal boundaries must be smaller
   * than {@code program.getMax()}, but should leave enougth space for conditional chains.
   *
   * @param program
   * @return opt the optimal boundaries
   */
  private Coordinate3D calculateOptimalSize() {
    Direction a = orientation.getA();
    Direction b = orientation.getB();
    Direction c = orientation.getC();

    CommandChain first = chains.peek();
    int maxA = program.getMax().get(a.getAxis());
    int maxB = program.getMax().get(b.getAxis());
    int maxC = program.getMax().get(c.getAxis());
    int optA = 1 + (int) Math.sqrt(first.getCommands().size());
    optA = Math.max(optA, getLongestSuccessiveConditionalCount() + 2);
    int optB = optA;
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

  private int getLongestSuccessiveConditionalCount() {
    int result = 0;
    for (CommandChain chain : chains) {
      List<Command> commands = chain.getCommands();
      int successiveConditionalCount = 0;
      for (Command command : commands) {
        if (command != null && command.isConditional()) {
          successiveConditionalCount++;
        } else {
          result = Math.max(result, successiveConditionalCount);
          successiveConditionalCount = 0;
        }
      }
      result = Math.max(result, successiveConditionalCount);
      successiveConditionalCount = 0;
    }
    return result;
  }

  private Directions newDirs() {
    int optA = getOptimalSize().get(orientation.getA().getAxis());
    Directions dirs = $(EAST.repeat(optA), NORTH, WEST.repeat(optA), NORTH).repeat();
    return dirs;
  }

  private CommandBlockChain compute(CommandChain chain, Set<Position> forbiddenReceivers,
      Set<Position> forbiddenTransmitters) {
    List<CommandBlock> blocks = new LinkedList<CommandBlock>();
    Chain linkChain = toChainLinkChain(chain);
    // recievers are not allowed at x=0 because the start transmitters of all chains are at x=0
    Predicate<Position> isRecieverAllowed =
        pos -> !forbiddenReceivers.contains(pos) && pos.getX() != 0;
    // transmitters are not allowed at x=1 because the start recievers of all chains are at x=0
    Predicate<Position> isTransmitterAllowed =
        pos -> !forbiddenTransmitters.contains(pos) && pos.getX() != 1;
    LinkedHashMap<Position, ChainLink> place =
        ChainLayouter.place(linkChain, newDirs(), isRecieverAllowed, isTransmitterAllowed);
    Iterator<Entry<Position, ChainLink>> it = place.entrySet().iterator();
    if (!it.hasNext()) {
      return new CommandBlockChain(chain.getName(), blocks);
    }
    Entry<Position, ChainLink> nextEntry = it.next();
    for (int x = 0; it.hasNext(); x++) {
      Entry<Position, ChainLink> entry = nextEntry;
      nextEntry = it.next();
      Position pos = entry.getKey();
      Position nextPos = nextEntry.getKey();
      Direction d = getDirection(pos, nextPos, orientation);
      Coordinate3D coord = toCoordinate(pos, orientation);
      if (entry.getValue() == ChainLink.NO_OPERATION) {
        x--;
        blocks.add(new CommandBlock(new InternalCommand(), d, coord));
      } else {
        Command command = chain.getCommands().get(x);
        blocks.add(new CommandBlock(command, d, coord));
      }
    }
    // last block is always air
    Position lastPos = nextEntry.getKey();
    blocks.add(new AirBlock(toCoordinate(lastPos, orientation)));
    return new CommandBlockChain(chain.getName(), blocks);
  }

  /**
   *
   * @param cp current position
   * @param np next position
   * @param orientation
   * @return
   */
  private static Direction getDirection(Position cp, Position np, MplOrientation orientation) {
    // current coordinate
    Coordinate3D cc = toCoordinate(cp, orientation);
    // next coordinate
    Coordinate3D nc = toCoordinate(np, orientation);
    return Direction.valueOf(nc.minus(cc));
  }

  /**
   * x -> a, y -> b
   */
  private static Coordinate3D toCoordinate(Position pos, MplOrientation orientation) {
    Coordinate3D xDir = orientation.getA().toCoordinate();
    Coordinate3D yDir = orientation.getB().toCoordinate();
    Coordinate3D coord = xDir.mult(pos.getX()).plus(yDir.mult(pos.getY()));
    return coord;
  }

  /**
   * a -> x, b -> y
   */
  private static Position toPosition(Coordinate3D coord, MplOrientation orientation) {
    Direction a = orientation.getA();
    Direction b = orientation.getB();
    int x = coord.get(a.getAxis());
    int y = coord.get(b.getAxis());
    return Position.at(x, y);
  }

  private static Chain toChainLinkChain(CommandChain chain) {
    List<Command> commands = chain.getCommands();
    ArrayList<ChainLink> chainLinks = new ArrayList<ChainLink>(commands.size());
    for (Command command : commands) {
      if (command == null) {
        chainLinks.add(ChainLink.TRANSMITTER);
      } else if (command.getMode() != Mode.CHAIN) {
        chainLinks.add(ChainLink.RECEIVER);
      } else if (command.isConditional()) {
        chainLinks.add(ChainLink.CONDITIONAL);
      } else {
        chainLinks.add(ChainLink.NORMAL);
      }
    }
    // add 1 normal ChainLink to the end (1 block air must be at the end in order to prevent
    // looping)
    chainLinks.add(ChainLink.NORMAL);
    return Chain.of(chainLinks.toArray(new ChainLink[0]));
  }

  private void addUnInstallation() {
    List<Command> installation = program.getInstallation();
    List<Command> uninstallation = program.getUninstallation();

    boolean thereAreProcesses = false;
    for (CommandBlockChain chain : result) {
      if (chain.getName() != null) {
        thereAreProcesses = true;
        break;
      }
    }
    if (thereAreProcesses || !installation.isEmpty() || !uninstallation.isEmpty()) {
      // move all chains by 1 block, if installation or uninstallation is added.
      // when an uninstallation is added an installation is also added, therefor we don't have to
      // check both here.
      for (CommandBlockChain chain : result) {
        chain.move(orientation.getC().toCoordinate());
      }
    }

    for (CommandBlockChain chain : result) {
      String name = chain.getName();
      if (name == null) {
        continue;
      }
      Coordinate3D chainStart = chain.getMin();
      installation
          .add(0,
              new Command("/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString()
                  + ")} {CustomName:" + chain.getName()
                  + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
      uninstallation.add(new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "_NOTIFY]"));
      // uninstallation
      // .add(0,new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "_INTERCEPTED]"));
    }

    if (!installation.isEmpty() || !uninstallation.isEmpty()) {
      installation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      installation.add(0, null);
    }
    if (!uninstallation.isEmpty()) {
      uninstallation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      uninstallation.add(0, null);
    }

    CommandBlockChain materialisedUninstallation = new IterativeChainComputer().computeOptimalChain(
        new CommandChain("uninstall", uninstallation), new Coordinate3D(100, 100, 0));
    materialisedUninstallation.move(Coordinate3D.UP);
    CommandBlockChain materialisedInstallation = new IterativeChainComputer() {
      protected boolean isCoordinateValid(Coordinate3D coordinate) {
        if (!super.isCoordinateValid(coordinate)) {
          return false;
        }
        List<CommandBlock> commandBlocks = materialisedUninstallation.getCommandBlocks();
        for (CommandBlock block : commandBlocks) {
          if (block.getCoordinate().equals(coordinate)) {
            return false;
          }
        }
        return true;
      };
    }.computeOptimalChain(new CommandChain("install", installation),
        new Coordinate3D(100, 100, 0));
    if (!materialisedInstallation.getCommandBlocks().isEmpty()) {
      result.add(materialisedInstallation);
    }
    if (!materialisedUninstallation.getCommandBlocks().isEmpty()) {
      result.add(materialisedUninstallation);
    }
  }

}
