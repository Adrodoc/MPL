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
package de.adrodoc55.minecraft.mpl.ide.gui.editor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.UndeclaredThrowableException;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author Adrodoc55
 */
public abstract class AbstractUndoableBnEdit extends AbstractUndoableEdit {
  private static final long serialVersionUID = 1L;
  protected final UndoableBnStyledDocument doc;

  public AbstractUndoableBnEdit(UndoableBnStyledDocument doc) {
    this.doc = checkNotNull(doc, "doc == null!");
    try {
      Field hasBeenDone = AbstractUndoableEdit.class.getDeclaredField("hasBeenDone");
      hasBeenDone.setAccessible(true);
      hasBeenDone.setBoolean(this, false);
    } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  /**
   * Silently removes from {@link #doc} and updates the cursor.
   *
   * @param offs
   * @param len
   * @throws BadLocationException
   * @see UndoableBnStyledDocument#removeSilent(int, int)
   */
  protected void remove(int offs, int len) throws BadLocationException {
    doc.removeSilent(offs, len);
    doc.fireRemoveUpdate(doc.new MyCaretUpdateEvent(offs, len, EventType.REMOVE));
  }

  /**
   * Silently replace in {@link #doc} and updates the cursor.
   *
   * @param offs
   * @param len
   * @throws BadLocationException
   * @see UndoableBnStyledDocument#replaceSilent(int, int)
   */
  protected void replace(int offs, int len, String text, AttributeSet attrs)
      throws BadLocationException {
    doc.replaceSilent(offs, len, text, attrs);
    doc.fireChangedUpdate(doc.new MyCaretUpdateEvent(offs, text.length(), EventType.CHANGE));
  }

  /**
   * Silently inserts into {@link #doc} and updates the cursor.
   *
   * @param offs
   * @param len
   * @throws BadLocationException
   * @see UndoableBnStyledDocument#insertStringSilent(int, int)
   */
  protected void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
    doc.insertStringSilent(offs, str, a);
    doc.fireInsertUpdate(doc.new MyCaretUpdateEvent(offs, str.length(), EventType.INSERT));
  }
}
