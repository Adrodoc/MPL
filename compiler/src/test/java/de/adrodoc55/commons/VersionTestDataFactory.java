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

import java.util.Arrays;

public class VersionTestDataFactory {
  public static Iterable<String[]> equalTo() {
    return Arrays.asList(new String[][] {//
        {"1", "1"}, //
        {"a", "a"}, //
        {"1a", "1a"}, //
        {"a1", "a1"}, //
        {"1.2", "1.2"}, //
        {"1", "1.0"}, //
        {"1", "1.0.0"}, //
    });
  }

  public static Iterable<String[]> lessThan() {
    return Arrays.asList(new String[][] {//
        {"1", "2"}, //
        {"1", "a"}, //
        {"1", "1a"}, //
        {"a1", "a2"}, //
        {"1.1", "1.2"}, //
        {"1.1", "2"}, //
        {"1.1", "2.1"}, //
        {"2", "10"}, //
        {"2.1", "10"}, //
        {"10.0", "100"}, //
        {"9223372036854775806", "9223372036854775807"},//
    });
  }

  public static Iterable<String[]> greaterThan() {
    Iterable<String[]> result = lessThan();
    for (String[] pair : result) {
      String temp = pair[0];
      pair[0] = pair[1];
      pair[1] = temp;
    }
    return result;
  }
}
