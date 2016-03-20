/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.gui.dialog;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Preconditions;

import de.adrodoc55.minecraft.mpl.autocompletion.AutoCompletionAction;
import de.adrodoc55.minecraft.mpl.gui.dialog.AutoCompletionDialogPM.Context;

/**
 * @author Adrodoc55
 */
public class AutoCompletionDialogControler {

  private final Context context;
  private Collection<AutoCompletionAction> options;
  private AutoCompletionDialogPM pm;
  private AutoCompletionDialog view;

  public AutoCompletionDialogControler(Context context) {
    this(Collections.emptyList(), context);
  }

  public AutoCompletionDialogControler(Collection<AutoCompletionAction> options, Context context) {
    this.options = Preconditions.checkNotNull(options, "options == null!");
    this.context = Preconditions.checkNotNull(context, "context == null!");
  }

  public void setOptions(Collection<AutoCompletionAction> options) {
    this.options = Preconditions.checkNotNull(options, "options == null!");
    getPresentationModel().setOptions(options);
    recalculateViewSize();
  }

  public AutoCompletionDialogPM getPresentationModel() {
    if (pm == null) {
      pm = new AutoCompletionDialogPM(options, context);
    }
    return pm;
  }

  public boolean hasView() {
    return view != null;
  }

  public AutoCompletionDialog getView() {
    if (view == null) {
      Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      view = new AutoCompletionDialog(activeWindow);
      view.setPresentationModel(getPresentationModel());
      recalculateViewSize();
      view.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          view = null;
        }
      });
    }
    return view;
  }

  private void recalculateViewSize() {
    AutoCompletionDialog view = getView();
    view.getBnList().setVisibleRowCount(Math.max(1, Math.min(options.size(), 10)));
    Dimension preferredSize = view.getPreferredSize();
    preferredSize.width += 5;
    view.setSize(preferredSize);
  }
}
