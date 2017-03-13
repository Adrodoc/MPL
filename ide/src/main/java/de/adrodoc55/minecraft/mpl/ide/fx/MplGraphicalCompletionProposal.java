package de.adrodoc55.minecraft.mpl.ide.fx;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.fx.text.ui.contentassist.ICompletionProposal;
import org.eclipse.fx.text.ui.contentassist.IContextInformation;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;

import javafx.scene.Node;

public class MplGraphicalCompletionProposal implements ICompletionProposal {
  private CompletionProposal proposal;

  public MplGraphicalCompletionProposal(CompletionProposal proposal) {
    this.proposal = proposal;
  }

  @Override
  public CharSequence getLabel() {
    return proposal.getLabel();
  }

  @Override
  public CharSequence getHoverInfo() {
    return proposal.getContextInformation().getText();
  }

  @Override
  public Node getGraphic() {
    return null; // TODO: Find a nice Icon for the different Completion Proposals
  }

  @Override
  public void apply(IDocument document) {
    this.proposal.apply(document);
  }

  @Override
  public TextSelection getSelection(IDocument document) {
    CompletionProposal.TextSelection selection = proposal.getSelection(document);
    return selection == null ? TextSelection.EMPTY
        : new TextSelection(selection.offset, selection.length);
  }

  @Override
  public IContextInformation getContextInformation() {
    ContextInformation contextInformation = proposal.getContextInformation();
    int contextInformationPosition = contextInformation.getOffset();
    // TODO: Seems weird to show Text after the Autocompletion has been selected.
    CharSequence informationDisplayString = null;// proposal.getLabel();
    CharSequence contextDisplayString = contextInformation.getText();
    return new GraphicalContextInformation(contextInformationPosition, informationDisplayString,
        contextDisplayString);
  }
}
