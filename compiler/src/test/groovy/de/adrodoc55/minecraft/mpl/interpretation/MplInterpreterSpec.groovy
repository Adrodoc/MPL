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
package de.adrodoc55.minecraft.mpl.interpretation

import static de.adrodoc55.TestBase.someString
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newNormalizingCommand
import static de.adrodoc55.minecraft.mpl.commands.chainlinks.Commands.newTestforSuccessCommand
import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

import spock.lang.Ignore

import com.google.common.collect.ListMultimap

import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.assembly.MplReference
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram
import de.adrodoc55.minecraft.mpl.chain.CommandChain
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command

@Ignore // Legacy
public class MplInterpreterSpec extends MplSpecBase {

  @Test
  public void "breakpoint in repeating Prozess wirft Exception"() {
    given:
    String name = some($Identifier())
    String identifier = some($Identifier())
    String programString = """
    repeat process ${name} (
    breakpoint
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 4
    project.exceptions[0].source.token.text == 'waitfor'
    project.exceptions[0].message == 'Encountered breakpoint in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "breakpoint in repeating script wirft Exception"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    repeat: /say hi
    breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'waitfor'
    script.exceptions[0].message == 'Encountered breakpoint in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "waitfor in repeating Prozess wirft Exception"() {
    given:
    String name = some($Identifier())
    String identifier = some($Identifier())
    String programString = """
    repeat process ${name} (
    start ${identifier}
    waitfor
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 4
    project.exceptions[0].source.token.text == 'waitfor'
    project.exceptions[0].message == 'Encountered waitfor in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "waitfor in repeating script wirft Exception"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    repeat: /say hi
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'waitfor'
    script.exceptions[0].message == 'Encountered waitfor in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "intercept in repeating Prozess wirft Exception"() {
    given:
    String name = some($Identifier())
    String identifier = some($Identifier())
    String programString = """
    repeat process ${name} (
    intercept ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'intercept'
    project.exceptions[0].message == 'Encountered intercept in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "intercept in repeating script wirft Exception"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    repeat: /say hi
    intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 3
    script.exceptions[0].source.token.text == 'intercept'
    script.exceptions[0].message == 'Encountered intercept in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "nested if"() {
    given:
    String programString = """
    if: /outer condition
    then (
      /say outer then1
      if: /inner condition
      then (
        /say inner then
      ) else (
        /say inner else
      )
      /say outer then2
    ) else (
      /say outer else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == newNormalizingCommand()
    commands[2] == new Command('say outer then1', true)
    commands[3] == newTestforSuccessCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('inner condition', true)
    commands[5] == new Command('say inner then', true)
    commands[6] == newTestforSuccessCommand(-5, 'chain_command_block', true)
    commands[7] == newTestforSuccessCommand(-3, 'chain_command_block', false, true)
    commands[8] == new Command('say inner else', true)
    commands[9] == newTestforSuccessCommand(-8, 'chain_command_block', true)
    commands[10] == new Command('say outer then2', true)
    commands[11] == newTestforSuccessCommand(-10, 'chain_command_block', false)
    commands[12] == new Command('say outer else', true)
    commands.size() == 13
  }

  @Test
  public void "double nested if im then teil"() {
    given:
    String programString = """
    if: /outer condition
    then (
      /say outer then1
      if: /middle condition
      then (
        /say middle then1
        if: /inner condition
        then (
          /say inner then
        ) else (
          /say inner else
        )
        /say middle then2
      ) else (
        /say middle else
      )
      /say outer then2
    ) else (
      /say outer else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == newNormalizingCommand()
    commands[2] == new Command('say outer then1', true)
    commands[3] == newTestforSuccessCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('middle condition', true)
    commands[5] == newNormalizingCommand()
    commands[6] == new Command('say middle then1', true)
    commands[7] == newTestforSuccessCommand(-2, 'chain_command_block', true)
    commands[8] == new Command('inner condition', true)
    commands[9] == new Command('say inner then', true)
    commands[10] == newTestforSuccessCommand(-5, 'chain_command_block', true)
    commands[11] == newTestforSuccessCommand(-3, 'chain_command_block', false, true)
    commands[12] == new Command('say inner else', true)
    commands[13] == newTestforSuccessCommand(-8, 'chain_command_block', true)
    commands[14] == new Command('say middle then2', true)
    commands[15] == newTestforSuccessCommand(-14, 'chain_command_block', true)
    commands[16] == newTestforSuccessCommand(-11, 'chain_command_block', false, true)
    commands[17] == new Command('say middle else', true)
    commands[18] == newTestforSuccessCommand(-17, 'chain_command_block', true)
    commands[19] == new Command('say outer then2', true)
    commands[20] == newTestforSuccessCommand(-19, 'chain_command_block', false)
    commands[21] == new Command('say outer else', true)
    commands.size() == 22
  }

  @Test
  public void "double nested if im else teil"() {
    given:
    String programString = """
    if: /outer condition
    then (
      /say outer then
    ) else (
      /say outer else1
      if: /middle condition
      then (
        /say middle then
      ) else (
        /say middle else1
        if: /inner condition
        then (
          /say inner then
        ) else (
          /say inner else
        )
        /say middle else2
      )
      /say outer else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == new Command('say outer then', true)
    commands[2] == newTestforSuccessCommand(-2, 'chain_command_block', false)
    commands[3]== new Command('say outer else1', true)
    commands[4] == newTestforSuccessCommand(-4, 'chain_command_block', false)
    commands[5] == new Command('middle condition', true)
    commands[6] == new Command('say middle then', true)
    commands[7] == newTestforSuccessCommand(-7, 'chain_command_block', false)
    commands[8] == newTestforSuccessCommand(-3, 'chain_command_block', false, true)
    commands[9] == new Command('say middle else1', true)
    commands[10] == newTestforSuccessCommand(-10, 'chain_command_block', false)
    commands[11] == newTestforSuccessCommand(-6, 'chain_command_block', false, true)
    commands[12] == new Command('inner condition', true)
    commands[13] == new Command('say inner then', true)
    commands[14] == newTestforSuccessCommand(-14, 'chain_command_block', false)
    commands[15] == newTestforSuccessCommand(-10, 'chain_command_block', false, true)
    commands[16] == newTestforSuccessCommand(-4, 'chain_command_block', false, true)
    commands[17] == new Command('say inner else', true)
    commands[18] == newTestforSuccessCommand(-18, 'chain_command_block', false)
    commands[19] == newTestforSuccessCommand(-14, 'chain_command_block', false, true)
    commands[20] == new Command('say middle else2', true)
    commands[21] == newTestforSuccessCommand(-21, 'chain_command_block', false)
    commands[22] == new Command('say outer else2', true)
    commands.size() == 23
  }

  @Test
  public void "start in then chain erzeugt include"() {
    given:
    String name = some($Identifier())
    String programString = """
    process ${name} (
      if: /say hi
      then (
        start testProcess
      )
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, MplReference> includeMap = interpreter.references
    List<MplReference> includes = includeMap.get(name)
    includes[0].processName == 'testProcess'
    includes.size() == 1
  }

  @Test
  public void "notify in then chain funktioniert"() {
    given:
    String name = some($Identifier())
    String programString = """
    process ${name} (
      if: /say hi
      then (
        notify
      )
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram project = interpreter.project
    project.exceptions.isEmpty()
  }
}
