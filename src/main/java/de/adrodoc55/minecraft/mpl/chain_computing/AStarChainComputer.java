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
    public CommandBlockChain computeOptimalChain(CommandChain chain) {
        Coordinate3D min = chain.getMin();
        Coordinate3D max = chain.getMax();

        LinkedList<PathElement> todos = new LinkedList<PathElement>();

        List<Command> commands = chain.getCommands();
        todos.add(new PathElement(min, 0, commands));

        int totalChainSize = commands.size();
        // int count = 0;
        while (!todos.isEmpty()) {
            todos.sort(Comparator.naturalOrder());
            PathElement current = todos.poll();
            // System.out.println(current);
            // count++;
            // System.out.println(count);

            if (current.getPathLength() > totalChainSize) {
                return toCommandBlockChain(chain.getName(), current);
            }

            if (!current.hasEnoughSpace()) {
                continue;
            }

            Iterable<PathElement> validContinuations = current
                    .getValidContinuations();
            for (PathElement p : validContinuations) {
                if (containsPath(todos, p)) {
                    continue;
                }
                Coordinate3D pos = p.getPos();
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                if (x < min.getX() || y < min.getY() || z < min.getZ()
                        || x > max.getX() || y > max.getY() || z > max.getZ()) {
                    continue;
                }
                todos.add(p);
            }
        }
        return null;
    }

    private CommandBlockChain toCommandBlockChain(String name, PathElement path) {
        LinkedList<CommandBlock> chain = new LinkedList<CommandBlock>();
        PathElement following = path;
        for (PathElement current = path.getPrevious(); current != null; current = current
                .getPrevious()) {
            Coordinate3D pos = current.getPos();
            List<Command> commands = current.getCommands();
            int index = current.getIndex();
            if (commands.size() > index) {
                Command command = commands.get(index);
                Direction direction = Direction.valueOf(following.getPos()
                        .minus(pos));
                CommandBlock block = new CommandBlock(command, direction, pos);
                chain.push(block);
            }
            following = current;
        }
        return new CommandBlockChain(name, chain);
    }

    private static boolean containsPath(Iterable<PathElement> iterable,
            PathElement p) {
        for (PathElement it : iterable) {
            if (it.pathEquals(p)) {
                return true;
            }
        }
        return false;
    }

}
