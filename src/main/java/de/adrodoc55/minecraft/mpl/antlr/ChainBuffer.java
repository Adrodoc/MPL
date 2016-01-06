package de.adrodoc55.minecraft.mpl.antlr;

import java.util.LinkedList;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;

class ChainBuffer {

    private String name;
    private boolean install;
    private boolean uninstall;
    private boolean process;
    private boolean script;
    private boolean repeatingProcess;
    private boolean repeatingContext;
    private final LinkedList<Command> commands = new LinkedList<Command>();

    public boolean add(Command e) {
        if (e != null) {
            if (e.getMode() == Mode.IMPULSE) {
                setRepeatingContext(false);
            } else if (e.getMode() == Mode.REPEAT) {
                setRepeatingContext(true);
            }
        }
        return commands.add(e);
    }

    /**
     * Returns the name of the current Context. The name is defined by the
     * identifier if this is a process and by the name of the file if this is a
     * project. If this is a script this method will throw an
     * {@link IllegalStateException}
     *
     * @return name
     * @throws IllegalStateException
     */
    public String getName() throws IllegalStateException {
        if (isProcess()) {
            if (name != null) {
                return name;
            } else {
                throw new IllegalStateException(
                        "The name of this process has not been set.");
            }
        } else {
            throw new IllegalStateException("This chain is not a process.");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    public boolean isUninstall() {
        return uninstall;
    }

    public void setUninstall(boolean uninstall) {
        this.uninstall = uninstall;
    }

    public boolean isProcess() {
        return process;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }

    public boolean isScript() {
        return script;
    }

    public void setScript(boolean script) {
        this.script = script;
    }

    public boolean isRepeatingProcess() {
        return repeatingProcess;
    }

    public void setRepeatingProcess(boolean repeatingProcess) {
        this.repeatingProcess = repeatingProcess;
    }

    public boolean isRepeatingContext() {
        return repeatingContext;
    }

    public void setRepeatingContext(boolean repeatingContext) {
        this.repeatingContext = repeatingContext;
    }

    public LinkedList<Command> getCommands() {
        return commands;
    }

}
