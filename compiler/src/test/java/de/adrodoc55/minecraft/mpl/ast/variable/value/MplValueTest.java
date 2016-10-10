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
package de.adrodoc55.minecraft.mpl.ast.variable.value;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;

public class MplValueTest extends MplTestBase {
  private MplCompilerContext lastContext;

  private MplValue parse(String value) {
    lastContext = some($MplCompilerContext());
    MplSource source = some($MplSource().withLine(value));
    return MplValue.parse(value, source, lastContext);
  }

  @Test
  public void test_parse__Integer() {
    // given:
    Integer expected = some($int());
    String value = String.valueOf(expected);

    // when:
    MplValue actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual).isExactlyInstanceOf(MplIntegerValue.class);
    MplIntegerValue act = (MplIntegerValue) actual;
    assertThat(act.getValue()).isEqualTo(expected);
  }

  @Test
  public void test_parse__Selector_and_Scoreboard() {
    // given:
    String selector = "@e";
    String scoreboard = some($Identifier());
    String value = String.valueOf(selector + " " + scoreboard);

    // when:
    MplValue actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual).isExactlyInstanceOf(MplScoreboardValue.class);
    MplScoreboardValue act = (MplScoreboardValue) actual;
    assertThat(act.getSelector().toString()).isEqualTo(selector);
    assertThat(act.getScoreboard()).isEqualTo(scoreboard);
  }

  @Test
  public void test_parse__When_Selector_and_Scoreboard_are_not_separated_by_a_space() {
    // given:
    String selector = "@e";
    String scoreboard = some($Identifier());
    String value = String.valueOf(selector + scoreboard);

    // when:
    IllegalArgumentException actual = null;
    try {
      parse(value);
    } catch (IllegalArgumentException ex) {
      actual = ex;
    }

    // then:
    assertThat(actual).hasMessage("The specified value does not contain a space");
  }

}
