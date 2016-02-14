/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.gui;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.Selection;
import org.beanfabrics.support.Operation;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.MplCompiler;
import de.adrodoc55.minecraft.mpl.OneCommandConverter;
import de.adrodoc55.minecraft.mpl.PythonConverter;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.gui.dialog.ExceptionDialog;
import de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialog;
import de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM;
import de.adrodoc55.minecraft.mpl.gui.dialog.SearchAndReplaceDialog;
import de.adrodoc55.minecraft.mpl.gui.dialog.SearchAndReplaceDialogControler;
import de.adrodoc55.minecraft.mpl.gui.dialog.SearchAndReplaceDialogPM;
import de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialog;
import de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM;

public class MplFramePM extends AbstractPM {

  ListPM<MplEditorPM> editors = new ListPM<MplEditorPM>();
  OperationPM newFile = new OperationPM();
  OperationPM openFile = new OperationPM();
  OperationPM saveFile = new OperationPM();
  OperationPM saveFileUnder = new OperationPM();
  OperationPM compileFile = new OperationPM();
  OperationPM compileFileUnder = new OperationPM();
  OperationPM compileCommand = new OperationPM();

  SearchAndReplaceDialogControler sarController =
      new SearchAndReplaceDialogControler(new SearchAndReplaceDialogPM.Context() {
        @Override
        public JTextComponent getComponent() {
          MplEditorPM selected = editors.getSelection().getFirst();
          MplEditor view = selected.getView();
          if (view == null) {
            return null;
          } else {
            return view.getTextPane();
          }
        }
      });

  public MplFramePM() {
    PMManager.setup(this);
  }

  @Operation
  public void newFile() {
    MplEditorPM editorPm = new MplEditorPM(createDefaultContext());
    addMplEditorPm(editorPm);
  }

