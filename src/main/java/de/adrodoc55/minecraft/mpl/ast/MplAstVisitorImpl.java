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
package de.adrodoc55.minecraft.mpl.ast;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.assertj.core.util.VisibleForTesting;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.PossiblyConditionalChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Conditional;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingTestforSuccessCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;

/**
 * @author Adrodoc55
 */
public class MplAstVisitorImpl implements MplAstVisitor {
  private ChainContainer container;
  private List<CommandChain> chains;
  @VisibleForTesting
  List<ChainLink> commands = new ArrayList<>();
  private CompilerOptions options;

  public MplAstVisitorImpl(CompilerOptions options) {
    this.options = checkNotNull(options, "options == null!");
  }

  public ChainContainer getResult() {
    return container;
  }

  @Override
  public void visitProgram(MplProgram program) {
    chains = new ArrayList<>(1);
    Orientation3D orientation = program.getOrientation();
    Coordinate3D max = program.getMax();

    program.getInstall().accept(this);
    CommandChain install = chains.get(chains.size() - 1);
    chains.remove(chains.size() - 1);

    program.getUninstall().accept(this);
    CommandChain uninstall = chains.get(chains.size() - 1);
    chains.remove(chains.size() - 1);

    chains = new ArrayList<>(program.getProcesses().size());
    for (MplProcess process : program.getProcesses()) {
      process.accept(this);
    }
    container = new ChainContainer(orientation, max, install, uninstall, chains);
  }

