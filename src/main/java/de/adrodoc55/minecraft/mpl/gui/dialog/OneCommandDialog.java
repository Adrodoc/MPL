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
package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnTextArea;

/**
 * The OneCommandDialog is a {@link View} on a
 * {@link de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class OneCommandDialog extends JDialog
    implements View<de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JScrollPane scrollPane;
  private BnTextArea bnTextArea;
  private JPanel panel;
  private JButton btnOk;

  public OneCommandDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>OneCommandDialog</code>.
   */
  public OneCommandDialog(Window parent) {
    super(parent, "Import via one Command");
    init();
    setModal(true);
    setSize(500, 500);
    setLocationRelativeTo(getParent());
    addWindowFocusListener(new WindowAdapter() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        getBnTextArea().requestFocus();
      }
    });
  }

  private void init() {
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(getScrollPane(), BorderLayout.CENTER);
    getContentPane().add(getPanel(), BorderLayout.SOUTH);
    getRootPane().setDefaultButton(getBtnOk());
    getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    getRootPane().getActionMap().put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=10,430
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider(); // @wb:location=10,430
      localModelProvider
          .setPresentationModelType(de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(
      de.adrodoc55.minecraft.mpl.gui.dialog.OneCommandDialogPM pModel) {
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

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getBnTextArea());
    }
    return scrollPane;
  }

  private BnTextArea getBnTextArea() {
    if (bnTextArea == null) {
      bnTextArea = new BnTextArea();
      bnTextArea.setSelectAllOnFocusGainedEnabled(true);
      bnTextArea.setLineWrap(true);
      bnTextArea.setPath(new Path("this.oneCommand"));
      bnTextArea.setModelProvider(getLocalModelProvider());
    }
    return bnTextArea;
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.add(getBtnOk());
    }
    return panel;
  }

  private JButton getBtnOk() {
    if (btnOk == null) {
      btnOk = new JButton("OK");
      btnOk.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          dispose();
        }
      });
    }
    return btnOk;
  }
}
