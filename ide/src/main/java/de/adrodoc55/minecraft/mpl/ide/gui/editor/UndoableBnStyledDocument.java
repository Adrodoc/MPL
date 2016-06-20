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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.beanfabrics.swing.internal.BnStyledDocument;

/**
 * @author Adrodoc55
 */
public class UndoableBnStyledDocument extends BnStyledDocument {
  private static final long serialVersionUID = 1L;

  private final List<UndoableEditListener> listeners = new ArrayList<>();

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

  @Override
  public void remove(int offset, int length) throws BadLocationException {
    String text = getText(offset, length);
    super.remove(offset, length);
    RemoveUndoableEdit edit = new RemoveUndoableEdit(offset, text);
    fireUndoableEditEvent(new UndoableEditEvent(this, edit));
  }

  private void remove2(int offs, int len, boolean isUndo) throws BadLocationException {
    super.remove(offs, len);
    fireRemoveUpdate(new CaretUpdateEvent(offs, len, EventType.REMOVE));
  }

  @Override
  public void replace(int offset, int length, String newText, AttributeSet attrs)
      throws BadLocationException {
    String oldText = getText(offset, length);
    super.replace(offset, length, newText, attrs);
    ChangeUndoableEdit edit = new ChangeUndoableEdit(offset, oldText, newText);
    fireUndoableEditEvent(new UndoableEditEvent(this, edit));
  }

  private void replace2(int offs, int len, String text, AttributeSet attrs, boolean isUndo)
      throws BadLocationException {
    super.replace(offs, len, text, attrs);
    fireChangedUpdate(new CaretUpdateEvent(offs, text.length(), EventType.CHANGE));
  }

  @Override
  public void insertString(int offset, String text, AttributeSet a) throws BadLocationException {
    super.insertString(offset, text, a);
    InsertUndoableEdit edit = new InsertUndoableEdit(offset, text);
    fireUndoableEditEvent(new UndoableEditEvent(this, edit));
  }

  private void insertString2(int offs, String str, AttributeSet a, boolean isUndo)
      throws BadLocationException {
    super.insertString(offs, str, a);
    fireInsertUpdate(new CaretUpdateEvent(offs, str.length(), EventType.INSERT));
  }

  public class CaretUpdateEvent extends DefaultDocumentEvent {
    private static final long serialVersionUID = 1L;

    public CaretUpdateEvent(int offs, int len, EventType type) {
      super(offs, len, type);
    }
  }

  public class InsertUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    protected final int offset;
    protected final String text;

    public InsertUndoableEdit(int offset, String text) {
      this.offset = offset;
      this.text = text;
    }

    @Override
    public boolean canUndo() {
      return super.canUndo() && offset + text.length() <= getLength();
    }

    @Override
    public void undo() throws CannotUndoException {
      super.undo();
      try {
        remove2(offset, text.length(), true);
      } catch (BadLocationException ex) {
        throw new CannotUndoException();
      }
    }

    @Override
    public boolean canRedo() {
      return super.canRedo() && offset <= getLength();
    }

    @Override
    public void redo() throws CannotRedoException {
      super.redo();
      try {
        insertString2(offset, text, null, false);
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }

    @Override
    public String getPresentationName() {
      return "insert";
    }
  }

  public class RemoveUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    protected final int offset;
    protected final String text;

    public RemoveUndoableEdit(int offset, String text) {
      this.offset = offset;
      this.text = text;
    }

    @Override
    public boolean canUndo() {
      return super.canUndo() && offset <= getLength();
    }

    @Override
    public void undo() throws CannotRedoException {
      super.undo();
      try {
        insertString2(offset, text, null, true);
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }

    @Override
    public boolean canRedo() {
      return super.canRedo() && offset + text.length() <= getLength();
    }

    @Override
    public void redo() throws CannotUndoException {
      super.redo();
      try {
        remove2(offset, text.length(), false);
      } catch (BadLocationException ex) {
        throw new CannotUndoException();
      }
    }

    @Override
    public String getPresentationName() {
      return "remove";
    }
  }

  public class ChangeUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    protected final int offset;
    protected final String oldText;
    protected final String newText;

    public ChangeUndoableEdit(int offset, String oldText, String newText) {
      this.offset = offset;
      this.oldText = oldText;
      this.newText = newText;
    }

    @Override
    public boolean canUndo() {
      return super.canUndo() && offset + newText.length() <= getLength();
    }

    @Override
    public void undo() throws CannotRedoException {
      super.undo();
      try {
        replace2(offset, newText.length(), oldText, null, true);
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }

    @Override
    public boolean canRedo() {
      return super.canRedo() && offset + oldText.length() <= getLength();
    }

    @Override
    public void redo() throws CannotUndoException {
      super.redo();
      try {
        replace2(offset, oldText.length(), newText, null, false);
      } catch (BadLocationException ex) {
        throw new CannotUndoException();
      }
    }

    @Override
    public String getPresentationName() {
      return "change";
    }
  }

}