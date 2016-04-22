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
package de.adrodoc55.minecraft.mpl.compilation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.antlr.MplBaseListener;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.chain.MplProcess;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NoOperationCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip;
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.Include;
import de.adrodoc55.minecraft.mpl.compilation.interpretation.MplInterpreter;
import de.adrodoc55.minecraft.mpl.compilation.placement.MplChainPlacer;
import de.adrodoc55.minecraft.mpl.program.MplProgram;
import de.adrodoc55.minecraft.mpl.program.MplProject;

/**
 * @author Adrodoc55
 */
public class MplCompiler extends MplBaseListener {

  public static MplCompilationResult compile(File programFile, CompilerOption... options)
      throws IOException, CompilationFailedException {
    MplProgram program = assembleProgram(programFile);
    MplMaterializer.materialze(program, options);
    List<CommandBlockChain> chains = place(program);
    List<MplBlock> blocks =
        chains.stream().flatMap(c -> c.getBlocks().stream()).collect(Collectors.toList());
    ImmutableMap<Coordinate3D, MplBlock> result = Maps.uniqueIndex(blocks, b -> b.getCoordinate());
    return new MplCompilationResult(program.getOrientation(), result);
  }

  private static List<CommandBlockChain> place(MplProgram program) {
    List<CommandBlockChain> chains = MplChainPlacer.place(program);
    for (CommandBlockChain chain : chains) {
      insertRelativeCoordinates(chain.getBlocks(), program.getOrientation());
    }
    return chains;
  }

  public static MplProgram assembleProgram(File programFile)
      throws IOException, CompilationFailedException {
    MplCompiler compiler = new MplCompiler();
    MplProgram program = compiler.assemble(programFile);
    List<CompilerException> exceptions = program.getExceptions();
    if (!exceptions.isEmpty()) {
      ImmutableListMultimap<File, CompilerException> index =
          Multimaps.index(exceptions, ex -> ex.getSource().file);
      throw new CompilationFailedException(index);
    }
    return program;
  }

  private SetMultimap<File, String> programContent = HashMultimap.create();
  private Set<File> addedInterpreters = new HashSet<>();
  private LinkedList<Include> includeTodos;
  private MplProject project;

  private Map<File, MplInterpreter> interpreterCache = new HashMap<>();

  private MplInterpreter interpret(File file) throws IOException {
    MplInterpreter interpreter = interpreterCache.get(file);
    if (interpreter == null) {
      interpreter = MplInterpreter.interpret(file);
    }
    return interpreter;
  }

  private MplCompiler() {}

  private MplProgram assemble(File programFile) throws IOException {
    includeTodos = new LinkedList<Include>();
    MplInterpreter main = interpret(programFile);
    if (main.isScript()) {
      return main.getScript();
    }
    project = main.getProject();

    // The main interpreter does not need to be added to itself
    addedInterpreters.add(programFile);

    // Skip includes that reference the same file
    List<Include> includes = main.getIncludes().values().stream()
        .filter(include -> !project.containsProcess(include.getProcessName()))
        .collect(Collectors.toList());
    includeTodos.addAll(includes);

    // Mark all processes of the main file
    project.getProcesses().stream().map(p -> p.getName()).forEach(name -> {
      programContent.put(programFile, name);
    });

    doIncludes();
    if (project.hasBreakpoint()) {
      addBreakpointProcess();
    }
    return project;
  }

  private void addBreakpointProcess() {
    List<ChainPart> commands = new LinkedList<>();
    commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    commands.add(new InternalCommand(
        "tellraw @a [{\"text\":\"[tp to breakpoint]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp @p @e[name=breakpoint_NOTIFY,c=-1]\"}},{\"text\":\"   \"},{\"text\":\"[continue program]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/execute @e[name=breakpoint_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block\"}}]"));
    commands.add(new InternalCommand(
        "summon ArmorStand ${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    commands.add(new Skip(true));
    commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    commands.add(new InternalCommand("kill @e[name=breakpoint_NOTIFY]"));
    project.addProcess(new MplProcess("breakpoint", commands));
  }

  private void doIncludes() {
    while (!includeTodos.isEmpty()) {
      Include include = includeTodos.poll();
      String processName = include.getProcessName();
      if (processName == null) {
        massInclude(include);
      } else {
        processInclude(include);
      }
    }
  }

  private void processInclude(Include include) {
    String processName = include.getProcessName();
    FileException lastException = null;
    List<MplInterpreter> found = new LinkedList<>();
    for (File file : include.getFiles()) {
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file);
      } catch (Exception ex) {
        lastException = new FileException(ex, file);
        continue;
      }
      if (interpreter.isScript()) {
        continue;
      }
      MplProject project = interpreter.getProject();
      if (project.containsProcess(processName)) {
        found.add(interpreter);
      }
    }

    if (found.isEmpty()) {
      CompilerException ex = new CompilerException(include.getSource(),
          "Could not resolve process " + processName, lastException);
      project.getExceptions().add(ex);
    } else if (found.size() > 1) {
      CompilerException ex = createAmbigiousProcessException(include, found);
      project.getExceptions().add(ex);
    } else {
      MplInterpreter interpreter = found.get(0);
      addInterpreter(interpreter);
      addProcess(interpreter, processName);
    }
  }

