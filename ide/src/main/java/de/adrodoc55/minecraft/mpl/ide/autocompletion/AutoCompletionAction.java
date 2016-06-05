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

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nullable;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.antlr.v4.runtime.Token;

import com.google.common.io.Resources;

import de.adrodoc55.commons.DocumentUtils;
import de.adrodoc55.commons.FileUtils;

/**
 * @author Adrodoc55
 */
public abstract class AutoCompletionAction {
  private static final String CURSOR = "\\$\\{cursor\\}";
  private static final String TOKEN = "${token}";

  private int startIndex;
  protected final @Nullable Token token;

  public AutoCompletionAction(int startIndex, @Nullable Token token) {
    this.startIndex = startIndex;
    this.token = token;
  }

  public void performOn(JTextComponent component) {
    Element root = component.getDocument().getDefaultRootElement();

    Element line = root.getElement(root.getElementIndex(startIndex));
    int indentCount = startIndex - line.getStartOffset();
    String indent = new String(new char[indentCount]).replace('\0', ' ');

    String template = FileUtils.toUnixLineEnding(getTemplateString());
    template = template.replace("\n", "\n" + indent);
    template = template.replace(TOKEN, getTokenString());
    String[] split = template.split(CURSOR, 2);
    String beforeCaret = split[0];
    String afterCaret = split.length == 2 ? split[1] : "";
    String replacement = beforeCaret + afterCaret;

    try {
      DocumentUtils.replace(component.getDocument(), startIndex, getLength(), replacement);
      component.getCaret().setDot(startIndex + beforeCaret.length());
    } catch (BadLocationException ex) {
      throw new UndeclaredThrowableException(ex);
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

  protected String getTemplateString() {
    try {
      return Resources.toString(getTemplate(), Charset.forName("UTF-8"));
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  protected abstract URL getTemplate();

  public abstract String getDisplayName();
}
