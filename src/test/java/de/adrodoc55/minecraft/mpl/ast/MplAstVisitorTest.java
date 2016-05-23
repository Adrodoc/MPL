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
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplNotify;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplProcess;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplProgram;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplSkip;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplStart;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplStop;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.commands.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NormalizingCommand;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class MplAstVisitorTest {

  protected MplAstVisitorImpl underTest;

  @Before
  public void before() {
    underTest = newUnderTest();
  }

  protected abstract MplAstVisitorImpl newUnderTest();

  protected abstract String getOnCommand(String ref);

  protected abstract String getOffCommand(String ref);

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
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ " + getOnCommand("~ ~ ~"), mode,
            false, needsRedstone));
  }

  @Test
  public void test_conditional_Start() {
    // given:
    MplStart mplStart = some($MplStart()//
        .withConditional(CONDITIONAL));
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ " + getOnCommand("~ ~ ~"), mode,
            true, needsRedstone));
  }

  @Test
  public void test_invert_Start() {
    // given:
    Mode modeForInverting = some($Enum(Mode.class));
    MplStart mplStart = some($MplStart()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return modeForInverting;
          }
        }));
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    mplStart.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(modeForInverting), //
        new InternalCommand(
            "/execute @e[name=" + mplStart.getProcess() + "] ~ ~ ~ " + getOnCommand("~ ~ ~"), mode,
            true, needsRedstone));
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
    Mode mode = mplStop.getMode();
    boolean needsRedstone = mplStop.getNeedsRedstone();

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ " + getOffCommand("~ ~ ~"), mode,
            false, needsRedstone));
  }

  @Test
  public void test_conditional_Stop() {
    // given:
    MplStop mplStop = some($MplStop()//
        .withConditional(CONDITIONAL));
    Mode mode = mplStop.getMode();
    boolean needsRedstone = mplStop.getNeedsRedstone();

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ " + getOffCommand("~ ~ ~"), mode,
            true, needsRedstone));
  }

  @Test
  public void test_invert_Stop() {
    // given:
    Mode modeForInvering = some($Enum(Mode.class));
    MplStop mplStop = some($MplStop()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() throws UnsupportedOperationException {
            return modeForInvering;
          }
        }));
    Mode mode = mplStop.getMode();
    boolean needsRedstone = mplStop.getNeedsRedstone();

    // when:
    mplStop.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(modeForInvering),
        new InternalCommand(
            "/execute @e[name=" + mplStop.getProcess() + "] ~ ~ ~ " + getOffCommand("~ ~ ~"), mode,
            true, needsRedstone));
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
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY + "] ~ ~ ~ "
            + getOnCommand("~ ~ ~")), //
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
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY + "] ~ ~ ~ "
            + getOnCommand("~ ~ ~"), true), //
        new InternalCommand("/kill @e[name=" + mplNotify.getProcess() + NOTIFY + "]", true));
  }

  @Test
  public void test_invert_Notify() {
    // given:
    Mode mode = some($Enum(Mode.class));
    MplNotify mplNotify = some($MplNotify()//
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
    mplNotify.accept(underTest);

    // then:
    assertThat(underTest.commands).containsExactly(//
        new InvertingCommand(mode), //
        new InternalCommand("/execute @e[name=" + mplNotify.getProcess() + NOTIFY + "] ~ ~ ~ "
            + getOnCommand("~ ~ ~"), true), //
        new InternalCommand("/kill @e[name=" + mplNotify.getProcess() + NOTIFY + "]", true));
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
  public void test_when_visiting_a_breakpoint_the_breakpoint_process_is_added() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(UNCONDITIONAL));

    MplProgram program = some($MplProgram());
    program.addProcess(some($MplProcess()//
        .withChainParts(listOf(mplBreakpoint))));

    // when:
    program.accept(underTest);

    // then:
    Condition<CommandChain> condition = new Condition<CommandChain>() {
      @Override
      public boolean matches(CommandChain value) {
        return "breakpoint".equals(value.getName());
      }
    };
    assertThat(underTest.chains).haveExactly(1, condition);
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone()), //
        // else
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 8} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone()), //
        // else
        new InternalCommand("/testforblock ${this - 7} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 9} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 11} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InvertingCommand(then1.getMode()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}",
            true), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InvertingCommand(else1.getMode()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(then1.getCommand(), then1.getMode(), true, then1.getNeedsRedstone()), //
        new InvertingCommand(then1.getMode()), //
        new InternalCommand("/testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(then2.getCommand(), then2.getMode(), true, then2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}"), //
        new InternalCommand(then3.getCommand(), then3.getMode(), true, then3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone())//
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
        new InternalCommand(else1.getCommand(), else1.getMode(), true, else1.getNeedsRedstone()), //
        new InvertingCommand(else1.getMode()), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}",
            true), //
        new InternalCommand(else2.getCommand(), else2.getMode(), true, else2.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(else3.getCommand(), else3.getMode(), true, else3.getNeedsRedstone())//
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
        new InternalCommand(outer1.getCommand(), outer1.getMode(), true, outer1.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(outer2.getCondition(), true), //
        new InternalCommand(innerThen.getCommand(), innerThen.getMode(), true,
            innerThen.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand("/testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}",
            true), //
        new InternalCommand(innerElse.getCommand(), innerElse.getMode(), true,
            innerElse.getNeedsRedstone()), //
        new InternalCommand("/testforblock ${this - 8} chain_command_block -1 {SuccessCount:1}"), //
        new InternalCommand(outer3.getCommand(), outer3.getMode(), true, outer3.getNeedsRedstone())//
    );
  }

}
