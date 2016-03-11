package de.adrodoc55.minecraft.mpl

import static de.adrodoc55.minecraft.mpl.MplTestBase.Command
import static de.adrodoc55.minecraft.mpl.MplTestBase.some
import spock.lang.Specification
import de.adrodoc55.minecraft.mpl.antlr.MplProcess
import de.adrodoc55.minecraft.mpl.antlr.MplProject

class MplProjectPlacerSpec extends Specification {

  void 'Placing an empty project returns an empty list'() {
    given:
    MplProject project = new MplProject()
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

  void 'Empty processes are ignored'() {
    given:
    MplProject project = new MplProject()
    MplProcess process = new MplProcess('testProcess', [])
    project.addProcess(process)
    project.getInstallation().add(some(Command()))
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

  void 'Empty installation is ignored'() {
    given:
    MplProject project = new MplProject()
    project.getInstallation()
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

  void 'Empty uninstallation is ignored'() {
    given:
    MplProject project = new MplProject()
    MplProcess process = new MplProcess(name, commands, source)
    project.addProcess(process)
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }
  
  void 'estimateB'() {
    
  }
  
}
