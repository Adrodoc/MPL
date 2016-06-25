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

import static de.adrodoc55.commons.TabToSpaceConverter.convertTabsToSpaces;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.undo.UndoableEdit;

import org.beanfabrics.swing.internal.BnStyledDocument;

/**
 * @author Adrodoc55
 */
public class UndoableBnStyledDocument extends BnStyledDocument {
  private static final long serialVersionUID = 1L;
  private final List<UndoableEditListener> listeners = new ArrayList<>();
  private int tabWidth;

  public UndoableBnStyledDocument() {
    this(2);
  }

  public UndoableBnStyledDocument(int tabWidth) {
    this.tabWidth = tabWidth;
  }

  @Override
  public void addUndoableEditListener(UndoableEditListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeUndoableEditListener(UndoableEditListener listener) {
    listeners.remove(listener);
  }

  @Override
  public UndoableEditListener[] getUndoableEditListeners() {
    return listeners.toArray(new UndoableEditListener[0]);
  }

  protected void fireUndoableEditEvent(UndoableEditEvent e) {
    for (UndoableEditListener listener : listeners) {
      listener.undoableEditHappened(e);
    }
  }

  protected void fireUndoableEditEvent(UndoableEdit edit) {
    fireUndoableEditEvent(new UndoableEditEvent(this, edit));
  }

  public void submit(UndoableEdit edit) {
    edit.redo();
    fireUndoableEditEvent(edit);
  }

  private int getOffsetInLine(int offset) {
    Element root = getDefaultRootElement();
    int elementIndex = root.getElementIndex(offset);
    int startOffset = root.getElement(elementIndex).getStartOffset();
    int lineOffset = offset - startOffset;
    return lineOffset;
  }

  @Override
  public void remove(int offset, int length) throws BadLocationException {
    String text = getText(offset, length);
    submit(new RemoveUndoableEdit(this, offset, text));
  }

  @Override
  public void replace(int offset, int length, String newText, AttributeSet attrs)
      throws BadLocationException {
    String oldText = getText(offset, length);
    int lineOffset = getOffsetInLine(offset);
    newText = convertTabsToSpaces(lineOffset, tabWidth, newText);
    submit(new ChangeUndoableEdit(this, offset, oldText, newText, attrs));
  }

  @Override
  public void insertString(int offset, String text, AttributeSet a) throws BadLocationException {
    int lineOffset = getOffsetInLine(offset);
    text = convertTabsToSpaces(lineOffset, tabWidth, text);
    submit(new InsertUndoableEdit(this, offset, text, a));
  }

  /**
   * Removes from this document without generating {@link UndoableEdit}
   *
   * @param offs the offset from the beginning
   * @param len the number of characters to remove
   * @throws BadLocationException if the given position is not a valid position within the document
   */
  public void removeSilent(int offs, int len) throws BadLocationException {
    super.remove(offs, len);
  }

  /**
   * Replaces in this document without generating {@link UndoableEdit}
   *
   * @param offs the offset from the beginning
   * @param len the number of characters to remove
   * @param text text to insert, null indicates no text to insert
   * @param attrs AttributeSet indicating attributes of inserted text
   * @throws BadLocationException if the given position is not a valid position within the document
   */
  public void replaceSilent(int offs, int len, String text, AttributeSet attrs)
      throws BadLocationException {
    super.replace(offs, len, text, attrs);
  }

  /**
   * Removes from this document without generating {@link UndoableEdit}
   *
   * @param offs the offset from the beginning
   * @param str the string to insert
   * @param a the attributes to associate with the inserted content
   * @throws BadLocationException if the given position is not a valid position within the document
   */
  public void insertStringSilent(int offs, String str, AttributeSet a) throws BadLocationException {
    super.insertString(offs, str, a);
  }

  public class MyCaretUpdateEvent extends DefaultDocumentEvent implements CaretUpdateEvent {
    private static final long serialVersionUID = 1L;

    public MyCaretUpdateEvent(int offs, int len, EventType type) {
      super(offs, len, type);
    }
  }

  @Override
  protected void fireRemoveUpdate(DocumentEvent e) {
    super.fireRemoveUpdate(e);
  }

  @Override
  protected void fireChangedUpdate(DocumentEvent e) {
    super.fireChangedUpdate(e);
  }

  @Override
  protected void fireInsertUpdate(DocumentEvent e) {
    super.fireInsertUpdate(e);
  }

}
