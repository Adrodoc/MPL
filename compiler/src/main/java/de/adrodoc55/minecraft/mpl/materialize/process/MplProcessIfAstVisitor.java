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

import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import de.adrodoc55.commons.collections.Lists;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.InternalMplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
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
import de.adrodoc55.minecraft.mpl.ast.visitor.IfNestingLayer;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;

/**
 * @author Adrodoc55
 */
public class MplProcessIfAstVisitor extends MplProcessAstVisitor {
  private boolean firstChainPart = true;

  public MplProcessIfAstVisitor(MplCompilerContext compilerContext,
      MplProcessAstVisitor.Context context) {
    super(compilerContext, context);
  }

  private List<ChainLink> visitModifiableChainPart(ModifiableChainPart chainPart) {
    List<ChainLink> result = new ArrayList<>();
    addInvertingCommandIfInvert(result, chainPart);
    if (chainPart.getConditional() != CONDITIONAL) {
      IfNestingLayer currentLayer = context.getIfNestingLayers().getFirst();
      if (!firstChainPart || currentLayer.dependsOnFailure()) {
        result.addAll(getConditionReferences(chainPart));
      }
    }
    chainPart.setConditional(CONDITIONAL);
    firstChainPart = false;
    return result;
  }

  /**
   * Returns references to all required {@link MplIf}s. If the {@link ChainPart} depends on the
   * parent's failure a reference to the grandparent is also added. This method will add parent
   * references, until the root is reached or until a layer depends on it's parent's success rather
   * that failure.
   *
   * @return references to all required {@link MplIf}s
   */
  private List<ChainLink> getConditionReferences(ModifiableChainPart chainPart) {
    Deque<IfNestingLayer> requiredReferences = new ArrayDeque<>();
    for (IfNestingLayer layer : context.getIfNestingLayers()) {
      requiredReferences.push(layer);
      if (!layer.dependsOnFailure()) {
        break;
      }
    }
    List<ChainLink> result = new ArrayList<>(requiredReferences.size());
    if (chainPart.getConditional() == UNCONDITIONAL) {
      IfNestingLayer first = requiredReferences.pop();
      result.add(first.newConditionReference());
    }
    for (IfNestingLayer layer : requiredReferences) {
      Command reference = layer.newConditionReference();
      reference.setConditional(true);
      result.add(reference);
    }
    return result;
  }

  @Override
  public List<ChainLink> visitInternalCommand(InternalMplCommand mplCommand) {
    return Lists.concat(visitModifiableChainPart(mplCommand),
        super.visitInternalCommand(mplCommand));
  }

  @Override
  public List<ChainLink> visitCommand(MplCommand mplCommand) {
    return Lists.concat(visitModifiableChainPart(mplCommand), super.visitCommand(mplCommand));
  }

  @Override
  public List<ChainLink> visitCall(MplCall mplCall) {
    return Lists.concat(visitModifiableChainPart(mplCall), super.visitCall(mplCall));
  }

  @Override
  public List<ChainLink> visitStart(MplStart mplStart) {
    return Lists.concat(visitModifiableChainPart(mplStart), super.visitStart(mplStart));
  }

  @Override
  public List<ChainLink> visitStop(MplStop mplStop) {
    return Lists.concat(visitModifiableChainPart(mplStop), super.visitStop(mplStop));
  }

  @Override
  public List<ChainLink> visitWaitfor(MplWaitfor mplWaitfor) {
    return Lists.concat(visitModifiableChainPart(mplWaitfor), super.visitWaitfor(mplWaitfor));
  }

  @Override
  public List<ChainLink> visitNotify(MplNotify mplNotify) {
    return Lists.concat(visitModifiableChainPart(mplNotify), super.visitNotify(mplNotify));
  }

  @Override
  public List<ChainLink> visitIntercept(MplIntercept mplIntercept) {
    return Lists.concat(visitModifiableChainPart(mplIntercept), super.visitIntercept(mplIntercept));
  }

  @Override
  public List<ChainLink> visitBreakpoint(MplBreakpoint mplBreakpoint) {
    return Lists.concat(visitModifiableChainPart(mplBreakpoint),
        super.visitBreakpoint(mplBreakpoint));
  }

  @Override
  public List<ChainLink> visitSkip(MplSkip skip) {
    throw new IllegalStateException("If cannot contain " + skip.getName());
  }

  @Override
  public List<ChainLink> visitIf(MplIf mplIf) {
    return Lists.concat(visitModifiableChainPart(mplIf), super.visitIf(mplIf));
  }

  @Override
  public List<ChainLink> visitWhile(MplWhile mplWhile) {
    return Lists.concat(visitModifiableChainPart(mplWhile), super.visitWhile(mplWhile));
  }

  @Override
  public List<ChainLink> visitBreak(MplBreak mplBreak) {
    return Lists.concat(visitModifiableChainPart(mplBreak), super.visitBreak(mplBreak));
  }

  @Override
  public List<ChainLink> visitContinue(MplContinue mplContinue) {
    return Lists.concat(visitModifiableChainPart(mplContinue), super.visitContinue(mplContinue));
  }
}
