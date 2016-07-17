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

import static de.adrodoc55.TestBase.$boolean;
import static de.adrodoc55.TestBase.$oneOf;
import static de.adrodoc55.TestBase.listOf;
import static de.adrodoc55.TestBase.some;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Mode;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplBreakpoint;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplCommand;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIf;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIntercept;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplProcess;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplWaitfor;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplWhile;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NoOperationCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

public class MplAstVisitorTest_MitTransmitter extends MplAstVisitorTest {

  @Override
  protected MplAstVisitorImpl newUnderTest() {
    return new MplAstVisitorImpl(new CompilerOptions(TRANSMITTER, DEBUG));
  }

  @Override
  protected String getOnCommand(String ref) {
    return "setblock " + ref + " redstone_block";
  }

  @Override
  protected String getOffCommand(String ref) {
    return "setblock " + ref + " air";
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___                          _
  //   |_ _| _ __ __   __ ___  _ __ | |_
  //    | | | '_ \\ \ / // _ \| '__|| __|
  //    | | | | | |\ V /|  __/| |   | |_
  //   |___||_| |_| \_/  \___||_|    \__|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_invert_modifier_referenziert_den_richtigen_mode() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand second = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(first));

    MplProcess process = some($MplProcess()//
        .withRepeating(false)//
        .withChainParts(listOf(first, second)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE), //
        new Command(first.getCommand(), first), //
        new InvertingCommand(first.getMode()), // Important line!
        new Command(second.getCommand(), second));
  }

