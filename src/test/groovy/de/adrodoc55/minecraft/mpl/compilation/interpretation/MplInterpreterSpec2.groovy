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

import org.junit.Test

import spock.lang.Unroll
import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.chain.MplProcess
import de.adrodoc55.minecraft.mpl.commands.Conditional
import de.adrodoc55.minecraft.mpl.commands.Mode
import de.adrodoc55.minecraft.mpl.commands.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.commands.chainparts.MplCommand
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
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 2
    project.exceptions[0].source.token.text == 'project'
    project.exceptions[0].message == "A file may only contain a single project!"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.line == 3
    project.exceptions[1].source.token.text == 'project'
    project.exceptions[1].message == "A file may only contain a single project!"
    project.exceptions.size() == 2
  }

  @Test
  public void "A projekt an processes can be defined in the same file"() {
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
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'orientation'
    project.exceptions[0].message == "A project may only have a single orientation!"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.line == 4
    project.exceptions[1].source.token.text == 'orientation'
    project.exceptions[1].message == "A project may only have a single orientation!"
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
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 2
    script.exceptions[0].source.token.text == 'orientation'
    script.exceptions[0].message == "A script may only have a single orientation!"
    script.exceptions[1].source.file == lastTempFile
    script.exceptions[1].source.token.line == 3
    script.exceptions[1].source.token.text == 'orientation'
    script.exceptions[1].message == "A script may only have a single orientation!"
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
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 2
    project.exceptions[0].source.token.text == id
    project.exceptions[0].message == "Duplicate process ${id}!"
    project.exceptions[1].source.file == lastTempFile
    project.exceptions[1].source.token.line == 6
    project.exceptions[1].source.token.text == id
    project.exceptions[1].message == "Duplicate process ${id}!"
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
    process1.repeat == false
    List<ChainPart> chainParts1 = process1.chainParts
    chainParts1[0] == new MplCommand("say I am a default process")
    chainParts1.size() == 1

    MplProcess process2 = processes.find { it.name == id2 }
    process2.repeat == false
    List<ChainPart> chainParts2 = process2.chainParts
    chainParts2[0] == new MplCommand("say I am an impulse process, wich is actually equivalent to the default")
    chainParts2.size() == 1

    MplProcess process3 = processes.find { it.name == id3 }
    process3.repeat == true
    List<ChainPart> chainParts3 = process3.chainParts
    chainParts3[0] == new MplCommand("say I am a repeating process. I am completely different :)")
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
    program.installation[0] == new MplCommand('say hi')
    program.installation[1] == new MplCommand('say hi2')
    program.installation.size() == 2

    program.uninstallation[0] == new MplCommand('say hi3')
    program.uninstallation[1] == new MplCommand('say hi4')
    program.uninstallation.size() == 2
  }

  @Test
  @Unroll("leading conditional with identifier: '#command'")
  public void "leading conditional with identifier"(String command) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      conditional: ${command} ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'conditional'
    project.exceptions[0].message == "The first part of a chain must be unconditional"
    project.exceptions.size() == 1

    where:
    command << ['start', 'stop', 'waitfor', 'notify', 'intercept', 'breakpoint']
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
  public void "start with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      start ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplStart(identifier)
    process.chainParts.size() == 1
  }

  @Test
  public void "conditional start with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      conditional: start ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplStart(identifier, CONDITIONAL, CHAIN)
    process.chainParts.size() == 2
  }

  @Test
  public void "invert start with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      invert: start ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplStart(identifier, INVERT, CHAIN)
    process.chainParts.size() == 2
  }

  @Test
  @Unroll("start with illegal modifier: '#modifier'")
  public void "start with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${modifier}: start ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == modifier
    project.exceptions[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    project.exceptions.size() == 1

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
  public void "stop with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      stop ${identifier}
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
  public void "conditional stop with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      conditional: stop ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplStop(identifier, CONDITIONAL, CHAIN)
    process.chainParts.size() == 2
  }

  @Test
  public void "invert stop with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      invert: stop ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplStop(identifier, INVERT, CHAIN)
    process.chainParts.size() == 2
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
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'stop'
    project.exceptions[0].message == "An impulse process can't be stopped."
    project.exceptions.size() == 1
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
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == modifier
    project.exceptions[0].message == "Illegal modifier for stop; only unconditional, conditional and invert are permitted"
    project.exceptions.size() == 1

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
  public void "waitfor with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      waitfor ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplWaitfor(identifier)
    process.chainParts.size() == 1
  }

  @Test
  public void "conditional waitfor with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      conditional: waitfor ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplWaitfor(identifier, CONDITIONAL, CHAIN)
    process.chainParts.size() == 2
  }

  @Test
  public void "invert waitfor with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      /say hi
      invert: waitfor ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplCommand('say hi')
    process.chainParts[1] == new MplWaitfor(identifier, INVERT, CHAIN)
    process.chainParts.size() == 2
  }

  @Test
  public void "waitfor notify with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      waitfor notify ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplWaitfor(identifier + MplInterpreter.NOTIFY);
    process.chainParts.size() == 1
  }

  @Test
  public void "waitfor without identifier after start"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      start ${identifier}
      waitfor
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    project.processes.size() == 1
    MplProcess process = project.processes.first()

    process.chainParts[0] == new MplStart(identifier)
    process.chainParts[1] == new MplWaitfor(identifier + MplInterpreter.NOTIFY);
    process.chainParts.size() == 2
  }

  @Test
  public void "waitfor without identifier without start"() {
    given:
    String programString = """
    process main (
      waitfor
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'waitfor'
    project.exceptions[0].message == "Missing identifier; no previous start was found to wait for"
    project.exceptions.size() == 1
  }

  @Test
  @Unroll("waitfor with illegal modifier: '#modifier'")
  public void "waitfor with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${modifier}: waitfor ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project

    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == modifier
    project.exceptions[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    project.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

}
