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
package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.awt.Toolkit;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.Options;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;
import org.beanfabrics.util.Interval;
import org.beanfabrics.validation.ValidationState;

import de.adrodoc55.commons.DocumentUtils;
import de.adrodoc55.commons.RegexUtils;
import de.adrodoc55.commons.StringUnescaper;
import de.adrodoc55.commons.beanfabrics.OptionsUtils;

public class SearchAndReplaceDialogPM extends AbstractPM {

  public static interface Context {
    JTextComponent getComponent();
  }

  private final Context context;

  TextPM search = new TextPM();
  Options<String> searchOptions = new Options<String>();
  TextPM replaceWith = new TextPM();
  Options<String> replaceWithOptions = new Options<String>();

  BooleanPM caseSensitive = new BooleanPM();
  BooleanPM wholeWord = new BooleanPM();
  BooleanPM regex = new BooleanPM();
  BooleanPM wrapSearch = new BooleanPM();
  BooleanPM incremental = new BooleanPM();
  BooleanPM extended = new BooleanPM();

  OperationPM find = new OperationPM();
  OperationPM replace = new OperationPM();
  OperationPM replaceFind = new OperationPM();
  OperationPM replaceAll = new OperationPM();

  public SearchAndReplaceDialogPM(Context context) {
    this.context = context;
    searchOptions.put("", "");
    search.setOptions(searchOptions);
    search.setRestrictedToOptions(false);
    replaceWithOptions.put("", "");
    replaceWith.setOptions(replaceWithOptions);
    replaceWith.setRestrictedToOptions(false);
    wrapSearch.setBoolean(true);
    incremental.setBoolean(true);
    search.addPropertyChangeListener("text", e -> {
      if (incremental.getBoolean()) {
        if (canFind() == null) {
          findIncremental();
        }
      }
    });
    regex.addPropertyChangeListener(e -> {
      if (regex.getBoolean()) {
        wholeWord.setEditable(false);
      } else {
        wholeWord.setEditable(true);
      }
    });
    PMManager.setup(this);
  }

  public JTextComponent getComponent() {
    return context.getComponent();
  }

  public void setSearch(String search) {
    if (regex.getBoolean()) {
      String escaped = RegexUtils.escape(search);
      this.search.setText(escaped);
    } else {
      this.search.setText(search);
    }
  }

  private String getReplaceWithText() {
    String text = replaceWith.getText();
    if (extended.getBoolean()) {
      return StringUnescaper.unescape_perl_string(text);
    } else {
      return text;
    }
  }

  public boolean isWholeWord() {
    return wholeWord.isEditable() && wholeWord.getBoolean();
  }

  public void findIncremental() {
    find.check();
    JTextComponent component = context.getComponent();
    Caret caret = component.getCaret();
    int startIndex = Math.min(caret.getDot(), caret.getMark());
    Document doc = component.getDocument();
    int length = doc.getLength() - startIndex;
    Interval result = search(startIndex, length);
    if (result == null && wrapSearch.getBoolean()) {
      result = search(0, doc.getLength());
    }
    if (result == null) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    caret.setDot(result.startIndex);
    caret.moveDot(result.endIndex);
    caret.setSelectionVisible(true);
  }

  @Operation
  public void find() {
    find.check();
    JTextComponent component = context.getComponent();
    Caret caret = component.getCaret();
    int startIndex = caret.getDot();
    Document doc = component.getDocument();
    int length = doc.getLength() - startIndex;
    Interval result = search(startIndex, length);
    if (result == null && wrapSearch.getBoolean()) {
      result = search(0, doc.getLength());
    }
    if (result == null) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    caret.setDot(result.startIndex);
    caret.moveDot(result.endIndex);
    caret.setSelectionVisible(true);

    String searchText = search.getText();
    OptionsUtils.put(searchOptions, searchText);
    searchOptions.remove("");
  }

