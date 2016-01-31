package de.adrodoc55.minecraft.mpl.gui;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;

import de.adrodoc55.minecraft.mpl.CompilerException;

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
      filter = new FileNameExtensionFilter("Minecraft Programming Language", new String[] {"mpl"});
    }
    return filter;
  }

  TextPM title = new TextPM();
  OperationPM close = new OperationPM();
  TextPM code = new TextPM();
  BooleanPM unsavedChanges = new BooleanPM();
  OperationPM resetChanges = new OperationPM();
  MplSyntaxFilterPM syntaxFilter = new MplSyntaxFilterPM();

  private File file;

  public static interface Context {
    void close(MplEditorPM editorPm);
  }

  private final Context context;

  private static int i;

  /**
   * Constructs a new MplEditorPM.
   *
   * @param context
   * @throws NullPointerException if context is null
   */
  public MplEditorPM(Context context) throws NullPointerException {
    if (context == null) {
      throw new NullPointerException("context == null");
    }
    this.context = context;
    title.setText("new" + i++ + ".mpl");
    code.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        setUnsavedChanges(true);
      }
    });
    PMManager.setup(this);
  }

  @Operation
  private void close() {
    context.close(this);
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

  public List<CompilerException> getExceptions() {
    return syntaxFilter.getExceptions();
  }

  public void setCompilerExceptions(List<CompilerException> exceptions) {
    syntaxFilter.setExceptions(exceptions);
  }

  public File getFile() {
    return file;
  }

  /**
   * Set the File and update the Title accordingly.
   *
   * @param file
   */
  public void setFile(File file) {
    this.file = file;
    setTitle(file.getName());
  }

  /**
   * (Re)loads the content of this Editor's File, if it has one.
   *
   * @throws IOException
   */
  public void load() throws IOException {
    if (file == null) {
      return;
    }
    byte[] bytes = Files.readAllBytes(file.toPath());
    String content = new String(bytes);
    code.setText(content);
    resetChanges.getPropertyChangeSupport().firePropertyChange("resetChanges", null, null);
    setUnsavedChanges(false);
  }

  /**
   * Saves the changes to this Editor's File, overwriting the content. The file and all it's parent
   * directories will be created if necassary. If the file is null a JFileChooser dialog will be
   * opened.<br>
   *
   * @throws IOException
   */
  public void save() {
    if (file == null) {
      saveUnder();
    } else {
      try {
        file.getParentFile().mkdirs();
        byte[] bytes = code.getText().getBytes();
        Files.write(file.toPath(), bytes);
        setUnsavedChanges(false);
      } catch (IOException ex) {
        Window activeWindow =
            KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        String path = file != null ? file.getPath() : null;
        JOptionPane.showMessageDialog(activeWindow, "An Exception occured while trying to save to '"
            + path + "'. Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Opens a JFileChooser dialog. When the user select's a file {@link #save()} will be called.<br>
   *
   * @throws IOException
   */
  public void saveUnder() {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    JFileChooser chooser = getMplChooser();
    int userAction = chooser.showSaveDialog(activeWindow);
    if (userAction != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = chooser.getSelectedFile();
    if (file.exists()) {
      int overwrite = JOptionPane.showOptionDialog(activeWindow,
          "The File '" + file.getName() + "' already exists and will be overwritten.", "Save...",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
      if (overwrite != JOptionPane.OK_OPTION) {
        saveUnder();
        return;
      }
    }
    setFile(file);
    save();
  }

}
