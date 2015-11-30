package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class AStarChainComputer implements ChainComputer {

	@Override
	public CommandBlockChain computeOptimalChain(Coordinate3D start,
			CommandChain input) {

		LinkedList<PathElement> todos = new LinkedList<PathElement>();

		List<Command> commands = input.getCommands();
		todos.add(new PathElement(start, 0, commands));

		int totalChainSize = commands.size();

		while (!todos.isEmpty()) {
			todos.sort(Comparator.naturalOrder());
			PathElement current = todos.poll();
//			System.out.println(current);
			Iterable<PathElement> validContinuations = current
					.getValidContinuations();

			if (current.getPathLength() >= totalChainSize) {
				return toCommandBlockChain(current);
			}

			for (PathElement p : validContinuations) {
				if (containsPath(todos, p)) {
					continue;
				}
				if (p.getPos().getX() < 0) {
					continue;
				}
				if (p.getPos().getY() < 0) {
					continue;
				}
				if (p.getPos().getZ() != 0) {
					continue;
				}
				todos.add(p);
			}
		}
		return null;
	}

	private CommandBlockChain toCommandBlockChain(PathElement path) {
//		int[][] matrix = new int[path.getPathLength()][path.getPathLength()];
//		for (PathElement it = path; it != null; it = it.getPrevious()) {
//			Coordinate3D pos = it.getPos();
//			matrix[pos.getY()][pos.getX()]++;
//		}
//		for (int y = 0; y < matrix.length; y++) {
//			int[] row = matrix[y];
//			for (int x = 0; x < row.length; x++) {
//				System.out.print(row[x]);
//			}
//			System.out.println();
//		}
		System.out.println(path);
		return null;
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
