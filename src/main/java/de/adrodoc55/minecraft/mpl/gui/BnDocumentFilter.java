/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
