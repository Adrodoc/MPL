package de.adrodoc55.minecraft.mpl.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.model.TextPM;

/**
 * The BnDocumentFilter is a {@link View} on a {@link org.beanfabrics.model.TextPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class BnDocumentFilter extends DocumentFilter
    implements View<BnDocumentPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private boolean enabled = true;

  public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
    super.remove(fb, offset, length);
    if (!enabled) {
      return;
    }
    BnDocumentPM pModel = getPresentationModel();
    if (pModel == null) {
      return;
    }
    int docLength = fb.getDocument().getLength();
    TextPM textPM = pModel.getTextPM();
    if (docLength != textPM.getText().length()) {
      textPM.setText(fb.getDocument().getText(0, docLength));
    }
  }

  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
      throws BadLocationException {
    super.insertString(fb, offset, string, attr);
    if (!enabled) {
      return;
    }
    BnDocumentPM pModel = getPresentationModel();
    if (pModel == null) {
      return;
    }
    int docLength = fb.getDocument().getLength();
    TextPM textPM = pModel.getTextPM();
    if (docLength != textPM.getText().length()) {
      textPM.setText(fb.getDocument().getText(0, docLength));
    }
  }

  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
      throws BadLocationException {
    super.replace(fb, offset, length, text, attrs);
    if (!enabled) {
      return;
    }
    BnDocumentPM pModel = getPresentationModel();
    if (pModel == null) {
      return;
    }
    int docLength = fb.getDocument().getLength();
    String docText = fb.getDocument().getText(0, docLength);
    TextPM textPM = pModel.getTextPM();
    if (!docText.equals(textPM.getText())) {
      textPM.setText(docText);
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider();
      localModelProvider.setPresentationModelType(BnDocumentPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public BnDocumentPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(BnDocumentPM pModel) {
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  public void setPath(Path path) {
    this.link.setPath(path);
  }

}
