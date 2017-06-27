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
package de.adrodoc55.minecraft.mpl.ide.fx.editor;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.commons.FileUtils.getFileNameWithoutExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.annotation.Nullable;

import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.LocalFile;
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
import org.eclipse.fx.core.AppMemento;
import org.eclipse.fx.core.ThreadSynchronize;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.text.ui.Feature;
import org.eclipse.fx.text.ui.contentassist.IContextInformationValidator;
import org.eclipse.fx.text.ui.source.AnnotationPresenter;
import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.text.ui.source.SourceViewerConfiguration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.adrodoc55.minecraft.mpl.MplPartitioner;
import de.adrodoc55.minecraft.mpl.MplPresentationReconciler;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.conversion.MplConverter;
import de.adrodoc55.minecraft.mpl.conversion.PythonConverter;
import de.adrodoc55.minecraft.mpl.conversion.SchematicConverter;
import de.adrodoc55.minecraft.mpl.conversion.StructureConverter;
import de.adrodoc55.minecraft.mpl.ide.fx.MplConstants;
import de.adrodoc55.minecraft.mpl.ide.fx.MplOptions;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.findreplace.FindReplaceDialog;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.completion.MplGraphicalCompletionProposal;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.completion.MplProposalComputer;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.contextinfo.MplContextInformationPresenter;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.hover.MplHoverInformationProvider;
import de.adrodoc55.minecraft.mpl.ide.fx.editor.marker.MplAnnotationPresenter;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * @author Adrodoc55
 */
public class MplEditor extends TextEditor implements MplSourceViewer.Context {
  public static MplEditor create(Path path, BorderPane pane, EventBus eventBus, Context context) {
    MplEditor editor = new MplEditor(eventBus, context);
    StringInput input = new DelegatingLocalSourceFileInput(
        new LocalSourceFileInput(path, StandardCharsets.UTF_8, eventBus));

    EditorContextMenuProvider contextMenuProvider = (Control styledText, Type type) -> {
    };
    ContextInformationPresenter contextInformationPresenter = new MplContextInformationPresenter();
    EditingContext editingContext = new DelegatingEditingContext();
    IDocument document = new InputDocument(input, eventBus);
    SourceViewerConfiguration configuration = createConfiguration(input, document, editingContext);
    IDocumentPartitioner partitioner = new MplPartitioner();
    Property<Input<?>> activeInput = editor.activeInput;
    @SuppressWarnings("unchecked")
    Property<Double> zoomFactor = (Property<Double>) (Property<?>) new SimpleDoubleProperty(1);

    editor.initUI(pane, eventBus, contextMenuProvider, contextInformationPresenter, editingContext,
        document, configuration, partitioner, input, activeInput, zoomFactor);
    return editor;
  }

  private static SourceViewerConfiguration createConfiguration(Input<?> input, IDocument document,
      EditingContext editingContext) {
    ThreadSynchronize threadSynchronize = null;
    MplPresentationReconciler reconciler = new MplPresentationReconciler();
    AppMemento appMemento = null;
    ProposalComputer proposalComputer = new MplProposalComputer(document, editingContext);
    IAnnotationModel annotationModel = new AnnotationModel();
    AnnotationPresenter annotationPresenter = new MplAnnotationPresenter();
    HoverInformationProvider hoverInformationProvider = new MplHoverInformationProvider();
    CompletionProposalPresenter proposalPresenter = MplGraphicalCompletionProposal::new;
    SearchProvider searchProvider = null;
    NavigationProvider navigationProvider = null;
    EditorOpener editorOpener = null;
    BehaviorContributor behaviorContributor = null;
    IContextInformationValidator contextInformationValidator = null;
    DefaultSourceViewerConfiguration result = new DefaultSourceViewerConfiguration(
        threadSynchronize, input, reconciler, appMemento, proposalComputer, annotationModel,
        annotationPresenter, hoverInformationProvider, proposalPresenter, searchProvider,
        navigationProvider, editorOpener, behaviorContributor, contextInformationValidator);
    result.getFeatures().add(Feature.SHOW_LINE_NUMBERS);
    return result;
  }

  public interface Context {
    MplOptions getMplOptions();

    @Nullable
    MplCompilationResult compile(File file, boolean silent);

    FindReplaceDialog getFindReplaceDialog();
  }

