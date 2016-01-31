package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class ExceptionDialogBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<ExceptionDialog> getBeanClass() {
    return ExceptionDialog.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
