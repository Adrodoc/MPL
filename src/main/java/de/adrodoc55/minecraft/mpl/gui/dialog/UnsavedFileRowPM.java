package de.adrodoc55.minecraft.mpl.gui.dialog;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;

import de.adrodoc55.minecraft.mpl.gui.MplEditorPM;

public class UnsavedFileRowPM extends AbstractPM {

  BooleanPM save = new BooleanPM();
  final MplEditorPM editorPm;

  public UnsavedFileRowPM(MplEditorPM editorPm) {
    save.setBoolean(true);
    this.editorPm = editorPm;
    PMManager.setup(this);
  }

}
