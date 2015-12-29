package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;
import java.util.List;

import de.adrodoc55.minecraft.Coordinate3D;

public class Program {

    private LinkedList<CommandChain> chains;
    private List<Command> installation;
    private List<Command> uninstallation;

    // Compiler-Options
    private Coordinate3D max;
    private String prefix;
    private MplOrientation orientation;

    public Program() {
    }

    public LinkedList<CommandChain> getChains() {
        return chains;
    }

    public void setChains(LinkedList<CommandChain> chains) {
        this.chains = chains;
    }

    public List<Command> getInstallation() {
        return installation;
    }

    public void setInstallation(List<Command> installation) {
        this.installation = installation;
    }

    public List<Command> getUninstallation() {
        return uninstallation;
    }

    public void setUninstallation(List<Command> uninstallation) {
        this.uninstallation = uninstallation;
    }

    public Coordinate3D getMax() {
        return max;
    }

    public void setMax(Coordinate3D max) {
        this.max = max;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public MplOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(MplOrientation orientation) {
        this.orientation = orientation;
    }

}
