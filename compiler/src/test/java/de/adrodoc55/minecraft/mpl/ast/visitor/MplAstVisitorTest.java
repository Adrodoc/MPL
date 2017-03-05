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
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.IMPULSE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newInvertingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand;
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand.REF;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

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

import com.google.common.collect.Iterators;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.ast.chainparts.Dependable;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
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
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLinkAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLinkIterableAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
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

  public <CL extends ChainLink> ChainLinkAssert<?, CL> assertThat(@Nullable CL actual) {
    return assertThat(actual, context.getOptions());
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
  public void test_an_impulse_process_ends_with_notify() throws Exception {
    // given:
    List<MplCommand> mplCommands = makeValid(listOf(several(), $MplCommand()//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL))));
    MplProcess process = some($MplProcess()//
        .withRepeating(false)//
        .withChainParts(mplCommands));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    assertThatNext(it).isNotInternal().isJumpDestination();
    Iterators.advance(it, mplCommands.size());
    assertThatNext(it).isNotInternal().isUnconditionalNotify(process.getName());
    assertThat(it).isEmpty();
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
  public void test_an_inline_process_is_ignored() throws Exception {
    // given:
    MplProcess process = some($MplProcess().withType(INLINE));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    assertThat(result).isNull();
  }

  @Test
  public void test_a_repeat_process_uses_a_repeat_command_block() {
    // given:
    MplCommand first = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand second = some($MplCommand()//
        .withPrevious(first)//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(first, second)));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();
    assertThat(it.next()).hasCommandParts(first.getCommand()).hasModifiers(REPEAT);
    assertThat(it.next()).matches(second);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.getCommands().iterator();
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it.next()).matches(first);
    assertThat(it.next()).isInvertingCommandFor(first.getMode()); // Important line!
    assertThat(it.next()).matches(second);
    assertThatNext(it).isNotInternal().isUnconditionalNotify(process.getName());
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
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();
    assertThat(it.next()).hasCommandParts(first.getCommand()).hasModifiers(REPEAT);
    assertThat(it.next()).isInvertingCommandFor(REPEAT); // Important line!
    assertThat(it.next()).matches(second);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isNotConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(modeForInverting);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isNotConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(modeForInverting);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand("~ ~ ~"))
        .hasMode(mode).isConditional().hasNeedsRedstone(needsRedstone);
    assertThat(it).isEmpty();
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
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
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
    assertThat(it.next()).isInternal().isStartCommand(+3).hasModifiers(mplWaitfor);
    assertThat(it.next()).isInvertingCommandFor(mplWaitfor.getMode());
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+1),
            " {CustomName:" + mplWaitfor.getEvent() + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(CONDITIONAL);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal()
        .hasCommandParts("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasDefaultModifiers();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Notify() {
    // given:
    MplNotify mplNotify = some($MplNotify()//
        .withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal()
        .hasCommandParts("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasModifiers(CONDITIONAL);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getOnCommand("~ ~ ~"))
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal()
        .hasCommandParts("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasModifiers(CONDITIONAL);
    assertThat(it).isEmpty();
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
    String event = some($String());
    MplIntercept mplIntercept = some($MplIntercept()//
        .withEvent(event)//
        .withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplIntercept.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().hasDefaultModifiers().hasCommandParts(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}");

    assertThat(it.next()).isInternal().hasDefaultModifiers().hasCommandParts(
        "summon " + markerEntity() + " ", new RelativeThisInsert(+1),
        " {CustomName:" + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

    assertThatNext(it).isNotInternal().isJumpDestination();

    assertThat(it.next()).isInternal().hasDefaultModifiers()
        .hasCommandParts("kill @e[name=" + event + ",r=2]");

    assertThat(it.next()).isInternal().hasDefaultModifiers().hasCommandParts(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}");

    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Intercept() {
    // given:
    String event = some($String());
    Mode mode = some($Mode());
    MplIntercept mplIntercept = some($MplIntercept()//
        .withEvent(event)//
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
    List<ChainLink> result = mplIntercept.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().hasModifiers(CONDITIONAL).hasCommandParts(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}");

    assertThat(it.next()).isInternal().hasModifiers(CONDITIONAL).hasCommandParts(
        "summon " + markerEntity() + " ", new RelativeThisInsert(+3),
        " {CustomName:" + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

    assertThat(it.next()).isInvertingCommandFor(CHAIN);

    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);

    assertThatNext(it).isNotInternal().isJumpDestination();

    assertThat(it.next()).isInternal().hasDefaultModifiers()
        .hasCommandParts("kill @e[name=" + event + ",r=2]");

    assertThat(it.next()).isInternal().hasDefaultModifiers().hasCommandParts(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}");

    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Intercept() {
    // given:
    String event = some($String());
    Mode mode = some($Mode());
    MplIntercept mplIntercept = some($MplIntercept()//
        .withEvent(event)//
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
    List<ChainLink> result = mplIntercept.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+4).hasModifiers(CONDITIONAL);

    assertThat(it.next()).isInvertingCommandFor(CHAIN);

    assertThat(it.next()).isInternal().hasModifiers(CONDITIONAL).hasCommandParts(
        "entitydata @e[name=" + event + "] {CustomName:" + event + INTERCEPTED + "}");

    assertThat(it.next()).isInternal().hasModifiers(CONDITIONAL).hasCommandParts(
        "summon " + markerEntity() + " ", new RelativeThisInsert(+1),
        " {CustomName:" + event + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}");

    assertThatNext(it).isNotInternal().isJumpDestination();

    assertThat(it.next()).isInternal().hasDefaultModifiers()
        .hasCommandParts("kill @e[name=" + event + ",r=2]");

    assertThat(it.next()).isInternal().hasDefaultModifiers().hasCommandParts(
        "entitydata @e[name=" + event + INTERCEPTED + "] {CustomName:" + event + "}");

    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition()).hasModifiers(mplIf);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition())
        .hasMode(mplIf.getMode()).isConditional().hasNeedsRedstone(mplIf.getNeedsRedstone());
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_If_with_one_Else_results_in_an_Invert() {
    // given:
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_If_with_one_Else_in_a_repeat_Process_results_in_an_Invert_which_references_a_Repeating_Command() {
    // given:
    MplCommand else1 = some($MplCommand()//
        .withConditional(UNCONDITIONAL));

    MplIf mplIf = some($MplIf()//
        .withNot(false)//
        .withElseParts(listOf(else1)));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(mplIf)));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();

    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition())
        .hasModifiers(REPEAT);
    assertThat(it.next()).isInvertingCommandFor(REPEAT); // Important line!
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_If_not_with_one_Then_results_in_an_Invert() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1))//
    );

    // when:
    List<ChainLink> result = mplIf.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_If_not_with_one_Then_in_a_repeat_Process_results_in_an_Invert_which_references_a_Repeating_Command() {
    // given:
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplIf mplIf = some($MplIf()//
        .withNot(true)//
        .withThenParts(listOf(then1)));

    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(listOf(mplIf)));

    // when:
    CommandChain result = underTest.visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();

    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition())
        .hasModifiers(REPEAT);
    assertThat(it.next()).isInvertingCommandFor(REPEAT); // Important line!
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isTestforSuccessCommand(-2, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    // then
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-4, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    // else
    assertThat(it.next()).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isTestforSuccessCommand(-8, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-10, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    // then
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isTestforSuccessCommand(-3, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-5, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    // else
    assertThat(it.next()).isTestforSuccessCommand(-7, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isTestforSuccessCommand(-9, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-11, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isInvertingCommandFor(then1.getMode());
    assertThat(it.next()).isTestforSuccessCommand(-3, true).isConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-4, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isInvertingCommandFor(else1.getMode());
    assertThat(it.next()).isTestforSuccessCommand(-4, false).isConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-4, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isTestforSuccessCommand(-1, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then1);
    assertThat(it.next()).isInvertingCommandFor(then1.getMode());
    assertThat(it.next()).isTestforSuccessCommand(-4, false).isConditional();
    assertThat(it.next()).matchesAsConditional(then2);
    assertThat(it.next()).isTestforSuccessCommand(-6, false).isNotConditional();
    assertThat(it.next()).matchesAsConditional(then3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(else1);
    assertThat(it.next()).isInvertingCommandFor(else1.getMode());
    assertThat(it.next()).isTestforSuccessCommand(-3, true).isConditional();
    assertThat(it.next()).matchesAsConditional(else2);
    assertThat(it.next()).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(else3);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).isNotInternal().hasCommandParts(outer.getCondition()).isConditional();
    assertThat(it.next()).matchesAsConditional(innerThen);
    assertThat(it.next()).isTestforSuccessCommand(-3, true).isNotConditional();
    assertThat(it.next()).isTestforSuccessCommand(-3, false).isConditional();
    assertThat(it.next()).matchesAsConditional(innerElse);
    assertThat(it).isEmpty();
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplIf.getCondition());
    assertThat(it.next()).isNormalizingCommand();
    assertThat(it.next()).matchesAsConditional(outer1);
    assertThat(it.next()).isTestforSuccessCommand(-2, true).isNotConditional();
    assertThat(it.next()).isNotInternal().hasCommandParts(outer2.getCondition()).isConditional();
    assertThat(it.next()).matchesAsConditional(innerThen);
    assertThat(it.next()).isTestforSuccessCommand(-5, true).isNotConditional();
    assertThat(it.next()).isTestforSuccessCommand(-3, false).isConditional();
    assertThat(it.next()).matchesAsConditional(innerElse);
    assertThat(it.next()).isTestforSuccessCommand(-8, true).isNotConditional();
    assertThat(it.next()).matchesAsConditional(outer3);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasModifiers(mplWhile);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal().hasCommandParts(mplWhile.getCondition())
        .hasModifiers(mplWhile);
  }

  @Test
  public void test_repeat_While_unconditional_modifier_affect_init_command() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional(UNCONDITIONAL)//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
  }

  @Test
  public void test_repeat_While_conditional_modifier_creates_conditional_jump() {
    // given:
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional(CONDITIONAL)//
        .withNeedsRedstone($boolean()));

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 3;
    if (underTest.options.hasOption(TRANSMITTER)) {
      ref--;
    }
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+3).hasModifiers(mplWhile);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(ref).hasModifiers(CONDITIONAL);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(ref).hasModifiers(mplWhile);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(mplWhile);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(+3).hasModifiers(mplWhile);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(ref).hasModifiers(CONDITIONAL);
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
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().isStartCommand(ref).hasModifiers(mplWhile);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
  }

  public abstract void test_repeat_mit_zwei_repeat();

  public abstract void test_repeat_while_mit_zwei_repeat();

  public abstract void test_repeat_while_not_mit_zwei_repeat();

  public abstract void test_while_repeat_mit_zwei_repeat();

  public abstract void test_while_not_repeat_mit_zwei_repeat();

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
