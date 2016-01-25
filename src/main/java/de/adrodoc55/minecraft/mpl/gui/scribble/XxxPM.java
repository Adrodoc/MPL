package de.adrodoc55.minecraft.mpl.gui.scribble;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;
import org.beanfabrics.support.Operation;

public class XxxPM extends AbstractPM {
  TextPM sourceCode = new TextPM();
  OperationPM change = new OperationPM();

  public XxxPM() {
    PMManager.setup(this);
  }


  @OnChange(path="sourceCode")
  public void xxx() {
    System.out.println("geändert: "+sourceCode.getText());
  }

  @Operation
  public void change() {
    sourceCode.setText("123");
  }

}
