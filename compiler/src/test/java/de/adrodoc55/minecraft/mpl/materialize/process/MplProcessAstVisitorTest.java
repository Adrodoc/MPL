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

import static de.adrodoc55.minecraft.mpl.ast.Conditional.CONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.INVERT;
import static de.adrodoc55.minecraft.mpl.ast.Conditional.UNCONDITIONAL;
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.INLINE;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept.INTERCEPTED;
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY;
import static de.adrodoc55.minecraft.mpl.commands.Mode.CHAIN;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor;
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgramBuilder;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ChainLink;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ProcessCommandsHelper;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.interpretation.insert.RelativeThisInsert;
import net.karneim.pojobuilder.GenerateMplPojoBuilder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class MplProcessAstVisitorTest extends MplMaterializationTestBase {
  protected MplCompilerContext context;
  protected MplProcessAstVisitor underTest;
  protected ProcessCommandsHelper helper;

  @Before
  public void before() {
    context = newContext();
    underTest = some($UnderTest());
    helper = new ProcessCommandsHelper(getOptions());
  }

  @Override
  protected CompilerOptions getOptions() {
    return context.getOptions();
  }

  protected abstract MplCompilerContext newContext();

  @GenerateMplPojoBuilder
  public static MplProcessAstVisitor createMplProcessAstVisitor(MplCompilerContext compilerContext,
      MplProgram program) {
    return new MplProcessAstVisitor(compilerContext, new MplProcessAstVisitorContext(program));
  }

  public MplProcessAstVisitorBuilder $UnderTest() {
    return new MplProcessAstVisitorBuilder()//
        .withCompilerContext(context)//
        .withProgram($MplProgram())//
    ;
  }

  @Override
  public MplProgramBuilder $MplProgram() {
    return super.$MplProgram().withContext(context);
  }

  protected String markerEntity() {
    return context.getVersion().markerEntity();
  }

  protected String getStartCommand() {
    return helper.getStartCommand();
  }

  protected String getOffCommand() {
    return helper.getStopCommand();
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
  public void test_unconditional_Call() {
    // given:
    MplCall mplCall = some($MplCall()//
        .withConditional(UNCONDITIONAL));

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
    MplCall mplCall = some($MplCall().withConditional(CONDITIONAL));

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
    Mode mode = some($Mode());
    underTest.context.setPrevious(() -> mode);
    MplCall mplCall = some($MplCall().withConditional(INVERT));

    // when:
    List<ChainLink> result = mplCall.accept(underTest);

    // then:
    underTest.context.setPrevious(() -> mode);
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
  public void test_calling_an_inline_process_will_inline_all_ChainParts() {
    // given:
    List<MplCommand> mplCommands = listOf(several(), $MplCommand()//
        .withConditional($oneOf(UNCONDITIONAL, CONDITIONAL)));
    MplProcess inline = some($MplProcess()//
        .withType(INLINE)//
        .withRepeating(false)//
        .withChainParts(mplCommands));
    MplProgram program = some($MplProgram().withProcesses(listOf(inline)));
    MplProcessAstVisitor underTest = some($UnderTest().withProgram(program));

    MplCall mplCall = some($MplCall()//
        .withProcess(inline.getName())//
        .withMode(CHAIN)//
        .withConditional(UNCONDITIONAL)//
        .withNeedsRedstone(false)//
    );

    // when:
    List<ChainLink> result = mplCall.accept(underTest);

    // then:
    assertMatches(result, mplCommands);
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
    MplStart mplStart = some($MplStart().withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplStart);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Start() {
    // given:
    MplStart mplStart = some($MplStart().withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplStart);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Start() {
    // given:
    Mode mode = some($Mode());
    underTest.context.setPrevious(() -> mode);
    MplStart mplStart = some($MplStart().withConditional(INVERT));

    // when:
    List<ChainLink> result = mplStart.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStart.getSelector() + " ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplStart);
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
    MplStop mplStop = some($MplStop().withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand())
        .hasModifiers(mplStop);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Stop() {
    // given:
    MplStop mplStop = some($MplStop().withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand())
        .hasModifiers(mplStop);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Stop() {
    // given:
    Mode mode = some($Mode());
    underTest.context.setPrevious(() -> mode);
    MplStop mplStop = some($MplStop().withConditional(INVERT));

    // when:
    List<ChainLink> result = mplStop.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts("execute " + mplStop.getSelector() + " ~ ~ ~ " + getOffCommand())
        .hasModifiers(mplStop);
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
    MplWaitfor mplWaitfor = some($MplWaitfor().withConditional(UNCONDITIONAL));

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
    MplWaitfor mplWaitfor = some($MplWaitfor().withConditional(CONDITIONAL));

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
    MplWaitfor mplWaitfor = some($MplWaitfor().withConditional(INVERT));

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
    MplNotify mplNotify = some($MplNotify().withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplNotify);
    assertThat(it.next()).isInternal()
        .hasCommandParts("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasDefaultModifiers();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Notify() {
    // given:
    MplNotify mplNotify = some($MplNotify().withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplNotify);
    assertThat(it.next()).isInternal()
        .hasCommandParts("kill @e[name=" + mplNotify.getEvent() + NOTIFY + "]")
        .hasModifiers(CONDITIONAL);
    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Notify() {
    // given:
    Mode mode = some($Mode());
    underTest.context.setPrevious(() -> mode);
    MplNotify mplNotify = some($MplNotify().withConditional(INVERT));

    // when:
    List<ChainLink> result = mplNotify.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isNotInternal()
        .hasCommandParts(
            "execute @e[name=" + mplNotify.getEvent() + NOTIFY + "] ~ ~ ~ " + getStartCommand())
        .hasModifiers(mplNotify);
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
    underTest.context.setPrevious(() -> mode);
    MplIntercept mplIntercept = some($MplIntercept()//
        .withEvent(event)//
        .withConditional(CONDITIONAL)//
    );

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
    underTest.context.setPrevious(() -> mode);
    MplIntercept mplIntercept = some($MplIntercept()//
        .withEvent(event)//
        .withConditional(INVERT)//
    );

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
  public void test_unconditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint()//
        .withConditional(UNCONDITIONAL));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().hasCommandParts("say " + mplBreakpoint.getMessage())
        .hasModifiers(mplBreakpoint);
    assertThat(it.next())/* TODO: .isInternal() */
        .hasCommandParts("execute @e[name=breakpoint] ~ ~ ~ " + getStartCommand())
        .hasDefaultModifiers();
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+1),
            " {CustomName:breakpoint" + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasDefaultModifiers();
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_conditional_Breakpoint() {
    // given:
    MplBreakpoint mplBreakpoint = some($MplBreakpoint().withConditional(CONDITIONAL));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInternal().hasCommandParts("say " + mplBreakpoint.getMessage())
        .hasModifiers(mplBreakpoint);
    assertThat(it.next())/* TODO: .isInternal() */
        .hasCommandParts("execute @e[name=breakpoint] ~ ~ ~ " + getStartCommand())
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+3),
            " {CustomName:breakpoint" + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
  }

  @Test
  public void test_invert_Breakpoint() {
    // given:
    Mode mode = some($Mode());
    underTest.context.setPrevious(() -> mode);
    MplBreakpoint mplBreakpoint = some($MplBreakpoint().withConditional(INVERT));

    // when:
    List<ChainLink> result = mplBreakpoint.accept(underTest);

    // then:
    Iterator<ChainLink> it = result.iterator();
    assertThat(it.next()).isInvertingCommandFor(mode);
    assertThat(it.next()).isInternal().hasCommandParts("say " + mplBreakpoint.getMessage())
        .hasModifiers(mplBreakpoint);
    assertThat(it.next())/* TODO: .isInternal() */
        .hasCommandParts("execute @e[name=breakpoint] ~ ~ ~ " + getStartCommand())
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInternal()
        .hasCommandParts("summon " + markerEntity() + " ", new RelativeThisInsert(+3),
            " {CustomName:breakpoint" + NOTIFY
                + ",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
        .hasModifiers(CONDITIONAL);
    assertThat(it.next()).isInvertingCommandFor(CHAIN);
    assertThat(it.next()).isInternal().isStartCommand(+1).hasModifiers(CONDITIONAL);
    assertThatNext(it).isNotInternal().isJumpDestination();
    assertThat(it).isEmpty();
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
    underTest.context.setPrevious(() -> mode);
    MplIf mplIf = some($MplIf()//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
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
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(INVERT));
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
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(INVERT));
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
    MplCommand then1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand then2 = some($MplCommand().withConditional(INVERT));
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
        .withElseParts(listOf(else1, else2, else3))//
    );

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
    MplCommand else1 = some($MplCommand().withConditional(UNCONDITIONAL));
    MplCommand else2 = some($MplCommand().withConditional(INVERT));
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
    underTest.context.setPrevious(() -> mode);
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(false)//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
    );

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
    if (getOptions().hasOption(TRANSMITTER)) {
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
    underTest.context.setPrevious(() -> mode);
    MplWhile mplWhile = some($MplWhile()//
        .withTrailing(true)//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
    );

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 1;
    if (getOptions().hasOption(TRANSMITTER)) {
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
    if (getOptions().hasOption(TRANSMITTER)) {
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
    underTest.context.setPrevious(() -> mode);
    MplWhile mplWhile = some($MplWhile()//
        .withCondition((String) null)//
        .withNot($boolean())//
        .withTrailing($boolean())//
        .withMode($Mode())//
        .withConditional(INVERT)//
        .withNeedsRedstone($boolean())//
    );

    // when:
    List<ChainLink> result = mplWhile.accept(underTest);

    // then:
    int ref = result.size() - 1;
    if (getOptions().hasOption(TRANSMITTER)) {
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

  @Test
  public abstract void test_unconditional_Break();

  @Test
  public abstract void test_conditional_Break();

  @Test
  public abstract void test_invert_Break();

  @Test
  public abstract void test_nested_Break_stops_all_inner_loops();

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
  public abstract void test_unconditional_Continue_without_condition();

  @Test
  public abstract void test_unconditional_Continue_with_condition();

  @Test
  public abstract void test_conditional_Continue_without_condition();

  @Test
  public abstract void test_conditional_Continue_with_condition();

  @Test
  public abstract void test_invert_Continue_without_condition();

  @Test
  public abstract void test_invert_Continue_with_condition();

  @Test
  public abstract void test_nested_Continue_stops_all_inner_loops();
}
