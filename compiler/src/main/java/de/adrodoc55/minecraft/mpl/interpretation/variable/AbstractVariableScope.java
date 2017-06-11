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
package de.adrodoc55.minecraft.mpl.interpretation.variable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable;

/**
 * @author Adrodoc55
 */
public abstract class AbstractVariableScope implements VariableScope {
  protected final List<LocalVariableScope> children = new ArrayList<>();
  protected final Map<String, MplVariable<?>> variables = new HashMap<>();

  @Override
  public @Nullable VariableScope getParent() {
    return null;
  }

  @Override
  public List<LocalVariableScope> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public void addChildren(LocalVariableScope child) {
    children.add(checkNotNull(child, "child == null!"));
  }

  public Map<String, MplVariable<?>> getVariables() {
    return Collections.unmodifiableMap(variables);
  }

  public boolean mayDeclareVariable(String identifier) {
    return !variables.containsKey(identifier);
  }

  @Override
  public void declareVariable(MplVariable<?> variable) throws DuplicateVariableException {
    String identifier = variable.getIdentifier();
    if (mayDeclareVariable(identifier)) {
      variables.put(identifier, variable);
    } else {
      throw new DuplicateVariableException(
          "The Variable " + identifier + " is already defined in this Namespace");
    }
  }

  @Override
  public @Nullable MplVariable<?> findVariable(String identifier) {
    return variables.get(identifier);
  }
}
