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

  public static AutoCompletionContext getContext(int index, String text) {
    ANTLRInputStream input = new ANTLRInputStream(text);
    MplLexer lexer = new MplLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
    MplParser parser = new MplParser(tokens);
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
    if (token != null) {
      if (context.isInProject()) {
        if ("include".startsWith(token.getText())) {
          options.add(new NewIncludeAction(token));
        }
      }
      if (!context.isInProject() && (!context.isProject() || context.isInProcess())) {
        if ("if:".startsWith(token.getText())) {
          options.add(new NewIfAction(token));
          options.add(new NewIfElseAction(token));
        }
      }
      if (!context.isInProcess() && !context.isInProject()
          && token.getType() == MplLexer.IDENTIFIER) {
        options.add(new NewProcessAction(token));
      }
    }
    return options;
  }

}
