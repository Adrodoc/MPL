package de.adrodoc55.minecraft.mpl.gui.scribble;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnLabel;

/**
 * The TabCloseComponent is a {@link View} on a {@link MplEditorPM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class TabCloseComponent extends JComponent implements View<MplEditorPM>,
        ModelSubscriber {
    private final Link link = new Link(this);
    private ModelProvider localModelProvider;
    private JLabel savedLabel;
    private JButton button;
    private BnLabel titleLabel;

    /**
     * Constructs a new <code>TabCloseComponent</code>.
     */
    public TabCloseComponent() {
        super();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
        gridBagLayout.rowWeights = new double[] { 0.0 };
        setLayout(gridBagLayout);
        GridBagConstraints gbc_savedLabel = new GridBagConstraints();
        gbc_savedLabel.insets = new Insets(0, 0, 0, 5);
        gbc_savedLabel.gridx = 0;
        gbc_savedLabel.gridy = 0;
        add(getSavedLabel(), gbc_savedLabel);
        GridBagConstraints gbc_titleLabel = new GridBagConstraints();
        gbc_titleLabel.insets = new Insets(0, 0, 0, 5);
        gbc_titleLabel.gridx = 1;
        gbc_titleLabel.gridy = 0;
        add(getTitleLabel(), gbc_titleLabel);
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.gridx = 2;
        gbc_button.gridy = 0;
        add(getButton(), gbc_button);
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
            localModelProvider.setPresentationModelType(MplEditorPM.class);
        }
        return localModelProvider;
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

    private JLabel getSavedLabel() {
        if (savedLabel == null) {
            savedLabel = new JLabel();
            savedLabel.setIcon(new ImageIcon(TabCloseComponent.class
                    .getResource("/icons/saved.gif")));
        }
        return savedLabel;
    }

    private JButton getButton() {
        if (button == null) {
            button = new JButton();
            button.setIcon(new ImageIcon(TabCloseComponent.class
                    .getResource("/icons/close.jpg")));
        }
        return button;
    }

    private BnLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new BnLabel();
            titleLabel.setPath(new Path("title"));
            titleLabel.setModelProvider(getLocalModelProvider());
        }
        return titleLabel;
    }
}
