package de.adrodoc55.minecraft.mpl.ide.fx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.EditingContext;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletion;
import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

public class MplProposalComputer implements ProposalComputer {
  private final IDocument document;
  private final EditingContext editingContext;

  public MplProposalComputer(IDocument document, EditingContext editingContext) {
    this.document = checkNotNull(document, "document == null!");
    this.editingContext = checkNotNull(editingContext, "editingContext == null!");
  }

  @Override
  public CompletableFuture<List<CompletionProposal>> compute() {
    return CompletableFuture.supplyAsync(this::computeAsync);
  }

  private List<CompletionProposal> computeAsync() {
    List<CompletionProposal> result = new ArrayList<>();
    int lineOffset = getLineOffset();
    int caretOffset = editingContext.getCaretOffset();
    String text = document.get();
    List<AutoCompletionAction> options = AutoCompletion.getOptions(caretOffset, text);
    for (AutoCompletionAction action : options) {
      int indent = action.getStartIndex() - lineOffset;
      result.add(new MplCompletionProposal(indent, action));
    }
    return result;
  }

  private int getLineOffset() {
    try {
      IRegion line = document.getLineInformationOfOffset(editingContext.getCaretOffset());
      return line.getOffset();
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
