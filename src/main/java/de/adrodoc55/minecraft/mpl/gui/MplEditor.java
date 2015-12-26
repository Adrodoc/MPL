package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;

public class MplEditor extends JComponent {

    private static final Pattern INSERT_PATTERN = Pattern.compile("\\$\\{[^{}]*+\\}");

    private static final long serialVersionUID = 1L;

    private File file;
    private JScrollPane scrollPane;
    private JTextPane textPane;

    private Style lowFocusKeywordStyle;
    private Style impulseStyle;
    private Style chainStyle;
    private Style repeatStyle;
    // private Style conditionalStyle;
    private Style needsRedstoneStyle;
    private Style commentStyle;
    private Style skipStyle;
    private Style insertStyle;

    public MplEditor() {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }

    public MplEditor(File file) throws IOException {
        this();
        this.file = file;
        if (file != null) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes);
            getTextPane().setText(content);
            recolor();
        }
    }

    private void recolor() {
        String text = getTextPane().getText().replace("\r\n", "\n").replace("\r", "\n");
        MplLexer lexer = new MplLexer(new ANTLRInputStream(text));
        loop: while (true) {
            Token token = lexer.nextToken();
            switch (token.getType()) {
            case MplLexer.EOF:
                break loop;
            case MplLexer.IMPULSE:
                Style style = getImpulseStyle();
                styleToken(token, style);
                break;
            case MplLexer.CHAIN:
                styleToken(token, getChainStyle());
                break;
            case MplLexer.REPEAT:
                styleToken(token, getRepeatStyle());
                break;
            case MplLexer.UNCONDITIONAL:
                styleToken(token, getLowFocusKeywordStyle());
                break;
            case MplLexer.CONDITIONAL:
                styleToken(token, getLowFocusKeywordStyle());
                break;
            case MplLexer.ALWAYS_ACTIVE:
                styleToken(token, getLowFocusKeywordStyle());
                break;
            case MplLexer.NEEDS_REDSTONE:
                styleToken(token, getNeedsRedstoneStyle());
                break;
            case MplLexer.COMMENT:
                styleToken(token, getCommentStyle());
                break;
            case MplLexer.SKIP:
                styleToken(token, getSkipStyle());
                break;
            case MplLexer.COMMAND:
                styleToken(token, getDefaultStyle());
                Matcher insert = INSERT_PATTERN.matcher(token.getText());
                while (insert.find()) {
                    int tokenStart = token.getStartIndex();
                    int start = tokenStart + insert.start();
                    int stop = token.getStartIndex() + insert.end();
                    styleToken(start, stop, getInsertStyle());
                }
                break;

            default:
                continue;
            }

        }
    }

    private void styleToken(Token token, Style style) {
        styleToken(token.getStartIndex(), token.getStopIndex() + 1, style);
    }

    private void styleToken(int start, int stop, Style style) {
        int length = stop - start;
        getStyledDocument().setCharacterAttributes(start, length, style, true);
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getTextPane());
        }
        return scrollPane;
    }

    private JTextPane getTextPane() {
        if (textPane == null) {
            textPane = new JTextPane();
            textPane.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            recolor();
                        }
                    });
                }
            });
        }
        return textPane;
    }

    private StyledDocument getStyledDocument() {
        return getTextPane().getStyledDocument();
    }

    private Style getDefaultStyle() {
        Style style = getStyledDocument().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontSize(style, 12);
        return style;
    }

    private Style getLowFocusKeywordStyle() {
        if (lowFocusKeywordStyle == null) {
            lowFocusKeywordStyle = getStyledDocument().addStyle("conditional", getDefaultStyle());
            StyleConstants.setBold(lowFocusKeywordStyle, true);
            StyleConstants.setForeground(lowFocusKeywordStyle, new Color(128, 128, 128));
        }
        return lowFocusKeywordStyle;
    }

    private Style getImpulseStyle() {
        if (impulseStyle == null) {
            impulseStyle = getStyledDocument().addStyle("impulse", getDefaultStyle());
            StyleConstants.setBold(impulseStyle, true);
            StyleConstants.setForeground(impulseStyle, new Color(255, 127, 80));
        }
        return impulseStyle;
    }

    private Style getChainStyle() {
        if (chainStyle == null) {
            chainStyle = getStyledDocument().addStyle("chain", getDefaultStyle());
            StyleConstants.setBold(chainStyle, true);
            StyleConstants.setForeground(chainStyle, new Color(60, 179, 113));
        }
        return chainStyle;
    }

    private Style getRepeatStyle() {
        if (repeatStyle == null) {
            repeatStyle = getStyledDocument().addStyle("repeat", getDefaultStyle());
            StyleConstants.setBold(repeatStyle, true);
            StyleConstants.setForeground(repeatStyle, new Color(106, 90, 205));
        }
        return repeatStyle;
    }

    // private Style getConditionalStyle() {
    // if (conditionalStyle == null) {
    // conditionalStyle = getStyledDocument().addStyle("conditional",
    // getDefaultStyle());
    // StyleConstants.setBold(conditionalStyle, true);
    // StyleConstants.setForeground(conditionalStyle, new Color(169, 169, 169));
    // }
    // return conditionalStyle;
    // }

    private Style getNeedsRedstoneStyle() {
        if (needsRedstoneStyle == null) {
            needsRedstoneStyle = getStyledDocument().addStyle("needsRedstone", getDefaultStyle());
            StyleConstants.setBold(needsRedstoneStyle, true);
            StyleConstants.setForeground(needsRedstoneStyle, Color.RED);
        }
        return needsRedstoneStyle;
    }

    private Style getCommentStyle() {
        if (commentStyle == null) {
            commentStyle = getStyledDocument().addStyle("comment", getDefaultStyle());
            StyleConstants.setForeground(commentStyle, new Color(0, 128, 0));
        }
        return commentStyle;
    }

    private Style getSkipStyle() {
        if (skipStyle == null) {
            skipStyle = getStyledDocument().addStyle("skip", getDefaultStyle());
            // StyleConstants.setForeground(insertStyle, new Color(0, 0, 255));
            StyleConstants.setBackground(skipStyle, new Color(0, 255, 255));
        }
        return skipStyle;
    }

    private Style getInsertStyle() {
        if (insertStyle == null) {
            insertStyle = getStyledDocument().addStyle("insert", getDefaultStyle());
            StyleConstants.setForeground(insertStyle, new Color(128, 0, 0));
            StyleConstants.setBackground(insertStyle, new Color(240, 230, 140));
        }
        return insertStyle;
    }

}
