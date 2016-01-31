package de.adrodoc55.minecraft.mpl.gui.dialog;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

public class OneCommandDialogPM extends AbstractPM {

  TextPM oneCommand = new TextPM();

  public OneCommandDialogPM() {
    PMManager.setup(this);
  }

  public void setText(String text) {
    oneCommand.setText(text);
  }
}
