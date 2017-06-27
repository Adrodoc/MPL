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
package de.adrodoc55.minecraft.mpl.ide.fx.dialog;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;

import com.beust.jcommander.internal.Nullable;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Adrodoc55
 */
public class SecondaryStage extends Stage {
  private Object controller;

  public SecondaryStage(Window owner, URL fxml) {
    initOwner2(owner);
    addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        if (!keyEvent.isConsumed()) {
          close();
          keyEvent.consume();
        }
      }
    });
    FXMLLoader loader = new FXMLLoader(fxml);
    Parent root;
    try {
      root = loader.load();
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to load FXML file", ex);
    }
    controller = requireNonNull(loader.getController(), "controller == null!");
    setScene(new Scene(root));
  }

  /**
   * Use this instead of {@link #initOwner(Window)}
   *
   * @param newOwner
   */
  public void initOwner2(@Nullable Window newOwner) {
    Window oldOwner = getOwner();
    if (oldOwner != null) {
      if (oldOwner instanceof Stage) {
        Bindings.unbindContent(getIcons(), ((Stage) oldOwner).getIcons());
      }
      Scene scene = getScene();
      Scene oldOwnerScene = oldOwner.getScene();
      if (scene != null && oldOwnerScene != null) {
        Bindings.unbindContent(scene.getStylesheets(), oldOwnerScene.getStylesheets());
      }
    }

    initOwner(newOwner);

    if (newOwner != null) {
      if (newOwner instanceof Stage) {
        Bindings.bindContent(getIcons(), ((Stage) newOwner).getIcons());
      }
      Scene scene = getScene();
      Scene newOwnerScene = newOwner.getScene();
      if (scene != null && newOwnerScene != null) {
        Bindings.bindContent(scene.getStylesheets(), newOwnerScene.getStylesheets());
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <T> T getLoadedController() {
    return (T) controller;
  }
}
