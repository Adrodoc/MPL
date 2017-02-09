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

import static de.adrodoc55.TestBase.$String
import static de.adrodoc55.TestBase.$int
import static de.adrodoc55.TestBase.some
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier
import static de.adrodoc55.minecraft.mpl.MplTestBase.$MplCompilerContext
import static de.adrodoc55.minecraft.mpl.ast.Conditional.*
import static de.adrodoc55.minecraft.mpl.ast.ProcessType.*
import static de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify.NOTIFY
import static de.adrodoc55.minecraft.mpl.commands.Mode.*

import org.antlr.v4.runtime.CommonToken
import org.junit.Test

import spock.lang.Unroll

import com.google.common.collect.SetMultimap

import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.ast.Conditional
import de.adrodoc55.minecraft.mpl.ast.chainparts.ChainPart
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplBreakpoint
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCall
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIntercept
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplNotify
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStart
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplStop
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplWaitfor
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplBreak
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplContinue
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram
import de.adrodoc55.minecraft.mpl.ast.variable.MplIntegerVariable
import de.adrodoc55.minecraft.mpl.ast.variable.MplStringVariable
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable
import de.adrodoc55.minecraft.mpl.ast.variable.selector.TargetSelector
import de.adrodoc55.minecraft.mpl.ast.variable.type.MplType
import de.adrodoc55.minecraft.mpl.ast.variable.value.MplScoreboardValue
import de.adrodoc55.minecraft.mpl.ast.variable.value.MplValue
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext
import de.adrodoc55.minecraft.mpl.compilation.MplSource

class MplInterpreterSpec2 extends MplSpecBase {

  static List commandOnlyModifier = ['impulse', 'chain', 'repeat', 'always active', 'needs redstone']

  MplSource source() {
    new MplSource(lastTempFile, "", new CommonToken(0))
  }

  @Test
  public void "Each file can only define one project"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    project ${id1} {}
    project ${id2} {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "A file can only contain a single project"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id1
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors[1].message == "A file can only contain a single project"
    lastContext.errors[1].source.file == lastTempFile
    lastContext.errors[1].source.text == id2
    lastContext.errors[1].source.lineNumber == 3
    lastContext.errors.size() == 2
  }

