package de.adrodoc55.minecraft.mpl;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.gui.ChainRenderer;

public class ChainCalculator {
    private static final int MAX_TRIES = 1000000;
    private List<Command> commands;
    private ChainRenderer renderer;
    private ChainRenderer optimalRenderer;

    public ChainCalculator(List<Command> commands) {
        this(commands, null, null);
    }

    public ChainCalculator(List<Command> commands, ChainRenderer renderer, ChainRenderer optimalRenderer) {
        this.commands = commands;
        this.renderer = renderer;
        this.optimalRenderer = optimalRenderer;
    }

    public List<Coordinate3D> calculateOptimalChain(List<Command> commands) {
        this.commands = commands;
        return calculateOptimalChain();
    }

    private int tries = 0;

    public List<Coordinate3D> calculateOptimalChain() {
        tries = 0;
        complete(null, new Coordinate3D());
        return optimal;
    }

    private void complete(List<Coordinate3D> previous, Coordinate3D current) {
        tries++;
        if (tries > MAX_TRIES) {
            return;
        }
        if (previous == null) {
            previous = new ArrayList<Coordinate3D>();
        }
        if (previous.contains(current)) {
            return;
        }
        if (current.getX() < 0 || current.getY() < 0 || current.getZ() < 0) {
            return;
        }

        previous.add(current);
        if (optimalScore <= calculateScore(previous)) {
            previous.remove(previous.size() - 1);
            return;
        }
        previous.remove(previous.size() - 1);

        if (renderer != null) {
            renderer.render(previous);
        }

        int index = previous.size();
        if (index >= commands.size()) {
            previous.add(current);
            registerPossibility(previous);
            previous.remove(previous.size() - 1);
            return;
        } else {
            Coordinate3D[] directions;
            Command currentCommand = commands.get(index);
            if (currentCommand != null && currentCommand.isConditional()) {
                if (previous.isEmpty()) {
                    throw new IllegalStateException("Der Erste Befehl kann nicht conditional sein!");
                }
                Coordinate3D lastCoordinate = previous.get(previous.size() - 1);
                Coordinate3D direction = current.minus(lastCoordinate);
                directions = new Coordinate3D[] { direction };
            } else {
                directions = getDirections();
            }

            previous.add(current);
            for (Coordinate3D relative : directions) {
                complete(previous, current.plus(relative));
            }
            previous.remove(previous.size() - 1);
        }
    }

    private static Coordinate3D[] getDirections() {
        return new Coordinate3D[] { Coordinate3D.SOUTH, Coordinate3D.UP, Coordinate3D.NORTH, Coordinate3D.DOWN };
    }

    private int optimalScore = Integer.MAX_VALUE;
    private List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

    private void registerPossibility(List<Coordinate3D> possibility) {
        int score = calculateScore(possibility);
        if (score < optimalScore) {
            optimal.clear();
            optimal.addAll(possibility);
            optimalScore = score;
            if (optimalRenderer != null) {
                optimalRenderer.render(optimal);
            }
        }
    }

    private static int calculateScore(Iterable<Coordinate3D> coordinates) {
        return getMaxLength(coordinates);
    }

    private static int getMaxLength(Iterable<Coordinate3D> coordinates) {
        int maxX = getMaxX(coordinates);
        int maxY = getMaxY(coordinates);
        int maxZ = getMaxZ(coordinates);
        int i = Math.max(maxX, maxY);
        i = Math.max(i, maxZ);
        return i;
    }

    private static int getMaxX(Iterable<Coordinate3D> coordinates) {
        int maxX = 0;
        for (Coordinate3D c : coordinates) {
            maxX = Math.max(maxX, c.getX());
        }
        return maxX;
    }

    private static int getMaxY(Iterable<Coordinate3D> coordinates) {
        int maxY = 0;
        for (Coordinate3D c : coordinates) {
            maxY = Math.max(maxY, c.getY());
        }
        return maxY;
    }

    private static int getMaxZ(Iterable<Coordinate3D> coordinates) {
        int maxZ = 0;
        for (Coordinate3D c : coordinates) {
            maxZ = Math.max(maxZ, c.getZ());
        }
        return maxZ;
    }

}
