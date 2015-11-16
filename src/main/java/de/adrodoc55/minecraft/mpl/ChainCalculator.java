package de.adrodoc55.minecraft.mpl;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;

public interface ChainCalculator {

	CommandBlockChain calculateOptimalChain(Coordinate3D start,
			CommandChain input);

	public default CommandBlockChain toCommandBlockChain(CommandChain input,
			List<Coordinate3D> coordinates) {
		List<Command> commands = input.getCommands();
		if (commands.size() >= coordinates.size()) {
			throw new IllegalArgumentException(
					"To generate a CommandBlockChain one additional Coordinate is needed!");
		}
		List<CommandBlock> commandBlocks = new ArrayList<CommandBlock>(
				commands.size());
		for (int a = 0; a < commands.size(); a++) {
			Command currentCommand = commands.get(a);
			Coordinate3D currentCoordinate = coordinates.get(a);
			if (currentCommand == null) {
				commandBlocks.add(new CommandBlock(null, null,
						currentCoordinate));
				continue;
			}

			Coordinate3D nextCoordinate = coordinates.get(a + 1);
			Coordinate3D directionalCoordinate = nextCoordinate
					.minus(currentCoordinate);
			Direction direction = Direction.valueOf(directionalCoordinate);

			CommandBlock currentCommandBlock = new CommandBlock(currentCommand,
					direction, currentCoordinate);
			commandBlocks.add(currentCommandBlock);
		}
		CommandBlockChain output = new CommandBlockChain(input.getName(),
				commandBlocks);
		return output;
	}

}
