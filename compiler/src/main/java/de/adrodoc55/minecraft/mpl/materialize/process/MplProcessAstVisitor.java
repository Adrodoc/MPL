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
import static com.google.common.base.Preconditions.checkState;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink.resolveAllTargetedThisInserts;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInternalCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckReturnValue;

import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.MplNode;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
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
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.ast.visitor.ContainsMatchVisitor;
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ProcessCommandsHelper;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import de.adrodoc55.minecraft.mpl.interpretation.insert.TargetedThisInsert;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class MplProcessAstVisitor extends ProcessCommandsHelper
    implements MplAstVisitor<List<ChainLink>> {
  public interface Context {
    MplProgram getProgram();

    void setBreakpoint(MplSource breakpoint);

    Deque<IfNestingLayer> getIfNestingLayers();

    Deque<MplWhile> getLoops();

    Deque<LoopRef> getLoopRefs();
  }

  protected final Context context;
  protected final MplCompilerContext compilerContext;

  public MplProcessAstVisitor(MplCompilerContext compilerContext, Context context) {
    super(compilerContext.getOptions());
    this.compilerContext = checkNotNull(compilerContext, "compilerContext == null!");
    this.context = checkNotNull(context, "context == null!");
  }

  public List<ChainLink> visitIgnoringWarnings(MplNode mplNode) {
    try {
      compilerContext.setIgnoreWarnings(true);
      return mplNode.accept(this);
    } finally {
      compilerContext.setIgnoreWarnings(false);
    }
  }

  /**
   * Checks if the given {@link ModifiableChainPart} has the {@link Conditional#INVERT INVERT}
   * modifier. If it does, an {@link Commands#newInvertingCommand inverting command} is added to
   * {@code commands}. If {@code chainPart} does not have predecessor an
   * {@link IllegalStateException} is thrown.
   *
   * @param commands the list to add to
   * @param chainPart the {@link ModifiableChainPart} to check
   * @throws IllegalStateException if {@code chainPart} does not have predecessor
   * @see ModifiableChainPart#getPrevious()
   */
  protected void addInvertingCommandIfInvert(List<? super Command> commands,
      ModifiableChainPart chainPart) throws IllegalStateException {
    if (chainPart.getConditional() == Conditional.INVERT) {
      Dependable previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      commands.add(newInvertingCommand(previous));
    }
  }

  /**
   * Checks if a process with the specified {@code processName} is part of the program. If there is
   * such a process, this method returns {@code true}, otherwise it returns {@code false} and adds a
   * compiler warning. In the special case that {@code processName == "breakpoint"} this method
   * returns false, but does not add a warning.
   *
   * @param chainpart where to display the warning
   * @param processName the required process
   * @return {@code true} if the process was found, {@code false} otherwise
   */
  private boolean checkProcessExists(ModifiableChainPart chainpart, String processName) {
    checkNotNull(processName, "processName == null!");
    if (!context.getProgram().containsProcess(processName)) {
      if (!"breakpoint".equals(processName)) {
        compilerContext.addWarning(new CompilerException(chainpart.getSource(),
            "Could not resolve process " + processName));
      }
      return false;
    }
    return true;
  }

  /**
   * Checks if the process with the specified {@code processName} is an inline process. If there is
   * such a process and it is inline, this method returns true, otherwise returns false and adds a
   * compiler exception.
   *
   * @param chainpart
   * @param processName
   * @return
   */
  private boolean checkNotInlineProcess(ModifiableChainPart chainpart, String processName) {
    checkNotNull(processName, "processName == null!");
    MplProcess process = context.getProgram().getProcess(processName);
    if (process != null && process.getType() == ProcessType.INLINE) {
      compilerContext.addError(new CompilerException(chainpart.getSource(),
          "Cannot " + chainpart.getName() + " an inline process"));
      return false;
    }
    return true;
  }

  @Override
  public List<ChainLink> visitInternalCommand(InternalMplCommand mplCommand) {
    return mplCommand.getChainLinks();
  }

  @Override
  public List<ChainLink> visitCommand(MplCommand mplCommand) {
    List<ChainLink> result = new ArrayList<>(2);
    addInvertingCommandIfInvert(result, mplCommand);

    CommandPartBuffer cmd = mplCommand.getMinecraftCommand();
    result.add(newCommand(cmd, mplCommand));
    return result;
  }

  @Override
  public List<ChainLink> visitCall(MplCall mplCall) {
    List<ChainLink> result = new ArrayList<>();
    String processName = mplCall.getProcess();
    if (checkProcessExists(mplCall, processName)) {
      MplProcess process = context.getProgram().getProcess(processName);
      if (process.getType() == INLINE) {
        for (ChainPart cp : process.getChainParts()) {
          result.addAll(cp.accept(this));
        }
        return result;
      }
    }
    ModifierBuffer modifier = new ModifierBuffer();
    modifier.setConditional(mplCall.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    MplStart mplStart = new MplStart("@e[name=" + processName + "]", mplCall, mplCall.getPrevious(),
        mplCall.getSource());
    result.addAll(mplStart.accept(this));

    MplWaitfor mplWaitfor = new MplWaitfor(processName, modifier, mplCall.getSource());
    result.addAll(visitIgnoringWarnings(mplWaitfor));
    return result;
  }

  @Override
  public List<ChainLink> visitStart(MplStart mplStart) {
    List<ChainLink> result = new ArrayList<>(2);
    String selector = mplStart.getSelector();
    String processName = selector.substring(8, selector.length() - 1);
    checkProcessExists(mplStart, processName);
    checkNotInlineProcess(mplStart, processName);
    addInvertingCommandIfInvert(result, mplStart);

    String command = "execute " + selector + " ~ ~ ~ " + getStartCommand();
    result.add(newCommand(command, mplStart));
    return result;
  }

  @Override
  public List<ChainLink> visitStop(MplStop mplStop) {
    List<ChainLink> result = new ArrayList<>(2);
    String selector = mplStop.getSelector();
    String processName = selector.substring(8, selector.length() - 1);
    checkProcessExists(mplStop, processName);
    checkNotInlineProcess(mplStop, processName);
    addInvertingCommandIfInvert(result, mplStop);

    String command = "execute " + selector + " ~ ~ ~ " + getStopCommand();
    result.add(newCommand(command, mplStop));
    return result;
  }

  @Override
  public List<ChainLink> visitWaitfor(MplWaitfor mplWaitfor) {
    List<ChainLink> result = new ArrayList<>();
    checkIsUsed(mplWaitfor);
    String event = mplWaitfor.getEvent();
    checkNotInlineProcess(mplWaitfor, event);

    List<ChainLink> dest = newJumpDestination(false);

    MinecraftVersion version = compilerContext.getVersion();
    CommandPartBuffer summonCpb = new CommandPartBuffer();
    summonCpb.add("summon " + version.markerEntity() + " ");
    summonCpb.add(new TargetedThisInsert(dest.get(0)));
    summonCpb.add(
        " {CustomName:" + event + NOTIFY + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
    Command summon = newInternalCommand(summonCpb, modifier());

    if (mplWaitfor.getConditional() == UNCONDITIONAL) {
      summon.setModifier(mplWaitfor);
      result.add(summon);
    } else {
      Command noWait = newInternalCommand(getStartCommand(dest.get(0)), modifier(CONDITIONAL));
      if (mplWaitfor.getConditional() == CONDITIONAL) {
        summon.setModifier(mplWaitfor);
        result.add(summon);
        result.add(newInvertingCommand(mplWaitfor.getMode()));
        result.add(noWait);
      } else { // conditional == INVERT
        noWait.setModifier(mplWaitfor);
        summon.setConditional(true);
        result.add(noWait);
        result.add(newInvertingCommand(mplWaitfor.getMode()));
        result.add(summon);
      }
    }
    result.addAll(dest);
    resolveAllTargetedThisInserts(result);
    return result;
  }

  /**
   * Checks if the specified {@link MplWaitfor} can be triggered in the program, that is if there is
   * a process with the same name as {@link MplWaitfor#getEvent()} or if there is a
   * {@link MplNotify} for the event of the waitfor. If the waitfor is not used, a compiler warning
   * is added.
   *
   * @param mplWaitfor the {@link MplWaitfor}
   * @return {@code true} if the specified waitfor is used, {@code false} otherwise
   */
  private boolean checkIsUsed(MplWaitfor mplWaitfor) {
    MplProgram program = context.getProgram();
    String event = mplWaitfor.getEvent();
    boolean triggeredByProcess = program.streamProcesses()//
        .anyMatch(p -> event.equals(p.getName()));
    if (triggeredByProcess) {
      return true;
    }

    for (MplProcess mplProcess : program.getAllProcesses()) {
      ContainsMatchVisitor visitor = new ContainsMatchVisitor(
          cp -> (cp instanceof MplNotify) && event.equals(((MplNotify) cp).getEvent()));
      Boolean notified = visitor.test(mplProcess);
      if (notified) {
        // Triggered by notify
        return true;
      }
    }
    compilerContext.addWarning(new CompilerException(mplWaitfor.getSource(),
        "The event " + event + " is never triggered"));
    return false;
  }

  @Override
  public List<ChainLink> visitNotify(MplNotify mplNotify) {
    List<ChainLink> result = new ArrayList<>(3);
    String event = mplNotify.getEvent();
    checkIsUsed(mplNotify);
    addInvertingCommandIfInvert(result, mplNotify);

    ModifierBuffer modifier = modifier(mplNotify.getConditional());
    result.add(
        newCommand("execute @e[name=" + event + NOTIFY + "] ~ ~ ~ " + getStartCommand(), modifier));
    result.add(newInternalCommand("kill @e[name=" + event + NOTIFY + "]", modifier));
    return result;
  }

  /**
   * Checks if the specified {@link MplNotify} is used in the program, that is if there is a
   * {@link MplWaitfor} for the event of the notify. If the notify is not used, a compiler warning
   * is added.
   *
   * @param mplNotify the {@link MplNotify}
   * @return {@code true} if the specified notify is used, {@code false} otherwise
   */
  private boolean checkIsUsed(MplNotify mplNotify) {
    String event = mplNotify.getEvent();
    for (MplProcess mplProcess : context.getProgram().getAllProcesses()) {
      ContainsMatchVisitor visitor = new ContainsMatchVisitor(
          cp -> ((cp instanceof MplWaitfor) && event.equals(((MplWaitfor) cp).getEvent()))
              || ((cp instanceof MplCall) && event.equals(((MplCall) cp).getProcess())));
      Boolean triggered = visitor.test(mplProcess);
      if (triggered) {
        return true;
      }
    }
    compilerContext.addWarning(
        new CompilerException(mplNotify.getSource(), "The event " + event + " is never used"));
    return false;
  }

  @Override
  public List<ChainLink> visitIntercept(MplIntercept mplIntercept) {
    List<ChainLink> result = new ArrayList<>();
    String event = mplIntercept.getEvent();
    checkProcessExists(mplIntercept, event);
    checkNotInlineProcess(mplIntercept, event);
    Conditional conditional = mplIntercept.getConditional();

    Command entitydata = newInternalCommand(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}",
        modifier(conditional));


    List<ChainLink> trc = newJumpDestination(false);

    MinecraftVersion version = compilerContext.getVersion();

    CommandPartBuffer summonCpb = new CommandPartBuffer();
    summonCpb.add("summon " + version.markerEntity() + " ");
    summonCpb.add(new TargetedThisInsert(trc.get(0)));
    summonCpb
        .add(" {CustomName:" + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");
    ChainLink summon = newInternalCommand(summonCpb, modifier(conditional));

    if (mplIntercept.getConditional() == UNCONDITIONAL) {
      result.add(entitydata);
      result.add(summon);
    } else {
      ChainLink noWait = newInternalCommand(getStartCommand(trc.get(0)), modifier(CONDITIONAL));
      if (mplIntercept.getConditional() == CONDITIONAL) {
        result.add(entitydata);
        result.add(summon);
        result.add(newInvertingCommand(CHAIN));
        result.add(noWait);
      } else { // conditional == INVERT
        result.add(noWait);
        result.add(newInvertingCommand(CHAIN));
        result.add(entitydata);
        result.add(summon);
      }
    }
    result.addAll(trc);
    result.add(newInternalCommand("kill @e[name=" + event + ",r=2]"));
    result.add(newInternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
    resolveAllTargetedThisInserts(result);
    return result;
  }

  @Override
  public List<ChainLink> visitBreakpoint(MplBreakpoint mplBreakpoint) {
    if (!options.hasOption(DEBUG)) {
      return Collections.emptyList();
    }
    List<ChainLink> result = new ArrayList<>();
    context.setBreakpoint(mplBreakpoint.getSource());

    addInvertingCommandIfInvert(result, mplBreakpoint);

    result.add(newInternalCommand("say " + mplBreakpoint.getMessage(), mplBreakpoint));

    ModifierBuffer modifier = new ModifierBuffer();
    modifier.setConditional(mplBreakpoint.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    MplCall mplCall = new MplCall("breakpoint", modifier, mplBreakpoint.getSource());
    result.addAll(mplCall.accept(this));
    // MplStart mplStart = new MplStart("@e[name=breakpoint]", modifier, mplBreakpoint.getSource());
    // MplWaitfor mplWaitfor = new MplWaitfor("breakpoint", modifier, mplBreakpoint.getSource());
    // result.addAll(mplStart.accept(this));
    // result.addAll(mplWaitfor.accept(this));
    return result;
  }

  @Override
  public List<ChainLink> visitSkip(MplSkip mplSkip) {
    List<ChainLink> result = new ArrayList<>(1);
    result.add(mplSkip);
    return result;
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___   __             _____  _                              _____  _
  //   |_ _| / _|           |_   _|| |__    ___  _ __             | ____|| | ___   ___
  //    | | | |_              | |  | '_ \  / _ \| '_ \            |  _|  | |/ __| / _ \
  //    | | |  _|  _  _  _    | |  | | | ||  __/| | | |  _  _  _  | |___ | |\__ \|  __/
  //   |___||_|   (_)(_)(_)   |_|  |_| |_| \___||_| |_| (_)(_)(_) |_____||_||___/ \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Override
  public List<ChainLink> visitIf(MplIf mplIf) {
    List<ChainLink> result = new ArrayList<>();
    addInvertingCommandIfInvert(result, mplIf);

    Command ref = newCommand(mplIf.getCondition(), mplIf);
    result.add(ref);
    if (mplIf.needsNormalizer()) {
      ref = newNormalizingCommand();
      result.add(ref);
    }
    IfNestingLayer layer = new IfNestingLayer(mplIf.isNot(), ref);
    context.getIfNestingLayers().push(layer);

    MplProcessIfAstVisitor ifVisitor = new MplProcessIfAstVisitor(compilerContext, context);

    layer.setInElse(false);
    for (ChainPart thenPart : mplIf.getThenParts()) {
      result.addAll(thenPart.accept(ifVisitor));
    }

    layer.setInElse(true);
    for (ChainPart elsePart : mplIf.getElseParts()) {
      result.addAll(elsePart.accept(ifVisitor));
    }

    context.getIfNestingLayers().pop();
    resolveAllTargetedThisInserts(result);
    return result;
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //   __        __ _      _  _
  //   \ \      / /| |__  (_)| |  ___
  //    \ \ /\ / / | '_ \ | || | / _ \
  //     \ V  V /  | | | || || ||  __/
  //      \_/\_/   |_| |_||_||_| \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Override
  public List<ChainLink> visitWhile(MplWhile mplWhile) {
    List<ChainLink> result = new ArrayList<>();
    context.getLoops().push(mplWhile);

    String condition = mplWhile.getCondition();
    boolean hasInitialCondition = condition != null && !mplWhile.isTrailing();

    int firstIndex = result.size();
    if (hasInitialCondition) {
      result.add(newCommand(condition));
    }

    TargetedThisInsert initLoopInsert = new TargetedThisInsert();
    Command initLoop = newInternalCommand(getStartCommand(initLoopInsert), modifier());
    TargetedThisInsert skipLoopInsert = new TargetedThisInsert();
    Command skipLoop = newInternalCommand(getStartCommand(skipLoopInsert), modifier(CONDITIONAL));

    if (!hasInitialCondition && !mplWhile.isConditional()) {
      result.add(initLoop);
    } else {
      initLoop.setConditional(true);

      boolean isNormal = hasInitialCondition && !mplWhile.isNot();
      if (isNormal || !hasInitialCondition && mplWhile.getConditional() == CONDITIONAL) {
        result.add(initLoop);
        result.add(newInvertingCommand(CHAIN));
        result.add(skipLoop);
      } else {
        result.add(skipLoop);
        result.add(newInvertingCommand(CHAIN));
        result.add(initLoop);
      }
    }
    ((Command) result.get(firstIndex)).setModifier(mplWhile);

    // From here the next command will be the entry link for the loop
    int entryIndex = result.size();

    if (options.hasOption(TRANSMITTER)) {
      result.add(new MplSkip(true));
    }

    Deque<ChainPart> chainParts = new ArrayDeque<>(mplWhile.getChainParts());
    if (chainParts.isEmpty()) {
      // Warning DO NOT USE Commands.newNoOperationCommand() here, because those are ignored when
      // resolving relative this inserts
      chainParts.add(new MplCommand("", mplWhile.getSource()));
    }
    ChainPart first = chainParts.peek();
    if (options.hasOption(TRANSMITTER) && first instanceof MplWhile) {
      if (((MplWhile) first).getCondition() == null) {
        // TODO: The NOP is only required due to the current chainlink placement which throws:
        // java.lang.IllegalArgumentException: RECEIVER at index 7 is followed by a TRANSMITTER
        // If that system is updated this will no longer be necessary
        first = new MplCommand(new CommandPartBuffer(), modifier(), mplWhile.getSource());
        chainParts.push(first);
      }
    }
    try {
      first.setMode(IMPULSE);
      first.setNeedsRedstone(true);
    } catch (IllegalModifierException ex) {
      throw new IllegalStateException("while cannot start with " + first.getName(), ex);
    }
    boolean dontRestart = false;
    for (ChainPart chainPart : chainParts) {
      result.addAll(chainPart.accept(this));
      if (chainPart instanceof MplBreak || chainPart instanceof MplContinue) {
        if (!((ModifiableChainPart) chainPart).isConditional()) {
          dontRestart = true;
          break;
        }
      }
    }
    ChainLink entryLink = result.get(entryIndex);
    initLoopInsert.setTarget(entryLink);

    if (!dontRestart) {
      if (condition == null) {
        result.addAll(getRestartBackref(entryLink, false));
      } else {
        result.add(newCommand(condition));
        if (!mplWhile.isNot()) {
          result.addAll(getContinueLoop(mplWhile, true));
          result.add(newInvertingCommand(CHAIN));
          result.addAll(getBreakLoop(mplWhile, true));
        } else {
          result.addAll(getBreakLoop(mplWhile, true));
          result.add(newInvertingCommand(CHAIN));
          result.addAll(getContinueLoop(mplWhile, true));
        }
      }
    }
    List<ChainLink> exit = newJumpDestination(true);
    result.addAll(exit);
    ChainLink exitLink = exit.get(0);
    skipLoopInsert.setTarget(exitLink);

    for (Iterator<LoopRef> it = context.getLoopRefs().iterator(); it.hasNext();) {
      LoopRef ref = it.next();
      if (ref.getLoop() == mplWhile) {
        ref.setEntryLink(entryLink);
        ref.setExitLink(exitLink);
        it.remove();
      }
    }

    context.getLoops().pop();
    resolveAllTargetedThisInserts(result);
    return result;
  }

  @Override
  public List<ChainLink> visitBreak(MplBreak mplBreak) {
    List<ChainLink> result = new ArrayList<>();
    MplWhile loop = mplBreak.getLoop();
    // FIXME: ein command von break MUSS nicht internal sein (bei unconditional)
    Conditional conditional = mplBreak.getConditional();
    if (conditional == UNCONDITIONAL) {
      List<Command> breakLoop = getBreakLoop(loop);
      breakLoop.get(0).setModifier(mplBreak);
      result.addAll(breakLoop);
      return result;
    }
    List<ChainLink> dest = newJumpDestination(false);
    Command dontBreak = newInternalCommand(getStartCommand(dest.get(0)), modifier(CONDITIONAL));
    if (conditional == CONDITIONAL) {
      List<Command> breakLoop = getBreakLoop(loop);
      breakLoop.get(0).setModifier(mplBreak);
      result.addAll(breakLoop);
      result.add(newInvertingCommand(breakLoop.get(breakLoop.size() - 1).getMode()));
      result.add(dontBreak);
    } else {
      dontBreak.setModifier(mplBreak);
      result.add(dontBreak);
      result.add(newInvertingCommand(dontBreak.getMode()));
      result.addAll(getBreakLoop(loop, true));
    }
    result.addAll(dest);
    resolveAllTargetedThisInserts(result);
    return result;
  }

  @Override
  public List<ChainLink> visitContinue(MplContinue mplContinue) {
    List<ChainLink> result = new ArrayList<>();
    MplWhile loop = mplContinue.getLoop();
    // FIXME: ein command von continue MUSS nicht internal sein (bei unconditional)
    Conditional conditional = mplContinue.getConditional();
    String condition = loop.getCondition();
    if (conditional == UNCONDITIONAL) {
      if (condition != null) {
        result.add(newCommand(condition, mplContinue));
        result.addAll(getContinueLoop(loop, true));
        result.add(newInvertingCommand(CHAIN));
        result.addAll(getBreakLoop(loop, true));
      } else {
        List<Command> continueLoop = getContinueLoop(loop);
        continueLoop.get(0).setModifier(mplContinue);
        result.addAll(continueLoop);
      }
      return result;
    }
    MplSource source = mplContinue.getSource();
    MplIf outerIf = new MplIf(false, "//", source);
    outerIf.setMode(mplContinue.getPrevious().getModeForInverting());
    outerIf.setConditional(mplContinue.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    outerIf.setPrevious(mplContinue.getPrevious());
    outerIf.enterThen();
    if (condition != null) {
      MplIf innerIf = new MplIf(false, condition, source);
      innerIf.enterThen();
      innerIf.add(newMplContinueLoop(loop, source));
      innerIf.enterElse();
      innerIf.add(newMplBreakLoop(loop, source));
      outerIf.add(innerIf);
    } else {
      outerIf.add(newMplContinueLoop(loop, source));
    }
    outerIf.enterElse();
    List<ChainLink> end = newJumpDestination(false);
    Command exit = newInternalCommand(getStartCommand(end.get(0)), modifier(CONDITIONAL));
    outerIf.add(new InternalMplCommand(source, exit));

    if (conditional == INVERT) {
      outerIf.switchThenAndElse();
    }
    List<ChainLink> ifResult = outerIf.accept(this);
    result.addAll(ifResult);

    result.addAll(end);
    resolveAllTargetedThisInserts(result);
    // FIXME: Dirty Hack: The condition of an if should be an instance of Dependable
    result.removeIf(it -> it == ifResult.get(0));
    return result;
  }

  /**
   * Returns a wrapper for {@link #getBreakLoop(MplWhile)}.
   *
   * @param loop the loop to break
   * @return a wrapper to break the loop
   */
  private InternalMplCommand newMplBreakLoop(MplWhile loop, MplSource source) {
    return new InternalMplCommand(source, getBreakLoop(loop));
  }

  /**
   * Returns a list of commands that break the specified loop. Breaking a loop consists out of two
   * parts:
   * <ol>
   * <li>Trigger the loops exit link to run the program after the loop
   * <li>Stop all loops within the specified loop
   * </ol>
   *
   * @param loop the loop to break out of
   * @return commands that break the specified loop
   */
  @CheckReturnValue
  private List<Command> getBreakLoop(MplWhile loop) {
    List<Command> result = new ArrayList<>();
    result.add(newExitLoopCommand(loop));
    for (MplWhile innerLoop : context.getLoops()) {
      Command stop = newLoopStoppingCommand(innerLoop);
      stop.setConditional(true);
      result.add(stop);
      if (innerLoop == loop) {
        break;
      }
    }
    return result;
  }

  /**
   * This is a shortcut for {@link #getBreakLoop(MplWhile)}.
   *
   * @param loop the loop to break out of
   * @param conditional whether the first command should be conditional
   * @return commands that break the specified loop
   */
  private List<Command> getBreakLoop(MplWhile loop, boolean conditional) {
    List<Command> breakLoop = getBreakLoop(loop);
    breakLoop.get(0).setConditional(conditional);
    return breakLoop;
  }

  /**
   * Returns a wrapper for {@link #getContinueLoop(MplWhile)}.
   *
   * @param loop the loop to continue
   * @return a wrapper to continue the loop
   */
  private InternalMplCommand newMplContinueLoop(MplWhile loop, MplSource source) {
    return new InternalMplCommand(source, getContinueLoop(loop));
  }

  /**
   * Returns a list of commands that continue the specified loop. Continuing a loop consists out of
   * two parts:
   * <ol>
   * <li>Stop all loops within the specified loop
   * <li>Trigger the loops entry link to restart the loop
   * </ol>
   *
   * @param loop the loop to continue
   * @return commands that continue the specified loop
   */
  @CheckReturnValue
  private List<Command> getContinueLoop(MplWhile loop) {
    List<Command> result = new ArrayList<>();
    for (MplWhile innerLoop : context.getLoops()) {
      Command stop = newLoopStoppingCommand(innerLoop);
      stop.setConditional(true);
      result.add(stop);
      if (innerLoop == loop) {
        break;
      }
    }
    result.get(0).setConditional(false);
    Command start = newLoopStartingCommand(loop);
    start.setConditional(true);
    result.add(start);
    return result;
  }

  /**
   * This is a shortcut for {@link #getContinueLoop(MplWhile)}.
   *
   * @param loop the loop to continue
   * @param conditional whether the first command should be conditional
   * @return commands that continue the specified loop
   */
  private List<Command> getContinueLoop(MplWhile loop, boolean conditional) {
    List<Command> continueLoop = getContinueLoop(loop);
    continueLoop.get(0).setConditional(conditional);
    return continueLoop;
  }

  /**
   * Returns a command that will start the entry link of the specified loop.
   *
   * @param loop the loop to start
   * @return a command that will start the loop
   */
  @CheckReturnValue
  private Command newLoopStartingCommand(MplWhile loop) {
    TargetedThisInsert insert = new TargetedThisInsert();
    context.getLoopRefs().add(new LoopRef(loop) {
      @Override
      public void setEntryLink(ChainLink entryLink) {
        insert.setTarget(entryLink);
      }
    });
    return newInternalCommand(getStartCommand(insert), modifier());
  }

  /**
   * Returns a command that will stop the entry link of the specified loop.
   *
   * @param loop the loop to stop
   * @return a command that will stop the loop
   */
  @CheckReturnValue
  private Command newLoopStoppingCommand(MplWhile loop) {
    TargetedThisInsert insert = new TargetedThisInsert();
    context.getLoopRefs().add(new LoopRef(loop) {
      @Override
      public void setEntryLink(ChainLink entryLink) {
        insert.setTarget(entryLink);
      }
    });
    return newInternalCommand(getStopCommand(insert), modifier());
  }

  /**
   * Returns a command that will trigger the loops exit link to run the program after the loop.
   *
   * @param loop the loop of which to trigger the exit link
   * @return a command that will trigger the loops exit link
   */
  @CheckReturnValue
  private Command newExitLoopCommand(MplWhile loop) {
    TargetedThisInsert insert = new TargetedThisInsert();
    context.getLoopRefs().add(new LoopRef(loop) {
      @Override
      public void setExitLink(ChainLink exitLink) {
        insert.setTarget(exitLink);
      }
    });
    return newInternalCommand(getStartCommand(insert), modifier());
  }
}
