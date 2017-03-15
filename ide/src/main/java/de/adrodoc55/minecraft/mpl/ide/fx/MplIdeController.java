package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.fx.ui.controls.filesystem.FileItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;

import de.adrodoc55.commons.eclipse.DndTabPane;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

public class MplIdeController {
  @FXML
  private DndTabPane editorTabPane;

  @FXML
  private ResourceTreeView fileExplorer;
  private RootDirItem rootDir;

  private EventBus eventBus = new SimpleEventBus();

  private void setRootDir(File directory) {
    if (directory != null) {
      if (rootDir != null) {
        rootDir.dispose();
      }
      rootDir = ResourceItem.createObservedPath(Paths.get(directory.getAbsolutePath()));
      fileExplorer.setRootDirectories(FXCollections.observableArrayList(rootDir));
    }
  }

  @FXML
  public void initialize() {
    fileExplorer.addEventHandler(ResourceEvent.openResourceEvent(),
        e -> openResources(e.getResourceItems()));

    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));
  }

  public void openResources(Collection<? extends ResourceItem> resources) {
    resources.stream()//
        .filter(it -> it instanceof FileItem)//
        .map(it -> (FileItem) it)//
        .filter(it -> it.getName().endsWith(".mpl"))//
        .forEach(this::openFile);
  }

  private void openFile(FileItem item) {
    Path path = (Path) item.getNativeResourceObject();
    Tab tab = provideTab(path);
    editorTabPane.getSelectionModel().select(tab);
  }

  private Tab provideTab(Path path) {
    return editorTabPane.getTabs().stream()//
        .filter(it -> it.getUserData() instanceof MplEditorData)//
        .filter(it -> ((MplEditorData) it.getUserData()).getPath().equals(path))//
        .findFirst().orElseGet(() -> createAndAttachTab(path));
  }

  private Tab createAndAttachTab(Path path) {
    BorderPane pane = new BorderPane();
    MplEditor editor = MplEditor.create(path, pane, eventBus);

    ReadOnlyBooleanProperty modifiedProperty = editor.modifiedProperty();
    StringExpression titleText = Bindings.createStringBinding(() -> {
      return modifiedProperty.get() ? "*" : "";
    }, modifiedProperty).concat(path.getFileName());

    Tab t = new Tab();
    t.textProperty().bind(titleText);
    t.setContent(pane);
    t.setUserData(new MplEditorData(path, editor));
    editorTabPane.getTabs().add(t);
    return t;
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
}
