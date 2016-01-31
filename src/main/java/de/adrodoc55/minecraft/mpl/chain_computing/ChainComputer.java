package de.adrodoc55.minecraft.mpl.chain_computing;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public interface ChainComputer {

  public default CommandBlockChain computeOptimalChain(CommandChain input) {
    return computeOptimalChain(input,
        new Coordinate3D(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
  }

  public abstract CommandBlockChain computeOptimalChain(CommandChain input, Coordinate3D max);

  public default CommandBlockChain toCommandBlockChain(CommandChain input,
      List<Coordinate3D> coordinates) {
    List<Command> commands = input.getCommands();
    if (commands.size() >= coordinates.size()) {
      throw new IllegalArgumentException(
          "To generate a CommandBlockChain one additional Coordinate is needed!");
    }
    List<CommandBlock> commandBlocks = new ArrayList<CommandBlock>(commands.size());
    for (int a = 0; a < commands.size(); a++) {
      Command currentCommand = commands.get(a);
      Coordinate3D currentCoordinate = coordinates.get(a);
      if (currentCommand == null) {
        commandBlocks.add(new CommandBlock(null, null, currentCoordinate));
        continue;
      }

      Coordinate3D nextCoordinate = coordinates.get(a + 1);
      Coordinate3D directionalCoordinate = nextCoordinate.minus(currentCoordinate);
      Direction direction = Direction.valueOf(directionalCoordinate);

      CommandBlock currentCommandBlock =
          new CommandBlock(currentCommand, direction, currentCoordinate);
      commandBlocks.add(currentCommandBlock);
    }
    CommandBlockChain output = new CommandBlockChain(input.getName(), commandBlocks);
    return output;
  }

}
