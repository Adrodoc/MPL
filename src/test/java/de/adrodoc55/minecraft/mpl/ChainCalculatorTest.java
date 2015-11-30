package de.adrodoc55.minecraft.mpl;

import static de.adrodoc55.TestBase.someString;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Command;
import static de.adrodoc55.minecraft.mpl.MplTestBase.$some;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.adrodoc55.minecraft.mpl.chain_computing.RecursiveChainComputer;

public class ChainCalculatorTest {

	@Test
	public void test_calculateChain_Erzeugte_Kette_enthaelt_alle_Commandos() {
		// Given:
		RecursiveChainComputer underTest = new RecursiveChainComputer();
		String name = someString();
		List<Command> commands = new ArrayList<Command>();
		commands.add($some($Command()));
		commands.add($some($Command()));
		commands.add($some($Command()));
		commands.add($some($Command()));
		commands.add($some($Command()));
		CommandChain input = new CommandChain(name, commands);

		// When:
		CommandBlockChain optimal = underTest.calculateOptimalChain(input);
		// Then:
		List<CommandBlock> blocks = optimal.getCommandBlocks();
		List<Command> actual = Lists.transform(blocks, block -> {
			return block.toCommand();

		});
		assertThat(actual).containsExactlyElementsOf(commands);
	}

}
