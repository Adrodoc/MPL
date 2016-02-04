package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class BnDocumentFilterBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<BnDocumentFilter> getBeanClass() {
    return BnDocumentFilter.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
