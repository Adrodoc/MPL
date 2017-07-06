package de.adrodoc55.minecraft.mpl;

public class MplPartitioner extends org.eclipse.jface.text.rules.FastPartitioner {
	public MplPartitioner() {
		super(new MplPartitionScanner(), new String[] {
			"_mpl_command","_mpl_selector","_mpl_singleline_comment","_mpl_multiline_comment","_mpl_string"
		});
	}
}
