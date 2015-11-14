package de.adrodoc55.minecraft.mpl;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.gui.ChainRenderer;

public class ChainCalculator {
    private static final int MAX_TRIES = 1000000;
    private Coordinate3D start;
    private List<Command> commands;
    private ChainRenderer renderer;
    private ChainRenderer optimalRenderer;

    public ChainCalculator() {
        this(null, null);
    }

    public ChainCalculator(ChainRenderer renderer, ChainRenderer optimalRenderer) {
        this.renderer = renderer;
        this.optimalRenderer = optimalRenderer;
    }

    public List<Coordinate3D> calculateOptimalChain(List<Command> commands) {
        return calculateOptimalChain(new Coordinate3D(), commands);
    }

    private int tries = 0;

    public List<Coordinate3D> calculateOptimalChain(Coordinate3D start, List<Command> commands) {
        this.start = start;
        this.commands = commands;
        optimalScore = Integer.MAX_VALUE;
        optimal.clear();
        tries = 0;
        calculate(null, start);
        return optimal;
    }

    private void calculate(List<Coordinate3D> previous, Coordinate3D current) {
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
        if (current.getX() < start.getX() || current.getY() < start.getY() || current.getZ() < start.getZ()) {
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
                calculate(previous, current.plus(relative));
            }
            previous.remove(previous.size() - 1);
        }
    }

    private static Coordinate3D[] getDirections() {
        return new Coordinate3D[] { Coordinate3D.SOUTH, Coordinate3D.UP, Coordinate3D.NORTH, Coordinate3D.DOWN };
    }

    private int optimalScore = Integer.MAX_VALUE;
    private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

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

    private int calculateScore(Iterable<Coordinate3D> coordinates) {
        return getMaxLength(coordinates);
    }

    private int getMaxLength(Iterable<Coordinate3D> coordinates) {
        int minX = start.getX();
        int minY = start.getY();
        int minZ = start.getZ();
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
        for (Coordinate3D c : coordinates) {
            maxX = Math.max(maxX, c.getX());
            maxY = Math.max(maxY, c.getY());
            maxZ = Math.max(maxZ, c.getZ());
        }
        int x = maxX - minX;
        int y = maxY - minY;
        int z = maxZ - minZ;
        int i = Math.max(x, y);
        i = Math.max(i, z);
        return i;
    }

}
