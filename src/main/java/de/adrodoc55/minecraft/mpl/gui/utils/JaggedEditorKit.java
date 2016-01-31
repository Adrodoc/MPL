package de.adrodoc55.minecraft.mpl.gui.utils;

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
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class JaggedEditorKit extends StyledEditorKit {
  private static final long serialVersionUID = 1L;

  public ViewFactory getViewFactory() {
    return new JaggedViewFactory();
  }

  public static class JaggedViewFactory implements ViewFactory {
    public View create(Element elem) {
      String kind = elem.getName();
      if (kind != null) {
        if (kind.equals(AbstractDocument.ContentElementName)) {
          return new JaggedLabelView(elem);
        } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
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
  }

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
  }
}
