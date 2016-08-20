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
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.InternalMplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ResolveableCommand;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;
import lombok.Getter;

/**
 * @author Adrodoc55
 */
public class MplWhileVisitor extends MplMainAstVisitor {
  public MplWhileVisitor(MplCompilerContext context, MplProgram program) {
    super(context);
    this.program = program;
  }

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
