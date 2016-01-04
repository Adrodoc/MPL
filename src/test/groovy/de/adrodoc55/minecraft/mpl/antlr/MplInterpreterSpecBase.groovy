package de.adrodoc55.minecraft.mpl.antlr

import static de.adrodoc55.TestBase.someString

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification

class MplInterpreterSpecBase extends Specification {
  @Rule
  TemporaryFolder tempFolder
  File lastTempFile

  File newTempFile() {
    lastTempFile = tempFolder.newFile()
  }

  MplInterpreter interpret(String program) {
    File file = newTempFile()
    file.text = program
    interpret(file)
  }

  MplInterpreter interpret(File file = newTempFile()) {
    MplInterpreter.interpret(file)
  }
}