  @Override
  public void visitProcess(MplProcess process) {
    List<ChainPart> chainParts = process.getChainParts();
    if (chainParts.isEmpty()) {
      return;
    }
    commands = new ArrayList<>(chainParts.size());
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(false));
    }
    if (process.isRepeating()) {
      ChainPart first = chainParts.get(0);
      try {
        first.setMode(REPEAT);
      } catch (IllegalModifierException ex) {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    } else {
      if (options.hasOption(TRANSMITTER)) {
        commands.add(new InternalCommand("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      } else {
        commands.add(new InternalCommand("/entitydata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
      }
    }
    for (ChainPart chainPart : chainParts) {
      chainPart.accept(this);
    }
    chains.add(new CommandChain(process.getName(), commands));
  }

  protected void visitPossibleInvert(PossiblyConditionalChainPart chainPart) {
    if (chainPart.getConditional() == Conditional.INVERT) {
      ChainPart previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      commands.add(new InvertingCommand(previous.getModeForInverting()));
    }
  }

  @Override
  public void visitCommand(MplCommand command) {
    visitPossibleInvert(command);

    String cmd = command.getCommand();
    Mode mode = command.getMode();
    boolean conditional = command.isConditional();
    boolean needsRedstone = command.needsRedstone();
    commands.add(new Command(cmd, mode, conditional, needsRedstone));
  }

  @Override
  public void visitStart(MplStart start) {
    visitPossibleInvert(start);

    String process = start.getProcess();
    String command;
    if (options.hasOption(TRANSMITTER)) {
      command = "execute @e[name=" + process + "] ~ ~ ~ setblock ~ ~ ~ redstone_block";
    } else {
      command = "execute @e[name=" + process + "] ~ ~ ~ blockdata ~ ~ ~ {auto:1}";
    }
    commands.add(new Command(command, start.isConditional()));
  }

  @Override
  public void visitStop(MplStop stop) {
    visitPossibleInvert(stop);

    String process = stop.getProcess();
    String command;
    if (options.hasOption(TRANSMITTER)) {
      command = "execute @e[name=" + process + "] ~ ~ ~ setblock ~ ~ ~ stone";
    } else {
      command = "execute @e[name=" + process + "] ~ ~ ~ blockdata ~ ~ ~ {auto:0}";
    }
    commands.add(new Command(command, stop.isConditional()));
  }

  @Override
  public void visitWaitfor(MplWaitfor waitfor) {
    ReferencingCommand summon = new ReferencingCommand("summon ArmorStand " + REF + " {CustomName:"
        + waitfor.getEvent() + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

    if (waitfor.getConditional() == UNCONDITIONAL) {
      summon.setRelative(1);
      commands.add(summon);
    } else {
      summon.setConditional(true);
      ReferencingCommand jump = new ReferencingCommand("setblock " + REF + " redstone_block", true);
      if (waitfor.getConditional() == CONDITIONAL) {
        summon.setRelative(3);
        jump.setRelative(1);
        commands.add(summon);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(jump);
      } else { // conditional == INVERT
        jump.setRelative(3);
        summon.setRelative(1);
        commands.add(jump);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(summon);
      }
    }

    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(false));
      commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commands.add(new InternalCommand("blockdata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
    }
  }

  @Override
  public void visitNotify(MplNotify notify) {
    visitPossibleInvert(notify);

    String process = notify.getProcess();
    boolean conditional = notify.isConditional();
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new InternalCommand(
          "execute @e[name=" + process + NOTIFY + "] ~ ~ ~ setblock ~ ~ ~ redstone_block",
          conditional));
    } else {
      commands.add(new InternalCommand(
          "execute @e[name=" + process + NOTIFY + "] ~ ~ ~ blockdata ~ ~ ~ {auto:1}", conditional));
    }
    commands.add(new Command("kill @e[name=" + process + NOTIFY + "]", conditional));
  }

  @Override
  public void visitIntercept(MplIntercept intercept) {
    String event = intercept.getEvent();
    boolean conditional = intercept.isConditional();

    InternalCommand entitydata = new InternalCommand(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}", conditional);

    ReferencingCommand summon = new ReferencingCommand("summon ArmorStand " + REF + " {CustomName:"
        + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", conditional);


    if (intercept.getConditional() == UNCONDITIONAL) {
      summon.setRelative(1);
      commands.add(entitydata);
      commands.add(summon);
    } else {
      ReferencingCommand jump = new ReferencingCommand("setblock " + REF + " redstone_block", true);
      if (intercept.getConditional() == CONDITIONAL) {
        summon.setRelative(3);
        jump.setRelative(1);
        commands.add(entitydata);
        commands.add(summon);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(jump);
      } else { // conditional == INVERT
        jump.setRelative(4);
        summon.setRelative(1);
        commands.add(jump);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(entitydata);
        commands.add(summon);
      }
    }

    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(false));
      commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commands.add(new InternalCommand("blockdata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
    }
    commands.add(new InternalCommand("kill @e[name=" + event + ",r=2]"));
    commands.add(new InternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
  }

  @Override
  public void visitBreakpoint(MplBreakpoint breakpoint) {
    visitPossibleInvert(breakpoint);

    boolean cond = breakpoint.isConditional();
    commands.add(new InternalCommand("say " + breakpoint.getMessage(), cond));

    Conditional conditional = cond ? CONDITIONAL : UNCONDITIONAL;
    visitStart(new MplStart("breakpoint", conditional));
    visitWaitfor(new MplWaitfor("breakpoint" + NOTIFY, conditional));
  }

  @Override
  public void visitSkip(MplSkip skip) {
    commands.add(skip);
  }

  // private Deque<MplIf> ifNestingParents = new ArrayDeque<>();

  @Override
  public void visitIf(MplIf mplIf) {
    InternalCommand ref = new InternalCommand(mplIf.getCondition());
    commands.add(ref);
    if (needsNormalizer(mplIf)) {
      ref = new NormalizingCommand();
      commands.add(ref);
    }

    // then
    Deque<ChainPart> thenParts = mplIf.getThenParts();
    boolean emptyThen = thenParts.isEmpty();
    if (!mplIf.isNot() && !emptyThen) {
      // First then does not need a reference
      addWithoutRef(thenParts.pop());
    }
    addAllWithRef(thenParts, ref, !mplIf.isNot());

    // else
    Deque<ChainPart> elseParts = mplIf.getElseParts();
    boolean emptyElse = elseParts.isEmpty();
    if (mplIf.isNot() && emptyThen && !emptyElse) {
      // First else does not need a reference, if there is no then part
      addWithoutRef(elseParts.pop());
    }
    addAllWithRef(elseParts, ref, mplIf.isNot());
  }

  private void addAllWithRef(Deque<ChainPart> chainParts, InternalCommand ref, boolean success) {
    while (!chainParts.isEmpty()) {
      ChainPart chainPart = chainParts.pop();
      PossiblyConditionalChainPart casted = (PossiblyConditionalChainPart) chainPart;
      if (casted.getConditional() == INVERT) {
        visitPossibleInvert(casted);
        casted.setConditional(CONDITIONAL);
        int relative = getCountToRef(ref);
        commands.add(new ReferencingTestforSuccessCommand(relative, CHAIN, success, true));
      } else if (casted.getConditional() != CONDITIONAL) {
        int relative = getCountToRef(ref);
        commands.add(new ReferencingTestforSuccessCommand(relative, CHAIN, success));
      }
      // if (!success) {
      // addParentReferences();
      // }
      addWithoutRef(chainPart);
    }
  }

  private void addWithoutRef(ChainPart chainPart) {
    PossiblyConditionalChainPart casted = (PossiblyConditionalChainPart) chainPart;
    if (casted.getConditional() == UNCONDITIONAL) {
      casted.setConditional(CONDITIONAL);
    }
    chainPart.accept(this);
  }

  // private void addParentReferences() {
  // ifNestingParents.iterator();
  //
  // }

  private int getCountToRef(InternalCommand ref) {
    int lastIndex = -1;
    for (int i = commands.size() - 1; i >= 0; i--) {
      if (ref == commands.get(i)) {
        lastIndex = i;
        break;
      }
    }
    return -commands.size() + lastIndex;
  }

  public static boolean needsNormalizer(MplIf mplIf) {
    if (!mplIf.isNot()) {
      return containsConditionReference(mplIf.getThenParts());
    } else {
      if (!mplIf.getThenParts().isEmpty()) {
        if (!mplIf.getElseParts().isEmpty())
          return true;
        else
          return false;
      }
      return containsConditionReference(mplIf.getElseParts());
    }
  }

  private static boolean containsConditionReference(Iterable<ChainPart> iterable) {
    Iterator<ChainPart> it = iterable.iterator();
    if (it.hasNext()) {
      it.next(); // Ignore the first element.
    }
    while (it.hasNext()) {
      ChainPart chainPart = it.next();
      if (chainPart instanceof MplIf) {
        if (needsParentNormalizer((MplIf) chainPart)) {
          return true;
        }
      } else if (chainPart instanceof PossiblyConditionalChainPart) {
        PossiblyConditionalChainPart cp = (PossiblyConditionalChainPart) chainPart;
        if (!cp.isConditional()) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean needsParentNormalizer(MplIf mplIf) {
    Deque<ChainPart> chainParts;
    if (mplIf.isNot()) {
      chainParts = mplIf.getThenParts();
    } else {
      chainParts = mplIf.getElseParts();
    }
    return containsConditionReference(chainParts);
  }

}
