package de.adrodoc55.minecraft.mpl;

import de.adrodoc55.minecraft.Coordinate3D.Direction;
import de.adrodoc55.minecraft.mpl.Command.Mode;

public abstract class MplConverter {

  protected static String toBlockId(Mode mode) {
    if (mode == null) {
      throw new NullPointerException("mode == null");
    }
    switch (mode) {
      case IMPULSE:
        return "command_block";
      case CHAIN:
        return "chain_command_block";
      case REPEAT:
        return "repeating_command_block";
    }
    throw new IllegalArgumentException("Unknown Mode: " + mode);
  }

  protected static int toIntBlockId(Mode mode) {
    if (mode == null) {
      throw new NullPointerException("mode == null");
    }
    switch (mode) {
      case IMPULSE:
        return 137;
      case CHAIN:
        return 211;
      case REPEAT:
        return 210;
    }
    throw new IllegalArgumentException("Unknown Mode: " + mode);
  }

  protected static int toDamageValue(CommandBlock block) {
    int damage = toDamageValue(block.getDirection());
    if (block.isConditional()) {
      damage += 8;
    }
    return damage;
  }

  private static int toDamageValue(Direction direction) {
    if (direction == null) {
      throw new NullPointerException("mode == null");
    }
    switch (direction) {
      case DOWN:
        return 0;
      case UP:
        return 1;
      case NORTH:
        return 2;
      case SOUTH:
        return 3;
      case WEST:
        return 4;
      case EAST:
        return 5;
    }
    throw new IllegalArgumentException("Unknown Direction: " + direction);
  }
}
