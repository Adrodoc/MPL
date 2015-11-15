package de.adrodoc55.minecraft.mpl;

import java.util.List;

public class CommandBlockChain {

	private final String name;
	private final List<CommandBlock> commandBlocks;

	public CommandBlockChain(String name, List<CommandBlock> commandBlocks) {
		this.name = name;
		this.commandBlocks = commandBlocks;
	}

	public String getName() {
		return name;
	}

	public List<CommandBlock> getCommandBlocks() {
		return commandBlocks;
	}

}
