package de.adrodoc55.minecraft.mpl.antlr

import static de.adrodoc55.TestBase.someIdentifier
import static de.adrodoc55.TestBase.someString
import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

import spock.lang.Unroll
import de.adrodoc55.minecraft.mpl.Command
import de.adrodoc55.minecraft.mpl.CommandChain
import de.adrodoc55.minecraft.mpl.CompilerException
import de.adrodoc55.minecraft.mpl.Command.Mode

public class MplInterpreterSpec extends MplInterpreterSpecBase {

  @Test
  @Unroll("Teste basis Modifier ('#programString')")
  public void "Teste basis Modifier"(String programString, Mode mode, boolean conditional, boolean needsRedstone) {
    given:
    String command = programString.find('/.+$')
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command(command, mode, conditional, needsRedstone)
    where:
    programString 											| mode 			| conditional 	| needsRedstone
    '/' + someString()										| Mode.CHAIN	| false			| false
    'impulse: /' + someString()								| Mode.IMPULSE	| false			| true
    'chain: /' + someString()								| Mode.CHAIN	| false			| false
    'repeat: /' + someString()								| Mode.REPEAT	| false			| true

    'conditional: /' + someString()							| Mode.CHAIN	| true			| false
    'impulse, conditional: /' + someString()				| Mode.IMPULSE	| true			| true
    'chain, conditional: /' + someString()					| Mode.CHAIN	| true			| false
    'repeat, conditional: /' + someString()					| Mode.REPEAT	| true			| true

    'needs redstone: /' + someString()						| Mode.CHAIN	| false			| true
    'impulse, needs redstone: /' + someString()				| Mode.IMPULSE	| false			| true
    'chain, needs redstone: /' + someString()				| Mode.CHAIN	| false			| true
    'repeat, needs redstone: /' + someString()				| Mode.REPEAT	| false			| true

    'always active: /' + someString()						| Mode.CHAIN	| false			| false
    'impulse, always active: /' + someString()				| Mode.IMPULSE	| false			| false
    'chain, always active: /' + someString()				| Mode.CHAIN	| false			| false
    'repeat, always active: /' + someString()				| Mode.REPEAT	| false			| false

    'conditional, needs redstone: /' + someString()			| Mode.CHAIN	| true			| true
    'impulse, conditional, needs redstone: /' + someString()| Mode.IMPULSE	| true			| true
    'chain, conditional, needs redstone: /' + someString()	| Mode.CHAIN	| true			| true
    'repeat, conditional, needs redstone: /' + someString()	| Mode.REPEAT	| true			| true

    'conditional, always active: /' + someString()			| Mode.CHAIN	| true			| false
    'impulse, conditional, always active: /' + someString()	| Mode.IMPULSE	| true			| false
    'chain, conditional, always active: /' + someString()	| Mode.CHAIN	| true			| false
    'repeat, conditional, always active: /' + someString()	| Mode.REPEAT	| true			| false
  }

  @Test
  @Unroll("Teste invert Modifier ('#programString')")
  public void "Teste invert Modifier"(String programString, Mode mode, boolean needsRedstone) {
    given:
    String command = programString.find('/.+$')
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3

    commands[0] == new Command("/blockdata \${this - 1} {SuccessCount:0}", true)
    commands[1] == new Command("/blockdata \${this - 1} {SuccessCount:1}")
    commands[2] == new Command(command, mode, true, needsRedstone)
    where:
    programString 										| mode 			| needsRedstone
    'invert: /' + someString()							| Mode.CHAIN	| false
    'impulse, invert: /' + someString()					| Mode.IMPULSE	| true
    'chain, invert: /' + someString()					| Mode.CHAIN	| false
    'repeat, invert: /' + someString()					| Mode.REPEAT	| true

    'invert, needs redstone: /' + someString()			| Mode.CHAIN	| true
    'impulse, invert, needs redstone: /' + someString()	| Mode.IMPULSE	| true
    'chain, invert, needs redstone: /' + someString()	| Mode.CHAIN	| true
    'repeat, invert, needs redstone: /' + someString()	| Mode.REPEAT	| true

    'invert, always active: /' + someString()			| Mode.CHAIN	| false
    'impulse, invert, always active: /' + someString()	| Mode.IMPULSE	| false
    'chain, invert, always active: /' + someString()	| Mode.CHAIN	| false
    'repeat, invert, always active: /' + someString()	| Mode.REPEAT	| false
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
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 6
    commands[0] == new Command("/say \${this + 7}")
    commands[1] == new Command("/say \${this + 3}")
    commands[2] == new Command("/blockdata \${this - 1} {SuccessCount:0}", true)
    commands[3] == new Command("/blockdata \${this - 1} {SuccessCount:1}")
    commands[4] == new Command("/say \${this - 3}", true)
    commands[5] == new Command("/say \${this - 7}")
  }

