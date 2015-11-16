package de.adrodoc55.minecraft.mpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.gui.ChainRenderer;

public class IterativeChainCalculator implements ChainCalculator {

	// public static void main(String[] args) {

	// List<Command> commands = new ArrayList<Command>();
	// commands.add($some($Command()));
	// commands.add($some($Command()));
	// File programFile = new File(
	// "C:/Users/Adrian/Downloads/TurretCommands.txt");
	// File programFile = new File(
	// "C:/Users/Adrian/Programme/workspace/MplGenerator/src/main/resources/testdata.txt");
	// Program p = new Program(programFile);
	//
	// List<Command> commands = p.getChains().values().iterator().next()
	// .getCommands();
	//
	// JFrame frame = new JFrame("Iterativ");
	// ChainRenderer renderer = new ChainRenderer(commands);
	// frame.getContentPane().add(renderer, BorderLayout.CENTER);
	// ChainRenderer optimalRenderer = new ChainRenderer(commands);
	// frame.getContentPane().add(optimalRenderer, BorderLayout.EAST);
	// frame.pack();
	// frame.setLocationRelativeTo(null);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.setVisible(true);
	//
	// IterativeChainCalculator calculator = new IterativeChainCalculator(
	// new Coordinate3D(), new Coordinate3D(0, 100, 100));
	//
	// calculator.renderer = renderer;
	// calculator.optimalRenderer = optimalRenderer;
	//
	// CommandChain chain = new CommandChain(programFile.getName(), commands);
	// long start = System.currentTimeMillis();
	// calculator.calculateOptimalChain(new Coordinate3D(), chain);
	// long stop = System.currentTimeMillis();
	// long time = stop - start;
	// long millis = time % 1000;
	// time /= 1000;
	// long sec = time % 60;
	// time /= 60;
	// long min = time % 60;
	// time /= 60;
	//
	// System.out.println("Min: " + min + ", Sec: " + sec + ", millis: "
	// + millis);
	// }

	private Coordinate3D start;
	private final Coordinate3D max;
	private ChainRenderer renderer;
	private ChainRenderer optimalRenderer;

	public IterativeChainCalculator() {
		this(new Coordinate3D(Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MAX_VALUE));
	}

	public IterativeChainCalculator(Coordinate3D max) {
		this.max = max;
	}

	private int optimalScore;
	private List<Command> commands;
	private List<Coordinate3D> path;
	private int i;
	private Command currentCommand;
	private Coordinate3D currentCoordinate;

	private static final int MAX_TRIES = 1000000;

	public CommandBlockChain calculateOptimalChain(Coordinate3D start,
			CommandChain input) {
		this.start = start;
		optimalScore = Integer.MAX_VALUE;
		this.commands = input.getCommands();
		List<Coordinate3D>[] todos = new List[commands.size() + 1];
		path = new ArrayList<Coordinate3D>(commands.size() + 1);
		path.add(start);
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
				if (renderer != null) {
					renderer.render(path);
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
		CommandBlockChain output = toCommandBlockChain(input, optimal);
		return output;
	}

	private List<Coordinate3D> getNextCoordinates() {
		Direction[] directions;
		currentCommand = commands.get(i);
		if (currentCommand != null && currentCommand.isConditional()) {
			if (path.size() < 2) {
				throw new IllegalStateException(
						"Der Erste Befehl kann nicht conditional sein!");
			}
			Coordinate3D previousCoordinate = path.get(i - 1);
			Direction direction = Direction.valueOf(currentCoordinate
					.minus(previousCoordinate));
			directions = new Direction[] { direction };
		} else {
			directions = getDirections();
		}
		List<Coordinate3D> coords = getAdjacentCoordinates(currentCoordinate,
				directions);
		return coords;
	}

	private static List<Coordinate3D> getAdjacentCoordinates(
			Coordinate3D coordinate, Direction[] directions) {
		List<Coordinate3D> coords = new ArrayList<Coordinate3D>(
				directions.length);
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
			List<Coordinate3D> adjacentCoordinates = getAdjacentCoordinates(
					coordinate, getDirections());
			for (Coordinate3D a : adjacentCoordinates) {
				if (isCoordinateValid(a) && !validCoordinates.contains(a)) {
					validCoordinates.add(a);
					todos.add(a);
				}
			}
		}

		return true;
	}

	private boolean isCoordinateValid(Coordinate3D coordinate) {
		if (i != path.indexOf(coordinate)) {
			return false;
		}

		int x = coordinate.getX();
		int y = coordinate.getY();
		int z = coordinate.getZ();
		if (x < start.getX() || y < start.getY() || z < start.getZ()
				|| x > max.getX() || y > max.getY() || z > max.getZ()) {
			return false;
		}

		if (optimalScore <= calculateScore(path)) {
			return false;
		}
		return true;
	}

	// private static List<Direction> directions;

	// private static List<Direction> getDirections() {
	// // if (directions == null) {
	// List<Direction> directions = new ArrayList<Direction>(6);
	// // directions.add(Direction.WEST);
	// // directions.add(Direction.EAST);
	// directions.add(Direction.DOWN);
	// directions.add(Direction.UP);
	// directions.add(Direction.NORTH);
	// directions.add(Direction.SOUTH);
	// // }
	// return directions;
	// }

	// private static int directionCounter = -1;

	private static Direction[] getDirections() {
		// directionCounter++;
		// directionCounter %= 4;
		Direction[] directions;
		// if (directionCounter == 0) {
		directions = new Direction[] { Direction.SOUTH, Direction.UP,
				Direction.NORTH, Direction.DOWN };
		// } else if (directionCounter == 1) {
		// directions = new Direction[] { Direction.UP, Direction.NORTH,
		// Direction.DOWN, Direction.SOUTH };
		// } else if (directionCounter == 2) {
		// directions = new Direction[] { Direction.NORTH, Direction.DOWN,
		// Direction.SOUTH, Direction.UP };
		// } else if (directionCounter == 3) {
		// directions = new Direction[] { Direction.DOWN, Direction.SOUTH,
		// Direction.UP, Direction.NORTH };
		// } else {
		// throw new RuntimeException();
		// }
		return directions;
	}

	private final List<Coordinate3D> optimal = new ArrayList<Coordinate3D>();

	private void registerPath(List<Coordinate3D> path) {
		int score = calculateScore(path);
		if (score < optimalScore) {
			optimal.clear();
			optimal.addAll(path);
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
