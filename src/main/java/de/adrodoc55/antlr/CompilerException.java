package de.adrodoc55.antlr;

public class CompilerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompilerException() {
    }

    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(Throwable cause) {
        super(cause);
    }

    public CompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompilerException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
