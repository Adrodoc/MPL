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
package de.adrodoc55.minecraft.mpl.compilation

import static de.adrodoc55.TestBase.some
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER

import org.antlr.v4.runtime.CommonToken
import org.junit.Test

import de.adrodoc55.minecraft.coordinate.Orientation3D
import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram
import de.adrodoc55.minecraft.mpl.blocks.AirBlock
import de.adrodoc55.minecraft.mpl.blocks.Transmitter
import de.adrodoc55.minecraft.mpl.chain.ChainContainer
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion
import spock.lang.Unroll

class MplCompilerSpec extends MplSpecBase {

  File lastProgramFile

  MplSource source() {
    new MplSource(lastProgramFile, "", new CommonToken(0))
  }

  private MplProgram assembleProgram(File programFile) {
    lastProgramFile = programFile
    MplCompiler compiler = new MplCompiler(MinecraftVersion.getDefault(), new CompilerOptions())
    MplProgram program = compiler.assemble(programFile)
    compiler.checkErrors()
    return program
  }

  private List<CommandBlockChain> place(File programFile, CompilerOption... options) {
    lastProgramFile = programFile
    MplCompiler compiler = new MplCompiler(MinecraftVersion.getDefault(), new CompilerOptions(options))
    MplProgram program = compiler.assemble(programFile)
    compiler.checkErrors()
    ChainContainer container = compiler.materialize(program)
    compiler.checkErrors()
    List<CommandBlockChain> chains = compiler.place(container)
    return chains
  }

  private MplCompilationResult compile(File programFile, CompilerOption... options) {
    lastProgramFile = programFile
    return MplCompiler.compile(programFile, MinecraftVersion.getDefault(), new CompilerOptions(options))
  }

  @Test
  public void "compiling a program without any remote processes throws an exception"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main {
      /this is the main process
    }

