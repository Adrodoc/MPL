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
package de.adrodoc55.minecraft.mpl.ast.chainparts.program;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.Token;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.commons.Named;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import lombok.Getter;
import lombok.Setter;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
public class MplProgram implements Named {
  private final MplCompilerContext context;
  @Getter
  private final File programFile;
  @Getter
  @Setter
  private @Nullable Token token;

  @Getter
  private @Nullable String name;

  @Getter
  private boolean script;

  @Getter
  @Setter
  protected Orientation3D orientation;

  @Getter
  @Setter
  protected @Nullable MplProcess install;

  @Getter
  @Setter
  protected @Nullable MplProcess uninstall;

  private final Map<String, MplProcess> processMap = new HashMap<>();

  protected @Nullable Coordinate3D max;

  @GenerateMplPojoBuilder
  public MplProgram(File programFile, MplCompilerContext context) {
    this.programFile = checkNotNull(programFile, "programFile == null");
    this.context = checkNotNull(context, "context == null!");
  }

  public Coordinate3D getMax() {
    if (max != null) {
      return max;
    } else {
      return new Coordinate3D(-1, -1, -1);
    }
  }

  public void addProcess(MplProcess process) {
    checkNotNull(process, "process == null!");
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
    context.addError(new CompilerException(oldSource, oldMessage));
    context.addError(new CompilerException(newSource, newMessage));
  }

  public boolean containsProcess(String name) {
    return processMap.containsKey(name);
  }

  public MplProcess getProcess(String name) {
    return processMap.get(name);
  }

  /**
   * Read only!
   *
   * @return an unmodifiable {@link Collection} of the {@link MplProcess}s of this
   *         {@link MplProgram}
   */
  public Collection<MplProcess> getProcesses() {
    return Collections.unmodifiableCollection(processMap.values());
  }

  @Deprecated
  @VisibleForTesting
  void setProcesses(Collection<MplProcess> processes) {
    processMap.clear();
    processMap.putAll(Maps.uniqueIndex(processes, p -> p.getName()));
  }

  /**
   * Opens a {@link Stream} containing all processes of {@code this} program including install and
   * uninstall. The returned stream will not contain any {@code null} values.
   *
   * @return a new {@link Stream} of all processes.
   */
  public Stream<MplProcess> streamProcesses() {
    return Stream.concat(Stream.of(install, uninstall), getProcesses().stream())
        .filter(p -> p != null);
  }

  /**
   * Returns all processes of {@code this} program including install and uninstall if they are
   * present.
   *
   * @return all processes
   */
  public Iterable<MplProcess> getAllProcesses() {
    Iterable<MplProcess> result = getProcesses();
    if (install != null) {
      result = Iterables.concat(Arrays.asList(install), result);
    }
    if (uninstall != null) {
      result = Iterables.concat(Arrays.asList(uninstall), result);
    }
    return result;
  }

  public String getHash() {
    return "MPL" + hashCode();
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public void setScript(boolean script) {
    this.script = script;
  }

  public void setMax(@Nullable Coordinate3D max) {
    this.max = max;
  }

}
