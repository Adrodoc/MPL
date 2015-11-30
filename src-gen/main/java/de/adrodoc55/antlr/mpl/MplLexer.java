// $ANTLR 3.5.2 Mpl.g 2015-11-29 21:01:55

package de.adrodoc55.antlr.mpl;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class MplLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public MplLexer() {} 
	public MplLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public MplLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "Mpl.g"; }

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:11:6: ( ',' )
			// Mpl.g:11:8: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__8"

	// $ANTLR start "T__9"
	public final void mT__9() throws RecognitionException {
		try {
			int _type = T__9;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:12:6: ( ':' )
			// Mpl.g:12:8: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__9"

	// $ANTLR start "T__10"
	public final void mT__10() throws RecognitionException {
		try {
			int _type = T__10;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:13:7: ( 'chain' )
			// Mpl.g:13:9: 'chain'
			{
			match("chain"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__10"

	// $ANTLR start "T__11"
	public final void mT__11() throws RecognitionException {
		try {
			int _type = T__11;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:14:7: ( 'conditional' )
			// Mpl.g:14:9: 'conditional'
			{
			match("conditional"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__11"

	// $ANTLR start "T__12"
	public final void mT__12() throws RecognitionException {
		try {
			int _type = T__12;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:15:7: ( 'impulse' )
			// Mpl.g:15:9: 'impulse'
			{
			match("impulse"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__12"

	// $ANTLR start "T__13"
	public final void mT__13() throws RecognitionException {
		try {
			int _type = T__13;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:16:7: ( 'repeat' )
			// Mpl.g:16:9: 'repeat'
			{
			match("repeat"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__13"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:47:2: ( '//' (~ ( '\\r' | '\\n' ) )* )
			// Mpl.g:48:2: '//' (~ ( '\\r' | '\\n' ) )*
			{
			match("//"); 

			// Mpl.g:48:7: (~ ( '\\r' | '\\n' ) )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '\uFFFF')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// Mpl.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop1;
				}
			}

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "COMMAND"
	public final void mCOMMAND() throws RecognitionException {
		try {
			int _type = COMMAND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:54:2: ( '/' (~ ( '\\r' | '\\n' ) )* )
			// Mpl.g:55:2: '/' (~ ( '\\r' | '\\n' ) )*
			{
			match('/'); 
			// Mpl.g:55:6: (~ ( '\\r' | '\\n' ) )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '\u0000' && LA2_0 <= '\t')||(LA2_0 >= '\u000B' && LA2_0 <= '\f')||(LA2_0 >= '\u000E' && LA2_0 <= '\uFFFF')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// Mpl.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMAND"

	// $ANTLR start "EOL"
	public final void mEOL() throws RecognitionException {
		try {
			int _type = EOL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:59:2: ( '\\n' | '\\r\\n' )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='\n') ) {
				alt3=1;
			}
			else if ( (LA3_0=='\r') ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// Mpl.g:60:2: '\\n'
					{
					match('\n'); 
					}
					break;
				case 2 :
					// Mpl.g:61:4: '\\r\\n'
					{
					match("\r\n"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EOL"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// Mpl.g:67:2: ( ( ' ' | '\\t' )+ )
			// Mpl.g:68:2: ( ' ' | '\\t' )+
			{
			// Mpl.g:68:2: ( ' ' | '\\t' )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0=='\t'||LA4_0==' ') ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// Mpl.g:
					{
					if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// Mpl.g:1:8: ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | COMMENT | COMMAND | EOL | WS )
		int alt5=10;
		alt5 = dfa5.predict(input);
		switch (alt5) {
			case 1 :
				// Mpl.g:1:10: T__8
				{
				mT__8(); 

				}
				break;
			case 2 :
				// Mpl.g:1:15: T__9
				{
				mT__9(); 

				}
				break;
			case 3 :
				// Mpl.g:1:20: T__10
				{
				mT__10(); 

				}
				break;
			case 4 :
				// Mpl.g:1:26: T__11
				{
				mT__11(); 

				}
				break;
			case 5 :
				// Mpl.g:1:32: T__12
				{
				mT__12(); 

				}
				break;
			case 6 :
				// Mpl.g:1:38: T__13
				{
				mT__13(); 

				}
				break;
			case 7 :
				// Mpl.g:1:44: COMMENT
				{
				mCOMMENT(); 

				}
				break;
			case 8 :
				// Mpl.g:1:52: COMMAND
				{
				mCOMMAND(); 

				}
				break;
			case 9 :
				// Mpl.g:1:60: EOL
				{
				mEOL(); 

				}
				break;
			case 10 :
				// Mpl.g:1:64: WS
				{
				mWS(); 

				}
				break;

		}
	}


	protected DFA5 dfa5 = new DFA5(this);
	static final String DFA5_eotS =
		"\6\uffff\1\14\4\uffff\1\16\1\uffff\1\16\1\uffff";
	static final String DFA5_eofS =
		"\17\uffff";
	static final String DFA5_minS =
		"\1\11\2\uffff\1\150\2\uffff\1\57\4\uffff\1\0\1\uffff\1\0\1\uffff";
	static final String DFA5_maxS =
		"\1\162\2\uffff\1\157\2\uffff\1\57\4\uffff\1\uffff\1\uffff\1\uffff\1\uffff";
	static final String DFA5_acceptS =
		"\1\uffff\1\1\1\2\1\uffff\1\5\1\6\1\uffff\1\11\1\12\1\3\1\4\1\uffff\1\10"+
		"\1\uffff\1\7";
	static final String DFA5_specialS =
		"\13\uffff\1\0\1\uffff\1\1\1\uffff}>";
	static final String[] DFA5_transitionS = {
			"\1\10\1\7\2\uffff\1\7\22\uffff\1\10\13\uffff\1\1\2\uffff\1\6\12\uffff"+
			"\1\2\50\uffff\1\3\5\uffff\1\4\10\uffff\1\5",
			"",
			"",
			"\1\11\6\uffff\1\12",
			"",
			"",
			"\1\13",
			"",
			"",
			"",
			"",
			"\12\15\1\uffff\2\15\1\uffff\ufff2\15",
			"",
			"\12\15\1\uffff\2\15\1\uffff\ufff2\15",
			""
	};

	static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
	static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
	static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
	static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
	static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
	static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
	static final short[][] DFA5_transition;

	static {
		int numStates = DFA5_transitionS.length;
		DFA5_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
		}
	}

	protected class DFA5 extends DFA {

		public DFA5(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 5;
			this.eot = DFA5_eot;
			this.eof = DFA5_eof;
			this.min = DFA5_min;
			this.max = DFA5_max;
			this.accept = DFA5_accept;
			this.special = DFA5_special;
			this.transition = DFA5_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | COMMENT | COMMAND | EOL | WS );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA5_11 = input.LA(1);
						s = -1;
						if ( ((LA5_11 >= '\u0000' && LA5_11 <= '\t')||(LA5_11 >= '\u000B' && LA5_11 <= '\f')||(LA5_11 >= '\u000E' && LA5_11 <= '\uFFFF')) ) {s = 13;}
						else s = 14;
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA5_13 = input.LA(1);
						s = -1;
						if ( ((LA5_13 >= '\u0000' && LA5_13 <= '\t')||(LA5_13 >= '\u000B' && LA5_13 <= '\f')||(LA5_13 >= '\u000E' && LA5_13 <= '\uFFFF')) ) {s = 13;}
						else s = 14;
						if ( s>=0 ) return s;
						break;
			}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 5, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
