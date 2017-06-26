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
package de.adrodoc55.minecraft.mpl.ide.fx.editor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Map;

import org.eclipse.fx.code.editor.LocalFile;
import org.eclipse.fx.code.editor.LocalSourceFileInput;
import org.eclipse.fx.code.editor.SourceFileInput;

/**
 * @author Adrodoc55
 */
public class DelegatingLocalSourceFileInput implements SourceFileInput, LocalFile {
  private LocalSourceFileInput delegate;

  public DelegatingLocalSourceFileInput(LocalSourceFileInput delegate) {
    this.delegate = checkNotNull(delegate, "delegate == null!");
  }

  public void setDelegate(LocalSourceFileInput delegate) {
    this.delegate = checkNotNull(delegate, "delegate == null!");
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public Map<String, Object> getTransientData() {
    return delegate.getTransientData();
  }

  @Override
  public Path getPath() {
    return delegate.getPath();
  }

  @Override
  public final void dispose() {
    delegate.dispose();
  }

  @Override
  public String getData() {
    return delegate.getData();
  }

  @Override
  public void setData(String data) {
    delegate.setData(data);
  }

  @Override
  public void persist() {
    delegate.persist();
  }

  @Override
  public String getURI() {
    return delegate.getURI();
  }

  @Override
  public void updateData(int offset, int length, String replacement) {
    delegate.updateData(offset, length, replacement);
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public void reload() {
    delegate.reload();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
