package de.adrodoc55.minecraft.mpl.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command;

public class ChainRenderer extends JComponent {

    private static final long serialVersionUID = 1L;

    private List<Coordinate3D> coordinates = new ArrayList<Coordinate3D>();
    private List<Command> commands;

    public ChainRenderer(List<Command> commands) {
        this.commands = commands;
    }

    public void render(List<Coordinate3D> coordinates) {
        this.coordinates = coordinates;
        repaint();
    }

    private static final int SCALE = 20;

    private Random random = new Random();

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate3D c = coordinates.get(i);
            int z = (c.getZ() * SCALE) + SCALE;
            int y = (c.getY() * SCALE) + SCALE;
            if (i < commands.size() && commands.get(i) == null) {
                g.setColor(Color.BLACK);
            } else if (i < commands.size() && commands.get(i).isConditional()) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(new Color(0.5f, random.nextFloat(), random.nextFloat()));
            }
            g.fillRect(z, y, SCALE, SCALE);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), z, y + SCALE * 2 / 3);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 1000);
    }

}
