package de.adrodoc55.minecraft.mpl.ide.fx;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.CompletionProposal.BaseCompletetionProposal;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletion;
import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

public class MplProposalComputer implements ProposalComputer {
  @Override
  public CompletableFuture<List<CompletionProposal>> compute(ProposalContext context) {
    List<CompletionProposal> proposals = new ArrayList<>();

    int lineOffset = getLineOffset(context);
    List<AutoCompletionAction> options =
        AutoCompletion.getOptions(context.location, context.document.get());
    for (AutoCompletionAction action : options) {
      int offsetInLine = action.getStartIndex() - lineOffset;
      proposals.add(toProposal(offsetInLine, action));
    }

    CompletableFuture<List<CompletionProposal>> result = new CompletableFuture<>();
    result.complete(proposals);
    return result;
  }

  private int getLineOffset(ProposalContext context) {
    try {
      IRegion line = context.document.getLineInformationOfOffset(context.location);
      return line.getOffset();
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  private BaseCompletetionProposal toProposal(int indent, AutoCompletionAction action) {
    String replacementString = action.processTemplate(indent);
    return new BaseCompletetionProposal(replacementString, action.getStartIndex(),
        action.getLength(), action.getDisplayName());
  }
}
