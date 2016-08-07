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
package de.adrodoc55.minecraft.mpl.assembly;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.FileException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.interpretation.MplInclude;
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter;
import de.adrodoc55.minecraft.mpl.interpretation.MplProcessReference;

/**
 * @author Adrodoc55
 */
public class MplProgramAssemler {
  protected final MplCompilerContext context;
  protected final Map<File, MplInterpreter> interpreterCache = new HashMap<>();
  protected MplProgramBuilder programBuilder;

  public MplProgramAssemler(MplCompilerContext context) {
    this.context = context;
  }

  protected MplInterpreter interpret(File file) throws IOException {
    MplInterpreter interpreter = interpreterCache.get(file);
    if (interpreter == null) {
      interpreter = MplInterpreter.interpret(file, context);
    }
    return interpreter;
  }

  public MplProgram assemble(File programFile) throws IOException {
    MplInterpreter main = interpret(programFile);
    MplProgram program = main.getProgram();
    programBuilder = new MplProgramBuilder(program, programFile);
    resolveReferences(main.getReferences().values());
    if (!program.isScript()) {
      doIncludes();
    }
    MplProgram result = programBuilder.getProgram();
    return result;
  }

  protected void doIncludes() {
    MplInclude include;
    while ((include = context.getNextInclude()) != null) {
      File file = include.getFile();
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file);
      } catch (IOException ex) {
        context.addException(new CompilerException(include.getSource(),
            "Couldn't include '" + FileUtils.getCanonicalPath(file) + "'", ex));
        continue;
      }
      MplProgram program = interpreter.getProgram();
      if (program.isScript()) {
        context.addException(new CompilerException(include.getSource(),
            "Can't include script '" + file.getName() + "'. Scripts may not be included."));
        continue;
      }
      String processName = include.getProcessName();
      if (processName != null) {
        programBuilder.addProcess(interpreter, processName);
        resolveReferences(interpreter.getReferences().get(processName));
      } else {
        programBuilder.addAllProcesses(interpreter);
        resolveReferences(interpreter.getReferences().values());
      }
    }
  }

  public void resolveReferences(Collection<MplProcessReference> references) {
    for (MplProcessReference reference : references) {
      resolveReference(reference);
    }
  }

  /**
   * Resolves the specified {@link MplProcessReference}. If {@code reference} could be resolved, a
   * {@link MplInclude} is added to {@link #context}. If it could not be resolved, a
   * {@link CompilerException} is posted using the reference source.
   *
   * @param reference
   */
  protected void resolveReference(MplProcessReference reference) {
    String processName = reference.getProcessName();
    FileException lastException = null;
    List<MplInterpreter> found = new LinkedList<>();
    for (File file : reference.getImports()) {
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file);
      } catch (IOException ex) {
        lastException = new FileException(ex, file);
        continue;
      }
      MplProgram program = interpreter.getProgram();
      if (program.containsProcess(processName)) {
        // Referencing a process in the same file is never ambigious
        if (file.equals(reference.getSource().file)) {
          found.clear();
          found.add(interpreter);
          break;
        } else {
          found.add(interpreter);
        }
      }
    }

    if (found.isEmpty()) {
      context.addException(new CompilerException(reference.getSource(),
          "Could not resolve process " + processName, lastException));
    } else if (found.size() > 1) {
      context.addException(createAmbigiousProcessException(reference, found));
    } else {
      MplInterpreter interpreter = found.get(0);
      context.addInclude(
          new MplInclude(processName, interpreter.getProgramFile(), reference.getSource()));
    }
  }

  protected CompilerException createAmbigiousProcessException(MplProcessReference reference,
      List<MplInterpreter> found) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < found.size() - 1; i++) {
      MplInterpreter interpreter = found.get(i);
      File programFile = interpreter.getProgramFile();
      sb.append(programFile);
      if (i + 1 < found.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(" and ");
    sb.append(found.get(found.size() - 1).getProgramFile());
    CompilerException ex = new CompilerException(reference.getSource(),
        "Process " + reference.getProcessName() + " is ambigious. It was found in '" + sb + "!");
    return ex;
  }

}
