package de.adrodoc55.minecraft.mpl.gui;

import java.util.List;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;

import de.adrodoc55.minecraft.mpl.CompilerException;

public class MplSyntaxFilterPM extends AbstractPM {

  private List<CompilerException> exceptions;

  public MplSyntaxFilterPM() {
    PMManager.setup(this);
  }

  public List<CompilerException> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<CompilerException> newExceptions) {
    List<CompilerException> oldExceptions = exceptions;
    this.exceptions = newExceptions;
    getPropertyChangeSupport().firePropertyChange("exceptions", oldExceptions, newExceptions);
  }

}
