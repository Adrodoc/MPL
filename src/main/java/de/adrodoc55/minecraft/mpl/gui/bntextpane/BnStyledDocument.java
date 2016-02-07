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
package de.adrodoc55.minecraft.mpl.gui.bntextpane;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.beanfabrics.View;
import org.beanfabrics.event.WeakPropertyChangeListener;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.util.ExceptionUtil;

/**
 * The <code>BnStyledDocument</code> is a {@link StyledDocument} which is a {@link View} on a
 * {@link ITextPM}.
 *
 * @author Adrodoc55
 */
public class BnStyledDocument extends DefaultStyledDocument implements View<ITextPM> {
  private static final long serialVersionUID = 1L;
  /**
   * Value <code>true</code> avoids event cycles between the document and the {@link TextPM}.
   */
  private boolean pending_modelChange = false;
  /**
   * Setting this to <code>true</code> disables the delegation (to the model) inside the
   * {@link #remove(int, int)} method.
   */
  private boolean suppressRemoveEvent = false;

  protected ITextPM pModel;

  private final WeakPropertyChangeListener propertyListener = new MyWeakPropertyChangeListener();

  private class MyWeakPropertyChangeListener implements WeakPropertyChangeListener, Serializable {
    private static final long serialVersionUID = 1L;

    public void propertyChange(PropertyChangeEvent evt) {
      if (pending_modelChange == false) { // avoid event cycle
        try {
          pending_modelChange = true;
          BnStyledDocument.this.refresh();
        } finally {
          pending_modelChange = false;
        }
      }
    }
  }

  public BnStyledDocument() {
    super();
  }

  public BnStyledDocument(ITextPM pModel) {
    this();
    this.setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  public ITextPM getPresentationModel() {
    return this.pModel;
  }

  /** {@inheritDoc} */
  public void setPresentationModel(ITextPM pModel) {
    disconnect();
    this.pModel = pModel;
    connect();
    try {
      pending_modelChange = true;
      this.refresh();
    } finally {
      pending_modelChange = false;
    }
  }

  /**
   * Returns whether this component is connected to the target {@link PresentationModel} to
   * synchronize with. This is a convenience method.
   *
   * @return <code>true</code> when this component is connected, else <code>false</code>
   */
  public boolean isConnected() {
    return pModel != null;
  }

  private void disconnect() {
    if (this.isConnected()) {
      this.pModel.removePropertyChangeListener("text", this.propertyListener);
    }
  }

  private void connect() {
    if (this.pModel != null) {
      this.pModel.addPropertyChangeListener("text", this.propertyListener);
    }
  }

  /**
   * Configures this component depending on the target {@link AbstractPM}s attributes.
   */
  protected void refresh() {
    try {
      String edText = this.isConnected() ? this.pModel.getText() : "";
      this.applyText(edText);
    } catch (BadLocationException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private void applyText(String text) throws BadLocationException {
    String oldText = this.getText(0, this.getLength());
    if (oldText.equals(text) == false) {
      this.suppressRemoveEvent = true; // do not synchronize
      try {
        // since'model.insertString' follows
        this.remove(0, this.getLength());
//        int oldCaretPos = getCaretPosition();
//         int newCaretPos = oldCaretPos + (pmText.length() - docText.length());
//         textPane.setText(pmText);
//         textPane.setCaretPosition(newCaretPos);
      } finally {
        this.suppressRemoveEvent = false;
      }
      this.insertString(0, text, null);
    }
  }

  public void remove(int offs, int len) throws BadLocationException {
    try {
      String edText = isConnected() ? this.pModel.getText() : "";
      super.remove(offs, len);
      String newText = this.getText(0, this.getLength());
      if (edText.equals(newText) == false) {
        // the removal to an empty string needs to be synchronized (no
        // model.insertString follows)
        if (suppressRemoveEvent == false) {
          if (this.isConnected()) {
            this.pModel.setText(newText);
          }
        }
      }
    } catch (BadLocationException ex) {
      throw ex;
    } catch (Throwable t) {
      ExceptionUtil.getInstance().handleException("Error during editing.", t);
    }
  }

  @Override
  public void replace(int offset, int length, String text, AttributeSet attrs)
      throws BadLocationException {
    if (this.isConnected()) {
      String edText = this.pModel.getText();
      super.replace(offset, length, text, attrs);
      String newText = this.getText(0, this.getLength());
      if (edText.equals(newText) == false) {
        this.pModel.setText(newText);
      }
    }
  }

  public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
    if (this.isConnected()) {
      String edText = this.pModel.getText();
      super.insertString(offs, str, a);
      String newText = this.getText(0, this.getLength());
      if (edText.equals(newText) == false) {
        this.pModel.setText(newText);
      }
    }
  }

  public void setSuppressRemoveEvent(boolean suppressRemoveEvent) {
    this.suppressRemoveEvent = suppressRemoveEvent;
  }
}
