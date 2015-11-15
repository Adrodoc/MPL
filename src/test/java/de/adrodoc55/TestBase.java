package de.adrodoc55;

import java.util.Random;

import org.assertj.core.api.Assertions;

public class TestBase extends Assertions {

	private static final Random RANDOM = new Random(5);

	public static int someInt() {
		return RANDOM.nextInt();
	}

	public static int someInt(int bound) {
		return RANDOM.nextInt(bound);
	}

	public static String someString() {
		return "String" + someInt();
	}

	public static boolean someBoolean() {
		return RANDOM.nextBoolean();
	}

}
