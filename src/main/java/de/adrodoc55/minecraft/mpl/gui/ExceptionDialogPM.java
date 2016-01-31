package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

public class ExceptionDialogPM extends AbstractPM {

  TextPM title = new TextPM();
  TextPM description = new TextPM();
  TextPM details = new TextPM();

  public ExceptionDialogPM() {
    details.setEditable(false);
    PMManager.setup(this);
  }

}
