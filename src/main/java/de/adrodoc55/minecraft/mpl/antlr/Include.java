package de.adrodoc55.minecraft.mpl.antlr;

import java.io.File;
import java.util.Collection;

import org.antlr.v4.runtime.Token;

public class Include {
  private final File srcFile;
  private final Token token;
  private final String processName;
  private final String line;
  private final Collection<File> files;

  public Include(File srcFile, Token token, String line, Collection<File> imports) {
    this(srcFile, token, null, line, imports);
  }

  public Include(File srcFile, Token token, String processName, String line,
      Collection<File> imports) {
    this.srcFile = srcFile;
    this.token = token;
    this.processName = processName;
    this.line = line;
    files = imports;
  }

  public File getSrcFile() {
    return srcFile;
  }

  public Token getToken() {
    return token;
  }

  public int getSrcLineNumber() {
    return token.getLine();
  }

  public int getStartIndex() {
    return token.getStartIndex();
  }

  public int getStopIndex() {
    return token.getStopIndex();
  }

  public String getProcessName() {
    return processName;
  }

  public String getSrcLine() {
    return line;
  }

  public Collection<File> getFiles() {
    return files;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((files == null) ? 0 : files.hashCode());
    result = prime * result + ((processName == null) ? 0 : processName.hashCode());
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
    if (files == null) {
      if (other.files != null)
        return false;
    } else if (!files.equals(other.files))
      return false;
    if (processName == null) {
      if (other.processName != null)
        return false;
    } else if (!processName.equals(other.processName))
      return false;
    return true;
  }

}
