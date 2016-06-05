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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.searchandreplace;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

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
 * The SearchAndReplaceDialog is a {@link View} on a {@link SearchAndReplaceDialogPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class SearchAndReplaceDialog extends JDialog
    implements WindowView<SearchAndReplaceDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JPanel contentPanel;
  private JPanel pnlInput;
  private BnComboBox bncbxSearch;
  private JLabel lblSearch;
  private JLabel lblReplaceWith;
  private BnComboBox bncbxReplaceWith;
  private JPanel pnlOptions;
  private BnCheckBox bnchckbxCaseSensitive;
  private BnCheckBox bnchckbxWholeWord;
  private BnCheckBox bnchckbxRegularExpression;
  private BnCheckBox bnchckbxWrapSearch;
  private BnCheckBox bnchckbxExtended;
  private BnCheckBox bnchckbxIncremental;
  private JPanel pnlButtons;
  private BnButton bnbtnFind;
  private BnButton bnbtnReplace;
  private BnButton bnbtnReplacefind;
  private BnButton bnbtnReplaceAll;
  private JPanel pnlClose;
  private JButton btnClose;

  /**
   * Constructs a new <code>SearchAndReplaceDialog</code>.
   *
   * @param parent the {@code Window} from which the dialog is displayed or {@code null} if this
   *        dialog has no parent
   */
  public SearchAndReplaceDialog(Window parent) {
    super(parent, "Search and Replace");
    setContentPane(getContentPanel());
    setModal(true);
    setModalityType(ModalityType.MODELESS);
    setSize(400, 250);
    setLocationRelativeTo(getParent());
    getRootPane().setDefaultButton(getBnbtnFind());
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    actionMap.put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    addWindowFocusListener(new WindowFocusListener() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        SearchAndReplaceDialogPM pModel = getPresentationModel();
        if (pModel != null) {
          pModel.revalidateProperties();
        }
        setOpacity(1);
      }

      @Override
      public void windowLostFocus(WindowEvent e) {
        try {
          setOpacity(0.4f);
        } catch (IllegalComponentStateException ignore) {
          // ignore: happens when the LookAndFeel does not support window decorations.
        }
      }
    });
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) {
      JButton defaultButton = getRootPane().getDefaultButton();
      if (defaultButton.isEnabled()) {
        defaultButton.requestFocusInWindow();
      } else {
        getBncbxSearch().requestFocusInWindow();
      }
    }
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
      localModelProvider.setPresentationModelType(SearchAndReplaceDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public SearchAndReplaceDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(SearchAndReplaceDialogPM pModel) {
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

  private JPanel getContentPanel() {
    if (contentPanel == null) {
      contentPanel = new JPanel();
      contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      contentPanel.setLayout(new BorderLayout());
      contentPanel.add(getPnlInput(), BorderLayout.NORTH);
      contentPanel.add(getPnlOptions(), BorderLayout.CENTER);
      contentPanel.add(getPnlButtons(), BorderLayout.EAST);
      contentPanel.add(getPnlClose(), BorderLayout.SOUTH);
    }
    return contentPanel;
  }

  private JPanel getPnlInput() {
    if (pnlInput == null) {
      pnlInput = new JPanel();
      pnlInput.setBorder(new EmptyBorder(5, 5, 5, 5));
      GridBagLayout gbl_pnlInput = new GridBagLayout();
      gbl_pnlInput.columnWeights = new double[] {0.0, 1.0};
      gbl_pnlInput.rowWeights = new double[] {0.0, 0.0};
      pnlInput.setLayout(gbl_pnlInput);
      GridBagConstraints gbc_lblSearch = new GridBagConstraints();
      gbc_lblSearch.insets = new Insets(0, 0, 5, 5);
      gbc_lblSearch.anchor = GridBagConstraints.WEST;
      gbc_lblSearch.gridx = 0;
      gbc_lblSearch.gridy = 0;
      pnlInput.add(getLblSearch(), gbc_lblSearch);
      GridBagConstraints gbc_bncbxSearch = new GridBagConstraints();
      gbc_bncbxSearch.insets = new Insets(0, 0, 5, 0);
      gbc_bncbxSearch.fill = GridBagConstraints.BOTH;
      gbc_bncbxSearch.gridx = 1;
      gbc_bncbxSearch.gridy = 0;
      pnlInput.add(getBncbxSearch(), gbc_bncbxSearch);
      GridBagConstraints gbc_lblReplaceWith = new GridBagConstraints();
      gbc_lblReplaceWith.anchor = GridBagConstraints.WEST;
      gbc_lblReplaceWith.insets = new Insets(0, 0, 0, 5);
      gbc_lblReplaceWith.gridx = 0;
      gbc_lblReplaceWith.gridy = 1;
      pnlInput.add(getLblReplaceWith(), gbc_lblReplaceWith);
      GridBagConstraints gbc_bncbxReplaceWith = new GridBagConstraints();
      gbc_bncbxReplaceWith.fill = GridBagConstraints.BOTH;
      gbc_bncbxReplaceWith.gridx = 1;
      gbc_bncbxReplaceWith.gridy = 1;
      pnlInput.add(getBncbxReplaceWith(), gbc_bncbxReplaceWith);
    }
    return pnlInput;
  }

  private BnComboBox getBncbxSearch() {
    if (bncbxSearch == null) {
      bncbxSearch = new BnComboBox();
      bncbxSearch.setPath(new Path("this.search"));
      bncbxSearch.setModelProvider(getLocalModelProvider());
      bncbxSearch.setEditable(true);
    }
    return bncbxSearch;
  }

  private JLabel getLblSearch() {
    if (lblSearch == null) {
      lblSearch = new JLabel("Search:");
    }
    return lblSearch;
  }

  private JLabel getLblReplaceWith() {
    if (lblReplaceWith == null) {
      lblReplaceWith = new JLabel("Replace with:");
    }
    return lblReplaceWith;
  }

  private BnComboBox getBncbxReplaceWith() {
    if (bncbxReplaceWith == null) {
      bncbxReplaceWith = new BnComboBox();
      bncbxReplaceWith.setPath(new Path("this.replaceWith"));
      bncbxReplaceWith.setModelProvider(getLocalModelProvider());
      bncbxReplaceWith.setEditable(true);
    }
    return bncbxReplaceWith;
  }

  private JPanel getPnlOptions() {
    if (pnlOptions == null) {
      pnlOptions = new JPanel();
      pnlOptions.setBorder(
          new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_pnlOptions = new GridBagLayout();
      gbl_pnlOptions.columnWidths = new int[] {0, 0, 0};
      gbl_pnlOptions.rowHeights = new int[] {0, 0, 0, 0};
      gbl_pnlOptions.columnWeights = new double[] {0.0, 0.0, 1.0};
      gbl_pnlOptions.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0};
      pnlOptions.setLayout(gbl_pnlOptions);
      GridBagConstraints gbc_bnchckbxCaseSensitive = new GridBagConstraints();
      gbc_bnchckbxCaseSensitive.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxCaseSensitive.insets = new Insets(0, 0, 5, 5);
      gbc_bnchckbxCaseSensitive.gridx = 0;
      gbc_bnchckbxCaseSensitive.gridy = 0;
      pnlOptions.add(getBnchckbxCaseSensitive(), gbc_bnchckbxCaseSensitive);
      GridBagConstraints gbc_bnchckbxWrapSearch = new GridBagConstraints();
      gbc_bnchckbxWrapSearch.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxWrapSearch.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxWrapSearch.gridx = 1;
      gbc_bnchckbxWrapSearch.gridy = 0;
      pnlOptions.add(getBnchckbxWrapSearch(), gbc_bnchckbxWrapSearch);
      GridBagConstraints gbc_bnchckbxWholeWord = new GridBagConstraints();
      gbc_bnchckbxWholeWord.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxWholeWord.insets = new Insets(0, 0, 5, 5);
      gbc_bnchckbxWholeWord.gridx = 0;
      gbc_bnchckbxWholeWord.gridy = 1;
      pnlOptions.add(getBnchckbxWholeWord(), gbc_bnchckbxWholeWord);
      GridBagConstraints gbc_bnchckbxIncremental = new GridBagConstraints();
      gbc_bnchckbxIncremental.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxIncremental.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxIncremental.gridx = 1;
      gbc_bnchckbxIncremental.gridy = 1;
      pnlOptions.add(getBnchckbxIncremental(), gbc_bnchckbxIncremental);
      GridBagConstraints gbc_bnchckbxRegularExpression = new GridBagConstraints();
      gbc_bnchckbxRegularExpression.insets = new Insets(0, 0, 5, 5);
      gbc_bnchckbxRegularExpression.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxRegularExpression.gridx = 0;
      gbc_bnchckbxRegularExpression.gridy = 2;
      pnlOptions.add(getBnchckbxRegularExpression(), gbc_bnchckbxRegularExpression);
      GridBagConstraints gbc_bnchckbxExtended = new GridBagConstraints();
      gbc_bnchckbxExtended.insets = new Insets(0, 0, 5, 0);
      gbc_bnchckbxExtended.anchor = GridBagConstraints.WEST;
      gbc_bnchckbxExtended.gridx = 1;
      gbc_bnchckbxExtended.gridy = 2;
      pnlOptions.add(getBnchckbxExtended(), gbc_bnchckbxExtended);
    }
    return pnlOptions;
  }

  private BnCheckBox getBnchckbxCaseSensitive() {
    if (bnchckbxCaseSensitive == null) {
      bnchckbxCaseSensitive = new BnCheckBox();
      bnchckbxCaseSensitive.setPath(new Path("this.caseSensitive"));
      bnchckbxCaseSensitive.setModelProvider(getLocalModelProvider());
      bnchckbxCaseSensitive.setText("Case sensitive");
    }
    return bnchckbxCaseSensitive;
  }

  private BnCheckBox getBnchckbxWholeWord() {
    if (bnchckbxWholeWord == null) {
      bnchckbxWholeWord = new BnCheckBox();
      bnchckbxWholeWord.setPath(new Path("this.wholeWord"));
      bnchckbxWholeWord.setModelProvider(getLocalModelProvider());
      bnchckbxWholeWord.setText("Whole word");
    }
    return bnchckbxWholeWord;
  }

  private BnCheckBox getBnchckbxRegularExpression() {
    if (bnchckbxRegularExpression == null) {
      bnchckbxRegularExpression = new BnCheckBox();
      bnchckbxRegularExpression.setPath(new Path("this.regex"));
      bnchckbxRegularExpression.setModelProvider(getLocalModelProvider());
      bnchckbxRegularExpression.setText("Regular expression");
    }
    return bnchckbxRegularExpression;
  }

  private BnCheckBox getBnchckbxWrapSearch() {
    if (bnchckbxWrapSearch == null) {
      bnchckbxWrapSearch = new BnCheckBox();
      bnchckbxWrapSearch.setPath(new Path("this.wrapSearch"));
      bnchckbxWrapSearch.setModelProvider(getLocalModelProvider());
      bnchckbxWrapSearch.setText("Wrap search");
    }
    return bnchckbxWrapSearch;
  }

  private BnCheckBox getBnchckbxIncremental() {
    if (bnchckbxIncremental == null) {
      bnchckbxIncremental = new BnCheckBox();
      bnchckbxIncremental.setPath(new Path("this.incremental"));
      bnchckbxIncremental.setModelProvider(getLocalModelProvider());
      bnchckbxIncremental.setText("Incremental");
    }
    return bnchckbxIncremental;
  }

  private BnCheckBox getBnchckbxExtended() {
    if (bnchckbxExtended == null) {
      bnchckbxExtended = new BnCheckBox();
      bnchckbxExtended.setPath(new Path("this.extended"));
      bnchckbxExtended.setModelProvider(getLocalModelProvider());
      bnchckbxExtended.setText("Extended (\\n, \\t, ...)");
    }
    return bnchckbxExtended;
  }

  private JPanel getPnlButtons() {
    if (pnlButtons == null) {
      pnlButtons = new JPanel();
      pnlButtons.setBorder(new EmptyBorder(5, 5, 5, 5));
      GridBagLayout gbl_pnlButtons = new GridBagLayout();
      gbl_pnlButtons.columnWidths = new int[] {0};
      gbl_pnlButtons.rowHeights = new int[] {0, 0, 0, 0, 0};
      gbl_pnlButtons.columnWeights = new double[] {0.0};
      gbl_pnlButtons.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0};
      pnlButtons.setLayout(gbl_pnlButtons);
      GridBagConstraints gbc_bnbtnFind = new GridBagConstraints();
      gbc_bnbtnFind.fill = GridBagConstraints.HORIZONTAL;
      gbc_bnbtnFind.insets = new Insets(0, 0, 5, 0);
      gbc_bnbtnFind.gridx = 0;
      gbc_bnbtnFind.gridy = 0;
      pnlButtons.add(getBnbtnFind(), gbc_bnbtnFind);
      GridBagConstraints gbc_bnbtnReplace = new GridBagConstraints();
      gbc_bnbtnReplace.fill = GridBagConstraints.HORIZONTAL;
      gbc_bnbtnReplace.insets = new Insets(0, 0, 5, 0);
      gbc_bnbtnReplace.gridx = 0;
      gbc_bnbtnReplace.gridy = 1;
      pnlButtons.add(getBnbtnReplace(), gbc_bnbtnReplace);
      GridBagConstraints gbc_bnbtnReplacefind = new GridBagConstraints();
      gbc_bnbtnReplacefind.fill = GridBagConstraints.HORIZONTAL;
      gbc_bnbtnReplacefind.insets = new Insets(0, 0, 5, 0);
      gbc_bnbtnReplacefind.gridx = 0;
      gbc_bnbtnReplacefind.gridy = 2;
      pnlButtons.add(getBnbtnReplacefind(), gbc_bnbtnReplacefind);
      GridBagConstraints gbc_bnbtnReplaceAll = new GridBagConstraints();
      gbc_bnbtnReplaceAll.fill = GridBagConstraints.HORIZONTAL;
      gbc_bnbtnReplaceAll.gridx = 0;
      gbc_bnbtnReplaceAll.gridy = 3;
      pnlButtons.add(getBnbtnReplaceAll(), gbc_bnbtnReplaceAll);
    }
    return pnlButtons;
  }

  private BnButton getBnbtnFind() {
    if (bnbtnFind == null) {
      bnbtnFind = new BnButton();
      bnbtnFind.setPath(new Path("this.find"));
      bnbtnFind.setModelProvider(getLocalModelProvider());
      bnbtnFind.setText("Find");
    }
    return bnbtnFind;
  }

  private BnButton getBnbtnReplace() {
    if (bnbtnReplace == null) {
      bnbtnReplace = new BnButton();
      bnbtnReplace.setPath(new Path("this.replace"));
      bnbtnReplace.setModelProvider(getLocalModelProvider());
      bnbtnReplace.setText("Replace");
    }
    return bnbtnReplace;
  }

  private BnButton getBnbtnReplacefind() {
    if (bnbtnReplacefind == null) {
      bnbtnReplacefind = new BnButton();
      bnbtnReplacefind.setPath(new Path("this.replaceFind"));
      bnbtnReplacefind.setModelProvider(getLocalModelProvider());
      bnbtnReplacefind.setText("Replace/Find");
    }
    return bnbtnReplacefind;
  }

  private BnButton getBnbtnReplaceAll() {
    if (bnbtnReplaceAll == null) {
      bnbtnReplaceAll = new BnButton();
      bnbtnReplaceAll.setPath(new Path("this.replaceAll"));
      bnbtnReplaceAll.setModelProvider(getLocalModelProvider());
      bnbtnReplaceAll.setText("Replace All");
    }
    return bnbtnReplaceAll;
  }

  private JPanel getPnlClose() {
    if (pnlClose == null) {
      pnlClose = new JPanel();
      pnlClose.add(getBtnClose());
    }
    return pnlClose;
  }

  private JButton getBtnClose() {
    if (btnClose == null) {
      btnClose = new JButton("Close");
      btnClose.addActionListener(e -> setVisible(false));
    }
    return btnClose;
  }
}