    process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    CompilationFailedException ex = thrown()
    Collection<CompilerException> exs = ex.errors.values()
    exs[0].source.file == new File(folder, 'main.mpl')
    exs[0].source.lineNumber == 0
    exs[0].source.text == null
    exs[0].message == "This file does not include any remote processes"
    exs.size() == 1
  }

  @Test
  public void "a process from the same file will be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process main {
      /this is the main process
    }

    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from a neighbour file will not be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'second.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))
  }

  @Test
  public void "a process from a neighbour file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process main {
      /this is the main process
      start other
    }
    """
    new File(folder, 'second.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from an imported file will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"

    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))
  }

  @Test
  public void "a process from an imported file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"

    remote process main {
      /this is the main process
      start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from an imported dir will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"

    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))
  }

  @Test
  public void "a process from an imported dir will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"

    remote process main {
      /this is the main process
      start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from the same file can be referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process main {
      /this is the main process
      start other
    }

    remote process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from the same file is not ambigious with an imported process and will win"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process main {
      /this is the main process
      start other
    }

    remote process other {
      /this is the other process in the same file
    }
    """
    new File(folder, 'other.mpl').text = """
    remote process other {
      /this is the other process from the other file
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process in the same file', source()))
  }

  @Test
  public void "a process that is referenced twice is not ambigious"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    remote process p1 {
      /this is the p1 process
      start p3
    }

    remote process p2 {
      /this is the p2 process
      start p3
    }

    remote process p3 {
      /this is the p3 process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    notThrown Exception
    result.processes.size() == 3
    MplProcess p1 = result.processes.find { it.name == 'p1' }
    p1.chainParts.contains(new MplCommand('/this is the p1 process', source()))

    MplProcess p2 = result.processes.find { it.name == 'p2' }
    p2.chainParts.contains(new MplCommand('/this is the p2 process', source()))

    MplProcess p3 = result.processes.find { it.name == 'p3' }
    p3.chainParts.contains(new MplCommand('/this is the p3 process', source()))
  }

  @Test
  public void "a process in the main file, that is referenced from a different file is not ambigious"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'p1.mpl').text = """
    remote process p1 {
      /this is the p1 process
      start p2
    }

    remote process p3 {
      /this is the p3 process
    }
    """
    new File(folder, 'p2.mpl').text = """
    remote process p2 {
      /this is the p2 process
      start p3
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'p1.mpl'))

    then:
    notThrown Exception
    result.processes.size() == 3
    MplProcess p1 = result.processes.find { it.name == 'p1' }
    p1.chainParts.contains(new MplCommand('/this is the p1 process', source()))

    MplProcess p2 = result.processes.find { it.name == 'p2' }
    p2.chainParts.contains(new MplCommand('/this is the p2 process', source()))

    MplProcess p3 = result.processes.find { it.name == 'p3' }
    p3.chainParts.contains(new MplCommand('/this is the p3 process', source()))
  }

  @Test
  public void "a process in a file that has already been processed and is referenced from a different file is not ambigious"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "p1.mpl"
    include "p2.mpl"
    """
    new File(folder, 'p1.mpl').text = """
    remote process p1 {
      /this is the p1 process
    }
    """
    new File(folder, 'p2.mpl').text = """
    remote process p2 {
      /this is the p2 process
      start p1
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    notThrown Exception
    result.processes.size() == 2
    MplProcess p1 = result.processes.find { it.name == 'p1' }
    p1.chainParts.contains(new MplCommand('/this is the p1 process', source()))

    MplProcess p2 = result.processes.find { it.name == 'p2' }
    p2.chainParts.contains(new MplCommand('/this is the p2 process', source()))
  }

  @Test
  public void "a process from an included file will be included"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "newFolder/newFile.mpl"

    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a process from an included dir will be included"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "newFolder"

    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
      /this is the other process
    }
    """
    new File(folder, 'newFolder/newFile.txt').text = """
    remote process irrelevant {
      /this is the irrelevant process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "the includes of an included file are processed"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "newFolder/newFile1.mpl"

    remote process main {
      /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile1.mpl').text = """
    include "newFile2.mpl"

    remote process other1 {
      /this is the other1 process
    }
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    remote process other2 {
      /this is the other2 process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 3
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other1 = result.processes.find { it.name == 'other1' }
    other1.chainParts.contains(new MplCommand('/this is the other1 process', source()))

    MplProcess other2 = result.processes.find { it.name == 'other2' }
    other2.chainParts.contains(new MplCommand('/this is the other2 process', source()))
  }

  @Test
  public void "the includes of an imported file are not processed"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile1.mpl"

    remote process main {
      /this is the main process
      start unknownProcess
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile1.mpl').text = """
    include "newFile2.mpl"

    remote process other1 {
      /this is the other1 process
    }
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    remote process other2 {
      /this is the other2 process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))
  }

  @Test
  public void "the includes of a referenced imported file are processed"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile1.mpl"

    remote process main {
      /this is the main process
      start other1
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile1.mpl').text = """
    include "newFile2.mpl"

    remote process other1 {
      /this is the other1 process
    }
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    remote process other2 {
      /this is the other2 process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 3
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other1 = result.processes.find { it.name == 'other1' }
    other1.chainParts.contains(new MplCommand('/this is the other1 process', source()))

    MplProcess other2 = result.processes.find { it.name == 'other2' }
    other2.chainParts.contains(new MplCommand('/this is the other2 process', source()))
  }

  @Test
  public void "including two processes with the same name throws duplicate process Exception"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "other1.mpl"
    include "other2.mpl"
    """
    new File(folder, 'other1.mpl').text = """
    remote process ${id2} {
      /this is the ${id2} process
    }
    """
    new File(folder, 'other2.mpl').text = """
    remote process ${id2} {
      /this is the second ${id2} process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    CompilationFailedException ex = thrown()
    Collection<CompilerException> exs = ex.errors.values()
    exs[0].source.file == new File(folder, 'other1.mpl')
    exs[0].source.lineNumber == 2
    exs[0].source.text == id2
    exs[0].message == "Duplicate process ${id2}; was also found in ${new File(folder, 'other2.mpl')}"
    exs[1].source.file == new File(folder, 'other2.mpl')
    exs[1].source.lineNumber == 2
    exs[1].source.text == id2
    exs[1].message == "Duplicate process ${id2}; was also found in ${new File(folder, 'other1.mpl')}"
    exs.size() == 2
  }

  @Test
  public void "scripts can't be included"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String id3 = some($Identifier())
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "newFolder/scriptFile.mpl"
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/scriptFile.mpl').text = """
    /this is a script
    /really !
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    CompilationFailedException ex = thrown()
    List<CompilerException> exs = ex.errors.get(new File(folder, 'main.mpl'))
    exs[0].source.file == new File(folder, 'main.mpl')
    exs[0].source.lineNumber == 2
    exs[0].source.text == '"newFolder/scriptFile.mpl"'
    exs[0].message == "Can't include script 'scriptFile.mpl'. Scripts may not be included."
    exs.size() == 1
  }

  @Test
  public void "orientation of main projects is processed"() {
    given:
    String id1 = some($Identifier())
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} {
      orientation "zyx"
    }

    remote process main {}
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.orientation == new Orientation3D('zyx')
  }

  @Test
  public void "orientation of included projects is ignored"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "other.mpl"

    remote process main {}
    """
    new File(folder, 'other.mpl').text = """
    project other {
      orientation "z-yx"
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.orientation == new Orientation3D()
  }

  @Test
  public void "ambigious processes within imports will be ignored, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    remote process main {
    /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process other {
    /this is the other process
    }
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    remote process other {
    /this is the second other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))
  }

  @Test
  public void "ambigious processes throw an Exception, if referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    remote process main {
    /this is the main process
    start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    File newFile = new File(folder, 'newFolder/newFile.mpl')
    newFile.text = """
    remote process other {
    /this is the other process
    }
    """
    File newFile2 = new File(folder, 'newFolder/newFile2.mpl')
    newFile2.text = """
    remote process other {
    /this is the second other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    CompilationFailedException ex = thrown()
    List<CompilerException> exs = ex.errors.get(new File(folder, 'main.mpl'))
    exs.size() == 1
    exs.first().message.startsWith "Process other is ambigious. It was found in "//'${newFile}' and '${newFile2}'"
  }

  @Test
  public void "all processes from an included file will be included"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "newFolder/newFile.mpl"
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    remote process main {
      /this is the main process
    }
    remote process other {
      /this is the other process
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process', source()))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process', source()))
  }

  @Test
  public void "a script starting with impulse can be compiled"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    impulse: /say hi
    """
    when:
    compile(new File(folder, 'main.mpl'))

    then:
    notThrown Exception
  }

  @Test
  public void "a script starting with repeat can be compiled"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    repeat: /say hi
    """
    when:
    compile(new File(folder, 'main.mpl'))

    then:
    notThrown Exception
  }

  @Test
  public void "a script can be compiled"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    /say hi
    """
    when:
    compile(new File(folder, 'main.mpl'))

    then:
    notThrown Exception
  }

  @Test
  public void "a script does not have install/uninstall by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    /say hi
    """
    when:
    List<CommandBlockChain> result = place(new File(folder, 'main.mpl'), TRANSMITTER)

    then:
    result.find { it.name == 'install' } == null
    result.find { it.name == 'uninstall' } == null
    result.find { it.name == null }
    result.size() == 1
  }

  @Test
  public void "having an install does not produce an uninstall"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install {
      /say install
    }
    """
    when:
    List<CommandBlockChain> result = place(new File(folder, 'main.mpl'), TRANSMITTER)

    then:
    result.find { it.name == 'install' }
    result.find { it.name == 'uninstall' } == null
    result.find { it.name == null }
    result.size() == 2
  }

  @Test
  public void "having an uninstall produces an install"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall {
      /say uninstall
    }
    """
    when:
    List<CommandBlockChain> result = place(new File(folder, 'main.mpl'), TRANSMITTER)

    then:
    CommandBlockChain install = result.find { it.name == 'install' }
    install.blocks[0].class == Transmitter
    install.blocks[1].getCommand().startsWith('setblock ')
    install.blocks[2].class == AirBlock
    install.blocks.size() == 3

    result.find { it.name == 'uninstall' }
    result.find { it.name == null }
    result.size() == 3
  }

  @Test
  public void "the commands of a custom install are executed after the generated ones"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install {
      /say install
    }

    remote process main { // If there is no process, there are no generated commands
      /say hi
    }
    """
    when:
    List<CommandBlockChain> result = place(new File(folder, 'main.mpl'), TRANSMITTER)

    then:
    CommandBlockChain install = result.find { it.name == 'install' }
    install.blocks.size() == 5
    install.blocks[0].class == Transmitter
    install.blocks[1].getCommand().startsWith('setblock ')
    install.blocks[2].getCommand().startsWith('summon ')
    install.blocks[3].getCommand() == 'say install'
    install.blocks[4].class == AirBlock
  }

  /**
   * Allowing start of processes within uninstall may cause problems if the started process
   * attempts to start more processes. The reason behind this is, that any newly started process is
   * executed 1 tick later at which point all processes will have been uninstalled.<br>
   * TODO Maybe the uninstall should automatically insert a multi-waitfor after all custom
   * commands for every process that has been started, but is not waited for. This would also
   * require every process that could at least be indirectly called by the uninstall to notify
   */
  @Test
  public void "the commands of a custom uninstall are executed before the generated ones"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall {
      /say uninstall
    }

    remote process main { // If there is no process, there are no generated commands
      /say hi
    }
    """
    when:
    List<CommandBlockChain> result = place(new File(folder, 'main.mpl'), TRANSMITTER)

    then:
    CommandBlockChain uninstall = result.find { it.name == 'uninstall' }
    uninstall.blocks.size() == 5
    uninstall.blocks[0].class == Transmitter
    uninstall.blocks[1].getCommand().startsWith('setblock ')
    uninstall.blocks[2].getCommand() == 'say uninstall'
    uninstall.blocks[3].getCommand().startsWith('kill @e[type=')
    uninstall.blocks[4].class == AirBlock
  }

  @Test
  public void "install and uninstall are processed"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install {
      /say main install
    }

    uninstall {
      /say main uninstall
    }

    remote process main {
      start other   // ein include erzeugen, damit sichergestellt wird, dass der interpreter sich nicht selbst included
    }

    remote process other {}
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    notThrown Exception

    Deque<ChainPart> install = result.install.chainParts
    install[0] == new MplCommand("/say main install", source())
    install.size() == 1

    Deque<ChainPart> uninstall = result.uninstall.chainParts
    uninstall[0] == new MplCommand("/say main uninstall", source())
    uninstall.size() == 1
  }

  @Test
  public void "the install of multiple files is concatenated"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "install1.mpl"
    include "install2.mpl"

    install {
      /say main install
    }

    remote process main {}
    """
    new File(folder, 'install1.mpl').text = """
    project p {}
    install {
      /say install 1
    }
    """
    new File(folder, 'install2.mpl').text = """
    project p {}
    install {
      /say install 2
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    notThrown Exception

    Deque<ChainPart> install = result.install.chainParts
    install[0] == new MplCommand("/say main install", source())
    install[1] == new MplCommand("/say install 1", source())
    install[2] == new MplCommand("/say install 2", source())
    install.size() == 3
  }

  @Test
  public void "the uninstall of multiple files is concatenated"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    include "uninstall1.mpl"
    include "uninstall2.mpl"

    uninstall {
      /say main uninstall
    }

    remote process main {}
    """
    new File(folder, 'uninstall1.mpl').text = """
    project p {}
    uninstall {
      /say uninstall 1
    }
    """
    new File(folder, 'uninstall2.mpl').text = """
    project p {}
    uninstall {
      /say uninstall 2
    }
    """
    when:
    MplProgram result = assembleProgram(new File(folder, 'main.mpl'))

    then:
    notThrown Exception

    Deque<ChainPart> uninstall = result.uninstall.chainParts
    uninstall[0] == new MplCommand("/say main uninstall", source())
    uninstall[1] == new MplCommand("/say uninstall 1", source())
    uninstall[2] == new MplCommand("/say uninstall 2", source())
    uninstall.size() == 3
  }

  @Test
  @Unroll("#action an inline process results in a compiler exception")
  public void "referencing an inline process results in a compiler exception"(String action) {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      ${action} other
    }
    inline process other {}
    """
    when:
    compile(programFile)

    then:
    CompilationFailedException ex = thrown()
    ex.errors.get(programFile)[0].message == "Cannot ${action} an inline process"
    ex.errors.get(programFile)[0].source.file == programFile
    ex.errors.get(programFile)[0].source.text == "other"
    ex.errors.get(programFile)[0].source.lineNumber == 3
    ex.errors.size() == 1

    where:
    action << ['start', 'stop', 'waitfor', 'intercept']
  }

  @Test
  @Unroll("#action an unknown process results in a compiler warning")
  public void "referencing an unknown process results in a compiler warning"(String action) {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      ${action} unknown
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.get(programFile)[0].message == "Could not resolve process unknown"
    result.warnings.get(programFile)[0].source.file == programFile
    result.warnings.get(programFile)[0].source.text == 'unknown'
    result.warnings.get(programFile)[0].source.lineNumber == 3
    result.warnings.size() == 1

    where:
    action << ['start', 'stop', 'intercept']
  }

  @Test
  public void "calling an unknown process results in a compiler warning"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      unknown()
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.get(programFile)[0].message == "Could not resolve process unknown"
    result.warnings.get(programFile)[0].source.file == programFile
    result.warnings.get(programFile)[0].source.text == 'unknown'
    result.warnings.get(programFile)[0].source.lineNumber == 3
    result.warnings.size() == 1
  }

  @Test
  public void "waiting for a process is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      waitfor other
    }

    remote process other {}
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "waiting for a notify is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      waitfor event
    }

    remote process other {
      notify event
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "waiting for a nested notify is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      waitfor event
    }

    remote process other {
      if: /testfor @e
      then {
        notify event
      } else {
        /say hi
      }
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "waiting for an unknown event results in a compiler warning"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      waitfor unknown
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.get(programFile)[0].message == "The event unknown is never triggered"
    result.warnings.get(programFile)[0].source.file == programFile
    result.warnings.get(programFile)[0].source.text == 'unknown'
    result.warnings.get(programFile)[0].source.lineNumber == 3
    result.warnings.size() == 1
  }

  @Test
  public void "notifying a waitfor event is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      notify event
    }

    remote process other {
      waitfor event
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "notifying a nested waitfor event is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      notify event
    }

    remote process other {
      if: /testfor @e
      then {
        waitfor event
      } else {
        /say hi
      }
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "notifying a call event is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      notify event
    }

    remote process other {
      event()
    }

    remote process event {}
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "notifying a nested call event is fine"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      notify event
    }

    remote process other {
      if: /testfor @e
      then {
        event()
      } else {
        /say hi
      }
    }

    remote process event {}
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.isEmpty()
  }

  @Test
  public void "notifying an unknown event results in a compiler warning"() {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      notify unknown
    }
    """
    when:
    MplCompilationResult result = compile(programFile)

    then:
    result.warnings.get(programFile)[0].message == "The event unknown is never used"
    result.warnings.get(programFile)[0].source.file == programFile
    result.warnings.get(programFile)[0].source.text == 'unknown'
    result.warnings.get(programFile)[0].source.lineNumber == 3
    result.warnings.size() == 1
  }

  @Test
  @Unroll("#action a selector is fine")
  public void "referencing a selector is fine"(String action) {
    given:
    File folder = tempFolder.root
    File programFile = new File(folder, 'main.mpl')
    programFile.text = """
    remote process main {
      ${action} @e[name=other]
    }
    """
    when:
    compile(programFile)

    then:
    notThrown Exception

    where:
    action << ['start', 'stop']
  }
}
