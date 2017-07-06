package de.adrodoc55.minecraft.mpl;

public class Mpl_mpl_multiline_comment extends org.eclipse.jface.text.rules.RuleBasedScanner {
	public Mpl_mpl_multiline_comment() {
		org.eclipse.jface.text.rules.Token mpl_multi_line_commentToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_multi_line_comment"));
		setDefaultReturnToken(mpl_multi_line_commentToken);
		org.eclipse.jface.text.rules.IRule[] rules = new org.eclipse.jface.text.rules.IRule[0];

		setRules(rules);
	}
}
