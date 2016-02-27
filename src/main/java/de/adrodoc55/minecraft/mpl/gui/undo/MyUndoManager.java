package de.adrodoc55.minecraft.mpl.gui.undo;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class MyUndoManager extends UndoManager implements DocumentListener {

  @Override
  public void insertUpdate(DocumentEvent e) {
//    e.
    // TODO Auto-generated method stub

  }

  @Override
  public void removeUpdate(DocumentEvent e) {
//    ElementChange e;
//    e.
    // TODO Auto-generated method stub

  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    // TODO Auto-generated method stub

  }

  public static class InsertUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    protected final Document doc;
    protected final int offset;
    protected final String text;

    public InsertUndoableEdit(Document doc, int offset, String text) {
      super();
      this.doc = doc;
      this.offset = offset;
      this.text = text;
    }

    @Override
    public boolean canUndo() {
      if (super.canUndo()) {
        return offset + text.length() <= doc.getLength();
      }
      return false;
    }

    @Override
    public void undo() throws CannotUndoException {
      super.undo();
      try {
        doc.remove(offset, text.length());
      } catch (BadLocationException ex) {
        throw new CannotUndoException();
      }
    }

    @Override
    public boolean canRedo() {
      if (super.canRedo()) {
        return offset <= doc.getLength();
      }
      return false;
    }

    @Override
    public void redo() throws CannotRedoException {
      super.redo();
      try {
        doc.insertString(offset, text, null);
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }

    @Override
    public String getPresentationName() {
      return "insert";
    }
  }

  public static class RemoveUndoableEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;

    protected final Document doc;
    protected final int offset;
    protected final String text;

    public RemoveUndoableEdit(Document doc, int offset, String text) {
      super();
      this.doc = doc;
      this.offset = offset;
      this.text = text;
    }

    @Override
    public boolean canUndo() {
      if (super.canUndo()) {
        return offset + text.length() <= doc.getLength();
      }
      return false;
    }

    @Override
    public void undo() throws CannotUndoException {
      super.undo();
      try {
        doc.remove(offset, text.length());
      } catch (BadLocationException ex) {
        throw new CannotUndoException();
      }
    }

    @Override
    public boolean canRedo() {
      if (super.canRedo()) {
        return offset <= doc.getLength();
      }
      return false;
    }

    @Override
    public void redo() throws CannotRedoException {
      super.redo();
      try {
        doc.insertString(offset, text, null);
      } catch (BadLocationException ex) {
        throw new CannotRedoException();
      }
    }

    @Override
    public String getPresentationName() {
      return "insert";
    }
  }

  public static class ChangeUndoableEdit implements UndoableEdit {

  }

}
