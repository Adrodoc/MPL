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

import java.lang.reflect.UndeclaredThrowableException;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.fx.code.editor.services.ContextInformation.BaseContextInformation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

/**
 * @author Adrodoc55
 */
public class MplCompletionProposal implements CompletionProposal {
  private final CharSequence label;
  private final int replacementOffset;
  private final int replacementLength;
  private final String replacementString;
  private final int cursorPositionInTemplate;
  private final ContextInformation contextInformation;

  public MplCompletionProposal(int indent, AutoCompletionAction proposal) {
    label = proposal.getDisplayName();
    replacementString = proposal.processTemplate(indent);
    replacementOffset = proposal.getStartIndex();
    replacementLength = proposal.getLength();
    cursorPositionInTemplate = proposal.getCursorIndex(indent);
    CharSequence hoverInfo = proposal.getDescription();
    contextInformation = new BaseContextInformation(replacementOffset, hoverInfo);
  }

  @Override
  public CharSequence getLabel() {
    return label;
  }

  public int getReplacementOffset() {
    return replacementOffset;
  }

  public int getReplacementLength() {
    return replacementLength;
  }

  public String getReplacementString() {
    return replacementString;
  }

  public int getCursorPosition() {
    return getReplacementOffset() + cursorPositionInTemplate;
  }

  @Override
  public void apply(IDocument document) {
    try {
      document.replace(replacementOffset, replacementLength, replacementString);
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  @Override
  public TextSelection getSelection(IDocument document) {
    return new TextSelection(getCursorPosition(), 0);
  }

  @Override
  public ContextInformation getContextInformation() {
    return contextInformation;
  }
}
