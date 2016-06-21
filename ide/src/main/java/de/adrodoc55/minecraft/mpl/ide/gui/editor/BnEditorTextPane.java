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

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.UndeclaredThrowableException;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import org.beanfabrics.Path;
import org.beanfabrics.swing.internal.BnStyledDocument;

import de.adrodoc55.minecraft.mpl.ide.gui.utils.NoWrapBnTextPane;

/**
 * @author Adrodoc55
 */
public class BnEditorTextPane extends NoWrapBnTextPane {
  private static final long serialVersionUID = 1L;

  public interface Context {
    EditorPM getPresentationModel();
  }

  private final Context context;

  private DocumentListener caretListener;
  private UndoManager undoManager;

  public BnEditorTextPane(Context context) {
    this.context = context;
    setFont(new Font("Consolas", Font.PLAIN, 13));
    setPath(new Path("this.code"));
    initUndo();
    initRedo();
    initSave();
    initDeleteLine();
  }

  @Override
  protected EditorKit createDefaultEditorKit() {
    return new UndoableBnStyledEditorKit();
  }

  @Override
  public UndoableBnStyledDocument getDocument() {
    return (UndoableBnStyledDocument) super.getDocument();
  }

  /**
   * Associates the editor with a text document. This must be a {@link UndoableBnStyledDocument}.
   *
   * @param doc the document to display/edit
   * @exception IllegalArgumentException if {@code doc} can't be narrowed to a
   *            {@link UndoableBnStyledDocument} which is the required type of model for this text
   *            component
   */
  @Override
  public void setDocument(Document doc) {
    if (doc instanceof UndoableBnStyledDocument) {
      BnStyledDocument oldDoc = getDocument();
      if (oldDoc != null) {
        oldDoc.removeDocumentListener(getCaretListener());
        oldDoc.removeUndoableEditListener(getUndoManager());
      }
      super.setDocument(doc);
      doc.addDocumentListener(getCaretListener());
      doc.addUndoableEditListener(getUndoManager());
    } else {
      throw new IllegalArgumentException("Model must be UndoableBnStyledDocument");
    }
  }

  @Override
  public UndoableBnStyledDocument getStyledDocument() {
    return getDocument();
  }

  /**
   * Associates the editor with a text document. This must be a <code>BnStyledDocument</code>.
   *
   * @param doc the document to display/edit
   * @exception IllegalArgumentException if <code>doc</code> can't be narrowed to a
   *            <code>BnStyledDocument</code> which is the required type of model for this text
   *            component
   */
  @Override
  public void setStyledDocument(StyledDocument doc) {
    setDocument(doc);
  }

  public DocumentListener getCaretListener() {
    if (caretListener == null) {
      caretListener = new DocumentListener() {
        @Override
        public void removeUpdate(DocumentEvent e) {
          if (e instanceof CaretUpdateEvent) {
            setCaretPosition(e.getOffset());
          }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
          if (e instanceof CaretUpdateEvent) {
            setCaretPosition(e.getOffset() + e.getLength());
          }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          if (e instanceof CaretUpdateEvent) {
            setCaretPosition(e.getOffset() + e.getLength());
          }
        }
      };
    }
    return caretListener;
  }

  public UndoManager getUndoManager() {
    if (undoManager == null) {
      undoManager = new UndoManager();
      undoManager.setLimit(-1);
    }
    return undoManager;
  }

  public void initUndo() {
    int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl), "undo");
    getActionMap().put("undo", new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (undoManager.canUndo()) {
          undoManager.undo();
        }
      }
    });
  }

  public void initRedo() {
    int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrl), "redo");
    getActionMap().put("redo", new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (undoManager.canRedo()) {
          undoManager.redo();
        }
      }
    });
  }

  public void initSave() {
    int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl), "save");
    getActionMap().put("save", new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        EditorPM pm = context.getPresentationModel();
        if (pm != null)
          pm.save();
      }
    });
  }

  public void initDeleteLine() {
    int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, ctrl), "delete");
    getActionMap().put("delete", new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        BnStyledDocument doc = getDocument();
        Element root = doc.getDefaultRootElement();
        int elementIndex = root.getElementIndex(getCaretPosition());
        Element line = root.getElement(elementIndex);
        int start = line.getStartOffset();
        int length = line.getEndOffset() - start;
        if (doc.getLength() < start + length) {
          if (start == 0) {
            length--;
          } else {
            start--;
          }
        }
        try {
          doc.remove(start, length);
        } catch (BadLocationException ex) {
          throw new UndeclaredThrowableException(ex);
        }
      }
    });
  }

}
