package de.adrodoc55.minecraft.mpl.gui;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import de.adrodoc55.minecraft.mpl.CompilerException;
import de.adrodoc55.minecraft.mpl.gui.utils.TabToSpaceTextPM;

public class MplSyntaxFilterPM extends AbstractPM implements BnDocumentPM {

  private List<CompilerExceptionWrapper> exceptions;

  TabToSpaceTextPM code = new TabToSpaceTextPM();

  public MplSyntaxFilterPM() {
    PMManager.setup(this);
  }

  public TextPM getTextPM() {
    return code;
  };

  List<CompilerExceptionWrapper> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<CompilerException> newExceptions) {
    List<CompilerExceptionWrapper> oldExceptions = exceptions;
    exceptions = new LinkedList<CompilerExceptionWrapper>();
    for (CompilerException ex : newExceptions) {
      exceptions.add(new CompilerExceptionWrapper(ex));
    }
    getPropertyChangeSupport().firePropertyChange("exceptions", oldExceptions, newExceptions);
  }

  static class CompilerExceptionWrapper {
    private Token token;

    private int startOffset;
    private int stopOffset;

    public CompilerExceptionWrapper(CompilerException ex) {
      this.token = ex.getToken();
      this.startOffset = 0;
      this.stopOffset = 0;
    }

    public int getStartIndex() {
      return token.getStartIndex() + startOffset;
    }

    public int getStopIndex() {
      return token.getStopIndex() + 1 + stopOffset;
    }

    public void addStartOffset(int offset) {
      this.startOffset += offset;
    }

    public void addStopOffset(int offset) {
      this.stopOffset += offset;
    }
  }

}
