package de.adrodoc55.minecraft.mpl.gui.scribble;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

public class MplFramePM extends AbstractPM {

    ListPM<MplEditorPM> editors = new ListPM<MplEditorPM>();
    OperationPM newFile = new OperationPM();

    public MplFramePM() {
        PMManager.setup(this);
    }

    @Operation
    public void newFile() {
        editors.add(new MplEditorPM());
    }

}
