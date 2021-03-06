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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.coordinate.Axis3.Y;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNoOperationCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;
import static de.kussm.direction.Direction.EAST;
import static de.kussm.direction.Direction.NORTH;
import static de.kussm.direction.Direction.WEST;
import static de.kussm.direction.Directions.$;
import static java.util.Comparator.naturalOrder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import de.adrodoc55.minecraft.coordinate.Axis3;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.blocks.AirBlock;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeOriginInsert;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLayouter;
import de.kussm.chain.ChainLinkType;
import de.kussm.direction.Directions;
import de.kussm.position.Position;

/**
 * @author Adrodoc55
 */
public abstract class MplChainPlacer {
  protected final ChainContainer container;
  protected final CompilerOptions options;
  protected final MinecraftVersion version;
  protected final List<CommandBlockChain> chains = new ArrayList<>();

  protected MplChainPlacer(ChainContainer container, MinecraftVersion version,
      CompilerOptions options) {
    this.container = container;
    this.version = version;
    this.options = options;
  }

  protected Orientation3D getOrientation() {
    return container.getOrientation();
  }

  protected CommandChain getInstall() {
    return container.getInstall();
  }

  protected CommandChain getUninstall() {
    return container.getUninstall();
  }

  public abstract List<CommandBlockChain> place() throws NotEnoughSpaceException;

  /**
   * Generates a flat {@link CommandBlockChain}. Flat means, that the chain will not have any width
   * in the c direction of the orientation.<br>
   * The chain will not have any illegal transmitter or receiver regarding all chains that have
   * already been added to {@link #chains}. Also the chain will not have any illegally placed
   * conditional command blocks.
   *
   * @param chain the {@link CommandChain} to be placed
   * @param start the starting coordinate of the chain
   * @param template along which the chain should be placed
   * @return a valid placed {@link CommandBlockChain}
   * @throws NotEnoughSpaceException if the template is to small to allow a valid placement
   */
  public CommandBlockChain generateFlat(CommandChain chain, Coordinate3D start, Directions template)
      throws NotEnoughSpaceException {
    LinkedHashMap<Position, ChainLinkType> placed = place(chain, start, template);
    List<MplBlock> blocks = toBlocks(chain.getCommands(), placed);
    CommandBlockChain result = new CommandBlockChain(chain.getName(), blocks, chain.getTags());
    result.move(start);
    return result;
  }

  /**
   * Places the given chain. The placement will not have any illegal transmitter or receiver
   * regarding all chains that have already been added to {@link #chains}. Also the chain will not
   * have any illegally placed conditional command blocks.
   *
   * @param chain the {@link CommandChain} to be placed
   * @param start the starting coordinate of the chain
   * @param template along which the chain should be placed
   * @return a {@link LinkedHashMap} containing the {@link Position}s and {@link ChainLinkType}s of
   *         the valid placed chain
   * @throws NotEnoughSpaceException if the template is to small to allow a valid placement
   */
  protected LinkedHashMap<Position, ChainLinkType> place(CommandChain chain, Coordinate3D start,
      Directions template) throws NotEnoughSpaceException {
    Chain chainLinkChain = toChainLinkChain(chain.getCommands());
    Set<Position> forbiddenReceiver = new HashSet<>();
    Set<Position> forbiddenTransmitter = new HashSet<>();
    fillForbiddenPositions(start, forbiddenReceiver, forbiddenTransmitter);
    return place(chainLinkChain, template, forbiddenReceiver, forbiddenTransmitter);
  }

