package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import de.adrodoc55.minecraft.mpl.commands.Mode;

public class ReferencingTestforSuccessCommand extends ReferencingCommand {

  public ReferencingTestforSuccessCommand(int relative, Mode previousMode, boolean success) {
    this(relative, previousMode, success, false);
  }

  public ReferencingTestforSuccessCommand(int relative, Mode previousMode, boolean success,
      boolean conditional) {
    super(constructCommand(previousMode, success), conditional);
    this.relative = relative;
  }

  public static String constructCommand(Mode previousMode, boolean success) {
    return "testforblock " + REF + " " + previousMode.getStringBlockId() + " -1 {SuccessCount:"
        + (success ? 1 : 0) + "}";
  }
}
