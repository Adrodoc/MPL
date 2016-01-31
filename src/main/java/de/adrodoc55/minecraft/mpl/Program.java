package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;

import de.adrodoc55.minecraft.Coordinate3D;

public class Program {

  private LinkedList<CommandChain> chains;
  private LinkedList<Command> installation;
  private LinkedList<Command> uninstallation;

  // Compiler-Options
  private Coordinate3D max;
  private String prefix;
  private MplOrientation orientation;

  public Program() {
    chains = new LinkedList<CommandChain>();
    installation = new LinkedList<Command>();
    uninstallation = new LinkedList<Command>();
  }

  public LinkedList<CommandChain> getChains() {
    return chains;
  }

  public void setChains(LinkedList<CommandChain> chains) {
    this.chains = chains;
  }

  public LinkedList<Command> getInstallation() {
    return installation;
  }

  public void setInstallation(LinkedList<Command> installation) {
    this.installation = installation;
  }

  public LinkedList<Command> getUninstallation() {
    return uninstallation;
  }

  public void setUninstallation(LinkedList<Command> uninstallation) {
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
