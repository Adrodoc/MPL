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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.readLines;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.FUNCTION;
import static de.adrodoc55.minecraft.mpl.materialize.function.McFunction.toFullName;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ProcessCommandsHelper;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.materialize.function.MplFunctionAstVisitor.Context;

/**
 * @author Adrodoc55
 */
public class MplFunctionMaterializer extends ProcessCommandsHelper {
  public static final String ROOT_FUNCTION_NAME = "root";
  private final MplCompilerContext compilerContext;

  public MplFunctionMaterializer(MplCompilerContext compilerContext) {
    super(compilerContext.getOptions());
    this.compilerContext = checkNotNull(compilerContext, "compilerContext == null!");
  }

  public Set<McFunction> materialize(MplProgram program) {
    Set<McFunction> result = new HashSet<>();
    for (MplProcess process : program.getProcesses()) {
      result.addAll(materialize(process));
    }
    if (!result.isEmpty()) {
      addDefaultFunctions(result);
    }
    return result;
  }

  private static void addDefaultFunctions(Set<McFunction> result) {
    try {
      List<String> defaultFunctionNames = Arrays.asList("install", "uninstall", "new-scope");
      for (String functionName : defaultFunctionNames) {
        List<String> content =
            readLines(getResource("mpl/lang/function/" + functionName + ".mcfunction"), UTF_8);
        McFunction function = new McFunction(functionName);
        function.addAllCommands(content);
        result.add(function);
      }
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  public Set<McFunction> materialize(MplProcess process) {
    if (process.getType() != FUNCTION) {
      return Collections.emptySet();
    }
    String processName = process.getName();
    Set<McFunction> result = MplFunctionAstVisitor.materialize(processName, ROOT_FUNCTION_NAME,
        process.getChainParts(), newVisitorContext());
    McFunction callFunction = new McFunction(processName + '/' + "call");
    callFunction.addCommand("function mpl:new-scope");
    callFunction.addCommand("execute @e[name=mpl:new-scope] ~ ~ ~ function "
        + toFullName(processName, ROOT_FUNCTION_NAME));
    result.add(callFunction);
    return result;
  }

  private Context newVisitorContext() {
    return new MplFunctionAstVisitor.Context() {
      private final AtomicInteger ifCounter = new AtomicInteger();

      @Override
      public AtomicInteger getIfCounter() {
        return ifCounter;
      }
    };
  }
}
