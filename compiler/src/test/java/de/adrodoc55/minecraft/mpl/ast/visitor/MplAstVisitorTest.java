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

import static de.adrodoc55.minecraft.mpl.MplTestUtils.findByName;
import static de.adrodoc55.minecraft.mpl.MplTestUtils.makeValid;
import static de.adrodoc55.minecraft.mpl.MplTestUtils.mapToCommands;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static de.adrodoc55.minecraft.mpl.interpretation.ModifierBuffer.modifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLinkIterableAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class MplAstVisitorTest extends MplTestBase {
  protected MplCompilerContext context;
  protected MplMainAstVisitor underTest;

  @Before
  public void before() {
    context = newContext();
    underTest = newUnderTest(context);
  }

  protected String markerEntity() {
    return context.getVersion().markerEntity();
  }

  protected abstract MplCompilerContext newContext();

  protected abstract MplMainAstVisitor newUnderTest(MplCompilerContext context);

  protected abstract String getOnCommand(String ref);

  protected abstract String getOffCommand(String ref);

  private String getStartCommandHeader() {
    return context.getOptions().hasOption(TRANSMITTER) ? "setblock " : "blockdata ";
  }

  private String getStartCommandTrailer() {
    return context.getOptions().hasOption(TRANSMITTER) ? " redstone_block" : " {auto:1b}";
  }

  private Object[] getStartCommand(Object ref) {
    return new Object[] {getStartCommandHeader(), ref, getStartCommandTrailer()};
  }

  public ChainLinkIterableAssert assertThatNext(@Nullable Iterator<ChainLink> actual) {
    return new ChainLinkIterableAssert(actual, context.getOptions());
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
  public void test_a_repeat_process_with_chainparts_results_in_a_chain_with_chainlinks()
      throws Exception {
    // given:
    List<MplCommand> mplCommands = makeValid(listOf(several(), $MplCommand()));
    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(mplCommands));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    mplCommands.get(0).setMode(REPEAT);
    mplCommands.get(0).setNeedsRedstone(true);
    List<ChainLink> commands = mapToCommands(mplCommands);
    if (underTest.options.hasOption(TRANSMITTER)) {
      commands.add(0, new MplSkip());
    }
    assertThat(result.getCommands()).containsExactlyElementsOf(commands);
  }

  @Test
  public void test_an_impulse_process_always_ends_with_notify() throws Exception {
    // given:
    List<MplCommand> mplCommands = makeValid(listOf(several(), $MplCommand()));
    MplProcess process = some($MplProcess()//
        .withRepeating(false)//
        .withChainParts(mplCommands));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    List<ChainLink> commands = result.getCommands();
    int i = commands.size() - 3;
    // @formatter:off
    assertThat(commands.get(i++)).asCommand()//
      .isInternal()
      .hasCommand("/execute @e[name=" + process.getName() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
      .hasModifiers(modifier()
    );
    assertThat(commands.get(i++)).asCommand()
      .isNotInternal()
      .hasCommand("/kill @e[name=" + process.getName() + NOTIFY + "]")
      .hasModifiers(modifier()
    );
    // @formatter:on
  }

  @Test
  public void test_a_process_with_tags_results_in_a_chain_with_tags() throws Exception {
    // given:
    List<String> tags = some($listOf(several(), $String()));
    MplProcess process = some($MplProcess().withTags(tags));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    assertThat(result.getTags()).containsExactlyElementsOf(tags);
  }

  @Test
  public void test_a_inline_process_is_ignored() throws Exception {
    // given:
    MplProcess process = some($MplProcess().withType(INLINE));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    assertThat(result).isNull();
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
    CommandChain result = underTest.visitProcess(process);

    // then:
    int i = context.getOptions().hasOption(TRANSMITTER) ? 2 : 1;
    List<ChainLink> commands = result.getCommands();
    assertThat(commands.get(i++)).hasCommand(first.getCommand()).hasModifiers(first);
    assertThat(commands.get(i++)).isInvertingCommandFor(first.getMode()); // Important line!
    assertThat(commands.get(i++)).matches(second);
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
    CommandChain result = underTest.visitProcess(process);

    // then:
    int i = context.getOptions().hasOption(TRANSMITTER) ? 2 : 1;
    List<ChainLink> commands = result.getCommands();
    assertThat(commands.get(i++)).hasCommand(first.getCommand()).hasModifiers(modifier(REPEAT));
    assertThat(commands.get(i++)).isInvertingCommandFor(REPEAT); // Important line!
    assertThat(commands.get(i++)).matches(second);
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //     ____        _  _
  //    / ___| __ _ | || |
  //   | |    / _` || || |
  //   | |___| (_| || || |
  //    \____|\__,_||_||_|
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void test_calling_an_inline_process_will_inline_all_ChainParts() {
    // given:
    List<MplCommand> mplCommands = makeValid(listOf(several(), $MplCommand()));
    MplProcess inline = some($MplProcess()//
        .withType(INLINE)//
        .withRepeating(false)//
        .withChainParts(mplCommands));
    MplProcess main = some($MplProcess()//
        .withRepeating(false)//
        .withChainParts(listOf(//
            some($MplCall()//
                .withProcess(inline.getName())//
                .withMode(CHAIN)//
                .withConditional(UNCONDITIONAL)//
                .withNeedsRedstone(false))//
    )));
    MplProgram program = some($MplProgram().withProcesses(listOf(main, inline)));

    // when:
    ChainContainer result = underTest.visitProgram(program);

    // then:
    List<CommandChain> chains = result.getChains();
    assertThat(chains).hasSize(1);
    CommandChain mainChain = findByName(main.getName(), chains);
    assertThat(mainChain.getCommands())
        .containsSequence(mapToCommands(mplCommands).toArray(new ChainLink[0]));
  }

  @Test
  public void test_unconditional_Call() {
    // given:
    MplCall mplCall = some($MplCall()//
        .withConditional(UNCONDITIONAL));
    underTest.program = some($MplProgram().withProcesses(listOf(//
        some($MplProcess().withName(mplCall.getProcess()))//
    )));

    // when:
    List<ChainLink> result = mplCall.accept(underTest);

    // then:
    List<ChainLink> expected = new ArrayList<>();
    MplStart mplStart = some($MplStart()//
        .withSelector("@e[name=" + mplCall.getProcess() + "]")//
        .withModifier(mplCall));
    expected.addAll(mplStart.accept(underTest));
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withEvent(mplCall.getProcess())//
        .withMode(CHAIN)//
        .withConditional(UNCONDITIONAL)//
        .withNeedsRedstone(false));
    expected.addAll(mplWaitfor.accept(underTest));

    assertThat(result).containsExactlyElementsOf(expected);
  }

  @Test
  public void test_conditional_Call() {
    // given:
    MplCall mplCall = some($MplCall()//
        .withConditional(CONDITIONAL));
    underTest.program = some($MplProgram().withProcesses(listOf(//
        some($MplProcess().withName(mplCall.getProcess()))//
    )));

    // when:
    List<ChainLink> result = mplCall.accept(underTest);

    // then:
    List<ChainLink> expected = new ArrayList<>();
    MplStart mplStart = some($MplStart()//
        .withSelector("@e[name=" + mplCall.getProcess() + "]")//
        .withModifier(mplCall));
    expected.addAll(mplStart.accept(underTest));
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withEvent(mplCall.getProcess())//
        .withMode(CHAIN)//
        .withConditional(CONDITIONAL)//
        .withNeedsRedstone(false));
    expected.addAll(mplWaitfor.accept(underTest));

    assertThat(result).containsExactlyElementsOf(expected);
  }

  @Test
  public void test_invert_Call() {
    // given:
    Mode modeForInverting = some($Mode());
    MplCall mplCall = some($MplCall()//
        .withConditional(CONDITIONAL).withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() {
            return modeForInverting;
          }
        }));
    underTest.program = some($MplProgram().withProcesses(listOf(//
        some($MplProcess().withName(mplCall.getProcess()))//
    )));

    // when:
    List<ChainLink> result = mplCall.accept(underTest);

    // then:
    List<ChainLink> expected = new ArrayList<>();
    MplStart mplStart = some($MplStart()//
        .withSelector("@e[name=" + mplCall.getProcess() + "]")//
        .withModifier(mplCall));
    expected.addAll(mplStart.accept(underTest));
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withEvent(mplCall.getProcess())//
        .withMode(CHAIN)//
        .withConditional(CONDITIONAL)//
        .withNeedsRedstone(false));
    expected.addAll(mplWaitfor.accept(underTest));

    assertThat(result).containsExactlyElementsOf(expected);
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
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isNotConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_conditional_Start() {
    // given:
    MplStart mplStart = some($MplStart()//
        .withConditional(CONDITIONAL));
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_invert_Start() {
    // given:
    Mode modeForInverting = some($Mode());
    MplStart mplStart = some($MplStart()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() {
            return modeForInverting;
          }
        }));
    Mode mode = mplStart.getMode();
    boolean needsRedstone = mplStart.getNeedsRedstone();

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInvertingCommandFor(modeForInverting);
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isNotConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_conditional_Stop() {
    // given:
    MplStop mplStop = some($MplStop()//
        .withConditional(CONDITIONAL));
    Mode mode = mplStop.getMode();
    boolean needsRedstone = mplStop.getNeedsRedstone();

    // when:
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_invert_Stop() {
    // given:
    Mode modeForInverting = some($Mode());
    MplStop mplStop = some($MplStop()//
        .withConditional(INVERT)//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() {
            return modeForInverting;
          }
        }));
    Mode mode = mplStop.getMode();
    boolean needsRedstone = mplStop.getNeedsRedstone();

    // when:
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInvertingCommandFor(modeForInverting);
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplWaitfor.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+1),
            " {CustomName:" + mplWaitfor.getEvent() + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(mplWaitfor);
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplWaitfor.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+3),
            " {CustomName:" + mplWaitfor.getEvent() + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(mplWaitfor);
    assertThat(it.next()).isInvertingCommandFor(mplWaitfor.getMode());
    assertThat(it.next()).isInternal().hasCommandParts(getStartCommand(new RelativeThisInsert(+1)))//
        .hasModifiers(modifier(CONDITIONAL));
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Waitfor() {
    // given:
    MplWaitfor mplWaitfor = some($MplWaitfor()//
        .withConditional(INVERT));

    // when:
    List<ChainLink> result = mplWaitfor.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().hasCommandParts(getStartCommand(new RelativeThisInsert(+3)))//
        .hasModifiers(mplWaitfor);
    assertThat(it.next()).isInvertingCommandFor(mplWaitfor.getMode());
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+1),
            " {CustomName:" + mplWaitfor.getEvent() + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(modifier(CONDITIONAL));
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
  }
  // TODO: Intercept und Breakpoint Tests aus Subklassen hochziehen

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
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInternal()
        .hasCommand(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasModifiers(modifier());
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]").hasModifiers(modifier());
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_conditional_Notify() {
    // given:
    MplNotify mplNotify = some($MplNotify()//
        .withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInternal()
        .hasCommand(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasModifiers(modifier(CONDITIONAL));
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasModifiers(modifier(CONDITIONAL));
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_invert_Notify() {
    // given:
    Mode mode = some($Mode());
    MplNotify mplNotify = some($MplNotify()//
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
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInvertingCommandFor(mode);
    assertThat(result.get(i++)).isInternal()
        .hasCommand(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasModifiers(modifier(CONDITIONAL));
    assertThat(result.get(i++)).isNotInternal()
        .hasCommand("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasModifiers(modifier(CONDITIONAL));
    assertThat(result).hasSize(i);
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
    ChainContainer result = underTest.visitProgram(program);

    // then:
    Condition<CommandChain> condition = new Condition<CommandChain>() {
      @Override
      public boolean matches(CommandChain value) {
        return "breakpoint".equals(value.getName());
      }
    };
    assertThat(result.getChains()).haveExactly(1, condition);
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
  public void test_If_modifier_gelten_fuer_condition() {
    // given:
    MplIf mplIf = some($MplIf()//
        .withMode($Mode())//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL))//
        .withNeedsRedstone($boolean())//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition())
        .hasModifiers(mplIf);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_modifier_mit_invert_gelten_fuer_condition() {
    // given:
    Mode mode = some($Mode());

    MplIf mplIf = some($MplIf()//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
        .withPrevious(new Dependable() {
          @Override
          public boolean canBeDependedOn() {
            return true;
          }

          @Override
          public Mode getModeForInverting() {
            return mode;
          }
        })//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isInvertingCommandFor(mode);
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition())
        .hasMode(mplIf.getMode()).isConditional().hasNeedsRedstone(mplIf.getNeedsRedstone());
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_then_mit_skip_wirft_exception() {
    // given:
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(some($MplSkip())))//
    );

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
        .withElseParts(listOf(some($MplSkip())))//
    );

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
        .withThenParts(listOf(then1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_mit_nur_einem_else_wird_zu_einem_invert() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isInvertingCommandFor(CHAIN);
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_not_mit_nur_einem_then_wird_zu_einem_invert() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isInvertingCommandFor(CHAIN);
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_not_mit_nur_einem_else_wird_zu_einem_conditional() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_mit_then_und_else_wird_zu_conditional_und_invert() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1))//
        .withElseParts(listOf(else1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-2, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_mit_meheren_then___erster_conditional_andere_SuccessCount_1() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(then1, then2, then3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_not_mit_meheren_then___alle_SuccessCount_0() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1, then2, then3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_mit_meheren_else___alle_SuccessCount_0() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1, else2, else3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
  }

  @Test
  public void test_If_not_mit_meheren_else___erster_conditional_andere_SuccessCount_1() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else3 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withElseParts(listOf(else1, else2, else3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
        .withElseParts(listOf(else1, else2, else3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    // then
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    // else
    assertThat(result.get(i++)).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-8, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-10, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
        .withElseParts(listOf(else1, else2, else3))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    // then
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    // else
    assertThat(result.get(i++)).isTestforSuccessCommand(-7, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-9, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-11, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isInvertingCommandFor(then1.getMode());
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, true).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isInvertingCommandFor(else1.getMode());
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, false).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then1);
    assertThat(result.get(i++)).isInvertingCommandFor(then1.getMode());
    assertThat(result.get(i++)).isTestforSuccessCommand(-4, false).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(then2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(then3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(else1);
    assertThat(result.get(i++)).isInvertingCommandFor(else1.getMode());
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, true).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(else2);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(else3);
    assertThat(result).hasSize(i);
  }

  /**
   * <pre>
   * if: /mplIf
   * then (
   *   if: /outer
   *   then (
   *     /innerThen
   *   ) else (
   *     /innerElse
   *   )
   * )
   * </pre>
   */
  @Test
  public void test_If_mit_kleinem_nested_then() {
    // given:

    MplCommand innerThen = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand innerElse = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf outer = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(innerThen))//
        .withElseParts(listOf(innerElse)));

    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withThenParts(listOf(outer)));

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).isNotInternal().hasCommand(outer.getCondition()).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(innerThen);
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, false).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(innerElse);
    assertThat(result).hasSize(i);
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
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    int i = 0;
    assertThat(result.get(i++)).isNotInternal().hasCommand(mplIf.getCondition());
    assertThat(result.get(i++)).isNormalizingCommand();
    assertThat(result.get(i++)).matchesAsConditional(outer1);
    assertThat(result.get(i++)).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(result.get(i++)).isNotInternal().hasCommand(outer2.getCondition()).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(innerThen);
    assertThat(result.get(i++)).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(result.get(i++)).isTestforSuccessCommand(-3, false).isConditional();
    assertThat(result.get(i++)).matchesAsConditional(innerElse);
    assertThat(result.get(i++)).isTestforSuccessCommand(-8, true).isNotConditional();
    assertThat(result.get(i++)).matchesAsConditional(outer3);
    assertThat(result).hasSize(i);
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
  public void test_While_mit_skip_wirft_exception() {
    // given:
    MplWhile mplIf = some($MplWhile()//
        .withChainParts(listOf(1, $MplSkip())));

    // when:
    Exception act = null;
    try {
      mplIf.accept(underTest);
    } catch (IllegalStateException ex) {
      act = ex;
    }

    // then:
    assertThat(act).isNotNull();
    assertThat(act.getMessage()).isEqualTo("while cannot start with skip");
  }

  @Test
  public void test_While_repeat_modifier_gelten_fuer_condition() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(false)//
        .withMode($Mode())//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL))//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    assertThat(result).startsWith(//
        new Command(mplWhile.getCondition(), mplWhile)//
    );
  }

  @Test
  @Ignore("While can't be invert")
  public void test_While_repeat_modifier_mit_invert_gelten_fuer_condition() {
    // given:
    Mode mode = some($Mode());

    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(false)//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
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
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    assertThat(result).startsWith(//
        newInvertingCommand(mode), //
        new Command(mplWhile.getCondition(), mplWhile)//
    );
  }

  @Test
  public void test_repeat_While_unconditional_modifier_gelten_fuer_init_command() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional($oneOf(UNCONDITIONAL))//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + 1}"), mplWhile)//
    );
  }

  @Test
  public void test_repeat_While_conditional_modifier_erzeugt_conditional_jump() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional($oneOf(CONDITIONAL))//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 3;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + 3}"), mplWhile), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + " + ref + "}"), true)//
    );
  }

  @Test
  public void test_repeat_While_invert_modifier_erzeugt_invert_jump() {
    // given:
    Mode mode = some($Mode());

    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
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
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 1;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + " + ref + "}"), mplWhile), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true)//
    );
  }

  @Test
  public void test_repeat_unconditional_modifier_gelten_fuer_init_command() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withMode($Mode())//
        .withConditional($oneOf(UNCONDITIONAL))//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + 1}"), mplWhile)//
    );
  }

  @Test
  public void test_repeat_conditional_modifier_erzeugt_conditional_jump() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withMode($Mode())//
        .withConditional($oneOf(CONDITIONAL))//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 3;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + 3}"), mplWhile), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + " + ref + "}"), true)//
    );
  }

  @Test
  public void test_repeat_invert_modifier_erzeugt_invert_jump() {
    // given:
    Mode mode = some($Mode());

    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
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
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 1;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    assertThat(result).startsWith(//
        new InternalCommand(getOnCommand("${this + " + ref + "}"), mplWhile), //
        newInvertingCommand(CHAIN), //
        new InternalCommand(getOnCommand("${this + 1}"), true)//
    );
  }

  @Test
  public void test_While_mit_Waitfor_hat_korrekte_Referenzen_zum_Ende() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withNot(false)//
        .withTrailing(false)//
        .withConditional(UNCONDITIONAL)//
        .withChainParts(listOf(some($MplWaitfor()))));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 1;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    int jumpIndex = 3;
    ReferencingCommand jump = (ReferencingCommand) result.get(jumpIndex);
    assertThat(jumpIndex + jump.getRelative()).isEqualTo(ref);
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

  private int findFirstReciever(List<ChainLink> chainLinks) {
    for (int i = 0; i < chainLinks.size(); i++) {
      ChainLink chainLink = chainLinks.get(i);
      if (isReciever(chainLink)) {
        return i;
      }
    }
    return -1;
  }

  private int findSecondReciever(List<ChainLink> chainLinks) {
    int startIndex = findFirstReciever(chainLinks) + 1;
    for (int i = startIndex; i < chainLinks.size(); i++) {
      ChainLink chainLink = chainLinks.get(i);
      if (isReciever(chainLink)) {
        return i;
      }
    }
    return -1;
  }

  private int findLastReciever(List<ChainLink> chainLinks) {
    for (int i = chainLinks.size() - 1; i >= 0; i--) {
      ChainLink chainLink = chainLinks.get(i);
      if (isReciever(chainLink)) {
        return i;
      }
    }
    return -1;
  }

  private boolean isReciever(ChainLink chainLink) {
    if (underTest.options.hasOption(TRANSMITTER)) {
      if (chainLink instanceof MplSkip) {
        return true;
      }
    } else {
      if (chainLink instanceof Command && ((Command) chainLink).getMode() == IMPULSE) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void test_unconditional_Break() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int beforeBreak = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeBreak + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), mplBreak.getMode(), false,
            mplBreak.getNeedsRedstone(), exit - (beforeBreak + 1)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeBreak + 2))//
    );
    assertThat(commands.size()).isBetween(3, 4);
  }

  @Test
  public void test_conditional_Break() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int beforeBreak = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeBreak + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), mplBreak.getMode(), true,
            mplBreak.getNeedsRedstone(), exit - (beforeBreak + 1)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeBreak + 2)), //
        newInvertingCommand(CHAIN), //
        new ReferencingCommand(getOnCommand(REF), true, 1)//
    );
  }

  @Test
  public void test_invert_Break() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplBreak mplBreak = some($MplBreak()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int beforeBreak = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeBreak + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), mplBreak.getMode(), true,
            mplBreak.getNeedsRedstone(), 4), //
        newInvertingCommand(CHAIN), //
        new ReferencingCommand(getOnCommand(REF), true, exit - (beforeBreak + 3)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeBreak + 4))//
    );
  }

  @Test
  public void test_nested_Break_stops_all_inner_loops() {
    // given:
    MplCommand command = some($MplCommand()//
        .withCommand("command")//
        .withConditional(UNCONDITIONAL));
    MplWhile innerWhile = some($MplWhile()//
        .withCondition((String) null));

    MplWhile outerWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withChainParts(listOf(innerWhile)));

    MplBreak mplBreak = some($MplBreak()//
        .withLoop(outerWhile)//
        .withConditional(UNCONDITIONAL)//
        .withPrevious(command));
    innerWhile.setChainParts(listOf(command, mplBreak));

    // when:
    List<ChainLink> result = outerWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int outerEntry = findFirstReciever(commands);
    int innerEntry = findSecondReciever(commands);
    int outerExit = findLastReciever(commands);
    int beforeBreak = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeBreak + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), mplBreak.getMode(), false,
            mplBreak.getNeedsRedstone(), outerExit - (beforeBreak + 1)), //
        new ReferencingCommand(getOffCommand(REF), true, innerEntry - (beforeBreak + 2)), //
        new ReferencingCommand(getOffCommand(REF), true, outerEntry - (beforeBreak + 3))//
    );
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
  public void test_unconditional_Continue_without_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOffCommand(REF), mplContinue.getMode(), false,
            mplContinue.getNeedsRedstone(), entry - (beforeContinue + 1)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 2))//
    );
    assertThat(commands.size()).isBetween(3, 4);
  }

  @Test
  public void test_unconditional_Continue_with_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(UNCONDITIONAL));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        new Command(mplWhile.getCondition(), mplContinue), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 2)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 3)), //
        newInvertingCommand(CHAIN), //
        new ReferencingCommand(getOnCommand(REF), true, exit - (beforeContinue + 5)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 6))//
    );
    assertThat(commands.size()).isBetween(7, 8);
  }

  @Test
  public void test_conditional_Continue_without_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int doNothing = findSecondReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 1)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 2)), //
        newTestforSuccessCommand(-3, IMPULSE, false), //
        new ReferencingCommand(getOnCommand(REF), true, doNothing - (beforeContinue + 4))//
    );
  }

  @Test
  public void test_conditional_Continue_with_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(CONDITIONAL)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int doNothing = findSecondReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        newNormalizingCommand(), //
        new Command(mplWhile.getCondition(), true), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 3)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 4)), //
        newTestforSuccessCommand(-4, CHAIN, true), //
        newTestforSuccessCommand(-4, CHAIN, false, true), //
        new ReferencingCommand(getOnCommand(REF), true, exit - (beforeContinue + 7)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 8)), //
        newTestforSuccessCommand(-8, CHAIN, false), //
        new ReferencingCommand(getOnCommand(REF), true, doNothing - (beforeContinue + 10))//
    );
  }

  @Test
  public void test_invert_Continue_without_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null));
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int doNothing = findSecondReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), true, doNothing - (beforeContinue + 1)), //
        newTestforSuccessCommand(-2, IMPULSE, false), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 3)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 4))//
    );
  }

  @Test
  public void test_invert_Continue_with_condition() {
    // given:
    MplCommand command = some($MplCommand().withConditional(UNCONDITIONAL));
    MplWhile mplWhile = some($MplWhile());
    MplContinue mplContinue = some($MplContinue()//
        .withLoop(mplWhile)//
        .withConditional(INVERT)//
        .withPrevious(command));
    mplWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    List<ChainLink> commands = result;
    int entry = findFirstReciever(commands);
    int exit = findLastReciever(commands);
    int doNothing = findSecondReciever(commands);
    int beforeContinue = commands.indexOf(new Command(command.getCommand(), command));
    commands = commands.subList(beforeContinue + 1, commands.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOnCommand(REF), true, doNothing - (beforeContinue + 1)), //
        newTestforSuccessCommand(-2, IMPULSE, false), //
        new Command(mplWhile.getCondition(), true), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 4)), //
        new ReferencingCommand(getOnCommand(REF), true, entry - (beforeContinue + 5)), //
        newTestforSuccessCommand(-6, IMPULSE, false), //
        newTestforSuccessCommand(-4, CHAIN, false, true), //
        new ReferencingCommand(getOnCommand(REF), true, exit - (beforeContinue + 8)), //
        new ReferencingCommand(getOffCommand(REF), true, entry - (beforeContinue + 9))//
    );
  }

  @Test
  public void test_nested_Continue_stops_all_inner_loops() {
    // given:
    MplCommand command = some($MplCommand()//
        .withCommand("command")//
        .withConditional(UNCONDITIONAL));
    MplWhile innerWhile = some($MplWhile()//
        .withCondition((String) null));

    MplWhile outerWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withChainParts(listOf(innerWhile)));

    MplContinue mplContinue = some($MplContinue()//
        .withLoop(outerWhile)//
        .withConditional(UNCONDITIONAL)//
        .withPrevious(command));
    innerWhile.setChainParts(listOf(command, mplContinue));

    // when:
    List<ChainLink> result = outerWhile.accept(underTest);

    // then:
    int outerEntry = findFirstReciever(result);
    int innerEntry = findSecondReciever(result);
    int beforeContinue = result.indexOf(new Command(command.getCommand(), command));
    List<ChainLink> commands = result.subList(beforeContinue + 1, result.size());

    assertThat(commands).startsWith(//
        new ReferencingCommand(getOffCommand(REF), mplContinue.getMode(), false,
            mplContinue.getNeedsRedstone(), innerEntry - (beforeContinue + 1)), //
        new ReferencingCommand(getOffCommand(REF), true, outerEntry - (beforeContinue + 2)), //
        new ReferencingCommand(getOnCommand(REF), true, outerEntry - (beforeContinue + 3))//
    );
  }

}
