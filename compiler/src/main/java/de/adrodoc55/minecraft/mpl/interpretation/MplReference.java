package de.adrodoc55.minecraft.mpl.interpretation;

import java.io.File;
import java.util.Collection;
import java.util.List;

import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.FileException;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;

public interface MplReference {
  CompilerException createAmbigiousException(List<File> found);

  Collection<File> getImports();

  boolean isContainedIn(MplProgram program);

  MplSource getSource();

  CompilerException createNotFoundException(FileException lastException);

  MplProcess getProcess(MplProgram program);
}
