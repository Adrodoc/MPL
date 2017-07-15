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
package de.adrodoc55.minecraft.mpl.materialize.process;

import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand;

import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.commands.Dependable;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Adrodoc55
 */
@RequiredArgsConstructor
@Getter
@Setter
public class IfNestingLayer {
  private final boolean not;
  private final @Nonnull Dependable ref;
  private boolean inElse;

  /**
   * Returns whether or not a command in the current nesting layer depends on the failure of this
   * layer's condition.
   *
   * @return whether or not a command in the current nesting layer depends on the failure of the
   *         condition
   */
  public boolean dependsOnFailure() {
    return not ^ inElse;
  }

  /**
   * Opposite of {@link #dependsOnFailure()}.
   *
   * @return not {@link #dependsOnFailure()}
   */
  public boolean dependsOnSuccess() {
    return !dependsOnFailure();
  }

  public Command newConditionReference() {
    return newTestforSuccessCommand(ref, !dependsOnFailure());
  }
}
