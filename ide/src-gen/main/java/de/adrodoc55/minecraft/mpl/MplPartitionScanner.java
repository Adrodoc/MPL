package de.adrodoc55.minecraft.mpl;

public class MplPartitionScanner extends org.eclipse.jface.text.rules.RuleBasedPartitionScanner {
	public MplPartitionScanner() {
		org.eclipse.jface.text.rules.IPredicateRule[] pr = new org.eclipse.jface.text.rules.IPredicateRule[8];
		pr[0] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "//"
			, ""
			, new org.eclipse.jface.text.rules.Token("_mpl_singleline_comment")
			, (char)0
			, true);
		pr[1] = new org.eclipse.jface.text.rules.MultiLineRule(
			  "/*"
			, "*/"
			, new org.eclipse.jface.text.rules.Token("_mpl_multiline_comment")
			, (char)0
			, false);
		pr[2] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "/"
			, ""
			, new org.eclipse.jface.text.rules.Token("_mpl_command")
			, (char)0
			, true);
		pr[3] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "@a["
			, "]"
			, new org.eclipse.jface.text.rules.Token("_mpl_selector")
			, (char)0
			, false);
		pr[4] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "@e["
			, "]"
			, new org.eclipse.jface.text.rules.Token("_mpl_selector")
			, (char)0
			, false);
		pr[5] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "@p["
			, "]"
			, new org.eclipse.jface.text.rules.Token("_mpl_selector")
			, (char)0
			, false);
		pr[6] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "@r["
			, "]"
			, new org.eclipse.jface.text.rules.Token("_mpl_selector")
			, (char)0
			, false);
		pr[7] = new org.eclipse.jface.text.rules.SingleLineRule(
			  "\""
			, "\""
			, new org.eclipse.jface.text.rules.Token("_mpl_string")
			, (char)0
			, false);
		setPredicateRules(pr);
	}
}
