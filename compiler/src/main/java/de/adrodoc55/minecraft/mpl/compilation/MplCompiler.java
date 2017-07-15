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
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.assembly.MplProgramAssemler;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.blocks.MplBlock;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.materialize.process.MplProcessMaterializer;
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
    ChainContainer container = materialize(program);
    checkErrors();
    List<CommandBlockChain> chains = place(container);
    return toResult(container.getOrientation(), chains);
  }

  /**
   * Checks if the {@link #context} contains any {@link CompilerException}s. If it does, this method
   * throws a new {@link CompilationFailedException}.
   *
   * @throws CompilationFailedException if at least one {@link CompilerException} ocurred
   */
  protected void checkErrors() throws CompilationFailedException {
    MplCompilerContext context = provideContext();
    Set<CompilerException> errors = context.getErrors();
    if (!errors.isEmpty()) {
      throw new CompilationFailedException(errors, context.getWarnings());
    }
  }

  public MplProgram assemble(File programFile) throws IOException {
    return new MplProgramAssemler(provideContext()).assemble(programFile);
  }

  public ChainContainer materialize(MplProgram program) {
    return new MplProcessMaterializer(provideContext()).materialize(program);
  }

  public List<CommandBlockChain> place(ChainContainer container) throws CompilationFailedException {
    try {
      List<CommandBlockChain> result;
      if (options.hasOption(DEBUG)) {
        result = new MplDebugProgramPlacer(container, version, options).place();
      } else {
        result = new MplProgramPlacer(container, version, options).place();
      }
      insertRelativeCoordinates(container.getOrientation(), result);
      return result;
    } catch (NotEnoughSpaceException ex) {
      throw new CompilationFailedException(
          "The maximal coordinate is to small to place the entire program", ex);
    }
  }

  protected void insertRelativeCoordinates(Orientation3D orientation,
      Iterable<CommandBlockChain> chains) {
    for (CommandBlockChain chain : chains) {
      List<MplBlock> blocks = chain.getBlocks();
      for (MplBlock block : blocks) {
        block.resolveThisInserts(blocks);
        block.resolveOriginInserts();
      }
    }
  }

  public MplCompilationResult toResult(Orientation3D orientation, List<CommandBlockChain> chains) {
    List<MplBlock> blocks = chains.stream()//
        .flatMap(c -> c.getBlocks().stream())//
        .collect(toList());

    ImmutableMap<Coordinate3D, MplBlock> result = Maps.uniqueIndex(blocks, b -> b.getCoordinate());
    return new MplCompilationResult(orientation, result, context.getWarnings());
  }
}
