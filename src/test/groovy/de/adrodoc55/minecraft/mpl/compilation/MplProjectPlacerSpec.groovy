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
package de.adrodoc55.minecraft.mpl.compilation

import static de.adrodoc55.minecraft.mpl.MplTestBase.Command
import static de.adrodoc55.minecraft.mpl.MplTestBase.some
import spock.lang.Specification
import de.adrodoc55.minecraft.coordinate.Orientation3D
import de.adrodoc55.minecraft.mpl.chain.CommandBlockChain
import de.adrodoc55.minecraft.mpl.chain.MplProcess
import de.adrodoc55.minecraft.mpl.program.MplProject

class MplProjectPlacerSpec extends Specification {

  void 'Placing an empty project returns an empty list'() {
    given:
    MplProject project = new MplProject()
    project.setOrientation(new Orientation3D())
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

  void 'Empty processes are ignored'() {
    given:
    MplProject project = new MplProject()
    project.setOrientation(new Orientation3D())
    MplProcess process = new MplProcess('testProcess', [])
    project.addProcess(process)
    project.getInstallation().add(some(Command()))
    when:
    List<CommandBlockChain> result = new MplProjectPlacer(project).place()
    then:
    result.isEmpty()
  }

}
