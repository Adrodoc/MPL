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
package de.adrodoc55.minecraft.mpl.interpretation

import static de.adrodoc55.TestBase.someString
import static de.adrodoc55.minecraft.mpl.MplTestBase.someIdentifier
import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

import spock.lang.Unroll

import com.google.common.collect.ListMultimap

import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess
import de.adrodoc55.minecraft.mpl.chain.CommandChain
import de.adrodoc55.minecraft.mpl.commands.Mode
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InternalCommand
import de.adrodoc55.minecraft.mpl.commands.chainlinks.InvertingCommand
import de.adrodoc55.minecraft.mpl.commands.chainlinks.NormalizingCommand
import de.adrodoc55.minecraft.mpl.commands.chainlinks.ReferencingCommand
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Skip

public class MplInterpreterSpec extends MplSpecBase {

  @Test
  @Unroll("Teste basis Modifier ('#programString')")
  public void "Teste basis Modifier"(String programString, Mode mode, boolean conditional, boolean needsRedstone) {
    given:
    String command = programString.find('/.+$')
    programString = """
    /say hi
    ${programString}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()
    List<Command> commands = script.chain.commands
    commands[0] == new Command('say hi')
    commands[1] == new Command(command, mode, conditional, needsRedstone)
    commands.size() == 2
    where:
    programString                                           | mode          | conditional   | needsRedstone
    '/' + someString()                                      | Mode.CHAIN    | false         | false
    'impulse: /' + someString()                             | Mode.IMPULSE  | false         | true
    'chain: /' + someString()                               | Mode.CHAIN    | false         | false
    'repeat: /' + someString()                              | Mode.REPEAT   | false         | true

    'conditional: /' + someString()                         | Mode.CHAIN    | true          | false
    'impulse, conditional: /' + someString()                | Mode.IMPULSE  | true          | true
    'chain, conditional: /' + someString()                  | Mode.CHAIN    | true          | false
    'repeat, conditional: /' + someString()                 | Mode.REPEAT   | true          | true

    'needs redstone: /' + someString()                      | Mode.CHAIN    | false         | true
    'impulse, needs redstone: /' + someString()             | Mode.IMPULSE  | false         | true
    'chain, needs redstone: /' + someString()               | Mode.CHAIN    | false         | true
    'repeat, needs redstone: /' + someString()              | Mode.REPEAT   | false         | true

    'always active: /' + someString()                       | Mode.CHAIN    | false         | false
    'impulse, always active: /' + someString()              | Mode.IMPULSE  | false         | false
    'chain, always active: /' + someString()                | Mode.CHAIN    | false         | false
    'repeat, always active: /' + someString()               | Mode.REPEAT   | false         | false

    'conditional, needs redstone: /' + someString()         | Mode.CHAIN    | true          | true
    'impulse, conditional, needs redstone: /' + someString()| Mode.IMPULSE  | true          | true
    'chain, conditional, needs redstone: /' + someString()  | Mode.CHAIN    | true          | true
    'repeat, conditional, needs redstone: /' + someString() | Mode.REPEAT   | true          | true

    'conditional, always active: /' + someString()          | Mode.CHAIN    | true          | false
    'impulse, conditional, always active: /' + someString() | Mode.IMPULSE  | true          | false
    'chain, conditional, always active: /' + someString()   | Mode.CHAIN    | true          | false
    'repeat, conditional, always active: /' + someString()  | Mode.REPEAT   | true          | false
  }

  @Test
  @Unroll("Teste invert Modifier ('#programString')")
  public void "Teste invert Modifier"(String programString, Mode mode, boolean needsRedstone) {
    given:
    String command = programString.find('/.+$')
    programString = """
      /say hi
    """ + programString
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new InvertingCommand(Mode.CHAIN)
    commands[2] == new Command(command, mode, true, needsRedstone)
    commands.size() == 3
    where:
    programString                                       | mode          | needsRedstone
    'invert: /' + someString()                          | Mode.CHAIN    | false
    'impulse, invert: /' + someString()                 | Mode.IMPULSE  | true
    'chain, invert: /' + someString()                   | Mode.CHAIN    | false
    'repeat, invert: /' + someString()                  | Mode.REPEAT   | true

    'invert, needs redstone: /' + someString()          | Mode.CHAIN    | true
    'impulse, invert, needs redstone: /' + someString() | Mode.IMPULSE  | true
    'chain, invert, needs redstone: /' + someString()   | Mode.CHAIN    | true
    'repeat, invert, needs redstone: /' + someString()  | Mode.REPEAT   | true

    'invert, always active: /' + someString()           | Mode.CHAIN    | false
    'impulse, invert, always active: /' + someString()  | Mode.IMPULSE  | false
    'chain, invert, always active: /' + someString()    | Mode.CHAIN    | false
    'repeat, invert, always active: /' + someString()   | Mode.REPEAT   | false
  }

  @Test
  @Unroll("Teste invert Modifier beruecksichtigt vorherigen command mode ('#mode')")
  public void "Teste invert Modifier beruecksichtigt vorherigen command mode"(Mode mode) {
    given:
    String command = '/' + someString()
    String lowercaseName = mode.name().toLowerCase()
    String programString = """
      ${lowercaseName}: ${command}
      invert: /say hi
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command(command, mode, false)
    commands[1] == new InvertingCommand(mode)
    commands[2] == new Command("say hi", true)
    commands.size() == 3
    where:
    mode << Mode.values()
  }

