package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import org.beanfabrics.BnModelObserver;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;

import de.adrodoc55.minecraft.mpl.gui.utils.RawUndoManager;
import de.adrodoc55.minecraft.mpl.gui.utils.RedoAction;
import de.adrodoc55.minecraft.mpl.gui.utils.UndoAction;

/**
 * The MplEditor is a {@link View} on a {@link MplEditorPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplEditor extends JComponent implements View<MplEditorPM>, ModelSubscriber {
  private static final long serialVersionUID = 1L;

  private static JFileChooser chooser;

  private static JFileChooser getFileChooser() {
    if (chooser == null) {
      chooser = new JFileChooser();
    }
    return chooser;
  }

  public static JFileChooser getDirChooser() {
    JFileChooser chooser = getFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setFileFilter(null);
    FileFilter filter = getFileFilter();
    chooser.removeChoosableFileFilter(filter);
    return chooser;
  }

  public static JFileChooser getMplChooser() {
    JFileChooser chooser = getFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    FileFilter filter = getFileFilter();
    chooser.setFileFilter(filter);
    chooser.addChoosableFileFilter(filter);
    return chooser;
  }

  private static FileFilter filter;

  private static FileFilter getFileFilter() {
    if (filter == null) {
      filter = new FileNameExtensionFilter("Minecraft Programming Language", new String[] {"mpl"});
    }
    return filter;
  }

  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JScrollPane scrollPane;
  private JTextPane textPane;
  private BnModelObserver codeObserver;
  private MplSyntaxFilter mplSyntaxFilter;
  private RawUndoManager rawUndoManager;
  private BnModelObserver resetUndoManagerObserver;

  /**
   * Constructs a new <code>MplEditor</code>.
   */
  public MplEditor() {
    super();
    setLayout(new BorderLayout());
    add(getScrollPane(), BorderLayout.CENTER);
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=10,340
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider(); // @wb:location=10,430
      localModelProvider.setPresentationModelType(MplEditorPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public MplEditorPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(MplEditorPM pModel) {
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  public void setPath(Path path) {
    this.link.setPath(path);
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getTextPane());
    }
    return scrollPane;
  }

  private JTextPane getTextPane() {
    if (textPane == null) {
      textPane = new JTextPane();
      StyledDocument doc = textPane.getStyledDocument();
      ((AbstractDocument) doc).setDocumentFilter(getMplSyntaxFilter());
      doc.addDocumentListener(new DocumentListener() {
        @Override
        public void removeUpdate(DocumentEvent e) {
          getPresentationModel().code.setText(textPane.getText());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
          getPresentationModel().code.setText(textPane.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {}
      });
      getCodeObserver().addPropertyChangeListener(evt -> {
        if (getPresentationModel() != null) {
          String text = getPresentationModel().code.getText();
          if (!textPane.getText().equals(text)) {
            textPane.setText(text);
          }
        }
      });
      UndoManager undoManager = getUndoManager();
      textPane.getDocument().addUndoableEditListener(undoManager);
      int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrl), "redo");
      textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl), "undo");
      textPane.getActionMap().put("redo", new RedoAction(undoManager));
      textPane.getActionMap().put("undo", new UndoAction(undoManager));

      textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl), "save");
      textPane.getActionMap().put("save", new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          MplEditorPM pModel = getPresentationModel();
          if (pModel == null) {
            return;
          }
          pModel.save();
        }
      });
    }
    return textPane;
  }

  /**
   * @wbp.nonvisual location=16,379
   */
  private MplSyntaxFilter getMplSyntaxFilter() {
    if (mplSyntaxFilter == null) {
      mplSyntaxFilter = new MplSyntaxFilter();
      mplSyntaxFilter.setPath(new Path("this.syntaxFilter"));
      mplSyntaxFilter.setModelProvider(getLocalModelProvider());
    }
    return mplSyntaxFilter;
  }

  /**
   * @wbp.nonvisual location=129,339
   */
  private BnModelObserver getCodeObserver() {
    if (codeObserver == null) {
      codeObserver = new BnModelObserver();
      codeObserver.setPath(new Path("this.code"));// @wb:location=9,379
      codeObserver.setModelProvider(getLocalModelProvider());
    }
    return codeObserver;
  }

  /**
   * @wbp.nonvisual location=8,419
   */
  private RawUndoManager getUndoManager() {
    if (rawUndoManager == null) {
      rawUndoManager = new RawUndoManager();
      getResetUndoManagerObserver().addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          rawUndoManager.discardAllEdits();
        }
      });
    }
    return rawUndoManager;
  }

  /**
   * @wbp.nonvisual location=119,379
   */
  private BnModelObserver getResetUndoManagerObserver() {
    if (resetUndoManagerObserver == null) {
      resetUndoManagerObserver = new BnModelObserver();
      resetUndoManagerObserver.setPath(new Path("this.resetChanges"));
      resetUndoManagerObserver.setModelProvider(getLocalModelProvider());
    }
    return resetUndoManagerObserver;
  }
}
