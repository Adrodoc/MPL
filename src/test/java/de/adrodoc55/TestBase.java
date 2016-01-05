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

    private static int somePositiveInt() {
        return RANDOM.nextInt(Integer.MAX_VALUE);
    }

    public static String someString() {
        return "String" + someInt();
    }

    public static String someIdentifier() {
        return "Identifier_" + somePositiveInt();
    }

    public static boolean someBoolean() {
        return RANDOM.nextBoolean();
    }

}
