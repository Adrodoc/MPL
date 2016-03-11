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
package de.adrodoc55.commons;

import java.util.Arrays;

/**
 * @author Adrodoc55
 */
public class TabToSpaceConverter {

  private TabToSpaceConverter() throws Throwable {
    throw new Throwable("Utils Classes cannot be instantiated");
  }

  /**
   * Converts all tabs to spaces, so that each tab ends after a multiple of 4 characters after the
   * last newline.
   *
   * @param text the string containing tabs
   * @return a String without tabs
   */
  public static String convertTabsToSpaces(String text) {
    return convertTabsToSpaces(0, 4, text);
  }

  /**
   * Converts all tabs to spaces, so that each tab ends after a multiple of {@code tabWidth}
   * characters after the last newline.
   *
   * @param tabWidth the maximum number of spaces to use for a tab
   * @param text the string containing tabs
   * @return a String without tabs
   */
  public static String convertTabsToSpaces(int tabWidth, String text) {
    return convertTabsToSpaces(0, tabWidth, text);
  }

  /**
   * Converts all tabs to spaces, so that each tab ends after a multiple of {@code tabWidth}
   * characters after the last newline. {@code offset} can be specified, if {@code text} is part of
   * a bigger {@code String} and does not immediately follow a newline.
   *
   * @param offset the number of characters between the last newline and the start of text
   * @param tabWidth the maximum number of spaces to use for a tab
   * @param text the string containing tabs
   * @return a String without tabs
   */
  public static String convertTabsToSpaces(int offset, int tabWidth, String text) {
    StringBuilder sb = new StringBuilder(text.length());
    int afterLastNlIndex = -offset;
    int afterLastTabIndex = 0;
    char[] textArray = text.toCharArray();
    for (int i = 0; i < textArray.length; i++) {
      if (textArray[i] == '\r' || textArray[i] == '\n') {
        afterLastNlIndex = i + 1;
      } else if (textArray[i] == '\t') {
        sb.append(Arrays.copyOfRange(textArray, afterLastTabIndex, i));
        int spaceCount = tabWidth - ((i - afterLastNlIndex) % tabWidth);
        for (int s = spaceCount; s > 0; s--) {
          sb.append(' ');
        }
        afterLastTabIndex = i + 1;
        // afterLastNlIndex muss korrigiert werden, da (spaceCount - 1) spaces nach der letzten
        // newline eingefügt wurden. Minus 1, da ein tab bereits da war, und ersetzt wird.
        afterLastNlIndex -= spaceCount - 1;
      }
    }
    sb.append(Arrays.copyOfRange(textArray, afterLastTabIndex, textArray.length));
    return sb.toString();
  }

}
