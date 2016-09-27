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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

/**
 * @author Adrodoc55
 */
@Immutable
public class Version implements Comparables<Version> {
  private final String version;
  private final Iterable<String> tokens;

  public Version(String version) {
    this.version = version;
    tokens = tokenize(version);
  }

  private static Iterable<String> tokenize(String version) {
    if (version.isEmpty()) {
      return Collections.emptyList();
    }
    Deque<String> tokens = new ArrayDeque<>();
    boolean lastWasDigit = isDigit(version.charAt(0));
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i);

      boolean isDigit = isDigit(c);
      if (isDigit != lastWasDigit || c == '.') {
        tokens.add(sb.toString());
        sb = new StringBuilder();
      }
      if (c != '.') {
        sb.append(c);
      }
      lastWasDigit = isDigit;
    }
    tokens.add(sb.toString());
    removeTrailingZeros(tokens);
    return tokens;
  }

  private static boolean isDigit(char c) {
    return '0' <= c && c <= '9';
  }

  private static void removeTrailingZeros(Deque<String> tokens) {
    for (Iterator<String> it = tokens.descendingIterator(); it.hasNext();) {
      String token = it.next();
      if (containsOnlyZeros(token)) {
        it.remove();
      } else {
        break;
      }
    }
  }

  private static boolean containsOnlyZeros(String string) {
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      if (c != '0') {
        return false;
      }
    }
    return true;
  }

  @Override
  public int compareTo(Version other) {
    Iterator<String> thisIt = this.tokens.iterator();
    Iterator<String> otherIt = other.tokens.iterator();
    while (true) {
      if (thisIt.hasNext() && otherIt.hasNext()) {
        String thisToken = thisIt.next();
        String otherToken = otherIt.next();
        int result;
        try {
          int thisInt = Integer.parseInt(thisToken);
          int otherInt = Integer.parseInt(otherToken);
          result = Integer.compare(thisInt, otherInt);
        } catch (NumberFormatException ex) {
          result = thisToken.compareTo(otherToken);
        }
        if (result != 0) {
          return result;
        }
      } else if (thisIt.hasNext()) {
        return 1;
      } else if (otherIt.hasNext()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  @Override
  public String toString() {
    return version;
  }
}
