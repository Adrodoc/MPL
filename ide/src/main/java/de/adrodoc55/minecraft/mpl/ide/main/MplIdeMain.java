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
package de.adrodoc55.minecraft.mpl.ide.main;

import java.io.IOException;

import de.adrodoc55.minecraft.mpl.ide.ApplicationUtils;
import de.adrodoc55.minecraft.mpl.ide.fx.MplIdeController;
import de.adrodoc55.minecraft.mpl.main.MplCompilerMain;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Adrodoc55
 */
public class MplIdeMain extends Application {
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      launch(args);
    } else {
      MplCompilerMain.main(args);
    }
  }

  @Override
  public void start(Stage stage) throws IOException {
    stage.setTitle(
        "Minecraft Programming Language - " + ApplicationUtils.getImplementationVersion());

    ObservableList<Image> icons = stage.getIcons();
    icons.add(new Image(getClass().getResourceAsStream("/icons/command_block.png")));
    icons.add(new Image(getClass().getResourceAsStream("/icons/command_block_32.png")));
    icons.add(new Image(getClass().getResourceAsStream("/icons/command_block_16.png")));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mpl-ide.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root, 1000, 500);

    ObservableList<String> stylesheets = scene.getStylesheets();
    stylesheets.add("/mpl-ide.css");
    stylesheets.add("/syntax/highlighting/mpl.css");

    MplIdeController controller = loader.getController();
    controller.initialize(getHostServices());

    stage.setScene(scene);
    stage.show();
  }
}
