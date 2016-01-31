package de.adrodoc55.minecraft.mpl.scribble;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.antlr.MplInterpreter;

public class ChainComputingScribble {
  public static void main(String[] args) throws IOException {
    File file = new File(
        "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_validateDirections.txt");
    MplInterpreter interpreter = MplInterpreter.interpret(file);
    List<Command> commands = interpreter.getChains().get(0).getCommands();
    StringBuilder sb = new StringBuilder("TRANSMITTER");
    for (Command command : commands) {
      sb.append(", ");
      if (command == null) {
        sb.append("TRANSMITTER");
      } else if (command.getMode() != Mode.CHAIN) {
        sb.append("RECEIVER");
      } else if (command.isConditional()) {
        sb.append("CONDITIONAL");
      } else {
        sb.append("NORMAL");
      }
    }
    System.out.println(sb.toString());
  }
}
