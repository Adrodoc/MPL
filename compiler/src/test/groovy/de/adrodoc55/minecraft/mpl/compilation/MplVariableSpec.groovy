package de.adrodoc55.minecraft.mpl.compilation;

import static de.adrodoc55.TestBase.$String
import static de.adrodoc55.TestBase.$int
import static de.adrodoc55.TestBase.some
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier
import static org.apache.commons.io.FilenameUtils.getBaseName

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplSpecBase;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplIf
import de.adrodoc55.minecraft.mpl.ast.chainparts.loop.MplWhile
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProcess
import de.adrodoc55.minecraft.mpl.ast.chainparts.program.MplProgram
import de.adrodoc55.minecraft.mpl.ast.variable.MplIntegerVariable
import de.adrodoc55.minecraft.mpl.ast.variable.MplStringVariable
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable
import de.adrodoc55.minecraft.mpl.ast.variable.selector.TargetSelector
import de.adrodoc55.minecraft.mpl.ast.variable.type.MplType;
import de.adrodoc55.minecraft.mpl.ast.variable.value.MplScoreboardValue
import de.adrodoc55.minecraft.mpl.ast.variable.value.MplValue
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption
import de.adrodoc55.minecraft.mpl.interpretation.MplInterpreter
import de.adrodoc55.minecraft.mpl.interpretation.VariableScope
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion
import spock.lang.Unroll;

public class MplVariableSpec extends MplSpecBase {
  File lastProgramFile

  private MplProgram assembleProgram(String program, File file = newTempFile(), CompilerOption... options) {
    file.text = program
    assembleProgram(file, options)
  }

  private MplProgram assembleProgram(File programFile, CompilerOption... options) {
    lastProgramFile = programFile
    MplCompiler compiler = new MplCompiler(MinecraftVersion.getDefault(), new CompilerOptions(options))
    lastContext = compiler.provideContext()
    compiler.assemble(programFile)
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

  @Test
  @Unroll("Type mismatch at local script variable declaration from #actualType to #declaredType")
  public void "Type mismatch at local script variable declaration"(MplType declaredType, MplType actualType, String value) {
    given:
    String id = some($Identifier())
    String programString = """
    ${declaredType} ${id} = ${value}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    interpreter.rootVariableScope.variables.isEmpty()

    lastContext.errors[0].message == "Type mismatch: cannot convert from ${actualType} to ${declaredType}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == value
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    [declaredType, actualType]<< typeCombinations()
    value = valueForType(actualType)
  }

  @Test
  @Unroll("Type mismatch at global variable declaration from #actualType to #declaredType")
  public void "Type mismatch at global variable declaration"(MplType declaredType, MplType actualType, String value) {
    given:
    String id = some($Identifier())
    String programString = """
    ${declaredType} ${id} = ${value}
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    interpreter.rootVariableScope.variables.isEmpty()

    lastContext.errors[0].message == "Type mismatch: cannot convert from ${actualType} to ${declaredType}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == value
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1

    where:
    [declaredType, actualType]<< typeCombinations()
    value = valueForType(actualType)
  }

  @Test
  @Unroll("Type mismatch at local variable declaration from #actualType to #declaredType")
  public void "Type mismatch at local variable declaration"(MplType declaredType, MplType actualType, String value) {
    given:
    String id = some($Identifier())
    String programString = """
    impulse process main {
      ${declaredType} ${id} = ${value}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    interpreter.rootVariableScope.variables.isEmpty()

    lastContext.errors[0].message == "Type mismatch: cannot convert from ${actualType} to ${declaredType}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == value
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1

    where:
    [declaredType, actualType]<< typeCombinations()
    value = valueForType(actualType)
  }

  @Test
  public void "Declaring a duplicate local script variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    Integer ${id} = ${some($int())}
    Integer ${id} = ${some($int())}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Duplicate variable ${id}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Declaring a local script Integer variable"() {
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

    VariableScope scope = interpreter.rootVariableScope
    MplIntegerVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a local script Selector variable"() {
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

    VariableScope scope = interpreter.rootVariableScope
    MplVariable<TargetSelector> variable = scope.findVariable(id)
    variable != null
    variable.value instanceof TargetSelector
    variable.value.toString() == value
  }

  @Test
  public void "Declaring a local script String variable"() {
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

    VariableScope scope = interpreter.rootVariableScope
    MplStringVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a local script Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector} ${scoreboard}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.rootVariableScope
    MplVariable<MplValue> variable = scope.findVariable(id)
    variable != null
    MplScoreboardValue value = variable.value
    value.selector instanceof TargetSelector
    value.selector.toString() == selector
    value.scoreboard == scoreboard
  }

  @Test
  public void "Inserting a local script Integer variable"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    Integer ${id} = ${value}
    /say The value is \${${id}}!
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local script Selector variable"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    Selector ${id} = ${value}
    /say The value is \${${id}}!
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local script String variable"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    String ${id} = "${value}"
    /say The value is \${${id}}!
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local script Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector} ${scoreboard}
    /say The value is \${${id}}!
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "The variable '${id}' of type Value cannot be inserted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting an unknown local script variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    /say The value is \${${id}}!
    """

    when:
    assembleProgram(programString)

    then:
    lastContext.errors[0].message == "${id} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 2
    lastContext.errors.size() == 1
  }

  @Test
  public void "Declaring a duplicate global variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    Integer ${id} = ${some($int())}
    Integer ${id} = ${some($int())}
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Duplicate variable ${id}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Declaring a global Integer variable"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    Integer ${id} = ${value}
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.rootVariableScope
    MplIntegerVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a global Selector variable"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    Selector ${id} = ${value}
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.rootVariableScope
    MplVariable<TargetSelector> variable = scope.findVariable(id)
    variable != null
    variable.value instanceof TargetSelector
    variable.value.toString() == value
  }

