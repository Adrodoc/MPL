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

import java.util.LinkedList;

import de.adrodoc55.minecraft.Coordinate3D;

public class Program {

  private LinkedList<CommandChain> chains;
  private LinkedList<Command> installation;
  private LinkedList<Command> uninstallation;

  // Compiler-Options
  private Coordinate3D max;
  private String prefix;
  private MplOrientation orientation;

  public Program() {
    chains = new LinkedList<CommandChain>();
    installation = new LinkedList<Command>();
    uninstallation = new LinkedList<Command>();
  }

  public LinkedList<CommandChain> getChains() {
    return chains;
  }

  public void setChains(LinkedList<CommandChain> chains) {
    this.chains = chains;
  }

  public LinkedList<Command> getInstallation() {
    return installation;
  }

  public void setInstallation(LinkedList<Command> installation) {
    this.installation = installation;
  }

  public LinkedList<Command> getUninstallation() {
    return uninstallation;
  }

  public void setUninstallation(LinkedList<Command> uninstallation) {
    this.uninstallation = uninstallation;
  }

  public Coordinate3D getMax() {
    return max;
  }

  public void setMax(Coordinate3D max) {
    this.max = max;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public MplOrientation getOrientation() {
    return orientation;
  }

  public void setOrientation(MplOrientation orientation) {
    this.orientation = orientation;
  }

}
