package de.adrodoc55.minecraft.mpl;

import de.adrodoc55.TestBase;
import de.adrodoc55.minecraft.mpl.Command.Mode;
import net.karneim.pojobuilder.Builder;

public class MplTestBase extends TestBase {

	public static <P> P $some(Builder<P> builder) {
		return builder.build();
	}

	public static CommandBuilder $Command() {
		CommandBuilder builder = new CommandBuilder();
		builder.withCommand(someCommand());
		builder.withConditional(someBoolean());
		builder.withMode(someMode());
		builder.withNeedsRedstone(someBoolean());
		return builder;
	}

	private static Mode someMode() {
		Mode[] values = Mode.values();
		return values[someInt(values.length)];
	}

	private static String someCommand() {
		return "/" + someString();
	}
}
