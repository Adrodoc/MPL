// Generated from C:\Users\Adrian\Programme\workspace\MPL\src\antlr\def\de\adrodoc55\antlr\mpl\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.antlr.mpl;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MplParser}.
 */
public interface MplListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MplParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MplParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MplParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MplParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(MplParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link MplParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(MplParser.LineContext ctx);
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
}