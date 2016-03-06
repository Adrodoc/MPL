/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl

import static de.adrodoc55.TestBase.someIdentifier

import org.junit.Test

import de.adrodoc55.minecraft.coordinate.Orientation3D;
import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException
import de.adrodoc55.minecraft.mpl.antlr.Include
import de.adrodoc55.minecraft.mpl.antlr.MplInterpreter

class MplCompilerSpec extends MplSpecBase {

  @Test
  public void "a process from the same file will be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main (
    /this is the main process
    )

    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "a process from a neighbour file will not be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main (
    /this is the main process
    )
    """
    new File(folder, 'second.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 1
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))
  }

  @Test
  public void "a process from a neighbour file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main (
    /this is the main process
    start other
    )
    """
    new File(folder, 'second.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "a process from an imported file will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"
    process main (
    /this is the main process
    )
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 1
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))
  }

  @Test
  public void "a process from an imported file will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder/newFile.mpl"
    process main (
    /this is the main process
    start other
    )
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "a process from an imported dir will not be included, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main (
    /this is the main process
    )
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 1
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))
  }

  @Test
  public void "a process from an imported dir will be included, if it is referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main (
    /this is the main process
    start other
    )
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "a process from the same file can be referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main (
    /this is the main process
    start other
    )

    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "project includes are processed correctly"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1}
    include "newFolder/newFile.mpl"
    include "newFolder2"
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process ${id2} (
    /this is the ${id2} process
    )
    """
    new File(folder, 'newFolder2').mkdirs()
    new File(folder, 'newFolder2/newFile2.mpl').text = """
    process ${id3} (
    /this is the ${id3} process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2

    CommandChain main = result.chains.find { it.name == id2 }
    main.commands.contains(new Command("/this is the ${id2} process"))

    CommandChain other = result.chains.find { it.name == id3 }
    other.commands.contains(new Command("/this is the ${id3} process"))
  }

  @Test
  public void "Eine Projekt mit Orientation erzeugt ein Projekt mit Orientation"() {
    given:
    String id1 = someIdentifier()
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${id1} (
    orientation = "zyx"
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.orientation == new Orientation3D('zyx')
  }

  @Test
  public void "Projekteinstellungen gelten nur, wenn die Datei direkt compiliert wird"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    project ${someIdentifier()} (
    include "other.mpl"
    )
    """
    new File(folder, 'other.mpl').text = """
    project ${someIdentifier()} (
    orientation = "z-yx"
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.orientation == new Orientation3D()
  }

  @Test
  public void "ambigious processes within imports will be ignored, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main (
    /this is the main process
    )
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other (
    /this is the other process
    )
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    process other (
    /this is the second other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 1
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))
  }

  @Test
  public void "ambigious processes throw an Exception, if referenced"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main (
    /this is the main process
    start other
    )
    """
    new File(folder, 'newFolder').mkdirs()
    File newFile = new File(folder, 'newFolder/newFile.mpl')
    newFile.text = """
    process other (
    /this is the other process
    )
    """
    File newFile2 = new File(folder, 'newFolder/newFile2.mpl')
    newFile2.text = """
    process other (
    /this is the second other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
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
    project main
    include "newFolder/newFile.mpl"
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process main (
    /this is the main process
    )
    process other (
    /this is the other process
    )
    """
    when:
    Program result = MplCompiler.assembleProgram(new File(folder, 'main.mpl'))
    then:
    result.chains.size() == 2
    CommandChain main = result.chains.find { it.name == 'main' }
    main.commands.contains(new Command('/this is the main process'))

    CommandChain other = result.chains.find { it.name == 'other' }
    other.commands.contains(new Command('/this is the other process'))
  }

  @Test
  public void "a script starting with impulse can be compiled"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    impulse: /say hi
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
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
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
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
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    notThrown Exception
  }

  @Test
  public void "a script does not have installation/uninstallation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    /say hi
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    chains.size() == 1
  }

  @Test
  public void "having an installation does not produce an uninstallation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install (
    /say install
    )
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    chains.size() == 2
    chains[0].name == null
    chains[1].name == 'install'
  }

  @Test
  public void "having an uninstallation also produces an installation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall (
    /say uninstall
    )
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    chains.size() == 3
    chains[0].name == null
    chains[1].name == 'install'
    chains[2].name == 'uninstall'
  }

  @Test
  public void "the commands of a custom installation are executed after the generated ones"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    install (
    /say install
    )

    process main () # If there is no process, there are no generated commands
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    CommandBlockChain installation = chains.find { it.name == 'install' }
    installation.commandBlocks.size() == 5
    installation.commandBlocks[0].toCommand() == null
    installation.commandBlocks[1].getCommand().startsWith('setblock ')
    installation.commandBlocks[2].getCommand().startsWith('summon ArmorStand ')
    installation.commandBlocks[3].toCommand() == new Command('say install')
    installation.commandBlocks[4].toCommand() == null
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
    uninstall (
    /say uninstall
    )

    process main () # If there is no process, there are no generated commands
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    CommandBlockChain uninstallation = chains.find { it.name == 'uninstall' }
    uninstallation.commandBlocks.size() == 5
    uninstallation.commandBlocks[0].toCommand() == null
    uninstallation.commandBlocks[1].getCommand().startsWith('setblock ')
    uninstallation.commandBlocks[2].toCommand() == new Command('say uninstall')
    uninstallation.commandBlocks[3].toCommand() == new Command('kill @e[type=ArmorStand,name=main]')
    uninstallation.commandBlocks[4].toCommand() == null
  }
}
