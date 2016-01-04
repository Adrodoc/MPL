package de.adrodoc55.minecraft.mpl.antlr;

import java.io.File;

import de.adrodoc55.minecraft.mpl.antlr.MplParser.IncludeContext;

public class Include {
    private File srcFile;
    private File programFile;
    private IncludeContext ctx;

    // public Include(File programFile) {
    // this.programFile = programFile;
    // }

    public Include(File srcFile, File parentFile, IncludeContext ctx) {
        this.srcFile = srcFile;
        String includePath = MplLexerUtils.getContainedString(ctx.STRING());
        this.programFile = new File(parentFile, includePath);
        this.ctx = ctx;
    }

    public File getProgramFile() {
        return programFile;
    }

    public void setProgramFile(File programFile) {
        this.programFile = programFile;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public int getSrcLine() {
        return ctx.STRING().getSymbol().getLine();
    }

    public int getSrcIndex() {
        return ctx.STRING().getSymbol().getStartIndex();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((programFile == null) ? 0 : programFile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Include other = (Include) obj;
        if (programFile == null) {
            if (other.programFile != null)
                return false;
        } else if (!programFile.equals(other.programFile))
            return false;
        return true;
    }

}
