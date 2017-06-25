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
package de.adrodoc55.minecraft.mpl.ide.fx.editor.marker;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import org.eclipse.fx.text.hover.HoverInfoType;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Adrodoc55
 */
public enum MplAnnotationType implements HoverInfoType {
  ERROR(Color.RED, "/icons/error_16.png"), //
  WARNING(Color.GOLD, "/icons/warning_16.png"),//
  ;
  private final Paint paint;
  private final String url;

  private MplAnnotationType(Paint paint, String url) {
    this.paint = checkNotNull(paint, "paint == null!");
    this.url = checkNotNull(url, "url == null!");
  }

  @Override
  public String getType() {
    return name();
  }

  public Paint getPaint() {
    return paint;
  }

  private @Nullable WeakReference<Image> image;

  public Image getImage() {
    if (image == null) {
      return loadImage();
    }
    Image result = image.get();
    if (result == null) {
      return loadImage();
    }
    return result;
  }

  private Image loadImage() {
    Image result = new Image(url);
    image = new WeakReference<Image>(result);
    return result;
  }
}
