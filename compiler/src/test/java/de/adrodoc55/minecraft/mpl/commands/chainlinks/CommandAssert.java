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

import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;

import de.adrodoc55.minecraft.mpl.MplUtils;
import de.adrodoc55.minecraft.mpl.ast.Conditional;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.Modifiable;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

public class CommandAssert extends ChainLinkAssert<CommandAssert, Command> {
  public CommandAssert(@Nullable Command actual, CompilerOptions options) {
    super(actual, CommandAssert.class, options);
  }

  @Override
  public CommandAssert asCommand() {
    return myself;
  }

  @Override
  public AbstractListAssert<?, List<? extends Object>, Object, ObjectAssert<Object>> commandParts() {
    isNotNull();
    return assertThat(actual.getCommandParts().getCommandParts()).as(description("commandParts"));
  }

  public AbstractComparableAssert<?, Mode> mode() {
    isNotNull();
    return assertThat(actual.mode).as(description("mode"));
  }

  public AbstractBooleanAssert<?> conditional() {
    isNotNull();
    return assertThat(actual.conditional).as(description("conditional"));
  }

  public AbstractBooleanAssert<?> needsRedstone() {
    isNotNull();
    return assertThat(actual.needsRedstone).as(description("needsRedstone"));
  }

  @Override
  public CommandAssert hasCommandParts(Object... commandParts) {
    commandParts().containsExactly(commandParts);
    return myself;
  }

  public CommandAssert hasMode(Mode mode) {
    mode().isEqualTo(mode);
    return myself;
  }

  public CommandAssert hasConditional(boolean conditional) {
    conditional().isEqualTo(conditional);
    return myself;
  }

  public CommandAssert isConditional() {
    conditional().isTrue();
    return myself;
  }

  public CommandAssert isNotConditional() {
    conditional().isFalse();
    return myself;
  }

  public CommandAssert hasNeedsRedstone(boolean needsRedstone) {
    needsRedstone().isEqualTo(needsRedstone);
    return myself;
  }

  public CommandAssert doesNeedRedstone() {
    needsRedstone().isTrue();
    return myself;
  }

  public CommandAssert doesNotNeedRedstone() {
    needsRedstone().isFalse();
    return myself;
  }

  @Override
  public CommandAssert hasDefaultModifiers() {
    hasModifiers(modifier());
    return myself;
  }

  @Override
  public CommandAssert hasModifiers(Mode mode) {
    hasModifiers(modifier(mode));
    return myself;
  }

  @Override
  public CommandAssert hasModifiers(Conditional conditional) {
    hasModifiers(modifier(conditional));
    return myself;
  }

  @Override
  public CommandAssert hasModifiers(Modifiable modifiers) {
    isNotNull();
    mode().isEqualTo(modifiers.getMode());
    conditional().isEqualTo(modifiers.isConditional());
    needsRedstone().isEqualTo(modifiers.getNeedsRedstone());
    return myself;
  }

  @Override
  public CommandAssert matches(MplCommand expected) {
    isNotInternal();
    hasCommandParts(expected.getCommand());
    hasModifiers(expected);
    return myself;
  }

  @Override
  public CommandAssert matchesAsImpulse(MplCommand expected) {
    isNotInternal();
    hasCommandParts(expected.getCommand());
    hasMode(IMPULSE);
    hasConditional(expected.isConditional());
    hasNeedsRedstone(expected.getNeedsRedstone());
    return myself;
  }

  @Override
  public CommandAssert matchesAsConditional(MplCommand expected) {
    isNotInternal();
    hasCommandParts(expected.getCommand());
    hasMode(expected.getMode());
    isConditional();
    hasNeedsRedstone(expected.getNeedsRedstone());
    return myself;
  }

  @Override
  public CommandAssert isNop() {
    commandParts().isEmpty();
    return myself;
  }

  @Override
  public CommandAssert isStartCommand(int relative) {
    hasCommandParts(//
        MplUtils.getStartCommandHeader(options), //
        new RelativeThisInsert(relative), //
        MplUtils.getStartCommandTrailer(options)//
    );
    return myself;
  }

  @Override
  public CommandAssert isStopCommand(int relative) {
    hasCommandParts(//
        MplUtils.getStopCommandHeader(options), //
        new RelativeThisInsert(relative), //
        MplUtils.getStopCommandTrailer(options)//
    );
    return myself;
  }

  @Override
  public CommandAssert isInvertingCommandFor(Mode mode) {
    isTestforSuccessCommand(mode, false);
    isNotConditional();
    return myself;
  }

  public CommandAssert isTestforSuccessCommand(Mode referencedMode, boolean success) {
    isTestforSuccessCommand(-1, referencedMode, success);
    return myself;
  }

  @Override
  public CommandAssert isTestforSuccessCommand(int relative, boolean success) {
    isTestforSuccessCommand(relative, CHAIN, success);
    return myself;
  }

  public CommandAssert isTestforSuccessCommand(int relative, Mode referencedMode, boolean success) {
    isInternal();
    hasCommandParts("testforblock ", new RelativeThisInsert(relative),
        " " + referencedMode.getStringBlockId() + " -1 {SuccessCount:" + (success ? 1 : 0) + "}");
    hasMode(CHAIN);
    doesNotNeedRedstone();
    return myself;
  }

  @Override
  public CommandAssert isNormalizingCommand() {
    isInternal();
    hasCommandParts("testforblock ~ ~ ~ chain_command_block");
    hasModifiers(modifier(CHAIN, CONDITIONAL));
    return myself;
  }
}
