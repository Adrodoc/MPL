package de.adrodoc55.minecraft.mpl.antlr

import static de.adrodoc55.TestBase.someString

import java.nio.file.Files

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification
import de.adrodoc55.minecraft.mpl.Program

class MplCompilerSpecBase extends Specification {
  @Rule
  public TemporaryFolder tempFolder

  Program compile(String program) {
    File file = tempFolder.newFile()
    file.text = program
    compile(file)
  }

  Program compile(File file = tempFolder.newFile()) {
    MplCompiler.compile(file);
  }
}
