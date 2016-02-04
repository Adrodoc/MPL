package de.adrodoc55.minecraft.mpl.antlr;

public class IllegalModifierException extends Exception {

  private static final long serialVersionUID = 1L;

  public IllegalModifierException() {}

  public IllegalModifierException(String message) {
    super(message);
  }

  public IllegalModifierException(Throwable cause) {
    super(cause);
  }

  public IllegalModifierException(String message, Throwable cause) {
    super(message, cause);
  }

}
