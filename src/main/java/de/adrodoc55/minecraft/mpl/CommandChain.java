package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;
import java.util.List;

public class CommandChain {

  private final String name;
  private final List<Command> commands;

  public CommandChain() {
    this(null);
  }

  public CommandChain(String name) {
    this(name, new LinkedList<Command>());
  }

  public CommandChain(String name, List<Command> commands) {
    this.name = name;
    this.commands = commands;
  }

  public String getName() {
    return name;
  }

  public List<Command> getCommands() {
    return commands;
  }

}
