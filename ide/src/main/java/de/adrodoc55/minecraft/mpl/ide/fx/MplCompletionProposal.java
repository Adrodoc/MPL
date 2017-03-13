package de.adrodoc55.minecraft.mpl.ide.fx;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.text.ui.contentassist.ICompletionProposal;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class MplCompletionProposal implements ICompletionProposal {
  private CompletionProposal proposal;

  public MplCompletionProposal(CompletionProposal proposal) {
    this.proposal = proposal;
  }

  @Override
  public CharSequence getLabel() {
    return this.proposal.getLabel();
  }

  @Override
  public Node getGraphic() {
    return new Label();
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
}
