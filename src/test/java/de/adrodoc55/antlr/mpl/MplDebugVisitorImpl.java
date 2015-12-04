package de.adrodoc55.antlr.mpl;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ModusContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;

//public (.+) (.+)\((.+) (.+)\) \{[^\}]*\}
//public $1 $2($3 $4) {System.out.println("$2(" + $4 + ")");return null;}
public class MplDebugVisitorImpl implements MplVisitor<Void> {

    @Override
    public Void visit(ParseTree tree) {
        System.out.println("visit(" + tree + ")");
        return null;
    }

    @Override
    public Void visitChildren(RuleNode node) {
        System.out.println("visitChildren(" + node + ")");
        return null;
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        System.out.println("visitTerminal(" + node + ")");
        return null;
    }

    @Override
    public Void visitErrorNode(ErrorNode node) {
        System.out.println("visitErrorNode(" + node + ")");
        return null;
    }

    @Override
    public Void visitProgram(ProgramContext ctx) {
        System.out.println("visitProgram(" + ctx + ")");
        return null;
    }

    @Override
    public Void visitLine(LineContext ctx) {
        System.out.println("visitLine(" + ctx + ")");
        return null;
    }

    @Override
    public Void visitModifierList(ModifierListContext ctx) {
        System.out.println("visitModifierList(" + ctx + ")");
        return null;
    }

    @Override
    public Void visitModus(ModusContext ctx) {
        System.out.println("visitModus(" + ctx + ")");
        return null;
    }

}
