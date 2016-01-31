package de.adrodoc55.minecraft.mpl;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.Token;

public class CompilerException extends Exception {

  private static final long serialVersionUID = 2588890897512612205L;
  private File file;
  private Token token;
  private String line;

  public CompilerException(File file, Token token, String line, String message) {
    super(message);
    init(file, token, line);
  }

  // public CompilerException(File file, int line, int startIndex,
  // int stopIndex, String message) {
  // super(message);
  // init(file, line, startIndex, stopIndex);
  // }

  public CompilerException(File file, Token token, String line, String message, Throwable cause) {
    super(message, cause);
    init(file, token, line);
  }

  // public CompilerException(File file, int line, int startIndex,
  // int stopIndex, String message, Throwable cause) {
  // super(message, cause);
  // init(file, line, startIndex, stopIndex);
  // }

  private void init(File file, Token token, String line) {
    this.file = file;
    this.token = token;
    this.line = line;
  }

  public File getFile() {
    return file;
  }

  public Token getToken() {
    return token;
  }

  public String getLine() {
    return line;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String path;
    try {
      path = this.getFile().getCanonicalPath();
    } catch (IOException e) {
      path = this.getFile().getAbsolutePath();
    }
    sb.append(path).append(':').append(token.getLine()).append(":\n");
    sb.append(this.getLocalizedMessage()).append("\n");
    sb.append(this.getLine()).append("\n");
    int count = this.getToken().getCharPositionInLine();
    sb.append(new String(new char[count]).replace('\0', ' '));
    sb.append("^");
    return sb.toString();
  }
}
