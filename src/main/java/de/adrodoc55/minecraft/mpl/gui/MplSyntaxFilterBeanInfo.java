package de.adrodoc55.minecraft.mpl.gui;

import org.beanfabrics.swing.ModelSubscriberBeanInfo;

/**
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplSyntaxFilterBeanInfo extends ModelSubscriberBeanInfo {
  @Override
  protected Class<MplSyntaxFilter> getBeanClass() {
    return MplSyntaxFilter.class;
  }

  @Override
  protected boolean isPathBound() {
    return false;
  }
}
