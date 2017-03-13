package de.adrodoc55.minecraft.mpl;

public class Mpl_mpl_selector extends org.eclipse.jface.text.rules.RuleBasedScanner {
	public Mpl_mpl_selector() {
		org.eclipse.jface.text.rules.Token mpl_selectorToken = new org.eclipse.jface.text.rules.Token(new org.eclipse.fx.text.ui.TextAttribute("mpl.mpl_selector"));
		setDefaultReturnToken(mpl_selectorToken);
		org.eclipse.jface.text.rules.IRule[] rules = new org.eclipse.jface.text.rules.IRule[0];

		setRules(rules);
	}
}
