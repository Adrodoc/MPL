// Generated from C:\Users\Adrian\Programme\workspace\MPL\compiler\src\antlr\def\de\adrodoc55\minecraft\mpl\antlr\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.minecraft.mpl.antlr;
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
		T__0=1, T__1=2, T__2=3, T__3=4, COMMENT=5, COMMAND=6, IMPORT=7, PROJECT=8, 
		INCLUDE=9, ORIENTATION=10, INSTALL=11, UNINSTALL=12, PROCESS=13, IMPULSE=14, 
		CHAIN=15, REPEAT=16, UNCONDITIONAL=17, CONDITIONAL=18, INVERT=19, ALWAYS_ACTIVE=20, 
		NEEDS_REDSTONE=21, START=22, STOP=23, WAITFOR=24, NOTIFY=25, INTERCEPT=26, 
		BREAKPOINT=27, SKIP=28, IF=29, NOT=30, THEN=31, ELSE=32, UNSIGNED_INT=33, 
		WS=34, STRING=35, IDENTIFIER=36, UNRECOGNIZED=37;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "COMMENT", "COMMAND", "IMPORT", "PROJECT", 
		"INCLUDE", "ORIENTATION", "INSTALL", "UNINSTALL", "PROCESS", "IMPULSE", 
		"CHAIN", "REPEAT", "UNCONDITIONAL", "CONDITIONAL", "INVERT", "ALWAYS_ACTIVE", 
		"NEEDS_REDSTONE", "START", "STOP", "WAITFOR", "NOTIFY", "INTERCEPT", "BREAKPOINT", 
		"SKIP", "IF", "NOT", "THEN", "ELSE", "UNSIGNED_INT", "WS", "STRING", "IDENTIFIER", 
		"UNRECOGNIZED"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "':'", "','", null, null, "'import'", "'project'", 
		"'include'", "'orientation'", "'install'", "'uninstall'", "'process'", 
		"'impulse'", "'chain'", "'repeat'", "'unconditional'", "'conditional'", 
		"'invert'", "'always active'", "'needs redstone'", "'start'", "'stop'", 
		"'waitfor'", "'notify'", "'intercept'", "'breakpoint'", "'skip'", "'if'", 
		"'not'", "'then'", "'else'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "COMMENT", "COMMAND", "IMPORT", "PROJECT", 
		"INCLUDE", "ORIENTATION", "INSTALL", "UNINSTALL", "PROCESS", "IMPULSE", 
		"CHAIN", "REPEAT", "UNCONDITIONAL", "CONDITIONAL", "INVERT", "ALWAYS_ACTIVE", 
		"NEEDS_REDSTONE", "START", "STOP", "WAITFOR", "NOTIFY", "INTERCEPT", "BREAKPOINT", 
		"SKIP", "IF", "NOT", "THEN", "ELSE", "UNSIGNED_INT", "WS", "STRING", "IDENTIFIER", 
		"UNRECOGNIZED"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\'\u015a\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\6\5\6Y\n\6\3\6\7\6\\\n\6\f\6\16\6_\13\6\3\6\3\6\3\7\3\7\7\7e"+
		"\n\7\f\7\16\7h\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\37"+
		"\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\6\"\u0140\n\"\r\"\16"+
		"\"\u0141\3#\6#\u0145\n#\r#\16#\u0146\3#\3#\3$\3$\7$\u014d\n$\f$\16$\u0150"+
		"\13$\3$\3$\3%\6%\u0155\n%\r%\16%\u0156\3&\3&\3\u014e\2\'\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'\3\2\6\4\2\f\f\17\17\3\2\62;\5\2\13\f\17\17\"\"\6\2\62;C\\aac|\u0160"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\3M\3\2\2\2\5O\3\2\2\2\7Q\3\2\2\2\tS\3\2\2\2\13"+
		"X\3\2\2\2\rb\3\2\2\2\17i\3\2\2\2\21p\3\2\2\2\23x\3\2\2\2\25\u0080\3\2"+
		"\2\2\27\u008c\3\2\2\2\31\u0094\3\2\2\2\33\u009e\3\2\2\2\35\u00a6\3\2\2"+
		"\2\37\u00ae\3\2\2\2!\u00b4\3\2\2\2#\u00bb\3\2\2\2%\u00c9\3\2\2\2\'\u00d5"+
		"\3\2\2\2)\u00dc\3\2\2\2+\u00ea\3\2\2\2-\u00f9\3\2\2\2/\u00ff\3\2\2\2\61"+
		"\u0104\3\2\2\2\63\u010c\3\2\2\2\65\u0113\3\2\2\2\67\u011d\3\2\2\29\u0128"+
		"\3\2\2\2;\u012d\3\2\2\2=\u0130\3\2\2\2?\u0134\3\2\2\2A\u0139\3\2\2\2C"+
		"\u013f\3\2\2\2E\u0144\3\2\2\2G\u014a\3\2\2\2I\u0154\3\2\2\2K\u0158\3\2"+
		"\2\2MN\7*\2\2N\4\3\2\2\2OP\7+\2\2P\6\3\2\2\2QR\7<\2\2R\b\3\2\2\2ST\7."+
		"\2\2T\n\3\2\2\2UV\7\61\2\2VY\7\61\2\2WY\7%\2\2XU\3\2\2\2XW\3\2\2\2Y]\3"+
		"\2\2\2Z\\\n\2\2\2[Z\3\2\2\2\\_\3\2\2\2][\3\2\2\2]^\3\2\2\2^`\3\2\2\2_"+
		"]\3\2\2\2`a\b\6\2\2a\f\3\2\2\2bf\7\61\2\2ce\n\2\2\2dc\3\2\2\2eh\3\2\2"+
		"\2fd\3\2\2\2fg\3\2\2\2g\16\3\2\2\2hf\3\2\2\2ij\7k\2\2jk\7o\2\2kl\7r\2"+
		"\2lm\7q\2\2mn\7t\2\2no\7v\2\2o\20\3\2\2\2pq\7r\2\2qr\7t\2\2rs\7q\2\2s"+
		"t\7l\2\2tu\7g\2\2uv\7e\2\2vw\7v\2\2w\22\3\2\2\2xy\7k\2\2yz\7p\2\2z{\7"+
		"e\2\2{|\7n\2\2|}\7w\2\2}~\7f\2\2~\177\7g\2\2\177\24\3\2\2\2\u0080\u0081"+
		"\7q\2\2\u0081\u0082\7t\2\2\u0082\u0083\7k\2\2\u0083\u0084\7g\2\2\u0084"+
		"\u0085\7p\2\2\u0085\u0086\7v\2\2\u0086\u0087\7c\2\2\u0087\u0088\7v\2\2"+
		"\u0088\u0089\7k\2\2\u0089\u008a\7q\2\2\u008a\u008b\7p\2\2\u008b\26\3\2"+
		"\2\2\u008c\u008d\7k\2\2\u008d\u008e\7p\2\2\u008e\u008f\7u\2\2\u008f\u0090"+
		"\7v\2\2\u0090\u0091\7c\2\2\u0091\u0092\7n\2\2\u0092\u0093\7n\2\2\u0093"+
		"\30\3\2\2\2\u0094\u0095\7w\2\2\u0095\u0096\7p\2\2\u0096\u0097\7k\2\2\u0097"+
		"\u0098\7p\2\2\u0098\u0099\7u\2\2\u0099\u009a\7v\2\2\u009a\u009b\7c\2\2"+
		"\u009b\u009c\7n\2\2\u009c\u009d\7n\2\2\u009d\32\3\2\2\2\u009e\u009f\7"+
		"r\2\2\u009f\u00a0\7t\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7e\2\2\u00a2\u00a3"+
		"\7g\2\2\u00a3\u00a4\7u\2\2\u00a4\u00a5\7u\2\2\u00a5\34\3\2\2\2\u00a6\u00a7"+
		"\7k\2\2\u00a7\u00a8\7o\2\2\u00a8\u00a9\7r\2\2\u00a9\u00aa\7w\2\2\u00aa"+
		"\u00ab\7n\2\2\u00ab\u00ac\7u\2\2\u00ac\u00ad\7g\2\2\u00ad\36\3\2\2\2\u00ae"+
		"\u00af\7e\2\2\u00af\u00b0\7j\2\2\u00b0\u00b1\7c\2\2\u00b1\u00b2\7k\2\2"+
		"\u00b2\u00b3\7p\2\2\u00b3 \3\2\2\2\u00b4\u00b5\7t\2\2\u00b5\u00b6\7g\2"+
		"\2\u00b6\u00b7\7r\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7c\2\2\u00b9\u00ba"+
		"\7v\2\2\u00ba\"\3\2\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7p\2\2\u00bd\u00be"+
		"\7e\2\2\u00be\u00bf\7q\2\2\u00bf\u00c0\7p\2\2\u00c0\u00c1\7f\2\2\u00c1"+
		"\u00c2\7k\2\2\u00c2\u00c3\7v\2\2\u00c3\u00c4\7k\2\2\u00c4\u00c5\7q\2\2"+
		"\u00c5\u00c6\7p\2\2\u00c6\u00c7\7c\2\2\u00c7\u00c8\7n\2\2\u00c8$\3\2\2"+
		"\2\u00c9\u00ca\7e\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7p\2\2\u00cc\u00cd"+
		"\7f\2\2\u00cd\u00ce\7k\2\2\u00ce\u00cf\7v\2\2\u00cf\u00d0\7k\2\2\u00d0"+
		"\u00d1\7q\2\2\u00d1\u00d2\7p\2\2\u00d2\u00d3\7c\2\2\u00d3\u00d4\7n\2\2"+
		"\u00d4&\3\2\2\2\u00d5\u00d6\7k\2\2\u00d6\u00d7\7p\2\2\u00d7\u00d8\7x\2"+
		"\2\u00d8\u00d9\7g\2\2\u00d9\u00da\7t\2\2\u00da\u00db\7v\2\2\u00db(\3\2"+
		"\2\2\u00dc\u00dd\7c\2\2\u00dd\u00de\7n\2\2\u00de\u00df\7y\2\2\u00df\u00e0"+
		"\7c\2\2\u00e0\u00e1\7{\2\2\u00e1\u00e2\7u\2\2\u00e2\u00e3\7\"\2\2\u00e3"+
		"\u00e4\7c\2\2\u00e4\u00e5\7e\2\2\u00e5\u00e6\7v\2\2\u00e6\u00e7\7k\2\2"+
		"\u00e7\u00e8\7x\2\2\u00e8\u00e9\7g\2\2\u00e9*\3\2\2\2\u00ea\u00eb\7p\2"+
		"\2\u00eb\u00ec\7g\2\2\u00ec\u00ed\7g\2\2\u00ed\u00ee\7f\2\2\u00ee\u00ef"+
		"\7u\2\2\u00ef\u00f0\7\"\2\2\u00f0\u00f1\7t\2\2\u00f1\u00f2\7g\2\2\u00f2"+
		"\u00f3\7f\2\2\u00f3\u00f4\7u\2\2\u00f4\u00f5\7v\2\2\u00f5\u00f6\7q\2\2"+
		"\u00f6\u00f7\7p\2\2\u00f7\u00f8\7g\2\2\u00f8,\3\2\2\2\u00f9\u00fa\7u\2"+
		"\2\u00fa\u00fb\7v\2\2\u00fb\u00fc\7c\2\2\u00fc\u00fd\7t\2\2\u00fd\u00fe"+
		"\7v\2\2\u00fe.\3\2\2\2\u00ff\u0100\7u\2\2\u0100\u0101\7v\2\2\u0101\u0102"+
		"\7q\2\2\u0102\u0103\7r\2\2\u0103\60\3\2\2\2\u0104\u0105\7y\2\2\u0105\u0106"+
		"\7c\2\2\u0106\u0107\7k\2\2\u0107\u0108\7v\2\2\u0108\u0109\7h\2\2\u0109"+
		"\u010a\7q\2\2\u010a\u010b\7t\2\2\u010b\62\3\2\2\2\u010c\u010d\7p\2\2\u010d"+
		"\u010e\7q\2\2\u010e\u010f\7v\2\2\u010f\u0110\7k\2\2\u0110\u0111\7h\2\2"+
		"\u0111\u0112\7{\2\2\u0112\64\3\2\2\2\u0113\u0114\7k\2\2\u0114\u0115\7"+
		"p\2\2\u0115\u0116\7v\2\2\u0116\u0117\7g\2\2\u0117\u0118\7t\2\2\u0118\u0119"+
		"\7e\2\2\u0119\u011a\7g\2\2\u011a\u011b\7r\2\2\u011b\u011c\7v\2\2\u011c"+
		"\66\3\2\2\2\u011d\u011e\7d\2\2\u011e\u011f\7t\2\2\u011f\u0120\7g\2\2\u0120"+
		"\u0121\7c\2\2\u0121\u0122\7m\2\2\u0122\u0123\7r\2\2\u0123\u0124\7q\2\2"+
		"\u0124\u0125\7k\2\2\u0125\u0126\7p\2\2\u0126\u0127\7v\2\2\u01278\3\2\2"+
		"\2\u0128\u0129\7u\2\2\u0129\u012a\7m\2\2\u012a\u012b\7k\2\2\u012b\u012c"+
		"\7r\2\2\u012c:\3\2\2\2\u012d\u012e\7k\2\2\u012e\u012f\7h\2\2\u012f<\3"+
		"\2\2\2\u0130\u0131\7p\2\2\u0131\u0132\7q\2\2\u0132\u0133\7v\2\2\u0133"+
		">\3\2\2\2\u0134\u0135\7v\2\2\u0135\u0136\7j\2\2\u0136\u0137\7g\2\2\u0137"+
		"\u0138\7p\2\2\u0138@\3\2\2\2\u0139\u013a\7g\2\2\u013a\u013b\7n\2\2\u013b"+
		"\u013c\7u\2\2\u013c\u013d\7g\2\2\u013dB\3\2\2\2\u013e\u0140\t\3\2\2\u013f"+
		"\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142D\3\2\2\2\u0143\u0145\t\4\2\2\u0144\u0143\3\2\2\2\u0145\u0146"+
		"\3\2\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148"+
		"\u0149\b#\3\2\u0149F\3\2\2\2\u014a\u014e\7$\2\2\u014b\u014d\13\2\2\2\u014c"+
		"\u014b\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014f\3\2\2\2\u014e\u014c\3\2"+
		"\2\2\u014f\u0151\3\2\2\2\u0150\u014e\3\2\2\2\u0151\u0152\7$\2\2\u0152"+
		"H\3\2\2\2\u0153\u0155\t\5\2\2\u0154\u0153\3\2\2\2\u0155\u0156\3\2\2\2"+
		"\u0156\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157J\3\2\2\2\u0158\u0159\13"+
		"\2\2\2\u0159L\3\2\2\2\n\2X]f\u0141\u0146\u014e\u0156\4\2\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}