  @Operation
  public void openFile() {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    JFileChooser chooser = MplEditor.getMplChooser();
    int userAction = chooser.showOpenDialog(activeWindow);
    if (userAction != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = chooser.getSelectedFile();
    if (!file.exists()) {
      String message = "The File '" + file.getPath() + "' couldn't be found!";
      JOptionPane.showMessageDialog(chooser, message, "Error", JOptionPane.ERROR_MESSAGE);
      openFile();
      return;
    }
    if (!file.isFile()) {
      String message = file.getPath() + " is not a File!";
      JOptionPane.showMessageDialog(chooser, message, "Error", JOptionPane.ERROR_MESSAGE);
      openFile();
      return;
    }
    if (!file.canRead()) {
      String message = "The File '" + file.getPath() + "' couldn't be opened!";
      JOptionPane.showMessageDialog(chooser, message, "Error", JOptionPane.ERROR_MESSAGE);
      openFile();
      return;
    }
    for (MplEditorPM editorPm : editors) {
      if (file.equals(editorPm.getFile())) {
        Selection<MplEditorPM> selection = editors.getSelection();
        selection.clear();
        selection.add(editorPm);
        return;
      }
    }
    try {
      MplEditorPM editorPm = new MplEditorPM(createDefaultContext());
      editorPm.setFile(file);
      editorPm.load();
      addMplEditorPm(editorPm);
      editorPm.setUnsavedChanges(false);
    } catch (IOException ex) {
      String message = "The File '" + file.getPath() + "' couldn't be loaded!\n" + ex.getMessage();
      String title = ex.getClass().getSimpleName();
      JOptionPane.showMessageDialog(chooser, message, title, JOptionPane.ERROR_MESSAGE);
      openFile();
    }
  }

  private void addMplEditorPm(MplEditorPM editorPm) {
    editors.add(editorPm);
    editors.getSelection().clear();
    editors.getSelection().add(editorPm);
  }

  @Operation
  public void saveFile() {
    MplEditorPM selected = editors.getSelection().getFirst();
    if (selected == null) {
      return;
    }
    selected.save();
  }

  @Operation
  public void saveFileUnder() {
    MplEditorPM selected = editors.getSelection().getFirst();
    if (selected == null) {
      return;
    }
    selected.saveUnder();
  }

  @Operation
  public void compileFile() {
    try {
      List<CommandBlockChain> chains = compile();
      if (chains == null) {
        return;
      }
      MplEditorPM selected = editors.getSelection().getFirst();
      if (selected == null) {
        return;
      }
      File dir = getCompilationDir();
      if (dir == null) {
        return;
      }
      String name = FileUtils.getFilenameWithoutExtension(selected.getTitle());
      String targetFileName = name + ".py";
      File outputFile = new File(dir, targetFileName);
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();
      String python = PythonConverter.convert(chains, name);
      try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath());) {
        writer.write(python.toString());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow, ex.getMessage(), ex.getClass().getSimpleName(),
          JOptionPane.ERROR_MESSAGE);
    }
  }

  @Operation
  public void compileFileUnder() {
    chooseCompilationDir();
    compileFile();
  }

  @Operation
  public void compileCommand() {
    List<CommandBlockChain> chains = compile();
    if (chains == null) {
      return;
    }
    String oneCommand = OneCommandConverter.convert(chains);
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    OneCommandDialog dialog = new OneCommandDialog(activeWindow);
    OneCommandDialogPM dialogPm = new OneCommandDialogPM();
    dialogPm.setText(oneCommand);
    dialog.setPresentationModel(dialogPm);
    dialog.setVisible(true);
  }

  void checkFiles() {
    for (MplEditorPM editorPm : editors) {
      editorPm.checkFile();
    }
  }

  private List<CommandBlockChain> compile() {
    checkFiles();
    if (warnAboutUnsavedResources()) {
      return null;
    }

    MplEditorPM selected = editors.getSelection().getFirst();
    if (selected == null) {
      return null;
    }
    File file = selected.getFile();
    if (file == null || !file.exists()) {
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow,
          "You need to save this File before it can be compiled!", "Compilation Failed!",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }
    try {
      List<CommandBlockChain> chains = MplCompiler.compile(file);
      for (MplEditorPM editorPm : editors) {
        editorPm.setCompilerExceptions(Collections.emptyList());
      }
      return chains;
    } catch (CompilationFailedException ex) {
      ExceptionDialog dialog = ExceptionDialog.create("Compilation Failed!",
          "The Compiler encountered Errors!", ex.toString());
      dialog.setVisible(true);
      Map<File, List<CompilerException>> exceptions = ex.getExceptions();
      for (File programFile : exceptions.keySet()) {
        for (MplEditorPM editorPm : editors) {
          if (programFile.equals(editorPm.getFile())) {
            editorPm.setCompilerExceptions(exceptions.get(programFile));
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow, ex.getMessage(), ex.getClass().getSimpleName(),
          JOptionPane.ERROR_MESSAGE);
    }
    return null;
  }

  private File compilationDir;

  private File getCompilationDir() {
    if (compilationDir == null) {
      chooseCompilationDir();
    }
    return compilationDir;
  }

  private void chooseCompilationDir() {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    JFileChooser chooser = MplEditor.getDirChooser();
    int userAction = chooser.showDialog(activeWindow, "Compile");
    if (userAction == JFileChooser.APPROVE_OPTION) {
      compilationDir = chooser.getSelectedFile();
    }
  }

  /**
   * Warn the User about any unsaved Resources, if there are any. Returns true if the User canceled
   * the Action. <br>
   * This should be called like this:<br>
   *
   * <pre>
   * <code>
   * if (warnAboutUnsavedResources()) {
   *   return;
   * }
   * </code>
   * </pre>
   *
   * @return canceled - whether or not the Action should be canceled.
   */
  private boolean warnAboutUnsavedResources() {
    LinkedList<MplEditorPM> unsaved = new LinkedList<MplEditorPM>();
    for (MplEditorPM mplEditorPm : editors) {
      if (mplEditorPm.hasUnsavedChanges()) {
        unsaved.add(mplEditorPm);
      }
    }
    if (!unsaved.isEmpty()) {
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      UnsavedResourcesDialog dialog = new UnsavedResourcesDialog(activeWindow);
      UnsavedResourcesDialogPM dialogPm = new UnsavedResourcesDialogPM(unsaved);
      dialog.setPresentationModel(dialogPm);
      dialog.setVisible(true);
      if (dialogPm.isCanceled()) {
        return true;
      }
    }
    return false;
  }

  private MplEditorPM.Context createDefaultContext() {
    return new MplEditorPM.Context() {
      @Override
      public void close(MplEditorPM editorPm) {
        editors.remove(editorPm);
      }

      @Override
      public SearchAndReplaceDialog getSearchAndReplaceDialog() {
        return sarController.getView();
      }
    };
  }

  public void terminate() {
    if (warnAboutUnsavedResources()) {
      return;
    }
    System.exit(0);
  }

}
