package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.LocalSourceFileInput;
import org.eclipse.fx.code.editor.StringInput;
import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.code.editor.fx.services.CompletionProposalPresenter;
import org.eclipse.fx.code.editor.fx.services.ContextInformationPresenter;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider.Type;
import org.eclipse.fx.code.editor.fx.services.internal.DefaultSourceViewerConfiguration;
import org.eclipse.fx.code.editor.services.BehaviorContributor;
import org.eclipse.fx.code.editor.services.DelegatingEditingContext;
import org.eclipse.fx.code.editor.services.EditingContext;
import org.eclipse.fx.code.editor.services.EditorOpener;
import org.eclipse.fx.code.editor.services.HoverInformationProvider;
import org.eclipse.fx.code.editor.services.InputDocument;
import org.eclipse.fx.code.editor.services.NavigationProvider;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.fx.code.editor.services.SearchProvider;
import org.eclipse.fx.core.ThreadSynchronize;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.fx.text.ui.contentassist.IContextInformationValidator;
import org.eclipse.fx.text.ui.source.AnnotationPresenter;
import org.eclipse.fx.text.ui.source.SourceViewerConfiguration;
import org.eclipse.fx.ui.controls.filesystem.FileItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceEvent;
import org.eclipse.fx.ui.controls.filesystem.ResourceItem;
import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.adrodoc55.minecraft.mpl.MplPartitioner;
import de.adrodoc55.minecraft.mpl.MplPresentationReconciler;
import javafx.application.Application;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
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
    final TextEditor editor;

    public EditorData(Path path, TextEditor editor) {
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

    setRootDir(new File("C:/Users/Adrian/Documents/Mpl"));

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
    setRootDir(directory);
  }

  private void setRootDir(File directory) {
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
    BorderPane pane = new BorderPane();
    StringInput input = new LocalSourceFileInput(path, StandardCharsets.UTF_8, eventBus);
    TextEditor editor = createEditor(pane, input);

    // ReadOnlyBooleanProperty modifiedProperty = editor.modifiedProperty();
    // StringExpression titleText = Bindings.createStringBinding(() -> {
    // return modifiedProperty.get() ? "*" : "";
    // }, modifiedProperty).concat(item.getName());
    StringExpression titleText = new ReadOnlyStringWrapper(item.getName());

    Tab t = new Tab();
    t.textProperty().bind(titleText);
    t.setContent(pane);
    t.setUserData(new EditorData(path, editor));
    tabFolder.getTabs().add(t);
    return t;
  }

  private TextEditor createEditor(BorderPane pane, StringInput input) {
    EditorContextMenuProvider contextMenuProvider = (Control styledText, Type type) -> {
    };
    ContextInformationPresenter contextInformationPresenter = null;
    EditingContext editingContext = new DelegatingEditingContext();
    IDocument document = new InputDocument(input, eventBus);
    SourceViewerConfiguration configuration = createConfiguration(input, document, editingContext);
    IDocumentPartitioner partitioner = new MplPartitioner();
    Property<Input<?>> activeInput = null;
    @SuppressWarnings("unchecked")
    Property<Double> zoomFactor = (Property<Double>) (Property<?>) new SimpleDoubleProperty(1);


    MplEditor editor = new MplEditor();
    editor.initUI(pane, eventBus, contextMenuProvider, contextInformationPresenter, editingContext,
        document, configuration, partitioner, input, activeInput, zoomFactor);
    return editor;
  }

  private SourceViewerConfiguration createConfiguration(Input<?> input, IDocument document,
      EditingContext editingContext) {
    ThreadSynchronize threadSynchronize = null;
    MplPresentationReconciler reconciler = new MplPresentationReconciler();
    ProposalComputer proposalComputer = new MplProposalComputer(document, editingContext);
    IAnnotationModel annotationModel = null;
    AnnotationPresenter annotationPresenter = null;
    HoverInformationProvider hoverInformationProvider = null;
    CompletionProposalPresenter proposalPresenter = MplGraphicalCompletionProposal::new;
    SearchProvider searchProvider = null;
    NavigationProvider navigationProvider = null;
    EditorOpener editorOpener = null;
    BehaviorContributor behaviorContributor = null;
    IContextInformationValidator contextInformationValidator = null;
    return new DefaultSourceViewerConfiguration(threadSynchronize, input, reconciler,
        proposalComputer, annotationModel, annotationPresenter, hoverInformationProvider,
        proposalPresenter, searchProvider, navigationProvider, editorOpener, behaviorContributor,
        contextInformationValidator);
  }

}
