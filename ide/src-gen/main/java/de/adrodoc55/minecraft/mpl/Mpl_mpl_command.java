package de.adrodoc55.minecraft.mpl;

public class Mpl_mpl_command extends org.eclipse.jface.text.rules.RuleBasedScanner {
	public Mpl_mpl_command() {
		org.eclipse.jface.text.rules.Token mpl_commandToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_command"));
		setDefaultReturnToken(mpl_commandToken);
		org.eclipse.jface.text.rules.Token mpl_insertToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_insert"));
		org.eclipse.jface.text.rules.IRule[] rules = new org.eclipse.jface.text.rules.IRule[1];
		rules[0] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "${"
			, "}"
			, mpl_insertToken
			, (char)0
			, false);

		setRules(rules);
	}
}