  private Interval search(int offset, int length) {
    JTextComponent component = context.getComponent();
    Document doc = component.getDocument();
    String text;
    try {
      text = doc.getText(offset, length);
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
    String searchText = search.getText();

    int startIndex;
    int endIndex;
    if (regex.getBoolean()) {
      Pattern pattern;
      try {
        if (caseSensitive.getBoolean()) {
          pattern = Pattern.compile(searchText);
        } else {
          pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        }
      } catch (PatternSyntaxException ex) {
        return null;
      }
      Matcher matcher = pattern.matcher(text);
      do { // Skip empty String matches
        if (matcher.find()) {
          startIndex = matcher.start();
          endIndex = matcher.end();
        } else {
          startIndex = -1;
          endIndex = -1;
          break;
        }
      } while (endIndex - startIndex <= 0);
    } else if (isWholeWord()) {
      Pattern pattern;
      searchText = "\\b" + Pattern.quote(searchText) + "\\b";
      if (caseSensitive.getBoolean()) {
        pattern = Pattern.compile(searchText);
      } else {
        pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
      }
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        startIndex = matcher.start();
        endIndex = matcher.end();
      } else {
        startIndex = -1;
        endIndex = -1;
      }
    } else {
      if (caseSensitive.getBoolean()) {
        startIndex = text.indexOf(searchText);
      } else {
        startIndex = text.toLowerCase().indexOf(searchText.toLowerCase());
      }
      endIndex = startIndex + searchText.length();
    }
    if (startIndex == -1) {
      return null;
    }

    startIndex += offset;
    endIndex += offset;
    Interval interval = new Interval(startIndex, endIndex);
    return interval;
  }

  @Validation(path = {"find", "search"})
  public ValidationState canFind() {
    if (search.isEmpty()) {
      return new ValidationState("Cannot search for empty String");

    }
    try {
      Pattern.compile(search.getText());
    } catch (PatternSyntaxException ex) {
      String rawMessage = ex.getLocalizedMessage();
      if (rawMessage.contains("\n")) {
        rawMessage = "<html>Invalid Regex:<br><pre>" + rawMessage.replaceAll("\r?\n", "<br>")
            + "</pre></html>";
        System.out.println(rawMessage);
      }
      return new ValidationState(rawMessage);
    }
    return null;
  }

  @Operation
  public void replace() {
    replace.check();
    JTextComponent component = context.getComponent();
    component.replaceSelection(getReplaceWithText());
    String replaceWithText = replaceWith.getText();
    OptionsUtils.put(replaceWithOptions, replaceWithText);
    replaceWithOptions.remove("");
  }

  private ValidationState canReplaceWithText() {
    try {
      getReplaceWithText();
    } catch (IllegalArgumentException ex) {
      return new ValidationState("Invalid escape: " + ex.getLocalizedMessage());
    }
    return null;
  }

  @Validation(path = "replace")
  public ValidationState canReplace() {
    JTextComponent component = context.getComponent();
    Caret caret = component.getCaret();
    if (caret.getDot() == caret.getMark()) {
      return new ValidationState("Nothing selected");
    }
    ValidationState canReplaceWithText = canReplaceWithText();
    if (canReplaceWithText != null) {
      return canReplaceWithText;
    }
    return null;
  }

  @Operation
  public void replaceFind() throws BadLocationException {
    replaceFind.check();
    if (canReplace() == null) {
      replace();
    }
    find();
  }

  @Validation(path = "replaceFind")
  public ValidationState canReplaceFind() {
    ValidationState canFind = canFind();
    if (canFind != null) {
      return canFind;
    }
    ValidationState canReplaceWithText = canReplaceWithText();
    if (canReplaceWithText != null) {
      return canReplaceWithText;
    }
    return null;
  }

  @Operation
  public void replaceAll() {
    replaceAll.check();
    JTextComponent component = context.getComponent();
    Document doc = component.getDocument();
    String replaceWithText = getReplaceWithText();
    int i = 0;
    while (true) {
      Interval found = search(i, doc.getLength() - i);
      if (found == null) {
        break;
      }
      int offset = found.startIndex;
      int length = found.endIndex - offset;
      try {
        DocumentUtils.replace(doc, offset, length, replaceWithText);
      } catch (BadLocationException ex) {
        throw new UndeclaredThrowableException(ex);
      }
      i = found.endIndex;
    }
  }

  @Validation(path = "replaceAll")
  public ValidationState canReplaceAll() {
    return canReplaceFind();
  }

}
