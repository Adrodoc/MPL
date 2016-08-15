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

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.FileException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.MplInclude;
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter;
import de.adrodoc55.minecraft.mpl.interpretation.MplProcessReference;
import de.adrodoc55.minecraft.mpl.interpretation.MplReference;

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

  protected MplInterpreter interpret(File file, MplCompilerContext context) throws IOException {
    MplInterpreter interpreter = interpreterCache.get(file);
    if (interpreter == null) {
      interpreter = MplInterpreter.interpret(file, context);
      interpreterCache.put(file, interpreter);
    }
    return interpreter;
  }

  public MplProgram assemble(File programFile) throws IOException {
    MplInterpreter main = interpret(programFile, context);
    MplProgram program = main.getProgram();
    programBuilder = new MplProgramBuilder(program, programFile);
    resolveReferences(main.getReferences().values());
    if (!program.isScript()) {
      doIncludes();
    }
    MplProgram result = programBuilder.getProgram();
    boolean containsRemoteProcess = result.getProcesses().stream()//
        .anyMatch(p -> p.getType() == ProcessType.REMOTE);
    if (context.getErrors().isEmpty() && !containsRemoteProcess) {
      context.addError(
          new CompilerException(new MplSource(programFile, new CommonToken(MplLexer.PROCESS), ""),
              "This file does not include any remote processes"));
    }
    return result;
  }

  protected void doIncludes() {
    MplInclude include;
    while ((include = context.getNextInclude()) != null) {
      File file = include.getFile();
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file, context);
      } catch (IOException ex) {
        context.addError(new CompilerException(include.getSource(),
            "Couldn't include '" + FileUtils.getCanonicalPath(file) + "'", ex));
        continue;
      }
      MplProgram program = interpreter.getProgram();
      if (program.isScript()) {
        context.addError(new CompilerException(include.getSource(),
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

  protected void resolveReferences(Collection<? extends MplReference> references) {
    for (MplReference reference : references) {
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
  protected void resolveReference(MplReference reference) {
    FileException possibleCause = null;
    Collection<File> imports = reference.getImports();
    List<MplInterpreter> found = new ArrayList<>(imports.size());
    for (File file : imports) {
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file, new MplCompilerContext(context.getOptions()));
      } catch (IOException ex) {
        possibleCause = new FileException(ex, file);
        continue;
      }
      MplProgram program = interpreter.getProgram();
      if (reference.isContainedIn(program)) {
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
      context.addWarning(reference.createNotFoundException(possibleCause));
    } else if (found.size() > 1) {
      List<File> files = found.stream().map(i -> i.getProgramFile()).collect(toList());
      context.addError(reference.createAmbigiousException(files));
    } else {
      MplInterpreter interpreter = found.get(0);
      context.addContext(interpreter.getContext());
      File programFile = interpreter.getProgramFile();
      MplProgram program = interpreter.getProgram();
      MplProcess process = reference.getProcess(program);
      context.addInclude(new MplInclude(process.getName(), programFile, reference.getSource()));
    }
  }

  public boolean refIsContained(MplProcessReference reference, MplProgram program) {
    return program.containsProcess(reference.getProcessName());
  }

}
