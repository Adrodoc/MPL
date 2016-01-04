package de.adrodoc55.minecraft.mpl.gui;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class UndoManagerFix extends UndoManager {

    private static final long serialVersionUID = 5335352180435980549L;

    @Override
    public synchronized void undo() throws CannotUndoException {
        do {
            UndoableEdit edit = editToBeUndone();
            if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
                AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                if (event.getType() == EventType.CHANGE) {
                    super.undo();
                    continue;
                }
            }
            break;
        } while (true);

        super.undo();
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
//        int caretPosition = getCaretPosition();

        do {
            UndoableEdit edit = editToBeRedone();
            if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
                AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                if (event.getType() == EventType.CHANGE) {
                    super.redo();
                    continue;
                }
            }
            break;
        } while (true);

//        setCaretPosition(caretPosition);
    }

}