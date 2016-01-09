package de.adrodoc55.minecraft.mpl.gui;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class RawUndoManager extends UndoManager {

    private static final long serialVersionUID = 1L;

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
            AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
            if (event.getType() == EventType.CHANGE) {
                return;
            }
        }
        super.undoableEditHappened(e);
    }

}
