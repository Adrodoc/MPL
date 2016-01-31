package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.util.Collection;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

import de.adrodoc55.minecraft.mpl.gui.MplEditorPM;

public class UnsavedFilesDialogPM extends AbstractPM {

  ListPM<UnsavedFileRowPM> unsaved = new ListPM<UnsavedFileRowPM>();
  OperationPM ok = new OperationPM();
  OperationPM cancel = new OperationPM();
  private boolean canceled;

  public UnsavedFilesDialogPM(Collection<MplEditorPM> unsavedEditors) {
    for (MplEditorPM mplEditorPM : unsavedEditors) {
      unsaved.add(new UnsavedFileRowPM(mplEditorPM));
    }
    PMManager.setup(this);
  }

  @Operation
  public void ok() {
    for (UnsavedFileRowPM unsavedFileRowPM : unsaved) {
      if (!unsavedFileRowPM.save.getBoolean()) {
        continue;
      }
      unsavedFileRowPM.editorPm.save();
    }
  }

  @Operation
  public void cancel() {
    canceled = true;
  }

  public boolean isCanceled() {
    return canceled;
  }

}
