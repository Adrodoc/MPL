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
package de.adrodoc55.minecraft.mpl.antlr

import static de.adrodoc55.TestBase.someIdentifier
import static de.adrodoc55.TestBase.someString
import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

import spock.lang.Unroll
import de.adrodoc55.minecraft.mpl.Command
import de.adrodoc55.minecraft.mpl.CommandChain
import de.adrodoc55.minecraft.mpl.CompilerException
import de.adrodoc55.minecraft.mpl.MplConverter
import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.Command.Mode
import de.adrodoc55.minecraft.mpl.antlr.commands.InvertingCommand
import de.adrodoc55.minecraft.mpl.antlr.commands.NormalizingCommand

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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command('say hi')
    commands[1] == new Command(command, mode, conditional, needsRedstone)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3

    commands[0] == new Command("say hi")
    commands[1] == new Command("testforblock \${this - 1} chain_command_block -1 {SuccessCount:0}")
    commands[2] == new Command(command, mode, true, needsRedstone)
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
    String blockId = MplConverter.toBlockId(mode)
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3

    commands[0] == new Command(command, mode, false)
    commands[1] == new Command("testforblock \${this - 1} ${blockId} -1 {SuccessCount:0}")
    commands[2] == new Command("say hi", true)
    where:
    mode << Mode.values()
  }

  @Test
  public void "am anfang eines repeating prozesses referenziert ein invert modifier einen repeating command block"() {
    given:
    String testString = """
    repeat process main (
      /say hi
      invert: /say inverted
    )
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new InvertingCommand(Mode.REPEAT)
    commands[2] == new Command("say inverted", true)
  }

  @Test
  public void "invert modifier korrigiert alle inserts"() {
    given:
    String testString = """
    /say \${this + 5}
    /say \${this + 1}
    invert: /say \${this - 1}
    /say \${this - 5}
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 5
    commands[0] == new Command("say \${this + 6}")
    commands[1] == new Command("say \${this + 2}")
    commands[2] == new Command("testforblock \${this - 1} chain_command_block {SuccessCount:0}")
    commands[3] == new Command("say \${this - 2}", true)
    commands[4] == new Command("say \${this - 6}")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("say hi")
    commands[1] == new Command("execute @e[name=${identifier}] ~ ~ ~ setblock ~ ~ ~ redstone_block", conditional)
    where:
    programString                               | conditional
    'start ' + someIdentifier()                 | false
    'conditional: start ' + someIdentifier()    | true
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command("execute @e[name=${name}] ~ ~ ~ setblock ~ ~ ~ stone", Mode.REPEAT, false)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new Command("execute @e[name=${name}] ~ ~ ~ setblock ~ ~ ~ stone", true)
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 3
    ex.token.text == 'stop'
    ex.message == 'Can only stop repeating processes.'
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("execute @e[name=${sid}] ~ ~ ~ setblock ~ ~ ~ stone")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 4
    commands[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("say hi")
    commands[2] == new Command("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block", true)
    commands[3] == new Command("kill @e[name=${name}_NOTIFY]", true)
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 2
    ex.token.text == 'notify'
    ex.message == 'Encountered notify outside of a process context.'
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new Command("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands[1] == new Command("execute @e[name=${name}_NOTIFY] ~ ~ ~ setblock ~ ~ ~ redstone_block", true)
    commands[2] == new Command("kill @e[name=${name}_NOTIFY]", true)
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 4
    ex.token.text == 'waitfor'
    ex.message == 'Encountered waitfor in repeating context.'
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 4
    ex.token.text == 'waitfor'
    ex.message == 'Encountered waitfor in repeating context.'
  }

  @Test
  public void "waitfor ohne Identifier bezieht sich auf das letzte Start"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 4
    commands[0] == new Command("execute @e[name=${identifier}] ~ ~ ~ setblock ~ ~ ~ redstone_block")
    commands[1] == new Command("summon ArmorStand \${this + 1} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[2] == null
    commands[3] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 2
    ex.token.text == 'waitfor'
    ex.message == 'Missing Identifier. No previous start was found to wait for.'
  }

  @Test
  public void "waitfor generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = 'waitfor ' + identifier
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("summon ArmorStand \${this + 1} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[1] == null
    commands[2] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6
    commands[0] == new Command("say hi")
    commands[1] == new Command("summon ArmorStand \${this + 3} {CustomName:${identifier}_NOTIFY,NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true)
    commands[2] == new Command("blockdata \${this - 1} {SuccessCount:1}")
    commands[3] == new Command("setblock \${this + 1} redstone_block", true)
    commands[4] == null
    commands[5] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 3
    ex.token.text == 'intercept'
    ex.message == 'Encountered intercept in repeating context.'
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
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions[0]
    ex.file == lastTempFile
    ex.token.line == 3
    ex.token.text == 'intercept'
    ex.message == 'Encountered intercept in repeating context.'
  }

  @Test
  public void "intercept generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = 'intercept ' + identifier
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6
    commands[0] == new Command("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[1] == new Command("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[2] == null
    commands[3] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[4] == new Command("kill @e[name=${identifier},r=2]")
    commands[5] == new Command("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 9
    commands[0] == new Command("say hi")
    commands[1] == new Command("testforblock \${this - 1} chain_command_block -1 {SuccessCount:0}")
    commands[2] == new Command("setblock \${this + 3} redstone_block", true)
    commands[3] == new Command("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[4] == new Command("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[5] == null
    commands[6] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[7] == new Command("kill @e[name=${identifier},r=2]")
    commands[8] == new Command("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 8
    commands[0] == new Command("say hi")
    commands[1] == new Command("setblock \${this + 3} redstone_block", true)
    commands[2] == new Command("entitydata @e[name=${identifier}] {CustomName:${identifier}_INTERCEPTED}")
    commands[3] == new Command("summon ArmorStand \${this + 1} {CustomName:${identifier},NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[4] == null
    commands[5] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[6] == new Command("kill @e[name=${identifier},r=2]")
    commands[7] == new Command("entitydata @e[name=${identifier}_INTERCEPTED] {CustomName:${identifier}}")
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2

    commands[0] == new Command("testfor @p")
    commands[1] == new Command("say then", true)
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
    List<CompilerException> exceptions = interpreter.exceptions
    exceptions.size() == 1
    CompilerException ex = exceptions.first()
    ex.file == lastTempFile
    ex.token.line == 4
    ex.token.text == 'conditional'
    ex.message == "The first command of a chain can only be unconditional."
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
    List<CompilerException> exceptions = interpreter.exceptions
    exceptions.size() == 1
    CompilerException ex = exceptions.first()
    ex.file == lastTempFile
    ex.token.line == 4
    ex.token.text == 'invert'
    ex.message == "The first command of a chain can only be unconditional."
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
    List<CompilerException> exceptions = interpreter.exceptions
    exceptions.size() == 1
    CompilerException ex = exceptions.first()
    ex.file == lastTempFile
    ex.token.line == 6
    ex.token.text == 'conditional'
    ex.message == "The first command of a chain can only be unconditional."
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
    List<CompilerException> exceptions = interpreter.exceptions
    exceptions.size() == 1
    CompilerException ex = exceptions.first()
    ex.file == lastTempFile
    ex.token.line == 6
    ex.token.text == 'invert'
    ex.message == "The first command of a chain can only be unconditional."
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[2] == new Command('say then', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 4

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say else', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 7

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}')
    commands[4] == new Command('say then2', true)
    commands[5] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}')
    commands[6] == new Command('say then3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 7

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}')
    commands[4] == new Command('say then2', true)
    commands[5] == new Command('testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}')
    commands[6] == new Command('say then3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 13

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}')
    commands[4] == new Command('say then2', true)
    commands[5] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:1}')
    commands[6] == new Command('say then3', true)
    commands[7] == new Command('testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}')
    commands[8] == new Command('say else1', true)
    commands[9] == new Command('testforblock ${this - 8} chain_command_block -1 {SuccessCount:0}')
    commands[10] == new Command('say else2', true)
    commands[11] == new Command('testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}')
    commands[12] == new Command('say else3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 14

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say then1', true)
    commands[4] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}')
    commands[5] == new Command('say then2', true)
    commands[6] == new Command('testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}')
    commands[7] == new Command('say then3', true)
    commands[8] == new Command('testforblock ${this - 7} chain_command_block -1 {SuccessCount:1}')
    commands[9] == new Command('say else1', true)
    commands[10] == new Command('testforblock ${this - 9} chain_command_block -1 {SuccessCount:1}')
    commands[11] == new Command('say else2', true)
    commands[12] == new Command('testforblock ${this - 11} chain_command_block -1 {SuccessCount:1}')
    commands[13] == new Command('say else3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
    commands[4] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}')
    commands[5] == new Command('say then3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then1', true)
    commands[2] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[4] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}', true)
    commands[5] == new Command('say then2', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 5

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say else1', true)
    commands[4] == new Command('say else2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung true, muss auch mein vorgänger false sein.
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 7

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('say then', true)
    commands[2] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say else1', true)
    commands[4] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[5] == new Command('testforblock ${this - 5} chain_command_block -1 {SuccessCount:0}', true)
    commands[6] == new Command('say else2', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('say then2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung false, muss auch mein vorgänger false sein.
    commands[4] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}')
    commands[5] == new Command('say then3', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6

    commands[0] == new Command('testfor @p')
    commands[1] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[2] == new Command('say then1', true)
    commands[3] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[4] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}', true)
    commands[5] == new Command('say then2', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 7

    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say then', true)
    commands[4] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}')
    commands[5] == new Command('say else1', true)
    commands[6] == new Command('say else2', true) // kein test auf if-Bedingung notwendig. Falls if-Bedingung true, muss auch mein vorgänger false sein.
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 9

    commands[0] == new Command('testfor @p')
    commands[1] == new NormalizingCommand()
    commands[2] == new Command('testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say then', true)
    commands[4] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:1}')
    commands[5] == new Command('say else1', true)
    commands[6] == new InvertingCommand(Mode.CHAIN)
    commands[7] == new Command('testforblock ${this - 6} chain_command_block -1 {SuccessCount:1}', true)
    commands[8] == new Command('say else2', true)
  }

  @Test
  public void "if am anfang eines repeating prozesses, ohne normalizer: referenzen referensieren den repeat mode"() {
    given:
    String programString = """
    repeat process main (
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 4

    commands[0] == new Command('testfor @p', Mode.REPEAT, false)
    commands[1] == new Command('say then', true)
    commands[2] == new Command('testforblock ${this - 2} repeating_command_block -1 {SuccessCount:0}')
    commands[3] == new Command('say else', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 13

    commands[0] == new Command('outer condition')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say outer then1', true)
    commands[3] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}')
    commands[4] == new Command('inner condition', true)
    commands[5] == new Command('say inner then', true)
    commands[6] == new Command('testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}')
    commands[7] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}', true)
    commands[8] == new Command('say inner else', true)
    commands[9] == new Command('testforblock ${this - 8} chain_command_block -1 {SuccessCount:1}')
    commands[10] == new Command('say outer then2', true)
    commands[11] == new Command('testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}')
    commands[12] == new Command('say outer else', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 22

    commands[0] == new Command('outer condition')
    commands[1] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[2] == new Command('say outer then1', true)
    commands[3] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}')
    commands[4] == new Command('middle condition', true)
    commands[5] == new Command('testforblock ~ ~ ~ chain_command_block', true)
    commands[6] == new Command('say middle then1', true)
    commands[7] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:1}')
    commands[8] == new Command('inner condition', true)
    commands[9] == new Command('say inner then', true)
    commands[10] == new Command('testforblock ${this - 5} chain_command_block -1 {SuccessCount:1}')
    commands[11] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}', true)
    commands[12] == new Command('say inner else', true)
    commands[13] == new Command('testforblock ${this - 8} chain_command_block -1 {SuccessCount:1}')
    commands[14] == new Command('say middle then2', true)
    commands[15] == new Command('testforblock ${this - 14} chain_command_block -1 {SuccessCount:1}')
    commands[16] == new Command('testforblock ${this - 11} chain_command_block -1 {SuccessCount:0}', true)
    commands[17] == new Command('say middle else', true)
    commands[18] == new Command('testforblock ${this - 17} chain_command_block -1 {SuccessCount:1}')
    commands[19] == new Command('say outer then2', true)
    commands[20] == new Command('testforblock ${this - 19} chain_command_block -1 {SuccessCount:0}')
    commands[21] == new Command('say outer else', true)
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
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 23

    commands[0] == new Command('outer condition')
    commands[1] == new Command('say outer then', true)
    commands[2] == new Command('testforblock ${this - 2} chain_command_block -1 {SuccessCount:0}')
    commands[3]== new Command('say outer else1', true)
    commands[4] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}')
    commands[5] == new Command('middle condition', true)
    commands[6] == new Command('say middle then', true)
    commands[7] == new Command('testforblock ${this - 7} chain_command_block -1 {SuccessCount:0}')
    commands[8] == new Command('testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}', true)
    commands[9] == new Command('say middle else1', true)
    commands[10] == new Command('testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}')
    commands[11] == new Command('testforblock ${this - 6} chain_command_block -1 {SuccessCount:0}', true)
    commands[12] == new Command('inner condition', true)
    commands[13] == new Command('say inner then', true)
    commands[14] == new Command('testforblock ${this - 14} chain_command_block -1 {SuccessCount:0}')
    commands[15] == new Command('testforblock ${this - 10} chain_command_block -1 {SuccessCount:0}', true)
    commands[16] == new Command('testforblock ${this - 4} chain_command_block -1 {SuccessCount:0}', true)
    commands[17] == new Command('say inner else', true)
    commands[18] == new Command('testforblock ${this - 18} chain_command_block -1 {SuccessCount:0}')
    commands[19] == new Command('testforblock ${this - 14} chain_command_block -1 {SuccessCount:0}', true)
    commands[20] == new Command('say middle else2', true)
    commands[21] == new Command('testforblock ${this - 21} chain_command_block -1 {SuccessCount:0}')
    commands[22] == new Command('say outer else2', true)
  }

  @Test
  public void "Ein impulse Prozess deaktiviert sich selbst"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name} (
    /say hi
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("say hi")
  }

  @Test
  public void "Eine repeat Prozess deaktiviert sich nicht selbst"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    /say hi
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.isEmpty()
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command("say hi", Mode.REPEAT, false)
  }

  @Test
  public void "Eine Datei kann mehrere Prozesse enthalten"() {
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
    interpreter.exceptions.isEmpty()
    List<CommandChain> chains = interpreter.chains
    chains.size() == 3

    chains[0].name == id1
    List<Command> commands1 = chains[0].commands
    commands1.size() == 2
    commands1[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands1[1] == new Command("say I am a default process")

    chains[1].name == id2
    List<Command> commands2 = chains[1].commands
    commands2.size() == 2
    commands2[0] == new Command("setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands2[1] == new Command("say I am an impulse process, wich is actually equivalent to the default")

    chains[2].name == id3
    List<Command> commands3 = chains[2].commands
    commands3.size() == 1
    commands3[0] == new Command("say I am a repeating process. I am completely different :)", Mode.REPEAT, false)
  }

  @Test
  void "an Interpreter will always include subfiles of it's parent directory, including itself"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.mpl')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)
    expect:
    interpreter.imports.size() == 2
    interpreter.imports.containsAll([programFile, neighbourFile])
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
    interpreter.exceptions.isEmpty()
    interpreter.imports.size() == 2
    interpreter.imports.containsAll([programFile, otherFile])
  }

  @Test
  void "calling addFileImport twice with the same file will add an exception"() {
    given:
    File otherFile = newTempFile()
    File programFile = new File(otherFile.parentFile, 'folder/file.txt')
    programFile.parentFile.mkdirs()
    programFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)
    when:
    interpreter.addFileImport(null, otherFile)
    interpreter.addFileImport(null, otherFile)
    then:
    interpreter.exceptions.size() == 1
    CompilerException ex = interpreter.exceptions.first()
    ex.file == programFile
    ex.token == null
    ex.message == 'Duplicate import.'
  }

  @Test
  public void "Eine Projektdatei mit Includes erzeugt Includes"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    project ${id1}
    include "datei1.mpl"
    include "ordner2"
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
    interpreter.exceptions.isEmpty()
    File parent = file.parentFile
    Map<File, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(null); // null indicates that the whole file should be included
    includes.size() == 2
    includes[0].files.size()==1
    includes[0].files.containsAll([new File(parent, "datei1.mpl")])
    includes[0].processName == null
    includes[1].files.size()==1
    includes[1].files.containsAll([new File(parent, "ordner2/datei4.mpl")])
    includes[1].processName == null
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
    interpreter.exceptions.isEmpty()
    Map<File, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes.size() == 1
    includes[0].files.size() == 1
    includes[0].files.containsAll([lastTempFile])
    includes[0].processName == id2
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
    interpreter.exceptions.isEmpty()
    Map<File, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes.size() == 1
    includes[0].files.size() == 2
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].processName == id2
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
    interpreter.exceptions.isEmpty()
    Map<File, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes.size() == 1
    includes[0].files.size() == 2
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].processName == id2
  }

  @Test
  public void "Two processes with the same name throws Exception"() {
    given:
    String id = someIdentifier()
    String programString = """
    process ${id} (
    /say I am a process
    )

    process ${id} (
    /say I am also a process
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    interpreter.exceptions.size() == 2
    interpreter.exceptions[0].file == lastTempFile
    interpreter.exceptions[0].token.line == 2
    interpreter.exceptions[0].token.text == id
    interpreter.exceptions[0].message == "Process ${id} is ambigious. Every process must have a unique name."
    interpreter.exceptions[1].file == lastTempFile
    interpreter.exceptions[1].token.line == 6
    interpreter.exceptions[1].token.text == id
    interpreter.exceptions[1].message == "Process ${id} is ambigious. Every process must have a unique name."
  }
}