  @Test
  public void test_Der_erste_invert_in_einem_repeating_process_referenziert_einen_repeating_command_block() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand second = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(first));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(first, second)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new MplSkip(), //
        new Command(first.getCommand(), REPEAT, false, first.getNeedsRedstone()), //
        new InvertingCommand(REPEAT), // Important line!
        new Command(second.getCommand(), second));
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
  public void test_a_repeat_process_uses_a_repeat_command_block() {
    // given:
    MplCommand first = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand second = some($MplCommand()//
        .withPrevious(first)//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplProcess mplProcess = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(first, second)));

    // when:
    mplProcess.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new MplSkip(), //
        new Command(first.getCommand(), REPEAT), //
        new Command(second.getCommand(), second)//
    );
  }

  @Test
  public void test_a_nameless_process_doesnt_have_startup_commands() {
    // given:
    MplCommand first = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand second = some($MplCommand()//
        .withPrevious(first)//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplProcess mplProcess = some($MplProcess()//
        .withName((String) null)//
        .withChainParts(listOf(first, second)));

    // when:
    mplProcess.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new MplSkip(), //
        new Command(first.getCommand(), first), //
        new Command(second.getCommand(), second)//
    );
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //   __        __      _  _     __
  //   \ \      / /__ _ (_)| |_  / _|  ___   _ __
  //    \ \ /\ / // _` || || __|| |_  / _ \ | '__|
  //     \ V  V /| (_| || || |_ |  _|| (_) || |
  //      \_/\_/  \__,_||_| \__||_|   \___/ |_|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
  }

  @Test
  public void test_conditional_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(CONDITIONAL));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/summon ArmorStand ${this + 3} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
  }

  @Test
  public void test_invert_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(INVERT));

    // when:
    mplWaitfor.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 3}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___         _                                _
  //   |_ _| _ __  | |_  ___  _ __  ___  ___  _ __  | |_
  //    | | | '_ \ | __|/ _ \| '__|/ __|/ _ \| '_ \ | __|
  //    | | | | | || |_|  __/| |  | (__|  __/| |_) || |_
  //   |___||_| |_| \__|\___||_|   \___|\___|| .__/  \__|
  //                                         |_|
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Intercept() {
    // given:
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}"), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_conditional_Intercept() {
    // given:
    Mode mode = some($Mode());
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(CONDITIONAL)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return mode;
          }
        }));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}", true), //
        new InternalCommand("/summon ArmorStand ${this + 3} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_invert_Intercept() {
    // given:
    Mode mode = some($Mode());
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return mode;
          }
        }));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 4}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}", true), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
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
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~")), //
        new InternalCommand(
            "/summon ArmorStand ${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
  }

  @Test
  public void test_conditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(CONDITIONAL));

    // when:
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
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
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode), //
        new InternalCommand("/say " + mplBreakpoint.getMessage(), mplBreakpoint), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ " + getOnCommand("~ ~ ~"), true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new MplSkip(), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ___   __             _____  _                              _____  _
  //   |_ _| / _|           |_   _|| |__    ___  _ __             | ____|| | ___   ___
  //    | | | |_              | |  | '_ \  / _ \| '_ \            |  _|  | |/ __| / _ \
  //    | | |  _|  _  _  _    | |  | | | ||  __/| | | |  _  _  _  | |___ | |\__ \|  __/
  //   |___||_|   (_)(_)(_)   |_|  |_| |_| \___||_| |_| (_)(_)(_) |_____||_||___/ \___|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_commands_im_ersten_if_ohne_normalizer_in_einem_repeating_process_referenzieren_einen_repeating_command_block() {
    // given:
    MplCommand first = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(first)));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(mplIf)));

    // when:
    process.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new MplSkip(), //
        new InternalCommand(mplIf.getCondition(), REPEAT), //
        // then
        new InternalCommand(
            "/testforblock ${this - 1} repeating_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(first.getCommand(), first.getMode(), true, first.getNeedsRedstone())//
    );
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
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withChainParts(listOf(repeat1, repeat2)));

    // when:
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new MplSkip(true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new InternalCommand(getOffCommand("${this - 3}")), //
        new InternalCommand(getOnCommand("${this - 4}"), true), //
        new MplSkip(true), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE)//
    );
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
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new MplSkip(true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOffCommand("${this - 4}"), true), //
        new InternalCommand(getOnCommand("${this - 5}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 2}"), true), //
        new InternalCommand(getOffCommand("${this - 8}"), true), //
        new MplSkip(true), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE)//
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
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new MplSkip(true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 5}"), true), //
        new InternalCommand(getOffCommand("${this - 5}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOffCommand("${this - 7}"), true), //
        new InternalCommand(getOnCommand("${this - 8}"), true), //
        new MplSkip(true), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE)//
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
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 3}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 10}"), true), //
        new MplSkip(true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new InternalCommand(mplWhile.getCondition()), //
        new InternalCommand(getOffCommand("${this - 4}"), true), //
        new InternalCommand(getOnCommand("${this - 5}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 2}"), true), //
        new InternalCommand(getOffCommand("${this - 8}"), true), //
        new MplSkip(true), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE)//
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
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 12}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true), //
        new MplSkip(true), //
        new Command(repeat1.getCommand(), IMPULSE, repeat1.isConditional(),
            repeat1.getNeedsRedstone()), //
        new Command(repeat2.getCommand(), repeat2.getMode(), repeat2.isConditional(),
            repeat2.getNeedsRedstone()), //
        new Command(mplWhile.getCondition()), //
        new InternalCommand(getOnCommand("${this + 5}"), true), //
        new InternalCommand(getOffCommand("${this - 5}"), true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(getOffCommand("${this - 7}"), true), //
        new InternalCommand(getOnCommand("${this - 8}"), true), //
        new MplSkip(true), //
        new InternalCommand(getOffCommand("${this - 1}"), IMPULSE)//
    );
  }

  @Test
  public void test_nested_repeat_requires_nop() {
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
    mplWhile.accept(underTest);

    // then:
    assertThat(underTest.commands).startsWith(//
        new InternalCommand(getOnCommand("${this + 1}")), //
        new MplSkip(true), //
        new NoOperationCommand(IMPULSE), //
        new InternalCommand(getOnCommand("${this + 1}")), //
        new MplSkip(true)//
    );
  }

}
