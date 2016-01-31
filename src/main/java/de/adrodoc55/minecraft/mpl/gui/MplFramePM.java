package de.adrodoc55.minecraft.mpl.gui;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.Main;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.gui.MplEditor;
import de.adrodoc55.minecraft.mpl.gui.MplEditorPM.Context;

public class MplFramePM extends AbstractPM {

  ListPM<MplEditorPM> editors = new ListPM<MplEditorPM>();
  OperationPM newFile = new OperationPM();
  OperationPM openFile = new OperationPM();
  OperationPM saveFile = new OperationPM();
  OperationPM saveFileUnder = new OperationPM();
  OperationPM compileFile = new OperationPM();
  OperationPM compileFileUnder = new OperationPM();

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
    try {
      MplEditorPM editorPm = new MplEditorPM(createDefaultContext());
      editorPm.setFile(file);
      addMplEditorPm(editorPm);
      editorPm.load();
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
    MplEditorPM selected = editors.getSelection().getFirst();
    if (selected == null) {
      return;
    }
    File file = selected.getFile();
    try {
      String targetFileName = FileUtils.getFilenameWithoutExtension(file) + ".py";
      File dir = getCompilationDir();
      if (dir == null) {
        return;
      }
      Main.main(file, new File(dir, targetFileName));
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
  }

  @Operation
  public void compileFileUnder() {
    chooseCompilationDir();
    compileFile();
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

  private Context createDefaultContext() {
    return new MplEditorPM.Context() {
      @Override
      public void close(MplEditorPM editorPm) {
        editors.remove(editorPm);
      }
    };
  }

}
