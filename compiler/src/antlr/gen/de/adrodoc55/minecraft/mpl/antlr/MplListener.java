// Generated from C:\Users\Adrian\Programme\workspace\MPL\compiler\src\antlr\def\de\adrodoc55\minecraft\mpl\antlr\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.minecraft.mpl.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MplParser}.
 */
public interface MplListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MplParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(MplParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(MplParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#scriptFile}.
	 * @param ctx the parse tree
	 */
	void enterScriptFile(MplParser.ScriptFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#scriptFile}.
	 * @param ctx the parse tree
	 */
	void exitScriptFile(MplParser.ScriptFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#projectFile}.
	 * @param ctx the parse tree
	 */
	void enterProjectFile(MplParser.ProjectFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#projectFile}.
	 * @param ctx the parse tree
	 */
	void exitProjectFile(MplParser.ProjectFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(MplParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(MplParser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#project}.
	 * @param ctx the parse tree
	 */
	void enterProject(MplParser.ProjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#project}.
	 * @param ctx the parse tree
	 */
	void exitProject(MplParser.ProjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#orientation}.
	 * @param ctx the parse tree
	 */
	void enterOrientation(MplParser.OrientationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#orientation}.
	 * @param ctx the parse tree
	 */
	void exitOrientation(MplParser.OrientationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#include}.
	 * @param ctx the parse tree
	 */
	void enterInclude(MplParser.IncludeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#include}.
	 * @param ctx the parse tree
	 */
	void exitInclude(MplParser.IncludeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#install}.
	 * @param ctx the parse tree
	 */
	void enterInstall(MplParser.InstallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#install}.
	 * @param ctx the parse tree
	 */
	void exitInstall(MplParser.InstallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#uninstall}.
	 * @param ctx the parse tree
	 */
	void enterUninstall(MplParser.UninstallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#uninstall}.
	 * @param ctx the parse tree
	 */
	void exitUninstall(MplParser.UninstallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#process}.
	 * @param ctx the parse tree
	 */
	void enterProcess(MplParser.ProcessContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#process}.
	 * @param ctx the parse tree
	 */
	void exitProcess(MplParser.ProcessContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#chain}.
	 * @param ctx the parse tree
	 */
	void enterChain(MplParser.ChainContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#chain}.
	 * @param ctx the parse tree
	 */
	void exitChain(MplParser.ChainContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#ifDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterIfDeclaration(MplParser.IfDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#ifDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitIfDeclaration(MplParser.IfDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#then}.
	 * @param ctx the parse tree
	 */
	void enterThen(MplParser.ThenContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#then}.
	 * @param ctx the parse tree
	 */
	void exitThen(MplParser.ThenContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#elseDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterElseDeclaration(MplParser.ElseDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#elseDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitElseDeclaration(MplParser.ElseDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#mplCommand}.
	 * @param ctx the parse tree
	 */
	void enterMplCommand(MplParser.MplCommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#mplCommand}.
	 * @param ctx the parse tree
	 */
	void exitMplCommand(MplParser.MplCommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#modifierList}.
	 * @param ctx the parse tree
	 */
	void enterModifierList(MplParser.ModifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#modifierList}.
	 * @param ctx the parse tree
	 */
	void exitModifierList(MplParser.ModifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#modus}.
	 * @param ctx the parse tree
	 */
	void enterModus(MplParser.ModusContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#modus}.
	 * @param ctx the parse tree
	 */
	void exitModus(MplParser.ModusContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterConditional(MplParser.ConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitConditional(MplParser.ConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#auto}.
	 * @param ctx the parse tree
	 */
	void enterAuto(MplParser.AutoContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#auto}.
	 * @param ctx the parse tree
	 */
	void exitAuto(MplParser.AutoContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(MplParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(MplParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(MplParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(MplParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#stop}.
	 * @param ctx the parse tree
	 */
	void enterStop(MplParser.StopContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#stop}.
	 * @param ctx the parse tree
	 */
	void exitStop(MplParser.StopContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#waitfor}.
	 * @param ctx the parse tree
	 */
	void enterWaitfor(MplParser.WaitforContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#waitfor}.
	 * @param ctx the parse tree
	 */
	void exitWaitfor(MplParser.WaitforContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#notifyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNotifyDeclaration(MplParser.NotifyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#notifyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNotifyDeclaration(MplParser.NotifyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#intercept}.
	 * @param ctx the parse tree
	 */
	void enterIntercept(MplParser.InterceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#intercept}.
	 * @param ctx the parse tree
	 */
	void exitIntercept(MplParser.InterceptContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#breakpoint}.
	 * @param ctx the parse tree
	 */
	void enterBreakpoint(MplParser.BreakpointContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#breakpoint}.
	 * @param ctx the parse tree
	 */
	void exitBreakpoint(MplParser.BreakpointContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#skip}.
	 * @param ctx the parse tree
	 */
	void enterSkip(MplParser.SkipContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#skip}.
	 * @param ctx the parse tree
	 */
	void exitSkip(MplParser.SkipContext ctx);
}