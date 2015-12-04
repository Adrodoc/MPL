package de.adrodoc55.antlr.mpl;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ModusContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;

public class AntlrTest {
    public static void main(String[] args) throws RecognitionException {
        String expression = "chain   , conditional : /ger eaf H 0 = we98 elloword\n";
        ANTLRInputStream input = new ANTLRInputStream(expression);
        MplLexer lexer = new MplLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        MplParser parser = new MplParser(tokens);
//        parser.addParseListener(new MplDebugListenerImpl());

        ProgramContext program = parser.program();

        List<Command> commands = new LinkedList<Command>();

        List<LineContext> line = program.line();
        for (LineContext lineContext : line) {
            TerminalNode command = lineContext.COMMAND();
            if (command == null) {
                continue;
            }
            ModifierListContext modifierList = lineContext.modifierList();
            if (modifierList == null) {
                commands.add(new Command(command.getText()));
                continue;
            }
            ModusContext modus = modifierList.modus();
            Mode mode = Mode.CHAIN;
            if (modus != null) {
                String modusText = modus.getText();
                mode = Mode.valueOf(modusText.toUpperCase());
            }
            boolean conditional = false;
            if (modifierList.CONDITIONAL() != null) {
                conditional = true;
            }
            boolean needsRedstone = (mode != Mode.CHAIN);
            if (modifierList.NEEDS_REDSTONE() != null) {
                needsRedstone = true;
            }
            commands.add(new Command(command.getText(), conditional, mode,
                    needsRedstone));
            continue;
        }

        System.out.println("----------------------------------");

        CommandChain c = new CommandChain("test", commands);

        for (Command command : c.getCommands()) {
            System.out.println(command);
        }

        // List<? extends Object> children = ast.toStringTree()
        // for (Object o : children) {
        // CommonTree tree = o;
        // for(Object c : tree.) {
        // System.out.println(o.getClass());
        // System.out.println(o);
        // }
        // System.out.println(o.getClass());
        // System.out.println(o);
        // }

        // System.out.println(ast.getText());
    }
}
