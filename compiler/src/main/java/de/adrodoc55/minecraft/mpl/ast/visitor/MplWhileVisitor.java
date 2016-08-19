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

import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModifiableChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreakLoop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinueLoop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.interpretation.IllegalModifierException;

/**
 * @author Adrodoc55
 */
public class MplWhileVisitor extends MplAstVisitorImpl {
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

    Deque<ChainPart> chainParts = new ArrayDeque<>(mplWhile.getChainParts());
    if (chainParts.isEmpty()) {
      chainParts.add(new MplCommand("", mplWhile.getSource()));
    }

    int firstIndex = result.size();
    if (hasInitialCondition) {
      result.add(new Command(condition));
    }

    ReferencingCommand init = new ReferencingCommand(getStartCommand(REF));
    ReferencingCommand skip = new ReferencingCommand(getStartCommand(REF), true);

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

    // From here the next command will be the entry point for the loop
    init.setRelative(-getCountToRef(result, init));
    int entryIndex = result.size();

    if (options.hasOption(TRANSMITTER)) {
      result.add(new MplSkip(true));
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
      result.addAll(chainPart.accept(this));
      if (chainPart instanceof MplBreak || chainPart instanceof MplContinue) {
        if (!((ModifiableChainPart) chainPart).isConditional()) {
          dontRestart = true;
          break;
        }
      }
    }
    ChainLink entryPoint = result.get(entryIndex);

    if (!dontRestart) {
      if (condition == null) {
        addRestartBackref(result, entryPoint, false);
      } else {
        result.add(new Command(condition));
        if (!mplWhile.isNot()) {
          addContinueLoop(result, mplWhile).setConditional(true);
          result.add(newInvertingCommand(CHAIN));
          addBreakLoop(result, mplWhile).setConditional(true);
        } else {
          addBreakLoop(result, mplWhile).setConditional(true);
          result.add(newInvertingCommand(CHAIN));
          addContinueLoop(result, mplWhile).setConditional(true);
        }
      }
    }
    // From here the next command will be the exit point of the loop
    int exitIndex = result.size();
    try {
      skip.setRelative(-getCountToRef(result, skip));
    } catch (IllegalArgumentException ex) {
      // If skip was not added the reference does not have to be set
    }
    addTransmitterReceiverCombo(result, true);
    ChainLink exitPoint = result.get(exitIndex);

    for (Iterator<LoopRef> it = loopRefs.iterator(); it.hasNext();) {
      LoopRef loopRef = it.next();
      if (loopRef.getLoop() == mplWhile) {
        loopRef.setEntryPoint(result, entryPoint);
        loopRef.setExitPoint(result, exitPoint);
        it.remove();
      }
    }

