package de.adrodoc55.minecraft.mpl.gui.scribble;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.Path;
import org.beanfabrics.View;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.beanfabrics.BnModelObserver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.beanfabrics.swing.BnButton;

/**
 * The XxxComponent is a {@link View} on a {@link XxxPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class XxxComponent extends JPanel implements View<XxxPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  /**
   * @wbp.nonvisual location=9,509
   */
  private final BnModelObserver bnModelObserver = new BnModelObserver();
  final JTextPane textPane = new JTextPane();

  /**
   * Constructs a new <code>XxxComponent</code>.
   */
  public XxxComponent() {
    super();
    bnModelObserver.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("evt=" + evt);
        System.out.println(evt.getOldValue());
        System.out.println(evt.getNewValue());
        if (getPresentationModel() != null) {
          String text = getPresentationModel().sourceCode.getText();
          if ( !textPane.getText().equals(text)) {
            textPane.setText(text);
          }
        }
      }
    });
    bnModelObserver.setPath(new Path("this.sourceCode"));
    bnModelObserver.setModelProvider(getLocalModelProvider());
    setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    panel.add(scrollPane, BorderLayout.CENTER);

    textPane.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
        System.out.println("XxxComponent.removeUpdate() e=" + e);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
        System.out.println("XxxComponent.insertUpdate() e=" + e);
        String text = textPane.getText();
        getPresentationModel().sourceCode.setText(text);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
        System.out.println("XxxComponent.changedUpdate() e=" + e);

      }
    });
    scrollPane.setViewportView(textPane);

    JPanel panel_1 = new JPanel();
    panel.add(panel_1, BorderLayout.NORTH);

    BnButton bnbtnChange = new BnButton();
    bnbtnChange.setPath(new Path("this.change"));
    bnbtnChange.setModelProvider(getLocalModelProvider());
    bnbtnChange.setText("Change");
    panel_1.add(bnbtnChange);
    //
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
      localModelProvider.setPresentationModelType(XxxPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public XxxPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(XxxPM pModel) {
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

}
