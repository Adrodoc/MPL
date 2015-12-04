package de.adrodoc55.antlr.mpl;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;

public class AntlrTest {
    public static void main(String[] args) throws RecognitionException {
        String expression = "repeat   , chain : /ger eaf H 0 = we98 elloword\n";
        ANTLRInputStream input = new ANTLRInputStream(expression);
        MplLexer lexer = new MplLexer(input);
        // while (true) {
        // Token token = lexer.nextToken();
        // if (token.getType() == Token.EOF) {
        // break;
        // }
        //
        // System.out.println("Token: ‘" + token.getText() + "’");
        // }
        TokenStream tokens = new CommonTokenStream(lexer);
        MplParser parser = new MplParser(tokens);
        parser.addParseListener(new MplDebugListenerImpl());
        ProgramContext program = parser.program();
        List<LineContext> line = program.line();
        for (LineContext lineContext : line) {
            TerminalNode command = lineContext.COMMAND();
            if (command == null) {
                continue;
            }
            command.getText();
        }
        System.out.println(program.toStringTree());

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
