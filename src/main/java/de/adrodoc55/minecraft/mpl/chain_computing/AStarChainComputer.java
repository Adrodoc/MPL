package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class AStarChainComputer implements ChainComputer {

    @Override
    public CommandBlockChain computeOptimalChain(Coordinate3D start, CommandChain input) {

        LinkedList<PathElement> todos = new LinkedList<PathElement>();

        List<Command> commands = input.getCommands();
        todos.add(new PathElement(start, 0, commands));

        int totalChainSize = commands.size();

        while (!todos.isEmpty()) {
            todos.sort(Comparator.naturalOrder());
            PathElement current = todos.poll();
//            System.out.println(current);

            if (current.getPathLength() > totalChainSize) {
                return toCommandBlockChain(input.getName(), current);
            }

            Iterable<PathElement> validContinuations = current.getValidContinuations();

            for (PathElement p : validContinuations) {
                if (containsPath(todos, p)) {
                    continue;
                }
                if (p.getPos().getX() < start.getX()) {
                    continue;
                }
                if (p.getPos().getY() < start.getY()) {
                    continue;
                }
                if (p.getPos().getZ() < start.getZ()) {
                    continue;
                }
                todos.add(p);
            }
        }
        return null;
    }

    private CommandBlockChain toCommandBlockChain(String name, PathElement path) {
        // int[][] matrix = new int[path.getPathLength()][path.getPathLength()];
        // for (PathElement it = path; it != null; it = it.getPrevious()) {
        // Coordinate3D pos = it.getPos();
        // matrix[pos.getY()][pos.getX()]++;
        // }
        // for (int y = 0; y < matrix.length; y++) {
        // int[] row = matrix[y];
        // for (int x = 0; x < row.length; x++) {
        // System.out.print(row[x]);
        // }
        // System.out.println();
        // }
//        System.out.println(path);
        LinkedList<CommandBlock> chain = new LinkedList<CommandBlock>();
        PathElement following = path;
        for (PathElement current = path.getPrevious(); current != null; current = current.getPrevious()) {
            Coordinate3D pos = current.getPos();
            List<Command> commands = current.getCommands();
            int index = current.getIndex();
            if (commands.size() > index) {
                Command command = commands.get(index);
                Direction direction = Direction.valueOf(following.getPos().minus(pos));
                CommandBlock block = new CommandBlock(command, direction, pos);
                chain.push(block);
            }
            following = current;
        }
        return new CommandBlockChain(name, chain);
    }

    private static boolean containsPath(Iterable<PathElement> iterable, PathElement p) {
        for (PathElement it : iterable) {
            if (it.pathEquals(p)) {
                return true;
            }
        }
        return false;
    }

}
