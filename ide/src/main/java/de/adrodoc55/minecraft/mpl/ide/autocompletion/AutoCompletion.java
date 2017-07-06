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
package de.adrodoc55.minecraft.mpl.ide.autocompletion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.antlr.MplParser;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.FileContext;

/**
 * @author Adrodoc55
 */
public class AutoCompletion {

  private AutoCompletion() throws Throwable {
    throw new Throwable("Utils Classes cannot be instantiated!");
  }

  public static @Nullable AutoCompletionContext getContext(int index, String text) {
    ANTLRInputStream input = new ANTLRInputStream(text);
    MplLexer lexer = new MplLexer(input);
    lexer.removeErrorListeners();
    TokenStream tokens = new CommonTokenStream(lexer);
    MplParser parser = new MplParser(tokens);
    parser.removeErrorListeners();
    FileContext ctx = parser.file();

    AutoCompletionListener listener = new AutoCompletionListener(index);
    try {
      new ParseTreeWalker().walk(listener, ctx);
    } catch (ResultException earlyExit) {
      return earlyExit.getResult();
    }
    return null;
  }

  public static List<AutoCompletionAction> getOptions(int index, String text) {
    AutoCompletionContext context = getContext(index, text);
    ArrayList<AutoCompletionAction> options = new ArrayList<>();
    if (context == null) {
      return options;
    }
    Token token = context.getToken();
    for (AutoCompletionAction action : getAllActions(index, token)) {
      if (action.shouldBeProposed(context)) {
        options.add(action);
      }
    }
    return options;
  }

  private static List<AutoCompletionAction> getAllActions(int index, Token token) {
    List<AutoCompletionAction> actions = new ArrayList<>();
    actions.add(new NewIncludeAction(index, token));
    actions.add(new NewIfAction(index, token));
    actions.add(new NewIfElseAction(index, token));
    actions.add(new NewWhileAction(index, token));
    actions.add(new NewDoWhileAction(index, token));
    actions.add(new NewProcessAction(index, token));
    return actions;
  }

}
