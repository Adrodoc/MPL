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
package de.adrodoc55.minecraft.mpl.materialize.function;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.materialize.function.McFunction.toFullName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Joiner;

import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.InternalMplCommand;
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
import de.adrodoc55.minecraft.mpl.ast.visitor.MplAstVisitor;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.materialize.function.MplFunctionAstVisitor.Result;

/**
 * @author Adrodoc55
 */
public class MplFunctionAstVisitor implements MplAstVisitor<Result> {
  public static Set<McFunction> materialize(String processName, String mcFuncName,
      Iterable<? extends ChainPart> chainParts, MplFunctionAstVisitor.Context visitorContext) {
    HashSet<McFunction> result = new HashSet<>();
    McFunction function = new McFunction(processName + '/' + mcFuncName);
    result.add(function);
    MplFunctionAstVisitor visitor = new MplFunctionAstVisitor(processName, visitorContext);
    for (ChainPart chainPart : chainParts) {
      Result res = chainPart.accept(visitor);
      function.addAllCommands(res.getCommands());
      result.addAll(res.getSubFunctions());
    }
    return result;
  }

  static class Result {
    private final List<String> commands = new ArrayList<>();
    private final Set<McFunction> subFunctions = new HashSet<>();

    public void addCondition(String condition, int ifNumber) {
      add("stats entity @s set SuccessCount @s MPL_SUCCESS");
      add("scoreboard players set @s MPL_SUCCESS 0");
      add(condition);
      add("scoreboard players operation @s MPL_SUCCESS_COPY = @s MPL_SUCCESS");
      add("stats entity @s clear SuccessCount");
      add("scoreboard players tag @s[score_MPL_SUCCESS_COPY_min=1,tag=!mpl:cond" + ifNumber
          + "] add mpl:cond" + ifNumber + "");
    }

    public List<String> getCommands() {
      return Collections.unmodifiableList(commands);
    }

    public void add(String command) {
      commands.add(checkNotNull(command, "command == null!"));
    }

    public Set<McFunction> getSubFunctions() {
      return Collections.unmodifiableSet(subFunctions);
    }

    public void add(McFunction subFunction) {
      subFunctions.add(checkNotNull(subFunction, "subFunction == null!"));
    }

    public void addAll(Collection<? extends McFunction> subFunctions) {
      this.subFunctions.addAll(subFunctions);
    }
  }
  public interface Context {
    AtomicInteger getIfCounter();
  }

  private final Context context;
  private final String processName;

  public MplFunctionAstVisitor(String processName, Context context) {
    this.processName = checkNotNull(processName, "processName == null!");
    this.context = checkNotNull(context, "context == null!");
  }

  @Override
  public Result visitInternalCommand(InternalMplCommand mplCommand) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitCommand(MplCommand mplCommand) {
    Result result = new Result();
    result.add(Joiner.on("").join(mplCommand.getCommandParts()));
    return result;
  }

  @Override
  public Result visitCall(MplCall mplCall) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitStart(MplStart mplStart) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitStop(MplStop mplStop) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitWaitfor(MplWaitfor mplWaitfor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitNotify(MplNotify mplNotify) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitIntercept(MplIntercept mplIntercept) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitBreakpoint(MplBreakpoint mplBreakpoint) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitSkip(MplSkip mplSkip) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitIf(MplIf mplIf) {
    Result result = new Result();
    int ifNumber = context.getIfCounter().incrementAndGet();
    result.addCondition(mplIf.getCondition(), ifNumber);
    Deque<ChainPart> thenParts = mplIf.getThenParts();
    if (!thenParts.isEmpty()) {
      String thenFuncName = "then" + ifNumber;
      result.add("execute @s[tag=mpl:cond" + ifNumber + "] ~ ~ ~ function "
          + toFullName(processName, thenFuncName));
      result.addAll(materialize(processName, thenFuncName, thenParts, context));
    }
    Deque<ChainPart> elseParts = mplIf.getElseParts();
    if (!elseParts.isEmpty()) {
      String elseFuncName = "else" + ifNumber;
      result.add("execute @s[tag=!mpl:cond" + ifNumber + "] ~ ~ ~ function "
          + toFullName(processName, elseFuncName));
      result.addAll(materialize(processName, elseFuncName, elseParts, context));
    }
    return result;
  }

  @Override
  public Result visitWhile(MplWhile mplWhile) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitBreak(MplBreak mplBreak) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result visitContinue(MplContinue mplContinue) {
    // TODO Auto-generated method stub
    return null;
  }
}
