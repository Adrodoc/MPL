package de.adrodoc55.antlr.mpl;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.antlr.mpl.MplParser.LineContext;
import de.adrodoc55.antlr.mpl.MplParser.ModifierListContext;
import de.adrodoc55.antlr.mpl.MplParser.ModusContext;
import de.adrodoc55.antlr.mpl.MplParser.ProgramContext;

//public (.+) (.+)\((.+) (.+)\) \{[^\}]*\}
//public $1 $2($3 $4) {System.out.println("$2(" + $4 + ")");}
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
    public void enterModus(ModusContext ctx) {
        System.out.println("enterModus(" + ctx + ")");
    }

    @Override
    public void exitModus(ModusContext ctx) {
        System.out.println("exitModus(" + ctx + ")");
    }

}
