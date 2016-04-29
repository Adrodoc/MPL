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
package de.adrodoc55.minecraft.mpl.compilation.placement;

import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import de.adrodoc55.minecraft.coordinate.Axis3D;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.chain.NamedCommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NoOperationCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip;
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.program.MplProgram;
import de.adrodoc55.minecraft.mpl.program.MplProject;
import de.adrodoc55.minecraft.mpl.program.MplScript;
import de.kussm.ChainLayouter;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLinkType;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Adrodoc55
 */
public abstract class MplChainPlacer {

  public static List<CommandBlockChain> place(MplProgram program) {
    if (program instanceof MplProject) {
      MplProject project = (MplProject) program;
      return new MplDebugProjectPlacer(project).place();
    }
    if (program instanceof MplScript) {
      MplScript script = (MplScript) program;
      return new MplScriptPlacer(script).place();
    }
    throw new IllegalArgumentException("Unknown MplProgram class: " + program.getClass());
  }

  protected final MplProgram program;
  protected final CompilerOptions options;
  protected final List<CommandBlockChain> chains = new LinkedList<CommandBlockChain>();

  protected MplChainPlacer(MplProgram program, CompilerOptions options) {
    this.program = program;
    this.options = options;
  }

  protected Orientation3D getOrientation() {
    return program.getOrientation();
  }

  protected List<ChainPart> getInstallation() {
    return program.getInstallation();
  }

  protected List<ChainPart> getUninstallation() {
    return program.getUninstallation();
  }

  public abstract List<CommandBlockChain> place();

  /**
   * Calculates the optimal boundaries for this Program. The optimal boundaries must be smaller than
   * {@code program.getMax()}, but should leave enough space for conditional chains.<br>
   * The optimal size is a zero based exclusive coordinate. That means that a result of (1, 1, 1)
   * relates to a 1 block sized cube.<br>
   *
   * @return opt the optimal boundaries
   */
  protected abstract Coordinate3D getOptimalSize();

  /**
   * Generates a flat {@link CommandBlockChain}. Flat means, that the chain will not have any width
   * in the c direction of the orientation.<br>
   * The chain will not have any illegal transmitter or receiver regarding all chains that have
   * already been added to {@link #chains}. Also the chain will not have any illegally placed
   * conditional command blocks.
   *
   * @param chain
   * @param start the starting coordinate of the chain
   * @param template
   * @return
   */
  protected CommandBlockChain generateFlat(CommandChain chain, Coordinate3D start,
      Directions template) {
    List<ChainLink> commands = chain.getCommands();
    Chain chainLinkChain = toChainLinkChain(commands);

    Set<Position> forbiddenReceiver = new HashSet<>();
    Set<Position> forbiddenTransmitter = new HashSet<>();
    fillForbiddenPositions(start, forbiddenReceiver, forbiddenTransmitter);

    LinkedHashMap<Position, ChainLinkType> placed =
        place(chainLinkChain, template, forbiddenReceiver, forbiddenTransmitter);

    String name = chain instanceof NamedCommandChain ? ((NamedCommandChain) chain).getName() : null;
    CommandBlockChain materialized = new CommandBlockChain(name, toBlocks(commands, placed));
    materialized.move(start);
    return materialized;
  }

  protected void fillForbiddenPositions(Coordinate3D start, Set<Position> forbiddenReceiver,
      Set<Position> forbiddenTransmitter) {
    Orientation3D orientation = getOrientation();
    Axis3D cAxis = orientation.getC().getAxis();
    int startC = start.get(cAxis);
    for (CommandBlockChain materialized : chains) {
      for (MplBlock block : materialized.getBlocks()) {
        Coordinate3D currentCoord = block.getCoordinate();
        int currentC = currentCoord.get(cAxis);

        Position pos = toPosition(currentCoord.minus(start), orientation);
        ImmutableSet<Position> illegalPositions;
        if (startC - 1 == currentC || currentC == startC + 1) {
          illegalPositions = ImmutableSet.<Position>of(pos);
        } else if (startC == currentC) {
          illegalPositions = pos.neighbours();
        } else {
          continue;
        }
        for (Position illegalPos : illegalPositions) {
          // Only look at positive positions
          if (illegalPos.getX() >= 0 && illegalPos.getY() >= 0) {
            if (isTransmitter(block)) {
              forbiddenReceiver.add(illegalPos);
            } else if (isReceiver(block)) {
              forbiddenTransmitter.add(illegalPos);
            }
          }
        }
      }
    }
  }

