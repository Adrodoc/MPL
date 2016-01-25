package de.adrodoc55.minecraft.mpl.gui.scribble;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;

import org.beanfabrics.BnModelObserver;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;

/**
 * The MplEditor is a {@link View} on a {@link MplEditorPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplEditor extends JComponent implements View<MplEditorPM>,
        ModelSubscriber {
    private static final long serialVersionUID = 1L;
    private final Link link = new Link(this);
    private ModelProvider localModelProvider;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private BnModelObserver bnModelObserver;

    /**
     * Constructs a new <code>MplEditor</code>.
     */
    public MplEditor() {
        super();
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
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
            localModelProvider.setPresentationModelType(MplEditorPM.class);
        }
        return localModelProvider;
    }

    /**
     * @wbp.nonvisual location=9,379
     */
    private BnModelObserver getBnModelObserver() {
        if (bnModelObserver == null) {
            bnModelObserver = new BnModelObserver();
            bnModelObserver.setPath(new Path("this.code"));// @wb:location=9,379
            bnModelObserver.setModelProvider(getLocalModelProvider());
        }
        return bnModelObserver;
    }

    /** {@inheritDoc} */
    public MplEditorPM getPresentationModel() {
        return getLocalModelProvider().getPresentationModel();
    }

    /** {@inheritDoc} */
    public void setPresentationModel(MplEditorPM pModel) {
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
            scrollPane.setViewportView(getTextPane());
        }
        return scrollPane;
    }

    private JTextPane getTextPane() {
        if (textPane == null) {
            textPane = new JTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            doc.addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    getPresentationModel().code.setText(textPane.getText());
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    getPresentationModel().code.setText(textPane.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
            getBnModelObserver().addPropertyChangeListener(evt -> {
                if (getPresentationModel() != null) {
                    String text = getPresentationModel().code.getText();
                    if (!textPane.getText().equals(text)) {
                        textPane.setText(text);
                    }
                }
            });
        }
        return textPane;
    }

}
