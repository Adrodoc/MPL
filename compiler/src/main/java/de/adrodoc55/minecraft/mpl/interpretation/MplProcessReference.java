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
package de.adrodoc55.minecraft.mpl.interpretation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(of = {"processName"}, callSuper = false)
@ToString(of = {"processName"}, callSuper = false)
@Getter
public class MplProcessReference extends MplReference {
  private final @Nonnull String processName;

  /**
   * Constructs a reference to a process.
   *
   * @param processName the name of the referenced process
   * @param imports the imported files that are expected to contain the process
   * @param source the source that requires {@code this} reference
   * @throws IllegalArgumentException if one of the {@code imports} is not a file
   */
  public MplProcessReference(@Nonnull String processName, @Nonnull Collection<File> imports,
      @Nonnull MplSource source) throws IllegalArgumentException {
    super(imports, source);
    this.processName = checkNotNull(processName, "processName == null!");
  }

  @Override
  public boolean isContainedIn(MplProgram program) {
    return program.containsProcess(processName);
  }

  @Override
  public MplProcess getProcess(MplProgram program) {
    return program.getProcess(processName);
  }

  @Override
  public CompilerException createAmbigiousException(List<File> files) {
    checkArgument(!files.isEmpty(), "files is empty!");
    int lastIndex = files.size() - 1;
    List<File> view = files.subList(0, lastIndex);
    String first = Joiner.on(", ").join(view);
    File last = files.get(lastIndex);
    return new CompilerException(source, "Process " + processName
        + " is ambigious. It was found in '" + first + " and " + last + "!");
  }

}
