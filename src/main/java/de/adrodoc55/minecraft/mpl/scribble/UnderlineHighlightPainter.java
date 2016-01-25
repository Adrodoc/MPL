package de.adrodoc55.minecraft.mpl.scribble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class UnderlineHighlightPainter extends DefaultHighlightPainter {

    public UnderlineHighlightPainter(Color c) {
        super(c);
    }

    @Override
    public void paint(Graphics g, int offs0, int offs1, Shape bounds,
            JTextComponent c) {
        int y = (int) (bounds.getBounds().getY() + bounds.getBounds().getHeight());
        int x1 = (int) bounds.getBounds().getX();
        int x2 = (int) (bounds.getBounds().getX() + bounds.getBounds().getWidth());
        Color old = g.getColor();
        g.setColor(Color.red);
        for (int i = x1; i <= x2; i += 6) {
            g.drawArc(i + 3, y - 3, 3, 3, 0, 180);
            g.drawArc(i + 6, y - 3, 3, 3, 180, 181);
        }
        g.setColor(old);
    }

}
