package de.adrodoc55.minecraft.mpl.antlr;

import static de.adrodoc55.TestBase.someString;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import de.adrodoc55.minecraft.mpl.CommandChain;
import de.adrodoc55.minecraft.mpl.Program;

public class MplCompilerTest extends MplCompilerTestBase {

    @Test
    public void test_ein_normaler_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText(commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.CHAIN);
        assertThat(command.isConditional()).isFalse();
        assertThat(command.needsRedstone()).isFalse();
    }

    @Test
    public void test_ein_impulse_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("impulse:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.IMPULSE);
        assertThat(command.isConditional()).isFalse();
        assertThat(command.needsRedstone()).isTrue();
    }

    @Test
    public void test_ein_chain_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("chain:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.CHAIN);
        assertThat(command.isConditional()).isFalse();
        assertThat(command.needsRedstone()).isFalse();
    }

    @Test
    public void test_ein_repeat_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("repeat:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.REPEAT);
        assertThat(command.isConditional()).isFalse();
        assertThat(command.needsRedstone()).isTrue();
    }

    @Test
    public void test_ein_conditional_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("conditional:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.CHAIN);
        assertThat(command.isConditional()).isTrue();
        assertThat(command.needsRedstone()).isFalse();
    }

    @Test
    public void test_ein_impulse_conditional_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("impulse, conditional:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.IMPULSE);
        assertThat(command.isConditional()).isTrue();
        assertThat(command.needsRedstone()).isTrue();
    }

    @Test
    public void test_ein_chain_conditional_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("chain, conditional:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.CHAIN);
        assertThat(command.isConditional()).isTrue();
        assertThat(command.needsRedstone()).isFalse();
    }

    @Test
    public void test_ein_repeat_conditional_Command() throws IOException {
        // Given:
        setChainName(someString());
        String commandString = "/" + someString();
        setText("repeat, conditional:" + commandString);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);

        Entry<Coordinate3D, CommandChain> entry = chains.entrySet().iterator()
                .next();
        assertThat(entry.getKey()).isEqualTo(new Coordinate3D());

        CommandChain value = entry.getValue();
        assertThat(value.getName()).isEqualTo(this.getChainName());

        List<Command> commands = value.getCommands();
        assertThat(commands.size()).isEqualTo(1);

        Command command = commands.iterator().next();
        assertThat(command.getCommand()).isEqualTo(commandString);
        assertThat(command.getMode()).isSameAs(Mode.REPEAT);
        assertThat(command.isConditional()).isTrue();
        assertThat(command.needsRedstone()).isTrue();
    }

}
