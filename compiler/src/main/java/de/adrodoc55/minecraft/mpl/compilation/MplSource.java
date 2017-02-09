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
package de.adrodoc55.minecraft.mpl.compilation;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

/**
 * @author Adrodoc55
 */
@Immutable
@Data
@RequiredArgsConstructor
public class MplSource {
  private final @Nonnull File file;
  private final @Nonnull String line;
  private final int lineNumber;
  private final int charPositionInLine;
  private final @Nullable String text;
  private final int startIndex;
  private final int stopIndex;

  @GenerateMplPojoBuilder
  public MplSource(File file, String line, Token token) {
    this(file, line, token, token, token.getText());
  }

  public MplSource(File file, String line, List<TerminalNode> nodes) {
    this(file, line, nodes.get(0).getSymbol(), nodes.get(nodes.size() - 1).getSymbol(),
        reconstructText(nodes));
  }

  public MplSource(File file, String line, ParserRuleContext ctx) {
    this(file, line, ctx.getStart(), ctx.getStop(), reconstructText(ctx.children));
  }

  public MplSource(File file, String line, Token start, Token stop, String text) {
    this(file, line, start.getLine(), start.getCharPositionInLine(), text, start.getStartIndex(),
        stop.getStopIndex());
  }

  private static String reconstructText(Iterable<? extends ParseTree> children) {
    return Joiner.on(' ').join(Iterables.transform(children, new Function<ParseTree, String>() {
      @Override
      public String apply(ParseTree input) {
        return input.getText();
      }
    }));
  }
}
