package de.adrodoc55.minecraft.mpl.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class DocumentUndoManager extends UndoManager {

    private static final long serialVersionUID = -3101060175804067458L;

    // @Override
    // public synchronized boolean addEdit(UndoableEdit anEdit) {
    // if (anEdit instanceof DocumentEvent && ((DefaultDocumentEvent)
    // anEdit).getType() == DocumentEvent.EventType.CHANGE) {
    // anEdit = new InsignificantUndoableEdit(anEdit);
    // }
    // return super.addEdit(anEdit);
    // }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        if (edit instanceof DocumentEvent && ((DocumentEvent) edit).getType() == DocumentEvent.EventType.CHANGE) {
            edit = new InsignificantUndoableEdit(edit);
        }
        addEdit(edit);
    }

    private static class InsignificantUndoableEdit implements UndoableEdit {

        private final UndoableEdit delegate;

        public InsignificantUndoableEdit(UndoableEdit delegate) {
            this.delegate = delegate;
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