  protected LinkedHashMap<Position, ChainLinkType> place(Chain linkChain, Directions template,
      Set<Position> forbiddenReceivers, Set<Position> forbiddenTransmitters) {
    // receivers are not allowed at x=0 because the start transmitters of all chains are at x=0
    Predicate<Position> isReceiverAllowed =
        pos -> !forbiddenReceivers.contains(pos) && pos.getX() != 0;

    // transmitters are not allowed at x=1 because the start receivers of all chains are at x=1
    Predicate<Position> isTransmitterAllowed =
        pos -> !forbiddenTransmitters.contains(pos) && pos.getX() != 1;

    LinkedHashMap<Position, ChainLinkType> placed =
        ChainLayouter.place(linkChain, template, isReceiverAllowed, isTransmitterAllowed);
    return placed;
  }

  protected List<MplBlock> toBlocks(List<ChainLink> commands,
      LinkedHashMap<Position, ChainLinkType> placed) {
    LinkedList<ChainLink> chainLinks = new LinkedList<>(commands);

    Orientation3D orientation = getOrientation();

    LinkedList<Entry<Position, ChainLinkType>> entries =
        placed.entrySet().stream().collect(Collectors.toCollection(LinkedList::new));

    List<MplBlock> blocks = new LinkedList<>();
    while (entries.size() > 1) {
      Entry<Position, ChainLinkType> entry = entries.pop();

      Position pos = entry.getKey();
      Position nextPos = entries.peek().getKey();

      Direction3D d = getDirection(pos, nextPos, orientation);
      Coordinate3D coord = toCoordinate(pos, orientation);

      if (entry.getValue() == ChainLinkType.NO_OPERATION) {
        blocks.add(new CommandBlock(new NoOperationCommand(), d, coord));
      } else {
        ChainLink chainLink = chainLinks.pop();
        if (chainLink instanceof Command) {
          blocks.add(new CommandBlock((Command) chainLink, d, coord));
        } else if (chainLink instanceof Skip) {
          blocks.add(new Transmitter(((Skip) chainLink).isInternal(), coord));
        }
      }
    }

    // last block is always air
    Position lastPos = entries.pop().getKey();
    blocks.add(new AirBlock(toCoordinate(lastPos, orientation)));
    return blocks;
  }

  /**
   * This method generates both installation uninstallation of the program at (0, 0, 0) and adds
   * them to {@link #chains}. The resulting chains are in the same flat plane.
   *
   * @see #generateFlat(CommandChain, Coordinate3D, Directions)
   */
  protected void generateUnInstallation() {
    Orientation3D orientation = getOrientation();
    Coordinate3D optimalSize = getOptimalSize();
    Axis3D aAxis = orientation.getA().getAxis();
    int aSize = optimalSize.get(aAxis);
    // Bei ungerader Größe hat install einen Block weniger, da install effektiv 2 Reihen mehr hat.
    int aInstall = aSize / 2;
    int aUninstall = aSize - aInstall;

    List<ChainPart> uninstallation = program.getUninstallation();
    if (!uninstallation.isEmpty()) {
      Coordinate3D uninstallSize = optimalSize.minus(aInstall, aAxis);

      NamedCommandChain chain = new NamedCommandChain("uninstall", uninstallation);
      Coordinate3D start = orientation.getB().toCoordinate();
      Directions template = newDirectionsTemplate(uninstallSize, orientation);
      CommandBlockChain materialised = generateFlat(chain, start, template);
      chains.add(materialised);
    }

    List<ChainPart> installation = program.getInstallation();
    if (!installation.isEmpty()) {
      Coordinate3D size = optimalSize.minus(aUninstall, aAxis);

      NamedCommandChain chain = new NamedCommandChain("install", installation);
      Coordinate3D start = new Coordinate3D();
      Directions template =
          $(EAST.repeat(Math.abs(aUninstall)), newDirectionsTemplate(size, orientation));
      CommandBlockChain materialised = generateFlat(chain, start, template);
      chains.add(materialised);
    }
  }

