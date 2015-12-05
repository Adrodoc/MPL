package de.adrodoc55.minecraft.mpl.chain_computing;

import static de.adrodoc55.TestBase.someInt;
import static de.adrodoc55.TestBase.someString;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Command;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$some;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.adrodoc55.minecraft.Coordinate3D;
import de.adrodoc55.minecraft.mpl.Command;
import de.adrodoc55.minecraft.mpl.CommandBlock;
import de.adrodoc55.minecraft.mpl.CommandBlockChain;
import de.adrodoc55.minecraft.mpl.CommandChain;

public abstract class ChainComputerTest {
    protected ChainComputer underTest;

    @Before
    public abstract void setup();

    @Test
    public void test_computeChain_Erzeugte_Kette_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();

        int anzahl = someInt(10);
        commands.add($some($Command().withConditional(false)));
        for (int a = 1; a < anzahl; a++) {
            commands.add($some($Command()));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        CommandBlockChain optimal = underTest.computeOptimalChain(new Coordinate3D(), input);
        // Then:
        List<CommandBlock> blocks = optimal.getCommandBlocks();
        List<Command> actual = Lists.transform(blocks, block -> {
            return block.toCommand();

        });
        assertThat(actual).containsExactlyElementsOf(commands);
    }

    @Test
    // 7 + 1 = 8 = 2 ^ 3 (Der letzte Block muss Luft sein)
    public void test_computeChain_7_nicht_conditional_Commandos_ergeben_2x2_Würfel_und_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();
        for (int a = 0; a < 7; a++) {
            commands.add($some($Command().withConditional(false)));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        CommandBlockChain optimal = underTest.computeOptimalChain(new Coordinate3D(), input);
        // Then:
        // 2 ^ 3 Würfel
        assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
        assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(1, 1, 1));
        // Enthält alle Commandos
        List<CommandBlock> blocks = optimal.getCommandBlocks();
        List<Command> actual = Lists.transform(blocks, block -> {
            return block.toCommand();

        });
        assertThat(actual).containsExactlyElementsOf(commands);
    }

    @Test
    // Conditional Commandos dürfen nicht geknickt werden.
    public void test_computeChain_7_conditional_Commandos_ergibt_Kette_und_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();
        commands.add($some($Command().withConditional(false)));
        for (int a = 1; a < 7; a++) {
            commands.add($some($Command().withConditional(true)));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        CommandBlockChain optimal = underTest.computeOptimalChain(new Coordinate3D(), input);
        // Then:
        // 7er Kette Würfel
        assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
        assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(0, 6, 0));
        // Enthält alle Commandos
        List<CommandBlock> blocks = optimal.getCommandBlocks();
        List<Command> actual = Lists.transform(blocks, block -> {
            return block.toCommand();

        });
        assertThat(actual).containsExactlyElementsOf(commands);
    }

}
