package de.adrodoc55.minecraft.mpl.antlr;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.antlr.v4.runtime.Token;

public class Include {
    private final File srcFile;
    private final Token token;
    private final String processName;
    private final Collection<File> files;

    public Include(File srcFile, Token token, String includePath) {
        this.srcFile = srcFile;
        this.token = token;
        processName = null;
        files = new HashSet<File>(1);
        files.add(new File(srcFile.getParentFile(), includePath));
    }

    public Include(File srcFile, Token token, String processName,
            Collection<File> imports) {
        this.srcFile = srcFile;
        this.token = token;
        this.processName = processName;
        files = imports;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public int getSrcLine() {
        return token.getLine();
    }

    public int getSrcIndex() {
        return token.getStartIndex();
    }

    public Collection<File> getFiles() {
        return files;
    }

    public String getProcessName() {
        return processName;
    }

}
