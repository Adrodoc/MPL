package de.adrodoc55.minecraft.mpl;

import java.util.List;

public class Chain {

    private final String name;
    private final List<Command> commands;

    public Chain(String name, List<Command> commands) {
        this.name = name;
        this.commands = commands;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public List<Command> getCommands() {
        return commands;
    }

}
