package de.adrodoc55.antlr.mpl;

import org.antlr.v4.runtime.tree.TerminalNode;

public class MplLexerUtils {

    private MplLexerUtils() throws Exception {
        throw new Exception("Utils Classes cannot be instantiated");
    }

    public static String getContainedString(TerminalNode stringNode) {
        if (stringNode.getSymbol().getType() != MplLexer.STRING) {
            throw new IllegalArgumentException(
                    "The Given TerminalNode is not of type MplLexer.STRING!");
        }
        String wholeString = stringNode.getText();
        String containedString = wholeString.substring(1,
                wholeString.length() - 1);
        return containedString;
    }

}
