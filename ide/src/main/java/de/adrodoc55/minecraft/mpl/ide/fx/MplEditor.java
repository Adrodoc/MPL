package de.adrodoc55.minecraft.mpl.ide.fx;

import org.eclipse.fx.code.editor.StringInput;
import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.code.editor.fx.services.CompletionProposalPresenter;
import org.eclipse.fx.code.editor.fx.services.internal.DefaultSourceViewerConfiguration;
import org.eclipse.fx.code.editor.services.HoverInformationProvider;
import org.eclipse.fx.code.editor.services.InputDocument;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.text.ui.source.AnnotationPresenter;
import org.eclipse.jface.text.source.IAnnotationModel;

import de.adrodoc55.minecraft.mpl.MplPartitioner;
import de.adrodoc55.minecraft.mpl.MplPresentationReconciler;

public class MplEditor extends TextEditor {
  public MplEditor(StringInput input, EventBus eventBus) {
    setInput(input);
    setDocument(new InputDocument(input, eventBus));
    setPartitioner(new MplPartitioner());
    DefaultSourceViewerConfiguration configuration = createConfiguration(input);
    setSourceViewerConfiguration(configuration);
  }

  private DefaultSourceViewerConfiguration createConfiguration(StringInput input) {
    MplPresentationReconciler reconciler = new MplPresentationReconciler();
    ProposalComputer proposalComputer = new MplProposalComputer();
    IAnnotationModel annotationModel = null;
    AnnotationPresenter presenter = null;
    HoverInformationProvider hoverInformationProvider = null;
    CompletionProposalPresenter proposalPresenter = MplCompletionProposal::new;
    return new DefaultSourceViewerConfiguration(input, reconciler, proposalComputer,
        annotationModel, presenter, hoverInformationProvider, proposalPresenter);
  }
}
