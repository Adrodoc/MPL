package de.adrodoc55.minecraft.mpl.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabCloseComponent extends JPanel {

    private static final long serialVersionUID = 1L;

    private boolean unsavedChanges;
    private final JTabbedPane parent;
    private final Component component;
    private JLabel savedLabel;
    private JLabel titleLabel;
    private JButton closeButton;

    public TabCloseComponent(JTabbedPane parent, String title,
            Component component) {
        this.parent = parent;
        this.component = component;
        unsavedChanges = true;
        setUnsavedChanges(false);
        add(getSavedLabel());
        JLabel titleLabel = getTitleLabel();
        titleLabel.setText(title);
        add(titleLabel);
        add(getCloseButton());
    }

    public String getTitle() {
        return getTitleLabel().getText();
    }

    public void setTitle(String title) {
        getTitleLabel().setText(title);
    }

    public void setUnsavedChanges(boolean unsavedChanges) {
        if (this.unsavedChanges == unsavedChanges) {
            return;
        }
        this.unsavedChanges = unsavedChanges;
        if (unsavedChanges) {
            getSavedLabel().setIcon(
                    new ImageIcon(MplFrame.class
                            .getResource("/icons/unsaved.gif")));
        } else {
            getSavedLabel().setIcon(
                    new ImageIcon(MplFrame.class
                            .getResource("/icons/saved.gif")));
        }
    }

    private JLabel getSavedLabel() {
        if (savedLabel == null) {
            savedLabel = new JLabel();
        }
        return savedLabel;
    }

    private JLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new JLabel();
        }
        return titleLabel;
    }

    private JButton getCloseButton() {
        if (closeButton == null) {
            URL iconUrl = TabCloseComponent.class.getClassLoader().getResource(
                    "icons/close.jpg");
            ImageIcon icon = new ImageIcon(iconUrl);
            closeButton = new JButton(icon);
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setOpaque(false);
            closeButton.setPreferredSize(new Dimension(17, 17));
            closeButton.addActionListener(e -> {
                parent.remove(component);
            });
        }
        return closeButton;
    }
}
