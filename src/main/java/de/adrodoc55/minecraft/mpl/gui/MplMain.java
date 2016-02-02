package de.adrodoc55.minecraft.mpl.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MplMain {

  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SwingUtilities.invokeLater(() -> {
      MplFrame frame = new MplFrame();
      MplFramePM pModel = new MplFramePM();
      frame.setPresentationModel(pModel);
      frame.setVisible(true);
    });
  }

}
