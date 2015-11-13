package de.adrodoc55.minecraft.mpl;

public class Command {

    private String command;
    private final boolean conditional;
    private final Mode mode;
    private final boolean needsRedstone;

    public Command(String command) {
        this(command, false);
    }

    public Command(String command, boolean conditional) {
        this(command, conditional, Mode.CHAIN);
    }

    public Command(String command, boolean conditional, Mode mode) {
        this(command, conditional, mode, mode == Mode.CHAIN ? false : true);
    }

    public Command(String command, boolean conditional, Mode mode, boolean needsRedstone) {
        this.command = command;
        this.conditional = conditional;
        this.mode = mode;
        this.needsRedstone = needsRedstone;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isConditional() {
        return conditional;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean needsRedstone() {
        return needsRedstone;
    }

    public static enum Mode {
        IMPULSE, CHAIN, REPEAT;
    }

}
