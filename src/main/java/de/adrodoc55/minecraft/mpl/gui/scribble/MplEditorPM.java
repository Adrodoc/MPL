package de.adrodoc55.minecraft.mpl.gui.scribble;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

public class MplEditorPM extends AbstractPM {

    private static JFileChooser chooser;
    private static FileFilter filter;

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

    private static FileFilter getFileFilter() {
        if (filter == null) {
            filter = new FileNameExtensionFilter(
                    "Minecraft Programming Language", new String[] { "mpl" });
        }
        return filter;
    }

    private final MplEditor view;

    TextPM title = new TextPM();
    TextPM code = new TextPM();
    BooleanPM unsavedChanges = new BooleanPM();

    private File file;

    public MplEditorPM() {
        view = new MplEditor();
        PMManager.setup(this);
        view.setPresentationModel(this);
    }

    public MplEditor getView() {
        return view;
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public boolean getUnsavedChanges() {
        return unsavedChanges.getBoolean();
    }

    public void setUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges.setBoolean(unsavedChanges);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Saves the changes to this Editor's File, overwriting the content. The
     * file and all it's parent directories will be created if necassary. If the
     * file is null a JFileChooser dialog will be opened.<br>
     *
     * @param component
     *            the {@link Component} to show the JFileChooser on.
     * @throws IOException
     */
    public void save(Component component) {
        if (file == null) {
            saveUnder(component);
        } else {
            try {
                file.getParentFile().mkdirs();
                byte[] bytes = code.getText().getBytes();
                Files.write(file.toPath(), bytes);
                setUnsavedChanges(false);
            } catch (IOException ex) {
                String path = file != null ? file.getPath() : null;
                JOptionPane.showMessageDialog(component,
                        "An Exception occured while trying to save to '" + path
                                + "'. Exception: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a JFileChooser dialog. When the user select's a file
     * {@link #save(Component)} will be called.<br>
     *
     * @param component
     *            the {@link Component} to show the JFileChooser on.
     * @throws IOException
     */
    public void saveUnder(Component component) {
        JFileChooser chooser = getMplChooser();
        int userAction = chooser.showSaveDialog(component);
        if (userAction != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file.exists()) {
            int overwrite = JOptionPane.showOptionDialog(component,
                    "The File '" + file.getName()
                            + "' already exists and will be overwritten.",
                    "Save...", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, null, null);
            if (overwrite != JOptionPane.OK_OPTION) {
                saveUnder(component);
                return;
            }
        }
        setFile(file);
        save(component);
    }

}
