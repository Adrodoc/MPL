package de.adrodoc55.minecraft.mpl.commands.chainlinks;

public class CommandReferencingCommand extends InternalCommand {
  private final Command referenced;

  public CommandReferencingCommand(Command referenced,boolean success) {
    this.referenced = referenced;
  }

  public Command getReferenced() {
    return referenced;
  }
}
