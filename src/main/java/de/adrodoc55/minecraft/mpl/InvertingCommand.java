package de.adrodoc55.minecraft.mpl;

public class InvertingCommand extends Command {

  /**
   * Constructs a Command, wich's success is always the opposite of the given command, if the
   * constructed command is placed directly after the given command.
   *
   * @param previous
   */
  public InvertingCommand(Command previous) {
    this(previous.getMode());
  }

  /**
   * Constructs a Command, wich's success is always the opposite of the previous command, if the
   * previous command has the given mode.
   *
   * @param previous
   */
  public InvertingCommand(Mode previousMode) {
    super(getInvert(previousMode));
  }

  private static String getInvert(Mode previousMode) {
    String blockId = MplConverter.toBlockId(previousMode);
    return "/testforblock ${this - 1} " + blockId + " -1 {SuccessCount:0}";
  }

}
