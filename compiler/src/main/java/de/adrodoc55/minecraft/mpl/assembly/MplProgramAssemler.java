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

import static de.adrodoc55.commons.FileUtils.getCanonicalFile;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.MplInclude;
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter;

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
    programFile = getCanonicalFile(programFile);
    // Don't cache the first interpreter, because it's program is mutable and will be changed
    MplInterpreter main = MplInterpreter.interpret(programFile, context);
    MplProgram program = main.getProgram();
    programBuilder = new MplProgramBuilder(program, programFile);
    resolveReferences(main.getReferences().values());
    if (!program.isScript()) {
      doIncludes();
    }
    MplProgram result = programBuilder.getProgram();
//    boolean containsRemoteProcess = result.getProcesses().stream()//
//        .anyMatch(p -> p.getType().isRemote());
//    if (context.getErrors().isEmpty() && !containsRemoteProcess) {
//      context.addError(
//          new CompilerException(new MplSource(programFile, "", new CommonToken(MplLexer.PROCESS)),
//              "This file does not include any remote processes"));
//    }
    return result;
  }

  protected void doIncludes() {
    MplInclude include;
    while ((include = context.getNextInclude()) != null) {
      File file = include.getFile();
      try {
        MplInterpreter interpreter = interpret(file, context);
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
      } catch (IOException ex) {
        context.addError(
            new CompilerException(include.getSource(), "Cannot include file '" + file + "'", ex));
      }
    }
  }

  protected void resolveReferences(Collection<? extends MplReference> references) {
    for (MplReference reference : references) {
      resolveReference(reference);
    }
  }

  /**
   * Resolves the specified {@link MplReference}. If {@code reference} could be resolved, a
   * {@link MplInclude} is added to {@link #context}. If it could not be resolved, a
   * {@link CompilerException} is posted using the reference source.
   *
   * @param reference
   */
  protected void resolveReference(MplReference reference) {
    MplSource source = reference.getSource();
    Collection<File> imports = reference.getImports();
    List<MplInterpreter> found = new ArrayList<>(imports.size());
    for (File file : imports) {
      try {
        MplInterpreter interpreter =
            interpret(file, new MplCompilerContext(context.getVersion(), context.getOptions()));
        if (reference.isContainedIn(interpreter)) {
          // Referencing a process in the same file is never ambigious
          if (file.equals(source.getFile())) {
            found.clear();
            found.add(interpreter);
            break;
          } else {
            found.add(interpreter);
          }
        }
      } catch (IOException ex) {
        context.addError(new CompilerException(source, "Cannot read file '" + file + "'", ex));
      }
    }

    if (found.isEmpty()) {
      reference.handleNotFound(context);
    } else if (found.size() == 1) {
      MplInterpreter interpreter = found.get(0);
      reference.resolve(interpreter);
      context.addContext(interpreter.getContext());
    } else if (found.size() > 1) {
      List<File> files = found.stream().map(MplInterpreter::getProgramFile).collect(toList());
      context.addError(reference.createAmbigiousException(files));
    }
  }
}
