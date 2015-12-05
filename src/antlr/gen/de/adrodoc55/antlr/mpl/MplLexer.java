// Generated from C:\Users\Adrian\Programme\workspace\MplGenerator\src\antlr\def\de\adrodoc55\antlr\mpl\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.antlr.mpl;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MplLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, COMMENT=6, COMMAND=7, CONDITIONAL=8, 
		NEEDS_REDSTONE=9, EOL=10, WS=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "COMMENT", "COMMAND", "CONDITIONAL", 
		"NEEDS_REDSTONE", "EOL", "WS"
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


	public MplLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mpl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\rl\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\5\7\66\n\7\3\7"+
		"\7\79\n\7\f\7\16\7<\13\7\3\7\3\7\3\b\3\b\7\bB\n\b\f\b\16\bE\13\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\5\13d\n\13\3\f\6\fg\n\f\r"+
		"\f\16\fh\3\f\3\f\2\2\r\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\3\2\4\4\2\f\f\17\17\4\2\13\13\"\"p\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3\31\3\2\2\2\5\33\3\2\2\2\7\35\3"+
		"\2\2\2\t%\3\2\2\2\13+\3\2\2\2\r\65\3\2\2\2\17?\3\2\2\2\21F\3\2\2\2\23"+
		"R\3\2\2\2\25c\3\2\2\2\27f\3\2\2\2\31\32\7.\2\2\32\4\3\2\2\2\33\34\7<\2"+
		"\2\34\6\3\2\2\2\35\36\7k\2\2\36\37\7o\2\2\37 \7r\2\2 !\7w\2\2!\"\7n\2"+
		"\2\"#\7u\2\2#$\7g\2\2$\b\3\2\2\2%&\7e\2\2&\'\7j\2\2\'(\7c\2\2()\7k\2\2"+
		")*\7p\2\2*\n\3\2\2\2+,\7t\2\2,-\7g\2\2-.\7r\2\2./\7g\2\2/\60\7c\2\2\60"+
		"\61\7v\2\2\61\f\3\2\2\2\62\63\7\61\2\2\63\66\7\61\2\2\64\66\7%\2\2\65"+
		"\62\3\2\2\2\65\64\3\2\2\2\66:\3\2\2\2\679\n\2\2\28\67\3\2\2\29<\3\2\2"+
		"\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\b\7\2\2>\16\3\2\2\2?C\7\61"+
		"\2\2@B\n\2\2\2A@\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2D\20\3\2\2\2EC\3"+
		"\2\2\2FG\7e\2\2GH\7q\2\2HI\7p\2\2IJ\7f\2\2JK\7k\2\2KL\7v\2\2LM\7k\2\2"+
		"MN\7q\2\2NO\7p\2\2OP\7c\2\2PQ\7n\2\2Q\22\3\2\2\2RS\7p\2\2ST\7g\2\2TU\7"+
		"g\2\2UV\7f\2\2VW\7u\2\2WX\7T\2\2XY\7g\2\2YZ\7f\2\2Z[\7u\2\2[\\\7v\2\2"+
		"\\]\7q\2\2]^\7p\2\2^_\7g\2\2_\24\3\2\2\2`d\7\f\2\2ab\7\17\2\2bd\7\f\2"+
		"\2c`\3\2\2\2ca\3\2\2\2d\26\3\2\2\2eg\t\3\2\2fe\3\2\2\2gh\3\2\2\2hf\3\2"+
		"\2\2hi\3\2\2\2ij\3\2\2\2jk\b\f\2\2k\30\3\2\2\2\b\2\65:Cch\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}