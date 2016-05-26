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
package de.adrodoc55.minecraft.mpl.interpretation

import static de.adrodoc55.minecraft.mpl.MplTestBase.someIdentifier
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY
import static de.adrodoc55.minecraft.mpl.commands.Conditional.*
import static de.adrodoc55.minecraft.mpl.commands.Mode.*

import org.junit.Test

import spock.lang.Unroll

import com.google.common.collect.ListMultimap

import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWhile
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram
import de.adrodoc55.minecraft.mpl.commands.Conditional

class MplInterpreterSpec2 extends MplSpecBase {

  static List commandOnlyModifier = ['impulse', 'chain', 'repeat', 'always active', 'needs redstone']

  @Test
  public void "Each file can only define one project"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    project ${id1} ()
    project ${id2} ()
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "A file can only contain a single project"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'project'
    program.exceptions[0].source.token.line == 2
    program.exceptions[1].message == "A file can only contain a single project"
    program.exceptions[1].source.file == lastTempFile
    program.exceptions[1].source.token.text == 'project'
    program.exceptions[1].source.token.line == 3
    program.exceptions.size() == 2
  }

  @Test
  public void "A project and processes can be defined in the same file"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    project ${id1} ()
    process ${id2} ()
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()
  }

  @Test
  public void "Each project can only have a single orientation"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    project ${id1} (
      orientation "zxy"
      orientation "z-xy"
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "A project can only have a single orientation"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'orientation'
    program.exceptions[0].source.token.line == 3
    program.exceptions[1].message == "A project can only have a single orientation"
    program.exceptions[1].source.file == lastTempFile
    program.exceptions[1].source.token.text == 'orientation'
    program.exceptions[1].source.token.line == 4
    program.exceptions.size() == 2
  }

  @Test
  public void "Each script can only have a single orientation"() {
    given:
    String programString = """
    orientation "zxy"
    orientation "z-xy"
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions[0].message == "A script can only have a single orientation"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'orientation'
    program.exceptions[0].source.token.line == 2
    program.exceptions[1].message == "A script can only have a single orientation"
    program.exceptions[1].source.file == lastTempFile
    program.exceptions[1].source.token.text == 'orientation'
    program.exceptions[1].source.token.line == 3
    program.exceptions.size() == 2
  }

  @Test
  public void "A file may not contain duplicate processes"() {
    given:
    String id = someIdentifier()
    String programString = """
    process ${id} (
    /say I am a process
    )

    process ${id} (
    /say I am the same process
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions[0].message == "Duplicate process ${id}"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == id
    program.exceptions[0].source.token.line == 2
    program.exceptions[1].message == "Duplicate process ${id}"
    program.exceptions[1].source.file == lastTempFile
    program.exceptions[1].source.token.text == id
    program.exceptions[1].source.token.line == 6
    program.exceptions.size() == 2
  }

  @Test
  public void "A file may contain multiple processes"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String id3 = someIdentifier()
    String programString = """
    process ${id1} (
    /say I am a default process
    )
    impulse process ${id2} (
    /say I am an impulse process, wich is actually equivalent to the default
    )
    repeat process ${id3} (
    /say I am a repeating process. I am completely different :)
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    Collection<MplProcess> processes = program.processes
    processes.size() == 3

    MplProcess process1 = processes.find { it.name == id1 }
    process1.repeating == false
    List<ChainPart> chainParts1 = process1.chainParts
    chainParts1[0] == new MplCommand("/say I am a default process")
    chainParts1.size() == 1

    MplProcess process2 = processes.find { it.name == id2 }
    process2.repeating == false
    List<ChainPart> chainParts2 = process2.chainParts
    chainParts2[0] == new MplCommand("/say I am an impulse process, wich is actually equivalent to the default")
    chainParts2.size() == 1

    MplProcess process3 = processes.find { it.name == id3 }
    process3.repeating == true
    List<ChainPart> chainParts3 = process3.chainParts
    chainParts3[0] == new MplCommand("/say I am a repeating process. I am completely different :)")
    chainParts3.size() == 1
  }

  @Test
  public void "Multiple install/uninstall blocks are concatenated"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    install (
      /say hi
    )

    install (
      /say hi2
    )

    uninstall (
      /say hi3
    )

    uninstall (
      /say hi4
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.install.chainParts[0] == new MplCommand('/say hi')
    program.install.chainParts[1] == new MplCommand('/say hi2')
    program.install.chainParts.size() == 2

    program.uninstall.chainParts[0] == new MplCommand('/say hi3')
    program.uninstall.chainParts[1] == new MplCommand('/say hi4')
    program.uninstall.chainParts.size() == 2
  }

  // ----------------------------------------------------------------------------------------------------
  //    ___                                 _        __  ___               _             _
  //   |_ _| _ __ ___   _ __    ___   _ __ | |_     / / |_ _| _ __    ___ | | _   _   __| |  ___
  //    | | | '_ ` _ \ | '_ \  / _ \ | '__|| __|   / /   | | | '_ \  / __|| || | | | / _` | / _ \
  //    | | | | | | | || |_) || (_) || |   | |_   / /    | | | | | || (__ | || |_| || (_| ||  __/
  //   |___||_| |_| |_|| .__/  \___/ |_|    \__| /_/    |___||_| |_| \___||_| \__,_| \__,_| \___|
  //                   |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  void "an interpreter will always include mpl subfiles of it's parent directory, including it's own file"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.mpl')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)

    expect:
    interpreter.imports.containsAll([programFile, neighbourFile])
    interpreter.imports.size() == 2
  }

  @Test
  void "an interpreter will not include non mpl subfiles of it's parent directory by default"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.txt')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)

    expect:
    interpreter.imports.containsAll([programFile])
    interpreter.imports.size() == 1
  }

  @Test
  void "addFileImport will add a file that is not in the same folder"() {
    given:
    File programFile = newTempFile()
    File otherFile = new File(programFile.parentFile, 'folder/other.txt')
    otherFile.parentFile.mkdirs()
    otherFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile)

    when:
    interpreter.addFileImport(null, otherFile)

    then:
    interpreter.imports.containsAll([programFile, otherFile])
    interpreter.imports.size() == 2
  }

  @Test
  void "the same file cannot be imported twice"() {
    given:
    String programString = """
    import "newFolder/newFile.txt"
    import "newFolder/newFile.txt"
    """
    File newFolder = new File(tempFolder.root, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.txt")
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == 'Duplicate import'
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == '"newFolder/newFile.txt"'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  public void "the same file cannot be included twice"() {
    given:
    String programString = """
    project main (
      include "newFolder/newFile.txt"
      include "newFolder/newFile.txt"
    )
    """
    File newFolder = new File(tempFolder.root, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.txt")
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == 'Duplicate include'
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == '"newFolder/newFile.txt"'
    program.exceptions[0].source.token.line == 4
    program.exceptions.size() == 1
  }

  @Test
  public void "a project can include files and directories"() {
    given:
    String id1 = someIdentifier()
    String programString = """
    project ${id1} (
      include "datei1.mpl"
      include "ordner2"
    )
    """
    File folder = tempFolder.root
    new File(folder, 'datei1.mpl').createNewFile()
    new File(folder, 'ordner2').mkdirs()
    new File(folder, 'ordner2/datei4.mpl').createNewFile()
    new File(folder, 'ordner2/datei5.txt').createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    File parent = lastTempFile.parentFile

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 2
    List<Include> includes = includeMap.get(null); // null indicates that the whole file should be included
    includes[0].files.containsAll([new File(parent, "datei1.mpl")])
    includes[0].files.size() == 1
    includes[0].processName == null
    includes[1].files.containsAll([new File(parent, "ordner2/datei4.mpl")])
    includes[1].files.size() == 1
    includes[1].processName == null
    includes.size() == 2
  }

  @Test
  public void "starting a foreign process creates an include"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
      start ${id2}
    )
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id2
    includes.size() == 1
  }

  @Test
  public void "starting a foreign process from a script does not create an include"() {
    given:
    String programString = """
    start other
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    interpreter.includes.isEmpty()
  }

  @Test
  public void "imported files are used for implicit includes"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFolder/newFile"

    process ${id1} (
      start ${id2}
    )
    """
    File file = newTempFile()
    File newFile = new File(file.parentFile, "newFolder/newFile")
    newFile.parentFile.mkdirs()
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString, file)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].files.size() == 2
    includes[0].processName == id2
    includes.size() == 1
  }
  @Test
  public void "the subfiles of imported directories are used for implicit includes"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    import "newFolder"

    process ${id1} (
      start ${id2}
    )
    """
    File file = newTempFile()
    File newFolder = new File(file.parentFile, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.mpl")
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString, file)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile, newFile])
    includes[0].files.size() == 2
    includes[0].processName == id2
    includes.size() == 1
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Includes
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "starting a process in the same file creates an include (process definition after call)"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
      start ${id2}
    )

    process ${id2} (
      /say I am the second process
    )
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id1);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id2
    includes.size() == 1
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Includes
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "starting a process in the same file creates an include (process definition before call)"() {
    given:
    String id1 = someIdentifier()
    String id2 = someIdentifier()
    String programString = """
    process ${id1} (
      /say I am a process
    )

    process ${id2} (
      start ${id1}
    )
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    ListMultimap<String, Include> includeMap = interpreter.includes
    includeMap.size() == 1
    List<Include> includes = includeMap.get(id2);
    includes[0].files.containsAll([lastTempFile])
    includes[0].files.size() == 1
    includes[0].processName == id1
    includes.size() == 1
  }

  // ----------------------------------------------------------------------------------------------------
  //    __  __             _  _   __  _
  //   |  \/  |  ___    __| |(_) / _|(_)  ___  _ __
  //   | |\/| | / _ \  / _` || || |_ | | / _ \| '__|
  //   | |  | || (_) || (_| || ||  _|| ||  __/| |
  //   |_|  |_| \___/  \__,_||_||_|  |_| \___||_|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("leading #conditional #command with identifier in script")
  public void "leading conditional/invert with identifier in script"(String conditional, String command) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${conditional}: ${command} ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'intercept']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command with identifier in process")
  public void "leading conditional/invert with identifier in process"(String conditional, String command) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${conditional}: ${command} ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'intercept']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command without identifier in script")
  public void "leading conditional/invert without identifier in script"(String conditional, String command) {
    given:
    String programString = """
    ${conditional}: ${command}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['breakpoint']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command without identifier in process")
  public void "leading conditional/invert without identifier in process"(String conditional, String command) {
    given:
    String programString = """
    process main (
      ${conditional}: ${command}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == conditional
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['notify', 'breakpoint']].combinations()*.flatten()
  }

  @Test
  public void "leading skip in repeating process"() {
    given:
    String testString = """
    repeat process main (
      skip
    )
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "skip cannot be the first command of a repeating process"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'skip'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  public void "conditional depending on skip throws exception"() {
    given:
    String testString = """
    skip
    conditional: /say conditional
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "conditional cannot depend on skip"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'conditional'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  public void "invert depending on skip throws exception"() {
    given:
    String testString = """
    skip
    invert: /say invert
    """
    when:
    MplInterpreter interpreter = interpret(testString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "invert cannot depend on skip"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'invert'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____   _                _
  //   / ___| | |_  __ _  _ __ | |_
  //   \___ \ | __|/ _` || '__|| __|
  //    ___) || |_| (_| || |   | |_
  //   |____/  \__|\__,_||_|    \__|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier start with identifier")
  public void "start with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplStart(identifier, modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "start with identifier in script"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplStart(identifier)
    process.chainParts.size() == 1
  }

  @Test
  @Unroll("start with illegal modifier: '#modifier'")
  public void "start with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____   _
  //   / ___| | |_  ___   _ __
  //   \___ \ | __|/ _ \ | '_ \
  //    ___) || |_| (_) || |_) |
  //   |____/  \__|\___/ | .__/
  //                     |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier stop with identifier")
  public void "stop with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} stop ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplStop(identifier, modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "stop without identifier in script"() {
    given:
    String programString = """
    stop
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Missing identifier"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'stop'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  public void "stop without identifier in repeat process"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat process ${identifier} (
      stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()
    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplStop(identifier)
    process.chainParts.size() == 1
  }

  @Test
  public void "stop without identifier in impulse process"() {
    given:
    String programString = """
    impulse process main (
      stop
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "An impulse process cannot be stopped"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'stop'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("stop with illegal modifier: '#modifier'")
  public void "stop with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process main (
      ${modifier}: stop ${identifier}
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for stop; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //   __        __      _  _     __
  //   \ \      / /__ _ (_)| |_  / _|  ___   _ __
  //    \ \ /\ / // _` || || __|| |_  / _ \ | '__|
  //     \ V  V /| (_| || || |_ |  _|| (_) || |
  //      \_/\_/  \__,_||_| \__||_|   \___/ |_|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier waitfor with identifier")
  public void "waitfor with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplWaitfor(identifier, modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "waitfor notify with identifier"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    waitfor notify ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWaitfor(identifier + NOTIFY);
    process.chainParts.size() == 1
  }

  @Test
  public void "waitfor without identifier after start"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplStart(identifier)
    process.chainParts[1] == new MplWaitfor(identifier + NOTIFY);
    process.chainParts.size() == 2
  }

  @Test
  public void "waitfor without identifier without start"() {
    given:
    String programString = """
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Missing identifier; no previous start was found to wait for"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'waitfor'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("waitfor with illegal modifier: '#modifier'")
  public void "waitfor with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for waitfor; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    _   _         _    _   __
  //   | \ | |  ___  | |_ (_) / _| _   _
  //   |  \| | / _ \ | __|| || |_ | | | |
  //   | |\  || (_) || |_ | ||  _|| |_| |
  //   |_| \_| \___/  \__||_||_|   \__, |
  //                               |___/
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier notify in process")
  public void "notify in process"(String modifier, Conditional conditional) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process ${identifier} (
      /say hi
      ${modifier} notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()
    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplNotify(identifier, modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "notify in script"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    notify
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Notify can only be used in a process"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'notify'
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1
  }

  @Test
  @Unroll("notify with illegal modifier: '#modifier'")
  public void "notify with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    process ${identifier} (
      ${modifier}: notify
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for notify; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ___         _                                _
  //   |_ _| _ __  | |_  ___  _ __  ___  ___  _ __  | |_
  //    | | | '_ \ | __|/ _ \| '__|/ __|/ _ \| '_ \ | __|
  //    | | | | | || |_|  __/| |  | (__|  __/| |_) || |_
  //   |___||_| |_| \__|\___||_|   \___|\___|| .__/  \__|
  //                                         |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier intercept with identifier")
  public void "intercept with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = someIdentifier()
    String programString = """
    /say hi
    ${modifier} intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplIntercept(identifier, modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("intercept with illegal modifier: '#modifier'")
  public void "intercept with illegal modifier"(String modifier) {
    given:
    String identifier = someIdentifier()
    String programString = """
    ${modifier}: intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for intercept; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____                     _                   _         _
  //   | __ )  _ __  ___   __ _ | | __ _ __    ___  (_) _ __  | |_
  //   |  _ \ | '__|/ _ \ / _` || |/ /| '_ \  / _ \ | || '_ \ | __|
  //   | |_) || |  |  __/| (_| ||   < | |_) || (_) || || | | || |_
  //   |____/ |_|   \___| \__,_||_|\_\| .__/  \___/ |_||_| |_| \__|
  //                                  |_|
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier breakpoint")
  public void "breakpoint"(String modifier, Conditional conditional) {
    given:
    String programString = """
    /say hi
    ${modifier} breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi')
    process.chainParts[1] == new MplBreakpoint("${lastTempFile.name} : line 3" , modifierBuffer, previous)
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("breakpoint with illegal modifier: '#modifier'")
  public void "breakpoint with illegal modifier"(String modifier) {
    given:
    String programString = """
    ${modifier}: breakpoint
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "Illegal modifier for breakpoint; only unconditional, conditional and invert are permitted"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == modifier
    program.exceptions[0].source.token.line == 2
    program.exceptions.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //    ___   __             _____  _                              _____  _
  //   |_ _| / _|           |_   _|| |__    ___  _ __             | ____|| | ___   ___
  //    | | | |_              | |  | '_ \  / _ \| '_ \            |  _|  | |/ __| / _ \
  //    | | |  _|  _  _  _    | |  | | | ||  __/| | | |  _  _  _  | |___ | |\__ \|  __/
  //   |___||_|   (_)(_)(_)   |_|  |_| |_| \___||_| |_| (_)(_)(_) |_____||_||___/ \___|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  public void "if with leading conditional in then"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
      conditional: /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'conditional'
    program.exceptions[0].source.token.line == 4
    program.exceptions.size() == 1
  }

  @Test
  public void "if with leading invert in then"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
      invert: /say then
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'invert'
    program.exceptions[0].source.token.line == 4
    program.exceptions.size() == 1
  }

  @Test
  public void "if with leading conditional in else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
    ) else (
      conditional: /say else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'conditional'
    program.exceptions[0].source.token.line == 5
    program.exceptions.size() == 1
  }

  @Test
  public void "if with leading invert in else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
    ) else (
      invert: /say else
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'invert'
    program.exceptions[0].source.token.line == 5
    program.exceptions.size() == 1
  }

  @Test
  public void "if then else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /say if
    then (
      /say then1
      /say then2
    ) else (
      /say else1
      /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(false, '/say if')
    process.chainParts.size() == 1

    MplIf mplIf = process.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1')
    mplIf.thenParts[1] == new MplCommand('/say then2')
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1')
    mplIf.elseParts[1] == new MplCommand('/say else2')
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "if not then else"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if not: /say if
    then (
      /say then1
      /say then2
    ) else (
      /say else1
      /say else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(true, '/say if')
    process.chainParts.size() == 1

    MplIf mplIf = process.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1')
    mplIf.thenParts[1] == new MplCommand('/say then2')
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1')
    mplIf.elseParts[1] == new MplCommand('/say else2')
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "nested if"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    if: /outer condition
    then (
      /say outer then1

      if: /inner then condition
      then (
        /say inner then then
      ) else (
        /say inner then else
      )

      /say outer then2
    ) else (
      /say outer else1

      if: /inner else condition
      then (
        /say inner else then
      ) else (
        /say inner else else
      )

      /say outer else2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(false, '/outer condition')
    process.chainParts.size() == 1

    MplIf outerIf = process.chainParts[0]
    outerIf.thenParts[0] == new MplCommand('/say outer then1')
    outerIf.thenParts[1] == new MplIf(false, '/inner then condition')
    outerIf.thenParts[2] == new MplCommand('/say outer then2')
    outerIf.thenParts.size() == 3
    outerIf.elseParts[0] == new MplCommand('/say outer else1')
    outerIf.elseParts[1] == new MplIf(false, '/inner else condition')
    outerIf.elseParts[2] == new MplCommand('/say outer else2')
    outerIf.elseParts.size() == 3

    MplIf innerThenIf = outerIf.thenParts[1]
    innerThenIf.thenParts[0] == new MplCommand('/say inner then then')
    innerThenIf.thenParts.size() == 1
    innerThenIf.elseParts[0] == new MplCommand('/say inner then else')
    innerThenIf.elseParts.size() == 1

    MplIf innerElseIf = outerIf.elseParts[1]
    innerElseIf.thenParts[0] == new MplCommand('/say inner else then')
    innerElseIf.thenParts.size() == 1
    innerElseIf.elseParts[0] == new MplCommand('/say inner else else')
    innerElseIf.elseParts.size() == 1
  }

  // ----------------------------------------------------------------------------------------------------
  //   __        __ _      _  _
  //   \ \      / /| |__  (_)| |  ___
  //    \ \ /\ / / | '_ \ | || | / _ \
  //     \ V  V /  | | | || || ||  __/
  //      \_/\_/   |_| |_||_||_| \___|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  public void "while repeat with leading conditional in repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    while: /say while
    repeat (
      conditional: /say repeat
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'conditional'
    program.exceptions[0].source.token.line == 4
    program.exceptions.size() == 1
  }

  @Test
  public void "while repeat with leading invert in repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    while: /say while
    repeat (
      invert: /say repeat
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'invert'
    program.exceptions[0].source.token.line == 4
    program.exceptions.size() == 1
  }

  @Test
  public void "repeat while with leading conditional in repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat (
      conditional: /say repeat
    ) do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'conditional'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  public void "repeat while with leading invert in repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat (
      invert: /say repeat
    ) do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program

    program.exceptions[0].message == "The first part of a chain must be unconditional"
    program.exceptions[0].source.file == lastTempFile
    program.exceptions[0].source.token.text == 'invert'
    program.exceptions[0].source.token.line == 3
    program.exceptions.size() == 1
  }

  @Test
  public void "while repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    while: /say while
    repeat (
      /say repeat1
      /say repeat2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, false, '/say while')
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1')
    mplWhile.chainParts[1] == new MplCommand('/say repeat2')
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "while not repeat"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    while not: /say while
    repeat (
      /say repeat1
      /say repeat2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(true, false, '/say while')
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1')
    mplWhile.chainParts[1] == new MplCommand('/say repeat2')
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat while"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat (
      /say repeat1
      /say repeat2
    ) do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, true, '/say while')
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1')
    mplWhile.chainParts[1] == new MplCommand('/say repeat2')
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat while not"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    repeat (
      /say repeat1
      /say repeat2
    ) do while not: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(true, true, '/say while')
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1')
    mplWhile.chainParts[1] == new MplCommand('/say repeat2')
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "nested while"() {
    given:
    String identifier = someIdentifier()
    String programString = """
    while: /outer condition
    repeat (
      /say outer repeat1

      while: /inner condition
      repeat (
        /say inner repeat
      )

      /say outer repeat2
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    program.exceptions.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, false, '/outer condition')
    process.chainParts.size() == 1

    MplWhile outerWhile = process.chainParts[0]
    outerWhile.chainParts[0] == new MplCommand('/say outer repeat1')
    outerWhile.chainParts[1] == new MplWhile(false, false, '/inner condition')
    outerWhile.chainParts[2] == new MplCommand('/say outer repeat2')
    outerWhile.chainParts.size() == 3

    MplWhile innerWhile = outerWhile.chainParts[1]
    innerWhile.chainParts[0] == new MplCommand('/say inner repeat')
    innerWhile.chainParts.size() == 1
  }

}
