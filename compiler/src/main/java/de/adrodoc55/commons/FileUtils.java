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

import java.io.File;
import java.io.IOException;

/**
 * @author Adrodoc55
 */
public class FileUtils {

  private FileUtils() throws Throwable {
    throw new Throwable("Utils Classes cannot be instantiated!");
  }

  public static String getFilenameWithoutExtension(File file) {
    return getFilenameWithoutExtension(file.getName());
  }

  public static String getFilenameWithoutExtension(String fileName) {
    int idx = fileName.lastIndexOf('.');
    String name = (idx == -1) ? fileName : fileName.substring(0, idx);
    return name;
  }

  public static String toUnixLineEnding(String text) {
    return text.replace("\r\n", "\n").replace("\r", "\n");
  }

  public static String getCanonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException ex) {
      return file.getAbsolutePath();
    }
  }

}
