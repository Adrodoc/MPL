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
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.VisibleForTesting;

import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.ModeOwner;
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
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

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
    Orientation3D orientation = program.getOrientation();

    program.getInstall().accept(this);
    CommandChain install = chains.get(chains.size() - 1);
    chains.remove(chains.size() - 1);
    program.getUninstall().accept(this);
    CommandChain uninstall = chains.get(chains.size() - 1);
    chains.remove(chains.size() - 1);

    List<CommandChain> chains = new ArrayList<>(program.getProcesses().size());
    for (MplProcess process : program.getProcesses()) {
      process.accept(this);
    }
    container = new ChainContainer(orientation, install, uninstall, chains);
  }

  @Override
  public void visitProcess(MplProcess process) {
    commands = new ArrayList<>(process.getChainParts().size());
    if (options.hasOption(TRANSMITTER)) {
      commands.add(new Skip(false));
    }
    if (process.isRepeating()) {
      // if (chainParts.isEmpty()) {
      commands.add(new InternalCommand("", REPEAT, false));
      // } else {
      // ChainPart first = chainParts.get(0);
      // first.setMode(REPEAT);
      // }
    } else {
      if (options.hasOption(TRANSMITTER)) {
        commands.add(new InternalCommand("/setblock ${this - 1} stone", Mode.IMPULSE, false));
      } else {
        commands.add(new InternalCommand("/entitydata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
      }
    }
    for (ChainPart chainPart : process.getChainParts()) {
      chainPart.accept(this);
    }
    chains.add(new CommandChain(process.getName(), commands));
  }

  public void visitPossiblyConditional(PossiblyConditionalChainPart chainPart) {
    if (chainPart.getConditional() == Conditional.INVERT) {
      ModeOwner previous = chainPart.getPrevious();
      checkState(previous != null,
          "Cannot invert ChainPart; no previous command found for " + chainPart);
      InvertingCommand e = new InvertingCommand(previous.getMode());
      commands.add(e);
    }
  }

  @Override
  public void visitCommand(MplCommand command) {
    visitPossiblyConditional(command);

    String cmd = command.getCommand();
    Mode mode = command.getMode();
    boolean conditional = command.isConditional();
    boolean needsRedstone = command.needsRedstone();
    commands.add(new Command(cmd, mode, conditional, needsRedstone));
  }

  @Override
  public void visitStart(MplStart start) {
    visitPossiblyConditional(start);

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
    visitPossiblyConditional(stop);

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

      ReferencingCommand first, second;
      if (waitfor.getConditional() == CONDITIONAL) {
        first = summon;
        second = jump;
      } else { // conditional == INVERT
        first = jump;
        second = summon;
      }
      first.setRelative(3);
      second.setRelative(1);
      commands.add(first);
      commands.add(new InvertingCommand(CHAIN));
      commands.add(second);
    }

    if (options.hasOption(TRANSMITTER)) {
      commands.add(new Skip(false));
      commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    } else {
      commands.add(new InternalCommand("entitydata ~ ~ ~ {auto:0}", Mode.IMPULSE, false));
    }
  }

  @Override
  public void visitNotify(MplNotify notify) {
    visitPossiblyConditional(notify);

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
    if (intercept.isConditional()) {
      if (intercept.getConditional() == Conditional.CONDITIONAL) {
        commands.add(new InvertingCommand(intercept.getPrevious().getMode()));
      }
      commands.add(new InternalCommand("setblock ${this + 3} redstone_block", true));
    }
    commands.add(new InternalCommand(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}"));
    commands.add(new InternalCommand("summon ArmorStand ${this + 1} {CustomName:" + event
        + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"));
    commands.add(new Skip(false));
    commands.add(new InternalCommand("setblock ${this - 1} stone", Mode.IMPULSE, false));
    commands.add(new InternalCommand("kill @e[name=" + event + ",r=2]"));
    commands.add(new InternalCommand(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}"));
  }

  @Override
  public void visitBreakpoint(MplBreakpoint breakpoint) {
    visitPossiblyConditional(breakpoint);

    boolean cond = breakpoint.isConditional();
    commands.add(new InternalCommand("say " + breakpoint.getMessage(), cond));

    Conditional conditional = cond ? Conditional.CONDITIONAL : Conditional.UNCONDITIONAL;
    visitStart(new MplStart("breakpoint", conditional));
    visitWaitfor(new MplWaitfor("breakpoint" + NOTIFY, conditional));
  }

  @Override
  public void visitSkip(Skip skip) {
    commands.add(skip);
  }

  @Override
  public void visitIf(MplIf mplIf) {


  }

}