  public CompilerException createAmbigiousProcessException(Include include,
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
    CompilerException ex = new CompilerException(include.getSource(),
        "Process " + include.getProcessName() + " is ambigious. It was found in '" + sb + "!");
    return ex;
  }

  public void addProcess(MplInterpreter interpreter, String processName) {
    MplProcess process = interpreter.getProject().getProcess(processName);
    File file = interpreter.getProgramFile();
    Set<String> processNames = programContent.get(file);
    if (!processNames.contains(process.getName())) {
      project.addProcess(process);
      includeTodos.addAll(interpreter.getIncludes().get(processName));
      programContent.put(file, processName);
    }
  }

  private void massInclude(Include include) {
    for (File file : include.getFiles()) {
      MplInterpreter interpreter = null;
      try {
        interpreter = interpret(file);
      } catch (IOException ex) {
        CompilerException compilerException =
            new CompilerException(include.getSource(), "Couldn't include '" + file + "'", ex);
        project.getExceptions().add(compilerException);
        return;
      }
      if (interpreter.isScript()) {
        CompilerException compilerException = new CompilerException(include.getSource(),
            "Can't include script '" + file.getName() + "'. Scripts may not be included.");
        project.getExceptions().add(compilerException);
        return;
      }
      addInterpreter(interpreter);
      addAllProcesses(interpreter);
    }
  }

  public void addAllProcesses(MplInterpreter interpreter) {
    Collection<MplProcess> processes = interpreter.getProject().getProcesses();
    for (MplProcess process : processes) {
      addProcess(interpreter, process.getName());
    }
  }

  public void addInterpreter(MplInterpreter interpreter) {
    if (addedInterpreters.add(interpreter.getProgramFile())) {
      MplProject project = interpreter.getProject();
      this.project.setHasBreakpoint(this.project.hasBreakpoint() | project.hasBreakpoint());
      this.project.getExceptions().addAll(project.getExceptions());
      this.project.getInstallation().addAll(project.getInstallation());
      this.project.getUninstallation().addAll(project.getUninstallation());
    }
  }

  private static final Pattern thisPattern =
      Pattern.compile("\\$\\{\\s*this\\s*([+-])\\s*(\\d+)\\s*\\}");

  private static final Pattern originPattern = Pattern
      .compile("\\$\\{\\s*origin\\s*\\+\\s*\\(\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s*\\)\\s*\\}");

  private static void insertRelativeCoordinates(List<MplBlock> blocks, Orientation3D orientation) {
    for (int i = 0; i < blocks.size(); i++) {
      MplBlock currentBlock = blocks.get(i);
      if (!(currentBlock instanceof CommandBlock)) {
        continue;
      }
      CommandBlock current = (CommandBlock) currentBlock;
      Matcher thisMatcher = thisPattern.matcher(current.getCommand());
      StringBuffer thisSb = new StringBuffer();
      while (thisMatcher.find()) {
        boolean minus = thisMatcher.group(1).equals("-");
        int relative = Integer.parseInt(thisMatcher.group(2));
        int direction = 1;
        if (minus) {
          direction = -1;
        }
        int refIndex = i;
        for (int steps = relative; steps > 0; steps--) {
          refIndex += direction;
          if (refIndex < 0) {
            refIndex = 0;
            break;
          }
          MplBlock block = blocks.get(refIndex);
          if (isNop(block) || (isInternal(block) && !isInternal(current))) {
            steps++;
          }
        }
        Coordinate3D referenced;
        if (refIndex < 0) {
          referenced = blocks.get(0).getCoordinate().minus(orientation.getA().toCoordinate());
        } else {
          referenced = blocks.get(refIndex).getCoordinate();
        }
        Coordinate3D relativeCoordinate = referenced.minus(current.getCoordinate());
        thisMatcher.appendReplacement(thisSb, relativeCoordinate.toRelativeString());
      }
      thisMatcher.appendTail(thisSb);
      current.setCommand(thisSb.toString());

      Matcher originMatcher = originPattern.matcher(current.getCommand());
      StringBuffer originSb = new StringBuffer();
      while (originMatcher.find()) {
        int x = Integer.parseInt(originMatcher.group(1));
        int y = Integer.parseInt(originMatcher.group(2));
        int z = Integer.parseInt(originMatcher.group(3));
        Coordinate3D referenced = new Coordinate3D(x, y, z);
        Coordinate3D relativeCoordinate = current.getCoordinate().mult(-1).plus(referenced);
        originMatcher.appendReplacement(originSb, relativeCoordinate.toRelativeString());
      }
      originMatcher.appendTail(originSb);
      current.setCommand(originSb.toString());
    }
  }

  private static boolean isInternal(MplBlock block) {
    if (block instanceof Transmitter) {
      Transmitter transmitter = (Transmitter) block;
      return transmitter.isInternal();
    }
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      Command command = commandBlock.toCommand();
      return command instanceof InternalCommand;
    }
    return false;
  }

  private static boolean isNop(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      Command command = commandBlock.toCommand();
      return command instanceof NoOperationCommand;
    }
    return false;
  }

}
