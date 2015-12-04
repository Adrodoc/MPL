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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, COMMENT=7, COMMAND=8, 
		EOL=9, WS=10;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "COMMENT", "COMMAND", 
		"EOL", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "':'", "'impulse'", "'chain'", "'repeat'", "'conditional'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "COMMENT", "COMMAND", "EOL", 
		"WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\fZ\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\7\bA\n\b\f\b\16\bD\13\b\3\b\3\b\3\t\3\t\7"+
		"\tJ\n\t\f\t\16\tM\13\t\3\n\3\n\3\n\5\nR\n\n\3\13\6\13U\n\13\r\13\16\13"+
		"V\3\13\3\13\2\2\f\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\3\2\4"+
		"\4\2\f\f\17\17\4\2\13\13\"\"]\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\3\27\3\2\2\2\5\31\3\2\2\2\7\33\3\2\2\2\t#\3\2\2\2\13)"+
		"\3\2\2\2\r\60\3\2\2\2\17<\3\2\2\2\21G\3\2\2\2\23Q\3\2\2\2\25T\3\2\2\2"+
		"\27\30\7.\2\2\30\4\3\2\2\2\31\32\7<\2\2\32\6\3\2\2\2\33\34\7k\2\2\34\35"+
		"\7o\2\2\35\36\7r\2\2\36\37\7w\2\2\37 \7n\2\2 !\7u\2\2!\"\7g\2\2\"\b\3"+
		"\2\2\2#$\7e\2\2$%\7j\2\2%&\7c\2\2&\'\7k\2\2\'(\7p\2\2(\n\3\2\2\2)*\7t"+
		"\2\2*+\7g\2\2+,\7r\2\2,-\7g\2\2-.\7c\2\2./\7v\2\2/\f\3\2\2\2\60\61\7e"+
		"\2\2\61\62\7q\2\2\62\63\7p\2\2\63\64\7f\2\2\64\65\7k\2\2\65\66\7v\2\2"+
		"\66\67\7k\2\2\678\7q\2\289\7p\2\29:\7c\2\2:;\7n\2\2;\16\3\2\2\2<=\7\61"+
		"\2\2=>\7\61\2\2>B\3\2\2\2?A\n\2\2\2@?\3\2\2\2AD\3\2\2\2B@\3\2\2\2BC\3"+
		"\2\2\2CE\3\2\2\2DB\3\2\2\2EF\b\b\2\2F\20\3\2\2\2GK\7\61\2\2HJ\n\2\2\2"+
		"IH\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2L\22\3\2\2\2MK\3\2\2\2NR\7\f\2"+
		"\2OP\7\17\2\2PR\7\f\2\2QN\3\2\2\2QO\3\2\2\2R\24\3\2\2\2SU\t\3\2\2TS\3"+
		"\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WX\3\2\2\2XY\b\13\2\2Y\26\3\2\2\2"+
		"\7\2BKQV\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}