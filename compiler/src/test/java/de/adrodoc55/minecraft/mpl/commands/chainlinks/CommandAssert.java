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
package de.adrodoc55.minecraft.mpl.commands.chainlinks;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractComparableAssert;

import de.adrodoc55.commons.ExtendedAbstractAssert;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.Modifiable;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

public class CommandAssert<S extends CommandAssert<S, A>, A extends Command>
    extends ExtendedAbstractAssert<S, A> {
  public CommandAssert(A actual) {
    this(actual, CommandAssert.class);
  }

  public CommandAssert(A actual, Class<?> selfType) {
    super(actual, selfType);
  }

  @SuppressWarnings("deprecation")
  public AbstractCharSequenceAssert<?, String> command() {
    isNotNull();
    return assertThat(actual.getCommand()).as(description("command"));
  }

  public AbstractComparableAssert<?, Mode> mode() {
    return assertThat(actual.mode).as(description("mode"));
  }

  public AbstractBooleanAssert<?> conditional() {
    return assertThat(actual.conditional).as(description("conditional"));
  }

  public AbstractBooleanAssert<?> needsRedstone() {
    return assertThat(actual.needsRedstone).as(description("needsRedstone"));
  }

  public S hasCommand(String command) {
    command().isEqualTo(command);
    return myself;
  }

  public S hasMode(Mode mode) {
    mode().isEqualTo(mode);
    return myself;
  }

  public S isConditional() {
    conditional().isTrue();
    return myself;
  }

  public S isNotConditional() {
    conditional().isFalse();
    return myself;
  }

  public S doesNeedRedstone() {
    needsRedstone().isTrue();
    return myself;
  }

  public S doesNotNeedRedstone() {
    needsRedstone().isFalse();
    return myself;
  }

  public S hasModifiers(Modifiable modifiers) {
    isNotNull();
    mode().isEqualTo(modifiers.getMode());
    conditional().isEqualTo(modifiers.isConditional());
    needsRedstone().isEqualTo(modifiers.getNeedsRedstone());
    return myself;
  }

  public S isInvertingCommandFor(Mode mode) {
    isTestforSuccessCommand(mode, false);
    return myself;
  }

  public S isTestforSuccessCommand(Mode referencedMode, boolean success) {
    command().isEqualTo("testforblock " + new RelativeThisInsert(-1) + " "
        + referencedMode.getStringBlockId() + " -1 {SuccessCount:" + (success ? 1 : 0) + "}");
    return myself;
  }
}
