package de.adrodoc55.minecraft.mpl.gui.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/*
 ** This class will merge individual edits into a single larger edit. That is, characters entered
 * sequentially will be grouped together and undone as a group. Any attribute changes will be
 * considered as part of the group and will therefore be undone when the group is undone.
 */
public class CompoundUndoManager extends UndoManager
    implements UndoableEditListener, DocumentListener {
  private static final long serialVersionUID = 1L;

  public CompoundEdit compoundEdit;
  private JTextComponent editor;

  // These fields are used to help determine whether the edit is an
  // incremental edit. For each character added the offset and length
  // should increase by 1 or decrease by 1 for each character removed.

  private int lastOffset;
  private int lastLength;

  public CompoundUndoManager(JTextComponent editor) {
    this.editor = editor;
    editor.getDocument().addUndoableEditListener(this);
  }

  /*
   ** Add a DocumentLister before the undo is done so we can position the Caret correctly as each
   * edit is undone.
   */
  public void undo() {
    editor.getDocument().addDocumentListener(this);
    super.undo();
    editor.getDocument().removeDocumentListener(this);
  }

  /*
   ** Add a DocumentLister before the redo is done so we can position the Caret correctly as each
   * edit is redone.
   */
  public void redo() {
    editor.getDocument().addDocumentListener(this);
    super.redo();
    editor.getDocument().removeDocumentListener(this);
  }

  /*
   ** Whenever an UndoableEdit happens the edit will either be absorbed by the current compound edit
   * or a new compound edit will be started
   */
  public void undoableEditHappened(UndoableEditEvent e) {
    // Start a new compound edit

    if (compoundEdit == null) {
      compoundEdit = startCompoundEdit(e.getEdit());
      lastLength = editor.getDocument().getLength();
      return;
    }

    // Check for an attribute change

    AbstractDocument.DefaultDocumentEvent event =
        (AbstractDocument.DefaultDocumentEvent) e.getEdit();

    if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
      compoundEdit.addEdit(e.getEdit());
      return;
    }

    // Check for an incremental edit or backspace.
    // The change in Caret position and Document length should be either
    // 1 or -1 .

    int offsetChange = editor.getCaretPosition() - lastOffset;
    int lengthChange = editor.getDocument().getLength() - lastLength;

    if (Math.abs(offsetChange) == 1 && Math.abs(lengthChange) == 1) {
      compoundEdit.addEdit(e.getEdit());
      lastOffset = editor.getCaretPosition();
      lastLength = editor.getDocument().getLength();
      return;
    }

    // Not incremental edit, end previous edit and start a new one

    compoundEdit.end();
    compoundEdit = startCompoundEdit(e.getEdit());
  }

  /*
   ** Each CompoundEdit will store a group of related incremental edits (ie. each character typed or
   * backspaced is an incremental edit)
   */
  private CompoundEdit startCompoundEdit(UndoableEdit anEdit) {
    // Track Caret and Document information of this compound edit

    lastOffset = editor.getCaretPosition();
    lastLength = editor.getDocument().getLength();

    // The compound edit is used to store incremental edits

    compoundEdit = new MyCompoundEdit();
    compoundEdit.addEdit(anEdit);

    // The compound edit is added to the UndoManager. All incremental
    // edits stored in the compound edit will be undone/redone at once

    addEdit(compoundEdit);
    return compoundEdit;
  }

  // Implement DocumentListener
  //
  // Updates to the Document as a result of Undo/Redo will cause the
  // Caret to be repositioned

  public void insertUpdate(final DocumentEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        int offset = e.getOffset() + e.getLength();
        offset = Math.min(offset, editor.getDocument().getLength());
        editor.setCaretPosition(offset);
      }
    });
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    editor.setCaretPosition(e.getOffset());
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}


  class MyCompoundEdit extends CompoundEdit {
    private static final long serialVersionUID = 1L;

    public boolean isInProgress() {
      // in order for the canUndo() and canRedo() methods to work
      // assume that the compound edit is never in progress

      return false;
    }

    public void undo() throws CannotUndoException {
      // End the edit so future edits don't get absorbed by this edit

      if (compoundEdit != null)
        compoundEdit.end();

      super.undo();

      // Always start a new compound edit after an undo

      compoundEdit = null;
    }

  }

  public static void main(String[] args) {
    final JTextPane textPane = new JTextPane();
    textPane.setPreferredSize(new Dimension(200, 200));

    // Comment out this code when not using SyntaxDocument class
    // This class provides basic java syntax highlighting
    // http://www.discoverteenergy.com/files/SyntaxDocument.java

    // EditorKit editorKit = new StyledEditorKit() {
    // public Document createDefaultDocument() {
    // return new SyntaxDocument();
    // }
    // };
    // textPane.setEditorKit(editorKit);

    // End of code to comment out

    final CompoundUndoManager undoManager = new CompoundUndoManager(textPane);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new JScrollPane(textPane), BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    panel.add(buttons, BorderLayout.SOUTH);

    JButton undo = new JButton("Undo");
    undo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          undoManager.undo();
          textPane.requestFocus();
        } catch (CannotUndoException ex) {
          System.out.println("Unable to undo: " + ex);
        }
      }
    });
    buttons.add(undo);

    JButton redo = new JButton("Redo");
    redo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          undoManager.redo();
          textPane.requestFocus();
        } catch (CannotRedoException ex) {
          System.out.println("Unable to redo: " + ex);
        }
      }
    });
    buttons.add(redo);

    JFrame frame = new JFrame("Compound Edit");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}