  public static int getLongestSuccessiveConditionalCount(List<? extends ChainLink> chainLinks) {
    Preconditions.checkNotNull(chainLinks, "chainLinks == null!");
    int result = 0;
    int successiveConditionalCount = 0;
    for (ChainLink chainLink : chainLinks) {
      if (chainLink instanceof Command && ((Command) chainLink).isConditional()) {
        successiveConditionalCount++;
      } else {
        result = Math.max(result, successiveConditionalCount);
        successiveConditionalCount = 0;
      }
    }
    result = Math.max(result, successiveConditionalCount);
    return result;
  }

  /**
   * x -> a, y -> b
   */
  public static Coordinate3D toCoordinate(Position pos, Orientation3D orientation) {
    Coordinate3D xDir = orientation.getA().toCoordinate();
    Coordinate3D yDir = orientation.getB().toCoordinate();
    Coordinate3D coord = xDir.mult(pos.getX()).plus(yDir.mult(pos.getY()));
    return coord;
  }

  /**
   * a -> x, b -> y
   */
  public static Position toPosition(Coordinate3D coord, Orientation3D orientation) {
    Direction3D a = orientation.getA();
    Direction3D b = orientation.getB();
    int x = coord.get(a.getAxis());
    int y = coord.get(b.getAxis());
    return Position.at(x, y);
  }

  /**
   *
   * @param cp current position
   * @param np next position
   * @param orientation
   * @return
   */
  protected static Direction3D getDirection(Position cp, Position np, Orientation3D orientation) {
    // current coordinate
    Coordinate3D cc = toCoordinate(cp, orientation);
    // next coordinate
    Coordinate3D nc = toCoordinate(np, orientation);
    return Direction3D.valueOf(nc.minus(cc));
  }

  public static boolean isTransmitter(MplBlock block) {
    return block instanceof Transmitter;
  }

  public static boolean isTransmitter(ChainLink chainLink) {
    return chainLink instanceof Skip;
  }

  public static boolean isReceiver(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      return isReceiver(commandBlock.toCommand());
    } else {
      return false;
    }
  }

  public static boolean isReceiver(ChainLink chainLink) {
    if (chainLink instanceof Command) {
      Command command = (Command) chainLink;
      return command.getMode() != Mode.CHAIN;
    } else {
      return false;
    }
  }

  public static ChainLinkType getType(ChainLink chainLink) {
    if (isTransmitter(chainLink)) {
      return ChainLinkType.TRANSMITTER;
    } else if (isReceiver(chainLink)) {
      return ChainLinkType.RECEIVER;
    } else if (chainLink instanceof Command) {
      Command command = (Command) chainLink;
      if (command.isConditional()) {
        return ChainLinkType.CONDITIONAL;
      }
    }
    return ChainLinkType.NORMAL;
  }

  protected static Chain toChainLinkChain(List<ChainLink> chainLinks) {
    ArrayList<ChainLinkType> chainLinkTypes = new ArrayList<ChainLinkType>(chainLinks.size());
    for (ChainLink chainLink : chainLinks) {
      chainLinkTypes.add(getType(chainLink));
    }
    // add 1 normal ChainLink to the end (1 block air must be at the end in order to prevent chain
    // looping)
    chainLinkTypes.add(ChainLinkType.NORMAL);
    return Chain.of(chainLinkTypes.toArray(new ChainLinkType[0]));
  }

  public static Directions newDirectionsTemplate(Coordinate3D size, Orientation3D orientation) {
    int sizeA = Math.abs(size.get(orientation.getA().getAxis()));
    int sizeB = Math.abs(size.get(orientation.getB().getAxis()));
    Directions dirs =
        $(EAST.repeat(sizeA - 1), NORTH, WEST.repeat(sizeA - 1), NORTH).repeat((sizeB + 1) * sizeA);
    return dirs;
  }

}
