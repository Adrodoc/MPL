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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Direction3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.ChainPart;
import de.adrodoc55.minecraft.mpl.commands.Command;
import de.adrodoc55.minecraft.mpl.commands.Command.Mode;
import de.adrodoc55.minecraft.mpl.commands.Skip;
import de.adrodoc55.minecraft.mpl.program.MplScript;

/**
 * @author Adrodoc55
 */
public class MplScriptPlacer extends MplChainPlacer {
  private final CommandChain chain;

  protected MplScriptPlacer(MplScript script) {
    super(script);
    this.chain = script.getChain();

    // The first block of each chain that start's with a RECIEVER must be a TRANSMITTER
    List<ChainPart> commands = chain.getCommands();
    if (!commands.isEmpty() && isReceiver(commands.get(0))) {
      commands.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }
  }

  // TODO: Scripte in 3D kompilieren
  @Override
  public List<CommandBlockChain> place() {
    CommandBlockChain generated = generateFlat(chain, new Coordinate3D(),
        newDirectionsTemplate(getOptimalSize(), getOrientation()));
    chains.add(generated);
    addUnInstallation();
    return chains;
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
    int maxA = program.getMax().get(a.getAxis());
    int maxB = program.getMax().get(b.getAxis());
    int maxC = program.getMax().get(c.getAxis());

    int installLength = calculateFutureInstallSize() + calculateFutureUninstallSize();
    int scriptLength = chain.getCommands().size();
    int longestChainLength = Math.max(installLength, scriptLength);

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
    // Plus 3 for first Transmitter, first Receiver and final air block
    return 3 + getInstallation().size();
  }

  private int calculateFutureUninstallSize() {
    // Plus 3 for first Transmitter, first Receiver and final air block
    return 3 + getUninstallation().size();
  }

  public final int getLongestSuccessiveConditionalCount() {
    return Stream.of(chain.getCommands(), getInstallation(), getUninstallation())
        .map(chainParts -> getLongestSuccessiveConditionalCount(chainParts))
        .max(Comparator.naturalOrder()).orElse(0);
  }

  private void addUnInstallation() {
    List<ChainPart> installation = program.getInstallation();
    List<ChainPart> uninstallation = program.getUninstallation();

    Orientation3D orientation = getOrientation();
    if (!installation.isEmpty() || !uninstallation.isEmpty()) {
      // move all chains by 1 block, if installation or uninstallation is added.
      for (CommandBlockChain chain : chains) {
        chain.move(orientation.getC().toCoordinate());
      }
    }

    if (!installation.isEmpty()) {
      installation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      installation.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }
    if (!uninstallation.isEmpty()) {
      uninstallation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      uninstallation.add(0, new Skip(false /* First TRANSMITTER can be referenced */));
    }

    generateUnInstallation();
  }

}
