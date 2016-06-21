/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
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
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package org.beanfabrics.swing.internal;

import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;

import org.beanfabrics.View;
import org.beanfabrics.event.WeakPropertyChangeListener;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.swing.ErrorIconPainter;

/**
 * The <code>TextPMTextPane</code> is a {@link JTextPane} that is a view on an {@link ITextPM}.
 *
 * @author Adrodoc55
 *
 */
public class TextPMTextPane extends JTextPane implements View<ITextPM> {
  private static final long serialVersionUID = 1L;

  private boolean selectAllOnFocusGainedEnabled = false;
  private boolean reformatOnFocusLostEnabled = false;
  private final PropertyChangeListener listener = new MyWeakPropertyChangeListener();

  private class MyWeakPropertyChangeListener implements WeakPropertyChangeListener, Serializable {
    private static final long serialVersionUID = 1L;

    public void propertyChange(PropertyChangeEvent evt) {
      refresh();
    }
  }

  private ErrorIconPainter errorIconPainter = createDefaultErrorIconPainter();

  public TextPMTextPane() {
    init();
  }

  public TextPMTextPane(StyledDocument doc) {
    super(doc);
    init();
  }

  public TextPMTextPane(ITextPM pModel) {
    init();
    setPresentationModel(pModel);
  }

  public boolean isSelectAllOnFocusGainedEnabled() {
    return selectAllOnFocusGainedEnabled;
  }

  public void setSelectAllOnFocusGainedEnabled(boolean selectAllOnFocusGainedEnabled) {
    this.selectAllOnFocusGainedEnabled = selectAllOnFocusGainedEnabled;
  }

  public boolean isReformatOnFocusLostEnabled() {
    return reformatOnFocusLostEnabled;
  }

  public void setReformatOnFocusLostEnabled(boolean reformatOnFocusLostEnabled) {
    this.reformatOnFocusLostEnabled = reformatOnFocusLostEnabled;
  }

  private void init() {
    this.setEnabled(false);
    this.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        TextPMTextPane.this.onFocusGained();
      }

      public void focusLost(FocusEvent e) {
        TextPMTextPane.this.onFocusLost();
      }
    });
  }

  protected void onFocusGained() {
    this.repaint();
    if (this.isSelectAllOnFocusGainedEnabled()) {
      this.selectAll();
    }
  }

  protected void onFocusLost() {
    this.repaint();
    if (this.isReformatOnFocusLostEnabled() && this.isConnected()) {
      getDocument().getPresentationModel().reformat();
    }
  }

  /**
   * Creates the <code>EditorKit</code> to use by default. This is implemented to return
   * {@link BnStyledEditorKit}.
   *
   * @return the editor kit
   */
  protected EditorKit createDefaultEditorKit() {
    return new BnStyledEditorKit();
  }

  public BnStyledDocument getDocument() {
    return (BnStyledDocument) super.getDocument();
  }

  /**
   * Associates the editor with a text document. This must be a <code>BnStyledDocument</code>.
   *
   * @param doc the document to display/edit
   * @exception IllegalArgumentException if <code>doc</code> can't be narrowed to a
   *            <code>BnStyledDocument</code> which is the required type of model for this text
   *            component
   */
  public void setDocument(Document doc) {
    if (doc instanceof BnStyledDocument) {
      super.setDocument(doc);
    } else {
      throw new IllegalArgumentException("Model must be BnStyledDocument");
    }
  }

  @Override
  public BnStyledDocument getStyledDocument() {
    return getDocument();
  }

  /**
   * Associates the editor with a text document. This must be a <code>BnStyledDocument</code>.
   *
   * @param doc the document to display/edit
   * @exception IllegalArgumentException if <code>doc</code> can't be narrowed to a
   *            <code>BnStyledDocument</code> which is the required type of model for this text
   *            component
   */
  @Override
  public void setStyledDocument(StyledDocument doc) {
    setDocument(doc);
  }

  /** {@inheritDoc} */
  public ITextPM getPresentationModel() {
    if (getDocument() == null)
      return null;
    return getDocument().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(ITextPM newModel) {
    ITextPM oldModel = getDocument().getPresentationModel();
    if (oldModel != null) {
      oldModel.removePropertyChangeListener(this.listener);
    }
    getDocument().setPresentationModel(newModel);
    if (newModel != null) {
      newModel.addPropertyChangeListener(listener);
    }
    this.refresh();
    this.firePropertyChange("presentationModel", oldModel, newModel);
  }

  /**
   * Returns whether this component is connected to the target {@link PresentationModel} to
   * synchronize with. This is a convenience method.
   *
   * @return <code>true</code> when this component is connected, else <code>false</code>
   */
  protected boolean isConnected() {
    return getDocument() != null && getDocument().getPresentationModel() != null;
  }

  /**
   * Configures the text field depending on the properties attributes
   */
  protected void refresh() {
    final ITextPM pModel = this.getPresentationModel();
    if (pModel != null) {
      this.setEnabled(true);
      this.setToolTipText(pModel.isValid() == false ? pModel.getValidationState().getMessage()
          : pModel.getDescription());
      this.setEditable(pModel.isEditable());
    } else {
      this.setEnabled(false);
    }
    this.repaint();
  }

  private ErrorIconPainter createDefaultErrorIconPainter() {
    ErrorIconPainter result = new ErrorIconPainter();
    result.setVerticalAlignment(SwingConstants.TOP);
    return result;
  }

  public ErrorIconPainter getErrorIconPainter() {
    return errorIconPainter;
  }

  public void setErrorIconPainter(ErrorIconPainter aErrorIconPainter) {
    if (aErrorIconPainter == null) {
      throw new IllegalArgumentException("aErrorIconPainter == null");
    }
    this.errorIconPainter = aErrorIconPainter;
  }

  /** {@inheritDoc} */
  @Override
  public void paintChildren(Graphics g) {
    super.paintChildren(g);
    if (shouldPaintErrorIcon()) {
      errorIconPainter.paint(g, this);
    }
  }

  private boolean shouldPaintErrorIcon() {
    ITextPM pModel = this.getPresentationModel();
    if (pModel == null) {
      return false;
    }
    return (pModel.isValid() == false);
  }

}
