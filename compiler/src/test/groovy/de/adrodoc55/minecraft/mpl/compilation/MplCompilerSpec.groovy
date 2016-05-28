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

import static de.adrodoc55.minecraft.mpl.MplTestBase.someIdentifier
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER

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
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command

class MplCompilerSpec extends MplSpecBase {

  @Test
  public void "a process from the same file will be included by default"() {
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
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a process from a neighbour file will not be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main {
    /this is the main process
    }
    """
    new File(folder, 'second.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))
  }

  @Test
  public void "a process from a neighbour file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main {
    /this is the main process
    start other
    }
    """
    new File(folder, 'second.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a process from an imported file will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"
    process main {
    /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))
  }

  @Test
  public void "a process from an imported file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"
    process main {
    /this is the main process
    start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a process from an imported dir will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main {
    /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))
  }

  @Test
  public void "a process from an imported dir will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main {
    /this is the main process
    start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a process from the same file can be referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main {
    /this is the main process
    start other
    }

    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a process from the same file is not ambigious with an imported process and will win"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main {
    /this is the main process
    start other
    }

    process other {
    /this is the other process in the same file
    }
    """
    new File(folder, 'other.mpl').text = """
    process other {
    /this is the other process from the other file
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process in the same file'))
  }

  @Test
  public void "a process that is referenced twice is not ambigious"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process p1 {
    /this is the p1 process
    start p3
    }

    process p2 {
    /this is the p2 process
    start p3
    }

    process p3 {
    /this is the p3 process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    notThrown Exception
    result.processes.size() == 3
    MplProcess p1 = result.processes.find { it.name == 'p1' }
    p1.chainParts.contains(new MplCommand('/this is the p1 process'))

    MplProcess p2 = result.processes.find { it.name == 'p2' }
    p2.chainParts.contains(new MplCommand('/this is the p2 process'))

    MplProcess p3 = result.processes.find { it.name == 'p3' }
    p3.chainParts.contains(new MplCommand('/this is the p3 process'))
  }

  @Test
  public void "a process in the main file, that is referenced from a different file is not ambigious"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'p1.mpl').text = """
    process p1 {
    /this is the p1 process
    start p2
    }

    process p3 {
    /this is the p3 process
    }
    """
    new File(folder, 'p2.mpl').text = """
    process p2 {
    /this is the p2 process
    start p3
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'p1.mpl'))
    then:
    notThrown Exception
    result.processes.size() == 3
    MplProcess p1 = result.processes.find { it.name == 'p1' }
    p1.chainParts.contains(new MplCommand('/this is the p1 process'))

    MplProcess p2 = result.processes.find { it.name == 'p2' }
    p2.chainParts.contains(new MplCommand('/this is the p2 process'))

    MplProcess p3 = result.processes.find { it.name == 'p3' }
    p3.chainParts.contains(new MplCommand('/this is the p3 process'))
  }

  @Test
  public void "project includes are processed correctly"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} {
    include "newFolder/newFile.mpl"
    include "newFolder2"
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process ${id2} {
    /this is the ${id2} process
    }
    """
    new File(folder, 'newFolder2').mkdirs()
    new File(folder, 'newFolder2/newFile2.mpl').text = """
    process ${id3} {
    /this is the ${id3} process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2

    MplProcess main = result.processes.find { it.name == id2 }
    main.chainParts.contains(new MplCommand("/this is the ${id2} process"))

    MplProcess other = result.processes.find { it.name == id3 }
    other.chainParts.contains(new MplCommand("/this is the ${id3} process"))
  }

  @Test
  public void "including two processes with the same name throws ambigious process Exception"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} {
    include "other1.mpl"
    include "other2.mpl"
    }
    """
    new File(folder, 'other1.mpl').text = """
    process ${id2} {
    /this is the ${id2} process
    }
    """
    new File(folder, 'other2.mpl').text = """
    process ${id2} {
    /this is the second ${id2} process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    CompilationFailedException ex = thrown()
    Collection<CompilerException> exs = ex.exceptions.values()
    exs[0].source.file == new File(folder, 'other1.mpl')
    exs[0].source.token.line == 2
    exs[0].source.token.text == id2
    exs[0].message == "Duplicate process ${id2}; was also found in ${new File(folder, 'other2.mpl')}"
    exs[1].source.file == new File(folder, 'other2.mpl')
    exs[1].source.token.line == 2
    exs[1].source.token.text == id2
    exs[1].message == "Duplicate process ${id2}; was also found in ${new File(folder, 'other1.mpl')}"
    exs.size() == 2
  }

  @Test
  public void "scripts can't be included"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} {
    include "newFolder/scriptFile.mpl"
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/scriptFile.mpl').text = """
    /this is a script
    / really !
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    CompilationFailedException ex = thrown()
    List<CompilerException> exs = ex.exceptions.get(new File(folder, 'main.mpl'))
    exs[0].source.file == new File(folder, 'main.mpl')
    exs[0].source.token.line == 3
    exs[0].source.token.text == '"newFolder/scriptFile.mpl"'
    exs[0].message == "Can't include script 'scriptFile.mpl'. Scripts may not be included."
    exs.size() == 1
  }

  @Test
  public void "Eine Projekt mit Orientation erzeugt ein Projekt mit Orientation"() {
    given:
    String id1 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} {
    orientation "zyx"
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.orientation == new Orientation3D('zyx')
  }

  @Test
  public void "Projekteinstellungen gelten nur, wenn die Datei direkt compiliert wird"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${someIdentifier()} {
    include "other.mpl"
    }
    """
    new File(folder, 'other.mpl').text = """
    project ${someIdentifier()} {
    orientation "z-yx"
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.orientation == new Orientation3D()
  }

  @Test
  public void "ambigious processes within imports will be ignored, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main {
    /this is the main process
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other {
    /this is the other process
    }
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    process other {
    /this is the second other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 1
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))
  }

  @Test
  public void "ambigious processes throw an Exception, if referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main {
    /this is the main process
    start other
    }
    """
    new File(folder, 'newFolder').mkdirs()
    File newFile = new File(folder, 'newFolder/newFile.mpl')
    newFile.text = """
    process other {
    /this is the other process
    }
    """
    File newFile2 = new File(folder, 'newFolder/newFile2.mpl')
    newFile2.text = """
    process other {
    /this is the second other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    CompilationFailedException ex = thrown()
    List<CompilerException> exs = ex.exceptions.get(new File(folder, 'main.mpl'))
    exs.size() == 1
    exs.first().message.startsWith "Process other is ambigious. It was found in "//'${newFile}' and '${newFile2}'"
  }

  @Test
  public void "all processes from an included file will be included"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project main {
    include "newFolder/newFile.mpl"
    }
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process main {
    /this is the main process
    }
    process other {
    /this is the other process
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.processes.size() == 2
    MplProcess main = result.processes.find { it.name == 'main' }
    main.chainParts.contains(new MplCommand('/this is the main process'))

    MplProcess other = result.processes.find { it.name == 'other' }
    other.chainParts.contains(new MplCommand('/this is the other process'))
  }

  @Test
  public void "a script starting with impulse can be compiled"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    impulse: /say hi
    """
    when:
    MplCompiler.compile(new File(folder, 'main.mpl'), new CompilerOptions())
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
    MplCompiler.compile(new File(folder, 'main.mpl'), new CompilerOptions())
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
    MplCompiler.compile(new File(folder, 'main.mpl'), new CompilerOptions())
    then:
    notThrown Exception
  }

  @Test
  public void "a script does not have installation/uninstallation by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    /say hi
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.install.chainParts.isEmpty()
    result.uninstall.chainParts.isEmpty()
  }

  @Test
  public void "having an installation does not produce an uninstallation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install {
      /say install
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.install.chainParts.size() == 1
    result.uninstall.chainParts.isEmpty()
    result.processes.size() == 1
    result.processes[0].chainParts.isEmpty()
  }

  @Test
  public void "having an uninstallation does not produce an installation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall {
      /say uninstall
    }
    """
    when:
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.install.chainParts.isEmpty()
    result.uninstall.chainParts.size() == 1
    result.processes.size() == 1
    result.processes[0].chainParts.isEmpty()
  }

  @Test
  public void "the commands of a custom installation are executed after the generated ones"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install {
      /say install
    }

    process main { # If there is no process, there are no generated commands
      /say hi
    }
    """
    when:
    MplProgram program = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    ChainContainer container = MplCompiler.materialize(program, new CompilerOptions(TRANSMITTER))
    List<CommandBlockChain> chains = MplCompiler.place(container, new CompilerOptions(TRANSMITTER))
    then:
    CommandBlockChain installation = chains.find { it.name == 'install' }
    installation.blocks.size() == 5
    installation.blocks[0].class == Transmitter
    installation.blocks[1].getCommand().startsWith('setblock ')
    installation.blocks[2].getCommand().startsWith('summon ArmorStand ')
    installation.blocks[3].toCommand() == new Command('say install')
    installation.blocks[4].class == AirBlock
  }

  /**
   * Allowing start of processes within uninstallation may cause problems if the started process
   * attempts to start more processes. The reason behind this is, that any newly started process is
   * executed 1 tick later at which point all processes will have been uninstalled.<br>
   * TODO Maybe the uninstallation should automatically insert a multi-waitfor after all custom
   * commands for every process that has been started, but is not waited for. This would also
   * require every process that could at least be indirectly called by the uninstallation to notify
   */
  @Test
  public void "the commands of a custom uninstallation are executed before the generated ones"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall {
    /say uninstall
    }

    process main { # If there is no process, there are no generated commands
      /say hi
    }
    """
    when:
    MplProgram program = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    ChainContainer container = MplCompiler.materialize(program, new CompilerOptions(TRANSMITTER))
    List<CommandBlockChain> chains = MplCompiler.place(container, new CompilerOptions(TRANSMITTER))
    then:
    CommandBlockChain uninstallation = chains.find { it.name == 'uninstall' }
    uninstallation.blocks.size() == 5
    uninstallation.blocks[0].class == Transmitter
    uninstallation.blocks[1].getCommand().startsWith('setblock ')
    uninstallation.blocks[2].toCommand() == new Command('say uninstall')
    uninstallation.blocks[3].getCommand().startsWith('kill @e[type=ArmorStand,tag=MPL')
    uninstallation.blocks[4].class == AirBlock
  }

  @Test
  public void "the installation and uninstallation of multiple files is concatenated"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project main {
      include "install1.mpl"
      include "install2.mpl"
    }

    install {
      /say main install
    }
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
    MplProgram result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    notThrown Exception

    List<ChainPart> install = result.install.chainParts
    install[0] == new MplCommand("/say main install")
    install[1] == new MplCommand("/say install 1")
    install[2] == new MplCommand("/say install 2")
    install.size() == 3
  }
}
