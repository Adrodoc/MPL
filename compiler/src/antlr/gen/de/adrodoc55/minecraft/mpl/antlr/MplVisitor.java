// Generated from C:\Users\adrian\Programme\workspace\MPL\compiler\src\antlr\def\de\adrodoc55\minecraft\mpl\antlr\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.minecraft.mpl.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MplParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MplVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MplParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(MplParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#scriptFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptFile(MplParser.ScriptFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#projectFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjectFile(MplParser.ProjectFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#importDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDeclaration(MplParser.ImportDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#project}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProject(MplParser.ProjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#orientation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrientation(MplParser.OrientationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#include}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclude(MplParser.IncludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#install}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstall(MplParser.InstallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#uninstall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUninstall(MplParser.UninstallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#process}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcess(MplParser.ProcessContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#chain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChain(MplParser.ChainContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#ifDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfDeclaration(MplParser.IfDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#then}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThen(MplParser.ThenContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#elseDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseDeclaration(MplParser.ElseDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#mplCommand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMplCommand(MplParser.MplCommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#modifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifierList(MplParser.ModifierListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#modus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModus(MplParser.ModusContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#conditional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(MplParser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#auto}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAuto(MplParser.AutoContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommand(MplParser.CommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(MplParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#stop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStop(MplParser.StopContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#waitfor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWaitfor(MplParser.WaitforContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#notifyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotifyDeclaration(MplParser.NotifyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#intercept}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntercept(MplParser.InterceptContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#breakpoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakpoint(MplParser.BreakpointContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#skip}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip(MplParser.SkipContext ctx);
}