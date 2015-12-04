// Generated from C:\Users\Adrian\Programme\workspace\MplGenerator\src\antlr\def\de\adrodoc55\antlr\mpl\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.antlr.mpl;
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
	 * Visit a parse tree produced by {@link MplParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MplParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MplParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(MplParser.LineContext ctx);
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
}