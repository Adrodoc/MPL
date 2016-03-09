package de.adrodoc55.minecraft.mpl

import de.adrodoc55.minecraft.mpl.antlr.MplProcess;
import de.adrodoc55.minecraft.mpl.antlr.MplProject;

class MplProjectPlacerSpec {

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
    MplProcess process = new MplProcess(name, commands, source)
    project.addProcess(process)
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

  void 'Empty installation is ignored'() {
    given:
    MplProject project = new MplProject()
    MplProcess process = new MplProcess(name, commands, source)
    project.addProcess(process)
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

}
