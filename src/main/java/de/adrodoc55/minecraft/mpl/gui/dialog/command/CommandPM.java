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
package de.adrodoc55.minecraft.mpl.gui.dialog.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;

/**
 * @author Adrodoc55
 */
public class CommandPM extends AbstractPM {
  public static interface Context {
    void close(CommandPM pm);
  }

  private final Context context;

  TextPM title = new TextPM();
  TextPM oneCommand = new TextPM();
  OperationPM copyToClipboard = new OperationPM();
  OperationPM copyAndClose = new OperationPM();
  OperationPM close = new OperationPM();

  public CommandPM(int number, String command, Context context) {
    this.context = checkNotNull(context, "context == null!");
    this.title.setEditable(false);
    this.oneCommand.setEditable(false);
    this.title.setText("Command " + number);
    this.oneCommand.setText(command);
    PMManager.setup(this);
  }

  @Operation
  public void copyToClipboard() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection contents = new StringSelection(oneCommand.getText());
    clipboard.setContents(contents, null);
  }

  @Operation
  public void copyAndClose() {
    copyToClipboard();
    close();
  }

  @Operation
  public void close() {
    context.close(this);
  }
}
