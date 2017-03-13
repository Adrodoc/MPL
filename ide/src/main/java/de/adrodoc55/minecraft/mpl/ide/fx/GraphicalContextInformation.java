package de.adrodoc55.minecraft.mpl.ide.fx;

import org.eclipse.fx.text.ui.contentassist.IContextInformation;

import javafx.scene.Node;

public class GraphicalContextInformation implements IContextInformation {
  private final int contextInformationPosition;
  private final CharSequence informationDisplayString;
  private final CharSequence contextDisplayString;

  public GraphicalContextInformation(int contextInformationPosition, CharSequence informationDisplayString,
      CharSequence contextDisplayString) {
    this.contextInformationPosition = contextInformationPosition;
    this.informationDisplayString = informationDisplayString;
    this.contextDisplayString = contextDisplayString;
  }

  @Override
  public int getContextInformationPosition() {
    return contextInformationPosition;
  }

  @Override
  public CharSequence getInformationDisplayString() {
    return informationDisplayString;
  }

  @Override
  public CharSequence getContextDisplayString() {
    return contextDisplayString;
  }

  @Override
  public Node getGraphic() {
    return null; // TODO: Find a nice Icon for the different ContextInformations
  }
}
