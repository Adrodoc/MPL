package de.adrodoc55.minecraft.mpl.scribble;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.adrodoc55.minecraft.mpl.antlr.MplListener;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.AutoContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.CommandDeclarationContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ConditionalContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.InstallContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.LineContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.MethodContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModifierListContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ModusContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProgramContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.ProjectContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkipContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.SkriptContext;
import de.adrodoc55.minecraft.mpl.antlr.MplParser.UninstallContext;

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
    public void enterInclude(IncludeContext ctx) {
        System.out.println("enterInclude(" + ctx.getText() + ")");
    }

    @Override
    public void exitInclude(IncludeContext ctx) {
        System.out.println("exitInclude(" + ctx.getText() + ")");
    }

    @Override
    public void enterInstall(InstallContext ctx) {
        System.out.println("enterInstall(" + ctx.getText() + ")");
    }

    @Override
    public void exitInstall(InstallContext ctx) {
        System.out.println("exitInstall(" + ctx.getText() + ")");
    }

    @Override
    public void enterUninstall(UninstallContext ctx) {
        System.out.println("enterUninstall(" + ctx.getText() + ")");
    }

    @Override
    public void exitUninstall(UninstallContext ctx) {
        System.out.println("exitUninstall(" + ctx.getText() + ")");
    }

    @Override
    public void enterProject(ProjectContext ctx) {
        System.out.println("enterProject(" + ctx.getText() + ")");
    }

    @Override
    public void exitProject(ProjectContext ctx) {
        System.out.println("exitProject(" + ctx.getText() + ")");
    }

    @Override
    public void enterMethod(MethodContext ctx) {
        System.out.println("enterMethod(" + ctx.getText() + ")");
    }

    @Override
    public void exitMethod(MethodContext ctx) {
        System.out.println("exitMethod(" + ctx.getText() + ")");
    }

    @Override
    public void enterSkript(SkriptContext ctx) {
        System.out.println("enterSkript(" + ctx.getText() + ")");
    }

    @Override
    public void exitSkript(SkriptContext ctx) {
        System.out.println("exitSkript(" + ctx.getText() + ")");
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

    @Override
    public void enterSkip(SkipContext ctx) {
        System.out.println("enterSkip(" + ctx.getText() + ")");
    }

    @Override
    public void exitSkip(SkipContext ctx) {
        System.out.println("exitSkip(" + ctx.getText() + ")");
    }

}
