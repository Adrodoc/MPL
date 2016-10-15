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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectFileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ScriptFileContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParserBaseListener;

/**
 * @author Adrodoc55
 */
public class AutoCompletionListener extends MplParserBaseListener {
  private final int index;
  private final AutoCompletionContext result = new AutoCompletionContext();

  public AutoCompletionListener(int index) {
    this.index = index;
  }

  @Override
  public void enterProjectFile(ProjectFileContext ctx) {
    result.setProject(true);
  }

  @Override
  public void enterScriptFile(ScriptFileContext ctx) {
    result.setProject(false);
  }

  @Override
  public void enterProject(ProjectContext ctx) {
    result.setInProject(true);
  }

  @Override
  public void exitProject(ProjectContext ctx) {
    result.setInProject(false);
  }

  @Override
  public void enterProcess(ProcessContext ctx) {
    result.setInProcess(true);
  }

  @Override
  public void exitProcess(ProcessContext ctx) {
    result.setInProcess(false);
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    visitNode(node);
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    visitNode(node);
  }

  protected void visitNode(TerminalNode node) {
    Token token = node.getSymbol();
    if (token == null || index > token.getStopIndex() + 1)
      return;
    if (index < token.getStartIndex() || token.getType() == MplLexer.EOF) {
      token = null;
    }
    result.setToken(token);
    throw new ResultException(result);
  }
}
