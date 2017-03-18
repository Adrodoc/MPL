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
package de.adrodoc55.minecraft.mpl.ide.fx.editor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.EditingContext;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletion;
import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

/**
 * @author Adrodoc55
 */
public class MplProposalComputer implements ProposalComputer {
  private final IDocument document;
  private final EditingContext editingContext;

  public MplProposalComputer(IDocument document, EditingContext editingContext) {
    this.document = checkNotNull(document, "document == null!");
    this.editingContext = checkNotNull(editingContext, "editingContext == null!");
  }

  @Override
  public CompletableFuture<List<CompletionProposal>> compute() {
    return CompletableFuture.supplyAsync(this::computeAsync);
  }

  private List<CompletionProposal> computeAsync() {
    List<CompletionProposal> result = new ArrayList<>();
    int lineOffset = getLineOffset();
    int caretOffset = editingContext.getCaretOffset();
    String text = document.get();
    List<AutoCompletionAction> options = AutoCompletion.getOptions(caretOffset, text);
    for (AutoCompletionAction action : options) {
      int indent = action.getStartIndex() - lineOffset;
      result.add(new MplCompletionProposal(indent, action));
    }
    return result;
  }

  private int getLineOffset() {
    try {
      IRegion line = document.getLineInformationOfOffset(editingContext.getCaretOffset());
      return line.getOffset();
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
