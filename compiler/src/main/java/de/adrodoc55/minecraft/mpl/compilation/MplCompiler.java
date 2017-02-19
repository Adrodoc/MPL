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
package de.adrodoc55.minecraft.mpl.compilation;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.assembly.MplProgramAssemler;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplMainAstVisitor;
import de.adrodoc55.minecraft.mpl.blocks.CommandBlock;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.blocks.Transmitter;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NoOperationCommand;
import de.adrodoc55.minecraft.mpl.placement.MplDebugProgramPlacer;
import de.adrodoc55.minecraft.mpl.placement.MplProgramPlacer;
import de.adrodoc55.minecraft.mpl.placement.NotEnoughSpaceException;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class MplCompiler {
  public static MplCompilationResult compile(File programFile, MinecraftVersion version,
      CompilerOptions options) throws IOException, CompilationFailedException {
    return new MplCompiler(version, options).compile(programFile);
  }

  private final MinecraftVersion version;
  private final CompilerOptions options;
  private MplCompilerContext context;

  public MplCompiler(MinecraftVersion version, CompilerOptions options) {
    this.version = version;
    this.options = options;
  }

  public MplCompilerContext provideContext() {
    if (context == null) {
      context = new MplCompilerContext(version, options);
    }
    return context;
  }

  protected void resetContext() {
    context = null;
  }

  public MplCompilationResult compile(File programFile)
      throws IOException, CompilationFailedException {
    resetContext();
    MplProgram program = assemble(programFile);
    checkErrors();
    targetThisInserts(program);
    checkErrors();
    ChainContainer container = materialize(program);
    checkErrors();
    List<CommandBlockChain> chains = place(container);
    Orientation3D orientation = program.getOrientation();
    insertRelativeCoordinates2(orientation, chains);
    insertRelativeCoordinates(orientation, chains);
    return toResult(orientation, chains);
  }

  /**
   * Checks if the {@link #context} contains any {@link CompilerException}s. If it does, this method
   * throws a new {@link CompilationFailedException}.
   *
   * @throws CompilationFailedException if at least one {@link CompilerException} ocurred
   */
  protected void checkErrors() throws CompilationFailedException {
    Set<CompilerException> errors = provideContext().getErrors();
    if (!errors.isEmpty()) {
      throw new CompilationFailedException(errors);
    }
  }

  public MplProgram assemble(File programFile) throws IOException {
    return new MplProgramAssemler(provideContext()).assemble(programFile);
  }

  public void targetThisInserts(MplProgram program) {
    for (MplProcess process : program.getAllProcesses()) {
      Deque<ChainPart> chainParts = process.getChainParts();
      for (ChainPart cp : chainParts) {
        cp.targetThisInserts(chainParts);
      }
    }
  }

  public ChainContainer materialize(MplProgram program) {
    return new MplMainAstVisitor(provideContext()).visitProgram(program);
  }

  public List<CommandBlockChain> place(ChainContainer container) throws CompilationFailedException {
    try {
      if (options.hasOption(DEBUG)) {
        return new MplDebugProgramPlacer(container, version, options).place();
      } else {
        return new MplProgramPlacer(container, version, options).place();
      }
    } catch (NotEnoughSpaceException ex) {
      throw new CompilationFailedException(
          "The maximal coordinate is to small to place the entire program", ex);
    }
  }

  public void insertRelativeCoordinates2(Orientation3D orientation,
      Iterable<CommandBlockChain> chains) {
    for (CommandBlockChain chain : chains) {
      List<MplBlock> blocks = chain.getBlocks();
      for (MplBlock block : blocks) {
        ChainPart chainPart = block.getChainPart();
        chainPart.resolveThisInserts(blocks);
      }
    }
  }

  @Deprecated
  protected static void insertRelativeCoordinates(Orientation3D orientation,
      Iterable<CommandBlockChain> chains) {
    for (CommandBlockChain chain : chains) {
      insertRelativeCoordinates(orientation, chain.getBlocks());
    }
  }

  public MplCompilationResult toResult(Orientation3D orientation, List<CommandBlockChain> chains) {
    List<MplBlock> blocks = chains.stream()//
        .flatMap(c -> c.getBlocks().stream())//
        .collect(toList());

    ImmutableMap<Coordinate3D, MplBlock> result = Maps.uniqueIndex(blocks, b -> b.getCoordinate());
    return new MplCompilationResult(orientation, result, context.getWarnings());
  }

  private static final Pattern thisPattern =
      Pattern.compile("\\$\\{\\s*this\\s*([+-])\\s*(\\d+)\\s*\\}");

  private static final String numberPattern = "-?\\d+(?:\\.\\d+)?";
  private static final Pattern originPattern =
      Pattern.compile("\\$\\{\\s*origin\\s*(?:\\+\\s*\\(\\s*(" + numberPattern + ")\\s+("
          + numberPattern + ")\\s+(" + numberPattern + ")\\s*\\)\\s*)?\\}");

  @Deprecated
  private static void insertRelativeCoordinates(Orientation3D orientation, List<MplBlock> blocks) {
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
        Coordinate3D relativeCoordinate = current.getCoordinate().mult(-1);
        if (originMatcher.group(1) != null) {
          double x = Double.parseDouble(originMatcher.group(1));
          double y = Double.parseDouble(originMatcher.group(2));
          double z = Double.parseDouble(originMatcher.group(3));
          Coordinate3D referenced = new Coordinate3D(x, y, z);
          relativeCoordinate = relativeCoordinate.plus(referenced);
        }
        originMatcher.appendReplacement(originSb, relativeCoordinate.toRelativeString());
      }
      originMatcher.appendTail(originSb);
      current.setCommand(originSb.toString());
    }
  }

  @Deprecated
  private static boolean isInternal(MplBlock block) {
    if (block instanceof Transmitter) {
      Transmitter transmitter = (Transmitter) block;
      return transmitter.isInternal();
    }
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      Command command = commandBlock.toCommand();
      return command.isInternal();
    }
    return false;
  }

  @Deprecated
  private static boolean isNop(MplBlock block) {
    if (block instanceof CommandBlock) {
      CommandBlock commandBlock = (CommandBlock) block;
      Command command = commandBlock.toCommand();
      return command instanceof NoOperationCommand;
    }
    return false;
  }

}
