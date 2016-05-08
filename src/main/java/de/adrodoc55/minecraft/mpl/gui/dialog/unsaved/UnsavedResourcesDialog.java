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
package de.adrodoc55.minecraft.mpl.gui.dialog.unsaved;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

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
 * The UnsavedFilesDialog is a {@link View} on a {@link UnsavedResourcesDialogPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class UnsavedResourcesDialog extends JDialog
    implements View<UnsavedResourcesDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JLabel lblDescription;
  private JPanel pnlButtons;
  private BnButton bnbtnOk;
  private BnButton bnbtnCancel;
  private BnTable bnTable;
  private JScrollPane scrollPane;
  private JPanel pnlCenter;
  private JPanel pnlSelectButtons;
  private BnButton bnbtnSelectAll;
  private BnButton bnbtnDeselectAll;

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
    setSize(250, 300);
    setLocationRelativeTo(getParent());
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        getBnbtnCancel().doClick();
        super.windowClosing(e);
      }
    });
  }

  private void init() {
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setVgap(5);
    borderLayout.setHgap(5);
    getContentPane().setLayout(borderLayout);
    getContentPane().add(getPnlCenter(), BorderLayout.CENTER);
    getContentPane().add(getPnlButtons(), BorderLayout.SOUTH);
    getRootPane().setDefaultButton(getBnbtnOk());
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    actionMap.put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        getBnbtnCancel().doClick();
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
      localModelProvider.setPresentationModelType(UnsavedResourcesDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public UnsavedResourcesDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(UnsavedResourcesDialogPM pModel) {
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
      GridBagLayout gbl_pnlButtons = new GridBagLayout();
      gbl_pnlButtons.columnWeights = new double[] {1.0, 0.0};
      gbl_pnlButtons.rowWeights = new double[] {0.0};
      pnlButtons.setLayout(gbl_pnlButtons);
      GridBagConstraints gbc_bnbtnOk = new GridBagConstraints();
      gbc_bnbtnOk.anchor = GridBagConstraints.NORTHEAST;
      gbc_bnbtnOk.insets = new Insets(0, 0, 0, 5);
      gbc_bnbtnOk.gridx = 0;
      gbc_bnbtnOk.gridy = 0;
      pnlButtons.add(getBnbtnOk(), gbc_bnbtnOk);
      GridBagConstraints gbc_bnbtnCancel = new GridBagConstraints();
      gbc_bnbtnCancel.insets = new Insets(0, 0, 0, 5);
      gbc_bnbtnCancel.anchor = GridBagConstraints.NORTHWEST;
      gbc_bnbtnCancel.gridx = 1;
      gbc_bnbtnCancel.gridy = 0;
      pnlButtons.add(getBnbtnCancel(), gbc_bnbtnCancel);
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
      bnTable = new BnTable() {
        @Override
        protected void connect() {
          super.connect();
          Enumeration<TableColumn> columns = bnTable.getColumnModel().getColumns();
          while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            column.setResizable(false);
          }
        }
      };
      bnTable.getTableHeader().setReorderingAllowed(false);
      bnTable.setSortable(false);
      bnTable.setShowGrid(false);
      bnTable.setCellSelectionEnabled(false);
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
      pnlCenter.add(getPnlSelectButtons(), BorderLayout.SOUTH);
    }
    return pnlCenter;
  }

  private JPanel getPnlSelectButtons() {
    if (pnlSelectButtons == null) {
      pnlSelectButtons = new JPanel();
      GridBagLayout gbl_pnlSelectButtons = new GridBagLayout();
      gbl_pnlSelectButtons.columnWeights = new double[] {1.0, 0.0};
      gbl_pnlSelectButtons.rowWeights = new double[] {0.0};
      pnlSelectButtons.setLayout(gbl_pnlSelectButtons);
      GridBagConstraints gbc_bnbtnSelectAll = new GridBagConstraints();
      gbc_bnbtnSelectAll.insets = new Insets(0, 0, 0, 5);
      gbc_bnbtnSelectAll.anchor = GridBagConstraints.EAST;
      gbc_bnbtnSelectAll.gridx = 0;
      gbc_bnbtnSelectAll.gridy = 0;
      pnlSelectButtons.add(getBnbtnSelectAll(), gbc_bnbtnSelectAll);
      GridBagConstraints gbc_bnbtnDeselectAll = new GridBagConstraints();
      gbc_bnbtnDeselectAll.anchor = GridBagConstraints.WEST;
      gbc_bnbtnDeselectAll.gridx = 1;
      gbc_bnbtnDeselectAll.gridy = 0;
      pnlSelectButtons.add(getBnbtnDeselectAll(), gbc_bnbtnDeselectAll);
    }
    return pnlSelectButtons;
  }

  private BnButton getBnbtnSelectAll() {
    if (bnbtnSelectAll == null) {
      bnbtnSelectAll = new BnButton();
      bnbtnSelectAll.setPath(new Path("this.selectAll"));
      bnbtnSelectAll.setModelProvider(getLocalModelProvider());
      bnbtnSelectAll.setText("Select All");
    }
    return bnbtnSelectAll;
  }

  private BnButton getBnbtnDeselectAll() {
    if (bnbtnDeselectAll == null) {
      bnbtnDeselectAll = new BnButton();
      bnbtnDeselectAll.setPath(new Path("this.deselectAll"));
      bnbtnDeselectAll.setModelProvider(getLocalModelProvider());
      bnbtnDeselectAll.setText("Deselect All");
    }
    return bnbtnDeselectAll;
  }
}
