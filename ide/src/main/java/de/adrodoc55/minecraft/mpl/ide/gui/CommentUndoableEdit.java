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
package de.adrodoc55.minecraft.mpl.ide.gui;

import java.lang.reflect.UndeclaredThrowableException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import de.adrodoc55.minecraft.mpl.ide.gui.editor.AbstractUndoableBnEdit;
import de.adrodoc55.minecraft.mpl.ide.gui.editor.UndoableBnStyledDocument;

/**
 * @author Adrodoc55
 */
public class CommentUndoableEdit extends AbstractUndoableBnEdit {
  private static final long serialVersionUID = 1L;
  private int firstLine;
  private int lastLine;

  public CommentUndoableEdit(UndoableBnStyledDocument doc, int dot, int mark) {
    super(doc);
    Element root = doc.getDefaultRootElement();
    firstLine = root.getElementIndex(Math.min(dot, mark));
    lastLine = root.getElementIndex(Math.max(dot, mark));
  }

  @Override
  public void undo() throws CannotRedoException {
    super.undo();
    perform();
  }

  @Override
  public void redo() throws CannotUndoException {
    super.redo();
    perform();
  }

  public void perform() {
    Element root = doc.getDefaultRootElement();

    boolean uncomment = true;
    for (int l = firstLine; l <= lastLine; l++) {
      Element line = root.getElement(l);
      if (!isComment(line)) {
        uncomment = false;
        break;
      }
    }

    for (int l = firstLine; l <= lastLine; l++) {
      Element line = root.getElement(l);
      int start = line.getStartOffset();
      try {
        if (uncomment) {
          doc.removeSilent(start, 2);
        } else {
          doc.insertStringSilent(start, "//", null);
        }
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }
  }

  public boolean isComment(Element line) {
    int start = line.getStartOffset();
    try {
      if (start + 1 < doc.getLength()) {
        String text = doc.getText(start, 2);
        if ("//".equals(text)) {
          return true;
        }
      }
      return false;
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  @Override
  public String getPresentationName() {
    return "comment";
  }
}
