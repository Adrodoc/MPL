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
package de.adrodoc55.minecraft.mpl.gui.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * This UndoManager will ignore Edits of Type {@link EventType}.CHANGE.
 *
 * @author Adrian
 *
 */
public class RawUndoManager extends UndoManager {

  private static final long serialVersionUID = 1L;

  public synchronized boolean addEdit(UndoableEdit anEdit) {
    if (anEdit instanceof AbstractDocument.DefaultDocumentEvent) {
      AbstractDocument.DefaultDocumentEvent de = (AbstractDocument.DefaultDocumentEvent) anEdit;
      if (de.getType() == DocumentEvent.EventType.CHANGE) {
        return super.addEdit(new InsignificantEdit(anEdit));
      }
    }
    return super.addEdit(anEdit);
  }

  public static class InsignificantEdit implements UndoableEdit {
    private final UndoableEdit delegate;

    public InsignificantEdit(UndoableEdit anEdit) {
      delegate = anEdit;
    }

    public boolean isSignificant() {
      return false;
    }

    public void undo() throws CannotUndoException {
      delegate.undo();
    }

    public boolean canUndo() {
      return delegate.canUndo();
    }

    public void redo() throws CannotRedoException {
      delegate.redo();
    }

    public boolean canRedo() {
      return delegate.canRedo();
    }

    public void die() {
      delegate.die();
    }

    public boolean addEdit(UndoableEdit anEdit) {
      return delegate.addEdit(anEdit);
    }

    public boolean replaceEdit(UndoableEdit anEdit) {
      return delegate.replaceEdit(anEdit);
    }

    public String getPresentationName() {
      return delegate.getPresentationName();
    }

    public String getUndoPresentationName() {
      return delegate.getUndoPresentationName();
    }

    public String getRedoPresentationName() {
      return delegate.getRedoPresentationName();
    }

  }
}
