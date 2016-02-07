package de.adrodoc55.minecraft.mpl.gui.bntextpane;

import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

/**
 * @author Adrodoc55
 */
public class BnStyledEditorKit extends StyledEditorKit {

  private static final long serialVersionUID = 1L;

  @Override
  public Document createDefaultDocument() {
    return new BnStyledDocument();
  }
}
