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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.antlr.MplProgram;
import de.adrodoc55.minecraft.mpl.antlr.MplProject;
import de.adrodoc55.minecraft.mpl.antlr.MplScript;
import de.kussm.chain.Chain;
import de.kussm.chain.ChainLink;
import de.kussm.position.Position;

public abstract class MplChainPlacer {

  public static List<CommandBlockChain> place(MplProgram program) {
    if (program instanceof MplProject) {
      MplProject project = (MplProject) program;
      return new MplProjectPlacer(project).place();
    }
    if (program instanceof MplScript) {
      MplScript script = (MplScript) program;
      return new MplScriptPlacer(script).place();
    }
    throw new IllegalArgumentException("Unknown MplProgram class: " + program.getClass());
  }

  protected final MplProgram program;
  protected final List<CommandBlockChain> result = new LinkedList<CommandBlockChain>();

  protected MplChainPlacer(MplProgram program) {
    this.program = program;
  }

  protected Orientation3D getOrientation() {
    Orientation3D orientation = program.getOrientation();
    if (orientation != null) {
      return orientation;
    } else {
      // Default Orientation
      return new Orientation3D();
    }
  }

  protected List<ChainPart> getInstallation() {
    return program.getInstallation();
  }

  protected List<ChainPart> getUninstallation() {
    return program.getUninstallation();
  }

  public abstract List<CommandBlockChain> place();

  public static int getLongestSuccessiveConditionalCount(List<ChainPart> commands) {
    int result = 0;
    int successiveConditionalCount = 0;
    for (ChainPart command : commands) {
      if (command instanceof Command && ((Command) command).isConditional()) {
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

  public static boolean isTransmitter(ChainPart chainPart) {
    return chainPart instanceof Skip;
  }

  public static boolean isReciever(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      return isReciever(commandBlock.toCommand());
    } else {
      return false;
    }
  }

  public static boolean isReciever(ChainPart chainPart) {
    if (chainPart instanceof Command) {
      Command command = (Command) chainPart;
      return command.getMode() != Mode.CHAIN;
    } else {
      return false;
    }
  }

  public static ChainLink toChainLink(ChainPart chainPart) {
    if (isTransmitter(chainPart)) {
      return ChainLink.TRANSMITTER;
    } else if (isReciever(chainPart)) {
      return ChainLink.RECEIVER;
    } else if (chainPart instanceof Command) {
      Command command = (Command) chainPart;
      if (command.isConditional()) {
        return ChainLink.CONDITIONAL;
      }
    }
    return ChainLink.NORMAL;
  }

  protected static Chain toChainLinkChain(List<ChainPart> chainParts) {

    ArrayList<ChainLink> chainLinks = new ArrayList<ChainLink>(chainParts.size());
    for (ChainPart chainPart : chainParts) {
      chainLinks.add(toChainLink(chainPart));
    }
    // add 1 normal ChainLink to the end (1 block air must be at the end in order to prevent
    // looping)
    chainLinks.add(ChainLink.NORMAL);
    return Chain.of(chainLinks.toArray(new ChainLink[0]));
  }

}
