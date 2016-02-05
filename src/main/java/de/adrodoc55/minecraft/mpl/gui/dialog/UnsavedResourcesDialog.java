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
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.table.BnColumnBuilder;
import org.beanfabrics.swing.table.BnTable;

/**
 * The UnsavedFilesDialog is a {@link View} on a
 * {@link de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class UnsavedResourcesDialog extends JDialog
    implements View<de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JLabel lblDescription;
  private JPanel pnlButtons;
  private BnButton bnbtnOk;
  private BnButton bnbtnCancel;
  private BnTable bnTable;
  private JScrollPane scrollPane;
  private JPanel pnlCenter;

  public UnsavedResourcesDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>UnsavedFilesDialog</code>.
   */
  public UnsavedResourcesDialog(Window parent) {
    super(parent, "Unsaved Resources");
    init();
    setModal(true);
    setSize(200, 300);
    // pack();
    setLocationRelativeTo(getParent());
  }

  private void init() {
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setVgap(5);
    borderLayout.setHgap(5);
    getContentPane().setLayout(borderLayout);
    getContentPane().add(getPnlCenter(), BorderLayout.CENTER);
    getContentPane().add(getPnlButtons(), BorderLayout.SOUTH);
    getRootPane().setDefaultButton(getBnbtnOk());
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
      localModelProvider.setPresentationModelType(
          de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(
      de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedResourcesDialogPM pModel) {
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

  private JLabel getLblDescription() {
    if (lblDescription == null) {
      lblDescription = new JLabel("Select resources to save:");
      lblDescription.setHorizontalAlignment(SwingConstants.CENTER);
    }
    return lblDescription;
  }

  private JPanel getPnlButtons() {
    if (pnlButtons == null) {
      pnlButtons = new JPanel();
      FlowLayout flowLayout = (FlowLayout) pnlButtons.getLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      pnlButtons.add(getBnbtnOk());
      pnlButtons.add(getBnbtnCancel());
    }
    return pnlButtons;
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

  private BnButton getBnbtnCancel() {
    if (bnbtnCancel == null) {
      bnbtnCancel = new BnButton();
      bnbtnCancel.setPath(new Path("this.cancel"));
      bnbtnCancel.setModelProvider(getLocalModelProvider());
      bnbtnCancel.setText("Cancel");
      bnbtnCancel.addActionListener(e -> dispose());
    }
    return bnbtnCancel;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getBnTable());
      scrollPane.setColumnHeader(null);
    }
    return scrollPane;
  }

  private BnTable getBnTable() {
    if (bnTable == null) {
      bnTable = new BnTable();
      bnTable.setSortable(false);
      bnTable.setShowVerticalLines(false);
      bnTable.setShowHorizontalLines(false);
      bnTable.setShowGrid(false);
      bnTable.setRowSelectionAllowed(false);
      bnTable.setPath(new Path("this.unsaved"));
      bnTable.setModelProvider(getLocalModelProvider());
      bnTable.setColumns(new BnColumnBuilder()
          // formatter:off
          .addColumn().withPath("this.save").withName("").withWidth(20).withWidthFixed(true)
          .addColumn().withPath("this.editorPm.title").withName("Resource").withWidth(10)
          // formatter:on
          .build());
    }
    return bnTable;
  }

  private JPanel getPnlCenter() {
    if (pnlCenter == null) {
      pnlCenter = new JPanel();
      pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
      pnlCenter.setLayout(new BorderLayout(5, 5));
      pnlCenter.add(getLblDescription(), BorderLayout.NORTH);
      pnlCenter.add(getScrollPane());
    }
    return pnlCenter;
  }
}
