package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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

public class MplEditor extends JComponent {

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
            filter = new FileNameExtensionFilter(
                    "Minecraft Programming Language", new String[] { "mpl" });
        }
        return filter;
    }

    private TabCloseComponent tabComponent;
    private File file;
    private boolean unsavedChanges;

    private JScrollPane scrollPane;
    private JTextPane textPane;
    private UndoManager undoManager;

    public MplEditor() {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }

    public MplEditor(File file) {
        this();
        this.file = file;
    }

    public void load() throws IOException {
        if (file != null) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes);
            getTextPane().setText(content);
            getUndoManager().discardAllEdits();
            setUnsavedChanges(false);
        }
    }

    public TabCloseComponent getTabComponent() {
        return tabComponent;
    }

    public void setTabComponent(TabCloseComponent tabComponent) {
        this.tabComponent = tabComponent;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        if (tabComponent != null) {
            tabComponent.setTitle(getTitle());
        }
    }

    public String getTitle() {
        return file != null ? file.getName() : "new.mpl";
    }

    public boolean getUnsavedChanges() {
        return unsavedChanges;
    }

    public void setUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
        getTabComponent().setUnsavedChanges(unsavedChanges);
    }

    /**
     * Saves the changes to this Editor's File, overwriting the content. The
     * file and all it's parent directories will be created if necassary. If the
     * file is null a JFileChooser dialog will be opened.<br>
     * If an IOException is thrown the user will be informed via a JOptionPane.
     *
     */
    public void save() {
        if (file == null) {
            saveUnder();
        } else {
            try {
                file.getParentFile().mkdirs();
                byte[] bytes = getTextPane().getText().getBytes();
                Files.write(file.toPath(), bytes);
                setUnsavedChanges(false);
            } catch (IOException ex) {
                String path = file != null ? file.getPath() : null;
                JOptionPane.showMessageDialog(chooser,
                        "An Exception occured while trying to save to '" + path
                                + "'. Exception: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveUnder() {
        JFileChooser chooser = getMplChooser();
        int userAction = chooser.showSaveDialog(this);
        if (userAction != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file.exists()) {
            int overwrite = JOptionPane.showOptionDialog(chooser, "The File '"
                    + file.getName()
                    + "' already exists and will be overwritten.", "Save...",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, null, null);
            if (overwrite != JOptionPane.OK_OPTION) {
                saveUnder();
                return;
            }
        }
        setFile(file);
        save();
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getTextPane());
        }
        return scrollPane;
    }

    private JTextPane getTextPane() {
        if (textPane == null) {
            textPane = new JTextPane();
            textPane.setEditorKit(new JaggedEditorKit());
            StyledDocument doc = textPane.getStyledDocument();
            ((AbstractDocument) doc).setDocumentFilter(new MplSyntaxFilter());
            doc.addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    setUnsavedChanges(true);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    setUnsavedChanges(true);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            UndoManager undoManager = getUndoManager();
            textPane.getDocument().addUndoableEditListener(undoManager);
            int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            textPane.getInputMap().put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, ctrl), "redo");
            textPane.getInputMap().put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl), "undo");
            textPane.getActionMap().put("redo", new RedoAction(undoManager));
            textPane.getActionMap().put("undo", new UndoAction(undoManager));

            textPane.getInputMap().put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl), "save");
            textPane.getActionMap().put("save", new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
        }
        return textPane;
    }

    private UndoManager getUndoManager() {
        if (undoManager == null) {
            undoManager = new RawUndoManager();
        }
        return undoManager;
    }

}
