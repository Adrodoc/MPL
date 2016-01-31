package de.adrodoc55.minecraft.mpl.gui.utils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

public class RedoAction extends AbstractAction {

  private static final long serialVersionUID = -4557132973085610799L;
  private final UndoManager manager;

  public RedoAction(UndoManager manager) {
    this.manager = manager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      manager.redo();
    } catch (CannotRedoException ex) {
      // Don't redo
    }
  }

}
