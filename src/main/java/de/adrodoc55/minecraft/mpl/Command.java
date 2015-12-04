package de.adrodoc55.minecraft.mpl;

import net.karneim.pojobuilder.Builder;
import net.karneim.pojobuilder.GeneratePojoBuilder;

public class Command {

	private String command;
	private Mode mode;
	private boolean conditional;
	private boolean needsRedstone;

	public Command(String command) {
		this(command, false);
	}

	public Command(String command, boolean conditional) {
		this(command, conditional, Mode.CHAIN);
	}

	public Command(String command, boolean conditional, Mode mode) {
		this(command, conditional, mode, mode == Mode.CHAIN ? false : true);
	}

	@GeneratePojoBuilder(withBuilderInterface = Builder.class)
	public Command(String command, boolean conditional, Mode mode, boolean needsRedstone) {
		this.command = command;
		this.conditional = conditional;
		this.mode = mode;
		this.needsRedstone = needsRedstone;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public static enum Mode {
		IMPULSE, CHAIN, REPEAT;
	}

	public boolean isConditional() {
		return conditional;
	}

	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}

	public boolean needsRedstone() {
		return needsRedstone;
	}

	public void setNeedsRedstone(boolean needsRedstone) {
		this.needsRedstone = needsRedstone;
	}

	@Override
	public String toString() {
		return "Command [command='" + command + "', mode=" + mode
				+ ", conditional=" + conditional + ", needsRedstone="
				+ needsRedstone + "]";
	}

}
