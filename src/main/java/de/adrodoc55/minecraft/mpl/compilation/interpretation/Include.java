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
package de.adrodoc55.minecraft.mpl.compilation.interpretation;

import java.io.File;
import java.util.Collection;

import de.adrodoc55.minecraft.mpl.compilation.MplSource;

public class Include {
  private final MplSource source;
  private final String processName;
  private final Collection<File> files;

  public Include(MplSource source, Collection<File> imports) {
    this(source, null, imports);
  }

  public Include(MplSource source, String processName, Collection<File> imports) {
    this.source = source;
    this.processName = processName;
    files = imports;
  }

  public MplSource getSource() {
    return source;
  }

  public String getProcessName() {
    return processName;
  }

  public Collection<File> getFiles() {
    return files;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((files == null) ? 0 : files.hashCode());
    result = prime * result + ((processName == null) ? 0 : processName.hashCode());
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
    Include other = (Include) obj;
    if (files == null) {
      if (other.files != null)
        return false;
    } else if (!files.equals(other.files))
      return false;
    if (processName == null) {
      if (other.processName != null)
        return false;
    } else if (!processName.equals(other.processName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Include [processName=" + processName + "]";
  }

}
