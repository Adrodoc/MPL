package de.adrodoc55.minecraft.mpl.compilation;

import static de.adrodoc55.TestBase.$String
import static de.adrodoc55.TestBase.$int
import static de.adrodoc55.TestBase.some
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplSpecBase;
import de.adrodoc55.minecraft.mpl.ast.chainparts.MplCommand
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

  @Test
  public void "Declaring a local Integer variable"() {
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
  public void "Declaring a local Selector variable"() {
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
  public void "Declaring a local String variable"() {
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
  public void "Declaring a local Value variable"() {
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

    VariableScope scope = interpreter.rootVariableScope
    MplVariable<MplValue> variable = scope.findVariable(id)
    variable != null
    MplScoreboardValue value = variable.value
    value.selector instanceof TargetSelector
    value.selector.toString() == selector
    value.scoreboard == scoreboard
  }

  @Test
  @Unroll("Type mismatch at local variable declaration from #actualType to #declaredType")
  public void "Type mismatch at local variable declaration"(MplType declaredType, MplType actualType, String value) {
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
  public void "Inserting a local Integer variable"() {
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
  public void "Inserting a local Selector variable"() {
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
  public void "Inserting a local String variable"() {
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
  public void "Inserting a local Value variable"() {
    given:
    String id = some($Identifier())
    String selector = "@e[name=${some($Identifier())}]"
    String scoreboard = some($Identifier())
    String programString = """
    Value ${id} = ${selector}  ${scoreboard}
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
  public void "Inserting an unknown variable"() {
    given:
    String id = some($Identifier())
    String programString = """
    remote process main {
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
}
