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
package de.adrodoc55.minecraft.mpl.ast.variable;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import de.adrodoc55.minecraft.mpl.compilation.MplSource;

/**
 * @author Adrodoc55
 */
public enum MplType {
  INTEGER {
    @Override
    public MplIntegerVariable newVariable(MplSource source, String identifier) {
      MplIntegerVariable result = new MplIntegerVariable(source, identifier);
      return result;
    }
  },
  SELECTOR {
    @Override
    public MplVariable<?> newVariable(MplSource source, String identifier) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  STRING {
    @Override
    public MplStringVariable newVariable(MplSource source, String identifier) {
      MplStringVariable result = new MplStringVariable(source, identifier);
      return result;
    }
  },
  VALUE {
    @Override
    public MplVariable<?> newVariable(MplSource source, String identifier) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean isAssignableFrom(MplType type) {
      return type == INTEGER || type == VALUE;
    }
  };
  public String toString() {
    return UPPER_UNDERSCORE.to(UPPER_CAMEL, super.toString());
  }

  public abstract MplVariable<?> newVariable(MplSource source, String identifier);

  public boolean isAssignableFrom(MplType type) {
    return this == type;
  }
}
