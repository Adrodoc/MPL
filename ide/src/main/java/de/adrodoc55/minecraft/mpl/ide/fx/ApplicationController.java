package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;

public class ApplicationController {
  @FXML
  private ResourceTreeView packageExplorer;

  @FXML
  public void initialize() {

    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));
  }

  private void setRootDir(File directory) {
    if (directory != null) {
      packageExplorer.setRootDirectories(FXCollections.observableArrayList(
          ResourceItem.createObservedPath(Paths.get(directory.getAbsolutePath()))));
    }
  }

}
