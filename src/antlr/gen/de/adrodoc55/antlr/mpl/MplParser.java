// Generated from C:\Users\Adrian\Programme\workspace\MPL\src\antlr\def\de\adrodoc55\antlr\mpl\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.antlr.mpl;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MplParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, COMMENT=6, COMMAND=7, CONDITIONAL=8, 
		NEEDS_REDSTONE=9, EOL=10, WS=11;
	public static final int
		RULE_program = 0, RULE_line = 1, RULE_modifierList = 2, RULE_modus = 3;
	public static final String[] ruleNames = {
		"program", "line", "modifierList", "modus"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "':'", "'impulse'", "'chain'", "'repeat'", null, null, "'conditional'", 
		"'needsRedstone'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "COMMENT", "COMMAND", "CONDITIONAL", 
		"NEEDS_REDSTONE", "EOL", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Mpl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MplParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MplParser.EOF, 0); }
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << COMMAND) | (1L << CONDITIONAL) | (1L << NEEDS_REDSTONE) | (1L << EOL))) != 0)) {
				{
				{
				setState(8);
				line();
				}
				}
				setState(13);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(14);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode EOL() { return getToken(MplParser.EOL, 0); }
		public TerminalNode COMMAND() { return getToken(MplParser.COMMAND, 0); }
		public ModifierListContext modifierList() {
			return getRuleContext(ModifierListContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << COMMAND) | (1L << CONDITIONAL) | (1L << NEEDS_REDSTONE))) != 0)) {
				{
				setState(17);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << CONDITIONAL) | (1L << NEEDS_REDSTONE))) != 0)) {
					{
					setState(16);
					modifierList();
					}
				}

				setState(19);
				match(COMMAND);
				}
			}

			setState(22);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModifierListContext extends ParserRuleContext {
		public ModusContext modus() {
			return getRuleContext(ModusContext.class,0);
		}
		public TerminalNode CONDITIONAL() { return getToken(MplParser.CONDITIONAL, 0); }
		public TerminalNode NEEDS_REDSTONE() { return getToken(MplParser.NEEDS_REDSTONE, 0); }
		public ModifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterModifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitModifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitModifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModifierListContext modifierList() throws RecognitionException {
		ModifierListContext _localctx = new ModifierListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_modifierList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			switch (_input.LA(1)) {
			case T__2:
			case T__3:
			case T__4:
				{
				setState(24);
				modus();
				setState(27);
				switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
				case 1:
					{
					setState(25);
					match(T__0);
					setState(26);
					match(CONDITIONAL);
					}
					break;
				}
				setState(31);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(29);
					match(T__0);
					setState(30);
					match(NEEDS_REDSTONE);
					}
				}

				}
				break;
			case CONDITIONAL:
				{
				setState(33);
				match(CONDITIONAL);
				setState(36);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(34);
					match(T__0);
					setState(35);
					match(NEEDS_REDSTONE);
					}
				}

				}
				break;
			case NEEDS_REDSTONE:
				{
				setState(38);
				match(NEEDS_REDSTONE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(41);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModusContext extends ParserRuleContext {
		public ModusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterModus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitModus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitModus(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModusContext modus() throws RecognitionException {
		ModusContext _localctx = new ModusContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_modus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\r\60\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\3\2\7\2\f\n\2\f\2\16\2\17\13\2\3\2\3\2\3\3\5\3\24"+
		"\n\3\3\3\5\3\27\n\3\3\3\3\3\3\4\3\4\3\4\5\4\36\n\4\3\4\3\4\5\4\"\n\4\3"+
		"\4\3\4\3\4\5\4\'\n\4\3\4\5\4*\n\4\3\4\3\4\3\5\3\5\3\5\2\2\6\2\4\6\b\2"+
		"\3\3\2\5\7\63\2\r\3\2\2\2\4\26\3\2\2\2\6)\3\2\2\2\b-\3\2\2\2\n\f\5\4\3"+
		"\2\13\n\3\2\2\2\f\17\3\2\2\2\r\13\3\2\2\2\r\16\3\2\2\2\16\20\3\2\2\2\17"+
		"\r\3\2\2\2\20\21\7\2\2\3\21\3\3\2\2\2\22\24\5\6\4\2\23\22\3\2\2\2\23\24"+
		"\3\2\2\2\24\25\3\2\2\2\25\27\7\t\2\2\26\23\3\2\2\2\26\27\3\2\2\2\27\30"+
		"\3\2\2\2\30\31\7\f\2\2\31\5\3\2\2\2\32\35\5\b\5\2\33\34\7\3\2\2\34\36"+
		"\7\n\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36!\3\2\2\2\37 \7\3\2\2 \"\7\13\2"+
		"\2!\37\3\2\2\2!\"\3\2\2\2\"*\3\2\2\2#&\7\n\2\2$%\7\3\2\2%\'\7\13\2\2&"+
		"$\3\2\2\2&\'\3\2\2\2\'*\3\2\2\2(*\7\13\2\2)\32\3\2\2\2)#\3\2\2\2)(\3\2"+
		"\2\2*+\3\2\2\2+,\7\4\2\2,\7\3\2\2\2-.\t\2\2\2.\t\3\2\2\2\t\r\23\26\35"+
		"!&)";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}