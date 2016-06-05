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
package de.adrodoc55.minecraft.mpl.ide.gui;

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
import javax.swing.text.JTextComponent;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.compilation.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.compilation.MplCompiler;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.searchandreplace.SearchAndReplaceDialog;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.searchandreplace.SearchAndReplaceDialogPM;

/**
 * @author Adrodoc55
 */
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
  BooleanPM unsavedChanges = new BooleanPM();
  TextPM code = new TextPM();
  MplSyntaxFilterPM syntaxFilter = new MplSyntaxFilterPM();

  private File file;

  public static interface Context {
    void close(MplEditorPM editorPm);

    SearchAndReplaceDialog getSearchAndReplaceDialog();

    void compile(MplEditorPM mplEditorPM);
  }

  private final Context context;
  private MplEditor view;

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
    title.setEditable(false); // For UnsavedResourcesDialog
    title.setText("new" + i++ + ".mpl");
    code.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        setUnsavedChanges(true);
      }
    });
    setUnsavedChanges(true);
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

  public boolean hasUnsavedChanges() {
    return unsavedChanges.getBoolean();
  }

  /**
   * Set's whether this editor has unsaved changes.
   *
   * @param unsavedChanges
   */
  public void setUnsavedChanges(boolean unsavedChanges) {
    this.unsavedChanges.setBoolean(unsavedChanges);
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
   * Checks, whether the file of this editor still exists. If the file does not exist,
   * <code>{@link #setUnsavedChanges}(true)</code> is called, to make sure no data is lost.
   */
  public void checkFile() {
    if (file == null) {
      return;
    }
    if (hasUnsavedChanges()) {
      return;
    }
    if (!file.exists()) {
      setUnsavedChanges(true);
    }
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
    String content = FileUtils.toUnixLineEnding(new String(bytes));
    code.setText(content);
    setUnsavedChanges(false);
  }

  /**
   * Saves the changes to this Editor's File, overwriting the content. The file and all it's parent
   * directories will be created if necassary. If the file is null a JFileChooser dialog will be
   * opened.<br>
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
        context.compile(this);
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
   */
  public void saveUnder() {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    JFileChooser chooser = getMplChooser();
    chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), getTitle()));
    int userAction = chooser.showSaveDialog(activeWindow);
    if (userAction != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = chooser.getSelectedFile();
    if (!file.getName().endsWith(".mpl")) {
      file = new File(file.getAbsolutePath() + ".mpl");
    }
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

  public MplCompilationResult compile(CompilerOptions options) throws CompilationFailedException {
    File file = getFile();
    if (file == null || !file.exists()) {
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow,
          "You need to save this File before it can be compiled!", "Compilation Failed!",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
    try {
      return MplCompiler.compile(file, options);
    } catch (IOException ex) {
      ex.printStackTrace();
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow, ex.getMessage(), ex.getClass().getSimpleName(),
          JOptionPane.ERROR_MESSAGE);
    }
    return null;
  }

  public void searchAndReplace() {
    SearchAndReplaceDialog dialog = context.getSearchAndReplaceDialog();
    SearchAndReplaceDialogPM pm = dialog.getPresentationModel();
    if (pm != null) {
      JTextComponent component = pm.getComponent();
      if (component != null) {
        String selected = component.getSelectedText();
        if (selected != null) {
          pm.setSearch(selected);
        }
      }
    }
    dialog.setVisible(true);
  }

  MplEditor getView() {
    return view;
  }

  void setView(MplEditor view) {
    this.view = view;
  }

}
