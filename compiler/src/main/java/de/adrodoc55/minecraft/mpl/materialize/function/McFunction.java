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
package de.adrodoc55.minecraft.mpl.materialize.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Adrodoc55
 */
@EqualsAndHashCode(exclude = "commands")
@RequiredArgsConstructor
@Getter
public class McFunction {
  public static final String MPL_NAMESPACE = "mpl";

  public static String toFullName(String processName, String rootFunctionName) {
    return MPL_NAMESPACE + ':' + processName + '/' + rootFunctionName;
  }

  private final @Nonnull String namespace;
  private final @Nonnull String name;
  private final List<String> commands = new ArrayList<>();

  public McFunction(String name) {
    this(MPL_NAMESPACE, name);
  }

  public String getFullName() {
    return namespace + ':' + name;
  }

  public String getFilePath() {
    return namespace + '/' + name + ".mcfunction";
  }

  public List<String> getCommands() {
    return Collections.unmodifiableList(commands);
  }

  public void addCommand(String command) {
    commands.add(command);
  }

  public void addAllCommands(Collection<? extends String> commands) {
    this.commands.addAll(commands);
  }

  @Override
  public String toString() {
    return "McFunction (name=" + getFullName() + ", commands=" + commands + ")";
  }
}
