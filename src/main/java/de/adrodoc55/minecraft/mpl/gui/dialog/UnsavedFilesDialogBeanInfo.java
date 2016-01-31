package de.adrodoc55.minecraft.mpl.gui.dialog;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class UnsavedFilesDialogBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<UnsavedFilesDialog> getBeanClass() {
    return UnsavedFilesDialog.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
