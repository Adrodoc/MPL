package de.adrodoc55.minecraft.mpl.gui.scribble;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplFrameBeanInfo extends ModelSubscriberBeanInfo {
    @Override
    protected Class<MplFrame> getBeanClass() {
        return MplFrame.class;
    }

    @Override
    protected boolean isPathBound() {
        return false;
    }
}
