package de.adrodoc55.minecraft.mpl.antlr

import static de.adrodoc55.TestBase.someString

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification
import de.adrodoc55.minecraft.mpl.MplCompiler
import de.adrodoc55.minecraft.mpl.Program

class MplInterpreterSpecBase extends Specification {
  @Rule
  public TemporaryFolder tempFolder

  MplInterpreter interpret(String program) {
    File file = tempFolder.newFile()
    file.text = program
    interpret(file)
  }

  MplInterpreter interpret(File file = tempFolder.newFile()) {
    MplInterpreter.interpret(file)
  }
}
