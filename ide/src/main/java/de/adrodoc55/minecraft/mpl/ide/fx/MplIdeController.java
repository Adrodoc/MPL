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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.fx.ui.controls.filesystem.FileItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;
import org.eclipse.fx.ui.controls.filesystem.RootDirItem;

import de.adrodoc55.commons.eclipse.DndTabPane;
import de.adrodoc55.minecraft.mpl.compilation.CompilationFailedException;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.compilation.MplCompiler;
import de.adrodoc55.minecraft.mpl.conversion.CommandConverter;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.multicontent.ImportCommandDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.options.OptionsDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.unsaved.UnsavedResourcesDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.MplEditor;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * @author Adrodoc55
 */
public class MplIdeController {
  @FXML
  private DndTabPane editorTabPane;

  @FXML
  private ResourceTreeView fileExplorer;
  private RootDirItem rootDir;

  private MplOptions options = new MplOptions(//
      MinecraftVersion.getDefault(), //
      new CompilerOptions(//
          CompilerOption.TRANSMITTER, //
          CompilerOption.DELETE_ON_UNINSTALL//
      )//
  );

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
  private void initialize() {
    fileExplorer.addEventHandler(ResourceEvent.openResourceEvent(),
        e -> openResources(e.getResourceItems()));

    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));
  }

  private Window getWindow() {
    return fileExplorer.getScene().getWindow();
  }

  @FXML
  public void options() {
    OptionsDialog dialog = new OptionsDialog(getWindow(), options);
    Optional<MplOptions> result = dialog.showAndWait();
    if (result.isPresent()) {
      options = result.get();
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
   * Warn the User about unsaved Resources, if there are any. Returns true if the User canceled the
   * Action. <br>
   * This should be called like this:<br>
   *
   * <code>
   * <pre>
   * if (warnAboutUnsavedResources()) {
   *   return;
   * }
   * </pre>
   * </code>
   *
   * @return canceled - whether or not the Action should be canceled.
   */
  public boolean warnAboutUnsavedResources() {
    List<Path> unsavedResources = editorTabPane.getTabs().stream()//
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
        Optional<? extends TextEditor> editor = getEditor(resourceToSave);
        if (editor.isPresent()) {
          editor.get().save();
        }
      }
      return false;
    } else {
      return true;
    }
  }

  private Optional<? extends TextEditor> getEditor(Path path) {
    return editorTabPane.getTabs().stream()//
        .map(Tab::getUserData)//
        .filter(MplEditorData.class::isInstance)//
        .map(MplEditorData.class::cast)//
        .filter(it -> it.getPath().equals(path))//
        .map(it -> it.getEditor())//
        .findFirst();
  }

  @FXML
  public void compileToImportCommand() throws IOException, CompilationFailedException {
    MplEditor editor = getSelectedEditor();
    if (editor == null) {
      return;
    }
    if (warnAboutUnsavedResources()) {
      return;
    }
    Window owner = getWindow();
    File file = editor.getFile();
    MinecraftVersion version = MinecraftVersion.getDefault();
    MplCompilationResult result = MplCompiler.compile(file, version, new CompilerOptions());
    List<String> commands = CommandConverter.convert(result, version);
    ImportCommandDialog dialog = new ImportCommandDialog(owner, commands);
    dialog.showAndWait();
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
