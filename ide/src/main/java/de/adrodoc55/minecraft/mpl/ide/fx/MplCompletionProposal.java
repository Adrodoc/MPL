package de.adrodoc55.minecraft.mpl.ide.fx;

import java.lang.reflect.UndeclaredThrowableException;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.fx.code.editor.services.ContextInformation.BaseContextInformation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

public class MplCompletionProposal implements CompletionProposal {
  private final CharSequence label;
  private final int replacementOffset;
  private final int replacementLength;
  private final String replacementString;
  private final int cursorPositionInTemplate;
  private final ContextInformation contextInformation;

  public MplCompletionProposal(int indent, AutoCompletionAction proposal) {
    label = proposal.getDisplayName();
    replacementString = proposal.processTemplate(indent);
    replacementOffset = proposal.getStartIndex();
    replacementLength = proposal.getLength();
    cursorPositionInTemplate = proposal.getCursorIndex(indent);
    CharSequence hoverInfo = proposal.getDescription();
    contextInformation = new BaseContextInformation(replacementOffset, hoverInfo);
  }

  @Override
  public CharSequence getLabel() {
    return label;
  }

  public int getReplacementOffset() {
    return replacementOffset;
  }

  public int getReplacementLength() {
    return replacementLength;
  }

  public String getReplacementString() {
    return replacementString;
  }

  public int getCursorPosition() {
    return getReplacementOffset() + cursorPositionInTemplate;
  }

  @Override
  public void apply(IDocument document) {
    try {
      document.replace(replacementOffset, replacementLength, replacementString);
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  @Override
  public TextSelection getSelection(IDocument document) {
    return new TextSelection(getCursorPosition(), 0);
  }

  @Override
  public ContextInformation getContextInformation() {
    return contextInformation;
  }
}
