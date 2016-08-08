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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter;

/**
 * @author Adrodoc55
 */
public class MplProgramBuilder {
  private final MplProgram program;
  private final Set<File> addedFiles = new HashSet<>();
  private SetMultimap<File, String> programContent = HashMultimap.create();

  public MplProgramBuilder(MplProgram main, File programFile) {
    program = main;
    program.getProcesses().stream()//
        .map(p -> p.getName())//
        .forEach(name -> programContent.put(programFile, name));
  }

  public MplProgram getProgram() {
    return program;
  }

  /**
   * Adds all processes of the specified interpreter to the program, that have not yet been added.
   *
   * @param interpreter
   */
  public void addAllProcesses(MplInterpreter interpreter) {
    addInterpreter(interpreter);
    for (MplProcess process : interpreter.getProgram().getProcesses()) {
      addProcess(interpreter, process.getName());
    }
  }

  public void addProcess(MplInterpreter interpreter, String processName) {
    addInterpreter(interpreter);
    File file = interpreter.getProgramFile();
    MplProgram program = interpreter.getProgram();
    if (programContent.put(file, processName)) {
      this.program.addProcess(program.getProcess(processName));
    }
  }

  /**
   * Adds install and uninstall of the specified {@link MplInterpreter} to the program, if that
   * interpreter was not added yet.
   *
   * @param interpreter
   */
  private void addInterpreter(MplInterpreter interpreter) {
    File file = interpreter.getProgramFile();
    if (addedFiles.add(file)) {
      MplProgram program = interpreter.getProgram();
      addToInstall(program.getInstall());
      addToUninstall(program.getUninstall());
    }
  }

  public void addToInstall(@Nullable MplProcess process) {
    MplProcess install = program.getInstall();
    if (install == null) {
      program.setInstall(process);
    } else {
      install.addAll(process);
    }
  }

  public void addToUninstall(@Nullable MplProcess process) {
    MplProcess uninstall = program.getUninstall();
    if (uninstall == null) {
      program.setUninstall(process);
    } else {
      uninstall.addAll(process);
    }
  }

}
