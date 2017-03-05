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
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MplAstVisitorTest_OhneTransmitter extends MplAstVisitorTest {
  @Override
  protected MplCompilerContext newContext() {
    CompilerOptions options = new CompilerOptions(DEBUG);
    return new MplCompilerContext(MinecraftVersion.getDefault(), options);
  }

  @Override
  protected MplMainAstVisitor newUnderTest(MplCompilerContext context) {
    MplMainAstVisitor result = new MplMainAstVisitor(context);
    result.program = new MplProgram(new File(""), context);
    return result;
  }

  @Override
  protected String getOnCommand(String ref) {
    return "blockdata " + ref + " {auto:1b}";
  }

  @Override
  protected String getOffCommand(String ref) {
    return "blockdata " + ref + " {auto:0b}";
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
    assertThat(result.getCommands()).containsExactly(//
        new Command(first.getCommand(), first), //
        new Command(second.getCommand(), second)//
    );
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____                     _                   _         _
  //   | __ )  _ __  ___   __ _ | | __ _ __    ___  (_) _ __  | |_
  //   |  _ \ | '__|/ _ \ / _` || |/ /| '_ \  / _ \ | || '_ \ | __|
  //   | |_) || |  |  __/| (_| ||   < | |_) || (_) || || | | || |_
  //   |____/ |_|   \___| \__,_||_|\_\| .__/  \___/ |_||_| |_| \__|
  //                                  |_|
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    assertThat(result).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new Command("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~")), //
        new InternalCommand("/summon " + markerEntity()
            + " ${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_conditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    assertThat(result).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new Command("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon " + markerEntity()
                + " ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
  }

  @Test
  public void test_invert_Breakpoint() {
    // given:
    Mode mode = some($Mode());
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() {
            return mode;
          }
        }));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    assertThat(result).containsExactly(//
        newInvertingCommand(mode), //
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new Command("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon " + markerEntity()
                + " ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE));
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
  public void test_repeat_mit_zwei_repeat() {
    // given:
    MplCommand repeat1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand repeat2 = some($MplCommand().withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
    assertThat(it.next()).matchesAsImpulse(repeat1);
    assertThat(it.next()).matches(repeat2);
    assertThat(it.next()).isInternal().isStopCommand(-2).hasDefaultModifiers();
    assertThat(it.next()).isInternal().isStartCommand(-3).hasModifiers(CONDITIONAL);
    assertThatNext(it).isInternal().isJumpDestination();
  }

  @Test
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
    assertThat(result).containsExactly(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOffCommand("${this - 3}"), true), //
        new InternalCommand(getOnCommand("${this - 4}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 2}"), true), //
        new InternalCommand(getOffCommand("${this - 7}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE)//
    );
  }

  @Test
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
    assertThat(result).containsExactly(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 5}"), true), //
        new InternalCommand(getOffCommand("${this - 4}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOffCommand("${this - 6}"), true), //
        new InternalCommand(getOnCommand("${this - 7}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE)//
    );
  }

  @Test
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
    assertThat(result).containsExactly(//
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 3}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 9}"), true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOffCommand("${this - 3}"), true), //
        new InternalCommand(getOnCommand("${this - 4}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 2}"), true), //
        new InternalCommand(getOffCommand("${this - 7}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE)//
    );
  }

  @Test
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
    assertThat(result).containsExactly(//
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 11}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 5}"), true), //
        new InternalCommand(getOffCommand("${this - 4}"), true), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOffCommand("${this - 6}"), true), //
        new InternalCommand(getOnCommand("${this - 7}"), true), //
        new InternalCommand(getOffCommand("~ ~ ~"), IMPULSE)//
    );
  }

  @Test
  public void test_nested_repeat_does_not_require_nop() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withChainParts(listOf(some($MplWhile()//
            .withCondition((String) null)//
            .withNot($boolean())//
            .withTrailing($boolean())//
    ))));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new InternalCommand(getOnCommand("${this + 1}"), IMPULSE)//
    );
  }

}
