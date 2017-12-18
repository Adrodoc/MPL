package de.adrodoc55.minecraft.mpl.ide.fx.richtext;

import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
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
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;

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

  private int tabWidth = 2;

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
    plainTextChanges().conditionOnShowing(this)//
        .supplyTask(() -> computeHighlightingAsync(getText()))//
        .awaitLatest(plainTextChanges())//
        .subscribe(t -> applyHighlighting(t.get()))//
    ;
    scrollToPixel(0, 0);
    addInputMap(this,
        sequence(//
            consume(keyPressed(TAB), this::tabPressed), //
            consume(keyPressed(TAB, SHIFT_DOWN), this::shiftTabPressed)//
        ));
  }

  private void tabPressed(KeyEvent e) {
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
    int caretLine = getCurrentParagraph();
    int anchorLine = getAnchorParagraph();
    int minLine = Math.min(caretLine, anchorLine);
    int maxLine = Math.max(caretLine, anchorLine);
    String spaces = Strings.repeat(" ", tabWidth);
    for (int line = minLine; line <= maxLine; line++) {
      String text = getText(line);
      if (!(text.startsWith(spaces) || text.trim().isEmpty())) {
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

  public int getAnchorParagraph() {
    return offsetToPosition(getAnchor(), Bias.Backward).getMajor();
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

  public int getTabWidth() {
    return tabWidth;
  }

  public void setTabWidth(int tabWidth) {
    this.tabWidth = tabWidth;
  }
}