  @Test
  public void "A project and processes can be defined in the same file"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    project ${id1} {}
    process ${id2} {}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()
  }

  @Test
  public void "Each project can only have a single orientation"() {
    given:
    String id1 = some($Identifier())
    String programString = """
    project ${id1} {
      orientation "zxy"
      orientation "z-xy"
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "A project can only have a single orientation"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'orientation'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors[1].message == "A project can only have a single orientation"
    lastContext.errors[1].source.file == lastTempFile
    lastContext.errors[1].source.text == 'orientation'
    lastContext.errors[1].source.lineNumber == 4
    lastContext.errors.size() == 2
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
    lastContext.errors[0].message == "A script can only have a single orientation"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'orientation'
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors[1].message == "A script can only have a single orientation"
    lastContext.errors[1].source.file == lastTempFile
    lastContext.errors[1].source.text == 'orientation'
    lastContext.errors[1].source.lineNumber == 3
    lastContext.errors.size() == 2
  }

  @Test
  public void "Multiple install/uninstall blocks are concatenated"() {
    given:
    String id1 = some($Identifier())
    String programString = """
    install {
      /say hi
    }

    install {
      /say hi2
    }

    uninstall {
      /say hi3
    }

    uninstall {
      /say hi4
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.install.chainParts[0] == new MplCommand('/say hi', source())
    program.install.chainParts[1] == new MplCommand('/say hi2', source())
    program.install.chainParts.size() == 2

    program.uninstall.chainParts[0] == new MplCommand('/say hi3', source())
    program.uninstall.chainParts[1] == new MplCommand('/say hi4', source())
    program.uninstall.chainParts.size() == 2
  }

  // @formatter:off
  // ----------------------------------------------------------------------------------------------------
  //    ____
  //   |  _ \  _ __  ___    ___  ___  ___  ___
  //   | |_) || '__|/ _ \  / __|/ _ \/ __|/ __|
  //   |  __/ | |  | (_) || (__|  __/\__ \\__ \
  //   |_|    |_|   \___/  \___|\___||___/|___/
  //
  // ----------------------------------------------------------------------------------------------------
  // @formatter:on

  @Test
  public void "A file may not contain duplicate processes"() {
    given:
    String id = some($Identifier())
    String programString = """
    process ${id} {
    /say I am a process
    }

    process ${id} {
    /say I am the same process
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Duplicate process ${id}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors[1].message == "Duplicate process ${id}"
    lastContext.errors[1].source.file == lastTempFile
    lastContext.errors[1].source.text == id
    lastContext.errors[1].source.lineNumber == 6
    lastContext.errors.size() == 2
  }

  @Test
  public void "A file may contain multiple processes"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String id3 = some($Identifier())
    String programString = """
    process ${id1} {
      /say I am the first process
    }
    process ${id2} {
      /say I am the second process
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    Collection<MplProcess> processes = program.processes
    processes.size() == 2

    MplProcess process1 = processes.find { it.name == id1 }
    Deque<ChainPart> chainParts1 = process1.chainParts
    chainParts1[0] == new MplCommand("/say I am the first process", source())
    chainParts1.size() == 1

    MplProcess process2 = processes.find { it.name == id2 }
    Deque<ChainPart> chainParts2 = process2.chainParts
    chainParts2[0] == new MplCommand("/say I am the second process", source())
    chainParts2.size() == 1
  }

  @Test
  public void "A process may be inline or remote"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String id3 = some($Identifier())
    String programString = """
    process ${id1} {}

    inline process ${id2} {}

    remote process ${id3} {}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    Collection<MplProcess> processes = program.processes
    processes.size() == 3

    MplProcess process1 = processes.find { it.name == id1 }
    process1.type == INLINE

    MplProcess process2 = processes.find { it.name == id2 }
    process2.type == INLINE

    MplProcess process3 = processes.find { it.name == id3 }
    process3.type == REMOTE
  }

  @Test
  public void "A remote process may be impulse or repeat"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String id3 = some($Identifier())
    String programString = """
    remote process ${id1} {}

    impulse process ${id2} {}

    repeat process ${id3} {}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    Collection<MplProcess> processes = program.processes
    processes.size() == 3

    MplProcess process1 = processes.find { it.name == id1 }
    process1.repeating == false

    MplProcess process2 = processes.find { it.name == id2 }
    process2.repeating == false

    MplProcess process3 = processes.find { it.name == id3 }
    process3.repeating == true
  }

  @Test
  @Unroll("An inline #repeat process is not allowed")
  public void "An inline repeat process is not allowed"(String repeat) {
    given:
    String id1 = some($Identifier())
    String programString = """
    inline ${repeat} process ${id1} {}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal combination of modifiers for the process ${id1}; only one of inline, impulse, or repeat is permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == repeat
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    repeat << ['impulse', 'repeat']
  }

  @Test
  public void "a process can have multiple tags"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    #Tag1
    #Test
    #AnotherTag
    process main {
      /say hi
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()
    process.tags[0] == 'Tag1'
    process.tags[1] == 'Test'
    process.tags[2] == 'AnotherTag'
    process.tags.size() == 3
  }

  public void "a process can be called"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    process main {
      other()
    }
    process other {}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 2
    MplProcess process = program.processes.find { it.name == 'main' }
    process.chainParts[0] == new MplCall('other', source())
    process.chainParts.size() == 1
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
  void "an interpreter will always import mpl subfiles of it's parent directory, including it's own file"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.mpl')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile, some($MplCompilerContext()))

    expect:
    interpreter.imports.containsAll([programFile, neighbourFile])
    interpreter.imports.size() == 2
  }

  @Test
  void "an interpreter will not import non mpl subfiles of it's parent directory by default"() {
    given:
    File programFile = newTempFile()
    File neighbourFile = new File(programFile.parentFile, 'neighbour.txt')
    neighbourFile.createNewFile()
    MplInterpreter interpreter = new MplInterpreter(programFile, some($MplCompilerContext()))

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
    MplInterpreter interpreter = new MplInterpreter(programFile, some($MplCompilerContext()))

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
    lastContext.errors[0].message == 'Duplicate import'
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == '"newFolder/newFile.txt"'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "the same file cannot be included twice"() {
    given:
    String programString = """
    include "newFolder/newFile.txt"
    include "newFolder/newFile.txt"
    """
    File newFolder = new File(tempFolder.root, "newFolder")
    newFolder.mkdirs()
    File newFile = new File(newFolder, "newFile.txt")
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == 'Duplicate include'
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == '"newFolder/newFile.txt"'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "files and directories can be included"() {
    given:
    String programString = """
    include "datei1.mpl"
    include "ordner2"
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
    lastContext.errors.isEmpty()

    File parent = lastTempFile.parentFile

    lastContext.toInclude[0].processName == null
    lastContext.toInclude[0].file == new File(parent, "datei1.mpl")
    lastContext.toInclude[0].source.file == lastTempFile
    lastContext.toInclude[1].processName == null
    lastContext.toInclude[1].file == new File(parent, "ordner2/datei4.mpl")
    lastContext.toInclude[1].source.file == lastTempFile
    lastContext.toInclude.size() == 2
  }

  @Test
  public void "calling a foreign process creates a reference"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    process ${id1} {
      ${id2}()
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id1);
    references[0].imports.containsAll([lastTempFile])
    references[0].imports.size() == 1
    references[0].processName == id2
    references.size() == 1
    referenceMap.size() == 1
  }

  @Test
  public void "starting a foreign process creates a reference"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    process ${id1} {
      start ${id2}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id1);
    references[0].imports.containsAll([lastTempFile])
    references[0].imports.size() == 1
    references[0].processName == id2
    references.size() == 1
    referenceMap.size() == 1
  }

  @Test
  public void "dynamically starting a foreign process does not create a reference"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    process ${id1} {
      start @e[name=${id2}]
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    interpreter.references.isEmpty()
  }

  @Test
  public void "starting a foreign process from a script does not create a reference"() {
    given:
    String programString = """
    start other
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    interpreter.references.isEmpty()
  }

  @Test
  public void "imported files are used for references"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    import "newFolder/newFile"

    process ${id1} {
      start ${id2}
    }
    """
    File file = newTempFile()
    File newFile = new File(file.parentFile, "newFolder/newFile")
    newFile.parentFile.mkdirs()
    newFile.createNewFile()

    when:
    MplInterpreter interpreter = interpret(programString, file)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id1);
    references[0].imports.containsAll([lastTempFile, newFile])
    references[0].imports.size() == 2
    references[0].processName == id2
    references.size() == 1
    referenceMap.size() == 1
  }
  @Test
  public void "the subfiles of imported directories are used for references"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    import "newFolder"

    process ${id1} {
      start ${id2}
    }
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
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id1);
    references[0].imports.containsAll([lastTempFile, newFile])
    references[0].imports.size() == 2
    references[0].processName == id2
    references.size() == 1
    referenceMap.size() == 1
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Referenzen
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "starting a process in the same file creates a reference (process definition after call)"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    process ${id1} {
      start ${id2}
    }

    process ${id2} {
      /say I am the second process
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id1);
    references[0].imports.containsAll([lastTempFile])
    references[0].imports.size() == 1
    references[0].processName == id2
    references.size() == 1
    referenceMap.size() == 1
  }

  /**
   * Grund hierfür ist: die Abhängigkeiten eines jeden Prozesses müssen durch die Referenzen
   * dokumentiert werden, da bei imports nur einzelne Prozesse includiert werden und deren
   * Abhängigkeiten sonst verloren gehen würden.
   */
  @Test
  public void "starting a process in the same file creates a reference (process definition before call)"() {
    given:
    String id1 = some($Identifier())
    String id2 = some($Identifier())
    String programString = """
    process ${id1} {
      /say I am a process
    }

    process ${id2} {
      start ${id1}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    SetMultimap<String, MplReference> referenceMap = interpreter.references
    Set<MplReference> references = referenceMap.get(id2);
    references[0].imports.containsAll([lastTempFile])
    references[0].imports.size() == 1
    references[0].processName == id1
    references.size() == 1
    referenceMap.size() == 1
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
    String identifier = some($Identifier())
    String programString = """
    ${conditional}: ${command} ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == conditional
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'notify', 'intercept']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command with identifier in process")
  public void "leading conditional/invert with identifier in process"(String conditional, String command) {
    given:
    String identifier = some($Identifier())
    String programString = """
    process main {
      ${conditional}: ${command} ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == conditional
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['start', 'stop', 'waitfor', 'notify', 'intercept']].combinations()*.flatten()
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
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == conditional
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['breakpoint']].combinations()*.flatten()
  }

  @Test
  @Unroll("leading #conditional #command without identifier in remote process")
  public void "leading conditional/invert without identifier in remote process"(String conditional, String command) {
    given:
    String programString = """
    remote process main {
      ${conditional}: ${command}
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == conditional
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    [conditional, command]<< [['conditional', 'invert'], ['breakpoint']].combinations()*.flatten()
  }

  @Test
  public void "leading skip in repeating process"() {
    given:
    String testString = """
    remote repeat process main {
      skip
    }
    """
    when:
    MplInterpreter interpreter = interpret(testString)

    then:
    lastContext.errors[0].message == "skip cannot be the first command of a repeating process"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'skip'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
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
    lastContext.errors[0].message == "conditional cannot depend on skip"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'conditional'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
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
    lastContext.errors[0].message == "invert cannot depend on skip"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'invert'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
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
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplStart("@e[name=${identifier}]", modifierBuffer, previous, source())
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("start identifier with illegal modifier: '#modifier'")
  public void "start identifier with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${modifier}: start ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  @Test
  @Unroll("#modifier start with selector")
  public void "start with selector"(String modifier, Conditional conditional) {
    given:
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} start @e[name=${identifier}]
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplStart("@e[name=${identifier}]", modifierBuffer, previous, source())
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("start selector with illegal modifier: '#modifier'")
  public void "start selector with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${modifier}: start @e[name=${identifier}]
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for start; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

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
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} stop ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplStop("@e[name=${identifier}]", modifierBuffer, previous, source())
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
    lastContext.errors[0].message == "Missing identifier"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'stop'
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1
  }

  @Test
  public void "stop without identifier in repeat process"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    remote repeat process ${identifier} {
      stop
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()
    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplStop("@e[name=${identifier}]", source())
    process.chainParts.size() == 1
  }

  @Test
  public void "stop without identifier in impulse process"() {
    given:
    String programString = """
    impulse process main {
      stop
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "An impulse process cannot be stopped"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'stop'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  @Unroll("stop identifier with illegal modifier: '#modifier'")
  public void "stop identifier with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    process main {
      ${modifier}: stop ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for stop; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  @Test
  @Unroll("#modifier stop with selector")
  public void "stop with selector"(String modifier, Conditional conditional) {
    given:
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} stop @e[name=${identifier}]
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplStop("@e[name=${identifier}]", modifierBuffer, previous, source())
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("stop selector with illegal modifier: '#modifier'")
  public void "stop selector with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    process main {
      ${modifier}: stop @e[name=${identifier}]
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for stop; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

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
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplWaitfor(identifier, modifierBuffer, previous, source())
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "waitfor without identifier after start"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    start ${identifier}
    waitfor
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplStart("@e[name=${identifier}]", source())
    process.chainParts[1] == new MplWaitfor(identifier, source());
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
    lastContext.errors[0].message == "Missing identifier; no previous start was found to wait for"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'waitfor'
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1
  }

  @Test
  @Unroll("waitfor with illegal modifier: '#modifier'")
  public void "waitfor with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${modifier}: waitfor ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for waitfor; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

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
  @Unroll("#modifier notify in remote process")
  public void "notify with identifer"(String modifier, Conditional conditional) {
    given:
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} notify ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()
    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplNotify(identifier, modifierBuffer, previous, source())
    process.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("notify with illegal modifier: '#modifier'")
  public void "notify with illegal modifier"(String modifier) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${modifier}: notify ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for notify; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

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
    String identifier = some($Identifier())
    String programString = """
    /say hi
    ${modifier} intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplIntercept(identifier, modifierBuffer, previous, source())
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
    String identifier = some($Identifier())
    String programString = """
    ${modifier}: intercept ${identifier}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for intercept; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

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
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);
    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = process.chainParts[0]
    }

    process.chainParts[0] == new MplCommand('/say hi', source())
    process.chainParts[1] == new MplBreakpoint("${lastTempFile.name} : line 3" , modifierBuffer, previous, source())
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
    lastContext.errors[0].message == "Illegal modifier for breakpoint; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

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
    String identifier = some($Identifier())
    String programString = """
    if: /say if
    then {
      conditional: /say then
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'conditional'
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "if with leading invert in then"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if: /say if
    then {
      invert: /say then
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'invert'
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "if with leading conditional in else"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if: /say if
    then {
    } else {
      conditional: /say else
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'conditional'
    lastContext.errors[0].source.lineNumber == 5
    lastContext.errors.size() == 1
  }

  @Test
  public void "if with leading invert in else"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if: /say if
    then {
    } else {
      invert: /say else
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'invert'
    lastContext.errors[0].source.lineNumber == 5
    lastContext.errors.size() == 1
  }

  @Test
  public void "if then else"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if: /say if
    then {
      /say then1
      /say then2
    } else {
      /say else1
      /say else2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(false, '/say if', source())
    process.chainParts.size() == 1

    MplIf mplIf = process.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1', source())
    mplIf.thenParts[1] == new MplCommand('/say then2', source())
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1', source())
    mplIf.elseParts[1] == new MplCommand('/say else2', source())
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "if not then else"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if not: /say if
    then {
      /say then1
      /say then2
    } else {
      /say else1
      /say else2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(true, '/say if', source())
    process.chainParts.size() == 1

    MplIf mplIf = process.chainParts[0]
    mplIf.thenParts[0] == new MplCommand('/say then1', source())
    mplIf.thenParts[1] == new MplCommand('/say then2', source())
    mplIf.thenParts.size() == 2
    mplIf.elseParts[0] == new MplCommand('/say else1', source())
    mplIf.elseParts[1] == new MplCommand('/say else2', source())
    mplIf.elseParts.size() == 2
  }

  @Test
  public void "nested if"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    if: /outer condition
    then {
      /say outer then1

      if: /inner then condition
      then {
        /say inner then then
      } else {
        /say inner then else
      }

      /say outer then2
    } else {
      /say outer else1

      if: /inner else condition
      then {
        /say inner else then
      } else {
        /say inner else else
      }

      /say outer else2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplIf(false, '/outer condition', source())
    process.chainParts.size() == 1

    MplIf outerIf = process.chainParts[0]
    outerIf.thenParts[0] == new MplCommand('/say outer then1', source())
    outerIf.thenParts[1] == new MplIf(false, '/inner then condition', source())
    outerIf.thenParts[2] == new MplCommand('/say outer then2', source())
    outerIf.thenParts.size() == 3
    outerIf.elseParts[0] == new MplCommand('/say outer else1', source())
    outerIf.elseParts[1] == new MplIf(false, '/inner else condition', source())
    outerIf.elseParts[2] == new MplCommand('/say outer else2', source())
    outerIf.elseParts.size() == 3

    MplIf innerThenIf = outerIf.thenParts[1]
    innerThenIf.thenParts[0] == new MplCommand('/say inner then then', source())
    innerThenIf.thenParts.size() == 1
    innerThenIf.elseParts[0] == new MplCommand('/say inner then else', source())
    innerThenIf.elseParts.size() == 1

    MplIf innerElseIf = outerIf.elseParts[1]
    innerElseIf.thenParts[0] == new MplCommand('/say inner else then', source())
    innerElseIf.thenParts.size() == 1
    innerElseIf.elseParts[0] == new MplCommand('/say inner else else', source())
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
    String identifier = some($Identifier())
    String programString = """
    while: /say while
    repeat {
      conditional: /say repeat
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'conditional'
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "while repeat with leading invert in repeat"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    while: /say while
    repeat {
      invert: /say repeat
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'invert'
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "repeat while with leading conditional in repeat"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    repeat {
      conditional: /say repeat
    } do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'conditional'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "repeat while with leading invert in repeat"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    repeat {
      invert: /say repeat
    } do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "The first part of a chain must be unconditional"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'invert'
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "repeat"() {
    given:
    String programString = """
    repeat {
      /say repeat1
      /say repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat with label"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${identifier}: repeat {
      /say repeat1
      /say repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(identifier, false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "while repeat"() {
    given:
    String programString = """
    while: /say while
    repeat {
      /say repeat1
      /say repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, false, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "while repeat with label"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${identifier}: while: /say while
    repeat {
      /say repeat1
      /say repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(identifier, false, false, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "while not repeat"() {
    given:
    String programString = """
    while not: /say while
    repeat {
      /say repeat1
      /say repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(true, false, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat while"() {
    given:
    String programString = """
    repeat {
      /say repeat1
      /say repeat2
    } do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, true, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat while with label"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${identifier}: repeat {
      /say repeat1
      /say repeat2
    } do while: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(identifier, false, true, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "repeat while not"() {
    given:
    String programString = """
    repeat {
      /say repeat1
      /say repeat2
    } do while not: /say while
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(true, true, '/say while', source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]
    mplWhile.chainParts[0] == new MplCommand('/say repeat1', source())
    mplWhile.chainParts[1] == new MplCommand('/say repeat2', source())
    mplWhile.chainParts.size() == 2
  }

  @Test
  public void "nested while"() {
    given:
    String identifier = some($Identifier())
    String programString = """
    while: /outer condition
    repeat {
      /say outer repeat1

      while: /inner condition
      repeat {
        /say inner repeat
      }

      /say outer repeat2
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    process.chainParts[0] == new MplWhile(false, false, '/outer condition', source())
    process.chainParts.size() == 1

    MplWhile outerWhile = process.chainParts[0]
    outerWhile.chainParts[0] == new MplCommand('/say outer repeat1', source())
    outerWhile.chainParts[1] == new MplWhile(false, false, '/inner condition', source())
    outerWhile.chainParts[2] == new MplCommand('/say outer repeat2', source())
    outerWhile.chainParts.size() == 3

    MplWhile innerWhile = outerWhile.chainParts[1]
    innerWhile.chainParts[0] == new MplCommand('/say inner repeat', source())
    innerWhile.chainParts.size() == 1
  }

  // ----------------------------------------------------------------------------------------------------
  //    ____                     _
  //   | __ )  _ __  ___   __ _ | | __
  //   |  _ \ | '__|/ _ \ / _` || |/ /
  //   | |_) || |  |  __/| (_| ||   <
  //   |____/ |_|   \___| \__,_||_|\_\
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier break with identifier")
  public void "break with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${identifier}: repeat {
      /say hi
      ${modifier} break ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);

    process.chainParts[0] == new MplWhile(false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]

    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = mplWhile.chainParts[0]
    }

    mplWhile.chainParts[0] == new MplCommand('/say hi', source())
    mplWhile.chainParts[1] == new MplBreak(identifier, mplWhile, modifierBuffer, previous, source())
    mplWhile.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("#modifier break without identifier")
  public void "break without identifier"(String modifier, Conditional conditional) {
    given:
    String programString = """
    repeat {
      /say hi
      ${modifier} break
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);

    process.chainParts[0] == new MplWhile(false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]

    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = mplWhile.chainParts[0]
    }

    mplWhile.chainParts[0] == new MplCommand('/say hi', source())
    mplWhile.chainParts[1] == new MplBreak(null, mplWhile, modifierBuffer, previous, source())
    mplWhile.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "break outside of loop"() {
    given:
    String testString = """
    break
    """
    when:
    MplInterpreter interpreter = interpret(testString)

    then:
    lastContext.errors[0].message == "break can only be used in a loop"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'break'
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1
  }

  @Test
  public void "break with missing label"() {
    given:
    String identifier = some($Identifier())
    String testString = """
    repeat {
      break ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(testString)

    then:
    lastContext.errors[0].message == "Missing label ${identifier}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == identifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  @Unroll("break with illegal modifier: '#modifier'")
  public void "break with illegal modifier"(String modifier) {
    given:
    String programString = """
    repeat {
      ${modifier}: break
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for break; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  // ----------------------------------------------------------------------------------------------------
  //     ____               _    _
  //    / ___| ___   _ __  | |_ (_) _ __   _   _   ___
  //   | |    / _ \ | '_ \ | __|| || '_ \ | | | | / _ \
  //   | |___| (_) || | | || |_ | || | | || |_| ||  __/
  //    \____|\___/ |_| |_| \__||_||_| |_| \__,_| \___|
  //
  // ----------------------------------------------------------------------------------------------------

  @Test
  @Unroll("#modifier continue with identifier")
  public void "continue with identifier"(String modifier, Conditional conditional) {
    given:
    String identifier = some($Identifier())
    String programString = """
    ${identifier}: repeat {
      /say hi
      ${modifier} continue ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);

    process.chainParts[0] == new MplWhile(false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]

    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = mplWhile.chainParts[0]
    }

    mplWhile.chainParts[0] == new MplCommand('/say hi', source())
    mplWhile.chainParts[1] == new MplContinue(identifier, mplWhile, modifierBuffer, previous, source())
    mplWhile.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  @Unroll("#modifier continue without identifier")
  public void "continue without identifier"(String modifier, Conditional conditional) {
    given:
    String programString = """
    repeat {
      /say hi
      ${modifier} continue
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProgram program = interpreter.program
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    ModifierBuffer modifierBuffer = new ModifierBuffer()
    modifierBuffer.setConditional(conditional);

    process.chainParts[0] == new MplWhile(false, false, null, source())
    process.chainParts.size() == 1

    MplWhile mplWhile = process.chainParts[0]

    ChainPart previous = null
    if (conditional != UNCONDITIONAL) {
      previous = mplWhile.chainParts[0]
    }

    mplWhile.chainParts[0] == new MplCommand('/say hi', source())
    mplWhile.chainParts[1] == new MplContinue(null, mplWhile, modifierBuffer, previous, source())
    mplWhile.chainParts.size() == 2
    where:
    modifier        | conditional
    ''              | UNCONDITIONAL
    'unconditional:'| UNCONDITIONAL
    'conditional:'  | CONDITIONAL
    'invert:'       | INVERT
  }

  @Test
  public void "continue outside of loop"() {
    given:
    String testString = """
    continue
    """
    when:
    MplInterpreter interpreter = interpret(testString)

    then:
    lastContext.errors[0].message == "continue can only be used in a loop"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == 'continue'
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1
  }

  @Test
  public void "continue with missing label"() {
    given:
    String identifier = some($Identifier())
    String testString = """
    repeat {
      continue ${identifier}
    }
    """
    when:
    MplInterpreter interpreter = interpret(testString)

    then:
    lastContext.errors[0].message == "Missing label ${identifier}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == identifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  @Unroll("continue with illegal modifier: '#modifier'")
  public void "continue with illegal modifier"(String modifier) {
    given:
    String programString = """
    repeat {
      ${modifier}: continue
    }
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Illegal modifier for continue; only unconditional, conditional and invert are permitted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == modifier
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    modifier << commandOnlyModifier
  }

  @Test
  public void "Declaring an Integer variable"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    Integer ${id} = ${value}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.variableScope
    MplIntegerVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a Selector variable"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    Selector ${id} = ${value}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.variableScope
    MplVariable<TargetSelector> variable = scope.findVariable(id)
    variable != null
    variable.value instanceof TargetSelector
    variable.value.toString() == value
  }

  @Test
  public void "Declaring a String variable"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    String ${id} = "${value}"
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.variableScope
    MplStringVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector}  ${scoreboard}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.variableScope
    MplVariable<MplValue> variable = scope.findVariable(id)
    variable != null
    MplScoreboardValue value = variable.value
    value.selector instanceof TargetSelector
    value.selector.toString() == selector
    value.scoreboard == scoreboard
  }

  @Test
  @Unroll("Type mismatch from #actualType to #declaredType")
  public void "Type mismatch at variable declaration"(MplType declaredType, MplType actualType, String value) {
    given:
    String id = some($Identifier())
    String programString = """
    ${declaredType} ${id} = ${value}
    """
    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    interpreter.variableScope.variables.isEmpty()

    lastContext.errors[0].message == "Type mismatch: cannot convert from ${actualType} to ${declaredType}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == value
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    [declaredType, actualType]<< typeCombinations()
    value = valueForType(actualType)
  }

  private List<List<MplType>> typeCombinations() {
    List<List<MplType>> result = [MplType.values(), MplType.values()].combinations()
    result.removeIf { List<MplType> list -> list[0] == list[1] }
    result.removeIf { List<MplType> list -> list[0] == MplType.VALUE && list[1] == MplType.INTEGER }
    return result
  }

  private String valueForType(MplType type) {
    if (type == MplType.INTEGER) {
      return String.valueOf(some($int()))
    } else if (type == MplType.SELECTOR) {
      return '@e'
    } else if (type == MplType.STRING) {
      return '"' + some($String()) + '"'
    } else if (type == MplType.VALUE) {
      return '@e ' + some($Identifier())
    }
  }
}