  private void fillForbiddenPositions(Coordinate3D start, Set<Position> forbiddenReceiver,
      Set<Position> forbiddenTransmitter) {
    Orientation3D orientation = getOrientation();
    Axis3 cAxis = orientation.getC().getAxis();
    int startC = (int) start.get(cAxis);
    for (CommandBlockChain materialized : chains) {
      for (MplBlock block : materialized.getBlocks()) {
        Coordinate3D currentCoord = block.getCoordinate();
        int currentC = (int) currentCoord.get(cAxis);

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

  /**
   * Places the chain according to the template WITHOUT regarding forbidden transmitter/receiver
   * positions. The chain will not have any illegally placed conditional command blocks.
   *
   * @param chain the {@link Chain} to place
   * @param template along which the chain should be placed
   * @return a {@link LinkedHashMap} containing the {@link Position}s and {@link ChainLinkType}s of
   *         the placed chain
   * @throws NotEnoughSpaceException if the template is to small to allow a placement
   */
  public LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions template)
      throws NotEnoughSpaceException {
    return place(chain, template, new HashSet<>(), new HashSet<>());
  }

  /**
   * Places the given chain. The chain will not have any illegally placed conditional command
   * blocks.<br>
   * If this placement runs with {@link CompilerOption#TRANSMITTER}, the result will not have any
   * illegal transmitter or receiver regarding the parameters. Also there will be no receiver at x=0
   * and no transmitter at x=1.
   *
   * @param chain the {@link Chain} to be placed
   * @param template along which the should will be placed
   * @param forbiddenReceivers a {@link Set} containing all {@link Position}s that may not contain a
   *        receiver
   * @param forbiddenTransmitters a {@link Set} containing all {@link Position}s that may not
   *        contain a transmitter
   * @return a {@link LinkedHashMap} containing the {@link Position}s and {@link ChainLinkType}s of
   *         the valid placed chain
   * @throws NotEnoughSpaceException if the template is to small to allow a valid placement
   */
  protected LinkedHashMap<Position, ChainLinkType> place(Chain chain, Directions template,
      Set<Position> forbiddenReceivers, Set<Position> forbiddenTransmitters)
      throws NotEnoughSpaceException {
    if (options.hasOption(TRANSMITTER)) {
      // receivers are not allowed at x=0 because the start transmitters of all chains are at x=0
      Predicate<Position> isReceiverAllowed =
          pos -> !forbiddenReceivers.contains(pos) && pos.getX() != 0;

      // transmitters are not allowed at x=1 because the start receivers of all chains are at x=1
      Predicate<Position> isTransmitterAllowed =
          pos -> !forbiddenTransmitters.contains(pos) && pos.getX() != 1;

      return ChainLayouter.place(chain, template, isReceiverAllowed, isTransmitterAllowed);
    } else {
      return ChainLayouter.place(chain, template);
    }
  }

  protected CommandChain getPopulatedInstall() {
    List<ChainLink> commands = getInstall().getCommands();
    ArrayList<ChainLink> result = new ArrayList<>(commands.size() + chains.size());
    result.addAll(commands);
    for (CommandBlockChain chain : chains) {
      String name = chain.getName();
      if (name == null || "install".equals(name) || "uninstall".equals(name)) {
        continue;
      }
      Coordinate3D chainStart = chain.getBlocks().get(0).getCoordinate();
      boolean nonTransmitterDebug = (!options.hasOption(TRANSMITTER) && options.hasOption(DEBUG));
      if (nonTransmitterDebug || version.isGreaterThanOrEqualTo(MinecraftVersion._16w32a)) {
        chainStart = chainStart.minus(0.4, Y);
      } else {
        chainStart = chainStart.plus(0.4, Y);
      }
      int index = options.hasOption(TRANSMITTER) ? 2 : 1;
      String tags = "";
      for (String tag : chain.getTags()) {
        tags += "," + tag;
      }

      CommandPartBuffer cpb = new CommandPartBuffer();
      cpb.add("summon " + version.markerEntity() + " ");
      cpb.add(new RelativeOriginInsert(chainStart));
      cpb.add(" {CustomName:" + name + ",Tags:[" + container.getHashCode() + tags
          + "],NoGravity:1b,Invisible:1b,Invulnerable:1b"
          + (nonTransmitterDebug ? "" : ",Marker:1b")
          + (options.hasOption(DEBUG) ? ",CustomNameVisible:1b" : "") + "}");
      result.add(index, newCommand(cpb, modifier()));
    }
    return new CommandChain(getInstall().getName(), result);
  }

  protected CommandChain getPopulatedUninstall() {
    List<ChainLink> commands = getUninstall().getCommands();
    ArrayList<ChainLink> result = new ArrayList<>(commands.size() + 1);
    result.addAll(commands);
    if (!commands.isEmpty()) {
      result.add(newCommand(
          "/kill @e[type=" + version.markerEntity() + ",tag=" + container.getHashCode() + "]"));
    }
    return new CommandChain(getUninstall().getName(), result);
  }

  protected List<MplBlock> toBlocks(List<ChainLink> commands, Map<Position, ChainLinkType> placed) {
    Deque<ChainLink> chainLinks = new ArrayDeque<>(commands);

    Orientation3D orientation = getOrientation();

    Deque<Entry<Position, ChainLinkType>> entries =
        placed.entrySet().stream().collect(Collectors.toCollection(ArrayDeque::new));

    List<MplBlock> blocks = new ArrayList<>();
    while (entries.size() > 1) {
      Entry<Position, ChainLinkType> entry = entries.pop();

      Position pos = entry.getKey();
      Position nextPos = entries.peek().getKey();

      Direction3 d = getDirection(pos, nextPos, orientation);
      Coordinate3D coord = toCoordinate(pos, orientation);

      if (entry.getValue() == ChainLinkType.NO_OPERATION) {
        blocks.add(new CommandBlock(newNoOperationCommand(), d, coord));
      } else {
        ChainLink chainLink = chainLinks.pop();
        blocks.add(chainLink.toBlock(coord, d));
      }
    }

    // last block is always air
    Position lastPos = entries.pop().getKey();
    blocks.add(new AirBlock(toCoordinate(lastPos, orientation)));
    return blocks;
  }

  public int getLongestSuccessiveConditionalCount() {
    return Stream
        .concat(container.getChains().stream().map(c -> c.getCommands()),
            Stream.of(getInstall().getCommands(), getUninstall().getCommands()))
        .map(commands -> getLongestSuccessiveConditionalCount(commands)).max(naturalOrder())
        .orElse(0);
  }

  protected CommandPartBuffer getDeleteCommand() {
    Coordinate3D max = getBoundaries();
    CommandPartBuffer result = new CommandPartBuffer();
    result.add("fill ");
    result.add(new RelativeOriginInsert(new Coordinate3D()));
    result.add(" ");
    result.add(new RelativeOriginInsert(max));
    result.add(" air");
    return result;
  }

  protected Coordinate3D getBoundaries() {
    return MplUtils.getBoundaries(getOrientation(),
        chains.stream()//
            .map(c -> c.getBlocks())//
            .flatMap(bs -> bs.stream())//
            .map(b -> b.getCoordinate())//
            .collect(Collectors.toList()));
  }

  public static int getLongestSuccessiveConditionalCount(List<? extends ChainLink> chainLinks) {
    checkNotNull(chainLinks, "chainLinks == null!");
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
   * Converts the {@link Position} to a {@link Coordinate3D} using the {@link Orientation3D}. The
   * conversion is done along the Axis x → a and y → b.<br>
   * The resulting coordinate will have the value 0 for the c axis of the orientation.
   *
   * @param pos to convert
   * @param orientation along which to perform the conversion
   * @return a {@link Coordinate3D} representing the given {@link Position}
   */
  public static Coordinate3D toCoordinate(Position pos, Orientation3D orientation) {
    Coordinate3D xDir = orientation.getA().toCoordinate3D();
    Coordinate3D yDir = orientation.getB().toCoordinate3D();
    Coordinate3D coord = xDir.mult(pos.getX()).plus(yDir.mult(pos.getY()));
    return coord;
  }

  /**
   * Converts the {@link Coordinate3D} to a {@link Position} using the {@link Orientation3D}. The
   * conversion is done along the Axis a → x and b → y.<br>
   *
   * @param coord to convert
   * @param orientation along which to perform the conversion
   * @return a {@link Position} representing the given {@link Coordinate3D}
   */
  public static Position toPosition(Coordinate3D coord, Orientation3D orientation) {
    Direction3 a = orientation.getA();
    Direction3 b = orientation.getB();
    int x = (int) coord.get(a.getAxis());
    int y = (int) coord.get(b.getAxis());
    return Position.at(x, y);
  }

  /**
   * @param cp current position
   * @param np next position
   * @param orientation along which to perform the conversion
   * @return the direction from the current position to the next position
   * @throws IllegalArgumentException if the two positions are not next to each other
   */
  protected static Direction3 getDirection(Position cp, Position np, Orientation3D orientation)
      throws IllegalArgumentException {
    // current coordinate
    Coordinate3D cc = toCoordinate(cp, orientation);
    // next coordinate
    Coordinate3D nc = toCoordinate(np, orientation);
    return Direction3.valueOf(nc.minus(cc));
  }

  public static boolean isTransmitter(MplBlock block) {
    return block instanceof Transmitter;
  }

  public static boolean isTransmitter(ChainLink chainLink) {
    return chainLink instanceof MplSkip;
  }

  public static boolean isReceiver(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      return commandBlock.getNeedsRedstone();
    } else {
      return false;
    }
  }

  public static boolean isReceiver(ChainLink chainLink) {
    if (chainLink instanceof Command) {
      Command command = (Command) chainLink;
      return command.getNeedsRedstone();
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

  protected static Directions newTemplate(int a) {
    // "Timeout" nach 100 b
    int maxB = 1000;
    return $(EAST.repeat(a - 1), NORTH, WEST.repeat(a - 1), NORTH).repeat(a * maxB);
  }

}
