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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
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
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
public class MplIfVisitor extends MplBaseAstVisitor {
  private final MplAstVisitor delegate;

  public MplIfVisitor(MplAstVisitor delegate, CompilerOptions options) {
    super(options);
    this.delegate = checkNotNull(delegate, "delegate == null!");
  }

  @RequiredArgsConstructor
  @Getter
  @Setter
  private static class IfNestingLayer {
    private final boolean not;
    private final @Nonnull Command ref;
    private boolean inElse;

    public boolean isDependingOnFailure() {
      return not ^ inElse;
    }
  }

  private Deque<IfNestingLayer> ifNestingLayers = new ArrayDeque<>();

  @Override
  public List<ChainLink> visitIf(MplIf mplIf) {
    List<ChainLink> result = new ArrayList<>();
    addInvertingCommandIfInvert(result, mplIf);

    Command ref = new Command(mplIf.getCondition(), mplIf);
    result.add(ref);
    if (needsNormalizer(mplIf)) {
      ref = newNormalizingCommand();
      result.add(ref);
    }
    IfNestingLayer layer = new IfNestingLayer(mplIf.isNot(), ref);
    ifNestingLayers.push(layer);

    // then
    layer.setInElse(false);
    Deque<ChainPart> thenParts = new ArrayDeque<>(mplIf.getThenParts());
    boolean emptyThen = thenParts.isEmpty();
    if (!mplIf.isNot() && !emptyThen) {
      // First then does not need a reference
      result.addAll(getAsConditional(thenParts.pop()));
    }
    result.addAll(getAllWithRef(thenParts));

    // else
    layer.setInElse(true);
    Deque<ChainPart> elseParts = new ArrayDeque<>(mplIf.getElseParts());
    boolean emptyElse = elseParts.isEmpty();
    if (mplIf.isNot() && emptyThen && !emptyElse) {
      // First else does not need a reference, if there is no then part
      result.addAll(getAsConditional(elseParts.pop()));
    }
    result.addAll(getAllWithRef(elseParts));

    ifNestingLayers.pop();
    return resolveReferences(result);
  }

  private List<ChainLink> getAllWithRef(Iterable<ChainPart> chainParts) {
    List<ChainLink> result = new ArrayList<>();
    for (ChainPart chainPart : chainParts) {
      result.addAll(getWithRef(cast(chainPart)));
    }
    return result;
  }

  private List<ChainLink> getWithRef(ModifiableChainPart chainPart) {
    List<ChainLink> result = new ArrayList<>();
    addInvertingCommandIfInvert(result, chainPart);
    if (chainPart.getConditional() != CONDITIONAL) {
      result.addAll(getConditionReferences(chainPart));
    }
    result.addAll(getAsConditional(chainPart));
    return result;
  }

  /**
   * Add's all references to required {@link MplIf}s. If the chainPart depends on the parent's
   * failure a reference to the grandparent is also added. This method is recursive and will add
   * parent references, until the root is reached or until a layer depends on it's parent's success
   * rather that failure.
   *
   * @return
   */
  private List<ChainLink> getConditionReferences(ModifiableChainPart chainPart) {
    Deque<IfNestingLayer> requiredReferences = new ArrayDeque<>();
    for (IfNestingLayer layer : ifNestingLayers) {
      requiredReferences.push(layer);
      boolean dependingOnFailure = layer.isNot() ^ layer.isInElse();
      if (!dependingOnFailure) {
        break;
      }
    }
    List<ChainLink> result = new ArrayList<>(requiredReferences.size());
    if (chainPart.getConditional() == UNCONDITIONAL) {
      IfNestingLayer first = requiredReferences.pop();
      result.add(getConditionReference(first));
    }
    for (IfNestingLayer layer : requiredReferences) {
      Command ref = getConditionReference(layer);
      ref.setConditional(true);
      result.add(ref);
    }
    return result;
  }

  private Command getConditionReference(IfNestingLayer layer) {
    Command referenced = layer.getRef();
    boolean success = !layer.isDependingOnFailure();
    return newTestforSuccessCommand(referenced, success);
  }

  private List<ChainLink> getAsConditional(ChainPart chainPart) {
    cast(chainPart).setConditional(CONDITIONAL);
    return chainPart.accept(this);
  }

  private static ModifiableChainPart cast(ChainPart chainPart) {
    try {
      return (ModifiableChainPart) chainPart;
    } catch (ClassCastException ex) {
      throw new IllegalStateException("If cannot contain " + chainPart.getName(), ex);
    }
  }

  public static boolean needsNormalizer(MplIf mplIf) {
    if (!mplIf.isNot()) {
      return containsConditionReferenceIgnoringFirstNonIf(mplIf.getThenParts());
    } else {
      if (!mplIf.getThenParts().isEmpty()) {
        if (!mplIf.getElseParts().isEmpty())
          return true;
        else
          return false;
      }
      return containsConditionReferenceIgnoringFirstNonIf(mplIf.getElseParts());
    }
  }

  private static boolean containsConditionReferenceIgnoringFirstNonIf(
      Iterable<ChainPart> iterable) {
    Iterator<ChainPart> it = iterable.iterator();
    if (it.hasNext()) {
      ChainPart first = it.next(); // Ignore the first element ...
      if (first instanceof MplIf) {
        it = iterable.iterator(); // ... only if it is not a nested if
      }
    }
    return containsConditionReference(it);
  }

  private static boolean containsConditionReference(Iterator<ChainPart> it) {
    while (it.hasNext()) {
      ChainPart chainPart = it.next();
      if (chainPart instanceof MplIf) {
        if (needsParentNormalizer((MplIf) chainPart)) {
          return true;
        }
      } else if (chainPart instanceof ModifiableChainPart) {
        ModifiableChainPart cp = (ModifiableChainPart) chainPart;
        if (!cp.isConditional()) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean needsParentNormalizer(MplIf mplIf) {
    if (mplIf.isNot()) {
      return containsConditionReference(mplIf.getThenParts().iterator());
    } else {
      return containsConditionReference(mplIf.getElseParts().iterator());
    }
  }

  // Delegate Methods

  public List<ChainLink> visitCommand(MplCommand command) {
    return delegate.visitCommand(command);
  }

  public List<ChainLink> visitCall(MplCall mplCall) {
    return delegate.visitCall(mplCall);
  }

  public List<ChainLink> visitStart(MplStart start) {
    return delegate.visitStart(start);
  }

  public List<ChainLink> visitStop(MplStop stop) {
    return delegate.visitStop(stop);
  }

  public List<ChainLink> visitWaitfor(MplWaitfor waitfor) {
    return delegate.visitWaitfor(waitfor);
  }

  public List<ChainLink> visitNotify(MplNotify notify) {
    return delegate.visitNotify(notify);
  }

  public List<ChainLink> visitIntercept(MplIntercept intercept) {
    return delegate.visitIntercept(intercept);
  }

  public List<ChainLink> visitBreakpoint(MplBreakpoint breakpoint) {
    return delegate.visitBreakpoint(breakpoint);
  }

  public List<ChainLink> visitSkip(MplSkip skip) {
    return delegate.visitSkip(skip);
  }

  public List<ChainLink> visitWhile(MplWhile mplWhile) {
    return delegate.visitWhile(mplWhile);
  }

  public List<ChainLink> visitBreak(MplBreak mplBreak) {
    return delegate.visitBreak(mplBreak);
  }

  public List<ChainLink> visitContinue(MplContinue mplContinue) {
    return delegate.visitContinue(mplContinue);
  }

}
