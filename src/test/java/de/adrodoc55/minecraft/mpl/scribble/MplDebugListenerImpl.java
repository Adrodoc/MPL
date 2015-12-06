package de.adrodoc55.minecraft.mpl.scribble;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.antlr.MplListener;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CoordinateContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeAtContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeMaxContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.LineContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModifierListContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipDeclarationContext;

//public (.+) (.+)\((.+) (.+)\) \{[^\}]*\}
//public $1 $2($3 $4) {System.out.println("$2(" + $4.getText() + ")");}
public class MplDebugListenerImpl implements MplListener {

    @Override
    public void visitTerminal(TerminalNode node) {
        System.out.println("visitTerminal(" + node.getText() + ")");
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.out.println("visitErrorNode(" + node.getText() + ")");
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        System.out.println("enterEveryRule(" + ctx.getText() + ")");
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        System.out.println("exitEveryRule(" + ctx.getText() + ")");
    }

    @Override
    public void enterProgram(ProgramContext ctx) {
        System.out.println("enterProgram(" + ctx.getText() + ")");
    }

    @Override
    public void exitProgram(ProgramContext ctx) {
        System.out.println("exitProgram(" + ctx.getText() + ")");
    }

    @Override
    public void enterLine(LineContext ctx) {
        System.out.println("enterLine(" + ctx.getText() + ")");
    }

    @Override
    public void exitLine(LineContext ctx) {
        System.out.println("exitLine(" + ctx.getText() + ")");
    }

    @Override
    public void enterIncludeDeclaration(IncludeDeclarationContext ctx) {
        System.out.println("enterIncludeDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void exitIncludeDeclaration(IncludeDeclarationContext ctx) {
        System.out.println("exitIncludeDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void enterIncludeAt(IncludeAtContext ctx) {
        System.out.println("enterIncludeAt(" + ctx.getText() + ")");
    }

    @Override
    public void exitIncludeAt(IncludeAtContext ctx) {
        System.out.println("exitIncludeAt(" + ctx.getText() + ")");
    }

    @Override
    public void enterIncludeMax(IncludeMaxContext ctx) {
        System.out.println("enterIncludeMax(" + ctx.getText() + ")");
    }

    @Override
    public void exitIncludeMax(IncludeMaxContext ctx) {
        System.out.println("exitIncludeMax(" + ctx.getText() + ")");
    }

    @Override
    public void enterCoordinate(CoordinateContext ctx) {
        System.out.println("enterCoordinate(" + ctx.getText() + ")");
    }

    @Override
    public void exitCoordinate(CoordinateContext ctx) {
        System.out.println("exitCoordinate(" + ctx.getText() + ")");
    }

    @Override
    public void enterSkipDeclaration(SkipDeclarationContext ctx) {
        System.out.println("enterSkipDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void exitSkipDeclaration(SkipDeclarationContext ctx) {
        System.out.println("exitSkipDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void enterCommandDeclaration(CommandDeclarationContext ctx) {
        System.out.println("enterCommandDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void exitCommandDeclaration(CommandDeclarationContext ctx) {
        System.out.println("exitCommandDeclaration(" + ctx.getText() + ")");
    }

    @Override
    public void enterModifierList(ModifierListContext ctx) {
        System.out.println("enterModifierList(" + ctx.getText() + ")");
    }

    @Override
    public void exitModifierList(ModifierListContext ctx) {
        System.out.println("exitModifierList(" + ctx.getText() + ")");
    }

    @Override
    public void enterModus(ModusContext ctx) {
        System.out.println("enterModus(" + ctx.getText() + ")");
    }

    @Override
    public void exitModus(ModusContext ctx) {
        System.out.println("exitModus(" + ctx.getText() + ")");
    }

    @Override
    public void enterConditional(ConditionalContext ctx) {
        System.out.println("enterConditional(" + ctx.getText() + ")");
    }

    @Override
    public void exitConditional(ConditionalContext ctx) {
        System.out.println("exitConditional(" + ctx.getText() + ")");
    }

    @Override
    public void enterAuto(AutoContext ctx) {
        System.out.println("enterAuto(" + ctx.getText() + ")");
    }

    @Override
    public void exitAuto(AutoContext ctx) {
        System.out.println("exitAuto(" + ctx.getText() + ")");
    }

    @Override
    public void enterCommand(CommandContext ctx) {
        System.out.println("enterCommand(" + ctx.getText() + ")");
    }

    @Override
    public void exitCommand(CommandContext ctx) {
        System.out.println("exitCommand(" + ctx.getText() + ")");
    }

}
