package de.adrodoc55.minecraft.mpl;

public class Mpl_mpl_string extends org.eclipse.jface.text.rules.RuleBasedScanner {
	public Mpl_mpl_string() {
		org.eclipse.jface.text.rules.Token mpl_stringToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_string"));
		setDefaultReturnToken(mpl_stringToken);
		org.eclipse.jface.text.rules.IRule[] rules = new org.eclipse.jface.text.rules.IRule[0];

		setRules(rules);
	}
}
