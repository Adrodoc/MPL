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
package de.adrodoc55.minecraft.mpl.materialize.process;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

public class MplProcessAstVisitorContext implements MplProcessAstVisitor.Context {
  private final MplProgram program;
  private final Deque<IfNestingLayer> ifNestingLayers = new ArrayDeque<>();
  private final Deque<MplWhile> loops = new ArrayDeque<>();
  private final Deque<LoopRef> loopRefs = new ArrayDeque<>();

  @GenerateMplPojoBuilder
  public MplProcessAstVisitorContext(MplProgram program) {
    this.program = checkNotNull(program, "program == null!");
  }

  @Override
  public void setBreakpoint(MplSource breakpoint) {}

  @Override
  public MplProgram getProgram() {
    return program;
  }

  @Override
  public Deque<IfNestingLayer> getIfNestingLayers() {
    return ifNestingLayers;
  }

  @Override
  public Deque<MplWhile> getLoops() {
    return loops;
  }

  @Override
  public Deque<LoopRef> getLoopRefs() {
    return loopRefs;
  }
}
