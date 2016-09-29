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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.compileroptions;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnComboBox;

import de.adrodoc55.minecraft.mpl.ide.gui.dialog.WindowView;

/**
 * The {@link CompilerOptionsDialog} is a {@link View} on a {@link CompilerOptionsDialogPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class CompilerOptionsDialog extends JDialog
    implements WindowView<CompilerOptionsDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JPanel contentPanel;
  private JPanel mainPanel;
  private JPanel buttonPanel;
  private BnCheckBox bnchckbxDebug;
  private BnCheckBox bnchckbxDeleteOnUninstall;
  private BnCheckBox bnchckbxTransmitter;
  private BnButton bnbtnOk;
  private BnComboBox bnComboBox;
  private JButton btnCancel;

  /**
   * Constructs a new {@link CompilerOptionsDialog}.
   *
   * @param parent the {@code Window} from which the dialog is displayed or {@code null} if this
   *        dialog has no parent
   */
  public CompilerOptionsDialog(Window parent) {
    super(parent, "Compiler Options");
    setContentPane(getContentPanel());
    setModal(true);
    setSize(175, 200);
    setLocationRelativeTo(getParent());
    getRootPane().setDefaultButton(getBnbtnOk());
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
      localModelProvider.setPresentationModelType(CompilerOptionsDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  @Override
  public CompilerOptionsDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  @Override
  public void setPresentationModel(CompilerOptionsDialogPM pModel) {
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  @Override
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  @Override
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  @Override
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  @Override
  public void setPath(Path path) {
    this.link.setPath(path);
  }

  private JPanel getContentPanel() {
    if (contentPanel == null) {
      contentPanel = new JPanel();
      contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      contentPanel.setLayout(new BorderLayout(0, 0));
      contentPanel.add(getMainPanel(), BorderLayout.CENTER);
      contentPanel.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    return contentPanel;
  }

  private JPanel getMainPanel() {
    if (mainPanel == null) {
      mainPanel = new JPanel();
      GridBagLayout gbl_mainPanel = new GridBagLayout();
      gbl_mainPanel.columnWeights = new double[] {1.0, 0.0, 1.0};
      mainPanel.setLayout(gbl_mainPanel);
      GridBagConstraints gbc_bnchckbxDebug = new GridBagConstraints();
      gbc_bnchckbxDebug.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxDebug.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxDebug.gridx = 1;
      gbc_bnchckbxDebug.gridy = 0;
      mainPanel.add(getBnchckbxDebug(), gbc_bnchckbxDebug);
      GridBagConstraints gbc_bnchckbxDeleteOnUninstall = new GridBagConstraints();
      gbc_bnchckbxDeleteOnUninstall.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxDeleteOnUninstall.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxDeleteOnUninstall.gridx = 1;
      gbc_bnchckbxDeleteOnUninstall.gridy = 1;
      mainPanel.add(getBnchckbxDeleteOnUninstall(), gbc_bnchckbxDeleteOnUninstall);
      GridBagConstraints gbc_bnchckbxTransmitter = new GridBagConstraints();
      gbc_bnchckbxTransmitter.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxTransmitter.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxTransmitter.gridx = 1;
      gbc_bnchckbxTransmitter.gridy = 2;
      mainPanel.add(getBnchckbxTransmitter(), gbc_bnchckbxTransmitter);
      GridBagConstraints gbc_bnComboBox = new GridBagConstraints();
      gbc_bnComboBox.gridwidth = 3;
      gbc_bnComboBox.fill = GridBagConstraints.HORIZONTAL;
      gbc_bnComboBox.gridx = 0;
      gbc_bnComboBox.gridy = 3;
      mainPanel.add(getBnComboBox(), gbc_bnComboBox);
    }
    return mainPanel;
  }

  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
      buttonPanel.add(getBnbtnOk());
      buttonPanel.add(getBtnCancel());
    }
    return buttonPanel;
  }

  private BnCheckBox getBnchckbxDebug() {
    if (bnchckbxDebug == null) {
      bnchckbxDebug = new BnCheckBox();
      bnchckbxDebug.setPath(new Path("this.debug"));
      bnchckbxDebug.setModelProvider(getLocalModelProvider());
      bnchckbxDebug.setText("Debug");
    }
    return bnchckbxDebug;
  }

  private BnCheckBox getBnchckbxDeleteOnUninstall() {
    if (bnchckbxDeleteOnUninstall == null) {
      bnchckbxDeleteOnUninstall = new BnCheckBox();
      bnchckbxDeleteOnUninstall.setPath(new Path("this.deleteOnUninstall"));
      bnchckbxDeleteOnUninstall.setModelProvider(getLocalModelProvider());
      bnchckbxDeleteOnUninstall.setText("Delete on Uninstall");
    }
    return bnchckbxDeleteOnUninstall;
  }

  private BnCheckBox getBnchckbxTransmitter() {
    if (bnchckbxTransmitter == null) {
      bnchckbxTransmitter = new BnCheckBox();
      bnchckbxTransmitter.setPath(new Path("this.transmitter"));
      bnchckbxTransmitter.setModelProvider(getLocalModelProvider());
      bnchckbxTransmitter.setText("Transmitter");
    }
    return bnchckbxTransmitter;
  }

  private BnComboBox getBnComboBox() {
    if (bnComboBox == null) {
      bnComboBox = new BnComboBox();
      bnComboBox.setPath(new Path("this.version"));
      bnComboBox.setModelProvider(getLocalModelProvider());
    }
    return bnComboBox;
  }

  private BnButton getBnbtnOk() {
    if (bnbtnOk == null) {
      bnbtnOk = new BnButton();
      bnbtnOk.setPath(new Path("this.ok"));
      bnbtnOk.setModelProvider(getLocalModelProvider());
      bnbtnOk.setText("OK");
      bnbtnOk.addActionListener(e -> dispose());
    }
    return bnbtnOk;
  }

  private JButton getBtnCancel() {
    if (btnCancel == null) {
      btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(e -> dispose());
    }
    return btnCancel;
  }
}
