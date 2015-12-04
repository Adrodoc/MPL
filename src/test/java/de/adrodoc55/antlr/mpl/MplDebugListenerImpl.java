package de.adrodoc55.antlr.mpl;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.CommandContext;
import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;

public class MplDebugListenerImpl implements MplListener {

    @Override
    public void visitTerminal(TerminalNode node) {
        System.out.println("visitTerminal(" + node + ")");
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.out.println("visitErrorNode(" + node + ")");
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        System.out.println("enterEveryRule(" + ctx + ")");
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        System.out.println("exitEveryRule(" + ctx + ")");
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        System.out.println("enterProgram(" + ctx + ")");
    }

    @Override
    public void exitProgram(ProgramContext ctx) {
        System.out.println("exitProgram(" + ctx + ")");
    }

    @Override
    public void enterLine(LineContext ctx) {
        System.out.println("enterLine(" + ctx + ")");
    }

    @Override
    public void exitLine(LineContext ctx) {
        System.out.println("exitLine(" + ctx + ")");
    }

    @Override
    public void enterModifierList(ModifierListContext ctx) {
        System.out.println("enterModifierList(" + ctx + ")");
    }

    @Override
    public void exitModifierList(ModifierListContext ctx) {
        System.out.println("exitModifierList(" + ctx + ")");
    }

    @Override
    public void enterModifier(ModifierContext ctx) {
        System.out.println("enterModifier(" + ctx + ")");
    }

    @Override
    public void exitModifier(ModifierContext ctx) {
        System.out.println("exitModifier(" + ctx + ")");
    }

    @Override
    public void enterCommand(CommandContext ctx) {
        System.out.println("enterCommand(" + ctx + ")");
    }

    @Override
    public void exitCommand(CommandContext ctx) {
        System.out.println("exitCommand(" + ctx + ")");
    }

}