    loops.pop();
    return result;
  }

  private List<LoopRef> loopRefs = new ArrayList<>();

  private interface LoopRef {
    MplWhile getLoop();

    default void setEntryPoint(List<ChainLink> commands, ChainLink entryPoint) {}

    default void setExitPoint(List<ChainLink> commands, ChainLink exitPoint) {}
  }

  public void setRef(List<ChainLink> commands, ReferencingCommand command, ChainLink reference) {
    command.setRelative(getCountToRef(commands, reference) - getCountToRef(commands, command));
  }

  @Override
  public List<ChainLink> visitBreak(MplBreak mplBreak) {
    List<ChainLink> result = new ArrayList<>();
    MplWhile loop = mplBreak.getLoop();
    // FIXME: ein command von break MUSS nicht internal sein (bei unconditional)
    Conditional conditional = mplBreak.getConditional();
    if (conditional == UNCONDITIONAL) {
      addBreakLoop(result, loop).setModifier(mplBreak);
      return result;
    }
    ReferencingCommand dontBreak = new ReferencingCommand(getStartCommand(REF), true);
    if (conditional == CONDITIONAL) {
      addBreakLoop(result, loop).setModifier(mplBreak);
      result.add(newInvertingCommand(CHAIN));
      result.add(dontBreak);
    } else {
      dontBreak.setModifier(mplBreak);
      result.add(dontBreak);
      result.add(newInvertingCommand(CHAIN));
      addBreakLoop(result, loop).setConditional(true);
    }
    dontBreak.setRelative(-getCountToRef(result, dontBreak));
    addTransmitterReceiverCombo(result, false);
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
        result.add(new InternalCommand(condition, mplContinue));
        addContinueLoop(result, loop).setConditional(true);
        result.add(newInvertingCommand(CHAIN));
        addBreakLoop(result, loop).setConditional(true);
      } else {
        addContinueLoop(result, loop).setModifier(mplContinue);
      }
      return result;
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

    for (int i = result.size() - 1; i >= 0; i--) {
      ChainLink chainLink = result.get(i);
      if (chainLink instanceof Command) {
        if ("/".equals(((Command) chainLink).getCommand())) {
          result.set(i, new ReferencingCommand(getStartCommand(REF), true, result.size() - i));
          break;
        }
      }
    }
    addTransmitterReceiverCombo(result, false);
    return result;
  }

  public List<ChainLink> visitBreakLoop(MplBreakLoop mplBreakLoop) {
    List<ChainLink> result = new ArrayList<>();
    addBreakLoop(result, mplBreakLoop.getLoop()).setModifier(mplBreakLoop);
    return result;
  }

  private ReferencingCommand addBreakLoop(List<ChainLink> commands, MplWhile loop) {
    ReferencingCommand result = addSkipLoop(commands, loop);
    for (MplWhile innerLoop : loops) {
      addStopLoop(commands, innerLoop).setConditional(true);
      if (innerLoop == loop) {
        break;
      }
    }
    return result;
  }

  public List<ChainLink> visitContinueLoop(MplContinueLoop mplContinueLoop) {
    List<ChainLink> result = new ArrayList<>();
    addContinueLoop(result, mplContinueLoop.getLoop()).setModifier(mplContinueLoop);
    return result;
  }

  private ReferencingCommand addContinueLoop(List<ChainLink> commands, MplWhile loop) {
    Iterator<MplWhile> it = loops.iterator();
    MplWhile innerestLoop = it.next();
    ReferencingCommand result = addStopLoop(commands, innerestLoop);
    if (innerestLoop != loop) {
      while (it.hasNext()) {
        MplWhile innerLoop = it.next();
        addStopLoop(commands, innerLoop).setConditional(true);
        if (innerLoop == loop) {
          break;
        }
      }
    }
    addStartLoop(commands, loop).setConditional(true);
    return result;
  }

  private ReferencingCommand addStartLoop(List<ChainLink> result, MplWhile loop) {
    ReferencingCommand startLoop = new ReferencingCommand(getStartCommand(REF));
    result.add(startLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setEntryPoint(List<ChainLink> commands, ChainLink entryPoint) {
        setRef(commands, startLoop, entryPoint);
      }
    });
    return startLoop;
  }

  private ReferencingCommand addStopLoop(List<ChainLink> result, MplWhile loop) {
    ReferencingCommand stopLoop = new ReferencingCommand(getStopCommand(REF));
    result.add(stopLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setEntryPoint(List<ChainLink> commands, ChainLink entryPoint) {
        setRef(commands, stopLoop, entryPoint);
      }
    });
    return stopLoop;
  }

  private ReferencingCommand addSkipLoop(List<ChainLink> result, MplWhile loop) {
    ReferencingCommand skipLoop = new ReferencingCommand(getStartCommand(REF));
    result.add(skipLoop);
    loopRefs.add(new LoopRef() {
      @Override
      public MplWhile getLoop() {
        return loop;
      }

      @Override
      public void setExitPoint(List<ChainLink> commands, ChainLink exitPoint) {
        setRef(commands, skipLoop, exitPoint);
      }
    });
    return skipLoop;
  }

}
