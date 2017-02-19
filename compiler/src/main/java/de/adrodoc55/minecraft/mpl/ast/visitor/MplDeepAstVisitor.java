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
package de.adrodoc55.minecraft.mpl.ast.visitor;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Collections2;

import de.adrodoc55.commons.collections.Collections;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.InternalMplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;

/**
 * Adrodoc55
 */
public abstract class MplDeepAstVisitor<T> implements MplAstVisitor<T> {
  protected abstract T visit(ChainPart chainPart);

  protected abstract T toResult(Collection<T> results);

  public Collection<T> visit(Collection<ChainPart> chainParts) {
    return Collections2.transform(chainParts, cp -> cp.accept(this));
  }

  @Override
  public T visitInternalCommand(InternalMplCommand mplCommand) {
    return visit(mplCommand);
  }

  @Override
  public T visitCommand(MplCommand mplCommand) {
    return visit(mplCommand);
  }

  @Override
  public T visitCall(MplCall mplCall) {
    return visit(mplCall);
  }

  @Override
  public T visitStart(MplStart mplStart) {
    return visit(mplStart);
  }

  @Override
  public T visitStop(MplStop mplStop) {
    return visit(mplStop);
  }

  @Override
  public T visitWaitfor(MplWaitfor mplWaitfor) {
    return visit(mplWaitfor);
  }

  @Override
  public T visitNotify(MplNotify mplNotify) {
    return visit(mplNotify);
  }

  @Override
  public T visitIntercept(MplIntercept mplIntercept) {
    return visit(mplIntercept);
  }

  @Override
  public T visitBreakpoint(MplBreakpoint mplBreakpoint) {
    return visit(mplBreakpoint);
  }

  @Override
  public T visitSkip(MplSkip mplSkip) {
    return visit(mplSkip);
  }

  @Override
  public T visitIf(MplIf mplIf) {
    Collection<T> a = Arrays.asList(visit(mplIf));
    Collection<T> b = visit(mplIf.getThenParts());
    Collection<T> c = visit(mplIf.getElseParts());
    return toResult(Collections.concat(a, b, c));
  }

  @Override
  public T visitWhile(MplWhile mplWhile) {
    Collection<T> a = Arrays.asList(visit(mplWhile));
    Collection<T> b = visit(mplWhile.getChainParts());
    return toResult(Collections.concat(a, b));
  }

  @Override
  public T visitBreak(MplBreak mplBreak) {
    return visit(mplBreak);
  }

  @Override
  public T visitContinue(MplContinue mplContinue) {
    return visit(mplContinue);
  }
}
