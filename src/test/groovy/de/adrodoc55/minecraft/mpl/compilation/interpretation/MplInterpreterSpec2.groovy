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
package de.adrodoc55.minecraft.mpl.compilation.interpretation

import static de.adrodoc55.minecraft.mpl.MplTestBase.someIdentifier
import static de.adrodoc55.minecraft.mpl.commands.Conditional.*
import static de.adrodoc55.minecraft.mpl.commands.Mode.*
import static de.adrodoc55.minecraft.mpl.commands.chainparts.MplNotify.NOTIFY

import org.junit.Test

import spock.lang.Unroll
import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.chain.MplProcess
import de.adrodoc55.minecraft.mpl.commands.Conditional
import de.adrodoc55.minecraft.mpl.commands.Mode
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplBreakpoint
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplCommand
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplIf
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplIntercept
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplNotify
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplStart
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplStop
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplWaitfor
import de.adrodoc55.minecraft.mpl.program.MplProgram
import de.adrodoc55.minecraft.mpl.program.MplProject
import de.adrodoc55.minecraft.mpl.program.MplScript

class MplInterpreterSpec2 extends MplSpecBase {

  static List commandOnlyModifier = ['impulse', 'chain', 'repeat', 'always active', 'needs redstone']

  @Test
  public void "Each file can only define one project"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    project ${id1} ()
    project ${id2} ()
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].message == "A file can only contain a single project"
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.text == 'project'
    project.exceptions[0].source.token.line == 2
    project.exceptions[1].message == "A file can only contain a single project"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.text == 'project'
    project.exceptions[1].source.token.line == 3
    project.exceptions.size() == 2
  }

  @Test
  public void "A project an processes can be defined in the same file"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    project ${id1} ()
    process ${id2} ()
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
  }

  @Test
  public void "Each project can only have a single orientation"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    project ${id1} (
      orientation "zxy"
      orientation "z-xy"
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].message == "A project can only have a single orientation"
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.text == 'orientation'
    project.exceptions[0].source.token.line == 3
    project.exceptions[1].message == "A project can only have a single orientation"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.text == 'orientation'
    project.exceptions[1].source.token.line == 4
    project.exceptions.size() == 2
  }

  @Test
  public void "Each script can only have a single orientation"() {
    given:
    String programString = """
    orientation "zxy"
    orientation "z-xy"
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].message == "A script can only have a single orientation"
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.text == 'orientation'
    script.exceptions[0].source.token.line == 2
    script.exceptions[1].message == "A script can only have a single orientation"
    script.exceptions[1].source.file == lastTempFile
    script.exceptions[1].source.token.text == 'orientation'
    script.exceptions[1].source.token.line == 3
    script.exceptions.size() == 2
  }

  @Test
  public void "A file may not contain duplicate processes"() {
    given:
    String id = someIdentifier()
    String programString = """
    process ${id} (
    /say I am a process
    )

    process ${id} (
    /say I am the same process
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].message == "Duplicate process ${id}"
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.text == id
    project.exceptions[0].source.token.line == 2
    project.exceptions[1].message == "Duplicate process ${id}"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.text == id
    project.exceptions[1].source.token.line == 6
    project.exceptions.size() == 2
  }

  @Test
  public void "A file may contain multiple processes"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    String programString = """
    process ${id1} (
    /say I am a default process
    )
    impulse process ${id2} (
    /say I am an impulse process, wich is actually equivalent to the default
    )
    repeat process ${id3} (
    /say I am a repeating process. I am completely different :)
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 3

    MplProcess process1 = processes.find { it.name == id1 }
    process1.repeating == false
    List<ChainPart> chainParts1 = process1.chainParts
    chainParts1[0] == new MplCommand("/say I am a default process")
    chainParts1.size() == 1

    MplProcess process2 = processes.find { it.name == id2 }
    process2.repeating == false
    List<ChainPart> chainParts2 = process2.chainParts
    chainParts2[0] == new MplCommand("/say I am an impulse process, wich is actually equivalent to the default")
    chainParts2.size() == 1

    MplProcess process3 = processes.find { it.name == id3 }
    process3.repeating == true
    List<ChainPart> chainParts3 = process3.chainParts
    chainParts3[0] == new MplCommand("/say I am a repeating process. I am completely different :)")
    chainParts3.size() == 1
  }

  @Test
  public void "Multiple install/uninstall blocks are concatenated"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    install (
      /say hi
    )

    install (
      /say hi2
    )

    uninstall (
      /say hi3
    )

    uninstall (
      /say hi4
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.installation[0] == new MplCommand('/say hi')
    program.installation[1] == new MplCommand('/say hi2')
    program.installation.size() == 2

    program.uninstallation[0] == new MplCommand('/say hi3')
    program.uninstallation[1] == new MplCommand('/say hi4')
    program.uninstallation.size() == 2
  }

  @Test
  @Unroll("leading #conditional #command with identifier in script")
  public void "leading conditional/invert with identifier in script"(String conditional, String command) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${conditional}: ${command} ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'intercept']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command with identifier in process")
  public void "leading conditional/invert with identifier in process"(String conditional, String command) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${conditional}: ${command} ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'intercept']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command without identifier in script")
  public void "leading conditional/invert without identifier in script"(String conditional, String command) {
    given:
    String programString = """
    ${conditional}: ${command}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['breakpoint']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command without identifier in process")
  public void "leading conditional/invert without identifier in process"(String conditional, String command) {
    given:
    String programString = """
    process main (
      ${conditional}: ${command}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['notify', 'breakpoint']].combinations()*.flatten()
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____   _                _
  //   / ___| | |_  __ _  _ __ | |_
  //   \___ \ | __|/ _` || '__|| __|
  //    ___) || |_| (_| || |   | |_
  //   |____/  \__|\__,_||_|    \__|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier start with identifier")
  public void "start with identifier"(String modifier, Conditional conditional, Mode previous) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplCommand('/say hi')
    script.chainParts[1] == new MplStart(identifier, conditional, previous)
    script.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  public void "start with identifier in script"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplStart(identifier)
    script.chainParts.size() == 1
  }

  @Test
  @Unroll("start with illegal modifier: '#modifier'")
  public void "start with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____   _
  //   / ___| | |_  ___   _ __
  //   \___ \ | __|/ _ \ | '_ \
  //    ___) || |_| (_) || |_) |
  //   |____/  \__|\___/ | .__/
  //                     |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier stop with identifier")
  public void "stop with identifier"(String modifier, Conditional conditional, Mode previous) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} stop ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplCommand('/say hi')
    script.chainParts[1] == new MplStop(identifier, conditional, previous)
    script.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  public void "stop without identifier in script"() {
    given:
    String programString = """
    stop
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Missing identifier"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'stop'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  public void "stop without identifier in repeat process"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat process ${identifier} (
      stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplStop(identifier)
    process.chainParts.size() == 1
  }

  @Test
  public void "stop without identifier in impulse process"() {
    given:
    String programString = """
    impulse process main (
      stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "An impulse process cannot be stopped"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'stop'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("stop with illegal modifier: '#modifier'")
  public void "stop with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${modifier}: stop ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for stop; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //   __        __      _  _     __
  //   \ \      / /__ _ (_)| |_  / _|  ___   _ __
  //    \ \ /\ / // _` || || __|| |_  / _ \ | '__|
  //     \ V  V /| (_| || || |_ |  _|| (_) || |
  //      \_/\_/  \__,_||_| \__||_|   \___/ |_|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier waitfor with identifier")
  public void "waitfor with identifier"(String modifier, Conditional conditional, Mode previous) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplCommand('/say hi')
    script.chainParts[1] == new MplWaitfor(identifier, conditional, previous)
    script.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  public void "waitfor notify with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    waitfor notify ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplWaitfor(identifier + NOTIFY);
    script.chainParts.size() == 1
  }

  @Test
  public void "waitfor without identifier after start"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplStart(identifier)
    script.chainParts[1] == new MplWaitfor(identifier + NOTIFY);
    script.chainParts.size() == 2
  }

  @Test
  public void "waitfor without identifier without start"() {
    given:
    String programString = """
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Missing identifier; no previous start was found to wait for"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'waitfor'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("waitfor with illegal modifier: '#modifier'")
  public void "waitfor with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for waitfor; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    _   _         _    _   __
  //   | \ | |  ___  | |_ (_) / _| _   _
  //   |  \| | / _ \ | __|| || |_ | | | |
  //   | |\  || (_) || |_ | ||  _|| |_| |
  //   |_| \_| \___/  \__||_||_|   \__, |
  //                               |___/
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier notify in process")
  public void "notify in process"(String modifier, Conditional conditional, Mode previous) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process ${identifier} (
      /say hi
      ${modifier} notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplNotify(identifier, conditional, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  public void "notify in script"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Notify can only be used in a process"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'notify'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("notify with illegal modifier: '#modifier'")
  public void "notify with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process ${identifier} (
      ${modifier}: notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for notify; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ___         _                                _
  //   |_ _| _ __  | |_  ___  _ __  ___  ___  _ __  | |_
  //    | | | '_ \ | __|/ _ \| '__|/ __|/ _ \| '_ \ | __|
  //    | | | | | || |_|  __/| |  | (__|  __/| |_) || |_
  //   |___||_| |_| \__|\___||_|   \___|\___|| .__/  \__|
  //                                         |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier intercept with identifier")
  public void "intercept with identifier"(String modifier, Conditional conditional, Mode previous) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplCommand('/say hi')
    script.chainParts[1] == new MplIntercept(identifier, conditional, previous)
    script.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  @Unroll("intercept with illegal modifier: '#modifier'")
  public void "intercept with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for intercept; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____                     _                   _         _
  //   | __ )  _ __  ___   __ _ | | __ _ __    ___  (_) _ __  | |_
  //   |  _ \ | '__|/ _ \ / _` || |/ /| '_ \  / _ \ | || '_ \ | __|
  //   | |_) || |  |  __/| (_| ||   < | |_) || (_) || || | | || |_
  //   |____/ |_|   \___| \__,_||_|\_\| .__/  \___/ |_||_| |_| \__|
  //                                  |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier breakpoint")
  public void "breakpoint"(String modifier, Conditional conditional, Mode previous) {
    given:
    String programString = """
    /say hi
    ${modifier} breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplCommand('/say hi')
    script.chainParts[1] == new MplBreakpoint("${lastTempFile.name} : line 3" , conditional, previous)
    script.chainParts.size() == 2
    where:
    modifier        | conditional   | previous
    ''              | UNCONDITIONAL | null
    'unconditional:'| UNCONDITIONAL | null
    'conditional:'  | CONDITIONAL   | CHAIN
    'invert:'       | INVERT        | CHAIN
  }

  @Test
  @Unroll("breakpoint with illegal modifier: '#modifier'")
  public void "breakpoint with illegal modifier"(String modifier) {
    given:
    String programString = """
    ${modifier}: breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for breakpoint; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ___   __             _____  _                              _____  _
  //   |_ _| / _|           |_   _|| |__    ___  _ __             | ____|| | ___   ___
  //    | | | |_              | |  | '_ \  / _ \| '_ \            |  _|  | |/ __| / _ \
  //    | | |  _|  _  _  _    | |  | | | ||  __/| | | |  _  _  _  | |___ | |\__ \|  __/
  //   |___||_|   (_)(_)(_)   |_|  |_| |_| \___||_| |_| (_)(_)(_) |_____||_||___/ \___|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  public void "if then else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
      /say then1
      /say then2
    ) else (
      /say else1
      /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplIf(false, '/say if')
    script.chainParts.size() == 1

    MplIf mplIf = script.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1')
    mplIf.thenParts[1] == new MplCommand('/say then2')
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1')
    mplIf.elseParts[1] == new MplCommand('/say else2')
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "if not then else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if not: /say if
    then (
      /say then1
      /say then2
    ) else (
      /say else1
      /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplIf(true, '/say if')
    script.chainParts.size() == 1

    MplIf mplIf = script.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1')
    mplIf.thenParts[1] == new MplCommand('/say then2')
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1')
    mplIf.elseParts[1] == new MplCommand('/say else2')
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "nested if"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /outer condition
    then (
      /say outer then1

      if: /inner then condition
      then (
        /say inner then then
      ) else (
        /say inner then else
      )

      /say outer then2
    ) else (
      /say outer else1

      if: /inner else condition
      then (
        /say inner else then
      ) else (
        /say inner else else
      )

      /say outer else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    script.chainParts[0] == new MplIf(false, '/outer condition')
    script.chainParts.size() == 1

    MplIf outerIf = script.chainParts[0]
    outerIf.thenParts[0] == new MplCommand('/say outer then1')
    outerIf.thenParts[1] == new MplIf(false, '/inner then condition')
    outerIf.thenParts[2] == new MplCommand('/say outer then2')
    outerIf.thenParts.size() == 3
    outerIf.elseParts[0] == new MplCommand('/say outer else1')
    outerIf.elseParts[1] == new MplIf(false, '/inner else condition')
    outerIf.elseParts[2] == new MplCommand('/say outer else2')
    outerIf.elseParts.size() == 3

    MplIf innerThenIf = outerIf.thenParts[1]
    innerThenIf.thenParts[0] == new MplCommand('/say inner then then')
    innerThenIf.thenParts.size() == 1
    innerThenIf.elseParts[0] == new MplCommand('/say inner then else')
    innerThenIf.elseParts.size() == 1

    MplIf innerElseIf = outerIf.elseParts[1]
    innerElseIf.thenParts[0] == new MplCommand('/say inner else then')
    innerElseIf.thenParts.size() == 1
    innerElseIf.elseParts[0] == new MplCommand('/say inner else else')
    innerElseIf.elseParts.size() == 1
  }

}
