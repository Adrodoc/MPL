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
import static com.google.common.base.Preconditions.checkState;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

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
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
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
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreakLoop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinueLoop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;

/**
 * @author Adrodoc55
 */
public class MplAstVisitorImpl implements MplAstVisitor {
  private final MplCompilerContext context;
  @VisibleForTesting
  final CompilerOptions options;
  @VisibleForTesting
  MplProgram program;
  // @VisibleForTesting
  // List<CommandChain> chains = new ArrayList<>();
  // @VisibleForTesting
  // List<ChainLink> commands = new ArrayList<>();

  private MplSource breakpoint;

  public MplAstVisitorImpl(MplCompilerContext context) {
    this.context = checkNotNull(context, "context == null!");
    this.options = context.getOptions();
  }

  /**
   * Returns the relative count to the given {@link ChainLink} as a negative integer. If {@code ref}
   * is null the returned count will reference the first link in {@link #commands}.
   *
   * @param ref the {@link ChainLink} to search for
   * @return the count to ref
   * @throws IllegalArgumentException if {@code ref} is not found
   * @throws NullPointerException if {@code ref} is null
   */
  private int getCountToRef(ChainLink ref) throws IllegalArgumentException, NullPointerException {
    checkNotNull(ref, "ref == null!");
    for (int i = commands.size() - 1; i >= 0; i--) {
      if (ref == commands.get(i)) {
        return -commands.size() + i;
      }
    }
    throw new IllegalArgumentException("The given ref was not found in commands.");
  }

  public String getStartCommand(String ref) {
    if (options.hasOption(TRANSMITTER)) {
      return "setblock " + ref + " redstone_block";
    } else {
      return "blockdata " + ref + " {auto:1b}";
    }
  }

  public String getStopCommand(String ref) {
    if (options.hasOption(TRANSMITTER)) {
      if (options.hasOption(DEBUG)) {
        return "setblock " + ref + " air";
      } else {
        return "setblock " + ref + " stone";
      }
    } else {
      return "blockdata " + ref + " {auto:0b}";
    }
  }

  private ReferencingCommand newReferencingStartCommand(boolean conditional, int relative) {
    return new ReferencingCommand(getStartCommand(REF), conditional, relative);
  }

  private ReferencingCommand newReferencingStopCommand(boolean conditional, int relative) {
    return new ReferencingCommand(getStopCommand(REF), conditional, relative);
  }

  private void addRestartBackref(List<ChainLink> commands, ChainLink chainLink,
      boolean conditional) {
    commands.add(newReferencingStopCommand(conditional, getCountToRef(chainLink)));
    commands.add(newReferencingStartCommand(true, getCountToRef(chainLink)));
  }

