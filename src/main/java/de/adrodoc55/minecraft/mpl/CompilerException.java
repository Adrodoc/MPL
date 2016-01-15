package de.adrodoc55.minecraft.mpl;

import java.io.File;

import org.antlr.v4.runtime.Token;

public class CompilerException extends Exception {

    private static final long serialVersionUID = 2588890897512612205L;
    private File file;
    private Token token;

    public CompilerException(File file, Token token, String message) {
        super(message);
        init(file, token);
    }

    // public CompilerException(File file, int line, int startIndex,
    // int stopIndex, String message) {
    // super(message);
    // init(file, line, startIndex, stopIndex);
    // }

    public CompilerException(File file, Token token, String message,
            Throwable cause) {
        super(message, cause);
        init(file, token);
    }

    // public CompilerException(File file, int line, int startIndex,
    // int stopIndex, String message, Throwable cause) {
    // super(message, cause);
    // init(file, line, startIndex, stopIndex);
    // }

    private void init(File file, Token token) {
        this.file = file;
        this.token = token;
    }

    public File getFile() {
        return file;
    }

    public Token getToken() {
        return token;
    }

}
