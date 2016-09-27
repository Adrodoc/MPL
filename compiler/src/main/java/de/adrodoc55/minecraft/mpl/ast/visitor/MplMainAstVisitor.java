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
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.antlr.v4.runtime.CommonToken;

import com.google.common.annotations.VisibleForTesting;

import de.adrodoc55.commons.CopyScope;
import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.ProcessType;
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
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ResolveableCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import de.adrodoc55.minecraft.mpl.version.MplVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
public class MplMainAstVisitor extends MplBaseAstVisitor {
  private final MplCompilerContext context;
  @VisibleForTesting
  MplProgram program;

  private MplSource breakpoint;

  public MplMainAstVisitor(MplCompilerContext context) {
    super(context.getOptions());
    this.context = checkNotNull(context, "context == null!");
  }

  public ChainContainer visitProgram(MplProgram program) {
    this.program = program;
    Orientation3D orientation = program.getOrientation();
    Coordinate3D max = program.getMax();
    CommandChain install = visitInstall(program);
    CommandChain uninstall = visitUninstall(program);

    List<CommandChain> chains = new ArrayList<>(program.getProcesses().size());
    for (MplProcess process : program.getProcesses()) {
      CommandChain chain = visitProcess(process);
      if (chain != null) {
        chains.add(chain);
      }
    }
    if (breakpoint != null) {
      chains.add(getBreakpointProcess(program));
    }
    return new ChainContainer(orientation, max, install, uninstall, chains, program.getHash());
  }

  private static MplSource defaultSource(File programFile) {
    return new MplSource(programFile, new CommonToken(MplLexer.PROCESS), "");
  }

  private CommandChain visitInstall(MplProgram program) {
    if (!isInstallRequired(program)) {
      return new CommandChain("install", new ArrayList<>(0));
    }
    MplProcess install = program.getInstall();
    if (install == null) {
      install = new MplProcess("install", defaultSource(program.getProgramFile()));
    }
    return visitProcess(install);
  }

  private boolean isInstallRequired(MplProgram program) {
    return program.getInstall() != null//
        || isUninstallRequired(program);
  }

  private CommandChain visitUninstall(MplProgram program) {
    if (!isUninstallRequired(program)) {
      return new CommandChain("uninstall", new ArrayList<>(0));
    }
    MplProcess uninstall = program.getUninstall();
    if (uninstall == null) {
      uninstall = new MplProcess("uninstall", defaultSource(program.getProgramFile()));
    }
    return visitProcess(uninstall);
  }

  private boolean isUninstallRequired(MplProgram program) {
    return program.getUninstall() != null//
        || options.hasOption(DELETE_ON_UNINSTALL)//
        || program.getProcesses().stream().anyMatch(p -> p.getName() != null);
  }

