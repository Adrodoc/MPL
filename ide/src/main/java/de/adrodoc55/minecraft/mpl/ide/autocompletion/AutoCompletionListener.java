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
package de.adrodoc55.minecraft.mpl.autocompletion;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.antlr.MplBaseListener;
import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProcessContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;

/**
 * @author Adrodoc55
 */
public class AutoCompletionListener extends MplBaseListener {
  private final int index;

  private boolean inProject;
  private boolean inProcess;

  public AutoCompletionListener(int index) {
    this.index = index;
  }

  @Override
  public void enterProject(ProjectContext ctx) {
    inProject = true;
  }

  @Override
  public void exitProject(ProjectContext ctx) {
    inProject = false;
  }

  @Override
  public void enterProcess(ProcessContext ctx) {
    inProcess = true;
  }

  @Override
  public void exitProcess(ProcessContext ctx) {
    inProcess = false;
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
    AutoCompletionContext result = new AutoCompletionContext(token, inProject, inProcess);
    throw new ResultException(result);
  }
}
