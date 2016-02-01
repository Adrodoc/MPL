package de.adrodoc55.minecraft.mpl

import org.junit.Test

import de.adrodoc55.minecraft.mpl.antlr.CompilationFailedException;

class MplCompilerSpec extends MplSpecBase {

  @Test
  public void "a process from the same file will be included by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    process main:
    /this is the main process
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    """
    new File(folder, 'second.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    start other
    """
    new File(folder, 'second.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    start other
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    start other
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other:
    /this is the other process
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
    process main:
    /this is the main process
    start other

    process other:
    /this is the other process
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
  public void "ambigious processes within imports will be ignored, by default"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    import "newFolder"
    process main:
    /this is the main process
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process other:
    /this is the other process
    """
    new File(folder, 'newFolder/newFile2.mpl').text = """
    process other:
    /this is the second other process
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
    process main:
    /this is the main process
    start other
    """
    new File(folder, 'newFolder').mkdirs()
    File newFile = new File(folder, 'newFolder/newFile.mpl')
    newFile.text = """
    process other:
    /this is the other process
    """
    File newFile2 = new File(folder, 'newFolder/newFile2.mpl')
    newFile2.text = """
    process other:
    /this is the second other process
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
    project main:
    include "newFolder/newFile.mpl"
    """
    new File(folder, 'newFolder').mkdirs()
    new File(folder, 'newFolder/newFile.mpl').text = """
    process main:
    /this is the main process
    process other:
    /this is the other process
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
    install
    /say install
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    chains.size() == 2
    chains[0].name == null
    chains[1].name == 'installation'
  }

  @Test
  public void "having an uninstallation also produces an installation"() {
    given:
    File folder = tempFolder.root
    new File(folder, 'main.mpl').text = """
    uninstall
    /say uninstall
    """
    when:
    List<CommandBlockChain> chains = MplCompiler.compile(new File(folder, 'main.mpl'))
    then:
    chains.size() == 3
    chains[0].name == null
    chains[1].name == 'installation'
    chains[2].name == 'uninstallation'
  }

}
