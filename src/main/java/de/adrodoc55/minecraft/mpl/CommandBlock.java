package de.adrodoc55.minecraft.mpl;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.Coordinate3D.Direction;

public class CommandBlock {

    private final Command command;
    private final Direction direction;
    private final Coordinate3D coordinate;

    public CommandBlock(Command command, Direction direction, Coordinate3D coordinate) {
        this.command = command;
        this.direction = direction;
        this.coordinate = coordinate;
    }

    public Command getCommand() {
        return command;
    }

    public Coordinate3D getCoordinate() {
        return coordinate;
    }

    public String toPython() {
        String x = "box.minx + " + coordinate.getX();
        String y = "box.miny + " + coordinate.getY();
        String z = "box.minz + " + coordinate.getZ();
        if (command == null) {
            return "level.setBlockAt(" + x + ", " + y + ", " + z + ", 1)";
        } else {
            String xyz = "(" + x + ", " + y + ", " + z + ")";
            String command = this.command.getCommand();
            String direction = this.direction.toString().toLowerCase();
            String mode = this.command.getMode().toString().toLowerCase();
            String conditional = this.command.isConditional() ? "True" : "False";
            String auto = this.command.needsRedstone() ? "False" : "True";
            return "create_command_block(level, " + xyz + ", '" + command + "', '" + direction + "', '" + mode + "', "
                    + conditional + ", " + auto + ")";
        }
    }

}
