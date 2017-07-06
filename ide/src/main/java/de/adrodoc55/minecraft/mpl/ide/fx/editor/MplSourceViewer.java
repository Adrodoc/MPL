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
import static javafx.scene.input.KeyCode.DIGIT7;
import static javafx.scene.input.KeyCode.F;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea;
import org.eclipse.fx.ui.controls.styledtext.StyledTextContent;
import org.eclipse.fx.ui.controls.styledtext.StyledTextContent.TextChangeListener;
import org.eclipse.fx.ui.controls.styledtext.TextChangedEvent;
import org.eclipse.fx.ui.controls.styledtext.TextChangingEvent;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;

import de.adrodoc55.minecraft.mpl.ide.fx.dialog.findreplace.FindReplaceController;
import de.adrodoc55.minecraft.mpl.ide.fx.dialog.findreplace.FindReplaceDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

/**
 * @author Adrodoc55
 */
public class MplSourceViewer extends SourceViewer {
  public interface Context {
    void setModified(boolean b);

    void save();

    FindReplaceDialog getFindReplaceDialog();
  }

  private final Context context;

  public MplSourceViewer(Context context) {
    this.context = checkNotNull(context, "context == null!");
  }

  @Override
  protected StyledTextArea createTextWidget() {
    StyledTextArea result = super.createTextWidget();
    result.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    TextChangeListener modifiedListener = new TextChangeListener() {
      @Override
      public void textChanged(TextChangedEvent event) {
        context.setModified(true);
      }

      @Override
      public void textSet(TextChangedEvent event) {
        context.setModified(true);
      }

      @Override
      public void textChanging(TextChangingEvent event) {
        context.setModified(true);
      }
    };
    result.contentProperty().addListener(new ChangeListener<StyledTextContent>() {
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
    if (new KeyCodeCombination(F, SHORTCUT_DOWN).match(event)) {
      findReplace();
      event.consume();
      return;
    }
  }

  private void performCommenting() {
    StyledTextArea control = getTextWidget();
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

  private void save() {
    context.save();
  }

  private void findReplace() {
    FindReplaceDialog dialog = context.getFindReplaceDialog();
    FindReplaceController controller = dialog.getController();
    controller.setFocusOwner(this);
    controller.extractSelectedText();
    dialog.show();
    dialog.requestFocus();
  }
}
