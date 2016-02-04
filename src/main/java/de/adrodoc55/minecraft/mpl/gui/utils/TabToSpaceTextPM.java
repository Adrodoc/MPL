package de.adrodoc55.minecraft.mpl.gui.utils;

import java.util.Arrays;

import org.beanfabrics.model.TextPM;

public class TabToSpaceTextPM extends TextPM {

  private int tabWidth = 4;

  public TabToSpaceTextPM() {
    super();
  }

  public TabToSpaceTextPM(String initialText) {
    super(initialText);
  }

  public TabToSpaceTextPM(int tabWidth) {
    super();
    this.tabWidth = tabWidth;
  }

  public TabToSpaceTextPM(String initialText, int tabWith) {
    super(initialText);
    this.tabWidth = tabWith;
  }

  @Override
  public void setText(String aText) {
    StringBuilder sb = new StringBuilder(aText.length());
    int afterLastNlIndex = 0;
    int afterLastTabIndex = 0;
    char[] textArray = aText.toCharArray();
    for (int i = 0; i < textArray.length; i++) {
      if (textArray[i] == '\r' || textArray[i] == '\n') {
        afterLastNlIndex = i + 1;
      } else if (textArray[i] == '\t') {
        sb.append(Arrays.copyOfRange(textArray, afterLastTabIndex, i));
        int spaceCount = tabWidth - ((i - afterLastNlIndex) % tabWidth);
        for (int s = spaceCount; s > 0; s--) {
          sb.append(' ');
        }
        afterLastTabIndex = i + 1;
      }
    }
    sb.append(Arrays.copyOfRange(textArray, afterLastTabIndex, textArray.length));
    super.setText(sb.toString());
  }

}
