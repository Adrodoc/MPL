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

import java.util.List;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.kussm.direction.Directions;

/**
 * @author Adrodoc55
 */
public class MplDebugProgramPlacer extends MplChainPlacer {

  private Coordinate3D start = new Coordinate3D().plus(3, getOrientation().getC());

  public MplDebugProgramPlacer(ChainContainer container, CompilerOptions options) {
    super(container, options);
  }

  @Override
  public List<CommandBlockChain> place() throws NotEnoughSpaceException {
    for (CommandChain chain : container.getChains()) {
      addChain(chain);
    }
    addUnInstallation();
    return chains;
  }

  public void addChain(CommandChain chain) throws NotEnoughSpaceException {
    Directions template = newTemplate(chain.getCommands().size() + 1);
    CommandBlockChain generated = generateFlat(chain, start, template);
    chains.add(generated);
    start = start.plus(getOrientation().getC().toCoordinate().mult(2));
  }

  private void addUnInstallation() throws NotEnoughSpaceException {
    start = new Coordinate3D();

    // move all chains by 2 block, if install is added and 2 more blocks if uninstall is added.
    // if there is at least one process, both installation and unistallation will be added.
    int offset = 0;
    if (!container.getChains().isEmpty()) {
      offset = 2;
    } else {
      if (!getInstall().getCommands().isEmpty()) {
        offset++;
      }
      if (!getUninstall().getCommands().isEmpty()) {
        offset++;
      }
    }
    for (CommandBlockChain chain : chains) {
      chain.move(getOrientation().getC().toCoordinate().mult(offset));
    }
    generateUnInstall();
  }

  protected void generateUnInstall() throws NotEnoughSpaceException {
    CommandChain uninstall = getPopulatedUninstall();
    Command deleteOnUninstall = null;
    if (options.hasOption(DELETE_ON_UNINSTALL)) {
      deleteOnUninstall = new Command();
      uninstall.getCommands().add(deleteOnUninstall);
    }

    addChain(getPopulatedInstall());
    addChain(uninstall);

    if (deleteOnUninstall != null) {
      deleteOnUninstall.setCommand(getDeleteCommand());
    }
  }
}