  @Test
  public void "Declaring a global String variable"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    String ${id} = "${value}"
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.rootVariableScope
    MplStringVariable variable = scope.findVariable(id)
    variable != null
    variable.value == value
  }

  @Test
  public void "Declaring a global Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector} ${scoreboard}
    impulse process main {}
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()

    VariableScope scope = interpreter.rootVariableScope
    MplVariable<MplValue> variable = scope.findVariable(id)
    variable != null
    MplScoreboardValue value = variable.value
    value.selector instanceof TargetSelector
    value.selector.toString() == selector
    value.scoreboard == scoreboard
  }

  @Test
  public void "Inserting a global Integer variable"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    Integer ${id} = ${value}
    impulse process main {
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a global Selector variable"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    Selector ${id} = ${value}
    impulse process main {
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a global String variable"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    String ${id} = "${value}"
    impulse process main {
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a global Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector} ${scoreboard}
    impulse process main {
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "The variable '${id}' of type Value cannot be inserted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting a qualified global Integer variable from the same file"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    File file = newTempFile()
    String qualifiedName = getBaseName(file.name) + '.' + id
    file.text = """
    Integer ${id} = ${value}
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """

    when:
    MplProgram program = assembleProgram(file)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global Selector variable from the same file"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    File file = newTempFile()
    String qualifiedName = getBaseName(file.name) + '.' + id
    file.text = """
    Selector ${id} = ${value}
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """

    when:
    MplProgram program = assembleProgram(file)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global String variable from the same file"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    File file = newTempFile()
    String qualifiedName = getBaseName(file.name) + '.' + id
    file.text = """
    String ${id} = "${value}"
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """

    when:
    MplProgram program = assembleProgram(file)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global Value variable from the same file"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    File file = newTempFile()
    String qualifiedName = getBaseName(file.name) + ' . ' + id
    file.text = """
    Value ${id} = ${selector} ${scoreboard}
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """

    when:
    MplProgram program = assembleProgram(file)

    then:
    lastContext.errors[0].message == "The variable '${id}' of type Value cannot be inserted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == qualifiedName
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting an unknown qualified global variable from the same file"() {
    given:
    String id = some($Identifier())
    File file = newTempFile()
    String qualifiedName = getBaseName(file.name) + '.' + id
    file.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """

    when:
    assembleProgram(file)

    then:
    lastContext.errors[0].message == "${qualifiedName} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == qualifiedName
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting a qualified global Integer variable from a different file"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    File mainFile = newTempFile()
    File otherFile = newTempFile()
    String qualifiedName = getBaseName(otherFile.name) + '.' + id
    mainFile.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """
    otherFile.text = """
    Integer ${id} = ${value}
    """

    when:
    MplProgram program = assembleProgram(mainFile)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global Selector variable from a different file"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    File mainFile = newTempFile()
    File otherFile = newTempFile()
    String qualifiedName = getBaseName(otherFile.name) + '.' + id
    mainFile.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """
    otherFile.text = """
    Selector ${id} = ${value}
    """

    when:
    MplProgram program = assembleProgram(mainFile)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global String variable from a different file"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    File mainFile = newTempFile()
    File otherFile = newTempFile()
    String qualifiedName = getBaseName(otherFile.name) + '.' + id
    mainFile.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """
    otherFile.text = """
    String ${id} = "${value}"
    """

    when:
    MplProgram program = assembleProgram(mainFile)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a qualified global Value variable from a different file"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    File mainFile = newTempFile()
    File otherFile = newTempFile()
    String qualifiedName = getBaseName(otherFile.name) + '.' + id
    mainFile.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """
    otherFile.text = """
    Value ${id} = ${selector} ${scoreboard}
    """

    when:
    MplProgram program = assembleProgram(mainFile)

    then:
    lastContext.errors[0].message == "The variable '${id}' of type Value cannot be inserted"
    lastContext.errors[0].source.file == mainFile
    lastContext.errors[0].source.text == qualifiedName
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting an unknown qualified global variable from a different file"() {
    given:
    String id = some($Identifier())
    File mainFile = newTempFile()
    File otherFile = newTempFile()
    String qualifiedName = getBaseName(otherFile.name) + '.' + id
    mainFile.text = """
    impulse process main {
      /say The value is \${${qualifiedName}}!
    }
    """
    otherFile.text = ""

    when:
    assembleProgram(mainFile)

    then:
    lastContext.errors[0].message == "${qualifiedName} cannot be resolved to a variable"
    lastContext.errors[0].source.file == mainFile
    lastContext.errors[0].source.text == qualifiedName
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "Declaring a duplicate local variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    impulse process main {
      Integer ${id} = ${some($int())}
      Integer ${id} = ${some($int())}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors[0].message == "Duplicate variable ${id}"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "Declaring a local Integer variable does not put it into the rootVariableScope"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      Integer ${id} = ${value}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()
    interpreter.rootVariableScope.variables.isEmpty()
  }

  @Test
  public void "Declaring a local Selector variable does not put it into the rootVariableScope"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    impulse process main {
      Selector ${id} = ${value}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()
    interpreter.rootVariableScope.variables.isEmpty()
  }

  @Test
  public void "Declaring a local String variable does not put it into the rootVariableScope"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    impulse process main {
      String ${id} = "${value}"
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()
    interpreter.rootVariableScope.variables.isEmpty()
  }

  @Test
  public void "Declaring a local Value variable does not put it into the rootVariableScope"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    impulse process main {
      Value ${id} = ${selector} ${scoreboard}
    }
    """

    when:
    MplInterpreter interpreter = interpret(programString)

    then:
    lastContext.errors.isEmpty()
    interpreter.rootVariableScope.variables.isEmpty()
  }

  @Test
  public void "Inserting a local Integer variable"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      Integer ${id} = ${value}
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local Selector variable"() {
    given:
    String id = some($Identifier())
    String value = "@e[name=${some($Identifier())}]"
    String programString = """
    impulse process main {
      Selector ${id} = ${value}
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local String variable"() {
    given:
    String id = some($Identifier())
    String value = some($String())
    String programString = """
    impulse process main {
      String ${id} = "${value}"
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplCommand command =  process.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "Inserting a local Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    impulse process main {
      Value ${id} = ${selector} ${scoreboard}
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "The variable '${id}' of type Value cannot be inserted"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 4
    lastContext.errors.size() == 1
  }

  @Test
  public void "Inserting an unknown local variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    impulse process main {
      /say The value is \${${id}}!
    }
    """

    when:
    assembleProgram(programString)

    then:
    lastContext.errors[0].message == "${id} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 3
    lastContext.errors.size() == 1
  }

  @Test
  public void "A local variable from one process is not found in another process"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      Integer ${id} = ${value}
    }
    impulse process other {
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "${id} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 6
    lastContext.errors.size() == 1
  }

  @Test
  public void "A local variable from outside an if is found in the if"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      Integer ${id} = ${value}
      if: /testfor @p
      then {
        /say The value is \${${id}}!
      }
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplIf mplIf =  process.chainParts[0]
    MplCommand command = mplIf.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "A local variable from an if is found in the if"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      if: /testfor @p
      then {
        Integer ${id} = ${value}
        /say The value is \${${id}}!
      }
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplIf mplIf =  process.chainParts[0]
    MplCommand command = mplIf.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "A local variable from an if is not found outside of the if"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      if: /testfor @p
      then {
        Integer ${id} = ${value}
      }
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "${id} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 7
    lastContext.errors.size() == 1
  }

  @Test
  public void "A local variable from outside a while is found in the while"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      Integer ${id} = ${value}
      while: /testfor @p
      repeat {
        /say The value is \${${id}}!
      }
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplWhile mplWhile =  process.chainParts[0]
    MplCommand command = mplWhile.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "A local variable from a while is found in the while"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      while: /testfor @p
      repeat {
        Integer ${id} = ${value}
        /say The value is \${${id}}!
      }
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors.isEmpty()

    program.processes.size() == 1
    MplProcess process = program.processes.first()

    MplWhile mplWhile =  process.chainParts[0]
    MplCommand command = mplWhile.chainParts[0]
    command.commandParts.join() == "say The value is ${value}!"
    process.chainParts.size() == 1
  }

  @Test
  public void "A local variable from a while is not found outside of the while"() {
    given:
    String id = some($Identifier())
    int value = some($int())
    String programString = """
    impulse process main {
      while: /testfor @p
      repeat {
        Integer ${id} = ${value}
      }
      /say The value is \${${id}}!
    }
    """

    when:
    MplProgram program = assembleProgram(programString)

    then:
    lastContext.errors[0].message == "${id} cannot be resolved to a variable"
    lastContext.errors[0].source.file == lastTempFile
    lastContext.errors[0].source.text == id
    lastContext.errors[0].source.lineNumber == 7
    lastContext.errors.size() == 1
  }

}
