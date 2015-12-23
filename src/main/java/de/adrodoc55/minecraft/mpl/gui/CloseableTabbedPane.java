package de.adrodoc55.minecraft.mpl.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class CloseableTabbedPane extends JTabbedPane {

    private static final long serialVersionUID = -311083949367758192L;

    @Override
    public void insertTab(String title, Icon icon, Component component,
            String tip, int index) {
        super.insertTab(title, icon, component, tip, index);
        setTabComponentAt(index, new TabCloseComponent(this, title, component));
    }
}
