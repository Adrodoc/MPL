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

import static com.google.common.base.Preconditions.checkNotNull;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.GeneratedBy.MATERIALIZER;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nullable;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;

import de.adrodoc55.commons.ExtendedAbstractAssert;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.Modifiable;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.CommandPartBuffer;

public class ChainLinkAssert<S extends ChainLinkAssert<S, A>, A extends ChainLink>
    extends ExtendedAbstractAssert<S, A> {
  protected final CompilerOptions options;

  public ChainLinkAssert(@Nullable A actual, CompilerOptions options) {
    this(actual, ChainLinkAssert.class, options);
  }

  public ChainLinkAssert(@Nullable A actual, Class<?> selfType, CompilerOptions options) {
    super(actual, selfType);
    this.options = checkNotNull(options, "options == null!");
  }

  public CommandAssert asCommand() {
    isNotNull();
    isInstanceOf(Command.class);
    return new CommandAssert((Command) actual, options).as(description());
  }

  public AbstractBooleanAssert<?> internal() {
    isNotNull();
    return assertThat(actual.getGeneratedBy() == MATERIALIZER).as(description("internal"));
  }

  public S hasInternal(boolean internal) {
    internal().isEqualTo(internal);
    return myself;
  }

  /**
   * Verifies that the actual value is {@link ChainLink#isInternal() internal}.
   */
  public S isInternal() {
    internal().isTrue();
    return myself;
  }

  /**
   * Verifies that the actual value is not {@link ChainLink#isInternal() internal}.
   */
  public S isNotInternal() {
    internal().isFalse();
    return myself;
  }

  public AbstractListAssert<?, ?, Object, ObjectAssert<Object>> commandParts() {
    return asCommand().commandParts();
  }

  public CommandAssert hasMinecraftCommand(CommandPartBuffer commandParts) {
    return asCommand().hasMinecraftCommand(commandParts);
  }

  public CommandAssert hasCommandParts(Object... commandParts) {
    return asCommand().hasCommandParts(commandParts);
  }

  public CommandAssert hasDefaultModifiers() {
    return asCommand().hasDefaultModifiers();
  }

  public CommandAssert hasModifiers(Mode mode) {
    return asCommand().hasModifiers(mode);
  }

  public CommandAssert hasModifiers(Conditional conditional) {
    return asCommand().hasModifiers(conditional);
  }

  public CommandAssert hasModifiers(Modifiable modifiers) {
    return asCommand().hasModifiers(modifiers);
  }

  public CommandAssert matches(MplCommand expected) {
    return asCommand().matches(expected);
  }

  public CommandAssert matchesAsImpulse(MplCommand expected) {
    return asCommand().matchesAsImpulse(expected);
  }

  public CommandAssert matchesAsConditional(MplCommand expected) {
    return asCommand().matchesAsConditional(expected);
  }

  public CommandAssert isNop() {
    return asCommand().isNop();
  }

  public CommandAssert isStartCommand(int relative) {
    return asCommand().isStartCommand(relative);
  }

  /**
   * Verifies that the actual value is a {@link Command} that stops itself.
   */
  public CommandAssert isStopCommand() {
    return asCommand().isStopCommand();
  }

  /**
   * Verifies that the actual value is a {@link Command} that stops the {@link ChainLink} relative
   * to it.
   */
  public CommandAssert isStopCommand(int relative) {
    return asCommand().isStopCommand(relative);
  }

  public CommandAssert isInvertingCommandFor(Mode mode) {
    return asCommand().isInvertingCommandFor(mode);
  }

  public CommandAssert isTestforSuccessCommand(int relative, boolean success) {
    return asCommand().isTestforSuccessCommand(relative, success);
  }

  public CommandAssert isTestforSuccessCommand(int relative, Mode referencedMode, boolean success) {
    return asCommand().isTestforSuccessCommand(relative, referencedMode, success);
  }

  public CommandAssert isNormalizingCommand() {
    return asCommand().isNormalizingCommand();
  }

  public S isSkip() {
    isInstanceOf(MplSkip.class);
    return myself;
  }
}
