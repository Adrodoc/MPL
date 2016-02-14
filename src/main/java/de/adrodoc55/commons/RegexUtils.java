package de.adrodoc55.commons;

import java.util.regex.Pattern;

public class RegexUtils {
  private static final Pattern SPECIAL_REGEX_CHARS =
      Pattern.compile("[\\{\\}\\(\\)\\[\\]\\.\\+\\*\\?\\^\\$\\|\\\\]");

  private RegexUtils() throws Throwable {
    throw new Throwable("Utils Classes cannot be instantiated");
  }

  public static String escape(String literal) {
    return SPECIAL_REGEX_CHARS.matcher(literal).replaceAll("\\\\$0").replaceAll("\r?\n", "\\\\R");
  }

}
