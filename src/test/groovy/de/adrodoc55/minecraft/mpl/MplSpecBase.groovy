package de.adrodoc55.minecraft.mpl

import static de.adrodoc55.TestBase.someString

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import de.adrodoc55.minecraft.mpl.antlr.MplInterpreter;
import spock.lang.Specification

class MplSpecBase extends Specification {
  @Rule
  TemporaryFolder tempFolder
  File lastTempFile

  File newTempFile() {
    lastTempFile = tempFolder.newFile()
  }

  MplInterpreter interpret(String program, File file = newTempFile()) {
    file.text = program
    interpret(file)
  }

  MplInterpreter interpret(File file = newTempFile()) {
    MplInterpreter.interpret(file)
  }
}
