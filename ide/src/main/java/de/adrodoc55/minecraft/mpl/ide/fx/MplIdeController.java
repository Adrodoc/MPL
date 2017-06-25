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
package de.adrodoc55.minecraft.mpl.ide.fx;

import static de.adrodoc55.minecraft.mpl.ide.fx.editor.marker.MplAnnotationType.ERROR;
import static de.adrodoc55.minecraft.mpl.ide.fx.editor.marker.MplAnnotationType.WARNING;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.fx.ui.controls.filesystem.DirItem;
import org.eclipse.fx.ui.controls.filesystem.FileItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;
import org.eclipse.fx.ui.controls.tabpane.DndTabPane;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

import com.google.common.collect.ImmutableListMultimap;

import de.adrodoc55.minecraft.mpl.compilation.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.compilation.MplCompiler;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;
import de.adrodoc55.minecraft.mpl.conversion.CommandConverter;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.multicontent.ImportCommandDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.options.OptionsDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.unsaved.UnsavedResourcesDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.MplEditor;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.marker.MplAnnotation;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.marker.MplAnnotationType;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * @author Adrodoc55
 */
public class MplIdeController {
  @FXML
  private Node root;

  @FXML
  private DndTabPane editorTabPane;

  private RootDirItem rootDir;
  @FXML
  private ResourceTreeView fileExplorer;
  @FXML
  private MenuItem newFileMenuItem;
  @FXML
  private MenuItem newDirectoryMenuItem;
  @FXML
  private MenuItem renameResourceMenuItem;

  private MplOptions options = new MplOptions(//
      MinecraftVersion.getDefault(), //
      new CompilerOptions(//
          CompilerOption.TRANSMITTER, //
          CompilerOption.DELETE_ON_UNINSTALL//
      )//
  );

  private EventBus eventBus = new SimpleEventBus();

