/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.gui;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.View;

import de.adrodoc55.minecraft.mpl.antlr.MplLexer;
import de.adrodoc55.minecraft.mpl.gui.MplSyntaxFilterPM.CompilerExceptionWrapper;
import de.adrodoc55.minecraft.mpl.gui.utils.TabToSpaceDocumentFilter;

/**
 * The MplSyntaxFilter is a {@link View} on a
 * {@link de.adrodoc55.minecraft.mpl.gui.MplSyntaxFilterPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplSyntaxFilter extends TabToSpaceDocumentFilter implements View<MplSyntaxFilterPM> {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;

  /**
   * Constructs a new <code>MplSyntaxFilter</code>.
   */
  public MplSyntaxFilter() {
    super();
  }

  public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
    super.remove(fb, offset, length);
    correctExceptionIndicies(offset, -length);
    Document document = fb.getDocument();
    if (document instanceof StyledDocument) {
      recolor((StyledDocument) document);
    }
  }

  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
      throws BadLocationException {
    super.insertString(fb, offset, string, attr);
    correctExceptionIndicies(offset, string.length());
    Document document = fb.getDocument();
    if (document instanceof StyledDocument) {
      recolor((StyledDocument) document);
    }
  }

  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
      throws BadLocationException {
    super.replace(fb, offset, length, text, attrs);
    correctExceptionIndicies(offset, text.length() - length);
    Document document = fb.getDocument();
    if (document instanceof StyledDocument) {
      recolor((StyledDocument) document);
    }
  }

  private static final Pattern INSERT_PATTERN = Pattern.compile("\\$\\{[^{}]*+\\}");

  private StyledDocument doc;

  private void recolor(StyledDocument doc) {
    this.doc = doc;
    recolor();
  }

  public void recolor() {
    if (doc == null) {
      return;
    }
    String text;
    try {
      // text = FileUtils.toUnixLineEnding(doc.getText(0, doc.getLength()));
      text = doc.getText(0, doc.getLength());
    } catch (BadLocationException ex) {
      throw new RuntimeException(
          "Encountered unexpected BadLocationException in MplSyntaxHighlighter", ex);
    }
    resetStyling();
    colorTokens(text);
    colorExceptions();
  }

  private void resetStyling() {
    doc.setCharacterAttributes(0, doc.getLength(), getDefaultStyle(), true);
  }

  private void correctExceptionIndicies(int startIndex, int offset) {
    MplSyntaxFilterPM pModel = getPresentationModel();
    if (pModel == null) {
      return;
    }
    List<CompilerExceptionWrapper> exceptions = pModel.getExceptions();
    if (exceptions == null) {
      return;
    }
    for (CompilerExceptionWrapper ex : exceptions) {
      if (ex.getStartIndex() >= startIndex) {
        ex.addStartOffset(offset);
      }
      if (ex.getStopIndex() >= startIndex) {
        ex.addStopOffset(offset);
      }
    }
  }

  private void colorExceptions() {
    MplSyntaxFilterPM pModel = getPresentationModel();
    if (pModel == null) {
      return;
    }
    List<CompilerExceptionWrapper> exceptions = pModel.getExceptions();
    if (exceptions == null) {
      return;
    }
    for (CompilerExceptionWrapper ex : exceptions) {
      styleToken(ex.getStartIndex(), ex.getStopIndex(), getErrorAttributes(), false);
    }
  }

  public void colorTokens(String text) {
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
        case MplLexer.ALWAYS_ACTIVE:
          styleToken(token, getLowFocusKeywordStyle());
          break;
        case MplLexer.NEEDS_REDSTONE:
          styleToken(token, getNeedsRedstoneStyle());
          break;
        case MplLexer.COMMENT:
          styleToken(token, getCommentStyle());
          break;
        case MplLexer.INCLUDE:
        case MplLexer.IMPORT:
        case MplLexer.INSTALL:
        case MplLexer.UNINSTALL:
        case MplLexer.PROJECT:
        case MplLexer.PROCESS:
        case MplLexer.CONDITIONAL:
        case MplLexer.INVERT:
        case MplLexer.START:
        case MplLexer.STOP:
        case MplLexer.WAITFOR:
        case MplLexer.NOTIFY:
        case MplLexer.INTERCEPT:
        case MplLexer.SKIP:
        case MplLexer.IF:
        case MplLexer.NOT:
        case MplLexer.THEN:
        case MplLexer.ELSE:
        case MplLexer.END:
          styleToken(token, getHighFocusKeywordStyle());
          break;
        case MplLexer.IDENTIFIER:
          styleToken(token, getIdentifierStyle());
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
        case MplLexer.UNRECOGNIZED:
          styleToken(token, getErrorAttributes());
          break;
        default:
          styleToken(token, getDefaultStyle());
      }
    }
  }

  private void styleToken(Token token, AttributeSet style) {
    styleToken(token, style, true);
  }

  private void styleToken(Token token, AttributeSet style, boolean replace) {
    styleToken(token.getStartIndex(), token.getStopIndex() + 1, style, replace);
  }

  private void styleToken(int start, int stop, AttributeSet style) {
    styleToken(start, stop, style, true);
  }

  private void styleToken(int start, int stop, AttributeSet style, boolean replace) {
    int length = stop - start;
    doc.setCharacterAttributes(start, length, style, replace);
  }

  private Style getDefaultStyle() {
    return doc.getStyle(StyleContext.DEFAULT_STYLE);
  }

  private Style getLowFocusKeywordStyle() {
    Style lowFocusKeywordStyle = doc.getStyle("lowFocusKeyword");
    if (lowFocusKeywordStyle == null) {
      lowFocusKeywordStyle = doc.addStyle("lowFocusKeyword", getDefaultStyle());
      StyleConstants.setBold(lowFocusKeywordStyle, true);
      StyleConstants.setForeground(lowFocusKeywordStyle, new Color(128, 128, 128));
    }
    return lowFocusKeywordStyle;
  }

  private Style getHighFocusKeywordStyle() {
    Style highFocusKeywordStyle = doc.getStyle("highFocusKeyword");
    if (highFocusKeywordStyle == null) {
      highFocusKeywordStyle = doc.addStyle("highFocusKeyword", getDefaultStyle());
      StyleConstants.setBold(highFocusKeywordStyle, true);
      StyleConstants.setForeground(highFocusKeywordStyle, new Color(128, 0, 0));
    }
    return highFocusKeywordStyle;
  }

  private Style getImpulseStyle() {
    Style impulseStyle = doc.getStyle("impulse");
    if (impulseStyle == null) {
      impulseStyle = doc.addStyle("impulse", getDefaultStyle());
      StyleConstants.setBold(impulseStyle, true);
      StyleConstants.setForeground(impulseStyle, new Color(255, 127, 80));
    }
    return impulseStyle;
  }

  private Style getChainStyle() {
    Style chainStyle = doc.getStyle("chain");
    if (chainStyle == null) {
      chainStyle = doc.addStyle("chain", getDefaultStyle());
      StyleConstants.setBold(chainStyle, true);
      StyleConstants.setForeground(chainStyle, new Color(60, 179, 113));
    }
    return chainStyle;
  }

  private Style getRepeatStyle() {
    Style repeatStyle = doc.getStyle("repeat");
    if (repeatStyle == null) {
      repeatStyle = doc.addStyle("repeat", getDefaultStyle());
      StyleConstants.setBold(repeatStyle, true);
      StyleConstants.setForeground(repeatStyle, new Color(106, 90, 205));
    }
    return repeatStyle;
  }

  private Style getNeedsRedstoneStyle() {
    Style needsRedstoneStyle = doc.getStyle("needsRedstone");
    if (needsRedstoneStyle == null) {
      needsRedstoneStyle = doc.addStyle("needsRedstone", getDefaultStyle());
      StyleConstants.setBold(needsRedstoneStyle, true);
      StyleConstants.setForeground(needsRedstoneStyle, Color.RED);
    }
    return needsRedstoneStyle;
  }

  private Style getCommentStyle() {
    Style commentStyle = doc.getStyle("comment");
    if (commentStyle == null) {
      commentStyle = doc.addStyle("comment", getDefaultStyle());
      StyleConstants.setForeground(commentStyle, new Color(0, 128, 0));
    }
    return commentStyle;
  }

  private Style getInsertStyle() {
    Style insertStyle = doc.getStyle("insert");
    if (insertStyle == null) {
      insertStyle = doc.addStyle("insert", getDefaultStyle());
      StyleConstants.setForeground(insertStyle, new Color(128, 0, 0));
      StyleConstants.setBackground(insertStyle, new Color(240, 230, 140));
    }
    return insertStyle;
  }

  private Style getIdentifierStyle() {
    Style identifierStyle = doc.getStyle("identifier");
    if (identifierStyle == null) {
      identifierStyle = doc.addStyle("identifier", getDefaultStyle());
      StyleConstants.setBold(identifierStyle, true);
      StyleConstants.setForeground(identifierStyle, new Color(128, 128, 0));
    }
    return identifierStyle;
  }

  private SimpleAttributeSet getErrorAttributes() {
    SimpleAttributeSet errorAttributes = new SimpleAttributeSet();
    StyleConstants.setUnderline(errorAttributes, true);
    return errorAttributes;
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=10,430
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider(); // @wb:location=10,430
      localModelProvider
          .setPresentationModelType(de.adrodoc55.minecraft.mpl.gui.MplSyntaxFilterPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public de.adrodoc55.minecraft.mpl.gui.MplSyntaxFilterPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(de.adrodoc55.minecraft.mpl.gui.MplSyntaxFilterPM pModel) {
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  public void setPath(Path path) {
    this.link.setPath(path);
  }

}
