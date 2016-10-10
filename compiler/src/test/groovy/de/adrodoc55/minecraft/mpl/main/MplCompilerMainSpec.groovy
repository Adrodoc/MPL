/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.main;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL

import java.nio.file.Files
import java.nio.file.NoSuchFileException

import org.junit.Test

import de.adrodoc55.minecraft.mpl.MplSpecBase

public class MplCompilerMainSpec extends MplSpecBase {
  String lastOutput
  String lastError

  void compile(String args) {
    File libsDir = new File('build/libs')
    File src = libsDir.listFiles().find { it.name.matches(/mpl-compiler.+-standalone\.jar/) }
    File target = new File(tempFolder.root, src.name)
    Files.copy(src.toPath(), target.toPath())

    Process process = Runtime.getRuntime().exec("java -jar \"${target}\" ${args}")
    process.waitFor()
    lastOutput = process.getInputStream().text
    lastError = process.getErrorStream().text
  }

  @Test
  public void "without srcPath"() {
    given:
    new File('a.txt').deleteOnExit()
    when:
    compile("-o a.txt")
    then:
    lastError == 'Main parameters are required ("<src-file>")\r\nRun with \'-h\' to print help\r\n'
  }

  @Test
  public void "test startCompiler with invalid srcPath"() {
    given:
    File file = new File("test.mpl");
    file.delete();

    String[] args = ["test.mpl", "-o", "a.txt"]

    when:
    MplCompilerMain.main(args);

    then:
    thrown NoSuchFileException
  }

  @Test
  public void test_startCompiler_with_valid_srcPath() {
    given:
    File file = new File("test.mpl");
    file.createNewFile();
    file.deleteOnExit();

    String[] args = ["test.mpl", "-o", "a.txt"]

    when:
    MplCompilerMain.main(args);

    then:
    File output = new File('a.txt')
    output.exists()
    output.delete()
  }
}
