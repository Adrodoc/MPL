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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.beanfabrics.swing.internal.BnStyledEditorKit;

/**
 * @author Adrodoc55
 */
public class BnJaggedEditorKit extends BnStyledEditorKit {
  private static final long serialVersionUID = 1L;

  public JaggedViewFactory getViewFactory() {
    return new JaggedViewFactory();
  }

  /**
   * @author Adrodoc55
   */
  public static class JaggedViewFactory implements ViewFactory {
    public View create(Element elem) {
      String kind = elem.getName();
      if (kind != null) {
        if (kind.equals(AbstractDocument.ContentElementName)) {
          return createLabelView(elem);
        } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
          // return new MyParagraphView(elem);
          return new ParagraphView(elem);
        } else if (kind.equals(AbstractDocument.SectionElementName)) {
          return new BoxView(elem, View.Y_AXIS);
        } else if (kind.equals(StyleConstants.ComponentElementName)) {
          return new ComponentView(elem);
        } else if (kind.equals(StyleConstants.IconElementName)) {
          return new IconView(elem);
        }
      } // default to text display
      return new LabelView(elem);
    }

    protected JaggedLabelView createLabelView(Element elem) {
      return new JaggedLabelView(elem);
    }
  }

  // public static class MyParagraphView extends ParagraphView {
  //
  // public MyParagraphView(Element elem) {
  // super(elem);
  // }
  //
  // public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
  // super.removeUpdate(e, a, f);
  // resetBreakSpots();
  // }
  //
  // public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
  // super.insertUpdate(e, a, f);
  // resetBreakSpots();
  // }
  //
  // private void resetBreakSpots() {
  // for (int i = 0; i < layoutPool.getViewCount(); i++) {
  // View v = layoutPool.getView(i);
  // if (v instanceof JaggedLabelView) {
  // ((JaggedLabelView) v).resetBreakSpots();
  // }
  // }
  // }
  //
  // }
  /**
   * @author Adrodoc55
   */
  public static class JaggedLabelView extends LabelView {
    public JaggedLabelView(Element elem) {
      super(elem);
    }

    public void paint(Graphics g, Shape allocation) {
      boolean underline = isUnderline();
      if (underline) {
        setUnderline(false);
      }
      super.paint(g, allocation);
      if (underline) {
        paintJaggedLine(g, allocation);
        setUnderline(true);
      }
    }

    public void paintJaggedLine(Graphics g, Shape a) {
      int y = (int) (a.getBounds().getY() + a.getBounds().getHeight());
      int x1 = (int) a.getBounds().getX();
      int x2 = (int) (a.getBounds().getX() + a.getBounds().getWidth());
      Color old = g.getColor();
      g.setColor(Color.RED);

      for (int x = x1; x <= x2; x++) {
        if (x % 2 == 0) {
          g.drawLine(x, y - 1, x, y - 1);
        } else if (x % 4 == 1) {
          g.drawLine(x, y - 2, x, y - 2);
        } else {
          g.drawLine(x, y, x, y);
        }
      }
      g.setColor(old);
    }

    // boolean isResetBreakSpots = false;
    //
    // public View breakView(int axis, int p0, float pos, float len) {
    // if (axis == View.X_AXIS) {
    // resetBreakSpots();
    // }
    // return super.breakView(axis, p0, pos, len);
    // }
    //
    // private void resetBreakSpots() {
    // isResetBreakSpots = true;
    // removeUpdate(null, null, null);
    // isResetBreakSpots = false;
    //
    // }
    //
    // public void preferenceChanged(View child, boolean width, boolean height) {
    // if (!isResetBreakSpots) {
    // super.preferenceChanged(child, width, height);
    // }
    // }

  }
}
