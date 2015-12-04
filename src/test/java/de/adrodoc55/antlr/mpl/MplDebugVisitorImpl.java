package de.adrodoc55.antlr.mpl;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.CommandContext;
import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;

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
    public Void visitModifier(ModifierContext ctx) {
        System.out.println("visitModifier(" + ctx + ")");
        return null;
    }

    @Override
    public Void visitCommand(CommandContext ctx) {
        System.out.println("visitCommand(" + ctx + ")");
        return null;
    }

}
