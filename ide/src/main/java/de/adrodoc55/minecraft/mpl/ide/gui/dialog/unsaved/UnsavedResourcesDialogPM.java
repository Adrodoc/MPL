/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.unsaved;

import java.util.Collection;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

import de.adrodoc55.minecraft.mpl.ide.gui.MplEditorPM;

/**
 * @author Adrodoc55
 */
public class UnsavedResourcesDialogPM extends AbstractPM {

  ListPM<UnsavedResourceRowPM> unsaved = new ListPM<UnsavedResourceRowPM>();
  OperationPM selectAll = new OperationPM();
  OperationPM deselectAll = new OperationPM();
  OperationPM ok = new OperationPM();
  OperationPM cancel = new OperationPM();
  private boolean canceled;

  public UnsavedResourcesDialogPM(Collection<MplEditorPM> unsavedEditors) {
    for (MplEditorPM mplEditorPM : unsavedEditors) {
      unsaved.add(new UnsavedResourceRowPM(mplEditorPM));
    }
    PMManager.setup(this);
  }

  @Operation
  public void selectAll() {
    for (UnsavedResourceRowPM unsavedResourceRowPM : unsaved) {
      unsavedResourceRowPM.save.setBoolean(true);
    }
  }

  @Operation
  public void deselectAll() {
    for (UnsavedResourceRowPM unsavedResourceRowPM : unsaved) {
      unsavedResourceRowPM.save.setBoolean(false);
    }
  }

  @Operation
  public void ok() {
    for (UnsavedResourceRowPM unsavedResourceRowPM : unsaved) {
      if (!unsavedResourceRowPM.save.getBoolean()) {
        continue;
      }
      unsavedResourceRowPM.editorPm.save();
    }
  }

  @Operation
  public void cancel() {
    canceled = true;
  }

  public boolean isCanceled() {
    return canceled;
  }

}
