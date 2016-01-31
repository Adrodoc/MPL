package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplEditorBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<MplEditor> getBeanClass() {
    return MplEditor.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
