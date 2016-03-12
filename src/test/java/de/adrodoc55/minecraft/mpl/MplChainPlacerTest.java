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
package de.adrodoc55.minecraft.mpl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.commands.Command;
import de.adrodoc55.minecraft.mpl.compilation.MplChainPlacer;

public class MplChainPlacerTest extends MplTestBase {

  @Test
  public void getLongestSuccessiveConditionalCount_throws_NullPointerException() {
    try {
      // when:
      MplChainPlacer.getLongestSuccessiveConditionalCount(null);
      // then:
    } catch (NullPointerException ex) {
      assertThat(ex.getMessage()).isEqualTo("chainParts == null!");
    }
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_0_for_empty_list() {
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(new LinkedList<>());
    // then:
    assertThat(result).isZero();
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_0_for_multiple_unconditionals() {
    // given:
    List<Command> list = listOf($Command().withConditional(false));
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isZero();
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_1_for_one_conditional() {
    // given:
    Command conditional = new Command("test", true);
    LinkedList<Command> list = new LinkedList<>();
    list.add(conditional);
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(1);
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_n_for_n_conditionals() {
    // given:
    List<Command> list = listOf($Command().withConditional(true));
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(list.size());
  }

  @Test
  public void getLongestSuccessiveConditionalCount_is_max_of_m_and_n_for_m_plus_n_conditionals() {
    // given:
    List<Command> conditionals1 = listOf($Command().withConditional(true));
    List<Command> unconditionals = listOf(someInt(1, 100), $Command().withConditional(false));
    List<Command> conditionals2 = listOf($Command().withConditional(true));

    List<Command> list = new LinkedList<>();
    list.addAll(conditionals1);
    list.addAll(unconditionals);
    list.addAll(conditionals2);
    // when:
    int result = MplChainPlacer.getLongestSuccessiveConditionalCount(list);
    // then:
    assertThat(result).isEqualTo(Math.max(conditionals1.size(), conditionals2.size()));
  }
}
