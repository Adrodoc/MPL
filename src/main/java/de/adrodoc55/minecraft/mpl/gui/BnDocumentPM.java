package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Property;

public interface BnDocumentPM extends PresentationModel {

  @Property
  TextPM getTextPM();
}
