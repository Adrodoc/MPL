package de.adrodoc55.minecraft.mpl.scribble;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class Test {
    public Test() {
        JFrame fr = new JFrame("TEST");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(new NewEditorKit());
        pane.setText("test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test ");
        StyledDocument doc = (StyledDocument) pane.getDocument();
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setUnderline(attr, true);
        // StyleConstants.setLineSpacing(attr, 5f);
        doc.setParagraphAttributes(0, doc.getLength(), attr, false);
        JScrollPane sp = new JScrollPane(pane);
        fr.getContentPane().add(sp);
        fr.setSize(300, 300);
        fr.show();
    }

    public static void main(String[] args) {
        Test test = new Test();
    }
}

class NewEditorKit extends StyledEditorKit {
    public ViewFactory getViewFactory() {
        return new NewViewFactory();
    }
}

class NewViewFactory implements ViewFactory {
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

class JaggedLabelView extends LabelView {
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
        g.setColor(Color.red);
        for (int i = x1; i <= x2; i += 6) {
            g.drawArc(i + 3, y - 3, 3, 3, 0, 180);
            g.drawArc(i + 6, y - 3, 3, 3, 180, 181);
        }
        g.setColor(old);
    }
}
