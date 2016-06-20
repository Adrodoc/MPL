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
package de.adrodoc55.minecraft.mpl.ide.autocompletion;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.antlr.v4.runtime.Token;

import de.adrodoc55.commons.DocumentUtils;
import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.commons.Filter;

/**
 * @author Adrodoc55
 */
public abstract class AutoCompletionAction {
  private static final Pattern INSERT = Pattern.compile("(?<!\\$)(\\$+)(?:\\{([^}\\n]*)\\})?");

  private static final String CURSOR = "cursor";
  private static final Filter<String> CURSOR_INSERTS = insert -> CURSOR.equals(insert);
  private static final Filter<String> NON_CURSOR_INSERTS = insert -> !CURSOR.equals(insert);

  private final int startIndex;
  protected final @Nullable Token token;

  public AutoCompletionAction(int startIndex, @Nullable Token token) {
    this.startIndex = token != null ? token.getStartIndex() : startIndex;
    this.token = token;
  }

  public void performOn(JTextComponent component) {
    Element root = component.getDocument().getDefaultRootElement();
    Element line = root.getElement(root.getElementIndex(startIndex));
    int indentCount = startIndex - line.getStartOffset();
    String indent = new String(new char[indentCount]).replace('\0', ' ');

    String template = FileUtils.toUnixLineEnding(getTemplate());
    template = template.replace("\n", "\n" + indent);
    template = replaceVariables(template, NON_CURSOR_INSERTS);
    int cursorIndex = getCursorIndex(template);
    template = replaceVariables(template, CURSOR_INSERTS);
    try {
      int length = getLength();
      DocumentUtils.replace(component.getDocument(), startIndex, length, template);
      component.getCaret().setDot(startIndex + cursorIndex);
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  private int getCursorIndex(String template) {
    Matcher matcher = INSERT.matcher(template);
    while (matcher.find()) {
      String match = matcher.group(2);
      if (CURSOR.equals(match)) {
        String leadingDollar = matcher.group(1);
        leadingDollar = leadingDollar != null ? leadingDollar : "";
        return matcher.start() + leadingDollar.length() / 2;
      }
    }
    return template.length();
  }

  private String replaceVariables(String template, Filter<String> filter) {
    Matcher matcher = INSERT.matcher(template);
    StringBuffer sb = new StringBuffer(template.length());
    while (matcher.find()) {
      String type = matcher.group(2);
      if (!filter.matches(type)) {
        continue;
      }
      String dollars = matcher.group(1);
      String replacement;
      int dollarCount = dollars.length();
      if (dollarCount % 2 == 0) {
        replacement = matcher.group().substring(dollarCount / 2);
      } else {
        checkState(type != null,
            "Template has incomplete variables. Type '$$' to enter the dollar character.");
        replacement = dollars.substring((dollarCount + 1) / 2) + getReplacement(type);
      }
      matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private String getReplacement(String variable) {
    switch (variable) {
      case CURSOR:
        return "";
      case "token":
        return getTokenString();
      default:
        throw new IllegalArgumentException("Unknown variable '" + variable + "'");
    }
  }

  private String getTokenString() {
    if (token != null)
      return token.getText();
    else
      return "";
  }

  private int getLength() {
    if (token != null)
      return token.getStopIndex() - startIndex + 1;
    else
      return 0;
  }

  protected abstract String getTemplate();

  public abstract String getDisplayName();

  public abstract boolean shouldBeProposed(AutoCompletionContext context);
}
