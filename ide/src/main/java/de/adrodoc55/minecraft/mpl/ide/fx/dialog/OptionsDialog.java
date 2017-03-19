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

import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.ide.fx.MplOptions;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * @author Adrodoc55
 */
public class OptionsDialog extends Dialog<MplOptions> {
  private OptionsController controller;

  public OptionsDialog(Window owner, MplOptions oldOptions) {
    initOwner(owner);
    initModality(Modality.WINDOW_MODAL);
    setTitle("Options");

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialog/options.fxml"));
    DialogPane root;
    try {
      root = loader.load();
      controller = requireNonNull(loader.getController(), "constroller == null!");
      controller.initialize(oldOptions);
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to load FXML file", ex);
    }
    root.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    setDialogPane(root);
    setResultConverter(this::convertResult);
  }

  private @Nullable MplOptions convertResult(ButtonType b) {
    if (ButtonData.OK_DONE.equals(b.getButtonData())) {
      return controller.getMplOptions();
    }
    return null;
  }
}