  private final Context context;
  private final EventBus eventBus;
  private final BooleanProperty modified = new SimpleBooleanProperty(this, "modified");
  private final Property<Input<?>> activeInput = new SimpleObjectProperty<>(this, "activeInput");

  public MplEditor(EventBus eventBus, Context context) {
    setInsertSpacesForTab(true);
    setTabAdvance(2);
    this.eventBus = checkNotNull(eventBus, "eventBus == null!");
    this.context = checkNotNull(context, "context == null!");
  }

  public ReadOnlyBooleanProperty modifiedProperty() {
    return modified;
  }

  public boolean isModified() {
    return modified.get();
  }

  @Override
  public void setModified(boolean modified) {
    this.modified.set(modified);
  }

  @Override
  public FindReplaceDialog getFindReplaceDialog() {
    return context.getFindReplaceDialog();
  }

  public File getFile() {
    LocalFile localFile = (LocalFile) activeInput.getValue();
    Path path = localFile.getPath();
    return path.toFile();
  }

  public void setFile(Path path) {
    DelegatingLocalSourceFileInput input = (DelegatingLocalSourceFileInput) activeInput.getValue();
    input.setDelegate(new LocalSourceFileInput(path, StandardCharsets.UTF_8, eventBus));
  }

  @Override
  public void save() {
    super.save();
    setModified(false);
    context.compile(getFile(), true);
  }

  @Override
  protected SourceViewer createSourceViewer() {
    return new MplSourceViewer(this);
  }

  private @Nullable Window getWindow() {
    SourceViewer sourceViewer = getSourceViewer();
    if (sourceViewer == null)
      return null;
    Scene scene = sourceViewer.getScene();
    if (scene == null)
      return null;
    return scene.getWindow();
  }

  private @Nullable File chooseOutputFile(@Nullable File initialDir,
      ExtensionFilter extensionFilter) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Compile to " + extensionFilter.getDescription());
    fileChooser.setInitialDirectory(initialDir);
    String initialFileName = getFileNameWithoutExtension(getFile());
    fileChooser.setInitialFileName(initialFileName);
    fileChooser.getExtensionFilters().add(extensionFilter);
    return fileChooser.showSaveDialog(getWindow());
  }

  private @Nullable File structureFile;

  private @Nullable File getStructureFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || structureFile == null) {
      structureFile = chooseOutputFile(initialDir, MplConstants.STRUCTURE_EXTENSION);
    }
    return structureFile;
  }

  public File compileToStructure(@Nullable File initialDir, boolean useCachedFile)
      throws IOException {
    MplCompilationResult result = context.compile(getFile(), false);
    if (result == null) {
      return null;
    }
    File file = getStructureFile(initialDir, useCachedFile);
    if (file == null) {
      return null;
    }
    compileTo(file, result, new StructureConverter());
    return file;
  }

  private @Nullable File schematicFile;

  private @Nullable File getSchematicFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || schematicFile == null) {
      schematicFile = chooseOutputFile(initialDir, MplConstants.SCHEMATIC_EXTENSION);
    }
    return schematicFile;
  }

  public File compileToSchematic(@Nullable File initialDir, boolean useCachedFile)
      throws IOException {
    MplCompilationResult result = context.compile(getFile(), false);
    if (result == null) {
      return null;
    }
    File file = getSchematicFile(initialDir, useCachedFile);
    if (file == null) {
      return null;
    }
    compileTo(file, result, new SchematicConverter());
    return file;
  }

  private @Nullable File mceditFile;

  private @Nullable File getMceditFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || mceditFile == null) {
      mceditFile = chooseOutputFile(initialDir, MplConstants.MCEDIT_EXTENSION);
    }
    return mceditFile;
  }

  public File compileToMcedit(@Nullable File initialDir, boolean useCachedFile) throws IOException {
    MplCompilationResult result = context.compile(getFile(), false);
    if (result == null) {
      return null;
    }
    File file = getMceditFile(initialDir, useCachedFile);
    if (file == null) {
      return null;
    }
    compileTo(file, result, new PythonConverter());
    return file;
  }

  private void compileTo(File file, MplCompilationResult result, MplConverter converter)
      throws FileNotFoundException, IOException {
    String name = getFileNameWithoutExtension(file);
    OutputStream out = new FileOutputStream(file);
    MinecraftVersion version = context.getMplOptions().getMinecraftVersion();
    converter.write(result, name, out, version);
  }
}
