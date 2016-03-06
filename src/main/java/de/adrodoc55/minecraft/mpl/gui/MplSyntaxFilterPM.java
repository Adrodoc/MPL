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
package de.adrodoc55.minecraft.mpl.gui;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;

import de.adrodoc55.minecraft.mpl.CompilerException;

public class MplSyntaxFilterPM extends AbstractPM {

  private List<CompilerExceptionWrapper> exceptions;

  public MplSyntaxFilterPM() {
    PMManager.setup(this);
  }

  List<CompilerExceptionWrapper> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<CompilerException> newExceptions) {
    List<CompilerExceptionWrapper> oldExceptions = exceptions;
    exceptions = new LinkedList<CompilerExceptionWrapper>();
    for (CompilerException ex : newExceptions) {
      exceptions.add(new CompilerExceptionWrapper(ex));
    }
    getPropertyChangeSupport().firePropertyChange("exceptions", oldExceptions, newExceptions);
  }

  static class CompilerExceptionWrapper {
    private Token token;

    private int startOffset;
    private int stopOffset;

    public CompilerExceptionWrapper(CompilerException ex) {
      this.token = ex.getSource().token;
      this.startOffset = 0;
      this.stopOffset = 0;
    }

    public int getStartIndex() {
      return token.getStartIndex() + startOffset;
    }

    public int getStopIndex() {
      return token.getStopIndex() + 1 + stopOffset;
    }

    public void addStartOffset(int offset) {
      this.startOffset += offset;
    }

    public void addStopOffset(int offset) {
      this.stopOffset += offset;
    }
  }

}
