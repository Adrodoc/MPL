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
package de.adrodoc55.minecraft.mpl.ast;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.VisibleForTesting;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;
import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
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
import de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
public class MplAstVisitorImpl implements MplAstVisitor {
  private ChainContainer container;
  @VisibleForTesting
  List<CommandChain> chains = new ArrayList<>();
  @VisibleForTesting
  List<ChainLink> commands = new ArrayList<>();
  @VisibleForTesting
  final CompilerOptions options;

  private boolean addBreakpointProcess;

  public MplAstVisitorImpl(CompilerOptions options) {
    this.options = checkNotNull(options, "options == null!");
  }

  @Override
  public ChainContainer getResult() {
    return container;
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
      return "setblock " + ref + " stone";
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

  private void addRestartBackref(ChainLink chainLink, boolean conditional) {
    commands.add(newReferencingStopCommand(conditional, getCountToRef(chainLink)));
    commands.add(newReferencingStartCommand(true, getCountToRef(chainLink)));
  }

  private void addTransmitterReceiverCombo(boolean internal) {
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new MplSkip(internal));
      commands.add(new InternalCommand(getStopCommand("${this - 1}"), Mode.IMPULSE));
    } else {
      commands.add(new InternalCommand(getStopCommand("~ ~ ~"), Mode.IMPULSE));
    }
  }

  @Override
  public void visitProgram(MplProgram program) {
    chains = new ArrayList<>(1);
    Orientation3D orientation = program.getOrientation();
    Coordinate3D max = program.getMax();
    CommandChain install = visitUnInstall(program.getInstall());
    CommandChain uninstall = visitUnInstall(program.getUninstall());

    chains = new ArrayList<>(program.getProcesses().size());
    for (MplProcess process : program.getProcesses()) {
      process.accept(this);
    }
    if (addBreakpointProcess) {
      addBreakpointProcess(program);
    }
    container = new ChainContainer(orientation, max, install, uninstall, chains, program.getHash());
  }

  private CommandChain visitUnInstall(MplProcess process) {
    process.accept(this);
    CommandChain chain = chains.get(0);
    chains.remove(0);
    return chain;
  }

  private void addBreakpointProcess(MplProgram program) {
    String hash = program.getHash();
    MplProcess process = new MplProcess("breakpoint");
    List<ChainPart> commands = new ArrayList<>();

    // Pause
    if (!options.hasOption(TRANSMITTER)) {
      commands.add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ clone ~ ~ ~ ~ ~ ~ ~ ~1 ~"));
    }
    commands.add(new MplCommand("/tp @e[tag=" + hash + "] ~ ~1 ~"));
    if (!options.hasOption(TRANSMITTER)) {
      commands
          .add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ blockdata ~ ~ ~ {Command:}"));
    }

    commands.add(new MplCommand(
        "tellraw @a [{\"text\":\"[tp to breakpoint]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tp @p @e[name=breakpoint_NOTIFY,c=-1]\"}},{\"text\":\" \"},{\"text\":\"[continue program]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/execute @e[name=breakpoint_CONTINUE] ~ ~ ~ "
            + getStartCommand("~ ~ ~") + "\"}}]"));

    commands.add(new MplWaitfor("breakpoint_CONTINUE"));
    commands.add(new MplCommand("/kill @e[name=breakpoint_CONTINUE]"));

    // Unpause
    commands.add(
        new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ clone ~ ~ ~ ~ ~ ~ ~ ~-1 ~ force move"));
    commands.add(new MplCommand("/tp @e[tag=" + hash + "] ~ ~-1 ~"));
    if (!options.hasOption(TRANSMITTER)) {
      commands.add(new MplCommand("/execute @e[tag=" + hash + "] ~ ~ ~ blockdata ~ ~ ~ {Command:"
          + getStopCommand("~ ~ ~") + "}"));
    }

    commands.add(new MplNotify("breakpoint"));

    process.setChainParts(commands);
    program.addProcess(process);
    process.accept(this);
  }

  @Override
  public void visitProcess(MplProcess process) {
    List<ChainPart> chainParts = process.getChainParts();
    commands = new ArrayList<>(chainParts.size());
    boolean containsSkip = containsHighlevelSkip(process);
    if (process.isRepeating()) {
      if (options.hasOption(TRANSMITTER)) {
        commands.add(new MplSkip());
      }
      if (process.getChainParts().isEmpty()) {
        process.add(new MplCommand(""));
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
      addTransmitterReceiverCombo(false);
    }
    for (ChainPart chainPart : chainParts) {
      chainPart.accept(this);
    }
    if (process.isRepeating() && containsSkip) {
      addRestartBackref(commands.get(0), false);
    }
    chains.add(new CommandChain(process.getName(), commands));
  }

  private boolean containsHighlevelSkip(MplProcess process) {
    List<ChainPart> chainParts = process.getChainParts();
    for (ChainPart chainPart : chainParts) {
      if (chainPart instanceof MplWaitfor || chainPart instanceof MplIntercept
          || chainPart instanceof MplBreakpoint) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given {@link ChainPart} has the {@link Conditional#INVERT INVERT} modifier. If it
   * does, an {@link InvertingCommand} is added to {@link #commands}. If {@code chainPart} does not
   * have predecessor an {@link IllegalStateException} is thrown.
   *
   * @param chainPart if {@code chainPart} does not have predecessor
   * @throws IllegalStateException
   * @see ModifiableChainPart#getPrevious()
   */
  protected void visitPossibleInvert(ModifiableChainPart chainPart) throws IllegalStateException {
    if (chainPart.getConditional() == Conditional.INVERT) {
      Dependable previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      commands.add(new InvertingCommand(previous.getModeForInverting()));
    }
  }

  @Override
  public void visitCommand(MplCommand command) {
    visitPossibleInvert(command);

    String cmd = command.getCommand();
    commands.add(new Command(cmd, command));
  }

  @Override
  public void visitStart(MplStart start) {
    visitPossibleInvert(start);

    String process = start.getProcess();
    String command = "execute @e[name=" + process + "] ~ ~ ~ " + getStartCommand("~ ~ ~");
    commands.add(new Command(command, start));
  }

  @Override
  public void visitStop(MplStop stop) {
    visitPossibleInvert(stop);

    String process = stop.getProcess();
    String command = "execute @e[name=" + process + "] ~ ~ ~ " + getStopCommand("~ ~ ~");
    commands.add(new Command(command, stop));
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
      ReferencingCommand jump = new ReferencingCommand(getStartCommand(REF), true);
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
    addTransmitterReceiverCombo(false);
  }

  @Override
  public void visitNotify(MplNotify notify) {
    visitPossibleInvert(notify);

    String process = notify.getProcess();
    boolean conditional = notify.isConditional();
    commands.add(new InternalCommand(
        "execute @e[name=" + process + NOTIFY + "] ~ ~ ~ " + getStartCommand("~ ~ ~"),
        conditional));
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
      ReferencingCommand jump = new ReferencingCommand(getStartCommand(REF), true);
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
    addTransmitterReceiverCombo(false);
    commands.add(new InternalCommand("kill @e[name=" + event + ",r=2]"));
    commands.add(new InternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
  }

  @Override
  public void visitBreakpoint(MplBreakpoint breakpoint) {
    if (!options.hasOption(DEBUG)) {
      return;
    }
    addBreakpointProcess = true;

    visitPossibleInvert(breakpoint);

    boolean cond = breakpoint.isConditional();
    commands.add(new InternalCommand("say " + breakpoint.getMessage(), cond));

    ModifierBuffer modifier = new ModifierBuffer();
    modifier.setConditional(cond ? CONDITIONAL : UNCONDITIONAL);
    visitStart(new MplStart("breakpoint", modifier));
    visitWaitfor(new MplWaitfor("breakpoint" + NOTIFY, modifier));
  }

  @Override
  public void visitSkip(MplSkip skip) {
    commands.add(skip);
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
  public void visitIf(MplIf mplIf) {
    visitPossibleInvert(mplIf);

    String condition = mplIf.getCondition();
    Command ref;
    if (condition != null) {
      ref = new InternalCommand(condition, mplIf);
      commands.add(ref);
    } else {
      ref = (Command) commands.get(commands.size() - 1);
    }
    if (needsNormalizer(mplIf)) {
      ref = new NormalizingCommand();
      commands.add(ref);
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
      commands.add(getConditionReference(first));
    }
    for (IfNestingLayer layer : requiredReferences) {
      ReferencingTestforSuccessCommand ref = getConditionReference(layer);
      ref.setConditional(true);
      commands.add(ref);
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

  @Override
  public void visitWhile(MplWhile mplWhile) {
    String condition = mplWhile.getCondition();
    boolean hasInitialCondition = condition != null && !mplWhile.isTrailing();
    if (hasInitialCondition) {
      visitPossibleInvert(mplWhile);
    }

    Deque<ChainPart> chainParts = mplWhile.getChainParts();
    if (chainParts.isEmpty()) {
      chainParts.add(new MplCommand(""));
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
          first = new MplCommand("");
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
  }

  private List<LoopRef> loopRefs = new ArrayList<>();

  private interface LoopRef {
    MplWhile getLoop();

    void setEntryPoint(ChainLink entryPoint);

    void setExitPoint(ChainLink exitPoint);
  }

  public void setRef(ReferencingCommand command, ChainLink reference) {
    command.setRelative(getCountToRef(reference) - getCountToRef(command));
  }

  public void visitBreakLoop(MplBreakLoop mplBreakLoop) {
    addBreakLoop(mplBreakLoop.getLoop()).setModifier(mplBreakLoop);
  }

  private ReferencingCommand addBreakLoop(MplWhile loop) {
    ReferencingCommand continueAfterLoop = new ReferencingCommand(getStartCommand(REF));
    ReferencingCommand stopLoop = new ReferencingCommand(getStopCommand(REF), true);
    commands.add(continueAfterLoop);
    commands.add(stopLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public void setExitPoint(ChainLink exitPoint) {
        setRef(continueAfterLoop, exitPoint);
      }

      @Override
      public void setEntryPoint(ChainLink entryPoint) {
        setRef(stopLoop, entryPoint);
      }

      @Override
      public MplWhile getLoop() {
        return loop;
      }
    });
    return continueAfterLoop;
  }

  @Override
  public void visitBreak(MplBreak mplBreak) {
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

  public void visitContinueLoop(MplContinueLoop mplContinueLoop) {
    addContinueLoop(mplContinueLoop.getLoop()).setModifier(mplContinueLoop);
  }

  private ReferencingCommand addContinueLoop(MplWhile loop) {
    ReferencingCommand stopLoop = new ReferencingCommand(getStopCommand(REF));
    ReferencingCommand startLoop = new ReferencingCommand(getStartCommand(REF), true);
    commands.add(stopLoop);
    commands.add(startLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public void setExitPoint(ChainLink exitPoint) {}

      @Override
      public void setEntryPoint(ChainLink entryPoint) {
        setRef(stopLoop, entryPoint);
        setRef(startLoop, entryPoint);
      }

      @Override
      public MplWhile getLoop() {
        return loop;
      }
    });
    return stopLoop;
  }

  @Override
  public void visitContinue(MplContinue mplContinue) {
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
    MplIf outerIf = new MplIf(false, null);
    outerIf.setConditional(mplContinue.isConditional() ? CONDITIONAL : UNCONDITIONAL);
    outerIf.setPrevious(mplContinue.getPrevious());
    outerIf.enterThen();
    if (condition != null) {
      MplIf innerIf = new MplIf(false, condition);
      innerIf.enterThen();
      innerIf.add(new MplContinueLoop(loop));
      innerIf.enterElse();
      innerIf.add(new MplBreakLoop(loop));
      outerIf.add(innerIf);
    } else {
      outerIf.add(new MplContinueLoop(loop));
    }
    outerIf.enterElse();
    // Mark this command to find it later no user can create such a command
    outerIf.add(new MplCommand("//"));

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

}
