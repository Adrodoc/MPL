package de.adrodoc55.minecraft.mpl.ide.fx;

import static javafx.scene.input.KeyCode.DIGIT7;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
import org.eclipse.fx.text.ui.contentassist.IContextInformationValidator;
import org.eclipse.fx.text.ui.source.AnnotationPresenter;
import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.text.ui.source.SourceViewerConfiguration;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea;
import org.eclipse.fx.ui.controls.styledtext.StyledTextContent;
import org.eclipse.fx.ui.controls.styledtext.StyledTextContent.TextChangeListener;
import org.eclipse.fx.ui.controls.styledtext.TextChangedEvent;
import org.eclipse.fx.ui.controls.styledtext.TextChangingEvent;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.adrodoc55.minecraft.mpl.MplPartitioner;
import de.adrodoc55.minecraft.mpl.MplPresentationReconciler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class MplEditor extends TextEditor {
  public static MplEditor create(Path path, BorderPane pane, EventBus eventBus) {
    StringInput input = new LocalSourceFileInput(path, StandardCharsets.UTF_8, eventBus);

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

  private static SourceViewerConfiguration createConfiguration(Input<?> input, IDocument document,
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

  private class ModifiedListener implements TextChangeListener {
    @Override
    public void textChanged(TextChangedEvent event) {
      modified.set(true);
    }

    @Override
    public void textSet(TextChangedEvent event) {
      modified.set(true);
    }

    @Override
    public void textChanging(TextChangingEvent event) {
      modified.set(true);
    }
  }

  private final BooleanProperty modified = new SimpleBooleanProperty();
  private final ModifiedListener modifiedListener = new ModifiedListener();

  public MplEditor() {
    setInsertSpacesForTab(true);
    setTabAdvance(2);
  }

  public ReadOnlyBooleanProperty modifiedProperty() {
    return modified;
  }

  @Override
  public void save() {
    super.save();
    modified.set(false);
  }

  @Override
  protected SourceViewer createSourceViewer() {
    SourceViewer result = super.createSourceViewer();
    StyledTextArea textWidget = result.getTextWidget();
    textWidget.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    textWidget.contentProperty().addListener(new ChangeListener<StyledTextContent>() {
      @Override
      public void changed(ObservableValue<? extends StyledTextContent> observable,
          StyledTextContent oldValue, StyledTextContent newValue) {
        if (oldValue != null)
          oldValue.removeTextChangeListener(modifiedListener);
        if (newValue != null)
          newValue.addTextChangeListener(modifiedListener);
      }
    });
    return result;
  }

  private void onKeyPressed(KeyEvent event) {
    if (event.isConsumed()) {
      return;
    }
    if (new KeyCodeCombination(DIGIT7, SHORTCUT_DOWN).match(event)) {
      performCommenting();
      event.consume();
      return;
    }
    if (new KeyCodeCombination(S, SHORTCUT_DOWN).match(event)) {
      save();
      event.consume();
      return;
    }
  }

  private void performCommenting() {
    StyledTextArea control = getSourceViewer().getTextWidget();
    StyledTextContent content = control.getContent();
    int caret = control.getCaretOffset();
    TextSelection selection = control.getSelection();
    int firstLineIndex = control.getLineAtOffset(selection.offset);
    int lastLineIndex = control.getLineAtOffset(selection.offset + selection.length);
    int firstLineOffset = control.getOffsetAtLine(firstLineIndex);
    int lastLineOffset = control.getOffsetAtLine(lastLineIndex);
    int lastLineLength = content.getLine(lastLineIndex).length();
    int replaceLength = lastLineOffset + lastLineLength - firstLineOffset;

    String text = content.getTextRange(firstLineOffset, replaceLength);

    String prefix = "//";
    int prefixLength = prefix.length();

    boolean allLinesAreComments = true;
    for (int lineIndex = firstLineIndex; lineIndex <= lastLineIndex; lineIndex++) {
      String line = content.getLine(lineIndex);
      if (!line.trim().startsWith(prefix)) {
        allLinesAreComments = false;
        break;
      }
    }

    int added = 0;
    int addedPerLine = allLinesAreComments ? -prefixLength : prefixLength;

    StringBuilder sb = new StringBuilder(text);
    for (int lineIndex = firstLineIndex; lineIndex <= lastLineIndex; lineIndex++) {
      int lineOffset = control.getOffsetAtLine(lineIndex) + added;
      int sbLineOffset = lineOffset - firstLineOffset;
      if (allLinesAreComments) {
        String line = content.getLine(lineIndex);
        int prefixIndex = line.indexOf(prefix);
        assert prefixIndex >= 0;
        int sbPrefixIndex = sbLineOffset + prefixIndex;
        sb.replace(sbPrefixIndex, sbPrefixIndex + prefixLength, "");
      } else {
        sb.replace(sbLineOffset, sbLineOffset, prefix);
      }
      added += addedPerLine;
    }

    content.replaceTextRange(firstLineOffset, replaceLength, sb.toString());
    control.setCaretOffset(selection.offset == caret ? caret + addedPerLine : caret + added);
    control.setSelectionRange(selection.offset + addedPerLine,
        selection.length + added - addedPerLine);
  }

}
