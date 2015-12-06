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

public class MplCompilerSpec extends MplCompilerSpecBase {

  @Test
  @Unroll("Teste Command ('#programString')")
  public void "Teste Command"(String programString, Mode mode, boolean conditional, boolean needsRedstone) {
    given:
    String commandString = programString.find('/.+$')
    when:
    Program program = compile(programString);
    then:
    List chains = program.getChains();
    chains.size() == 1

    CommandChain chain = chains.first()
    chain.min == new Coordinate3D()
    chain.max == new Coordinate3D(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)

    List<Command> commands = chain.commands
    commands.size() == 1

    Command command = commands.first()
    command.command == commandString
    command.mode == mode
    command.conditional == conditional
    command.needsRedstone() == needsRedstone

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

    'needsRedstone: /' + someString()						| Mode.CHAIN	| false			| true
    'impulse, needsRedstone: /' + someString()				| Mode.IMPULSE	| false			| true
    'chain, needsRedstone: /' + someString()				| Mode.CHAIN	| false			| true
    'repeat, needsRedstone: /' + someString()				| Mode.REPEAT	| false			| true

    'alwaysActive: /' + someString()						| Mode.CHAIN	| false			| false
    'impulse, alwaysActive: /' + someString()				| Mode.IMPULSE	| false			| false
    'chain, alwaysActive: /' + someString()					| Mode.CHAIN	| false			| false
    'repeat, alwaysActive: /' + someString()				| Mode.REPEAT	| false			| false

    'conditional, needsRedstone: /' + someString()			| Mode.CHAIN	| true			| true
    'impulse, conditional, needsRedstone: /' + someString()	| Mode.IMPULSE	| true			| true
    'chain, conditional, needsRedstone: /' + someString()	| Mode.CHAIN	| true			| true
    'repeat, conditional, needsRedstone: /' + someString()	| Mode.REPEAT	| true			| true

    'conditional, alwaysActive: /' + someString()			| Mode.CHAIN	| true			| false
    'impulse, conditional, alwaysActive: /' + someString()	| Mode.IMPULSE	| true			| false
    'chain, conditional, alwaysActive: /' + someString()	| Mode.CHAIN	| true			| false
    'repeat, conditional, alwaysActive: /' + someString()	| Mode.REPEAT	| true			| false
  }
}
