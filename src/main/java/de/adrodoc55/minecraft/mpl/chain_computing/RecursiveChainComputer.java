package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class RecursiveChainComputer implements ChainComputer {
    private static final int MAX_TRIES = 1000000;
    private Coordinate3D min;
    private Coordinate3D max;
    private List<Command> commands;

    private int tries = 0;

    public CommandBlockChain computeOptimalChain(CommandChain chain,
            Coordinate3D max) {
        this.min = new Coordinate3D();
        this.max = max;
        this.commands = chain.getCommands();
        optimalScore = Integer.MAX_VALUE;
        optimal.clear();
        tries = 0;
        calculateRecursively(null, min);

        CommandBlockChain output = toCommandBlockChain(chain, optimal);
        return output;
    }

    private void calculateRecursively(List<Coordinate3D> previous,
            Coordinate3D current) {
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
        int x = current.getX();
        int y = current.getY();
        int z = current.getZ();
        if (x < min.getX() || y < min.getY() || z < min.getZ()
                || x > max.getX() || y > max.getY() || z > max.getZ()) {
            return;
        }

        previous.add(current);
        if (optimalScore <= calculateScore(previous)) {
            previous.remove(previous.size() - 1);
            return;
        }
        previous.remove(previous.size() - 1);

        int index = previous.size();
        if (index >= commands.size()) {
            previous.add(current);
            registerPossibility(previous);
            previous.remove(previous.size() - 1);
            return;
        } else {
            Direction[] directions;
            Command currentCommand = commands.get(index);
            if (currentCommand != null && currentCommand.isConditional()) {
                if (previous.isEmpty()) {
                    throw new IllegalStateException(
                            "The first Command can't be conditional!");
                }
                Coordinate3D lastCoordinate = previous.get(previous.size() - 1);
                Coordinate3D relativeCoordinate = current.minus(lastCoordinate);
                Direction direction = Direction.valueOf(relativeCoordinate);
                directions = new Direction[] { direction };
            } else {
                directions = getDirections();
            }

            previous.add(current);
            for (Direction direction : directions) {
                calculateRecursively(previous,
                        current.plus(direction.toCoordinate()));
            }
            previous.remove(previous.size() - 1);
        }
    }

    private static Direction[] getDirections() {
        return Direction.values();
        // return new Coordinate3D[] { Coordinate3D.SOUTH, Coordinate3D.UP,
        // Coordinate3D.NORTH, Coordinate3D.DOWN };
    }

    private int optimalScore = Integer.MAX_VALUE;
    private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

    private void registerPossibility(List<Coordinate3D> possibility) {
        int score = calculateScore(possibility);
        if (score < optimalScore) {
            optimal.clear();
            optimal.addAll(possibility);
            optimalScore = score;
        }
    }

    private int calculateScore(Iterable<Coordinate3D> coordinates) {
        return getMaxLength(coordinates);
    }

    private int getMaxLength(Iterable<Coordinate3D> coordinates) {
        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
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
