package de.adrodoc55.minecraft.mpl.gui.dialog;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class UnsavedResourcesDialogBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<UnsavedResourcesDialog> getBeanClass() {
    return UnsavedResourcesDialog.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
