// $ANTLR 3.5.2 Mpl.g 2015-11-29 21:01:55

package de.adrodoc55.antlr.mpl;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class MplParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMAND", "COMMENT", "EOL", "WS", 
		"','", "':'", "'chain'", "'conditional'", "'impulse'", "'repeat'"
	};
	public static final int EOF=-1;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int T__11=11;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int COMMAND=4;
	public static final int COMMENT=5;
	public static final int EOL=6;
	public static final int WS=7;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public MplParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public MplParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return MplParser.tokenNames; }
	@Override public String getGrammarFileName() { return "Mpl.g"; }


	public static class program_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "program"
	// Mpl.g:18:1: program : ( line )* EOF ;
	public final MplParser.program_return program() throws RecognitionException {
		MplParser.program_return retval = new MplParser.program_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EOF2=null;
		ParserRuleReturnScope line1 =null;

		Object EOF2_tree=null;

		try {
			// Mpl.g:19:2: ( ( line )* EOF )
			// Mpl.g:20:2: ( line )* EOF
			{
			root_0 = (Object)adaptor.nil();


			// Mpl.g:20:2: ( line )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==COMMAND||LA1_0==EOL||(LA1_0 >= 10 && LA1_0 <= 13)) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// Mpl.g:20:2: line
					{
					pushFollow(FOLLOW_line_in_program53);
					line1=line();
					state._fsp--;

					adaptor.addChild(root_0, line1.getTree());

					}
					break;

				default :
					break loop1;
				}
			}

			EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_program56); 
			EOF2_tree = (Object)adaptor.create(EOF2);
			adaptor.addChild(root_0, EOF2_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "program"


	public static class line_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "line"
	// Mpl.g:23:1: line : ( ( modifierList )? COMMAND )? EOL ;
	public final MplParser.line_return line() throws RecognitionException {
		MplParser.line_return retval = new MplParser.line_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COMMAND4=null;
		Token EOL5=null;
		ParserRuleReturnScope modifierList3 =null;

		Object COMMAND4_tree=null;
		Object EOL5_tree=null;

		try {
			// Mpl.g:24:2: ( ( ( modifierList )? COMMAND )? EOL )
			// Mpl.g:25:2: ( ( modifierList )? COMMAND )? EOL
			{
			root_0 = (Object)adaptor.nil();


			// Mpl.g:25:2: ( ( modifierList )? COMMAND )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==COMMAND||(LA3_0 >= 10 && LA3_0 <= 13)) ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// Mpl.g:26:3: ( modifierList )? COMMAND
					{
					// Mpl.g:26:3: ( modifierList )?
					int alt2=2;
					int LA2_0 = input.LA(1);
					if ( ((LA2_0 >= 10 && LA2_0 <= 13)) ) {
						alt2=1;
					}
					switch (alt2) {
						case 1 :
							// Mpl.g:26:3: modifierList
							{
							pushFollow(FOLLOW_modifierList_in_line70);
							modifierList3=modifierList();
							state._fsp--;

							adaptor.addChild(root_0, modifierList3.getTree());

							}
							break;

					}

					COMMAND4=(Token)match(input,COMMAND,FOLLOW_COMMAND_in_line73); 
					COMMAND4_tree = (Object)adaptor.create(COMMAND4);
					adaptor.addChild(root_0, COMMAND4_tree);

					}
					break;

			}

			EOL5=(Token)match(input,EOL,FOLLOW_EOL_in_line79); 
			EOL5_tree = (Object)adaptor.create(EOL5);
			adaptor.addChild(root_0, EOL5_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "line"


	public static class modifier_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "modifier"
	// Mpl.g:30:1: modifier : ( 'impulse' | 'chain' | 'repeat' | 'conditional' );
	public final MplParser.modifier_return modifier() throws RecognitionException {
		MplParser.modifier_return retval = new MplParser.modifier_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set6=null;

		Object set6_tree=null;

		try {
			// Mpl.g:31:2: ( 'impulse' | 'chain' | 'repeat' | 'conditional' )
			// Mpl.g:
			{
			root_0 = (Object)adaptor.nil();


			set6=input.LT(1);
			if ( (input.LA(1) >= 10 && input.LA(1) <= 13) ) {
				input.consume();
				adaptor.addChild(root_0, (Object)adaptor.create(set6));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "modifier"


	public static class modifierList_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "modifierList"
	// Mpl.g:38:1: modifierList : modifier ( ',' modifier )? ':' ;
	public final MplParser.modifierList_return modifierList() throws RecognitionException {
		MplParser.modifierList_return retval = new MplParser.modifierList_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token char_literal8=null;
		Token char_literal10=null;
		ParserRuleReturnScope modifier7 =null;
		ParserRuleReturnScope modifier9 =null;

		Object char_literal8_tree=null;
		Object char_literal10_tree=null;

		try {
			// Mpl.g:39:2: ( modifier ( ',' modifier )? ':' )
			// Mpl.g:40:2: modifier ( ',' modifier )? ':'
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_modifier_in_modifierList114);
			modifier7=modifier();
			state._fsp--;

			adaptor.addChild(root_0, modifier7.getTree());

			// Mpl.g:41:2: ( ',' modifier )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==8) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// Mpl.g:42:3: ',' modifier
					{
					char_literal8=(Token)match(input,8,FOLLOW_8_in_modifierList121); 
					char_literal8_tree = (Object)adaptor.create(char_literal8);
					adaptor.addChild(root_0, char_literal8_tree);

					pushFollow(FOLLOW_modifier_in_modifierList123);
					modifier9=modifier();
					state._fsp--;

					adaptor.addChild(root_0, modifier9.getTree());

					}
					break;

			}

			char_literal10=(Token)match(input,9,FOLLOW_9_in_modifierList129); 
			char_literal10_tree = (Object)adaptor.create(char_literal10);
			adaptor.addChild(root_0, char_literal10_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "modifierList"

	// Delegated rules



	public static final BitSet FOLLOW_line_in_program53 = new BitSet(new long[]{0x0000000000003C50L});
	public static final BitSet FOLLOW_EOF_in_program56 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifierList_in_line70 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_COMMAND_in_line73 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_EOL_in_line79 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_modifier_in_modifierList114 = new BitSet(new long[]{0x0000000000000300L});
	public static final BitSet FOLLOW_8_in_modifierList121 = new BitSet(new long[]{0x0000000000003C00L});
	public static final BitSet FOLLOW_modifier_in_modifierList123 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_9_in_modifierList129 = new BitSet(new long[]{0x0000000000000002L});
}
