package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
