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

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.antlr.Include;
import de.adrodoc55.minecraft.mpl.antlr.MplBaseListener;
import de.adrodoc55.minecraft.mpl.antlr.MplInterpreter;
import de.adrodoc55.minecraft.mpl.chain_computing.IterativeChainComputer;

public class MplCompiler extends MplBaseListener {

  public static List<CommandBlockChain> compile(File programFile)
      throws IOException, CompilationFailedException {
    Program program = assembleProgram(programFile);
    List<CommandBlockChain> chains = computeChains(program);
    for (CommandBlockChain chain : chains) {
      insertRelativeCoordinates(chain.getCommandBlocks(), program.getOrientation());
    }
    return chains;
  }

  public static Program assembleProgram(File programFile)
      throws IOException, CompilationFailedException {
    MplCompiler compiler = new MplCompiler();
    Program program = compiler.assemble(programFile);
    if (!compiler.exceptions.isEmpty()) {
      throw new CompilationFailedException(compiler.exceptions);
    }
    return program;
  }

  private Map<File, List<CompilerException>> exceptions =
      new HashMap<File, List<CompilerException>>();
  private Map<File, Set<String>> programTree = new HashMap<File, Set<String>>();
  private Set<Include> includes;
  private LinkedList<Include> includeTodos;
  private Program program;

  private MplCompiler() {}

