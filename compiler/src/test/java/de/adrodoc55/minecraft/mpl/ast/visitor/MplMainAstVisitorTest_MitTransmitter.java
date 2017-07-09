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
package de.adrodoc55.minecraft.mpl.ast.visitor;

import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MplMainAstVisitorTest_MitTransmitter extends MplMainAstVisitorTest {
  @Override
  protected MplCompilerContext newContext() {
    CompilerOptions options = new CompilerOptions(TRANSMITTER, DEBUG);
    return new MplCompilerContext(MinecraftVersion.getDefault(), options);
  }

  @Override
  protected MplMainAstVisitor newUnderTest(MplCompilerContext context) {
    MplMainAstVisitor result = new MplMainAstVisitor(context);
    result.program = new MplProgram(new File(""), context);
    return result;
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____
  //   |  _ \  _ __  ___    ___  ___  ___  ___
  //   | |_) || '__|/ _ \  / __|/ _ \/ __|/ __|
  //   |  __/ | |  | (_) || (__|  __/\__ \\__ \
  //   |_|    |_|   \___/  \___|\___||___/|___/
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  @Override
  public void test_a_nameless_process_doesnt_have_startup_commands() {
    // given:
    MplCommand first = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand second = some($MplCommand()//
        .withPrevious(first)//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplProcess process = some($MplProcess()//
        .withName((String) null)//
        .withChainParts(listOf(first, second)));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    assertThat(it.next()).isNotInternal().isSkip();
    assertThat(it.next()).matches(first);
    assertThat(it.next()).matches(second);
    assertThat(it).isEmpty();
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //   __        __ _      _  _
  //   \ \      / /| |__  (_)| |  ___
  //    \ \ /\ / / | '_ \ | || | / _ \
  //     \ V  V /  | | | || || ||  __/
  //      \_/\_/   |_| |_||_||_| \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  @Override
  public void test_repeat_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isInternal().isStopCommand(-3).hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStartCommand(-4).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);
    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_repeat_while_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withNot(false)//
        .withTrailing(true)//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStopCommand(-4).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStartCommand(-5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStopCommand(-8).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);
    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_repeat_while_not_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withNot(true)//
        .withTrailing(true)//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStartCommand(+5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStopCommand(-5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStopCommand(-7).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStartCommand(-8).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);
    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_while_repeat_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withNot(false)//
        .withTrailing(false)//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isStartCommand(+3).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+10).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStopCommand(-4).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStartCommand(-5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStopCommand(-8).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);
    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_while_not_repeat_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withNot(true)//
        .withTrailing(false)//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isStartCommand(+12).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStartCommand(+5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStopCommand(-5).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStopCommand(-7).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isStartCommand(-8).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);
    assertThat(it).isEmpty();
  }

  // TODO: The empty command is only required due to the current chainlink placement which throws:
  // java.lang.IllegalArgumentException: RECEIVER at index 7 is followed by a TRANSMITTER
  // If that system is updated this will no longer be necessary
  @Test
  public void test_nested_repeat_requires_empty_command() {
    // given:
    MplWhile innerWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
    );
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withChainParts(listOf(innerWhile)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it.next())/* FIXME .isInternal() */.isEmpty().hasModifiers(IMPULSE);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(innerWhile);
    assertThat(it.next()).isInternal().isSkip();
    assertThat(it).isNotEmpty();
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____                     _
  //   | __ )  _ __  ___   __ _ | | __
  //   |  _ \ | '__|/ _ \ / _` || |/ /
  //   | |_) || |  |  __/| (_| ||   <
  //   |____/ |_|   \___| \__,_||_|\_\
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  @Override
  public void test_unconditional_Break() {
    // given:
    MplWhile mplWhile = some($MplWhile().withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // break
    // FIXME: ein command von break MUSS nicht internal sein
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(mplBreak); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-3).hasModifiers(CONDITIONAL); // ref enter

    // while trailer
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_conditional_Break() {
    // given:
    MplWhile mplWhile = some($MplWhile().withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // break
    assertThat(it.next()).isInternal().isStartCommand(+8).hasModifiers(mplBreak); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-3).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL); // ref no break
    assertThat(it.next()).isNotInternal().isSkip(); // no break
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    // while trailer
    assertThat(it.next()).isInternal().isStopCommand(-8).hasDefaultModifiers(); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-9).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_invert_Break() {
    // given:
    MplWhile mplWhile = some($MplWhile().withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // break
    assertThat(it.next()).isInternal().isStartCommand(+4).hasModifiers(mplBreak); // ref no break
    assertThat(it.next()).isInvertingCommandFor(mplBreak.getMode());
    assertThat(it.next()).isInternal().isStartCommand(+6).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-5).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isNotInternal().isSkip(); // no break
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    // while trailer
    assertThat(it.next()).isInternal().isStopCommand(-8).hasDefaultModifiers(); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-9).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_nested_Break_stops_all_inner_loops() {
    // given:
    MplWhile innerWhile = some($MplWhile()//
        .withCondition((String) null));

    MplCommand outerCommand = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile outerWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withChainParts(listOf(outerCommand, innerWhile)));

    MplCommand innerCommand = some($MplCommand().withConditional(UNCONDITIONAL));
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(outerWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(innerCommand));
    innerWhile.setChainParts(listOf(innerCommand, mplBreak));

    // when:
    List<ChainLink> result = outerWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    // outer while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(outerWhile);
    assertThat(it.next()).isInternal().isSkip(); // outer enter
    assertThat(it.next()).matchesAsImpulse(outerCommand);

    // inner while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(innerWhile);
    assertThat(it.next()).isInternal().isSkip(); // inner enter
    assertThat(it.next()).matchesAsImpulse(innerCommand);

    // break
    assertThat(it.next()).isInternal().isStartCommand(+13).hasModifiers(mplBreak); // ref o exit
    assertThat(it.next()).isInternal().isStopCommand(-3).hasModifiers(CONDITIONAL); // ref i enter
    assertThat(it.next()).isInternal().isStopCommand(-7).hasModifiers(CONDITIONAL); // ref o enter
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
    assertThat(it.next()).isNotInternal().isSkip(); // don't break
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    // inner while trailer
    assertThat(it.next()).isInternal().isStopCommand(-9).hasDefaultModifiers(); // ref i enter
    assertThat(it.next()).isInternal().isStartCommand(-10).hasModifiers(CONDITIONAL); // ref i enter
    assertThat(it.next()).isInternal().isSkip(); // inner exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    // outer while trailer
    assertThat(it.next()).isInternal().isStopCommand(-16).hasDefaultModifiers(); // ref o enter
    assertThat(it.next()).isInternal().isStartCommand(-17).hasModifiers(CONDITIONAL); // ref o enter
    assertThat(it.next()).isInternal().isSkip(); // outer exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasModifiers(IMPULSE);

    assertThat(it).isEmpty();
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //     ____               _    _
  //    / ___| ___   _ __  | |_ (_) _ __   _   _   ___
  //   | |    / _ \ | '_ \ | __|| || '_ \ | | | | / _ \
  //   | |___| (_) || | | || |_ | || | | || |_| ||  __/
  //    \____|\___/ |_| |_| \__||_||_| |_| \__,_| \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  @Override
  public void test_unconditional_Continue_without_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // continue
    // FIXME: ein command von continue MUSS nicht internal sein
    assertThat(it.next()).isInternal().isStopCommand(-2).hasModifiers(mplContinue); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-3).hasModifiers(CONDITIONAL); // ref enter

    // while trailer
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_unconditional_Continue_with_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile().withNot(false).withTrailing(true));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // continue
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasModifiers(mplContinue);
    assertThat(it.next()).isInternal().isStopCommand(-3).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-4).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-7).hasModifiers(CONDITIONAL); // ref enter

    // while trailer
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_conditional_Continue_without_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile().withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command); // conditional condition

    // continue
    assertThat(it.next()).isInternal().isStopCommand(-2) // ref enter
    /* FIXME: .hasModifiers(mplContinue) */;
    assertThat(it.next()).isInternal().isStartCommand(-3).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isTestforSuccessCommand(-3, IMPULSE, false); // ref conditional condition
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL); // ref nop
    assertThat(it.next()).isNotInternal().isSkip(); // nop
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // while trailer
    assertThat(it.next()).isInternal().isStopCommand(-8).hasDefaultModifiers(); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-9).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_conditional_Continue_with_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile().withNot(false).withTrailing(true));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command);

    // continue
    assertThat(it.next()).isNormalizingCommand(); // normalizer
    assertThat(it.next())/* FIXME: .isInternal() */.hasCommandParts(mplWhile.getCondition())
    /* .hasModifiers(mplContinue) */; // cond
    assertThat(it.next()).isInternal().isStopCommand(-4).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-5).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isTestforSuccessCommand(-4, true); // ref normalizer
    assertThat(it.next()).isTestforSuccessCommand(-4, false).hasModifiers(CONDITIONAL); // ref cond
    assertThat(it.next()).isInternal().isStartCommand(+12).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-9).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isTestforSuccessCommand(-8, false); // ref normalizer
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL); // ref nop
    assertThat(it.next()).isNotInternal().isSkip(); // nop
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // while trailer
    assertThat(it.next())/* FIXME .isInternal() */.hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStopCommand(-15).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-16).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInvertingCommandFor(CHAIN);// TODO testfor success -3 false
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-19).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_invert_Continue_without_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile().withCondition((String) null));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command); // invert condition

    // continue
    assertThat(it.next()).isInternal().isStartCommand(+4) // ref nop
    /* FIXME: .hasModifiers(mplContinue) */;
    assertThat(it.next()).isTestforSuccessCommand(-2, IMPULSE, false); // ref invert condition
    assertThat(it.next()).isInternal().isStopCommand(-4).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-5).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isNotInternal().isSkip(); // nop
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // while trailer
    assertThat(it.next()).isInternal().isStopCommand(-8).hasDefaultModifiers(); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-9).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_invert_Continue_with_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile().withNot(false).withTrailing(true));
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).isInternal().isSkip(); // enter
    assertThat(it.next()).matchesAsImpulse(command); // invert conditon

    // continue
    assertThat(it.next()).isInternal().isStartCommand(+9)
    /* FIXME .hasModifiers(mplContinue) */; // ref nop
    assertThat(it.next()).isTestforSuccessCommand(-2, IMPULSE, false); // ref invert conditon
    assertThat(it.next())/* FIXME: .isInternal() */.hasCommandParts(mplWhile.getCondition())
        .hasModifiers(CONDITIONAL); // cond
    assertThat(it.next()).isInternal().isStopCommand(-5).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-6).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isTestforSuccessCommand(-6, IMPULSE, false); // ref invert conditon
    assertThat(it.next()).isTestforSuccessCommand(-4, false).hasModifiers(CONDITIONAL); // ref cond
    assertThat(it.next()).isInternal().isStartCommand(+10).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-10).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isNotInternal().isSkip(); // nop
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // while trailer
    assertThat(it.next())/* FIXME .isInternal() */.hasCommandParts(mplWhile.getCondition())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStopCommand(-14).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isStartCommand(-15).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInvertingCommandFor(CHAIN);// TODO testfor success -3 false
    assertThat(it.next()).isInternal().isStartCommand(+2).hasModifiers(CONDITIONAL); // ref exit
    assertThat(it.next()).isInternal().isStopCommand(-18).hasModifiers(CONDITIONAL); // ref enter
    assertThat(it.next()).isInternal().isSkip(); // exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

  @Test
  @Override
  public void test_nested_Continue_stops_all_inner_loops() {
    // given:
    MplWhile innerWhile = some($MplWhile().withCondition((String) null));

    MplCommand outerCommand = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile outerWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withChainParts(listOf(outerCommand, innerWhile)));

    MplCommand innerCommand = some($MplCommand().withConditional(UNCONDITIONAL));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(outerWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(innerCommand));
    innerWhile.setChainParts(listOf(innerCommand, mplContinue));

    // when:
    List<ChainLink> result = outerWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();

    // outer while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(outerWhile);
    assertThat(it.next()).isInternal().isSkip(); // outer enter
    assertThat(it.next()).matchesAsImpulse(outerCommand);

    // inner while header
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(innerWhile);
    assertThat(it.next()).isInternal().isSkip(); // inner enter
    assertThat(it.next()).matchesAsImpulse(innerCommand);

    // continue
    assertThat(it.next()).isInternal().isStopCommand(-2)
    /* FIXME .hasModifiers(mplContinue) */; // ref i enter
    assertThat(it.next()).isInternal().isStopCommand(-6).hasModifiers(CONDITIONAL); // ref o enter
    assertThat(it.next()).isInternal().isStartCommand(-7).hasModifiers(CONDITIONAL); // ref o enter
    assertThat(it.next()).isTestforSuccessCommand(-4, IMPULSE, false); // ref conditional condition
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL); // ref nop
    assertThat(it.next()).isNotInternal().isSkip(); // nop
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // inner while trailer
    assertThat(it.next()).isInternal().isStopCommand(-9).hasDefaultModifiers(); // ref i enter
    assertThat(it.next()).isInternal().isStartCommand(-10).hasModifiers(CONDITIONAL); // ref i enter
    assertThat(it.next()).isInternal().isSkip(); // inner exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    // outer while trailer
    assertThat(it.next()).isInternal().isStopCommand(-16).hasDefaultModifiers(); // ref o enter
    assertThat(it.next()).isInternal().isStartCommand(-17).hasModifiers(CONDITIONAL); // ref o enter
    assertThat(it.next()).isInternal().isSkip(); // outer exit
    assertThat(it.next()).isInternal().isStopCommand(-1).hasMode(IMPULSE);

    assertThat(it).isEmpty();
  }

}
