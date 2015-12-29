package de.adrodoc55.minecraft.mpl.antlr;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;

class CommandBuffer {
    private String command;
    private Mode mode;
    private Boolean conditional;
    private Boolean needsRedstone;

    public Command toCommand() {
        return new Command(command, mode, conditional, needsRedstone);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setConditional(Boolean conditional) {
        this.conditional = conditional;
    }

    public void setNeedsRedstone(Boolean needsRedstone) {
        this.needsRedstone = needsRedstone;
    }
}