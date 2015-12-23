package de.adrodoc55.minecraft.mpl.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TabCloseComponent extends JPanel {

    private static final long serialVersionUID = 1L;
    private final CloseableTabbedPane parent;
    private final String title;
    private final Component component;
    private JLabel titleLabel;
    private JButton closeButton;

    public TabCloseComponent(CloseableTabbedPane parent, String title,
            Component component) {
        this.parent = parent;
        this.title = title;
        this.component = component;
        add(getTitleLabel());
        add(getCloseButton());
    }

    private JLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new JLabel(this.title);
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
