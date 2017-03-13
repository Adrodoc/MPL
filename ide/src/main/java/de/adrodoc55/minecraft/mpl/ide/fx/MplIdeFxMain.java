package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.fx.code.editor.LocalSourceFileInput;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.fx.ui.controls.filesystem.FileItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MplIdeFxMain extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  private TabPane tabFolder;
  private ResourceTreeView viewer;
  private EventBus eventBus = new SimpleEventBus();

  static class EditorData {
    final Path path;
    final MplEditor editor;

    public EditorData(Path path, MplEditor editor) {
      this.path = path;
      this.editor = editor;
    }
  }

  @Override
  public void start(Stage stage) {
    BorderPane root = new BorderPane();
    root.setTop(createMenuBar());

    viewer = new ResourceTreeView();
    viewer.addEventHandler(ResourceEvent.openResourceEvent(), this::handleOpenResource);
    root.setLeft(viewer);

    tabFolder = new TabPane();
    root.setCenter(tabFolder);

    Scene s = new Scene(root, 800, 600);
    s.getStylesheets().add(getClass().getResource("/syntax/highlighting/mpl.css").toExternalForm());

    stage.setScene(s);
    stage.show();
  }

  private MenuBar createMenuBar() {
    MenuBar bar = new MenuBar();

    Menu fileMenu = new Menu("File");

    MenuItem rootDirectory = new MenuItem("Select root folder ...");
    rootDirectory.setOnAction(this::handleSelectRootFolder);

    MenuItem saveFile = new MenuItem("Save");
    saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
    saveFile.setOnAction(this::handleSave);


    fileMenu.getItems().addAll(rootDirectory, saveFile);

    bar.getMenus().add(fileMenu);

    return bar;
  }

  private void handleSelectRootFolder(ActionEvent e) {
    DirectoryChooser chooser = new DirectoryChooser();
    File directory = chooser.showDialog(viewer.getScene().getWindow());
    if (directory != null) {
      viewer.setRootDirectories(FXCollections.observableArrayList(
          ResourceItem.createObservedPath(Paths.get(directory.getAbsolutePath()))));
    }
  }

  private void handleSave(ActionEvent e) {
    Tab t = tabFolder.getSelectionModel().getSelectedItem();
    if (t != null) {
      ((EditorData) t.getUserData()).editor.save();
    }
  }

  private void handleOpenResource(ResourceEvent<ResourceItem> e) {
    List<ResourceItem> items = e.getResourceItems();
    items.stream()//
        .filter(r -> r instanceof FileItem)//
        .map(r -> (FileItem) r)//
        .filter(r -> r.getName().endsWith(".mpl"))//
        .forEach(this::handle);
  }

  private void handle(FileItem item) {
    Path path = (Path) item.getNativeResourceObject();

    Tab tab = tabFolder.getTabs().stream()
        .filter(t -> ((EditorData) t.getUserData()).path.equals(path)).findFirst().orElseGet(() -> {
          return createAndAttachTab(path, item);
        });
    tabFolder.getSelectionModel().select(tab);
  }

  private Tab createAndAttachTab(Path path, FileItem item) {
    BorderPane p = new BorderPane();
    MplEditor editor =
        new MplEditor(new LocalSourceFileInput(path, StandardCharsets.UTF_8, eventBus), eventBus);
    editor.initUI(p);

    // ReadOnlyBooleanProperty modifiedProperty = editor.modifiedProperty();
    // StringExpression titleText = Bindings.createStringBinding(() -> {
    // return modifiedProperty.get() ? "*" : "";
    // }, modifiedProperty).concat(item.getName());

    Tab t = new Tab();
    // t.textProperty().bind(titleText);
    t.setContent(p);
    t.setUserData(new EditorData(path, editor));
    tabFolder.getTabs().add(t);
    return t;
  }

}
