package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import java.util.Iterator;
import java.util.List;

public class CommandReferencingCommand extends InternalCommand {
  private final Command referenced;
  private final boolean success;

  public CommandReferencingCommand(Command referenced, boolean success) {
    this.referenced = referenced;
    this.success = success;
  }

  public Command getReferenced() {
    return referenced;
  }

  public ChainLink resolve(List<ChainLink> chainLinks) {
    int self = -1;
    int ref = -1;
    int i = 0;
    for (Iterator<ChainLink> it = chainLinks.iterator(); it.hasNext(); i++) {
      ChainLink chainLink = it.next();
      if (this == chainLink) {
        self = i;
      }
      if (referenced == chainLink) {
        ref = i;
      }
    }
    if (self == -1) {
      throwNotFoundException("This");
    }
    if (ref == -1) {
      throwNotFoundException("The referenced command");
    }
    ReferencingTestforSuccessCommand result =
        new ReferencingTestforSuccessCommand(ref - self, referenced.getMode(), success);
    result.setConditional(conditional);
    return result;
  }

  private void throwNotFoundException(String string) throws IllegalArgumentException {
    throw new IllegalArgumentException(
        "Failed to resolve reference. " + string + " was not found in the specified chainLinks");
  }
}
