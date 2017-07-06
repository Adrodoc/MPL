package de.adrodoc55.minecraft.mpl;

public class MplPresentationReconciler extends org.eclipse.fx.text.ui.presentation.PresentationReconciler {
	public MplPresentationReconciler() {
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer __dftl_partition_content_typeDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl__dftl_partition_content_type());
		setDamager(__dftl_partition_content_typeDamageRepairer, "__dftl_partition_content_type");
		setRepairer(__dftl_partition_content_typeDamageRepairer, "__dftl_partition_content_type");
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer _mpl_singleline_commentDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl_mpl_singleline_comment());
		setDamager(_mpl_singleline_commentDamageRepairer, "_mpl_singleline_comment");
		setRepairer(_mpl_singleline_commentDamageRepairer, "_mpl_singleline_comment");
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer _mpl_multiline_commentDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl_mpl_multiline_comment());
		setDamager(_mpl_multiline_commentDamageRepairer, "_mpl_multiline_comment");
		setRepairer(_mpl_multiline_commentDamageRepairer, "_mpl_multiline_comment");
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer _mpl_commandDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl_mpl_command());
		setDamager(_mpl_commandDamageRepairer, "_mpl_command");
		setRepairer(_mpl_commandDamageRepairer, "_mpl_command");
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer _mpl_selectorDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl_mpl_selector());
		setDamager(_mpl_selectorDamageRepairer, "_mpl_selector");
		setRepairer(_mpl_selectorDamageRepairer, "_mpl_selector");
		org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer _mpl_stringDamageRepairer = new org.eclipse.fx.text.ui.rules.DefaultDamagerRepairer(new Mpl_mpl_string());
		setDamager(_mpl_stringDamageRepairer, "_mpl_string");
		setRepairer(_mpl_stringDamageRepairer, "_mpl_string");
	}
}
