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
package de.adrodoc55.minecraft.mpl.ast.chainparts.program;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import com.google.common.base.Preconditions;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.ast.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.ast.MplNode;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import lombok.Getter;
import lombok.Setter;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@GenerateMplPojoBuilder
public class MplProgram implements MplNode, Named {

  @Getter
  @Setter
  private Token token;

  @Getter
  private String name;

  @Getter
  private boolean script;

  @Getter
  @Setter
  protected Orientation3D orientation;

  @Getter
  @Setter
  protected MplProcess install = new MplProcess("install");

  @Getter
  @Setter
  protected MplProcess uninstall = new MplProcess("uninstall");

  @Getter
  protected final List<CompilerException> exceptions = new LinkedList<>();

  private final Map<String, MplProcess> processMap = new HashMap<>();

  protected Coordinate3D max;

  public Coordinate3D getMax() {
    if (max != null) {
      return max;
    } else {
      return new Coordinate3D(-1, -1, -1);
    }
  }

  public void setMax(Coordinate3D max) {
    if (max.getX() < 0 || max.getY() < 0 || max.getZ() < 0) {
      throw new IllegalArgumentException("The max coordinate of a program must be positive!");
    }
    this.max = max;
  }

  public void addProcess(MplProcess process) {
    Preconditions.checkNotNull(process, "process == null!");
    String name = process.getName();
    MplProcess previous = processMap.get(name);
    if (previous == null) {
      processMap.put(name, process);
      return;
    }
    String oldMessage = "Duplicate process " + name;
    String newMessage = oldMessage;
    MplSource oldSource = previous.getSource();
    MplSource newSource = process.getSource();
    if (!newSource.file.equals(oldSource.file)) {
      oldMessage += "; was also found in " + FileUtils.getCanonicalPath(newSource.file);
      newMessage += "; was also found in " + FileUtils.getCanonicalPath(oldSource.file);
    }
    CompilerException ex1 = new CompilerException(oldSource, oldMessage);
    exceptions.add(ex1);
    CompilerException ex2 = new CompilerException(newSource, newMessage);
    exceptions.add(ex2);
  }

  public boolean containsProcess(String name) {
    return processMap.containsKey(name);
  }

  public MplProcess getProcess(String name) {
    return processMap.get(name);
  }

  /**
   * Read only!
   */
  public Collection<MplProcess> getProcesses() {
    return Collections.unmodifiableCollection(processMap.values());
  }

  @Override
  public void accept(MplAstVisitor visitor) {
    visitor.visitProgram(this);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScript(boolean script) {
    this.script = script;
  }
}
