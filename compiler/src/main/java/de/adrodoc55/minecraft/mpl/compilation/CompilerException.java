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

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.charset.CharacterCodingException;

import javax.annotation.Nonnull;

import de.adrodoc55.commons.FileUtils;

/**
 * @author Adrodoc55
 */
public class CompilerException extends Exception {
  private static final long serialVersionUID = 1L;

  private @Nonnull MplSource source;

  public CompilerException(@Nonnull MplSource source, String message) {
    super(message);
    init(source);
  }

  public CompilerException(@Nonnull MplSource source, String message, Throwable cause) {
    super(message, cause);
    init(source);
  }

  private void init(MplSource source) {
    this.source = checkNotNull(source, "source == null!");
  }

  public @Nonnull MplSource getSource() {
    return source;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String path = FileUtils.getCanonicalPath(source.getFile());
    sb.append(path).append(':').append(source.getLineNumber()).append(":\n");
    sb.append(this.getLocalizedMessage()).append("\n");
    int count = source.getCharPositionInLine();
    if (count >= 0) {
      sb.append(source.getLine()).append("\n");
      sb.append(new String(new char[count]).replace('\0', ' '));
      sb.append("^\n");
    }
    Throwable cause = getCause();
    if (cause != null) {
      sb.append("caused by: ");
      if (cause instanceof CharacterCodingException) {
        sb.append("Invalid file encoding, must be UTF-8!");
      } else {
        sb.append(cause.toString());
      }
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    Throwable cause = getCause();
    result = prime * result + ((cause == null) ? 0 : cause.hashCode());
    String message = getMessage();
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompilerException other = (CompilerException) obj;
    Throwable cause = getCause();
    Throwable otherCause = other.getCause();
    if (cause == null) {
      if (otherCause != null)
        return false;
    } else if (!cause.equals(otherCause))
      return false;
    String message = getMessage();
    String otherMessage = other.getMessage();
    if (message == null) {
      if (otherMessage != null)
        return false;
    } else if (!message.equals(otherMessage))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }

}
