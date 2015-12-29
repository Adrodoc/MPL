package de.adrodoc55.minecraft.mpl;

import java.io.File;

public class CompilerException extends RuntimeException {

    private static final long serialVersionUID = 2588890897512612205L;
    private File file;
    private int line;
    private int index;

    public CompilerException(File file, int line, int index, String message) {
        super(message);
        init(file, line, index);
    }

    public CompilerException(File file, int line, int index, String message,
            Throwable cause) {
        super(message, cause);
        init(file, line, index);
    }

    private void init(File file, int line, int index) {
        this.file = file;
        this.line = line;
        this.index = index;
    }

    public File getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

}
