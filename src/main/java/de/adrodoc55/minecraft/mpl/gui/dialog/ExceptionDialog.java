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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.beanfabrics.BnModelObserver;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnLabel;
import org.beanfabrics.swing.BnTextArea;

/**
 * The ExceptionDialog is a {@link View} on a {@link ExceptionDialogPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class ExceptionDialog extends JDialog implements View<ExceptionDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JLabel lblIcon;
  private BnLabel lblDescription;
  private JScrollPane scrollPane;
  private BnTextArea textArea;
  private JPanel pnlCenter;
  private JPanel pnlBottom;
  private JButton btnOk;
  private BnModelObserver bnModelObserver;

  /**
   * Constructs a new <code>ExceptionDialog</code>.
   */
  public ExceptionDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>ExceptionDialog</code>.
   */
  public ExceptionDialog(Window parent) {
    super(parent);
    init();
    setModal(true);
    setSize(800, 400);
    setLocationRelativeTo(getParent());
  }

  private void init() {
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(getPnlCenter(), BorderLayout.CENTER);
    getContentPane().add(getPanel_1(), BorderLayout.SOUTH);
    getBnModelObserver().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        ExceptionDialogPM pModel = getPresentationModel();
        if (pModel != null) {
          setTitle(pModel.title.getText());
        }
      }
    });
    getRootPane().setDefaultButton(getBtnOk());
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    actionMap.put("close", new AbstractAction() {
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
      localModelProvider.setPresentationModelType(ExceptionDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public ExceptionDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(ExceptionDialogPM pModel) {
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

  private JLabel getLblIcon() {
    if (lblIcon == null) {
      lblIcon = new JLabel();
      lblIcon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
    }
    return lblIcon;
  }

  private BnLabel getLblDescription() {
    if (lblDescription == null) {
      lblDescription = new BnLabel();
      lblDescription.setPath(new Path("this.description"));
      lblDescription.setModelProvider(getLocalModelProvider());
    }
    return lblDescription;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getTextArea());
    }
    return scrollPane;
  }

  private BnTextArea getTextArea() {
    if (textArea == null) {
      textArea = new BnTextArea();
      textArea.setLineWrap(true);
      textArea.setPath(new Path("this.details"));
      textArea.setModelProvider(getLocalModelProvider());
    }
    return textArea;
  }

  private JPanel getPnlCenter() {
    if (pnlCenter == null) {
      pnlCenter = new JPanel();
      GridBagLayout gbl_pnlCenter = new GridBagLayout();
      gbl_pnlCenter.columnWidths = new int[] {0};
      gbl_pnlCenter.rowHeights = new int[] {0};
      gbl_pnlCenter.columnWeights = new double[] {0.0, 1.0};
      gbl_pnlCenter.rowWeights = new double[] {0.0, 1.0};
      pnlCenter.setLayout(gbl_pnlCenter);
      GridBagConstraints gbc_lblIcon = new GridBagConstraints();
      gbc_lblIcon.insets = new Insets(10, 10, 10, 5);
      gbc_lblIcon.gridx = 0;
      gbc_lblIcon.gridy = 0;
      pnlCenter.add(getLblIcon(), gbc_lblIcon);
      GridBagConstraints gbc_lblDescription = new GridBagConstraints();
      gbc_lblDescription.insets = new Insets(5, 5, 5, 5);
      gbc_lblDescription.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblDescription.gridx = 1;
      gbc_lblDescription.gridy = 0;
      pnlCenter.add(getLblDescription(), gbc_lblDescription);
      GridBagConstraints gbc_scrollPane = new GridBagConstraints();
      gbc_scrollPane.gridwidth = 2;
      gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
      gbc_scrollPane.fill = GridBagConstraints.BOTH;
      gbc_scrollPane.gridx = 0;
      gbc_scrollPane.gridy = 1;
      pnlCenter.add(getScrollPane(), gbc_scrollPane);
    }
    return pnlCenter;
  }

  private JPanel getPanel_1() {
    if (pnlBottom == null) {
      pnlBottom = new JPanel();
      pnlBottom.add(getBtnOk());
    }
    return pnlBottom;
  }

  private JButton getBtnOk() {
    if (btnOk == null) {
      btnOk = new JButton("OK");
      btnOk.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ExceptionDialog.this.dispose();
        }
      });
    }
    return btnOk;
  }

  /**
   * @wbp.nonvisual location=139,429
   */
  private BnModelObserver getBnModelObserver() {
    if (bnModelObserver == null) {
      bnModelObserver = new BnModelObserver();
      bnModelObserver.setPath(new Path("this.title"));
      bnModelObserver.setModelProvider(getLocalModelProvider());
    }
    return bnModelObserver;
  }

  public static ExceptionDialog create(String title, String message, String details) {
    Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    ExceptionDialog dialog = new ExceptionDialog(activeWindow);
    ExceptionDialogPM dialogPm = new ExceptionDialogPM();
    dialog.setPresentationModel(dialogPm);
    dialogPm.title.setText(title);
    dialogPm.description.setText(message);
    dialogPm.details.setText(details);
    return dialog;
  }
}
