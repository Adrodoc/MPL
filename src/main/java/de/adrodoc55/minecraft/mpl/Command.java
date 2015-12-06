package de.adrodoc55.minecraft.mpl;

import net.karneim.pojobuilder.Builder;
import net.karneim.pojobuilder.GeneratePojoBuilder;

public class Command {

    private String command;
    private Mode mode;
    private boolean conditional;
    private boolean needsRedstone;

    public Command() {
        this(null);
    }

    public Command(String command) {
        this(command, null);
    }

    public Command(String command, Boolean conditional) {
        this(command, null, conditional);
    }

    public Command(String command, Mode mode, Boolean conditional) {
        this(command, mode, conditional, null);
    }

    @GeneratePojoBuilder(withBuilderInterface = Builder.class)
    public Command(String command, Mode mode, Boolean conditional,
            Boolean needsRedstone) {
        this.command = command;

        this.conditional = (conditional != null) ? conditional : false;
        this.mode = (mode != null) ? mode : Mode.CHAIN;

        if (needsRedstone != null) {
            this.needsRedstone = needsRedstone;
        } else {
            this.needsRedstone = (this.mode == Mode.CHAIN) ? false : true;
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public static enum Mode {
        IMPULSE, CHAIN, REPEAT;
    }

    public boolean isConditional() {
        return conditional;
    }

    public void setConditional(boolean conditional) {
        this.conditional = conditional;
    }

    public boolean needsRedstone() {
        return needsRedstone;
    }

    public void setNeedsRedstone(boolean needsRedstone) {
        this.needsRedstone = needsRedstone;
    }

    @Override
    public String toString() {
        return "Command [command='" + command + "', mode=" + mode
                + ", conditional=" + conditional + ", needsRedstone="
                + needsRedstone + "]";
    }

}
