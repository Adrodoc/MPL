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
package de.adrodoc55.commons;

import javax.annotation.concurrent.Immutable;

/**
 * @author Adrodoc55
 */
@Immutable
public class Version implements Comparables<Version> {
  private final String version;

  public Version(String version) {
    int index = version.length();
    while (index >= 0) {
      int i = index;
      while (version.charAt(--i) == '0');
      if (version.charAt(i) == '.') {
        index = i;
      } else {
        break;
      }
    }
    this.version = version.substring(0, index);
  }

  @Override
  public int compareTo(Version other) {
    int thisIndex = 0;
    int otherIndex = 0;
    while (true) {
      boolean thisEnd = thisIndex >= this.version.length();
      boolean otherEnd = otherIndex >= other.version.length();
      if (thisEnd && otherEnd) {
        return 0;
      } else if (thisEnd) {
        return -1;
      } else if (otherEnd) {
        return 1;
      }
      char thisChar = this.version.charAt(thisIndex);
      char otherChar = other.version.charAt(otherIndex);
      int result;
      if (isDigit(thisChar) && isDigit(otherChar)) {
        int thisStartIndex = thisIndex;
        int otherStartIndex = otherIndex;
        while (++thisIndex < this.version.length() && isDigit(this.version.charAt(thisIndex)));
        while (++otherIndex < other.version.length() && isDigit(other.version.charAt(otherIndex)));
        long thisLong = Long.parseLong(this.version.substring(thisStartIndex, thisIndex));
        long otherLong = Long.parseLong(other.version.substring(otherStartIndex, otherIndex));
        result = Long.compare(thisLong, otherLong);
      } else {
        result = Character.compare(thisChar, otherChar);
        thisIndex++;
        otherIndex++;
      }
      if (result != 0) {
        return result;
      }
    }
  }

  private static boolean isDigit(char c) {
    return '0' <= c && c <= '9';
  }

  @Override
  public String toString() {
    return version;
  }
}