  private final Map<Path, Tab> tabs = new HashMap<>();
  private final Map<Path, TextEditor> editors = new HashMap<>();

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
  private void initialize() {
    setupWindowCloseRequestListener();

    fileExplorer.addEventHandler(ResourceEvent.openResourceEvent(),
        e -> openResources(e.getResourceItems()));

    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));
  }

  private void setupWindowCloseRequestListener() {
    EventHandler<WindowEvent> onWindowCloseRequest = e -> {
      if (warnAboutUnsavedResources())
        e.consume();
    };
    ChangeListener<Window> windowPropertyListener = (observableWindow, oldWindow, newWindow) -> {
      if (oldWindow != null && onWindowCloseRequest == oldWindow.getOnCloseRequest()) {
        oldWindow.setOnCloseRequest(null);
      }
      if (newWindow != null) {
        newWindow.setOnCloseRequest(onWindowCloseRequest);
      }
    };
    ChangeListener<Scene> scenePropertyListener = (observableScene, oldScene, newScene) -> {
      if (oldScene != null) {
        oldScene.windowProperty().removeListener(windowPropertyListener);
      }
      if (newScene != null) {
        windowPropertyListener.changed(null, null, newScene.getWindow());
        newScene.windowProperty().addListener(windowPropertyListener);
      }
    };
    scenePropertyListener.changed(null, null, root.getScene());
    root.sceneProperty().addListener(scenePropertyListener);
  }

  private Window getWindow() {
    return root.getScene().getWindow();
  }

  @FXML
  public void options() {
    OptionsDialog dialog = new OptionsDialog(getWindow(), options);
    Optional<MplOptions> result = dialog.showAndWait();
    if (result.isPresent()) {
      options = result.get();
    }
  }

  @FXML
  public void showResourceContextMenu(WindowEvent e) {
    ObservableList<ResourceItem> items = fileExplorer.getSelectedItems();
    if (items.size() > 1) {
      newFileMenuItem.setDisable(true);
      newDirectoryMenuItem.setDisable(true);
      renameResourceMenuItem.setDisable(true);
      renameResourceMenuItem.getStyleClass().set(1, "rename-file");
    } else {
      newFileMenuItem.setDisable(false);
      newDirectoryMenuItem.setDisable(false);
      renameResourceMenuItem.setDisable(false);
      Iterator<ResourceItem> it = items.iterator();
      if (it.hasNext() && it.next() instanceof DirItem) {
        renameResourceMenuItem.getStyleClass().set(1, "rename-dir");
      } else {
        renameResourceMenuItem.getStyleClass().set(1, "rename-file");
      }
    }
  }

  @FXML
  public void newFile() {
    ObservableList<ResourceItem> items = fileExplorer.getSelectedItems();
    Iterator<ResourceItem> it = items.iterator();
    if (it.hasNext()) {
      ResourceItem first = it.next();
      File dir = getDirectory(first);
      try {
        new File(dir, "new123.mpl").createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private File getDirectory(ResourceItem item) {
    Path path = (Path) item.getNativeResourceObject();
    File dir = path.toFile();
    if (!dir.isDirectory()) {
      dir = dir.getParentFile();
    }
    return dir;
  }

  @FXML
  public void newDirectory() {

  }

  @FXML
  public void renameResource() {

  }

  @FXML
  public void deleteResources() {
    ObservableList<ResourceItem> items = fileExplorer.getSelectedItems();

    int itemSize = items.size();
    String message = "Are you sure you want to delete ";
    if (itemSize == 1) {
      message += "'" + items.get(0).getName() + "'";
    } else {
      message += "these " + itemSize + " files";
    }
    message += "?";
    Alert alert = new Alert(CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
    alert.setHeaderText(null);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && ButtonData.YES.equals(result.get().getButtonData())) {
      for (ResourceItem item : items) {
        Path path = (Path) item.getNativeResourceObject();
        try {
          Files.delete(path);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  @FXML
  public void cutResources() {
    copyResources(true);
  }

  @FXML
  public void copyResources() {
    copyResources(false);
  }

  private static DataFormat CUT = new DataFormat("application/cut");

  private void copyResources(boolean cut) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    List<File> files = fileExplorer.getSelectedItems().stream()//
        .map(it -> ((Path) it.getNativeResourceObject()).toFile())//
        .collect(Collectors.toList());
    content.putFiles(files);
    content.put(CUT, cut);
    clipboard.setContent(content);
  }

  @FXML
  public void pasteResources() {
    ObservableList<ResourceItem> items = fileExplorer.getSelectedItems();


    Iterator<ResourceItem> it = items.iterator();
    if (it.hasNext()) {
      ResourceItem first = it.next();
      Path targetDir = getDirectory(first).toPath();

      Clipboard clipboard = Clipboard.getSystemClipboard();
      List<File> files = clipboard.getFiles();
      Object cut = clipboard.getContent(CUT);
      if (cut instanceof Boolean && ((Boolean) cut).booleanValue()) {
        for (File file : files) {
          try {
            Files.move(file.toPath(), targetDir.resolve(file.getName()));
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      } else {
        for (File file : files) {
          try {
            Files.copy(file.toPath(), targetDir.resolve(file.getName()));
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
      rootDir.refresh();
    }
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
    Tab result = tabs.get(path);
    if (result != null) {
      return result;
    }
    return createAndAttachTab(path);
  }

  private Tab createAndAttachTab(Path path) {
    BorderPane pane = new BorderPane();
    MplEditor editor = MplEditor.create(path, pane, eventBus);

    ReadOnlyBooleanProperty modifiedProperty = editor.modifiedProperty();
    StringExpression titleText = Bindings.createStringBinding(() -> {
      return modifiedProperty.get() ? "*" : "";
    }, modifiedProperty).concat(path.getFileName());

    Tab tab = new Tab();
    tab.textProperty().bind(titleText);
    tab.setContent(pane);
    tab.setUserData(new MplEditorData(path, editor));
    tab.setOnCloseRequest(e -> {
      if (warnAboutUnsavedResources(Arrays.asList(tab)))
        e.consume();
    });
    editorTabPane.getTabs().add(tab);
    tab.setOnClosed(e -> {
      editors.remove(path);
      tabs.remove(path);
    });
    editors.put(path, editor);
    tabs.put(path, tab);
    return tab;
  }

  private @Nullable MplEditor getSelectedEditor() {
    Tab tab = editorTabPane.getSelectionModel().getSelectedItem();
    if (tab != null) {
      Object userData = tab.getUserData();
      if (userData instanceof MplEditorData) {
        return ((MplEditorData) userData).getEditor();
      }
    }
    return null;
  }

  @FXML
  public void open() {
    DirectoryChooser chooser = new DirectoryChooser();
    File directory = chooser.showDialog(getWindow());
    setRootDir(directory);
  }

  @FXML
  public void save() {
    MplEditor editor = getSelectedEditor();
    if (editor != null) {
      editor.save();
    }
  }

  /**
   * Warn the User about unsaved Resources, if there are any. Returns {@code true} if the User
   * canceled the Action. <br>
   * This should be called like this:<br>
   *
   * <code>
   * <pre>
   * if (warnAboutUnsavedResources()) {
   *   return; // or whatever you need to do to cancel the current action.
   * }
   * </pre>
   * </code>
   *
   * @return canceled - whether or not the Action should be canceled.
   */
  public boolean warnAboutUnsavedResources() {
    return warnAboutUnsavedResources(editorTabPane.getTabs());
  }

  /**
   * Warn the User about unsaved Resources, if there are any in the specified {@link Tab}s. Returns
   * {@code true} if the User canceled the Action. <br>
   * This should be called like this:<br>
   *
   * <code>
   * <pre>
   * if (warnAboutUnsavedResources(tabs)) {
   *   return; // or whatever you need to do to cancel the current action.
   * }
   * </pre>
   * </code>
   *
   * @return canceled - whether or not the Action should be canceled.
   */
  public boolean warnAboutUnsavedResources(Collection<Tab> tabs) {
    List<Path> unsavedResources = tabs.stream()//
        .map(Tab::getUserData)//
        .filter(MplEditorData.class::isInstance)//
        .map(MplEditorData.class::cast)//
        .filter(it -> it.getEditor().modifiedProperty().get())//
        .map(MplEditorData::getPath)//
        .collect(Collectors.toList());

    if (unsavedResources.isEmpty()) {
      return false;
    }

    UnsavedResourcesDialog dialog = new UnsavedResourcesDialog(getWindow(), unsavedResources);
    Optional<Collection<Path>> resourcesToSave = dialog.showAndWait();
    if (resourcesToSave.isPresent()) {
      for (Path resourceToSave : resourcesToSave.get()) {
        TextEditor editor = editors.get(resourceToSave);
        if (editor != null) {
          editor.save();
        }
      }
      return false;
    } else {
      return true;
    }
  }

  @FXML
  public void compileToImportCommand() throws IOException {
    MplEditor selectedEditor = getSelectedEditor();
    if (selectedEditor == null)
      return;
    if (warnAboutUnsavedResources())
      return;

    Window owner = getWindow();
    File selectedFile = selectedEditor.getFile();
    clearCompilerExceptions();

    MplCompilationResult result = compile(selectedFile);
    if (result == null) {
      return;
    }
    MinecraftVersion version = options.getMinecraftVersion();
    List<String> commands = CommandConverter.convert(result, version);
    ImportCommandDialog dialog = new ImportCommandDialog(owner, commands);
    dialog.showAndWait();
  }

  private MplCompilationResult compile(File selectedFile) throws IOException {
    try {
      MinecraftVersion version = options.getMinecraftVersion();
      CompilerOptions compilerOptions = options.getCompilerOptions();
      MplCompilationResult result = MplCompiler.compile(selectedFile, version, compilerOptions);
      handleCompilerExceptions(WARNING, result.getWarnings());
      return result;
    } catch (CompilationFailedException ex) {
      handleCompilerExceptions(ERROR, ex.getErrors());
      return null;
    }
  }

  private void clearCompilerExceptions() {
    for (TextEditor editor : editors.values()) {
      IAnnotationModel annotationModel = editor.getSourceViewer().getAnnotationModel();
      for (Annotation annotation : (Iterable<Annotation>) annotationModel::getAnnotationIterator) {
        annotationModel.removeAnnotation(annotation);
      }
    }
  }

  private void handleCompilerExceptions(MplAnnotationType type,
      ImmutableListMultimap<File, CompilerException> exceptions) {
    for (Entry<File, Collection<CompilerException>> entry : exceptions.asMap().entrySet()) {
      File file = entry.getKey();
      TextEditor editor = editors.get(file.toPath());
      if (editor != null) {
        IAnnotationModel annotationModel = editor.getSourceViewer().getAnnotationModel();
        for (CompilerException ex : entry.getValue()) {
          MplAnnotation annotation = new MplAnnotation(type, ex.getLocalizedMessage());
          MplSource source = ex.getSource();
          Position position = new Position(source.getStartIndex(), source.getLength());
          annotationModel.addAnnotation(annotation, position);
        }
      }
    }
  }

  @FXML
  public void compileToStructure() {}

  @FXML
  public void compileToSchematic() {}

  @FXML
  public void compileToCbse() {}

  @FXML
  public void compileToMcedit() {}

}
