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
package de.adrodoc55.minecraft.mpl.ast.variable.selector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.compilation.CompilerException;
import de.adrodoc55.minecraft.mpl.compilation.MplCompilerContext;
import de.adrodoc55.minecraft.mpl.compilation.MplSource;

public class TargetSelectorTest extends MplTestBase {
  private MplCompilerContext lastContext;
  private MplSource lastSource;

  private TargetSelector parse(String value) {
    lastContext = new MplCompilerContext();
    lastSource = some($MplSource().withLine(value));
    return TargetSelector.parse(value, lastSource, lastContext);
  }

  @Test
  public void test_parse__At_Player() {
    // given:
    String value = "@p";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getType()).isSameAs(TargetSelectorType.P);
    assertThat(actual.getArguments()).isEmpty();
  }

  @Test
  public void test_parse__At_All() {
    // given:
    String value = "@a";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getType()).isSameAs(TargetSelectorType.A);
    assertThat(actual.getArguments()).isEmpty();
  }

  @Test
  public void test_parse__At_Random() {
    // given:
    String value = "@r";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getType()).isSameAs(TargetSelectorType.R);
    assertThat(actual.getArguments()).isEmpty();
  }

  @Test
  public void test_parse__At_Entity() {
    // given:
    String value = "@e";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getType()).isSameAs(TargetSelectorType.E);
    assertThat(actual.getArguments()).isEmpty();
  }

  @Test
  public void test_parse__With_empty_Arguments() {
    // given:
    String value = "@e[]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).isEmpty();
  }

  @Test
  public void test_parse__With_String_Argument() {
    // given:
    String value = "@e[name=blub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "blub"));
  }

  @Test
  public void test_parse__Value_cannot_contain_a_comma() {
    // given:
    String value = "@e[name=bl,ub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(actual).isNull();
    assertThat(lastContext.getErrors()).hasSize(1);
    CompilerException error = lastContext.getErrors().iterator().next();
    assertThat(error.getSource()).isSameAs(lastSource);
  }

  @Test
  public void test_parse__Value_cannot_contain_a_space() {
    // given:
    String value = "@e[name=bl ub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(actual).isNull();
    assertThat(lastContext.getErrors()).hasSize(1);
    CompilerException error = lastContext.getErrors().iterator().next();
    assertThat(error.getSource()).isSameAs(lastSource);
  }

  @Test
  public void test_parse__Value_cannot_contain_a_tab() {
    // given:
    String value = "@e[name=bl\tub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(actual).isNull();
    assertThat(lastContext.getErrors()).hasSize(1);
    CompilerException error = lastContext.getErrors().iterator().next();
    assertThat(error.getSource()).isSameAs(lastSource);
  }

  @Test
  public void test_parse__Value_cannot_contain_a_carriage_return() {
    // given:
    String value = "@e[name=bl\rub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(actual).isNull();
    assertThat(lastContext.getErrors()).hasSize(1);
    CompilerException error = lastContext.getErrors().iterator().next();
    assertThat(error.getSource()).isSameAs(lastSource);
  }

  @Test
  public void test_parse__Value_cannot_contain_a_new_line() {
    // given:
    String value = "@e[name=bl\nub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(actual).isNull();
    assertThat(lastContext.getErrors()).hasSize(1);
    CompilerException error = lastContext.getErrors().iterator().next();
    assertThat(error.getSource()).isSameAs(lastSource);
  }

  @Test
  public void test_parse__Value_can_contain_an_equals_sign() {
    // given:
    String value = "@e[name=bl=ub]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "bl=ub"));
  }

  @Test
  public void test_parse__Value_can_be_an_at_sign() {
    // given:
    String value = "@e[name=@]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "@"));
  }

  @Test
  public void test_parse__Value_can_be_an_equals_sign() {
    // given:
    String value = "@e[name==]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "="));
  }

  @Test
  public void test_parse__Value_can_be_an_exclamation_mark() {
    // given:
    String value = "@e[name=!!]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "!!"));
  }

  @Test
  public void test_parse__Value_can_be_an_open_square_bracket() {
    // given:
    String value = "@e[name=[]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "["));
  }

  @Test
  public void test_parse__Value_can_be_a_closed_square_bracket() {
    // given:
    String value = "@e[name=]]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("name", "]"));
  }

  @Test
  public void test_parse__With_positive_Integer_Argument() {
    // given:
    String value = "@e[x=1]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("x", "1"));
  }

  @Test
  public void test_parse__With_negative_Integer_Argument() {
    // given:
    String value = "@e[x=-1]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("x", "-1"));
  }

  @Test
  public void test_parse__With_inverted_Argument() {
    // given:
    String value = "@e[type=!Player]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("type", "!Player"));
  }

  @Test
  public void test_parse__With_void_Argument() {
    // given:
    String value = "@e[type=]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("type", ""));
  }

  @Test
  public void test_parse__With_inverted_void_Argument() {
    // given:
    String value = "@e[type=!]";

    // when:
    TargetSelector actual = parse(value);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual.getArguments()).containsExactly(entry("type", "!"));
  }

  @Test
  public void test_parse__Order_of_Arguments_is_preserved() {
    // given:
    String value1 = "@e[x=1,y=2]";
    String value2 = "@e[y=2,x=1]";

    // when:
    TargetSelector actual1 = parse(value1);
    TargetSelector actual2 = parse(value2);

    // then:
    assertThat(lastContext.getErrors()).isEmpty();

    assertThat(actual1.getArguments().keySet()).containsExactly("x", "y");
    assertThat(actual2.getArguments().keySet()).containsExactly("y", "x");
  }

}
