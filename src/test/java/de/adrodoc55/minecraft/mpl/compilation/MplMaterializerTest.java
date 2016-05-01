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
package de.adrodoc55.minecraft.mpl.compilation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.chain.NamedCommandChain;
import de.adrodoc55.minecraft.mpl.commands.Mode;
import de.adrodoc55.minecraft.mpl.commands.chainlinks.Command;
import de.adrodoc55.minecraft.mpl.commands.chainparts.program.MplProcess;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import net.karneim.pojobuilder.Builder;

public class MplMaterializerTest extends MplTestBase {

  @Test
  public void Impulse_Prozess_mit_Transmitter() {
    // given:
    MplProcess process = some($MplProcess());
    CompilerOptions options = new CompilerOptions(CompilerOption.TRANSMITTER);

    // when:
    NamedCommandChain chain = MplMaterializer.materialize(process, options);

    // then:
    Command reciever = new Command("setblock ${this - 1} stone", Mode.IMPULSE, false);
    assertThat(chain.getName()).isEqualTo(process.getName());
    assertThat(chain.getCommands()).startsWith(reciever);
  }

  @Test
  public void Eine
  repeat Prozess
  deaktiviert sich

  nicht selbst() {
    given:
    String name = someIdentifier()
    String programString = """
    repeat process ${name} (
    /say hi
    )
    """
    when:
    MplInterpreter interpreter = interpret(programString)
    then:
    MplProject project = interpreter.project
    project.exceptions.isEmpty()
    Collection<MplProcess> processes = project.processes
    processes.size() == 1

    MplProcess process = processes.first()
    process.name == name

    List<Command> commands = process.commands
    commands[0] == new Command("say hi", Mode.REPEAT, false)
    commands.size() == 1
  }

}