  private void addTransmitterReceiverCombo(List<ChainLink> commands, boolean internal) {
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(internal));
      commands.add(new InternalCommand(getStopCommand("${this - 1}"), Mode.IMPULSE));
    } else {
      commands.add(new InternalCommand(getStopCommand("~ ~ ~"), Mode.IMPULSE));
    }
  }

  @Override
  public ChainContainer visitProgram(MplProgram program) {
    this.program = program;
    Orientation3D orientation = program.getOrientation();
    Coordinate3D max = program.getMax();
    File file = program.getProgramFile();
    CommandChain install = visitUnInstall("install", file, program.getInstall());
    CommandChain uninstall = visitUnInstall("uninstall", file, program.getUninstall());

    List<CommandChain> chains = new ArrayList<>(program.getProcesses().size());
    for (MplProcess process : program.getProcesses()) {
      CommandChain chain = process.accept(this);
      if (chain != null) {
        chains.add(chain);
      }
    }
    if (breakpoint != null) {
      addBreakpointProcess(program);
    }
    return new ChainContainer(orientation, max, install, uninstall, chains, program.getHash());
  }

  private @Nullable CommandChain visitUnInstall(String name, File programFile,
      @Nullable MplProcess process) {
    if (process == null) {
      process =
          new MplProcess(name, new MplSource(programFile, new CommonToken(MplLexer.PROCESS), ""));
    }
    return process.accept(this);
  }

  private void addBreakpointProcess(MplProgram program) {
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
    program.addProcess(process);
    process.accept(this);
  }

  @Override
  public @Nullable CommandChain visitProcess(MplProcess process) {
    if (process.getType() == INLINE) {
      return null;
    }
    List<ChainPart> chainParts = new CopyScope().copy(process.getChainParts());
    List<ChainLink> commands = new ArrayList<>(chainParts.size());
    boolean containsSkip = containsHighlevelSkip(chainParts);
    String name = process.getName();
    if (name != null) {
      if (process.isRepeating()) {
        if (options.hasOption(TRANSMITTER)) {
          commands.add(new MplSkip());
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
        addTransmitterReceiverCombo(commands, false);
      }
    } else if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip());
    }
    for (ChainPart chainPart : chainParts) {
      commands.addAll(chainPart.accept(this));
    }
    if (process.isRepeating() && containsSkip) {
      addRestartBackref(commands, commands.get(0), false);
    }
    if (!process.isRepeating() && name != null && !"install".equals(name)
        && !"uninstall".equals(name)) {
      new MplNotify(name, process.getSource()).accept(this);
    }
    return new CommandChain(name, commands, process.getTags());
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
   * such a process, this method returns true, otherwise returns false and adds a compiler warning.
   *
   * @param chainpart where to display the warning
   * @param processName the required process
   * @return true if the process was found, false otherwise
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

  /**
   * Checks if the given {@link ChainPart} has the {@link Conditional#INVERT INVERT} modifier. If it
   * does, an {@link InvertingCommand} is added to {@link #commands}. If {@code chainPart} does not
   * have predecessor an {@link IllegalStateException} is thrown.
   *
   * @param chainPart the {@link ModifiableChainPart} to check
   * @throws IllegalStateException if {@code chainPart} does not have predecessor
   * @see ModifiableChainPart#getPrevious()
   */
  public static void visitPossibleInvert(List<? super InvertingCommand> commands,
      ModifiableChainPart chainPart) throws IllegalStateException {
    if (chainPart.getConditional() == Conditional.INVERT) {
      Dependable previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      commands.add(new InvertingCommand(previous));
    }
  }

  @Override
  public List<ChainLink> visitCommand(MplCommand command) {
    List<ChainLink> result = new ArrayList<>(2);
    visitPossibleInvert(result, command);

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

    MplWaitfor mplWaitfor = new MplWaitfor(processName, modifier, mplCall.getSource());
    result.addAll(mplStart.accept(this));
    result.addAll(mplWaitfor.accept(this));
    return result;
  }

  @Override
  public List<ChainLink> visitStart(MplStart start) {
    List<ChainLink> result = new ArrayList<>(2);
    String selector = start.getSelector();
    String processName = selector.substring(8, selector.length() - 1);
    checkProcessExists(start, processName);
    checkNotInlineProcess(start, processName);
    visitPossibleInvert(result, start);

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
    visitPossibleInvert(result, stop);

    String command = "execute " + selector + " ~ ~ ~ " + getStopCommand("~ ~ ~");
    result.add(new Command(command, stop));
    return result;
  }

  @Override
  public List<ChainLink> visitWaitfor(MplWaitfor waitfor) {
    List<ChainLink> result = new ArrayList<>();
    String event = waitfor.getEvent();
    // boolean triggeredByProcess = program.streamProcesses()//
    // .anyMatch(p -> event.equals(p.getName()));
    // if (!triggeredByProcess) {
    // boolean triggeredByNotify = program.streamProcesses()//
    // .flatMap(p -> p.getChainParts().stream())//
    // .anyMatch(c -> {
    // if (c instanceof MplNotify)
    // return event.equals(((MplNotify) c).getEvent());
    // return false;
    // });
    // if (!triggeredByNotify) {
    // context.addWarning(new CompilerException(waitfor.getSource(),
    // "The event " + event + " is never triggered"));
    // }
    // }
    checkNotInlineProcess(waitfor, event);

    ReferencingCommand summon = new ReferencingCommand("summon ArmorStand " + REF + " {CustomName:"
        + event + NOTIFY + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

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
        result.add(new InvertingCommand(CHAIN));
        result.add(jump);
      } else { // conditional == INVERT
        jump.setRelative(3);
        summon.setRelative(1);
        result.add(jump);
        result.add(new InvertingCommand(CHAIN));
        result.add(summon);
      }
    }
    addTransmitterReceiverCombo(result, false);
    return result;
  }

  @Override
  public List<ChainLink> visitNotify(MplNotify notify) {
    List<ChainLink> result = new ArrayList<>(3);
    String event = notify.getEvent();
    // boolean used = program.streamProcesses()//
    // .flatMap(p -> p.getChainParts().stream())//
    // .anyMatch(c -> {
    // if (c instanceof MplWaitfor)
    // return event.equals(((MplWaitfor) c).getEvent());
    // return false;
    // });
    // if (!used) {
    // context.addWarning(
    // new CompilerException(notify.getSource(), "The event " + event + " is never used"));
    // }
    visitPossibleInvert(result, notify);

    boolean conditional = notify.isConditional();
    result.add(new InternalCommand(
        "execute @e[name=" + event + NOTIFY + "] ~ ~ ~ " + getStartCommand("~ ~ ~"), conditional));
    result.add(new Command("kill @e[name=" + event + NOTIFY + "]", conditional));
    return result;
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

    ReferencingCommand summon = new ReferencingCommand("summon ArmorStand " + REF + " {CustomName:"
        + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", conditional);


    if (intercept.getConditional() == UNCONDITIONAL) {
      summon.setRelative(1);
      result.add(entitydata);
      result.add(summon);
    } else {
      ReferencingCommand jump = new ReferencingCommand(getStartCommand(REF), true);
      if (intercept.getConditional() == CONDITIONAL) {
        summon.setRelative(3);
        jump.setRelative(1);
        result.add(entitydata);
        result.add(summon);
        result.add(new InvertingCommand(CHAIN));
        result.add(jump);
      } else { // conditional == INVERT
        jump.setRelative(4);
        summon.setRelative(1);
        result.add(jump);
        result.add(new InvertingCommand(CHAIN));
        result.add(entitydata);
        result.add(summon);
      }
    }
    addTransmitterReceiverCombo(result, false);
    result.add(new InternalCommand("kill @e[name=" + event + ",r=2]"));
    result.add(new InternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
    return result;
  }

  @Override
  public List<ChainLink> visitBreakpoint(MplBreakpoint mplBreakpoint) {
    if (!options.hasOption(DEBUG)) {
      return Collections.emptyList();
    }
    List<ChainLink> result = new ArrayList<>();
    this.breakpoint = mplBreakpoint.getSource();

    visitPossibleInvert(result, mplBreakpoint);

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

  @Override
  public List<ChainLink> visitIf(MplIf mplIf) {
    return new MplIfVisitor(context).visitIf(mplIf);
  }

  private Deque<MplWhile> loops = new ArrayDeque<>();

  @Override
  public List<ChainLink> visitWhile(MplWhile mplWhile) {
    loops.push(mplWhile);

    String condition = mplWhile.getCondition();
    boolean hasInitialCondition = condition != null && !mplWhile.isTrailing();
    if (hasInitialCondition) {
      visitPossibleInvert(mplWhile);
    }

    Deque<ChainPart> chainParts = mplWhile.getChainParts();
    if (chainParts.isEmpty()) {
      chainParts.add(new MplCommand("", mplWhile.getSource()));
    }

    int firstIndex = commands.size();
    if (hasInitialCondition) {
      commands.add(new Command(condition));
    }

    ReferencingCommand init = new ReferencingCommand(getStartCommand(REF));
    ReferencingCommand skip = new ReferencingCommand(getStartCommand(REF), true);

    if (!hasInitialCondition && !mplWhile.isConditional()) {
      commands.add(init);
    } else {
      init.setConditional(true);

      boolean isNormal = hasInitialCondition && !mplWhile.isNot();
      if (isNormal || !hasInitialCondition && mplWhile.getConditional() == CONDITIONAL) {
        commands.add(init);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(skip);
      } else {
        commands.add(skip);
        commands.add(new InvertingCommand(CHAIN));
        commands.add(init);
      }
    }
    ((Command) commands.get(firstIndex)).setModifier(mplWhile);

    // From here the next command will be the entry point for the loop
    init.setRelative(-getCountToRef(init));
    int entryIndex = commands.size();

    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(true));
    }
    try {
      ChainPart first = chainParts.peek();
      if (options.hasOption(TRANSMITTER) && first instanceof MplWhile) {
        if (((MplWhile) first).getCondition() == null) {
          first = new MplCommand("", mplWhile.getSource());
          chainParts.push(first);
        }
      }
      first.setMode(IMPULSE);
      first.setNeedsRedstone(true);
    } catch (IllegalModifierException ex) {
      throw new IllegalStateException("while cannot contain skip", ex);
    }
    boolean dontRestart = false;
    for (ChainPart chainPart : chainParts) {
      chainPart.accept(this);
      if (chainPart instanceof MplBreak || chainPart instanceof MplContinue) {
        if (!((ModifiableChainPart) chainPart).isConditional()) {
          dontRestart = true;
          break;
        }
      }
    }
    ChainLink entryPoint = commands.get(entryIndex);

    if (!dontRestart) {
      if (condition == null) {
        addRestartBackref(entryPoint, false);
      } else {
        commands.add(new Command(condition));
        if (!mplWhile.isNot()) {
          addContinueLoop(mplWhile).setConditional(true);
          commands.add(new InvertingCommand(CHAIN));
          addBreakLoop(mplWhile).setConditional(true);
        } else {
          addBreakLoop(mplWhile).setConditional(true);
          commands.add(new InvertingCommand(CHAIN));
          addContinueLoop(mplWhile).setConditional(true);
        }
      }
    }
    // From here the next command will be the exit point of the loop
    int exitIndex = commands.size();
    try {
      skip.setRelative(-getCountToRef(skip));
    } catch (IllegalArgumentException ex) {
      // If skip was not added the reference does not have to be set
    }
    addTransmitterReceiverCombo(true);
    ChainLink exitPoint = commands.get(exitIndex);

    for (Iterator<LoopRef> it = loopRefs.iterator(); it.hasNext();) {
      LoopRef loopRef = it.next();
      if (loopRef.getLoop() == mplWhile) {
        loopRef.setEntryPoint(entryPoint);
        loopRef.setExitPoint(exitPoint);
        it.remove();
      }
    }

    loops.pop();
  }

  private List<LoopRef> loopRefs = new ArrayList<>();

  private interface LoopRef {
    MplWhile getLoop();

    default void setEntryPoint(ChainLink entryPoint) {}

    default void setExitPoint(ChainLink exitPoint) {}
  }

  public void setRef(ReferencingCommand command, ChainLink reference) {
    command.setRelative(getCountToRef(reference) - getCountToRef(command));
  }

  @Override
  public List<ChainLink> visitBreak(MplBreak mplBreak) {
    MplWhile loop = mplBreak.getLoop();
    // FIXME: ein command von break MUSS nicht internal sein (bei unconditional)
    Conditional conditional = mplBreak.getConditional();
    if (conditional == UNCONDITIONAL) {
      addBreakLoop(loop).setModifier(mplBreak);
      return;
    }
    ReferencingCommand dontBreak = new ReferencingCommand(getStartCommand(REF), true);
    if (conditional == CONDITIONAL) {
      addBreakLoop(loop).setModifier(mplBreak);
      commands.add(new InvertingCommand(CHAIN));
      commands.add(dontBreak);
    } else {
      dontBreak.setModifier(mplBreak);
      commands.add(dontBreak);
      commands.add(new InvertingCommand(CHAIN));
      addBreakLoop(loop).setConditional(true);
    }
    dontBreak.setRelative(-getCountToRef(dontBreak));
    addTransmitterReceiverCombo(false);

  }

  @Override
  public List<ChainLink> visitContinue(MplContinue mplContinue) {
    MplWhile loop = mplContinue.getLoop();
    // FIXME: ein command von continue MUSS nicht internal sein (bei unconditional)
    Conditional conditional = mplContinue.getConditional();
    String condition = loop.getCondition();
    if (conditional == UNCONDITIONAL) {
      if (condition != null) {
        commands.add(new InternalCommand(condition, mplContinue));
        addContinueLoop(loop).setConditional(true);
        commands.add(new InvertingCommand(CHAIN));
        addBreakLoop(loop).setConditional(true);
      } else {
        addContinueLoop(loop).setModifier(mplContinue);
      }
      return;
    }
    MplSource source = mplContinue.getSource();
    MplIf outerIf = new MplIf(false, null, source);
    outerIf.setConditional(mplContinue.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    outerIf.setPrevious(mplContinue.getPrevious());
    outerIf.enterThen();
    if (condition != null) {
      MplIf innerIf = new MplIf(false, condition, source);
      innerIf.enterThen();
      innerIf.add(new MplContinueLoop(loop, source));
      innerIf.enterElse();
      innerIf.add(new MplBreakLoop(loop, source));
      outerIf.add(innerIf);
    } else {
      outerIf.add(new MplContinueLoop(loop, source));
    }
    outerIf.enterElse();
    // Mark this command to find it later no user can create such a command
    outerIf.add(new MplCommand("//", source));

    if (conditional == INVERT) {
      outerIf.switchThenAndElse();
    }
    outerIf.accept(this);

    for (int i = commands.size() - 1; i >= 0; i--) {
      ChainLink chainLink = commands.get(i);
      if (chainLink instanceof Command) {
        if ("/".equals(((Command) chainLink).getCommand())) {
          commands.set(i, new ReferencingCommand(getStartCommand(REF), true, commands.size() - i));
          break;
        }
      }
    }
    addTransmitterReceiverCombo(false);
  }

  public void visitBreakLoop(MplBreakLoop mplBreakLoop) {
    addBreakLoop(mplBreakLoop.getLoop()).setModifier(mplBreakLoop);
  }

  private ReferencingCommand addBreakLoop(MplWhile loop) {
    ReferencingCommand result = addSkipLoop(loop);
    for (MplWhile innerLoop : loops) {
      addStopLoop(innerLoop).setConditional(true);
      if (innerLoop == loop) {
        break;
      }
    }
    return result;
  }

  public void visitContinueLoop(MplContinueLoop mplContinueLoop) {
    addContinueLoop(mplContinueLoop.getLoop()).setModifier(mplContinueLoop);
  }

  private ReferencingCommand addContinueLoop(MplWhile loop) {
    Iterator<MplWhile> it = loops.iterator();
    MplWhile innerestLoop = it.next();
    ReferencingCommand result = addStopLoop(innerestLoop);
    if (innerestLoop != loop) {
      while (it.hasNext()) {
        MplWhile innerLoop = it.next();
        addStopLoop(innerLoop).setConditional(true);
        if (innerLoop == loop) {
          break;
        }
      }
    }
    addStartLoop(loop).setConditional(true);
    return result;
  }

  private ReferencingCommand addStartLoop(MplWhile loop) {
    ReferencingCommand startLoop = new ReferencingCommand(getStartCommand(REF));
    commands.add(startLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setEntryPoint(ChainLink entryPoint) {
        setRef(startLoop, entryPoint);
      }
    });
    return startLoop;
  }

  private ReferencingCommand addStopLoop(MplWhile loop) {
    ReferencingCommand stopLoop = new ReferencingCommand(getStopCommand(REF));
    commands.add(stopLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setEntryPoint(ChainLink entryPoint) {
        setRef(stopLoop, entryPoint);
      }
    });
    return stopLoop;
  }

  private ReferencingCommand addSkipLoop(MplWhile loop) {
    ReferencingCommand skipLoop = new ReferencingCommand(getStartCommand(REF));
    commands.add(skipLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setExitPoint(ChainLink exitPoint) {
        setRef(skipLoop, exitPoint);
      }
    });
    return skipLoop;
  }

}
