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
package de.adrodoc55.minecraft.mpl.ide.fx.richtext;

import static com.google.common.primitives.Ints.constrainToRange;
import static javafx.scene.input.KeyCode.DIGIT7;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;
import static org.fxmisc.wellbehaved.event.InputMap.sequence;
import static org.fxmisc.wellbehaved.event.Nodes.addInputMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.TextOps;

import com.google.common.base.Strings;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;

/**
 * @author Adrodoc55
 */
public class MplEditor2 extends CodeArea {
  private static @Nullable ExecutorService syntaxHighlightingThread;

  public static ExecutorService getSyntaxHighlightingThread() {
    if (syntaxHighlightingThread == null) {
      syntaxHighlightingThread = Executors.newSingleThreadExecutor(r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
      });
    }
    return syntaxHighlightingThread;
  }

  public MplEditor2() {}

  public MplEditor2(
      EditableStyledDocument<Collection<String>, String, Collection<String>> document) {
    super(document);
  }

  public MplEditor2(String text) {
    super(text);
    try {
      StyleSpans<Collection<String>> highlighting = computeHighlighting(getText(), () -> false);
      applyHighlighting(highlighting);
    } catch (InterruptedException ex) {
      throw new AssertionError(ex);
    }
  }

  {
    plainTextChanges().subscribe(c -> setModified(true));
    plainTextChanges().conditionOnShowing(this)//
        .supplyTask(() -> computeHighlightingAsync(getText()))//
        .awaitLatest(plainTextChanges())//
        .subscribe(t -> applyHighlighting(t.get()))//
    ;
    scrollToPixel(0, 0);
    addInputMap(this,
        sequence(//
            consume(keyPressed(TAB), this::tabPressed), //
            consume(keyPressed(TAB, SHIFT_DOWN), this::shiftTabPressed), //
            consume(keyPressed(DIGIT7, SHORTCUT_DOWN), this::performCommenting)//
        ));
  }

  private void tabPressed(KeyEvent e) {
    int tabWidth = getTabWidth();
    int caretLine = getCurrentParagraph();
    int anchorLine = getAnchorParagraph();
    if (caretLine == anchorLine) {
      int spaceCount = tabWidth - getCaretColumn() % tabWidth;
      String spaces = Strings.repeat(" ", spaceCount);
      replaceSelection(spaces);
    } else {
      int initialAnchor = getAnchor();
      int initialCaret = getCaretPosition();

      int minLine = Math.min(caretLine, anchorLine);
      int maxLine = Math.max(caretLine, anchorLine);
      String text = getText(minLine, 0, maxLine, 0);
      String spaces = Strings.repeat(" ", tabWidth);
      String replacement = spaces + text.replace("\n", '\n' + spaces);

      replaceText(minLine, 0, maxLine, 0, replacement);

      int textLengthDelta = replacement.length() - text.length();
      if (initialAnchor < initialCaret) {
        selectRange(initialAnchor + tabWidth, initialCaret + textLengthDelta);
      } else {
        selectRange(initialAnchor + textLengthDelta, initialCaret + tabWidth);
      }
    }
  }

  private void shiftTabPressed(KeyEvent e) {
    int tabWidth = getTabWidth();
    int caretLine = getCurrentParagraph();
    int anchorLine = getAnchorParagraph();
    int minLine = Math.min(caretLine, anchorLine);
    int maxLine = Math.max(caretLine, anchorLine);
    String spaces = Strings.repeat(" ", tabWidth);
    for (int lineIndex = minLine; lineIndex <= maxLine; lineIndex++) {
      String line = getText(lineIndex);
      if (!(line.startsWith(spaces) || line.trim().isEmpty())) {
        return;
      }
    }
    int initialAnchor = getAnchor();
    int initialCaret = getCaretPosition();

    String text = getText(minLine, 0, maxLine, tabWidth);
    String replacement = text.replace('\n' + spaces, "\n").substring(tabWidth);

    replaceText(minLine, 0, maxLine, tabWidth, replacement);

    int textLengthDelta = replacement.length() - text.length();
    if (initialAnchor < initialCaret) {
      selectRange(initialAnchor - tabWidth, initialCaret + textLengthDelta);
    } else {
      selectRange(initialAnchor + textLengthDelta, initialCaret - tabWidth);
    }
  }

  private static final String COMMENT_PREFIX = "//";

  private void performCommenting(KeyEvent e) {
    int anchorLineIndex = getAnchorParagraph();
    int caretLineIndex = getCurrentParagraph();
    int minLineIndex = Math.min(anchorLineIndex, caretLineIndex);
    int maxLineIndex = Math.max(anchorLineIndex, caretLineIndex);

    boolean allLinesAreComments = true;
    for (int lineIndex = minLineIndex; lineIndex <= maxLineIndex; lineIndex++) {
      String line = getText(lineIndex);
      if (!line.trim().startsWith(COMMENT_PREFIX)) {
        allLinesAreComments = false;
        break;
      }
    }

    if (allLinesAreComments) {
      uncommentSelectedLines();
    } else {
      commentSelectedLines();
    }
  }

  private void uncommentSelectedLines() {
    int anchorPos = getAnchor();
    int caretPos = getCaretPosition();
    int minSelection = Math.min(anchorPos, caretPos);
    int maxSelection = Math.max(anchorPos, caretPos);
    int anchorLineIndex = getParagraphIndex(anchorPos);
    int caretLineIndex = getCurrentParagraph();
    int minLineIndex = Math.min(anchorLineIndex, caretLineIndex);
    int maxLineIndex = Math.max(anchorLineIndex, caretLineIndex);
    int lineCount = maxLineIndex - minLineIndex + 1;
    String minLine = getText(minLineIndex);
    String maxLine = getText(maxLineIndex);
    int prefixLength = COMMENT_PREFIX.length();

    // Calculate new selection
    int firstPrefix = getAbsolutePosition(minLineIndex, minLine.indexOf(COMMENT_PREFIX));
    int lastPrefix = getAbsolutePosition(maxLineIndex, maxLine.indexOf(COMMENT_PREFIX));
    int minDelta = constrainToRange(minSelection - firstPrefix, 0, prefixLength);
    int maxDelta = (lineCount - 1) * prefixLength
        + constrainToRange(maxSelection - lastPrefix, 0, prefixLength);

    // Remove the first comment prefix in each selected line
    StringBuilder sb = new StringBuilder();
    for (int lineIndex = minLineIndex; lineIndex <= maxLineIndex; lineIndex++) {
      String line = getText(lineIndex);
      int prefixIndex = line.indexOf(COMMENT_PREFIX);
      assert prefixIndex >= 0;
      sb.append(line.substring(0, prefixIndex));
      sb.append(line.substring(prefixIndex + prefixLength));
      if (lineIndex != maxLineIndex) {
        sb.append('\n');
      }
    }
    String replacement = sb.toString();
    replaceText(minLineIndex, 0, maxLineIndex, getParagraphLength(maxLineIndex), replacement);

    // Update selection
    if (anchorPos < caretPos) {
      selectRange(anchorPos - minDelta, caretPos - maxDelta);
    } else {
      selectRange(anchorPos - maxDelta, caretPos - minDelta);
    }
  }

  private void commentSelectedLines() {
    int anchorPos = getAnchor();
    int caretPos = getCaretPosition();
    int anchorLineIndex = getParagraphIndex(anchorPos);
    int caretLineIndex = getCurrentParagraph();
    int minLineIndex = Math.min(anchorLineIndex, caretLineIndex);
    int maxLineIndex = Math.max(anchorLineIndex, caretLineIndex);
    int lineCount = maxLineIndex - minLineIndex + 1;
    int prefixLength = COMMENT_PREFIX.length();

    // Calculate new selection
    int minDelta = prefixLength;
    int maxDelta = lineCount * prefixLength;

    // Add a comment prefix at the start of each selected line
    StringBuilder sb = new StringBuilder();
    for (int lineIndex = minLineIndex; lineIndex <= maxLineIndex; lineIndex++) {
      String line = getText(lineIndex);
      sb.append(COMMENT_PREFIX).append(line);
      if (lineIndex != maxLineIndex) {
        sb.append('\n');
      }
    }
    String replacement = sb.toString();
    replaceText(minLineIndex, 0, maxLineIndex, getParagraphLength(maxLineIndex), replacement);

    // Update selection
    if (anchorPos < caretPos) {
      selectRange(anchorPos + minDelta, caretPos + maxDelta);
    } else {
      selectRange(anchorPos + maxDelta, caretPos + minDelta);
    }
  }

  public int getAnchorParagraph() {
    return getParagraphIndex(getAnchor());
  }

  public int getParagraphIndex(int charOffset) {
    return offsetToPosition(charOffset).getMajor();
  }

  public Position offsetToPosition(int charOffset) {
    return offsetToPosition(charOffset, Bias.Backward);
  }

  public void insertContent(int position, String text) {
    replaceContent(position, position, text);
  }

  public void deleteContent(int start, int end) {
    replaceContent(start, end, "");
  }

  public void replaceContent(int start, int end, String text) {
    Collection<String> paragraphStyle = getParagraphStyleForInsertionAt(start);
    Collection<String> style = getTextStyleForInsertionAt(start);
    TextOps<String, Collection<String>> segmentOps =
        (TextOps<String, Collection<String>>) getSegOps();
    StyledDocument<Collection<String>, String, Collection<String>> replacement =
        ReadOnlyStyledDocument.fromString(text, paragraphStyle, style, segmentOps);
    getContent().replace(start, end, replacement);
  }

  private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(String text) {
    Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
      @Override
      protected StyleSpans<Collection<String>> call() throws Exception {
        return computeHighlighting(text, this::isCancelled);
      }

    };
    getSyntaxHighlightingThread().execute(task);
    return task;
  }

  private StyleSpans<Collection<String>> computeHighlighting(String text,
      BooleanSupplier isCancelled) throws InterruptedException {
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    spansBuilder.add(Collections.emptyList(), 0);

    ANTLRInputStream input = new ANTLRInputStream(text);
    MplLexer lexer = new MplLexer(input);
    int lastTokenEnd = 0;
    for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
      if (isCancelled.getAsBoolean()) {
        throw new InterruptedException();
      }

      String styleClass = null;
      switch (token.getType()) {
        case MplLexer.COLON:
        case MplLexer.COMMA:
        case MplLexer.EQUALS_SIGN:
        case MplLexer.PLUS:
        case MplLexer.MINUS:
          styleClass = "punctuation";
          break;
        case MplLexer.OPENING_BRACKET:
        case MplLexer.CLOSING_BRACKET:
        case MplLexer.OPENING_CURLY_BRACKET:
        case MplLexer.CLOSING_CURLY_BRACKET:
          styleClass = "bracket";
          break;
        case MplLexer.TAG:
          styleClass = "tag";
          break;
        case MplLexer.BREAKPOINT:
        case MplLexer.CONDITIONAL:
        case MplLexer.ELSE:
        case MplLexer.IF:
        case MplLexer.IMPORT:
        case MplLexer.INCLUDE:
        case MplLexer.INLINE:
        case MplLexer.INSTALL:
        case MplLexer.INTERCEPT:
        case MplLexer.INVERT:
        case MplLexer.NOT:
        case MplLexer.NOTIFY:
        case MplLexer.ORIENTATION:
        case MplLexer.PROCESS:
        case MplLexer.PROJECT:
        case MplLexer.REMOTE:
        case MplLexer.SKIP_TOKEN:
        case MplLexer.START:
        case MplLexer.STOP:
        case MplLexer.THEN:
        case MplLexer.UNINSTALL:
        case MplLexer.WAITFOR:
          styleClass = "keyword";
          break;
        case MplLexer.IMPULSE:
          styleClass = "impulse";
          break;
        case MplLexer.CHAIN:
          styleClass = "chain";
          break;
        case MplLexer.BREAK:
        case MplLexer.CONTINUE:
        case MplLexer.DO:
        case MplLexer.REPEAT:
        case MplLexer.WHILE:
          styleClass = "repeat";
          break;
        case MplLexer.ALWAYS_ACTIVE:
          styleClass = "always-active";
          break;
        case MplLexer.NEEDS_REDSTONE:
          styleClass = "needs-redstone";
          break;
        case MplLexer.UNCONDITIONAL:
          styleClass = "unconditional";
          break;
        case MplLexer.SLASH:
        case MplLexer.COMMAND_STRING:
          styleClass = "command";
          break;
        case MplLexer.DOLLAR:
        case MplLexer.INSERT_CLOSING_BRACKET:
        case MplLexer.INSERT_CLOSING_CURLY_BRACKET:
        case MplLexer.INSERT_DOT:
        case MplLexer.INSERT_IDENTIFIER:
        case MplLexer.INSERT_MINUS:
        case MplLexer.INSERT_OPENING_BRACKET:
        case MplLexer.INSERT_OPENING_CURLY_BRACKET:
        case MplLexer.INSERT_ORIGIN:
        case MplLexer.INSERT_PLUS:
        case MplLexer.INSERT_THIS:
        case MplLexer.INSERT_UNSIGNED_INTEGER:
        case MplLexer.INSERT_WS:
          styleClass = "insert";
          break;
        case MplLexer.IDENTIFIER:
          styleClass = "identifier";
          break;
        case MplLexer.SELECTOR:
          styleClass = "selector";
          break;
        case MplLexer.STRING:
          styleClass = "string";
          break;
        case MplLexer.TYPE:
          styleClass = "type";
          break;
        case MplLexer.UNRECOGNIZED:
          styleClass = "unrecognized";
          break;
        case MplLexer.UNSIGNED_INTEGER:
          styleClass = "number";
          break;
        case MplLexer.COMMENT:
        case MplLexer.MULTILINE_COMMENT:
          styleClass = "comment";
      }
      int start = token.getStartIndex();
      int end = token.getStopIndex() + 1;
      spansBuilder.add(Collections.emptyList(), start - lastTokenEnd);
      spansBuilder.add(Arrays.asList("mpl", styleClass), end - start);
      lastTokenEnd = end;
    }
    return spansBuilder.create();
  }

  private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
    setStyleSpans(0, highlighting);
  }

  private final BooleanProperty modified = new SimpleBooleanProperty();

  public ReadOnlyBooleanProperty modifiedProperty() {
    return modified;
  }

  public boolean isModified() {
    return modifiedProperty().get();
  }

  private void setModified(boolean modified) {
    this.modified.set(modified);
  }

  private final IntegerProperty tabWidth = new SimpleIntegerProperty(2);

  public IntegerProperty tabWidthProperty() {
    return tabWidth;
  }

  public int getTabWidth() {
    return tabWidthProperty().get();
  }

  public void setTabWidth(int tabWidth) {
    tabWidthProperty().set(tabWidth);
  }
}
