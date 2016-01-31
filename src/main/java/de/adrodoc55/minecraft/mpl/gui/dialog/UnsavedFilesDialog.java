package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
 * {@link de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedFilesDialogPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class UnsavedFilesDialog extends JDialog
    implements View<de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedFilesDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JLabel lblDescription;
  private JPanel pnlButtons;
  private BnButton bnbtnOk;
  private BnButton bnbtnCancel;
  private BnTable bnTable;
  private JScrollPane scrollPane;
  private JPanel pnlCenter;

  public UnsavedFilesDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>UnsavedFilesDialog</code>.
   */
  public UnsavedFilesDialog(Window parent) {
    super(parent, "Unsaved Files");
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
          de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedFilesDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedFilesDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(
      de.adrodoc55.minecraft.mpl.gui.dialog.UnsavedFilesDialogPM pModel) {
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
