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

import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingTestforSuccessCommand;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
public class MplIfVisitorBackup extends MplAstVisitorImpl {
  public MplIfVisitorBackup(MplCompilerContext context) {
    super(context);
  }

  @RequiredArgsConstructor
  @Getter
  @Setter
  private static class IfNestingLayer {
    private final boolean not;
    private final @Nonnull Command ref;
    private boolean inElse;
  }

  private Deque<IfNestingLayer> ifNestingLayers = new ArrayDeque<>();

  @Override
  public List<ChainLink> visitIf(MplIf mplIf) {
    List<ChainLink> result = new ArrayList<>();
    visitPossibleInvert(result, mplIf);

    String condition = mplIf.getCondition();
    Command ref;
    if (condition != null) {
      ref = new InternalCommand(condition, mplIf);
      result.add(ref);
    } else {
      ref = (Command) result.get(result.size() - 1);
    }
    if (needsNormalizer(mplIf)) {
      ref = new NormalizingCommand();
      result.add(ref);
    }
    IfNestingLayer layer = new IfNestingLayer(mplIf.isNot(), ref);
    ifNestingLayers.push(layer);

    // then
    layer.setInElse(false);
    Deque<ChainPart> thenParts = mplIf.getThenParts();
    boolean emptyThen = thenParts.isEmpty();
    if (!mplIf.isNot() && !emptyThen) {
      // First then does not need a reference
      addAsConditional(thenParts.pop());
    }
    addAllWithRef(thenParts);

    // else
    layer.setInElse(true);
    Deque<ChainPart> elseParts = mplIf.getElseParts();
    boolean emptyElse = elseParts.isEmpty();
    if (mplIf.isNot() && emptyThen && !emptyElse) {
      // First else does not need a reference, if there is no then part
      addAsConditional(elseParts.pop());
    }
    addAllWithRef(elseParts);

    ifNestingLayers.pop();
    return result;
  }

  private void addAllWithRef(Iterable<ChainPart> chainParts) {
    for (ChainPart chainPart : chainParts) {
      addWithRef(cast(chainPart));
    }
  }

  private void addWithRef(ModifiableChainPart chainPart) {
    visitPossibleInvert(chainPart);
    if (chainPart.getConditional() != CONDITIONAL) {
      addConditionReferences(chainPart);
    }
    addAsConditional(chainPart);
  }

  /**
   * Add's all references to required {@link MplIf}s. If the chainPart depends on the parent's
   * failure a reference to the grandparent is also added. This method is recursive and will add
   * parent references, until the root is reached or until a layer depends on it's parent's success
   * rather that failure.
   */
  private void addConditionReferences(ModifiableChainPart chainPart) {
    Deque<IfNestingLayer> requiredReferences = new ArrayDeque<>();
    for (IfNestingLayer layer : ifNestingLayers) {
      requiredReferences.push(layer);
      boolean dependingOnFailure = layer.isNot() ^ layer.isInElse();
      if (!dependingOnFailure) {
        break;
      }
    }
    if (chainPart.getConditional() == UNCONDITIONAL) {
      IfNestingLayer first = requiredReferences.pop();
      result.add(getConditionReference(first));
    }
    for (IfNestingLayer layer : requiredReferences) {
      ReferencingTestforSuccessCommand ref = getConditionReference(layer);
      ref.setConditional(true);
      result.add(ref);
    }
  }

  private ReferencingTestforSuccessCommand getConditionReference(IfNestingLayer layer) {
    Command ref = layer.getRef();
    int relative = getCountToRef(ref);
    boolean dependingOnFailure = layer.isNot() ^ layer.isInElse();
    return new ReferencingTestforSuccessCommand(relative, ref.getMode(), !dependingOnFailure);
  }

  private void addAsConditional(ChainPart chainPart) {
    cast(chainPart).setConditional(CONDITIONAL);
    chainPart.accept(this);
  }

  private static ModifiableChainPart cast(ChainPart chainPart) {
    try {
      return (ModifiableChainPart) chainPart;
    } catch (ClassCastException ex) {
      throw new IllegalStateException("If cannot contain skip", ex);
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
      ChainPart first = it.next(); // Ignore the first element.
      if (first instanceof MplIf) {
        it = iterable.iterator(); // Only if it is not a nested if
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

}
