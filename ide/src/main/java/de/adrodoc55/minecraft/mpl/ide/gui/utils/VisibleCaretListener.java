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
package de.adrodoc55.minecraft.mpl.ide.gui.utils;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

/**
 * Simple class to ensure that the caret is visible within the viewport of the scrollpane. This is
 * the normal situation. However, I've noticed that solutions that attempt to turn a text pane into
 * a non wrapping text pane will result in the caret not being visible when adding text to the right
 * edge of the viewport.<br>
 * <br>
 * In general, this class can be used any time you wish to increase the number of visible pixels
 * after the caret on the right edge of a scroll pane.<br>
 * <br>
 * https://tips4java.wordpress.com/2009/01/25/no-wrap-text-pane/
 */
public class VisibleCaretListener implements CaretListener {
  private int visiblePixels;

  /**
   * Convenience constructor to create a VisibleCaretListener using the default value for visible
   * pixels, which is set to 2.
   */
  public VisibleCaretListener() {
    this(2);
  }

  /**
   * Create a VisibleCaretListener.
   *
   * @param pixels the number of visible pixels after the caret.
   */
  public VisibleCaretListener(int visiblePixels) {
    setVisiblePixels(visiblePixels);
  }

  /**
   * Get the number of visble pixels displayed after the Caret.
   *
   * @return the number of visible pixels after the caret.
   */
  public int getVisiblePixels() {
    return visiblePixels;
  }

  /**
   * Control the number of pixels that should be visible in the viewport after the caret position.
   *
   * @param pixels the number of visible pixels after the caret.
   */
  public void setVisiblePixels(int visiblePixels) {
    this.visiblePixels = visiblePixels;
  }

  //
  // Implement CaretListener interface
  //
  @Override
  public void caretUpdate(final CaretEvent e) {
    // Attempt to scroll the viewport to make sure Caret is visible

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          JTextComponent component = (JTextComponent) e.getSource();
          int position = component.getCaretPosition();
          Rectangle r = component.modelToView(position);
          r.x += visiblePixels;
          component.scrollRectToVisible(r);
        } catch (Exception ble) {
        }
      }
    });
  }
}
