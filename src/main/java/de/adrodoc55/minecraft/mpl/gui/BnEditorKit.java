package de.adrodoc55.minecraft.mpl.gui;

import java.io.IOException;
import java.io.Reader;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyledEditorKit;

public class BnEditorKit extends StyledEditorKit {

  private static final long serialVersionUID = 1L;

  @Override
  public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
    if (!(doc instanceof AbstractDocument)) {
      super.read(in, doc, pos);
      return;
    }
    AbstractDocument abstractDocument = (AbstractDocument) doc;
    DocumentFilter documentFilter = abstractDocument.getDocumentFilter();
    if (!(documentFilter instanceof BnDocumentFilter)) {
      super.read(in, doc, pos);
      return;
    }
    BnDocumentFilter bnDocumentFilter = (BnDocumentFilter) documentFilter;
    bnDocumentFilter.setEnabled(false);
    super.read(in, doc, pos);
    bnDocumentFilter.setEnabled(true);
  }

}
