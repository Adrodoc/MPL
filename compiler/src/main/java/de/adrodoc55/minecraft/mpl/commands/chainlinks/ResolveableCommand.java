package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import de.adrodoc55.commons.CopyScope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveableCommand extends InternalCommand {
  private @Nullable ChainLink referenced;

  public ResolveableCommand(String command) {
    super(command);
  }

  public ResolveableCommand(String command, boolean conditional) {
    super(command, conditional);
  }

  public ResolveableCommand(String command, boolean conditional, ChainLink referenced) {
    super(command, conditional);
    this.referenced = referenced;
  }

  public ResolveableCommand(String command, ChainLink referenced) {
    this(command);
    this.referenced = referenced;
  }

  @Deprecated
  protected ResolveableCommand(ResolveableCommand original) {
    super(original);
    // Don't copy reference to preserve the same instance
    referenced = original.referenced;
  }

  @Deprecated
  @Override
  public ResolveableCommand createFlatCopy(CopyScope scope) throws NullPointerException {
    return new ResolveableCommand(this);
  }

  public ReferencingCommand resolve(List<ChainLink> chainLinks) {
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
    return new ReferencingCommand(getCommand(), this, ref - self);
  }

  private void throwNotFoundException(String string) throws IllegalArgumentException {
    throw new IllegalArgumentException(
        "Failed to resolve reference. " + string + " was not found in the specified chainLinks");
  }
}
