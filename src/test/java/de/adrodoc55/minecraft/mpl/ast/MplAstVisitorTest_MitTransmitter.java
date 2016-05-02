/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.ast;

import static de.adrodoc55.TestBase.$Enum;
import static de.adrodoc55.TestBase.listOf;
import static de.adrodoc55.TestBase.some;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplBreakpoint;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplCommand;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIf;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplIntercept;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplNotify;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplSkip;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplStart;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplStop;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplWaitfor;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.ast.chainparts.ModeOwner;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NormalizingCommand;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MplAstVisitorTest_MitTransmitter {

  MplAstVisitorImpl underTest;

  @Before
  public void before() {
    underTest = new MplAstVisitorImpl(new CompilerOptions(TRANSMITTER));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____   _                _
  //   / ___| | |_  __ _  _ __ | |_
  //   \___ \ | __|/ _` || '__|| __|
  //    ___) || |_| (_| || |   | |_
  //   |____/  \__|\__,_||_|    \__|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Start() {
    // given:
    MplStart mplStart = some($MplStart()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ redstone_block"));
  }

  @Test
  public void test_conditional_Start() {
    // given:
    MplStart mplStart = some($MplStart()//
        .withConditional(CONDITIONAL));

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ redstone_block",
            true));
  }

  @Test
  public void test_invert_Start() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplStart mplStart = some($MplStart()//
        .withConditional(INVERT)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
            return mode;
          }
        }));

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode),
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ redstone_block",
            mplStart.isConditional()));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____   _
  //   / ___| | |_  ___   _ __
  //   \___ \ | __|/ _ \ | '_ \
  //    ___) || |_| (_) || |_) |
  //   |____/  \__|\___/ | .__/
  //                     |_|
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Stop() {
    // given:
    MplStop mplStop = some($MplStop()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ stone"));
  }

  @Test
  public void test_conditional_Stop() {
    // given:
    MplStop mplStop = some($MplStop()//
        .withConditional(CONDITIONAL));

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ stone", true));
  }

  @Test
  public void test_invert_Stop() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplStop mplStop = some($MplStop()//
        .withConditional(INVERT)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
            return mode;
          }
        }));

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode),
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ setblock ~ ~ ~ stone",
            mplStop.isConditional()));
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
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
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
        new InternalCommand("/setblock ${this + 1} redstone_block", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
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
        new InternalCommand("/setblock ${this + 3} redstone_block", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplWaitfor.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    _   _         _    _   __
  //   | \ | |  ___  | |_ (_) / _| _   _
  //   |  \| | / _ \ | __|| || |_ | | | |
  //   | |\  || (_) || |_ | ||  _|| |_| |
  //   |_| \_| \___/  \__||_||_|   \__, |
  //                               |___/
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_unconditional_Notify() {
    // given:
    MplNotify mplNotify = some($MplNotify()//
        .withConditional(UNCONDITIONAL));

    // when:
    mplNotify.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY
            + "] ~ ~ ~ setblock ~ ~ ~ redstone_block"), //
        new InternalCommand("/kill @e[name=" + mplNotify.getProcess() + NOTIFY + "]"));
  }

  @Test
  public void test_conditional_Notify() {
    // given:
    MplNotify mplNotify = some($MplNotify()//
        .withConditional(CONDITIONAL));

    // when:
    mplNotify.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY
            + "] ~ ~ ~ setblock ~ ~ ~ redstone_block", true), //
        new InternalCommand("/kill @e[name=" + mplNotify.getProcess() + NOTIFY + "]", true));
  }

  @Test
  public void test_invert_Notify() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplNotify mplNotify = some($MplNotify()//
        .withConditional(INVERT)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
            return mode;
          }
        }));

    // when:
    mplNotify.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode), //
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY
            + "] ~ ~ ~ setblock ~ ~ ~ redstone_block", true), //
        new InternalCommand("/kill @e[name=" + mplNotify.getProcess() + NOTIFY + "]", true));
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
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_conditional_Intercept() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(CONDITIONAL)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
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
        new InternalCommand("/setblock ${this + 1} redstone_block", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE), //
        new InternalCommand("/kill @e[name=" + mplIntercept.getEvent() + ",r=2]"), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + INTERCEPTED
            + "] {CustomName:" + mplIntercept.getEvent() + "}"));
  }

  @Test
  public void test_invert_Intercept() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplIntercept mplIntercept = some($MplIntercept()//
        .withConditional(INVERT)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
            return mode;
          }
        }));

    // when:
    mplIntercept.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand("/setblock ${this + 4} redstone_block", true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/entitydata @e[name=" + mplIntercept.getEvent() + "] {CustomName:"
            + mplIntercept.getEvent() + INTERCEPTED + "}", true), //
        new InternalCommand("/summon ArmorStand ${this + 1} {CustomName:" + mplIntercept.getEvent()
            + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE), //
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
        new InternalCommand("/say " + mplBreakpoint.getMessage()), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ setblock ~ ~ ~ redstone_block"), //
        new InternalCommand(
            "/summon ArmorStand ${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}"), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
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
        new InternalCommand("/say " + mplBreakpoint.getMessage(), true), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ setblock ~ ~ ~ redstone_block",
            true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/setblock ${this + 1} redstone_block", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
  }

  @Test
  public void test_invert_Breakpoint() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(INVERT)//
        .withPrevious(new ModeOwner() {
          @Override
          public Mode getMode() {
            return mode;
          }
        }));

    // when:
    mplBreakpoint.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode), //
        new InternalCommand("/say " + mplBreakpoint.getMessage(), true), //
        new InternalCommand("/execute @e[name=breakpoint] ~ ~ ~ setblock ~ ~ ~ redstone_block",
            true), //
        new InternalCommand(
            "/summon ArmorStand ${this + 3} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}",
            true), //
        new InvertingCommand(CHAIN), //
        new InternalCommand("/setblock ${this + 1} redstone_block", true), //
        new MplSkip(), //
        new InternalCommand("/setblock ${this - 1} stone", IMPULSE));
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
  public void test_If_then_mit_skip_wirft_exception() {
    // given:
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(some($MplSkip()))));

    // when:
    Exception act = null;
    try {
      mplIf.accept(underTest);
    } catch (IllegalStateException ex) {
      act = ex;
    }

    // then:
    assertThat(act).isNotNull();
    assertThat(act.getMessage()).isEqualTo("If cannot contain skip");
  }

  @Test
  public void test_If_else_mit_skip_wirft_exception() {
    // given:
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(some($MplSkip()))));

    // when:
    Exception act = null;
    try {
      mplIf.accept(underTest);
    } catch (IllegalStateException ex) {
      act = ex;
    }

    // then:
    assertThat(act).isNotNull();
    assertThat(act.getMessage()).isEqualTo("If cannot contain skip");
  }

  @Test
  public void test_If_mit_nur_einem_then_wird_zu_einem_conditional() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_nur_einem_else_wird_zu_einem_invert() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_nur_einem_then_wird_zu_einem_invert() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InvertingCommand(CHAIN), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_nur_einem_else_wird_zu_einem_conditional() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_then_und_else_wird_zu_conditional_und_invert() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1))//
        .withElseParts(listOf(else1)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_meheren_then___erster_conditional_andere_SuccessCount_1() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_meheren_then___alle_SuccessCount_0() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_meheren_else___alle_SuccessCount_0() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_meheren_else___erster_conditional_andere_SuccessCount_1() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_meheren_then_und_else() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2, then3))//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        // then
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone()), //
        // else
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 8} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_meheren_then_und_else() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1, then2, then3))//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        // then
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone()), //
        // else
        new InternalCommand("/testforblock ${this - 7} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 9} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 11} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_conditional_im_then() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(CONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_conditional_im_then___kein_normalizer() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(CONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_invert_im_then() {
    // given:
    MplCommand then1 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand then2 = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(then1));

    MplCommand then3 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InvertingCommand(then1.getMode()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}",
            true), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_conditional_im_else() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(CONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_mit_invert_im_else() {
    // given:
    MplCommand else1 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand else2 = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(else1));

    MplCommand else3 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InvertingCommand(else1.getMode()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_conditional_im_then() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(CONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_invert_im_then() {
    // given:
    MplCommand then1 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand then2 = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(then1));

    MplCommand then3 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1, then2, then3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand("/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.needsRedstone()), //
        new InvertingCommand(then1.getMode()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_conditional_im_else() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(CONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_conditional_im_else___kein_normalizer() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(CONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1, else2)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone())//
    );
  }

  @Test
  public void test_If_not_mit_invert_im_else() {
    // given:
    MplCommand else1 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplCommand else2 = some($MplCommand()//
        .withConditional(INVERT)//
        .withPrevious(else1));

    MplCommand else3 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1, else2, else3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.needsRedstone()), //
        new InvertingCommand(else1.getMode()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}",
            true), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.needsRedstone())//
    );
  }

  /**
   * <pre>
   * if: /mplIf
   * then (
   *   /outerThen1
   *   if: /outerThen2
   *   then (
   *     /innerThen1
   *   ) else (
   *     /innerElse1
   *   )
   *   /outerThen3
   * )
   * </pre>
   */
  @Test
  public void test_If_mit_nested_then() {
    // given:
    MplCommand outer1 = some($MplCommand().withConditional(UNCONDITIONAL));

    MplCommand innerThen = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand innerElse = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf outer2 = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(innerThen))//
        .withElseParts(listOf(innerElse)));

    MplCommand outer3 = some($MplCommand().withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(outer1, outer2, outer3)));

    // when:
    mplIf.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(mplIf.getCondition()), //
        new NormalizingCommand(), //
        new InternalCommand(outer1.getCommand(), outer1.getMode(), true, outer1.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(outer2.getCondition(), true), //
        new InternalCommand(innerThen.getCommand(), innerThen.getMode(), true,
            innerThen.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(innerElse.getCommand(), innerElse.getMode(), true,
            innerElse.needsRedstone()), //
        new InternalCommand("/testforblock ${this - 8} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(outer3.getCommand(), outer3.getMode(), true, outer3.needsRedstone())//
    );
  }

}
