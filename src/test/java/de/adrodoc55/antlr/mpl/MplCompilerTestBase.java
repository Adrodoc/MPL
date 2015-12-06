package de.adrodoc55.antlr.mpl;

import static de.adrodoc55.TestBase.someString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import de.adrodoc55.minecraft.mpl.Program;

public class MplCompilerTestBase {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setup() {
        tempFile = null;
        fileName = null;
        chainName = null;
        extension = null;
    }

    private File tempFile;
    private String fileName;
    private String chainName;
    private String extension;

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    protected File getTempFile() {
        if (tempFile == null) {
            tempFile = new File(tempFolder.getRoot(), getFileName());
        }
        return tempFile;
    }

    protected String getFileName() {
        if (fileName == null) {
            fileName = getChainName() + getExtension();
        }
        return fileName;
    }

    protected String getChainName() {
        if (chainName == null) {
            chainName = someString();
        }
        return chainName;
    }

    protected String getExtension() {
        if (extension == null) {
            extension = ".txt";
        }
        return extension;
    }

    protected void setText(String text) throws IOException {
        Files.write(text, getTempFile(), Charset.defaultCharset());
    }

    protected Program compile() throws IOException {
        return MplCompiler.compile(getTempFile());
    }

}
