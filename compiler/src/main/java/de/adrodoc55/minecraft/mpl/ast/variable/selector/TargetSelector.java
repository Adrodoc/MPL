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
package de.adrodoc55.minecraft.mpl.ast.variable.selector;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.google.common.collect.ImmutableMap;

import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorLexer;
import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorParser;
import de.adrodoc55.minecraft.mpl.antlr.TargetSelectorParser.SelectorContext;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@Immutable
public class TargetSelector {
  public static @Nullable TargetSelector parse(String value, MplSource source,
      MplCompilerContext context) {
    checkNotNull(value, "value == null!");
    checkNotNull(source, "source == null!");
    checkNotNull(context, "context == null!");

    ANTLRInputStream input = new ANTLRInputStream(value);
    TargetSelectorLexer lexer = new TargetSelectorLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
    TargetSelectorParser parser = new TargetSelectorParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object token, int line,
          int charPositionInLine, String message, RecognitionException cause) {
        context.addError(new CompilerException(source, message));
      }
    });
    SelectorContext ctx = parser.selector();
    if (context.getErrors().isEmpty()) {
      TargetSelectorListenerImpl listener = new TargetSelectorListenerImpl();
      new ParseTreeWalker().walk(listener, ctx);
      return listener.getResult();
    }
    return null;
  }

  private final TargetSelectorType type;
  private final Map<String, String> arguments;

  @GenerateMplPojoBuilder
  public TargetSelector(TargetSelectorType type, Map<String, String> arguments) {
    this.type = type;
    this.arguments = ImmutableMap.copyOf(arguments);
  }

  /**
   * @return the {@link #type}
   */
  public TargetSelectorType getType() {
    return type;
  }

  /**
   * @return the {@link #arguments}
   */
  public Map<String, String> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('@');
    sb.append(type);
    if (!arguments.isEmpty()) {
      sb.append('[');
      for (Iterator<Entry<String, String>> it = arguments.entrySet().iterator(); it.hasNext();) {
        Entry<String, String> entry = it.next();
        sb.append(entry.getKey());
        sb.append('=');
        sb.append(entry.getValue());
        if (it.hasNext()) {
          sb.append(',');
        }
      }
      sb.append(']');
    }
    return sb.toString();
  }
}
