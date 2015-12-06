package de.adrodoc55.minecraft.mpl.scribble;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.RecognitionException;

import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.Program;
import de.adrodoc55.minecraft.mpl.antlr.MplCompiler;

public class AntlrTest {
    public static void main(String[] args) throws RecognitionException,
            IOException {
        // String expression = "chain , conditional : /ger eaf H 0 = we98
        // elloword\n";
        // ANTLRInputStream input = new ANTLRInputStream(expression);
        File file = new File(
                "C:/Users/adrian/Programme/workspace/ApertureCraftVanilla/src/main/minecraft/ApertureCraft Vanilla.txt");
        // ANTLRInputStream input = new ANTLRInputStream(
        // Files.newBufferedReader(file.toPath()));
        // MplLexer lexer = new MplLexer(input);
        // TokenStream tokens = new CommonTokenStream(lexer);
        // MplParser parser = new MplParser(tokens);
        //
        // ProgramContext program = parser.program();
        //
        // ParseTreeWalker walker = new ParseTreeWalker();
        // MplDebugListenerImpl mplDebugListenerImpl = new
        // MplDebugListenerImpl();
        // walker.walk(mplDebugListenerImpl, program);

        Program compiled = MplCompiler.compile(file);
        List<CommandChain> chains = compiled.getChains();
        for (CommandChain chain : chains) {
            System.out
                    .println("--------------------------------------------------");
            System.out.println("At: " + chain.getMin());
            System.out.println("Name: " + chain.getName());
            for (Command command : chain.getCommands()) {
                System.out.println(command);
            }
        }
    }

}
