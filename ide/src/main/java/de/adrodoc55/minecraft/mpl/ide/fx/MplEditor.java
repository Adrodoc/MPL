package de.adrodoc55.minecraft.mpl.ide.fx;

import static javafx.scene.input.KeyCode.DIGIT7;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea;
import org.eclipse.fx.ui.controls.styledtext.StyledTextContent;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

public class MplEditor extends TextEditor {
  public MplEditor() {
    setInsertSpacesForTab(true);
    setTabAdvance(2);
  }

  @Override
  protected SourceViewer createSourceViewer() {
    SourceViewer result = super.createSourceViewer();
    StyledTextArea textWidget = result.getTextWidget();
    textWidget.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    return result;
  }

  private void onKeyPressed(KeyEvent event) {
    if (event.isConsumed()) {
      return;
    }
    if (new KeyCodeCombination(DIGIT7, SHORTCUT_DOWN).match(event)) {
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
}
