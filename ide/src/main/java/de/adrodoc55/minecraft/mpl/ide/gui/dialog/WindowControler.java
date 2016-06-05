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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;

import org.beanfabrics.model.PresentationModel;

/**
 * @author Adrodoc55
 * @param <V> the {@link WindowView} managed by this controller.
 * @param <PM> the {@link PresentationModel} managed by this controller.
 */
public abstract class WindowControler<V extends WindowView<PM>, PM extends PresentationModel> {
  private V view;
  private PM pm;

  public V getView() {
    if (view == null) {
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      view = createView(activeWindow);
      view.setPresentationModel(getPresentationModel());
    }
    return view;
  }

  protected abstract V createView(Window activeWindow);

  public PM getPresentationModel() {
    if (pm == null) {
      pm = createPM();
    }
    return pm;
  }

  protected abstract PM createPM();

  public void dispose() {
    if (hasView()) {
      getView().dispose();
    }
  }

  public boolean hasView() {
    return view != null;
  }

  public void setLocation(Component source, Point location) {
    int fontSize = source.getFont().getSize();
    location.translate(1, fontSize + 3);
    Point screenPos = source.getLocationOnScreen();
    location.translate(screenPos.x, screenPos.y);
    getView().setLocation(location);
  }
}