  @Test
  public void "invert nach skip wirft exception"() {
    given:
    String testString = """
    /say hi
    skip
    invert: /say inverted
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'invert'
    script.exceptions[0].message == "Invert modifier may not follow a skip!"
    script.exceptions.size() == 1
  }

  @Test
  public void "skip at the start of a repeat process throws exception"() {
    given:
    String testString = """
    repeat process name (
    skip
    )
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'skip'
    project.exceptions[0].message == "Skip may not be the first command of a repeating process!"
    project.exceptions.size() == 1
  }

  @Test
  public void "am anfang eines repeating prozesses referenziert ein invert modifier einen repeating command block"() {
    given:
    String name = someIdentifier()
    String testString = """
    repeat process ${name} (
      /say hi
      invert: /say inverted
    )
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new InvertingCommand(Mode.REPEAT)
    commands[2] == new Command("say inverted", true)
    commands.size() == 3
  }

  @Test
  @Unroll("start generiert die richtigen Commandos ('#programString')")
  public void "start generiert die richtigen Commandos"(String programString, boolean conditional) {
    given:
    String identifier = programString.find('(?<=start ).+$')
    programString = """
    /say hi
    ${programString}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new Command("execute @e[name=${identifier}] ~ ~ ~ setblock ~ ~ ~ redstone_block", conditional)
    commands.size() == 2
    where:
    programString                               | conditional
    'start ' + someIdentifier()                 | false
    'conditional: start ' + someIdentifier()    | true
  }

  @Test
  public void "start kann in install verwendet werden"() {
    given:
    String id = someIdentifier()
    String programString = """
    install (
    start ${id}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    List<ChainPart> installation = script.installation
    installation[0] == new Command("execute @e[name=${id}] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    installation.size() == 1
  }

  @Test
  public void "start kann in processen verwendet werden"() {
    given:
    String name = someIdentifier()
    String id = someIdentifier()
    String programString = """
    process ${name} (
    start ${id}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new InternalCommand('setblock \${this - 1} stone', Mode.IMPULSE, false)
    commands[1] == new Command("execute @e[name=${id}] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands.size() == 2
  }

  @Test
  public void "repeating stop generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("execute @e[name=${name}] ~ ~ ~ setblock ~ ~ ~ stone", Mode.REPEAT, false)
    commands.size() == 1
  }

  @Test
  public void "repeating conditional stop generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    /say hi
    conditional: stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new Command("execute @e[name=${name}] ~ ~ ~ setblock ~ ~ ~ stone", true)
    commands.size() == 2
  }

