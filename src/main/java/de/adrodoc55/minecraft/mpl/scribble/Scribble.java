package de.adrodoc55.minecraft.mpl.scribble;

import java.io.IOException;

import de.adrodoc55.minecraft.mpl.Main;

public class Scribble {

    public static void main(String[] args) throws IOException {
        // File programFile = new File(
        // "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_createPortal.txt");
        // Program compile = MplCompiler.compile(programFile);
        // CommandChain chain = compile.getChains().get(0);
        // List<Command> commands = chain.getCommands();
        // StringBuilder sb = new StringBuilder();
        // for (Command c : commands) {
        // if (c == null) {
        // sb.append('1');
        // } else {
        // sb.append((c.isConditional() ? '0' : '1'));
        // }
        // }
        // System.out.println(sb.toString());
        String inputFile = "C:/Users/Adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ACV_velocity.txt";
        String outputDir = "C:/Users/Adrian/Minecraft/MCEdit.v1.4.0.1.Win.64bit/stock-filters";
        String[] arg = { inputFile, outputDir };
        Main.main(arg);
    }

}
