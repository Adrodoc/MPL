package de.adrodoc55.minecraft.mpl;

import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class CommandChain {

    private final String name;
    private final List<Command> commands;
    private Coordinate3D min;
    private Coordinate3D max;

    public CommandChain(String name, List<Command> commands) {
        this(name, commands, new Coordinate3D(), new Coordinate3D(
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    public CommandChain(String name, List<Command> commands, Coordinate3D min,
            Coordinate3D max) {
        super();
        this.name = name;
        this.commands = commands;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Coordinate3D getMin() {
        return min;
    }

    public void setMin(Coordinate3D min) {
        this.min = min;
    }

    public Coordinate3D getMax() {
        return max;
    }

    public void setMax(Coordinate3D max) {
        this.max = max;
    }

}
