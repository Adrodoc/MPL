package de.adrodoc55.minecraft.mpl.gui.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument;
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
        return false;
      }
    }
    return super.addEdit(anEdit);
  }
}
