package de.adrodoc55.commons;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentUtils {

  private DocumentUtils() throws Throwable {
    throw new Throwable("Utils Classes cannot be instantiated");
  }

  public static void replace(Document doc, int offset, int length, String text)
      throws BadLocationException {
    if (doc instanceof AbstractDocument) {
      ((AbstractDocument) doc).replace(offset, length, text, null);
    } else {
      if (length > 0) {
        doc.remove(offset, length);
      }
      if (text != null && text.length() > 0) {
        doc.insertString(offset, text, null);
      }
    }
  }

}
