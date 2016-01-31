package de.adrodoc55.minecraft.mpl.gui.utils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoAction extends AbstractAction {

  private static final long serialVersionUID = -1330600449731063166L;
  private final UndoManager manager;

  public UndoAction(UndoManager manager) {
    this.manager = manager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      manager.undo();
    } catch (CannotUndoException ex) {
      // Don't undo
    }
  }

}
