package de.adrodoc55.minecraft.mpl.antlr;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.adrodoc55.minecraft.mpl.CompilerException;

public class CompilationFailedException extends Exception {

  private static final long serialVersionUID = 3102503882849016612L;
  private final Map<File, List<CompilerException>> exceptions;

  public CompilationFailedException(Map<File, List<CompilerException>> exceptions) {
    super();
    this.exceptions = exceptions;
  }

  public Map<File, List<CompilerException>> getExceptions() {
    return exceptions;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<File, List<CompilerException>> outer_it : exceptions.entrySet()) {
      for (CompilerException it : outer_it.getValue()) {
        sb.append(it.toString());
        sb.append('\n');
      }
    }
    return sb.toString();
  }
}
