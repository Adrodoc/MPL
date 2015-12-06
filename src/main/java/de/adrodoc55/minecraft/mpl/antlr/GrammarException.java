package de.adrodoc55.minecraft.mpl.antlr;

public class GrammarException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GrammarException() {
    }

    public GrammarException(String message) {
        super(message);
    }

    public GrammarException(Throwable cause) {
        super(cause);
    }

    public GrammarException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrammarException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
