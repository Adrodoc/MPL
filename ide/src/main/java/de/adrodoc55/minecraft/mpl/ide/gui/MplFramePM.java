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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.Selection;
import org.beanfabrics.support.Operation;

import com.google.common.collect.ImmutableListMultimap;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.compilation.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.conversion.CommandConverter;
import de.adrodoc55.minecraft.mpl.conversion.MplConverter;
import de.adrodoc55.minecraft.mpl.conversion.PythonConverter;
import de.adrodoc55.minecraft.mpl.conversion.SchematicConverter;
import de.adrodoc55.minecraft.mpl.conversion.StructureConverter;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.command.CommandDialog;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.command.CommandDialogController;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.command.CommandDialogPM;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.compilerexception.ExceptionDialog;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.compileroptions.CompilerOptionsDialogController;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.compileroptions.CompilerOptionsDialogPM;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.searchandreplace.SearchAndReplaceDialogController;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.searchandreplace.SearchAndReplaceDialogPM;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.unsaved.UnsavedResourcesDialog;
import de.adrodoc55.minecraft.mpl.ide.gui.dialog.unsaved.UnsavedResourcesDialogPM;
import de.adrodoc55.minecraft.mpl.version.MplVersion;

/**
 * @author Adrodoc55
 */
public class MplFramePM extends AbstractPM {
  public static final String COMPILE_TO_COMMAND = "Compile to Command";
  public static final String COMPILE_TO_STRUCTURE = "Compile to Structure";
  public static final String COMPILE_TO_SCHEMATIC = "Compile to Schematic";
  public static final String COMPILE_TO_FILTER = "Compile to MCEdit Filter";

  final ListPM<MplEditorPM> editors = new ListPM<MplEditorPM>();
  final OperationPM newFile = new OperationPM();
  final OperationPM openFile = new OperationPM();
  final OperationPM saveFile = new OperationPM();
  final OperationPM saveFileUnder = new OperationPM();
  final OperationPM compileToCommand = new OperationPM();
  final OperationPM compileToStructure = new OperationPM();
  final OperationPM compileToStructureUnder = new OperationPM();
  final OperationPM compileToSchematic = new OperationPM();
  final OperationPM compileToSchematicUnder = new OperationPM();
  final OperationPM compileToFilter = new OperationPM();
  final OperationPM compileToFilterUnder = new OperationPM();
  final OperationPM openOptionsDialog = new OperationPM();

  private final CompilerOptionsDialogController optionCtrl = new CompilerOptionsDialogController();