  @Test
  public void "impulse stop wirft exception"() {
    given:
    String name = someIdentifier()
    String programString = """
    impulse process ${name} (
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
    project.exceptions[0].message == 'Can only stop repeating processes.'
    project.exceptions.size() == 1
  }

  @Test
  public void "stop mit identifier generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String sid = someIdentifier()
    String programString = """
    impulse process ${name} (
    stop ${sid}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("execute @e[name=${sid}] ~ ~ ~ setblock ~ ~ ~ stone")
    commands.size() == 2
  }

  @Test
  public void "notify generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name} (
    notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new InternalCommand("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]")
    commands.size() == 3
  }

  @Test
  public void "conditional: notify generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name} (
    /say hi
    conditional: notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("say hi")
    commands[2] == new InternalCommand("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block", true)
    commands[3] == new Command("kill @e[name=${name}_NOTIFY]", true)
    commands.size() == 4
  }

  @Test
  public void "invert: notify generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name} (
    /say hi
    invert: notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("say hi")
    commands[2] == new InvertingCommand(Mode.CHAIN)
    commands[3] == new InternalCommand("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block", true)
    commands[4] == new Command("kill @e[name=${name}_NOTIFY]", true)
    commands.size() == 5
  }

  @Test
  public void "notify wirft außerhalb eines Prozesses eine CompilerException"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 2
    script.exceptions[0].source.token.text == 'notify'
    script.exceptions[0].message == 'Encountered notify outside of a process context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "In einem repeat Prozess stoppt notify den prozess nicht"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    /say hi
    notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new InternalCommand("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]")
    commands.size() == 3
  }

  @Test
  public void "In einem repeat Prozess stoppt notify den Prozess nicht, ist aber conditional"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    /say hi
    conditional: notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new InternalCommand("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block", true)
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]", true)
    commands.size() == 3
  }

  @Test
  public void "breakpoint in repeating Prozess wirft Exception"() {
    given:
    String name = someIdentifier()
    String identifier = someIdentifier()
    String programString = """
    repeat process ${name} (
    breakpoint
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 4
    project.exceptions[0].source.token.text == 'waitfor'
    project.exceptions[0].message == 'Encountered breakpoint in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "breakpoint in repeating script wirft Exception"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat: /say hi
    breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'waitfor'
    script.exceptions[0].message == 'Encountered breakpoint in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "breakpoint generiert die richtigen Commands"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name} (
      /say hi
      breakpoint
      /say hi2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    Collection<MplProcess> processes = project.processes

    MplProcess process = processes.find { it.name == 'name' }
    List<Command> commands = process.commands
    commands[0] == new Command("say hi")
    commands[1] == new InternalCommand("execute @e[name=breakpoint] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[2] == new InternalCommand("say encountered breakpoint ${lastTempFile.name} : line 3")
    commands[3] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[4] == new Skip(false) // Nicht internal, da breakpoint manuell referenziert werden können soll
    commands[5] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[6] == new Command("say hi2")
    commands.size() == 7

    MplProcess breakpoint = processes.find { it.name == 'breakpoint' }
    List<Command> breakpointCommands = process.commands
    breakpointCommands[0] == new InternalCommand('tellraw @a [{"text":"[tp to breakpoint]","color":"gold","clickEvent":{"action":"run_command","value":"/tp @p @e[name=breakpoint_NOTIFY,c=-1]"}},{"text":"   "},{"text":"[continue program]","color":"gold","clickEvent":{"action":"run_command","value":"/execute @e[name=breakpoint_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block"}}]')
    breakpointCommands[1] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:breakpoint_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    breakpointCommands[2] == new Skip(true)
    breakpointCommands[3] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    breakpointCommands[4] == new InternalCommand("kill @e[name=breakpoint_NOTIFY]")
    breakpointCommands.size() == 5

    processes.size() == 2
  }

  @Test
  public void "waitfor in repeating Prozess wirft Exception"() {
    given:
    String name = someIdentifier()
    String identifier = someIdentifier()
    String programString = """
    repeat process ${name} (
    start ${identifier}
    waitfor
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 4
    project.exceptions[0].source.token.text == 'waitfor'
    project.exceptions[0].message == 'Encountered waitfor in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "waitfor in repeating script wirft Exception"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat: /say hi
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'waitfor'
    script.exceptions[0].message == 'Encountered waitfor in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "waitfor ohne Identifier bezieht sich auf Notify des letzten Starts"() {
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

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("execute @e[name=${identifier}] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[1] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[2] == new Skip(false) // Nicht internal, da waitfor manuell referenziert werden können soll
    commands[3] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands.size() == 4
  }

  @Test
  public void "waitfor ohne Identifier ohne vorheriges Start wirft CompilerException"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 2
    script.exceptions[0].source.token.text == 'waitfor'
    script.exceptions[0].message == 'Missing Identifier. No previous start was found to wait for.'
    script.exceptions.size() == 1
  }

  @Test
  public void "waitfor mit identifier generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[1] == new Skip(false) // Nicht internal, da waitfor manuell referenziert werden können soll
    commands[2] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands.size() == 3
  }

  @Test
  public void "conditional: waitfor generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    conditional: waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new InternalCommand("summon ArmorStand \${this + 3} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true)
    commands[2] == new InternalCommand("blockdata \${this - 1} {SuccessCount:1}")
    commands[3] == new InternalCommand("setblock \${this + 1} redstone_block", true)
    commands[4] == new Skip(false) // Nicht internal, da waitfor manuell referenziert werden können soll
    commands[5] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands.size() == 6
  }

  @Test
  public void "waitfor notify mit identifier generiert die richtigen Commandos"() {
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

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[1] == new Skip(false) // Nicht internal, da waitfor manuell referenziert werden können soll
    commands[2] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands.size() == 3
  }

  @Test
  public void "conditional: waitfor notify generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    conditional: waitfor notify ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new InternalCommand("summon ArmorStand \${this + 3} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true)
    commands[2] == new InternalCommand("blockdata \${this - 1} {SuccessCount:1}")
    commands[3] == new InternalCommand("setblock \${this + 1} redstone_block", true)
    commands[4] == new Skip(false) // Nicht internal, da waitfor manuell referenziert werden können soll
    commands[5] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands.size() == 6
  }

  @Test
  public void "intercept in repeating Prozess wirft Exception"() {
    given:
    String name = someIdentifier()
    String identifier = someIdentifier()
    String programString = """
    repeat process ${name} (
    intercept ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == 'intercept'
    project.exceptions[0].message == 'Encountered intercept in repeating context.'
    project.exceptions.size() == 1
  }

  @Test
  public void "intercept in repeating script wirft Exception"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat: /say hi
    intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 3
    script.exceptions[0].source.token.text == 'intercept'
    script.exceptions[0].message == 'Encountered intercept in repeating context.'
    script.exceptions.size() == 1
  }

  @Test
  public void "intercept generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = 'intercept ' + identifier
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new InternalCommand("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[1] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[2] == new Skip(false) // Nicht internal, da intercept manuell referenziert werden können soll
    commands[3] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[4] == new InternalCommand("kill @e[name=${identifier},r=2]")
    commands[5] == new InternalCommand("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
    commands.size() == 6
  }

  @Test
  public void "conditional: intercept generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = """
      /say hi
      conditional: intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new InvertingCommand(Mode.CHAIN)
    commands[2] == new InternalCommand("setblock \${this + 3} redstone_block", true)
    commands[3] == new InternalCommand("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[4] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[5] == new Skip(false) // Nicht internal, da intercept manuell referenziert werden können soll
    commands[6] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[7] == new InternalCommand("kill @e[name=${identifier},r=2]")
    commands[8] == new InternalCommand("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
    commands.size() == 9
  }

  @Test
  public void "invert: intercept generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    invert: intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("say hi")
    commands[1] == new InternalCommand("setblock \${this + 3} redstone_block", true)
    commands[2] == new InternalCommand("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[3] == new InternalCommand("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[4] == new Skip(false) // Nicht internal, da intercept manuell referenziert werden können soll
    commands[5] == new InternalCommand("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[6] == new InternalCommand("kill @e[name=${identifier},r=2]")
    commands[7] == new InternalCommand("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
    commands.size() == 8
  }

  @Test
  public void "if mit nur einem then wird zu conditional"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command("testfor @p")
    commands[1] == new Command("say then", true)
    commands.size() == 2
  }

  @Test
  public void "if erzeugt Exception, wenn erster then conditional"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      conditional: /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'conditional'
    script.exceptions[0].message == "The first command of a chain must be unconditional!"
    script.exceptions.size() == 1
  }

  @Test
  public void "if erzeugt Exception, wenn erster invert"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      invert: /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 4
    script.exceptions[0].source.token.text == 'invert'
    script.exceptions[0].message == "The first command of a chain must be unconditional!"
    script.exceptions.size() == 1
  }

  @Test
  public void "if erzeugt Exception, wenn erster else conditional"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    ) else (
      conditional: /say else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 6
    script.exceptions[0].source.token.text == 'conditional'
    script.exceptions[0].message == "The first command of a chain must be unconditional!"
    script.exceptions.size() == 1
  }

  @Test
  public void "if erzeugt Exception, wenn erster else invert"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    ) else (
      invert: /say else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions[0].source.file == lastTempFile
    script.exceptions[0].source.token.line == 6
    script.exceptions[0].source.token.text == 'invert'
    script.exceptions[0].message == "The first command of a chain must be unconditional!"
    script.exceptions.size() == 1
  }

  @Test
  public void "if not mit nur einem then wird zu invert"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[2] == new Command('say then', true)
    commands.size() == 3
  }

  @Test
  public void "if mit then und else wird zu conditional und invert"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    ) else (
      /say else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new ReferencingCommand(-2, 'chain_command_block', false)
    commands[3] == new Command('say else', true)
    commands.size() == 4
  }

  @Test
  public void "if mit meheren then erster conditional, andere SuccessCount:1"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then1
      /say then2
      /say then3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say then1', true)
    commands[3] == new ReferencingCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('say then2', true)
    commands[5] == new ReferencingCommand(-4, 'chain_command_block', true)
    commands[6] == new Command('say then3', true)
    commands.size() == 7
  }

  @Test
  public void "if not mit meheren then alle SuccessCount:0"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then1
      /say then2
      /say then3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[2] == new Command('say then1', true)
    commands[3] == new ReferencingCommand(-3, 'chain_command_block', false)
    commands[4] == new Command('say then2', true)
    commands[5] == new ReferencingCommand(-5, 'chain_command_block', false)
    commands[6] == new Command('say then3', true)
    commands.size() == 7
  }

  @Test
  public void "if mit meheren then und else"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then1
      /say then2
      /say then3
    ) else (
      /say else1
      /say else2
      /say else3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say then1', true)
    commands[3] == new ReferencingCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('say then2', true)
    commands[5] == new ReferencingCommand(-4, 'chain_command_block', true)
    commands[6] == new Command('say then3', true)
    commands[7] == new ReferencingCommand(-6, 'chain_command_block', false)
    commands[8] == new Command('say else1', true)
    commands[9] == new ReferencingCommand(-8, 'chain_command_block', false)
    commands[10] == new Command('say else2', true)
    commands[11] == new ReferencingCommand(-10, 'chain_command_block', false)
    commands[12] == new Command('say else3', true)
    commands.size() == 13
  }

  @Test
  public void "if not mit meheren then und else"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then1
      /say then2
      /say then3
    ) else (
      /say else1
      /say else2
      /say else3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[3] == new Command('say then1', true)
    commands[4] == new ReferencingCommand(-3, 'chain_command_block', false)
    commands[5] == new Command('say then2', true)
    commands[6] == new ReferencingCommand(-5, 'chain_command_block', false)
    commands[7] == new Command('say then3', true)
    commands[8] == new ReferencingCommand(-7, 'chain_command_block', true)
    commands[9] == new Command('say else1', true)
    commands[10] == new ReferencingCommand(-9, 'chain_command_block', true)
    commands[11] == new Command('say else2', true)
    commands[12] == new ReferencingCommand(-11, 'chain_command_block', true)
    commands[13] == new Command('say else3', true)
    commands.size() == 14
  }

  @Test
  public void "if mit conditional im then"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then1
      conditional: /say then2
      /say then3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
    commands[4] == new ReferencingCommand(-3, 'chain_command_block', true)
    commands[5] == new Command('say then3', true)
    commands.size() == 6
  }

  @Test
  public void "if mit conditional im then (kein normalizer)"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then1
      conditional: /say then2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then1', true)
    commands[2] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
    commands.size() == 3
  }

  @Test
  public void "if mit invert im then"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then1
      invert: /say then2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say then1', true)
    commands[3] == new InvertingCommand(Mode.CHAIN)
    commands[4] == new ReferencingCommand(-3, 'chain_command_block', true, true)
    commands[5] == new Command('say then2', true)
    commands.size() == 6
  }

  @Test
  public void "if mit conditional im else"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    ) else (
      /say else1
      conditional: /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new ReferencingCommand(-2, 'chain_command_block', false)
    commands[3] == new Command('say else1', true)
    commands[4] == new Command('say else2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung true, muss auch mein vorgänger false sein.
    commands.size() == 5
  }

  @Test
  public void "if mit invert im else"() {
    given:
    String programString = """
    if: /testfor @p
    then (
      /say then
    ) else (
      /say else1
      invert: /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new ReferencingCommand(-2, 'chain_command_block', false)
    commands[3] == new Command('say else1', true)
    commands[4] == new InvertingCommand(Mode.CHAIN)
    commands[5] == new ReferencingCommand(-5, 'chain_command_block', false, true)
    commands[6] == new Command('say else2', true)
    commands.size() == 7
  }

  @Test
  public void "if not mit conditional im then"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then1
      conditional: /say then2
      /say then3
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
    commands[4] == new ReferencingCommand(-4, 'chain_command_block', false)
    commands[5] == new Command('say then3', true)
    commands.size() == 6
  }

  @Test
  public void "if not mit invert im then"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then1
      invert: /say then2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[2] == new Command('say then1', true)
    commands[3] == new InvertingCommand(Mode.CHAIN)
    commands[4] == new ReferencingCommand(-4, 'chain_command_block', false, true)
    commands[5] == new Command('say then2', true)
    commands.size() == 6
  }

  @Test
  public void "if not mit conditional im else"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then
    ) else (
      /say else1
      conditional: /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[3] == new Command('say then', true)
    commands[4] == new ReferencingCommand(-3, 'chain_command_block', true)
    commands[5] == new Command('say else1', true)
    commands[6] == new Command('say else2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung true, muss auch mein vorgänger false sein.
    commands.size() == 7
  }

  @Test
  public void "if not mit invert im else"() {
    given:
    String programString = """
    if not: /testfor @p
    then (
      /say then
    ) else (
      /say else1
      invert: /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new ReferencingCommand(-1, 'chain_command_block', false)
    commands[3] == new Command('say then', true)
    commands[4] == new ReferencingCommand(-3, 'chain_command_block', true)
    commands[5] == new Command('say else1', true)
    commands[6] == new InvertingCommand(Mode.CHAIN)
    commands[7] == new ReferencingCommand(-6, 'chain_command_block', true, true)
    commands[8] == new Command('say else2', true)
    commands.size() == 9
  }

  @Test
  public void "if am anfang eines repeating prozesses, ohne normalizer: referenzen referensieren den repeat mode"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
      if: /testfor @p
      then (
        /say then
      ) else (
        /say else
      )
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command('testfor @p', Mode.REPEAT, false)
    commands[1] == new Command('say then', true)
    commands[2] == new ReferencingCommand(-2, 'repeating_command_block', false)
    commands[3] == new Command('say else', true)
    commands.size() == 4
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
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say outer then1', true)
    commands[3] == new ReferencingCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('inner condition', true)
    commands[5] == new Command('say inner then', true)
    commands[6] == new ReferencingCommand(-5, 'chain_command_block', true)
    commands[7] == new ReferencingCommand(-3, 'chain_command_block', false, true)
    commands[8] == new Command('say inner else', true)
    commands[9] == new ReferencingCommand(-8, 'chain_command_block', true)
    commands[10] == new Command('say outer then2', true)
    commands[11] == new ReferencingCommand(-10, 'chain_command_block', false)
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
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('say outer then1', true)
    commands[3] == new ReferencingCommand(-2, 'chain_command_block', true)
    commands[4] == new Command('middle condition', true)
    commands[5] == new NormalizingCommand()
    commands[6] == new Command('say middle then1', true)
    commands[7] == new ReferencingCommand(-2, 'chain_command_block', true)
    commands[8] == new Command('inner condition', true)
    commands[9] == new Command('say inner then', true)
    commands[10] == new ReferencingCommand(-5, 'chain_command_block', true)
    commands[11] == new ReferencingCommand(-3, 'chain_command_block', false, true)
    commands[12] == new Command('say inner else', true)
    commands[13] == new ReferencingCommand(-8, 'chain_command_block', true)
    commands[14] == new Command('say middle then2', true)
    commands[15] == new ReferencingCommand(-14, 'chain_command_block', true)
    commands[16] == new ReferencingCommand(-11, 'chain_command_block', false, true)
    commands[17] == new Command('say middle else', true)
    commands[18] == new ReferencingCommand(-17, 'chain_command_block', true)
    commands[19] == new Command('say outer then2', true)
    commands[20] == new ReferencingCommand(-19, 'chain_command_block', false)
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
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    CommandChain chain = script.chain
    List<Command> commands = chain.commands
    commands[0] == new Command('outer condition')
    commands[1] == new Command('say outer then', true)
    commands[2] == new ReferencingCommand(-2, 'chain_command_block', false)
    commands[3]== new Command('say outer else1', true)
    commands[4] == new ReferencingCommand(-4, 'chain_command_block', false)
    commands[5] == new Command('middle condition', true)
    commands[6] == new Command('say middle then', true)
    commands[7] == new ReferencingCommand(-7, 'chain_command_block', false)
    commands[8] == new ReferencingCommand(-3, 'chain_command_block', false, true)
    commands[9] == new Command('say middle else1', true)
    commands[10] == new ReferencingCommand(-10, 'chain_command_block', false)
    commands[11] == new ReferencingCommand(-6, 'chain_command_block', false, true)
    commands[12] == new Command('inner condition', true)
    commands[13] == new Command('say inner then', true)
    commands[14] == new ReferencingCommand(-14, 'chain_command_block', false)
    commands[15] == new ReferencingCommand(-10, 'chain_command_block', false, true)
    commands[16] == new ReferencingCommand(-4, 'chain_command_block', false, true)
    commands[17] == new Command('say inner else', true)
    commands[18] == new ReferencingCommand(-18, 'chain_command_block', false)
    commands[19] == new ReferencingCommand(-14, 'chain_command_block', false, true)
    commands[20] == new Command('say middle else2', true)
    commands[21] == new ReferencingCommand(-21, 'chain_command_block', false)
    commands[22] == new Command('say outer else2', true)
    commands.size() == 23
  }

  @Test
  public void "start in then chain erzeugt include"() {
    given:
    String name = someIdentifier()
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
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    List<Include> includes = includeMap.get(name)
    includes[0].processName == 'testProcess'
    includes.size() == 1
  }

  @Test
  public void "notify in then chain funktioniert"() {
    given:
    String name = someIdentifier()
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
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
  }

  @Test
  void "an Interpreter will always include subfiles of it's parent directory, including itself"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.mpl')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)
    expect:
    interpreter.imports.containsAll([programFile, neighbourFile])
    interpreter.imports.size() == 2
  }

  @Test
  void "addFileImport will add a file that is not in the same folder"() {
    given:
    File otherFile = newTempFile()
    File programFile = new File(otherFile.parentFile, 'folder/neighbour.txt')
    programFile.parentFile.mkdirs()
    programFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)
    when:
    interpreter.addFileImport(null, otherFile)
    then:
    interpreter.imports.containsAll([programFile, otherFile])
    interpreter.imports.size() == 2
  }

  @Test
  void "importing the same file twice will add an exception"() {
    given:
    String programString = """
    import "newFolder/newFile.txt"
    import "newFolder/newFile.txt"
    """
    File newFolder = new File(tempFolder.root, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.txt")
    newFile.createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions[0].source.file == lastTempFile
    project.exceptions[0].source.token.line == 3
    project.exceptions[0].source.token.text == '"newFolder/newFile.txt"'
    project.exceptions[0].message == 'Duplicate import.'
    project.exceptions.size() == 1
  }

  @Test
  public void "Ein Projekt mit Includes erzeugt Includes"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    project ${id1} (
    include "datei1.mpl"
    include "ordner2"
    )
    """
    File folder = tempFolder.root
    new File(folder, 'datei1.mpl').createNewFile()
    new File(folder, 'ordner2').mkdirs()
    new File(folder, 'ordner2/datei4.mpl').createNewFile()
    new File(folder, 'ordner2/datei5.txt').createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString)
    File file = lastTempFile
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    File parent = file.parentFile

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 2
    List<Include> includes = includeMap.get(null); // null indicates that the whole file should be included
    includes[0].files.containsAll([new File(parent, "datei1.mpl")])
    includes[0].files.size() == 1
    includes[0].processName == null
    includes[1].files.containsAll([new File(parent, "ordner2/datei4.mpl")])
    includes[1].files.size() == 1
    includes[1].processName == null
    includes.size() == 2
  }

  @Test
  public void "Ein Script erzeugt niemals ein include"() {
    given:
    String programString = """
    start other
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplScript script = interpreter.script
    script.exceptions.isEmpty()

    interpreter.includes.isEmpty()
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Includes
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "Eine Prozessdatei, die versucht einen eigenen Prozess zu starten erzeugt auch ein Include. Prozess definition erst nachher"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
    /say I am a process
    conditional: start ${id2}
    )

    process ${id2} (
    /say I am the second process
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id2
    includes.size() == 1
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Includes
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "Eine Prozessdatei, die versucht einen eigenen Prozess zu starten erzeugt auch ein Include. Prozess definition bereits vorher"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
    /say I am a process
    )

    process ${id2} (
    /say I am the second process
    conditional: start ${id1}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id2);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id1
    includes.size() == 1
  }

  @Test
  public void "Eine Prozessdatei, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
    /say I am a process
    conditional: start ${id2}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id2
    includes.size() == 1
  }

  @Test
  public void "Eine Prozessdatei mit Dir import, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFolder"
    process ${id1} (
    /say I am a process
    conditional: start ${id2}
    )
    """
    File file = newTempFile()
    File newFolder = new File(file.parentFile, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.mpl")
    newFile.createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString, file)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].files.size() == 2
    includes[0].processName == id2
    includes.size() == 1
  }

  @Test
  public void "Eine Prozessdatei mit File import, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFolder/newFile"
    process ${id1} (
    /say I am a process
    conditional: start ${id2}
    )
    """
    File file = newTempFile()
    File newFile = new File(file.parentFile, "newFolder/newFile")
    newFile.parentFile.mkdirs()
    newFile.createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString, file)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].files.size() == 2
    includes[0].processName == id2
    includes.size() == 1
  }

}
