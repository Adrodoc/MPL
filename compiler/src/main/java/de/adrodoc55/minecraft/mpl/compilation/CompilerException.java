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
package de.adrodoc55.minecraft.mpl.compilation;

import java.io.File;
import java.nio.charset.CharacterCodingException;

import de.adrodoc55.commons.FileUtils;

/**
 * @author Adrodoc55
 */
public class CompilerException extends Exception {

  private static final long serialVersionUID = 1L;

  private MplSource source;

  public CompilerException(MplSource source, String message) {
    super(message);
    init(source);
  }

  public CompilerException(MplSource source, String message, Throwable cause) {
    super(message, cause);
    init(source);
  }

  private void init(MplSource source) {
    this.source = source;
  }

  public MplSource getSource() {
    return source;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String path = FileUtils.getCanonicalPath(source.file);
    sb.append(path).append(':').append(source.token.getLine()).append(":\n");
    sb.append(this.getLocalizedMessage()).append("\n");
    sb.append(source.line).append("\n");
    int count = source.token.getCharPositionInLine();
    sb.append(new String(new char[count]).replace('\0', ' '));
    sb.append("^");
    Throwable cause = getCause();
    if (cause != null) {
      sb.append("\npossible cause: ");
      File file = null;
      if (cause instanceof FileException) {
        file = ((FileException) cause).getFile();
        cause = cause.getCause();
      }
      if (cause != null && cause instanceof CharacterCodingException) {
        sb.append("Invalid file encoding, must be UTF-8!");
      } else {
        sb.append(cause.toString());
      }
      if (file != null) {
        sb.append("\nin file: ").append(FileUtils.getCanonicalPath(file));
      }
    }
    return sb.toString();
  }
}