  @Test
  @Unroll("start generiert die richtigen Commandos ('#programString')")
  public void "start generiert die richtigen Commandos"(String programString, boolean conditional) {
    given:
    String identifier = programString.find('(?<=start ).+$')
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command("/execute @e[name=${identifier}] ~ ~ ~ /setblock ~ ~ ~ redstone_block", conditional)
    where:
    programString 								| conditional
    'start ' + someIdentifier()					| false
    'conditional: start ' + someIdentifier()	| true
  }

  @Test
  public void "repeating stop generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name}
    stop
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command("/execute @e[name=${name}] ~ ~ ~ /setblock ~ ~ ~ stone", Mode.REPEAT, false)
  }

  @Test
  public void "repeating conditional stop generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name}
    /say hi
    conditional: stop
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("/say hi", Mode.REPEAT, false)
    commands[1] == new Command("/execute @e[name=${name}] ~ ~ ~ /setblock ~ ~ ~ stone", true)
  }

  @Test
  public void "impulse stop wirft exception"() {
    given:
    String name = someIdentifier()
    String programString = """
    impulse process ${name}
    stop
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    CompilerException ex = thrown()
    ex.file == lastTempFile
    ex.line == 3
    ex.index == 47
    ex.message == 'Can only stop repeating processes.'
  }

  @Test
  public void "stop mit identifier generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String sid = someIdentifier()
    String programString = """
    impulse process ${name}
    stop ${sid}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("/execute @e[name=${sid}] ~ ~ ~ /setblock ~ ~ ~ stone")
  }

  @Test
  public void "notify generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name}
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("/execute @e[name=${name}_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("/kill @e[name=${name}_NOTIFY]")
  }

  @Test
  public void "conditional: notify generiert die richtigen Commandos"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name}
    conditional: notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("/execute @e[name=${name}_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block", true)
    commands[2] == new Command("/kill @e[name=${name}_NOTIFY]", true)
  }

  @Test
  public void "notify wirft auﬂerhalb eines Prozesses eine CompilerException"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    CompilerException ex = thrown()
    ex.file == lastTempFile
    ex.line == 2
    ex.index == 5
    ex.message == 'Encountered notify outside of a process context.'
  }

  @Test
  public void "In einem repeat Prozess stoppt notify den prozess nicht"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name}
    /say hi
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("/say hi", Mode.REPEAT, false)
    commands[1] == new Command("/execute @e[name=${name}_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block")
    commands[2] == new Command("/kill @e[name=${name}_NOTIFY]")
  }

  @Test
  public void "In einem repeat Prozess stoppt notify den Prozess nicht, ist aber conditional"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name}
    /say hi
    conditional: notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("/say hi", Mode.REPEAT, false)
    commands[1] == new Command("/execute @e[name=${name}_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block", true)
    commands[2] == new Command("/kill @e[name=${name}_NOTIFY]", true)
  }

  @Test
  public void "waitfor in repeating Prozess wirft Exception"() {
    given:
    String name = someIdentifier()
    String identifier = someIdentifier()
    String programString = """
    repeat process ${name}
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    CompilerException ex = thrown()
    ex.file == lastTempFile
    ex.line == 4
    ex.index == 77
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
    CompilerException ex = thrown()
    ex.file == lastTempFile
    ex.line == 4
    ex.index == 56
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
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 4
    //	commands[0] == start
    commands[1] == new Command("/summon ArmorStand \${this + 1} {CustomName:\"${identifier}_NOTIFY\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[2] == null
    commands[3] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
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
    CompilerException ex = thrown()
    ex.file == lastTempFile
    ex.line == 2
    ex.index == 11
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
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 3
    commands[0] == new Command("/summon ArmorStand \${this + 1} {CustomName:\"${identifier}_NOTIFY\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}")
    commands[1] == null
    commands[2] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
  }

  @Test
  public void "conditional: waitfor generiert die richtigen Commandos"() {
    given:
    String identifier = someIdentifier()
    String programString = 'conditional: waitfor ' + identifier
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 5
    commands[0] == new Command("/summon ArmorStand \${this + 3} {CustomName:\"${identifier}_NOTIFY\",NoGravity:1b,Invisible:1b,Invulnerable:1b,Marker:1b}", true)
    commands[1] == new Command("/blockdata \${this - 1} {SuccessCount:1}")
    commands[2] == new Command("/setblock \${this + 1} redstone_block", true)
    commands[3] == null
    commands[4] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
  }

  @Test
  public void "Ein impulse Prozess deaktiviert sich selbst"() {
    given:
    String name = someIdentifier()
    String programString = """
    process ${name}
    /say hi
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 2
    commands[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands[1] == new Command("/say hi")
  }

  @Test
  public void "Eine repeat Prozess deaktiviert sich nicht selbst"() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name}
    /say hi
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List chains = interpreter.chains
    chains.size() == 1

    CommandChain chain = chains.first()
    List<Command> commands = chain.commands
    commands.size() == 1
    commands[0] == new Command("/say hi", Mode.REPEAT, false)
  }

  @Test
  public void "Eine Datei kann mehrere Prozesse enthalten"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    String programString = """
    process ${id1}
    /say I am a default process
    impulse process ${id2}
    /say I am an impulse process, wich is actually equivalent to the default
    repeat process ${id3}
    /say I am a repeating process. I am completely different :)
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    List<CommandChain> chains = interpreter.chains
    chains.size() == 3

    chains[0].name == id1
    List<Command> commands1 = chains[0].commands
    commands1.size() == 2
    commands1[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands1[1] == new Command("/say I am a default process")

    chains[1].name == id2
    List<Command> commands2 = chains[1].commands
    commands2.size() == 2
    commands2[0] == new Command("/setblock \${this - 1} stone", Mode.IMPULSE, false)
    commands2[1] == new Command("/say I am an impulse process, wich is actually equivalent to the default")

    chains[2].name == id3
    List<Command> commands3 = chains[2].commands
    commands3.size() == 1
    commands3[0] == new Command("/say I am a repeating process. I am completely different :)", Mode.REPEAT, false)
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
    when:
    MplInterpreter interpreter = interpret(programString)
    File file = lastTempFile
    then:
    File parent = file.parentFile
    List<Include> includes = interpreter.includes
    includes.size() == 2
    includes[0].files.size()==1
    includes[0].files.containsAll([new File(parent, "datei1.mpl")])
    includes[0].processName == null
    includes[1].files.size()==1
    includes[1].files.containsAll([new File(parent, "ordner2")])
    includes[1].processName == null
  }

  @Test
  public void "Eine Prozessdatei, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1}
    /say I am a process
    conditional: start ${id2}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    File file = lastTempFile
    then:
    List<Include> includes = interpreter.includes
    includes.size() == 1
    includes[0].files.size() == 1
    includes[0].files.containsAll([file.parentFile])
    includes[0].processName == id2
  }

  @Test
  public void "Eine Prozessdatei mit Dir import, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFolder"
    process ${id1}
    /say I am a process
    conditional: start ${id2}
    """
    File file = newTempFile()
    File newFolder = new File(file.parentFile, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile")
    newFile.createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString, file)
    then:
    List<Include> includes = interpreter.includes
    includes.size() == 1
    includes[0].files.size() == 2
    includes[0].files.containsAll([file.parentFile, newFile])
    includes[0].processName == id2
  }

  @Test
  public void "Eine Prozessdatei mit File import, die versucht einen fremden Prozess zu starten erzeugt ein Include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFile"
    process ${id1}
    /say I am a process
    conditional: start ${id2}
    """
    File file = newTempFile()
    File newFile = new File(file.parentFile, "newFile")
    newFile.createNewFile()
    when:
    MplInterpreter interpreter = interpret(programString, file)
    then:
    List<Include> includes = interpreter.includes
    includes.size() == 1
    includes[0].files.size() == 2
    includes[0].files.containsAll([file.parentFile, newFile])
    includes[0].processName == id2
  }
}