  @CheckReturnValue
  private CommandChain getBreakpointProcess(MplProgram program) {
    String hash = program.getHash();
    MplProcess process = new MplProcess("breakpoint", breakpoint);
    List<ChainPart> commands = new ArrayList<>();

    // Pause
    if (!options.hasOption(TRANSMITTER)) {
      commands.add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ clone ~ ~ ~ ~ ~ ~ ~ ~1 ~",
          breakpoint));
    }
    commands.add(new MplCommand("/tp @e[tag=" + hash + "] ~ ~1 ~", breakpoint));
    if (!options.hasOption(TRANSMITTER)) {
      commands.add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ blockdata ~ ~ ~ {Command:}",
          breakpoint));
    }

    commands.add(new MplCommand(
        "tellraw @a [{\"text\":\"[tp to breakpoint]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp @p @e[name=breakpoint_NOTIFY,c=-1]\"}},{\"text\":\" \"},{\"text\":\"[continue program]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/execute @e[name=breakpoint_CONTINUE] ~ ~ ~ "
            + getStartCommand("~ ~ ~") + "\"}}]",
        breakpoint));

    commands.add(new MplWaitfor("breakpoint_CONTINUE", breakpoint));
    commands.add(new MplCommand("/kill @e[name=breakpoint_CONTINUE]", breakpoint));

    // Unpause
    commands.add(new MplCommand(
        "/execute @e[tag=" + hash + "] ~ ~ ~ clone ~ ~ ~ ~ ~ ~ ~ ~-1 ~ force move", breakpoint));
    commands.add(new MplCommand("/tp @e[tag=" + hash + "] ~ ~-1 ~", breakpoint));
    if (!options.hasOption(TRANSMITTER)) {
      commands.add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ blockdata ~ ~ ~ {Command:"
          + getStopCommand("~ ~ ~") + "}", breakpoint));
    }

    commands.add(new MplNotify("breakpoint", breakpoint));

    process.setChainParts(commands);
    return visitProcess(process);
  }

  private List<ChainLink> visitIgnoringWarnings(ChainPart cp) {
    MplCompilerContext context = new MplCompilerContext(options);
    MplMainAstVisitor visitor = new MplMainAstVisitor(context);
    visitor.program = program;
    List<ChainLink> accept = cp.accept(visitor);
    context.clearWarnings();
    this.context.addContext(context);
    return accept;
  }

  /**
   * Returns null if the specified {@code process} is of type {@link ProcessType#INLINE}.
   *
   * @param process the {@link MplProcess} to convert
   * @return result a new {@link CommandChain}
   */
  public @Nullable CommandChain visitProcess(MplProcess process) {
    if (process.getType() == INLINE) {
      return null;
    }
    List<ChainPart> chainParts = new CopyScope().copy(process.getChainParts());
    List<ChainLink> result = new ArrayList<>(chainParts.size());
    boolean containsSkip = containsHighlevelSkip(chainParts);
    String name = process.getName();
    if (name != null) {
      if (process.isRepeating()) {
        if (options.hasOption(TRANSMITTER)) {
          result.add(new MplSkip());
        }
        if (chainParts.isEmpty()) {
          chainParts.add(new MplCommand("", process.getSource()));
        }
        ChainPart first = chainParts.get(0);
        try {
          if (containsSkip) {
            first.setMode(IMPULSE);
          } else {
            first.setMode(REPEAT);
          }
          first.setNeedsRedstone(true);
        } catch (IllegalModifierException ex) {
          throw new IllegalStateException(ex.getMessage(), ex);
        }
      } else {
        result.addAll(getTransmitterReceiverCombo(false));
      }
    } else if (options.hasOption(TRANSMITTER)) {
      result.add(new MplSkip());
    }
    for (ChainPart chainPart : chainParts) {
      result.addAll(chainPart.accept(this));
    }
    if (process.isRepeating() && containsSkip) {
      result.addAll(getRestartBackref(result.get(0), false));
      resolveReferences(result);
    }
    if (!process.isRepeating() && name != null && !"install".equals(name)
        && !"uninstall".equals(name)) {
      result.addAll(visitIgnoringWarnings(new MplNotify(name, process.getSource())));
    }
    return new CommandChain(name, result, process.getTags());
  }

  private boolean containsHighlevelSkip(List<ChainPart> chainParts) {
    for (ChainPart chainPart : chainParts) {
      if (chainPart instanceof MplWaitfor//
          || chainPart instanceof MplIntercept//
          || chainPart instanceof MplBreakpoint//
          || chainPart instanceof MplWhile//
          || chainPart instanceof MplBreak//
          || chainPart instanceof MplContinue//
      ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a process with the specified {@code processName} is part of the program. If there is
   * such a process, this method returns {@code true}, otherwise it returns {@code false} and adds a
   * compiler warning.
   *
   * @param chainpart where to display the warning
   * @param processName the required process
   * @return {@code true} if the process was found, {@code false} otherwise
   */
  private boolean checkProcessExists(ModifiableChainPart chainpart, String processName) {
    checkNotNull(processName, "processName == null!");
    if (!program.containsProcess(processName)) {
      context.addWarning(
          new CompilerException(chainpart.getSource(), "Could not resolve process " + processName));
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
    MplProcess process = program.getProcess(processName);
    if (process != null && process.getType() == ProcessType.INLINE) {
      context.addError(new CompilerException(chainpart.getSource(),
          "Cannot " + chainpart.getName() + " an inline process"));
      return false;
    }
    return true;
  }

  @Override
  public List<ChainLink> visitCommand(MplCommand command) {
    List<ChainLink> result = new ArrayList<>(2);
    addInvertingCommandIfInvert(result, command);

    String cmd = command.getCommand();
    result.add(new Command(cmd, command));
    return result;
  }

  @Override
  public List<ChainLink> visitCall(MplCall mplCall) {
    List<ChainLink> result = new ArrayList<>();
    String processName = mplCall.getProcess();
    if (checkProcessExists(mplCall, processName)) {
      MplProcess process = program.getProcess(processName);
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
  public List<ChainLink> visitStart(MplStart start) {
    List<ChainLink> result = new ArrayList<>(2);
    String selector = start.getSelector();
    String processName = selector.substring(8, selector.length() - 1);
    checkProcessExists(start, processName);
    checkNotInlineProcess(start, processName);
    addInvertingCommandIfInvert(result, start);

    String command = "execute " + selector + " ~ ~ ~ " + getStartCommand("~ ~ ~");
    result.add(new Command(command, start));
    return result;
  }

  @Override
  public List<ChainLink> visitStop(MplStop stop) {
    List<ChainLink> result = new ArrayList<>(2);
    String selector = stop.getSelector();
    String processName = selector.substring(8, selector.length() - 1);
    checkProcessExists(stop, processName);
    checkNotInlineProcess(stop, processName);
    addInvertingCommandIfInvert(result, stop);

    String command = "execute " + selector + " ~ ~ ~ " + getStopCommand("~ ~ ~");
    result.add(new Command(command, stop));
    return result;
  }

  @Override
  public List<ChainLink> visitWaitfor(MplWaitfor waitfor) {
    List<ChainLink> result = new ArrayList<>();
    checkIsUsed(waitfor);
    String event = waitfor.getEvent();
    checkNotInlineProcess(waitfor, event);

    MplVersion version = context.getVersion();
    ReferencingCommand summon = new ReferencingCommand(
        "summon " + version.getMarkerEntityName() + " " + REF + " {CustomName:" + event + NOTIFY
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

    if (waitfor.getConditional() == UNCONDITIONAL) {
      summon.setRelative(1);
      result.add(summon);
    } else {
      summon.setConditional(true);
      ReferencingCommand jump = new ReferencingCommand(getStartCommand(REF), true);
      if (waitfor.getConditional() == CONDITIONAL) {
        summon.setRelative(3);
        jump.setRelative(1);
        result.add(summon);
        result.add(newInvertingCommand(CHAIN));
        result.add(jump);
      } else { // conditional == INVERT
        jump.setRelative(3);
        summon.setRelative(1);
        result.add(jump);
        result.add(newInvertingCommand(CHAIN));
        result.add(summon);
      }
    }
    result.addAll(getTransmitterReceiverCombo(false));
    return result;
  }

  /**
   * Checks if the specified {@link MplWaitfor} can be triggered in the program, that is if there is
   * a process with the same name as {@link MplWaitfor#getEvent()} or if there is a
   * {@link MplNotify} for the event of the waitfor. If the waitfor is not used, a compiler warning
   * is added.
   *
   * @param waitfor the {@link MplWaitfor}
   * @return {@code true} if the specified waitfor is used, {@code false} otherwise
   */
  private boolean checkIsUsed(MplWaitfor waitfor) {
    String event = waitfor.getEvent();
    boolean triggeredByProcess = program.streamProcesses()//
        .anyMatch(p -> event.equals(p.getName()));
    if (triggeredByProcess) {
      return true;
    }
    boolean triggeredByNotify = program.streamProcesses()//
        .flatMap(p -> p.getChainParts().stream())//
        .anyMatch(c -> {
          if (c instanceof MplNotify)
            return event.equals(((MplNotify) c).getEvent());
          return false;
        });
    if (triggeredByNotify) {
      return true;
    }
    context.addWarning(
        new CompilerException(waitfor.getSource(), "The event " + event + " is never triggered"));
    return false;
  }

  @Override
  public List<ChainLink> visitNotify(MplNotify notify) {
    List<ChainLink> result = new ArrayList<>(3);
    String event = notify.getEvent();
    checkIsUsed(notify);
    addInvertingCommandIfInvert(result, notify);

    boolean conditional = notify.isConditional();
    result.add(new InternalCommand(
        "execute @e[name=" + event + NOTIFY + "] ~ ~ ~ " + getStartCommand("~ ~ ~"), conditional));
    result.add(new Command("kill @e[name=" + event + NOTIFY + "]", conditional));
    return result;
  }

  /**
   * Checks if the specified {@link MplNotify} is used in the program, that is if there is a
   * {@link MplWaitfor} for the event of the notify. If the notify is not used, a compiler warning
   * is added.
   *
   * @param notify the {@link MplNotify}
   * @return {@code true} if the specified notify is used, {@code false} otherwise
   */
  private boolean checkIsUsed(MplNotify notify) {
    String event = notify.getEvent();
    boolean used = program.streamProcesses()//
        .flatMap(p -> p.getChainParts().stream())//
        .anyMatch(c -> {
          if (c instanceof MplWaitfor)
            return event.equals(((MplWaitfor) c).getEvent());
          return false;
        });
    if (!used) {
      context.addWarning(
          new CompilerException(notify.getSource(), "The event " + event + " is never used"));
    }
    return used;
  }

  @Override
  public List<ChainLink> visitIntercept(MplIntercept intercept) {
    List<ChainLink> result = new ArrayList<>();
    String event = intercept.getEvent();
    checkProcessExists(intercept, event);
    checkNotInlineProcess(intercept, event);
    boolean conditional = intercept.isConditional();

    InternalCommand entitydata = new InternalCommand(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}", conditional);

    MplVersion version = context.getVersion();
    ResolveableCommand summon = new ResolveableCommand(
        "summon " + version.getMarkerEntityName() + " " + REF + " {CustomName:" + event
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
        conditional);


    ResolveableCommand jump = new ResolveableCommand(getStartCommand(REF), true);
    if (intercept.getConditional() == UNCONDITIONAL) {
      result.add(entitydata);
      result.add(summon);
    } else {
      if (intercept.getConditional() == CONDITIONAL) {
        result.add(entitydata);
        result.add(summon);
        result.add(newInvertingCommand(CHAIN));
        result.add(jump);
      } else { // conditional == INVERT
        result.add(jump);
        result.add(newInvertingCommand(CHAIN));
        result.add(entitydata);
        result.add(summon);
      }
    }
    List<ChainLink> trc = getTransmitterReceiverCombo(false);
    ChainLink ref = trc.get(0);
    summon.setReferenced(ref);
    jump.setReferenced(ref);
    result.addAll(trc);
    result.add(new InternalCommand("kill @e[name=" + event + ",r=2]"));
    result.add(new InternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
    return resolveReferences(result);
  }

  @Override
  public List<ChainLink> visitBreakpoint(MplBreakpoint mplBreakpoint) {
    if (!options.hasOption(DEBUG)) {
      return Collections.emptyList();
    }
    List<ChainLink> result = new ArrayList<>();
    this.breakpoint = mplBreakpoint.getSource();

    addInvertingCommandIfInvert(result, mplBreakpoint);

    result.add(new InternalCommand("say " + mplBreakpoint.getMessage(), mplBreakpoint));

    ModifierBuffer modifier = new ModifierBuffer();
    modifier.setConditional(mplBreakpoint.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    // new MplCall("breakpoint", modifier, mplBreakpoint.getSource()).accept(this);
    MplStart mplStart = new MplStart("@e[name=breakpoint]", modifier, mplBreakpoint.getSource());
    MplWaitfor mplWaitfor = new MplWaitfor("breakpoint", modifier, mplBreakpoint.getSource());
    result.addAll(mplStart.accept(this));
    result.addAll(mplWaitfor.accept(this));
    return result;
  }

  @Override
  public List<ChainLink> visitSkip(MplSkip skip) {
    List<ChainLink> result = new ArrayList<>(1);
    result.add(skip);
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

  private Deque<MplWhile> loops = new ArrayDeque<>();

  @Override
  public List<ChainLink> visitWhile(MplWhile mplWhile) {
    List<ChainLink> result = new ArrayList<>();
    loops.push(mplWhile);

    String condition = mplWhile.getCondition();
    boolean hasInitialCondition = condition != null && !mplWhile.isTrailing();

    int firstIndex = result.size();
    if (hasInitialCondition) {
      result.add(new Command(condition));
    }

    ResolveableCommand init = new ResolveableCommand(getStartCommand(REF));
    ResolveableCommand skip = new ResolveableCommand(getStartCommand(REF), true);

    if (!hasInitialCondition && !mplWhile.isConditional()) {
      result.add(init);
    } else {
      init.setConditional(true);

      boolean isNormal = hasInitialCondition && !mplWhile.isNot();
      if (isNormal || !hasInitialCondition && mplWhile.getConditional() == CONDITIONAL) {
        result.add(init);
        result.add(newInvertingCommand(CHAIN));
        result.add(skip);
      } else {
        result.add(skip);
        result.add(newInvertingCommand(CHAIN));
        result.add(init);
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
      chainParts.add(new MplCommand("", mplWhile.getSource()));
    }
    ChainPart first = chainParts.peek();
    if (options.hasOption(TRANSMITTER) && first instanceof MplWhile) {
      if (((MplWhile) first).getCondition() == null) {
        first = new MplCommand("", mplWhile.getSource());
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
    init.setReferenced(entryLink);

    if (!dontRestart) {
      if (condition == null) {
        result.addAll(getRestartBackref(entryLink, false));
      } else {
        result.add(new Command(condition));
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
    List<ChainLink> exit = getTransmitterReceiverCombo(true);
    result.addAll(exit);
    ChainLink exitLink = exit.get(0);
    skip.setReferenced(exitLink);

    for (Iterator<LoopRef> it = loopRefs.iterator(); it.hasNext();) {
      LoopRef ref = it.next();
      if (ref.getLoop() == mplWhile) {
        ref.setEntryLink(entryLink);
        ref.setExitLink(exitLink);
        it.remove();
      }
    }

    loops.pop();
    return resolveReferences(result);
  }

  private Deque<LoopRef> loopRefs = new ArrayDeque<>();

  @Getter
  private abstract class LoopRef {
    private final @Nonnull MplWhile loop;

    public LoopRef(MplWhile loop) {
      this.loop = checkNotNull(loop, "loop == null!");
    }

    void setEntryLink(ChainLink entryLink) {}

    void setExitLink(ChainLink exitLink) {}
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
    ResolveableCommand dontBreak = new ResolveableCommand(getStartCommand(REF), true);
    if (conditional == CONDITIONAL) {
      List<Command> breakLoop = getBreakLoop(loop);
      breakLoop.get(0).setModifier(mplBreak);
      result.addAll(breakLoop);
      result.add(newInvertingCommand(CHAIN));
      result.add(dontBreak);
    } else {
      dontBreak.setModifier(mplBreak);
      result.add(dontBreak);
      result.add(newInvertingCommand(CHAIN));
      result.addAll(getBreakLoop(loop, true));
    }
    List<ChainLink> trc = getTransmitterReceiverCombo(false);
    dontBreak.setReferenced(trc.get(0));
    result.addAll(trc);
    return resolveReferences(result);
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
        result.add(new Command(condition, mplContinue));
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
    ResolveableCommand exit = new ResolveableCommand(getStartCommand(REF), true);
    outerIf.add(new InternalMplCommand(source, exit));

    if (conditional == INVERT) {
      outerIf.switchThenAndElse();
    }
    List<ChainLink> ifResult = outerIf.accept(this);
    result.addAll(ifResult);

    List<ChainLink> end = getTransmitterReceiverCombo(false);
    exit.setReferenced(end.get(0));
    result.addAll(end);
    resolveReferences(result);
    // FIXME: Dirty Hack
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
    for (MplWhile innerLoop : loops) {
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
    for (MplWhile innerLoop : loops) {
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
    ResolveableCommand startLoop = new ResolveableCommand(getStartCommand(REF));
    loopRefs.add(new LoopRef(loop) {
      @Override
      public void setEntryLink(ChainLink entryLink) {
        startLoop.setReferenced(entryLink);
      }
    });
    return startLoop;
  }

  /**
   * Returns a command that will stop the entry link of the specified loop.
   *
   * @param loop the loop to stop
   * @return a command that will stop the loop
   */
  @CheckReturnValue
  private Command newLoopStoppingCommand(MplWhile loop) {
    ResolveableCommand stopLoop = new ResolveableCommand(getStopCommand(REF));
    loopRefs.add(new LoopRef(loop) {
      @Override
      public void setEntryLink(ChainLink entryLink) {
        stopLoop.setReferenced(entryLink);
      }
    });
    return stopLoop;
  }

  /**
   * Returns a command that will trigger the loops exit link to run the program after the loop.
   *
   * @param loop the loop of which to trigger the exit link
   * @return a command that will trigger the loops exit link
   */
  @CheckReturnValue
  private Command newExitLoopCommand(MplWhile loop) {
    ResolveableCommand skipLoop = new ResolveableCommand(getStartCommand(REF));
    loopRefs.add(new LoopRef(loop) {
      @Override
      public void setExitLink(ChainLink exitLink) {
        skipLoop.setReferenced(exitLink);
      }
    });
    return skipLoop;
  }

}
