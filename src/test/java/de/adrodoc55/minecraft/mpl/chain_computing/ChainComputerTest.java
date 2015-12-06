package de.adrodoc55.minecraft.mpl.chain_computing;

import static de.adrodoc55.TestBase.someInt;
import static de.adrodoc55.TestBase.someString;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Command;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        CommandBlockChain optimal = underTest.computeOptimalChain(input);
        // Then:
        List<CommandBlock> blocks = optimal.getCommandBlocks();
        List<Command> actual = Lists.transform(blocks, block -> {
            return block.toCommand();

        });
        assertThat(actual).containsExactlyElementsOf(commands);
    }

    @Test
    public void test_computeChain_Erster_Command_darf_nicht_conditional_sein() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();

        int anzahl = someInt(10);
        commands.add($some($Command().withConditional(true)));
        for (int a = 1; a < anzahl; a++) {
            commands.add($some($Command()));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        try {
            underTest.computeOptimalChain(input);
            fail("No Exception thrown.");
        } catch (Exception ex) {
            // Then:
            assertThat(ex).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(ex.getMessage()).isEqualTo(
                    "The first Command can't be conditional!");
        }

    }

    @Test
    // 7 + 1 = 8 = 2 ^ 3 (Der letzte Block muss Luft sein)
    public void test_computeChain_7_nicht_conditional_Commandos_ergeben_2x2x2_Würfel_und_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();
        for (int a = 0; a < 7; a++) {
            commands.add($some($Command().withConditional(false)));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        CommandBlockChain optimal = underTest.computeOptimalChain(input);
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
    // 26 + 1 = 27 = 3 ^ 3 (Der letzte Block muss Luft sein)
    public void test_computeChain_26_nicht_conditional_Commandos_ergeben_3x3x3_Würfel_und_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();
        for (int a = 0; a < 26; a++) {
            commands.add($some($Command().withConditional(false)));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<CommandBlockChain> task = new Callable<CommandBlockChain>() {
            public CommandBlockChain call() {
                return underTest.computeOptimalChain(input);
            }
        };
        Future<CommandBlockChain> future = executor.submit(task);
        CommandBlockChain optimal = null;
        try {
            optimal = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            fail("TimeoutException: " + ex);
        } catch (InterruptedException ex) {
            fail("InterruptedException: " + ex);
        } catch (ExecutionException ex) {
            fail("ExecutionException: " + ex);
        } finally {
            future.cancel(true); // may or may not desire this
        }
        // Then:
        // 2 ^ 3 Würfel
        assertThat(optimal.getMin()).isEqualTo(new Coordinate3D(0, 0, 0));
        assertThat(optimal.getMax()).isEqualTo(new Coordinate3D(2, 2, 2));
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
        CommandBlockChain optimal = underTest.computeOptimalChain(input);
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

    @Test
    // Conditional Commandos dürfen nicht geknickt werden.
    public void test_computeChain_conditional_Commandos_werden_nicht_geknickt_und_Kette_enthaelt_alle_Commandos() {
        // Given:
        String name = someString();
        List<Command> commands = new ArrayList<Command>();
        commands.add($some($Command().withConditional(false)));
        for (int a = 1; a < 7; a++) {
            commands.add($some($Command()));
        }
        CommandChain input = new CommandChain(name, commands);

        // When:
        CommandBlockChain optimal = underTest.computeOptimalChain(input);
        // Then:
        List<CommandBlock> commandBlocks = optimal.getCommandBlocks();
        for (int a = 0; a < commandBlocks.size(); a++) {
            CommandBlock commandBlock = commandBlocks.get(a);
            if (commandBlock.isConditional()) {
                Coordinate3D currentCoordinate = commandBlock.getCoordinate();
                Coordinate3D relativeCoordinate = commandBlock.getDirection()
                        .toCoordinate();

                Coordinate3D expectedPreviousCoordinate = currentCoordinate
                        .minus(relativeCoordinate);
                Coordinate3D actualPreviousCoordinate = commandBlocks
                        .get(a - 1).getCoordinate();
                assertThat(actualPreviousCoordinate).isEqualTo(
                        expectedPreviousCoordinate);

                if (a + 1 < commandBlocks.size()) {
                    Coordinate3D expectedNextCoordinate = currentCoordinate
                            .plus(relativeCoordinate);
                    Coordinate3D actualNextCoordinate = commandBlocks
                            .get(a + 1).getCoordinate();
                    assertThat(actualNextCoordinate).isEqualTo(
                            expectedNextCoordinate);
                }
            }
        }
        // Enthält alle Commandos
        List<CommandBlock> blocks = commandBlocks;
        List<Command> actual = Lists.transform(blocks, block -> {
            return block.toCommand();

        });
        assertThat(actual).containsExactlyElementsOf(commands);
    }

}
