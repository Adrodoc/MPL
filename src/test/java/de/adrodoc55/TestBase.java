package de.adrodoc55;

import java.util.Random;

public class TestBase {

	private static final Random RANDOM = new Random(5);

	public static int someInt() {
		return RANDOM.nextInt();
	}

	public static String someString() {
		return "String" + someInt();
	}

}
