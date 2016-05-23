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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.autocompletion;

import java.util.Collections;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;

import de.adrodoc55.minecraft.mpl.ide.autocompletion.AutoCompletionAction;

/**
 * @author Adrodoc55
 */
public class AutoCompletionDialogPM extends AbstractPM {

  public static interface Context {
    void choose(AutoCompletionAction action);
  }

  private final Context context;

  final ListPM<AutoCompletionPM> options = new ListPM<>();

  public AutoCompletionDialogPM(Context context) {
    this(Collections.emptyList(), context);
  }

  public AutoCompletionDialogPM(Iterable<AutoCompletionAction> options, Context context) {
    this.context = context;
    setOptions(options);
    PMManager.setup(this);
  }

  public void setOptions(Iterable<AutoCompletionAction> options) {
    this.options.clear();
    for (AutoCompletionAction action : options) {
      this.options.add(new AutoCompletionPM(action));
    }
    if (options.iterator().hasNext()) {
      this.options.getSelection().add(this.options.getAt(0));
    }
  }

  void chooseSelection() {
    AutoCompletionPM first = options.getSelection().getFirst();
    if (first == null)
      return;
    context.choose(first.getAction());
  }

}
