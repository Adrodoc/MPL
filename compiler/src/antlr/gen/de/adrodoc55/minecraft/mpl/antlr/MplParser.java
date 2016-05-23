// Generated from C:\Users\Adrian\Programme\workspace\MPL\compiler\src\antlr\def\de\adrodoc55\minecraft\mpl\antlr\Mpl.g4 by ANTLR 4.5
package de.adrodoc55.minecraft.mpl.antlr;
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
		T__0=1, T__1=2, T__2=3, T__3=4, COMMENT=5, COMMAND=6, IMPORT=7, PROJECT=8, 
		INCLUDE=9, ORIENTATION=10, INSTALL=11, UNINSTALL=12, PROCESS=13, IMPULSE=14, 
		CHAIN=15, REPEAT=16, UNCONDITIONAL=17, CONDITIONAL=18, INVERT=19, ALWAYS_ACTIVE=20, 
		NEEDS_REDSTONE=21, START=22, STOP=23, WAITFOR=24, NOTIFY=25, INTERCEPT=26, 
		BREAKPOINT=27, SKIP=28, IF=29, NOT=30, THEN=31, ELSE=32, UNSIGNED_INT=33, 
		WS=34, STRING=35, IDENTIFIER=36, UNRECOGNIZED=37;
	public static final int
		RULE_file = 0, RULE_scriptFile = 1, RULE_projectFile = 2, RULE_importDeclaration = 3, 
		RULE_project = 4, RULE_orientation = 5, RULE_include = 6, RULE_install = 7, 
		RULE_uninstall = 8, RULE_process = 9, RULE_chain = 10, RULE_ifDeclaration = 11, 
		RULE_then = 12, RULE_elseDeclaration = 13, RULE_mplCommand = 14, RULE_modifierList = 15, 
		RULE_modus = 16, RULE_conditional = 17, RULE_auto = 18, RULE_command = 19, 
		RULE_start = 20, RULE_stop = 21, RULE_waitfor = 22, RULE_notifyDeclaration = 23, 
		RULE_intercept = 24, RULE_breakpoint = 25, RULE_skip = 26;
	public static final String[] ruleNames = {
		"file", "scriptFile", "projectFile", "importDeclaration", "project", "orientation", 
		"include", "install", "uninstall", "process", "chain", "ifDeclaration", 
		"then", "elseDeclaration", "mplCommand", "modifierList", "modus", "conditional", 
		"auto", "command", "start", "stop", "waitfor", "notifyDeclaration", "intercept", 
		"breakpoint", "skip"
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
	public static class FileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MplParser.EOF, 0); }
		public ScriptFileContext scriptFile() {
			return getRuleContext(ScriptFileContext.class,0);
		}
		public ProjectFileContext projectFile() {
			return getRuleContext(ProjectFileContext.class,0);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(54);
				scriptFile();
				}
				break;
			case 2:
				{
				setState(55);
				projectFile();
				}
				break;
			}
			setState(58);
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

	public static class ScriptFileContext extends ParserRuleContext {
		public List<OrientationContext> orientation() {
			return getRuleContexts(OrientationContext.class);
		}
		public OrientationContext orientation(int i) {
			return getRuleContext(OrientationContext.class,i);
		}
		public List<InstallContext> install() {
			return getRuleContexts(InstallContext.class);
		}
		public InstallContext install(int i) {
			return getRuleContext(InstallContext.class,i);
		}
		public List<UninstallContext> uninstall() {
			return getRuleContexts(UninstallContext.class);
		}
		public UninstallContext uninstall(int i) {
			return getRuleContext(UninstallContext.class,i);
		}
		public List<ChainContext> chain() {
			return getRuleContexts(ChainContext.class);
		}
		public ChainContext chain(int i) {
			return getRuleContext(ChainContext.class,i);
		}
		public ScriptFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scriptFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterScriptFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitScriptFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitScriptFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptFileContext scriptFile() throws RecognitionException {
		ScriptFileContext _localctx = new ScriptFileContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_scriptFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COMMAND) | (1L << ORIENTATION) | (1L << INSTALL) | (1L << UNINSTALL) | (1L << IMPULSE) | (1L << CHAIN) | (1L << REPEAT) | (1L << UNCONDITIONAL) | (1L << CONDITIONAL) | (1L << INVERT) | (1L << ALWAYS_ACTIVE) | (1L << NEEDS_REDSTONE) | (1L << START) | (1L << STOP) | (1L << WAITFOR) | (1L << NOTIFY) | (1L << INTERCEPT) | (1L << BREAKPOINT) | (1L << SKIP) | (1L << IF))) != 0)) {
				{
				setState(64);
				switch (_input.LA(1)) {
				case ORIENTATION:
					{
					setState(60);
					orientation();
					}
					break;
				case INSTALL:
					{
					setState(61);
					install();
					}
					break;
				case UNINSTALL:
					{
					setState(62);
					uninstall();
					}
					break;
				case COMMAND:
				case IMPULSE:
				case CHAIN:
				case REPEAT:
				case UNCONDITIONAL:
				case CONDITIONAL:
				case INVERT:
				case ALWAYS_ACTIVE:
				case NEEDS_REDSTONE:
				case START:
				case STOP:
				case WAITFOR:
				case NOTIFY:
				case INTERCEPT:
				case BREAKPOINT:
				case SKIP:
				case IF:
					{
					setState(63);
					chain();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ProjectFileContext extends ParserRuleContext {
		public List<ImportDeclarationContext> importDeclaration() {
			return getRuleContexts(ImportDeclarationContext.class);
		}
		public ImportDeclarationContext importDeclaration(int i) {
			return getRuleContext(ImportDeclarationContext.class,i);
		}
		public List<ProjectContext> project() {
			return getRuleContexts(ProjectContext.class);
		}
		public ProjectContext project(int i) {
			return getRuleContext(ProjectContext.class,i);
		}
		public List<InstallContext> install() {
			return getRuleContexts(InstallContext.class);
		}
		public InstallContext install(int i) {
			return getRuleContext(InstallContext.class,i);
		}
		public List<UninstallContext> uninstall() {
			return getRuleContexts(UninstallContext.class);
		}
		public UninstallContext uninstall(int i) {
			return getRuleContext(UninstallContext.class,i);
		}
		public List<ProcessContext> process() {
			return getRuleContexts(ProcessContext.class);
		}
		public ProcessContext process(int i) {
			return getRuleContext(ProcessContext.class,i);
		}
		public ProjectFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_projectFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterProjectFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitProjectFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitProjectFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProjectFileContext projectFile() throws RecognitionException {
		ProjectFileContext _localctx = new ProjectFileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_projectFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(69);
				importDeclaration();
				}
				}
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PROJECT) | (1L << INSTALL) | (1L << UNINSTALL) | (1L << PROCESS) | (1L << IMPULSE) | (1L << REPEAT))) != 0)) {
				{
				setState(79);
				switch (_input.LA(1)) {
				case PROJECT:
					{
					setState(75);
					project();
					}
					break;
				case INSTALL:
					{
					setState(76);
					install();
					}
					break;
				case UNINSTALL:
					{
					setState(77);
					uninstall();
					}
					break;
				case PROCESS:
				case IMPULSE:
				case REPEAT:
					{
					setState(78);
					process();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ImportDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(MplParser.IMPORT, 0); }
		public TerminalNode STRING() { return getToken(MplParser.STRING, 0); }
		public ImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitImportDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitImportDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_importDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(IMPORT);
			setState(85);
			match(STRING);
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

	public static class ProjectContext extends ParserRuleContext {
		public TerminalNode PROJECT() { return getToken(MplParser.PROJECT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public List<OrientationContext> orientation() {
			return getRuleContexts(OrientationContext.class);
		}
		public OrientationContext orientation(int i) {
			return getRuleContext(OrientationContext.class,i);
		}
		public List<IncludeContext> include() {
			return getRuleContexts(IncludeContext.class);
		}
		public IncludeContext include(int i) {
			return getRuleContext(IncludeContext.class,i);
		}
		public ProjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_project; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterProject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitProject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitProject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProjectContext project() throws RecognitionException {
		ProjectContext _localctx = new ProjectContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_project);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(PROJECT);
			setState(88);
			match(IDENTIFIER);
			setState(89);
			match(T__0);
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INCLUDE || _la==ORIENTATION) {
				{
				setState(92);
				switch (_input.LA(1)) {
				case ORIENTATION:
					{
					setState(90);
					orientation();
					}
					break;
				case INCLUDE:
					{
					setState(91);
					include();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(97);
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

	public static class OrientationContext extends ParserRuleContext {
		public TerminalNode ORIENTATION() { return getToken(MplParser.ORIENTATION, 0); }
		public TerminalNode STRING() { return getToken(MplParser.STRING, 0); }
		public OrientationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orientation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterOrientation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitOrientation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitOrientation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrientationContext orientation() throws RecognitionException {
		OrientationContext _localctx = new OrientationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_orientation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(ORIENTATION);
			setState(100);
			match(STRING);
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

	public static class IncludeContext extends ParserRuleContext {
		public TerminalNode INCLUDE() { return getToken(MplParser.INCLUDE, 0); }
		public TerminalNode STRING() { return getToken(MplParser.STRING, 0); }
		public IncludeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_include; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterInclude(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitInclude(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitInclude(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IncludeContext include() throws RecognitionException {
		IncludeContext _localctx = new IncludeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_include);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(INCLUDE);
			setState(103);
			match(STRING);
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

	public static class InstallContext extends ParserRuleContext {
		public TerminalNode INSTALL() { return getToken(MplParser.INSTALL, 0); }
		public ChainContext chain() {
			return getRuleContext(ChainContext.class,0);
		}
		public InstallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_install; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterInstall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitInstall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitInstall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstallContext install() throws RecognitionException {
		InstallContext _localctx = new InstallContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_install);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(INSTALL);
			setState(106);
			match(T__0);
			setState(107);
			chain();
			setState(108);
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

	public static class UninstallContext extends ParserRuleContext {
		public TerminalNode UNINSTALL() { return getToken(MplParser.UNINSTALL, 0); }
		public ChainContext chain() {
			return getRuleContext(ChainContext.class,0);
		}
		public UninstallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uninstall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterUninstall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitUninstall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitUninstall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UninstallContext uninstall() throws RecognitionException {
		UninstallContext _localctx = new UninstallContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_uninstall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(UNINSTALL);
			setState(111);
			match(T__0);
			setState(112);
			chain();
			setState(113);
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

	public static class ProcessContext extends ParserRuleContext {
		public TerminalNode PROCESS() { return getToken(MplParser.PROCESS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public ChainContext chain() {
			return getRuleContext(ChainContext.class,0);
		}
		public TerminalNode IMPULSE() { return getToken(MplParser.IMPULSE, 0); }
		public TerminalNode REPEAT() { return getToken(MplParser.REPEAT, 0); }
		public ProcessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_process; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterProcess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitProcess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitProcess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessContext process() throws RecognitionException {
		ProcessContext _localctx = new ProcessContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_process);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			_la = _input.LA(1);
			if (_la==IMPULSE || _la==REPEAT) {
				{
				setState(115);
				_la = _input.LA(1);
				if ( !(_la==IMPULSE || _la==REPEAT) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
			}

			setState(118);
			match(PROCESS);
			setState(119);
			match(IDENTIFIER);
			setState(120);
			match(T__0);
			setState(121);
			chain();
			setState(122);
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

	public static class ChainContext extends ParserRuleContext {
		public List<IfDeclarationContext> ifDeclaration() {
			return getRuleContexts(IfDeclarationContext.class);
		}
		public IfDeclarationContext ifDeclaration(int i) {
			return getRuleContext(IfDeclarationContext.class,i);
		}
		public List<MplCommandContext> mplCommand() {
			return getRuleContexts(MplCommandContext.class);
		}
		public MplCommandContext mplCommand(int i) {
			return getRuleContext(MplCommandContext.class,i);
		}
		public List<SkipContext> skip() {
			return getRuleContexts(SkipContext.class);
		}
		public SkipContext skip(int i) {
			return getRuleContext(SkipContext.class,i);
		}
		public ChainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_chain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterChain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitChain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitChain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ChainContext chain() throws RecognitionException {
		ChainContext _localctx = new ChainContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_chain);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(127); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(127);
					switch (_input.LA(1)) {
					case IF:
						{
						setState(124);
						ifDeclaration();
						}
						break;
					case COMMAND:
					case IMPULSE:
					case CHAIN:
					case REPEAT:
					case UNCONDITIONAL:
					case CONDITIONAL:
					case INVERT:
					case ALWAYS_ACTIVE:
					case NEEDS_REDSTONE:
					case START:
					case STOP:
					case WAITFOR:
					case NOTIFY:
					case INTERCEPT:
					case BREAKPOINT:
						{
						setState(125);
						mplCommand();
						}
						break;
					case SKIP:
						{
						setState(126);
						skip();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(129); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class IfDeclarationContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(MplParser.IF, 0); }
		public TerminalNode COMMAND() { return getToken(MplParser.COMMAND, 0); }
		public TerminalNode NOT() { return getToken(MplParser.NOT, 0); }
		public ThenContext then() {
			return getRuleContext(ThenContext.class,0);
		}
		public ElseDeclarationContext elseDeclaration() {
			return getRuleContext(ElseDeclarationContext.class,0);
		}
		public IfDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterIfDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitIfDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitIfDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfDeclarationContext ifDeclaration() throws RecognitionException {
		IfDeclarationContext _localctx = new IfDeclarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ifDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(IF);
			setState(133);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(132);
				match(NOT);
				}
			}

			setState(135);
			match(T__2);
			setState(136);
			match(COMMAND);
			setState(138);
			_la = _input.LA(1);
			if (_la==THEN) {
				{
				setState(137);
				then();
				}
			}

			setState(141);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(140);
				elseDeclaration();
				}
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

	public static class ThenContext extends ParserRuleContext {
		public TerminalNode THEN() { return getToken(MplParser.THEN, 0); }
		public ChainContext chain() {
			return getRuleContext(ChainContext.class,0);
		}
		public ThenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_then; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterThen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitThen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitThen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ThenContext then() throws RecognitionException {
		ThenContext _localctx = new ThenContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_then);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(THEN);
			setState(144);
			match(T__0);
			setState(145);
			chain();
			setState(146);
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

	public static class ElseDeclarationContext extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(MplParser.ELSE, 0); }
		public ChainContext chain() {
			return getRuleContext(ChainContext.class,0);
		}
		public ElseDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterElseDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitElseDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitElseDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseDeclarationContext elseDeclaration() throws RecognitionException {
		ElseDeclarationContext _localctx = new ElseDeclarationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_elseDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(ELSE);
			setState(149);
			match(T__0);
			setState(150);
			chain();
			setState(151);
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

	public static class MplCommandContext extends ParserRuleContext {
		public CommandContext command() {
			return getRuleContext(CommandContext.class,0);
		}
		public StartContext start() {
			return getRuleContext(StartContext.class,0);
		}
		public StopContext stop() {
			return getRuleContext(StopContext.class,0);
		}
		public WaitforContext waitfor() {
			return getRuleContext(WaitforContext.class,0);
		}
		public NotifyDeclarationContext notifyDeclaration() {
			return getRuleContext(NotifyDeclarationContext.class,0);
		}
		public InterceptContext intercept() {
			return getRuleContext(InterceptContext.class,0);
		}
		public BreakpointContext breakpoint() {
			return getRuleContext(BreakpointContext.class,0);
		}
		public ModifierListContext modifierList() {
			return getRuleContext(ModifierListContext.class,0);
		}
		public MplCommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mplCommand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterMplCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitMplCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitMplCommand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MplCommandContext mplCommand() throws RecognitionException {
		MplCommandContext _localctx = new MplCommandContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_mplCommand);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IMPULSE) | (1L << CHAIN) | (1L << REPEAT) | (1L << UNCONDITIONAL) | (1L << CONDITIONAL) | (1L << INVERT) | (1L << ALWAYS_ACTIVE) | (1L << NEEDS_REDSTONE))) != 0)) {
				{
				setState(153);
				modifierList();
				}
			}

			setState(163);
			switch (_input.LA(1)) {
			case COMMAND:
				{
				setState(156);
				command();
				}
				break;
			case START:
				{
				setState(157);
				start();
				}
				break;
			case STOP:
				{
				setState(158);
				stop();
				}
				break;
			case WAITFOR:
				{
				setState(159);
				waitfor();
				}
				break;
			case NOTIFY:
				{
				setState(160);
				notifyDeclaration();
				}
				break;
			case INTERCEPT:
				{
				setState(161);
				intercept();
				}
				break;
			case BREAKPOINT:
				{
				setState(162);
				breakpoint();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class ModifierListContext extends ParserRuleContext {
		public ModusContext modus() {
			return getRuleContext(ModusContext.class,0);
		}
		public ConditionalContext conditional() {
			return getRuleContext(ConditionalContext.class,0);
		}
		public AutoContext auto() {
			return getRuleContext(AutoContext.class,0);
		}
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
		enterRule(_localctx, 30, RULE_modifierList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			switch (_input.LA(1)) {
			case IMPULSE:
			case CHAIN:
			case REPEAT:
				{
				setState(165);
				modus();
				setState(168);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(166);
					match(T__3);
					setState(167);
					conditional();
					}
					break;
				}
				setState(172);
				_la = _input.LA(1);
				if (_la==T__3) {
					{
					setState(170);
					match(T__3);
					setState(171);
					auto();
					}
				}

				}
				break;
			case UNCONDITIONAL:
			case CONDITIONAL:
			case INVERT:
				{
				setState(174);
				conditional();
				setState(177);
				_la = _input.LA(1);
				if (_la==T__3) {
					{
					setState(175);
					match(T__3);
					setState(176);
					auto();
					}
				}

				}
				break;
			case ALWAYS_ACTIVE:
			case NEEDS_REDSTONE:
				{
				setState(179);
				auto();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(182);
			match(T__2);
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
		public TerminalNode IMPULSE() { return getToken(MplParser.IMPULSE, 0); }
		public TerminalNode CHAIN() { return getToken(MplParser.CHAIN, 0); }
		public TerminalNode REPEAT() { return getToken(MplParser.REPEAT, 0); }
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
		enterRule(_localctx, 32, RULE_modus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IMPULSE) | (1L << CHAIN) | (1L << REPEAT))) != 0)) ) {
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

	public static class ConditionalContext extends ParserRuleContext {
		public TerminalNode UNCONDITIONAL() { return getToken(MplParser.UNCONDITIONAL, 0); }
		public TerminalNode CONDITIONAL() { return getToken(MplParser.CONDITIONAL, 0); }
		public TerminalNode INVERT() { return getToken(MplParser.INVERT, 0); }
		public ConditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitConditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitConditional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalContext conditional() throws RecognitionException {
		ConditionalContext _localctx = new ConditionalContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_conditional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << UNCONDITIONAL) | (1L << CONDITIONAL) | (1L << INVERT))) != 0)) ) {
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

	public static class AutoContext extends ParserRuleContext {
		public TerminalNode NEEDS_REDSTONE() { return getToken(MplParser.NEEDS_REDSTONE, 0); }
		public TerminalNode ALWAYS_ACTIVE() { return getToken(MplParser.ALWAYS_ACTIVE, 0); }
		public AutoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_auto; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterAuto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitAuto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitAuto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AutoContext auto() throws RecognitionException {
		AutoContext _localctx = new AutoContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_auto);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_la = _input.LA(1);
			if ( !(_la==ALWAYS_ACTIVE || _la==NEEDS_REDSTONE) ) {
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

	public static class CommandContext extends ParserRuleContext {
		public TerminalNode COMMAND() { return getToken(MplParser.COMMAND, 0); }
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitCommand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_command);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(COMMAND);
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

	public static class StartContext extends ParserRuleContext {
		public TerminalNode START() { return getToken(MplParser.START, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterStart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitStart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(START);
			setState(193);
			match(IDENTIFIER);
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

	public static class StopContext extends ParserRuleContext {
		public TerminalNode STOP() { return getToken(MplParser.STOP, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public StopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterStop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitStop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitStop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StopContext stop() throws RecognitionException {
		StopContext _localctx = new StopContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(STOP);
			setState(197);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(196);
				match(IDENTIFIER);
				}
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

	public static class WaitforContext extends ParserRuleContext {
		public TerminalNode WAITFOR() { return getToken(MplParser.WAITFOR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public TerminalNode NOTIFY() { return getToken(MplParser.NOTIFY, 0); }
		public WaitforContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_waitfor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterWaitfor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitWaitfor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitWaitfor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WaitforContext waitfor() throws RecognitionException {
		WaitforContext _localctx = new WaitforContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_waitfor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(WAITFOR);
			setState(204);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(201);
				_la = _input.LA(1);
				if (_la==NOTIFY) {
					{
					setState(200);
					match(NOTIFY);
					}
				}

				setState(203);
				match(IDENTIFIER);
				}
				break;
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

	public static class NotifyDeclarationContext extends ParserRuleContext {
		public TerminalNode NOTIFY() { return getToken(MplParser.NOTIFY, 0); }
		public NotifyDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_notifyDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterNotifyDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitNotifyDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitNotifyDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotifyDeclarationContext notifyDeclaration() throws RecognitionException {
		NotifyDeclarationContext _localctx = new NotifyDeclarationContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_notifyDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(NOTIFY);
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

	public static class InterceptContext extends ParserRuleContext {
		public TerminalNode INTERCEPT() { return getToken(MplParser.INTERCEPT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MplParser.IDENTIFIER, 0); }
		public InterceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intercept; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterIntercept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitIntercept(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitIntercept(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InterceptContext intercept() throws RecognitionException {
		InterceptContext _localctx = new InterceptContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_intercept);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(INTERCEPT);
			setState(209);
			match(IDENTIFIER);
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

	public static class BreakpointContext extends ParserRuleContext {
		public TerminalNode BREAKPOINT() { return getToken(MplParser.BREAKPOINT, 0); }
		public BreakpointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakpoint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterBreakpoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitBreakpoint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitBreakpoint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakpointContext breakpoint() throws RecognitionException {
		BreakpointContext _localctx = new BreakpointContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_breakpoint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(BREAKPOINT);
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

	public static class SkipContext extends ParserRuleContext {
		public TerminalNode SKIP() { return getToken(MplParser.SKIP, 0); }
		public SkipContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skip; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).enterSkip(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MplListener ) ((MplListener)listener).exitSkip(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MplVisitor ) return ((MplVisitor<? extends T>)visitor).visitSkip(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkipContext skip() throws RecognitionException {
		SkipContext _localctx = new SkipContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_skip);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(SKIP);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\'\u00da\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\5\2;\n\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\7\3C\n\3\f\3\16\3F\13\3\3\4\7\4I\n\4\f\4\16\4L\13\4\3\4\3\4\3\4\3\4"+
		"\7\4R\n\4\f\4\16\4U\13\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\7\6_\n\6\f\6"+
		"\16\6b\13\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\n\3\13\5\13w\n\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3"+
		"\f\6\f\u0082\n\f\r\f\16\f\u0083\3\r\3\r\5\r\u0088\n\r\3\r\3\r\3\r\5\r"+
		"\u008d\n\r\3\r\5\r\u0090\n\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\20\5\20\u009d\n\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20"+
		"\u00a6\n\20\3\21\3\21\3\21\5\21\u00ab\n\21\3\21\3\21\5\21\u00af\n\21\3"+
		"\21\3\21\3\21\5\21\u00b4\n\21\3\21\5\21\u00b7\n\21\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\5\27\u00c8\n\27"+
		"\3\30\3\30\5\30\u00cc\n\30\3\30\5\30\u00cf\n\30\3\31\3\31\3\32\3\32\3"+
		"\32\3\33\3\33\3\34\3\34\3\34\2\2\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(*,.\60\62\64\66\2\6\4\2\20\20\22\22\3\2\20\22\3\2\23\25\3\2\26"+
		"\27\u00e0\2:\3\2\2\2\4D\3\2\2\2\6J\3\2\2\2\bV\3\2\2\2\nY\3\2\2\2\fe\3"+
		"\2\2\2\16h\3\2\2\2\20k\3\2\2\2\22p\3\2\2\2\24v\3\2\2\2\26\u0081\3\2\2"+
		"\2\30\u0085\3\2\2\2\32\u0091\3\2\2\2\34\u0096\3\2\2\2\36\u009c\3\2\2\2"+
		" \u00b6\3\2\2\2\"\u00ba\3\2\2\2$\u00bc\3\2\2\2&\u00be\3\2\2\2(\u00c0\3"+
		"\2\2\2*\u00c2\3\2\2\2,\u00c5\3\2\2\2.\u00c9\3\2\2\2\60\u00d0\3\2\2\2\62"+
		"\u00d2\3\2\2\2\64\u00d5\3\2\2\2\66\u00d7\3\2\2\28;\5\4\3\29;\5\6\4\2:"+
		"8\3\2\2\2:9\3\2\2\2;<\3\2\2\2<=\7\2\2\3=\3\3\2\2\2>C\5\f\7\2?C\5\20\t"+
		"\2@C\5\22\n\2AC\5\26\f\2B>\3\2\2\2B?\3\2\2\2B@\3\2\2\2BA\3\2\2\2CF\3\2"+
		"\2\2DB\3\2\2\2DE\3\2\2\2E\5\3\2\2\2FD\3\2\2\2GI\5\b\5\2HG\3\2\2\2IL\3"+
		"\2\2\2JH\3\2\2\2JK\3\2\2\2KS\3\2\2\2LJ\3\2\2\2MR\5\n\6\2NR\5\20\t\2OR"+
		"\5\22\n\2PR\5\24\13\2QM\3\2\2\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2RU\3\2\2"+
		"\2SQ\3\2\2\2ST\3\2\2\2T\7\3\2\2\2US\3\2\2\2VW\7\t\2\2WX\7%\2\2X\t\3\2"+
		"\2\2YZ\7\n\2\2Z[\7&\2\2[`\7\3\2\2\\_\5\f\7\2]_\5\16\b\2^\\\3\2\2\2^]\3"+
		"\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2b`\3\2\2\2cd\7\4\2\2d\13"+
		"\3\2\2\2ef\7\f\2\2fg\7%\2\2g\r\3\2\2\2hi\7\13\2\2ij\7%\2\2j\17\3\2\2\2"+
		"kl\7\r\2\2lm\7\3\2\2mn\5\26\f\2no\7\4\2\2o\21\3\2\2\2pq\7\16\2\2qr\7\3"+
		"\2\2rs\5\26\f\2st\7\4\2\2t\23\3\2\2\2uw\t\2\2\2vu\3\2\2\2vw\3\2\2\2wx"+
		"\3\2\2\2xy\7\17\2\2yz\7&\2\2z{\7\3\2\2{|\5\26\f\2|}\7\4\2\2}\25\3\2\2"+
		"\2~\u0082\5\30\r\2\177\u0082\5\36\20\2\u0080\u0082\5\66\34\2\u0081~\3"+
		"\2\2\2\u0081\177\3\2\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\27\3\2\2\2\u0085\u0087\7\37\2"+
		"\2\u0086\u0088\7 \2\2\u0087\u0086\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089"+
		"\3\2\2\2\u0089\u008a\7\5\2\2\u008a\u008c\7\b\2\2\u008b\u008d\5\32\16\2"+
		"\u008c\u008b\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e\u0090"+
		"\5\34\17\2\u008f\u008e\3\2\2\2\u008f\u0090\3\2\2\2\u0090\31\3\2\2\2\u0091"+
		"\u0092\7!\2\2\u0092\u0093\7\3\2\2\u0093\u0094\5\26\f\2\u0094\u0095\7\4"+
		"\2\2\u0095\33\3\2\2\2\u0096\u0097\7\"\2\2\u0097\u0098\7\3\2\2\u0098\u0099"+
		"\5\26\f\2\u0099\u009a\7\4\2\2\u009a\35\3\2\2\2\u009b\u009d\5 \21\2\u009c"+
		"\u009b\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u00a5\3\2\2\2\u009e\u00a6\5("+
		"\25\2\u009f\u00a6\5*\26\2\u00a0\u00a6\5,\27\2\u00a1\u00a6\5.\30\2\u00a2"+
		"\u00a6\5\60\31\2\u00a3\u00a6\5\62\32\2\u00a4\u00a6\5\64\33\2\u00a5\u009e"+
		"\3\2\2\2\u00a5\u009f\3\2\2\2\u00a5\u00a0\3\2\2\2\u00a5\u00a1\3\2\2\2\u00a5"+
		"\u00a2\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a4\3\2\2\2\u00a6\37\3\2\2"+
		"\2\u00a7\u00aa\5\"\22\2\u00a8\u00a9\7\6\2\2\u00a9\u00ab\5$\23\2\u00aa"+
		"\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00ad\7\6"+
		"\2\2\u00ad\u00af\5&\24\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b7\3\2\2\2\u00b0\u00b3\5$\23\2\u00b1\u00b2\7\6\2\2\u00b2\u00b4\5&"+
		"\24\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5"+
		"\u00b7\5&\24\2\u00b6\u00a7\3\2\2\2\u00b6\u00b0\3\2\2\2\u00b6\u00b5\3\2"+
		"\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\7\5\2\2\u00b9!\3\2\2\2\u00ba\u00bb"+
		"\t\3\2\2\u00bb#\3\2\2\2\u00bc\u00bd\t\4\2\2\u00bd%\3\2\2\2\u00be\u00bf"+
		"\t\5\2\2\u00bf\'\3\2\2\2\u00c0\u00c1\7\b\2\2\u00c1)\3\2\2\2\u00c2\u00c3"+
		"\7\30\2\2\u00c3\u00c4\7&\2\2\u00c4+\3\2\2\2\u00c5\u00c7\7\31\2\2\u00c6"+
		"\u00c8\7&\2\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8-\3\2\2\2\u00c9"+
		"\u00ce\7\32\2\2\u00ca\u00cc\7\33\2\2\u00cb\u00ca\3\2\2\2\u00cb\u00cc\3"+
		"\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cf\7&\2\2\u00ce\u00cb\3\2\2\2\u00ce"+
		"\u00cf\3\2\2\2\u00cf/\3\2\2\2\u00d0\u00d1\7\33\2\2\u00d1\61\3\2\2\2\u00d2"+
		"\u00d3\7\34\2\2\u00d3\u00d4\7&\2\2\u00d4\63\3\2\2\2\u00d5\u00d6\7\35\2"+
		"\2\u00d6\65\3\2\2\2\u00d7\u00d8\7\36\2\2\u00d8\67\3\2\2\2\31:BDJQS^`v"+
		"\u0081\u0083\u0087\u008c\u008f\u009c\u00a5\u00aa\u00ae\u00b3\u00b6\u00c7"+
		"\u00cb\u00ce";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}