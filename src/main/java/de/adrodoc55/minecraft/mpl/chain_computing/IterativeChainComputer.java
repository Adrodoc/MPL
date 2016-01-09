package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class IterativeChainComputer implements ChainComputer {

    private Coordinate3D min;
    private Coordinate3D max;

    private int optimalScore;
    private List<Command> commands;
    private List<Coordinate3D> path;
    private int i;
    private Command currentCommand;
    private Coordinate3D currentCoordinate;

    private static final int MAX_TRIES = 1000000;

    public CommandBlockChain computeOptimalChain(CommandChain chain, Coordinate3D max) {
        this.min = new Coordinate3D();
        this.max = max;
        optimalScore = Integer.MAX_VALUE;
        optimal.clear();
        this.commands = chain.getCommands();
        @SuppressWarnings("unchecked")
        List<Coordinate3D>[] todos = new List[commands.size() + 1];
        path = new ArrayList<Coordinate3D>(commands.size() + 1);
        path.add(min);
        int tries = 0;
        while (!path.isEmpty()) {
            tries++;
            if (tries > MAX_TRIES) {
                break;
            }
            i = path.size() - 1;
            currentCoordinate = path.get(i);
            if (todos[i] == null) {
                if (!canPathContinue()) {
                    todos[i] = null;
                    path.remove(i);
                    continue;
                }
                if (i >= commands.size()) {
                    registerPath(path);
                    todos[i] = null;
                    path.remove(i);
                    continue;
                }
                todos[i] = getNextCoordinates();
            }
            List<Coordinate3D> currentTodos = todos[i];
            if (currentTodos.isEmpty()) {
                todos[i] = null;
                path.remove(i);
                continue;
            } else {
                Coordinate3D currentTodo = currentTodos.get(0);
                currentTodos.remove(0);
                path.add(currentTodo);
                continue;
            }
        }
        if (optimal.isEmpty()) {
            throw new IllegalStateException(
                    "Couldn't find a Solution for '" + chain.getName() + "' within " + MAX_TRIES + " tries!");
        }
        CommandBlockChain output = toCommandBlockChain(chain, optimal);
        return output;
    }

    private List<Coordinate3D> getNextCoordinates() {
        Direction[] directions;
        currentCommand = commands.get(i);
        if (currentCommand != null && currentCommand.isConditional()) {
            if (path.size() < 2) {
                throw new IllegalStateException("The first Command can't be conditional!");
            }
            Coordinate3D previousCoordinate = path.get(i - 1);
            Direction direction = Direction.valueOf(currentCoordinate.minus(previousCoordinate));
            directions = new Direction[] { direction };
        } else {
            directions = Direction.values();
        }
        List<Coordinate3D> coords = getAdjacentCoordinates(currentCoordinate, directions);
        return coords;
    }

    private static List<Coordinate3D> getAdjacentCoordinates(Coordinate3D coordinate, Direction[] directions) {
        List<Coordinate3D> coords = new ArrayList<Coordinate3D>(directions.length);
        for (Direction direction : directions) {
            coords.add(coordinate.plus(direction.toCoordinate()));
        }
        return coords;
    }

    private boolean canPathContinue() {
        if (!isCoordinateValid(currentCoordinate)) {
            return false;
        }

        Set<Coordinate3D> validCoordinates = new HashSet<Coordinate3D>();
        List<Coordinate3D> todos = new ArrayList<Coordinate3D>();
        todos.add(currentCoordinate);
        while (!todos.isEmpty()) {
            Coordinate3D coordinate = todos.get(0);
            todos.remove(0);
            List<Coordinate3D> adjacentCoordinates = getAdjacentCoordinates(coordinate, Direction.values());
            for (Coordinate3D a : adjacentCoordinates) {
                if (isCoordinateValid(a) && !validCoordinates.contains(a)) {
                    validCoordinates.add(a);
                    todos.add(a);
                }
            }
        }

        return true;
    }

    protected boolean isCoordinateValid(Coordinate3D coordinate) {
        if (i != path.indexOf(coordinate)) {
            return false;
        }

        int x = coordinate.getX();
        int y = coordinate.getY();
        int z = coordinate.getZ();
        if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || y > max.getY() || z > max.getZ()) {
            return false;
        }

        if (optimalScore <= calculateCost(path)) {
            return false;
        }
        return true;
    }

    private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

    private void registerPath(List<Coordinate3D> path) {
        int score = calculateCost(path);
        if (score < optimalScore) {
            optimal.clear();
            optimal.addAll(path);
            optimalScore = score;
        }
    }

    /**
     * Higher Values indicate a higher Cost.
     * @param coordinates
     * @return cost
     */
    protected int calculateCost(List<Coordinate3D> coordinates) {
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
