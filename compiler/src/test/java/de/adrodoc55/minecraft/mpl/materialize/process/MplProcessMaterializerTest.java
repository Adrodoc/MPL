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
package de.adrodoc55.minecraft.mpl.materialize.process;

import static de.adrodoc55.minecraft.mpl.MplTestUtils.findByName;
import static de.adrodoc55.minecraft.mpl.MplTestUtils.makeValid;
import static de.adrodoc55.minecraft.mpl.MplTestUtils.mapToCommands;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.commands.Mode.REPEAT;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import com.google.common.collect.Iterators;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.chain.ChainContainer;
import de.adrodoc55.minecraft.mpl.chain.CommandChain;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLinkAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLinkIterableAssert;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.MplSkip;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;

@FixMethodOrder(NAME_ASCENDING)
public abstract class MplProcessMaterializerTest extends MplTestBase {
  protected MplCompilerContext context;
  protected MplProcessMaterializer underTest;

  @Before
  public void before() {
    context = newContext();
    underTest = new MplProcessMaterializer(context);
  }

  protected abstract MplCompilerContext newContext();

  protected CommandChain visitProcess(MplProcess process) {
    MplProgram program = new MplProgram(new File(""), context);
    program.addProcess(process);
    return underTest.visitProcess(program, process);
  }

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
  public abstract void test_a_nameless_process_doesnt_have_startup_commands();

  @Test
  public void test_a_repeat_process_with_chainparts_results_in_a_chain_with_chainlinks()
      throws Exception {
    // given:
    List<MplCommand> mplCommands = makeValid(listOf(several(), $MplCommand()));
    MplProcess process = some($MplProcess()//
        .withRepeating(true)//
        .withChainParts(mplCommands));

    // when:
    CommandChain result = visitProcess(process);

    // then:
    mplCommands.get(0).setMode(REPEAT);
    mplCommands.get(0).setNeedsRedstone(true);
    List<ChainLink> commands = mapToCommands(mplCommands);
    if (context.getOptions().hasOption(TRANSMITTER)) {
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
    CommandChain result = visitProcess(process);

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
    CommandChain result = visitProcess(process);

    // then:
    assertThat(result.getTags()).containsExactlyElementsOf(tags);
  }

  @Test
  public void test_an_inline_process_is_ignored() throws Exception {
    // given:
    MplProcess process = some($MplProcess().withType(INLINE));

    // when:
    CommandChain result = visitProcess(process);

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
    CommandChain result = visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();
    assertThat(it.next()).hasMinecraftCommand(first.getMinecraftCommand()).hasModifiers(REPEAT);
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
    CommandChain result = visitProcess(process);

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
    CommandChain result = visitProcess(process);

    // then:
    Iterator<ChainLink> it = result.getCommands().iterator();
    if (context.getOptions().hasOption(TRANSMITTER))
      assertThat(it.next()).isSkip().isNotInternal();
    assertThat(it.next()).hasMinecraftCommand(first.getMinecraftCommand()).hasModifiers(REPEAT);
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
    ChainContainer result = underTest.materialize(program);

    // then:
    List<CommandChain> chains = result.getChains();
    assertThat(chains).hasSize(1);
    CommandChain mainChain = findByName(main.getName(), chains);
    assertThat(mainChain.getCommands())
        .containsSequence(mapToCommands(mplCommands).toArray(new ChainLink[0]));
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
    ChainContainer result = underTest.materialize(program);

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
    CommandChain result = visitProcess(process);

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
    CommandChain result = visitProcess(process);

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
}
