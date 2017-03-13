package de.adrodoc55.minecraft.mpl;

public class Mpl__dftl_partition_content_type extends org.eclipse.jface.text.rules.RuleBasedScanner {
	public Mpl__dftl_partition_content_type() {
		org.eclipse.jface.text.rules.Token mpl_defaultToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_default"));
		setDefaultReturnToken(mpl_defaultToken);
		org.eclipse.jface.text.rules.Token mpl_punctuationToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_punctuation"));
		org.eclipse.jface.text.rules.Token mpl_bracketToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_bracket"));
		org.eclipse.jface.text.rules.Token mpl_keywordToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_keyword"));
		org.eclipse.jface.text.rules.Token mpl_impulseToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_impulse"));
		org.eclipse.jface.text.rules.Token mpl_chainToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_chain"));
		org.eclipse.jface.text.rules.Token mpl_repeatToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_repeat"));
		org.eclipse.jface.text.rules.Token mpl_unconditionalToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_unconditional"));
		org.eclipse.jface.text.rules.Token mpl_always_activeToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_always_active"));
		org.eclipse.jface.text.rules.Token mpl_needs_redstoneToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_needs_redstone"));
		org.eclipse.jface.text.rules.IRule[] rules = new org.eclipse.jface.text.rules.IRule[4];
		rules[0] = new org.eclipse.fx.text.rules.CharacterRule(mpl_punctuationToken, new char[] {':',',','=','+','-','#'});
		rules[1] = new org.eclipse.fx.text.rules.CharacterRule(mpl_bracketToken, new char[] {'(',')','{','}'});
		rules[2] = new org.eclipse.jface.text.rules.WhitespaceRule(Character::isWhitespace);

		org.eclipse.fx.text.rules.JavaLikeWordDetector wordDetector= new org.eclipse.fx.text.rules.JavaLikeWordDetector();
		org.eclipse.fx.text.rules.CombinedWordRule combinedWordRule= new org.eclipse.fx.text.rules.CombinedWordRule(wordDetector, mpl_defaultToken);
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_keywordWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_keywordWordRule.addWord("breakpoint", mpl_keywordToken);
			mpl_keywordWordRule.addWord("conditional", mpl_keywordToken);
			mpl_keywordWordRule.addWord("else", mpl_keywordToken);
			mpl_keywordWordRule.addWord("if", mpl_keywordToken);
			mpl_keywordWordRule.addWord("import", mpl_keywordToken);
			mpl_keywordWordRule.addWord("include", mpl_keywordToken);
			mpl_keywordWordRule.addWord("inline", mpl_keywordToken);
			mpl_keywordWordRule.addWord("install", mpl_keywordToken);
			mpl_keywordWordRule.addWord("intercept", mpl_keywordToken);
			mpl_keywordWordRule.addWord("invert", mpl_keywordToken);
			mpl_keywordWordRule.addWord("not", mpl_keywordToken);
			mpl_keywordWordRule.addWord("notify", mpl_keywordToken);
			mpl_keywordWordRule.addWord("orientation", mpl_keywordToken);
			mpl_keywordWordRule.addWord("process", mpl_keywordToken);
			mpl_keywordWordRule.addWord("project", mpl_keywordToken);
			mpl_keywordWordRule.addWord("remote", mpl_keywordToken);
			mpl_keywordWordRule.addWord("skip", mpl_keywordToken);
			mpl_keywordWordRule.addWord("start", mpl_keywordToken);
			mpl_keywordWordRule.addWord("stop", mpl_keywordToken);
			mpl_keywordWordRule.addWord("then", mpl_keywordToken);
			mpl_keywordWordRule.addWord("uninstall", mpl_keywordToken);
			mpl_keywordWordRule.addWord("waitfor", mpl_keywordToken);
			combinedWordRule.addWordMatcher(mpl_keywordWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_impulseWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_impulseWordRule.addWord("impulse", mpl_impulseToken);
			combinedWordRule.addWordMatcher(mpl_impulseWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_chainWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_chainWordRule.addWord("chain", mpl_chainToken);
			combinedWordRule.addWordMatcher(mpl_chainWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_repeatWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_repeatWordRule.addWord("break", mpl_repeatToken);
			mpl_repeatWordRule.addWord("continue", mpl_repeatToken);
			mpl_repeatWordRule.addWord("do", mpl_repeatToken);
			mpl_repeatWordRule.addWord("repeat", mpl_repeatToken);
			mpl_repeatWordRule.addWord("while", mpl_repeatToken);
			combinedWordRule.addWordMatcher(mpl_repeatWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_unconditionalWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_unconditionalWordRule.addWord("unconditional", mpl_unconditionalToken);
			combinedWordRule.addWordMatcher(mpl_unconditionalWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_always_activeWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_always_activeWordRule.addWord("always", mpl_always_activeToken);
			mpl_always_activeWordRule.addWord("active", mpl_always_activeToken);
			combinedWordRule.addWordMatcher(mpl_always_activeWordRule);
		}
		{
			org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher mpl_needs_redstoneWordRule = new org.eclipse.fx.text.rules.CombinedWordRule.WordMatcher();
			mpl_needs_redstoneWordRule.addWord("needs", mpl_needs_redstoneToken);
			mpl_needs_redstoneWordRule.addWord("redstone", mpl_needs_redstoneToken);
			combinedWordRule.addWordMatcher(mpl_needs_redstoneWordRule);
		}
		rules[3] = combinedWordRule;
		setRules(rules);
	}
}
