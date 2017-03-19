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
package de.adrodoc55.minecraft.mpl.ide.fx.dialog.unsaved;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Adrodoc55
 */
public class UnsavedResource {
  private final BooleanProperty save = new SimpleBooleanProperty(this, "save", true);
  private final StringProperty name = new SimpleStringProperty(this, "name");
  private final Path path;

  public UnsavedResource(Path path) {
    this.path = checkNotNull(path, "path == null!");
    setName(path.getFileName().toString());
  }

  public final BooleanProperty saveProperty() {
    return save;
  }

  public final boolean isSave() {
    return saveProperty().get();
  }

  public final void setSave(boolean save) {
    saveProperty().set(save);
  }

  public final StringProperty nameProperty() {
    return name;
  }

  public final String getName() {
    return nameProperty().get();
  }

  public final void setName(String name) {
    nameProperty().set(name);
  }

  public Path getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "UnsavedResource [save=" + isSave() + ", path=" + path + "]";
  }
}
