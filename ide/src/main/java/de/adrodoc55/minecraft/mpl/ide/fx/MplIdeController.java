package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;

public class MplIdeController {
  @FXML
  private ResourceTreeView fileExplorer;
  private RootDirItem rootDir;

  @FXML
  public void initialize() {
    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));
  }

  @FXML
  public void open() {
    DirectoryChooser chooser = new DirectoryChooser();
    File directory = chooser.showDialog(fileExplorer.getScene().getWindow());
    setRootDir(directory);
  }

  @FXML
  public void save() {}

  @FXML
  public void compileToImportCommand() {}

  @FXML
  public void compileToStructure() {}

  @FXML
  public void compileToSchematic() {}

  @FXML
  public void compileToCbse() {}

  @FXML
  public void compileToMcedit() {}

  private void setRootDir(File directory) {
    if (directory != null) {
      if (rootDir != null) {
        rootDir.dispose();
      }
      rootDir = ResourceItem.createObservedPath(Paths.get(directory.getAbsolutePath()));
      fileExplorer.setRootDirectories(FXCollections.observableArrayList(rootDir));
    }
  }
}