  SearchAndReplaceDialogController sarController =
      new SearchAndReplaceDialogController(new SearchAndReplaceDialogPM.Context() {
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
    newFile.setDescription("Create a new file");
    openFile.setDescription("Open a file");
    saveFile.setDescription("Save the current file");
    compileToCommand.setDescription(COMPILE_TO_COMMAND);
    compileToStructure.setDescription(COMPILE_TO_STRUCTURE);
    compileToStructureUnder.setDescription(COMPILE_TO_STRUCTURE);
    compileToSchematic.setDescription(COMPILE_TO_SCHEMATIC);
    compileToSchematicUnder.setDescription(COMPILE_TO_SCHEMATIC);
    compileToFilter.setDescription(COMPILE_TO_FILTER);
    compileToFilterUnder.setDescription(COMPILE_TO_FILTER);
    PMManager.setup(this);
  }

  @Operation
  public void newFile() {
    MplEditorPM editorPm = new MplEditorPM(createEditorContext());
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
      MplEditorPM editorPm = new MplEditorPM(createEditorContext());
      editorPm.setFile(file);
      editorPm.load();
      addMplEditorPm(editorPm);
      editorPm.setUnsavedChanges(false);
    } catch (IOException ex) {
      ex.printStackTrace();
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
  public void compileToCommand() {
    MplCompilationResult result = compile();
    if (result == null) {
      return;
    }
    List<String> commands = CommandConverter.convert(result);
    CommandDialogController ctrl = new CommandDialogController();
    CommandDialogPM pm = ctrl.getPresentationModel();
    CommandDialog view = ctrl.getView();
    pm.setCommands(commands);
    view.setVisible(true);
  }

  @Operation
  public void compileToStructure() {
    File dir = getCompilationDir(COMPILE_TO_STRUCTURE);
    if (dir == null) {
      return;
    }
    compileTo(new StructureConverter(), dir, ".nbt");
  }

  @Operation
  public void compileToStructureUnder() {
    chooseCompilationDir(COMPILE_TO_STRUCTURE);
    compileToSchematic();
  }

  @Operation
  public void compileToSchematic() {
    File dir = getCompilationDir(COMPILE_TO_SCHEMATIC);
    if (dir == null) {
      return;
    }
    compileTo(new SchematicConverter(), dir, ".schematic");
  }

  @Operation
  public void compileToSchematicUnder() {
    chooseCompilationDir(COMPILE_TO_SCHEMATIC);
    compileToSchematic();
  }

  @Operation
  public void compileToFilter() {
    File dir = getCompilationDir(COMPILE_TO_FILTER);
    if (dir == null) {
      return;
    }
    compileTo(new PythonConverter(), dir, ".py");
  }

  @Operation
  public void compileToFilterUnder() {
    chooseCompilationDir(COMPILE_TO_FILTER);
    compileToFilter();
  }

  public void compileTo(MplConverter converter, File dir, String fileEnding) {
    try {
      MplCompilationResult result = compile();
      if (result == null) {
        return;
      }
      MplEditorPM selected = editors.getSelection().getFirst();
      if (selected == null) {
        return;
      }
      String name = FileUtils.getFilenameWithoutExtension(selected.getTitle());
      String targetFileName = name + fileEnding;
      File outputFile = new File(dir, targetFileName);
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();
      try (FileOutputStream out = new FileOutputStream(outputFile);) {
        converter.write(result, name, out);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      JOptionPane.showMessageDialog(activeWindow, ex.getMessage(), ex.getClass().getSimpleName(),
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private MplCompilationResult compile() {
    if (warnAboutUnsavedResources()) {
      return null;
    }
    MplEditorPM selected = editors.getSelection().getFirst();
    if (selected == null) {
      return null;
    }
    try {
      return compile(selected);
    } catch (CompilationFailedException ex) {
      ExceptionDialog dialog = ExceptionDialog.create("Compilation Failed!",
          "The Compiler encountered Errors!", ex.toString());
      dialog.setVisible(true);
      return null;
    }
  }

  private MplCompilationResult compile(MplEditorPM selected) throws CompilationFailedException {
    try {
      CompilerOptionsDialogPM optionPm = optionCtrl.getPresentationModel();
      MplVersion version = optionPm.getSavedVersion();
      CompilerOptions options = optionPm.getSavedOptions();
      MplCompilationResult result = selected.compile(version, options);
      for (MplEditorPM editor : editors) {
        editor.setErrors(Collections.emptyList());
        editor.setWarnings(Collections.emptyList());
      }
      ImmutableListMultimap<File, CompilerException> warnings = result.getWarnings();
      for (File programFile : warnings.keySet()) {
        for (MplEditorPM editor : editors) {
          if (programFile.equals(editor.getFile())) {
            editor.setWarnings(warnings.get(programFile));
          }
        }
      }
      return result;
    } catch (CompilationFailedException ex) {
      ImmutableListMultimap<File, CompilerException> errors = ex.getErrors();
      for (File programFile : errors.keySet()) {
        for (MplEditorPM editor : editors) {
          if (programFile.equals(editor.getFile())) {
            editor.setErrors(errors.get(programFile));
          }
        }
      }
      throw ex;
    }
  }

  private File compilationDir;

  private File getCompilationDir(String title) {
    if (compilationDir == null) {
      chooseCompilationDir(title);
    }
    return compilationDir;
  }

  private void chooseCompilationDir(String title) {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    JFileChooser chooser = MplEditor.getDirChooser();
    int userAction = chooser.showDialog(activeWindow, title);
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
    checkFiles();
    LinkedList<MplEditorPM> unsaved = new LinkedList<MplEditorPM>();
    for (MplEditorPM editorPm : editors) {
      if (editorPm.hasUnsavedChanges() && isRelevant(editorPm)) {
        unsaved.add(editorPm);
      }
    }
    if (!unsaved.isEmpty()) {
      return showUnsavedResourcesDialog(unsaved);
    }
    return false;
  }

  /**
   * Checks for the existence of every editor's file. See {@link MplEditorPM#checkFile()} for more
   * details.
   */
  void checkFiles() {
    for (MplEditorPM editorPm : editors) {
      editorPm.checkFile();
    }
  }

  private MplEditorPM.Context createEditorContext() {
    return new MplEditorPM.Context() {
      @Override
      public void close(MplEditorPM editorPm) {
        if (editorPm.hasUnsavedChanges() && isRelevant(editorPm)) {
          ArrayList<MplEditorPM> unsaved = new ArrayList<MplEditorPM>(1);
          unsaved.add(editorPm);

          if (showUnsavedResourcesDialog(unsaved)) {
            return;
          }
        }
        editors.remove(editorPm);
      }

      @Override
      public SearchAndReplaceDialogController getSearchAndReplaceController() {
        return sarController;
      }

      @Override
      public void compile(MplEditorPM mplEditorPM) {
        try {
          MplFramePM.this.compile(mplEditorPM);
        } catch (CompilationFailedException e) {
          // Ignore
        }
      }
    };
  }

  /**
   * An editor is relevant if it is not empty or has a file.
   *
   * @param editorPm the editor
   * @return whether this editor is relevant
   */
  private static boolean isRelevant(MplEditorPM editorPm) {
    return !editorPm.code.isEmpty() || editorPm.getFile() != null;
  }

  /**
   * Shows an {@link UnsavedResourcesDialog} to the user. Returns true if the User canceled the
   * Action. <br>
   * This should be called like this:<br>
   *
   * <pre>
   * <code>
   * if (showUnsavedResourcesDialog(unsaved)) {
   *   return;
   * }
   * </code>
   * </pre>
   *
   * @return canceled - whether or not the Action should be canceled.
   */
  private boolean showUnsavedResourcesDialog(Collection<MplEditorPM> unsaved) {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    UnsavedResourcesDialog dialog = new UnsavedResourcesDialog(activeWindow);
    UnsavedResourcesDialogPM dialogPm = new UnsavedResourcesDialogPM(unsaved);
    dialog.setPresentationModel(dialogPm);
    dialog.setVisible(true);
    return dialogPm.isCanceled();
  }

  @Operation
  public void openOptionsDialog() {
    optionCtrl.getView().setVisible(true);
  }

  public void terminate() {
    if (warnAboutUnsavedResources()) {
      return;
    }
    System.exit(0);
  }

}
