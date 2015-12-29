package de.adrodoc55.minecraft.mpl.antlr;

import static de.adrodoc55.TestBase.someString
import static org.assertj.core.api.Assertions.assertThat

import org.junit.Test

import spock.lang.Unroll
import de.adrodoc55.minecraft.Coordinate3D
import de.adrodoc55.minecraft.mpl.Command
import de.adrodoc55.minecraft.mpl.CommandChain
import de.adrodoc55.minecraft.mpl.Program
import de.adrodoc55.minecraft.mpl.Command.Mode

public class MplInterpreterSpec extends MplInterpreterSpecBase {

  @Test
  @Unroll("Teste Command ('#programString')")
  public void "Teste Command"(String programString, Mode mode, boolean conditional, boolean needsRedstone) {
    given:
    String commandString = programString.find('/.+$')
    when:
    MplInterpreter interpreter = interpret(programString);
    then:
    List chains = interpreter.getChains();
    chains.size() == 1

    CommandChain chain = chains.first()

    List<Command> commands = chain.commands
    commands.size() == 1

    Command command = commands.first()
    command.command == commandString
    command.mode == mode
    command.conditional == conditional
    command.needsRedstone() == needsRedstone

    where:
    programString 													| mode 			| conditional 	| needsRedstone
    '/' + someString() + '\n'										| Mode.CHAIN	| false			| false
    'impulse: /' + someString() + '\n'								| Mode.IMPULSE	| false			| true
    'chain: /' + someString() + '\n'								| Mode.CHAIN	| false			| false
    'repeat: /' + someString() + '\n'								| Mode.REPEAT	| false			| true

    'conditional: /' + someString() + '\n'							| Mode.CHAIN	| true			| false
    'impulse, conditional: /' + someString() + '\n'					| Mode.IMPULSE	| true			| true
    'chain, conditional: /' + someString() + '\n'					| Mode.CHAIN	| true			| false
    'repeat, conditional: /' + someString() + '\n'					| Mode.REPEAT	| true			| true

    'needs redstone: /' + someString() + '\n'						| Mode.CHAIN	| false			| true
    'impulse, needs redstone: /' + someString() + '\n'				| Mode.IMPULSE	| false			| true
    'chain, needs redstone: /' + someString() + '\n'				| Mode.CHAIN	| false			| true
    'repeat, needs redstone: /' + someString() + '\n'				| Mode.REPEAT	| false			| true

    'always active: /' + someString() + '\n'						| Mode.CHAIN	| false			| false
    'impulse, always active: /' + someString() + '\n'				| Mode.IMPULSE	| false			| false
    'chain, always active: /' + someString() + '\n'					| Mode.CHAIN	| false			| false
    'repeat, always active: /' + someString() + '\n'				| Mode.REPEAT	| false			| false

    'conditional, needs redstone: /' + someString() + '\n'			| Mode.CHAIN	| true			| true
    'impulse, conditional, needs redstone: /' + someString() + '\n'	| Mode.IMPULSE	| true			| true
    'chain, conditional, needs redstone: /' + someString() + '\n'	| Mode.CHAIN	| true			| true
    'repeat, conditional, needs redstone: /' + someString() + '\n'	| Mode.REPEAT	| true			| true

    'conditional, always active: /' + someString() + '\n'			| Mode.CHAIN	| true			| false
    'impulse, conditional, always active: /' + someString() + '\n'	| Mode.IMPULSE	| true			| false
    'chain, conditional, always active: /' + someString() + '\n'	| Mode.CHAIN	| true			| false
    'repeat, conditional, always active: /' + someString() + '\n'	| Mode.REPEAT	| true			| false
  }
}
