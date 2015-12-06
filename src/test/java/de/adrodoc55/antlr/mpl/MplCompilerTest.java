package de.adrodoc55.antlr.mpl;

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
    public void test_Ein_normales_Commando_wird_erkannt() throws IOException {
        // Given:
        setChainName("Ein-Commando");
        setExtension(".txt");
        String text = "/ein Commando";
        setText(text);
        // When:
        Program program = compile();
        // Then:
        Map<Coordinate3D, CommandChain> chains = program.getChains();
        assertThat(chains.size()).isEqualTo(1);
        for (Entry<Coordinate3D, CommandChain> entry : chains.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(new Coordinate3D());
            CommandChain value = entry.getValue();
            assertThat(value.getName()).isEqualTo(this.getChainName());
            List<Command> commands = value.getCommands();
            assertThat(commands.size()).isEqualTo(1);
            for (Command command : commands) {
                assertThat(command.getCommand()).isEqualTo(text);
                assertThat(command.getMode()).isSameAs(Mode.CHAIN);
                assertThat(command.isConditional()).isFalse();
                assertThat(command.needsRedstone()).isFalse();
            }
        }
    }

}