  private Program assemble(File programFile) throws IOException {
    includes = new HashSet<Include>();
    includeTodos = new LinkedList<Include>();
    program = new Program();
    program.setOrientation(new MplOrientation());
    MplInterpreter main = MplInterpreter.interpret(programFile);
    addInterpreter(main);
    doIncludes();
    return program;
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

  private void massInclude(Include include) {
    for (File file : include.getFiles()) {
      MplInterpreter interpreter = null;
      try {
        interpreter = MplInterpreter.interpret(file);
      } catch (IOException ex) {
        CompilerException compilerException = new CompilerException(include.getSrcFile(),
            include.getToken(), include.getSrcLine(), "Couldn't include '" + file + "'", ex);
        List<CompilerException> list = exceptions.get(include.getSrcFile());
        if (list == null) {
          list = new LinkedList<CompilerException>();
          exceptions.put(include.getSrcFile(), list);
        }
        list.add(compilerException);
        return;
      }
      if (interpreter.isProject()) {
        CompilerException compilerException =
            new CompilerException(include.getSrcFile(), include.getToken(), include.getSrcLine(),
                "Can't include Project " + include + ". Projects may not be included.");
        List<CompilerException> list = exceptions.get(include.getSrcFile());
        if (list == null) {
          list = new LinkedList<CompilerException>();
          exceptions.put(include.getSrcFile(), list);
        }
        list.add(compilerException);
        return;
      }
      addInterpreter(interpreter);
    }

  }

  private void processInclude(Include include) {
    String processName = include.getProcessName();
    Exception lastException = null;
    File found = null;
    Collection<File> files = include.getFiles();
    for (File file : files) {
      MplInterpreter interpreter = null;
      lastException = null;
      try {
        interpreter = MplInterpreter.interpret(file);
      } catch (Exception ex) {
        lastException = ex;
      }
      if (lastException != null) {
        continue;
      }
      List<CommandChain> chains = interpreter.getChains();
      for (CommandChain chain : chains) {
        if (processName.equals(chain.getName())) {
          if (found != null) {
            CompilerException compilerException = new CompilerException(include.getSrcFile(),
                include.getToken(), include.getSrcLine(), "Process " + processName
                    + " is ambigious. It was found in '" + found + "' and '" + file + "'");
            List<CompilerException> list = exceptions.get(include.getSrcFile());
            if (list == null) {
              list = new LinkedList<CompilerException>();
              exceptions.put(include.getSrcFile(), list);
            }
            list.add(compilerException);
            return;
          }
          found = file;
          addInterpreter(interpreter, processName);
        }
      }
    }
    if (found == null) {
      CompilerException compilerException =
          new CompilerException(include.getSrcFile(), include.getToken(), include.getSrcLine(),
              "Could not resolve process " + processName, lastException);
      List<CompilerException> list = exceptions.get(include.getSrcFile());
      if (list == null) {
        list = new LinkedList<CompilerException>();
        exceptions.put(include.getSrcFile(), list);
      }
      list.add(compilerException);
      return;
    }
  }

  private void addInterpreter(MplInterpreter interpreter) {
    addInterpreter(interpreter, null);
  }

  private void addInterpreter(MplInterpreter interpreter, String process) {
    File programFile = interpreter.getProgramFile();
    Set<String> alreadyIncluded = programTree.get(programFile);
    if (alreadyIncluded == null) {
      program.getInstallation().addAll(interpreter.getInstallation());
      program.getUninstallation().addAll(interpreter.getUninstallation());
      alreadyIncluded = new HashSet<String>();
      programTree.put(programFile, alreadyIncluded);
      if (!interpreter.getExceptions().isEmpty()) {
        exceptions.put(programFile, interpreter.getExceptions());
      }
    }

    for (CommandChain chain : interpreter.getChains()) {
      if (process == null || process.equals(chain.getName())) {
        if (alreadyIncluded.add(chain.getName())) {
          program.getChains().add(chain);
        }
      }
    }
    Map<String, List<Include>> includeMapping = interpreter.getIncludes();
    for (String key : includeMapping.keySet()) {
      if (process == null || process.equals(key)) {
        List<Include> includes = includeMapping.get(key);
        for (Include include : includes) {
          if (this.includes.add(include)) {
            includeTodos.add(include);
          }
        }
      }
    }
  }

  private static List<CommandBlockChain> computeChains(Program program) {
    LinkedList<CommandChain> chains = program.getChains();
    chains.sort((o1, o2) -> {
      return Integer.compare(o1.getCommands().size(), o2.getCommands().size()) * -1;
    });
    MplOrientation orientation = program.getOrientation();
    Direction a = orientation.getA();
    Direction b = orientation.getB();
    Direction c = orientation.getC(); // Avoid Axis of C statt Max?
    Coordinate3D cPos = c.toCoordinate();
    Coordinate3D bPos = b.toCoordinate();

    CommandChain first = chains.poll();
    Coordinate3D firstMax =
        a.toCoordinate().mult(Integer.MAX_VALUE).plus(b.toCoordinate().mult(Integer.MAX_VALUE));
    CommandBlockChain optimalFirst =
        new IterativeChainComputer().computeOptimalChain(first, firstMax);
    optimalFirst.move(c.toCoordinate());

    List<CommandBlockChain> materialised = new LinkedList<CommandBlockChain>();
    materialised.add(optimalFirst);

    int maxA = optimalFirst.getMax().get(a.getAxis());
    int maxB = optimalFirst.getMax().get(b.getAxis()) + 1;
    Coordinate3D max = a.toCoordinate().mult(maxA).plus(b.toCoordinate().mult(Integer.MAX_VALUE))
        .plus(c.toCoordinate().mult(0)); // FIXME: max
                                         // ist
                                         // inkorrekt
                                         // wenn
                                         // orientation
                                         // negativ

    IterativeChainComputer chainComputer = new IterativeChainComputer() {
      @Override
      protected int calculateCost(List<Coordinate3D> coordinates) {
        int maxB = 0;
        for (Coordinate3D coordinate : coordinates) {
          int deltaB = coordinate.get(b.getAxis());
          if (b.isNegative()) {
            deltaB *= -1;
          }
          maxB = Math.max(maxB, deltaB);
        }
        return maxB;
      }
    };

    int[] array = new int[chains.size()];
    for (CommandChain chain : chains) {
      CommandBlockChain optimal = chainComputer.computeOptimalChain(chain, max);
      materialised.add(optimal);
      int localMaxB = optimal.getMax().get(b.getAxis()) + 1;
      for (int x = 0; x < array.length; x++) {
        int localMax = array[x] + localMaxB;
        if (localMax <= maxB || array[x] == 0) {
          optimal.move(cPos.mult(x + 2).plus(bPos.mult(array[x])));
          array[x] = localMax;
          break;
        }
      }
    }

    addUnInstallation(program, materialised, orientation);
    return materialised;
  }

  private static void addUnInstallation(Program program, List<CommandBlockChain> materialised,
      MplOrientation orientation) {
    List<Command> installation = program.getInstallation();
    List<Command> uninstallation = program.getUninstallation();

    for (CommandBlockChain chain : materialised) {
      String name = chain.getName();
      if (name == null) {
        continue;
      }
      Coordinate3D chainStart = chain.getMin().minus(orientation.getA().toCoordinate());
      installation.add(new Command(
          "/summon ArmorStand ${origin + (" + chainStart.toAbsoluteString() + ")} {CustomName:\""
              + chain.getName() + "\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
      uninstallation.add(new Command("/kill @e[type=ArmorStand,name=" + chain.getName() + "]"));
    }

    if (!installation.isEmpty() || !uninstallation.isEmpty()) {
      installation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }
    if (!uninstallation.isEmpty()) {
      uninstallation.add(0, new Command("/setblock ${this - 1} stone", Mode.IMPULSE, false));
    }

    CommandBlockChain materialisedUninstallation = new IterativeChainComputer().computeOptimalChain(
        new CommandChain("uninstallation", uninstallation), new Coordinate3D(100, 100, 0));
    materialisedUninstallation.move(Coordinate3D.UP);
    CommandBlockChain materialisedInstallation = new IterativeChainComputer() {
      protected boolean isCoordinateValid(Coordinate3D coordinate) {
        if (!super.isCoordinateValid(coordinate)) {
          return false;
        }
        List<CommandBlock> commandBlocks = materialisedUninstallation.getCommandBlocks();
        for (CommandBlock block : commandBlocks) {
          if (block.getCoordinate().equals(coordinate)) {
            return false;
          }
        }
        return true;
      };
    }.computeOptimalChain(new CommandChain("installation", installation),
        new Coordinate3D(100, 100, 0));
    if (!materialisedInstallation.getCommandBlocks().isEmpty()) {
      materialised.add(materialisedInstallation);
    }
    if (!materialisedUninstallation.getCommandBlocks().isEmpty()) {
      materialised.add(materialisedUninstallation);
    }
  }

  private static final Pattern thisPattern =
      Pattern.compile("\\$\\{\\s*this\\s*([+-])\\s*(\\d+)\\s*\\}");

  private static final Pattern originPattern = Pattern
      .compile("\\$\\{\\s*origin\\s*\\+\\s*\\(\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s*\\)\\s*\\}");

  private static void insertRelativeCoordinates(List<CommandBlock> commandBlocks,
      MplOrientation orientation) {
    for (int i = 0; i < commandBlocks.size(); i++) {
      CommandBlock current = commandBlocks.get(i);
      if (current.toCommand() == null) {
        continue;
      }

      if (current != null) {
        Matcher thisMatcher = thisPattern.matcher(current.getCommand());
        StringBuffer thisSb = new StringBuffer();
        while (thisMatcher.find()) {
          boolean minus = thisMatcher.group(1).equals("-");
          int relative = Integer.parseInt(thisMatcher.group(2));
          if (minus) {
            relative *= -1;
          }
          int index = i + relative;
          Coordinate3D referenced;
          if (index < 0) {
            referenced =
                commandBlocks.get(0).getCoordinate().minus(orientation.getA().toCoordinate());
          } else {
            referenced = commandBlocks.get(index).getCoordinate();
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
  }

}
