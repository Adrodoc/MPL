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
package de.adrodoc55.minecraft.mpl.interpretation.variable;

import org.junit.Test;

import de.adrodoc55.minecraft.mpl.MplTestBase;
import de.adrodoc55.minecraft.mpl.ast.variable.MplVariable;

public class LocalVariableScopeTest extends MplTestBase {

  @Test
  public void test_declareVariable__Throws_Exception_for_two_Variables_with_the_same_Identifier()
      throws Exception {
    // given:
    GlobalVariableScope root = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(root);
    String identifier = some($Identifier());

    underTest.declareVariable(some($MplVariable().withIdentifier(identifier)));

    // when:
    DuplicateVariableException actual = null;
    try {
      underTest.declareVariable(some($MplVariable().withIdentifier(identifier)));
    } catch (DuplicateVariableException ex) {
      actual = ex;
    }

    // then:
    assertThat(actual).isNotNull();
  }

  @Test
  public void test_declareVariable__Throws_Exception_when_Variable_is_already_defined_in_Parent_Local_VariableScope()
      throws Exception {
    // given:
    GlobalVariableScope root = new GlobalVariableScope();
    LocalVariableScope parent = new LocalVariableScope(root);
    LocalVariableScope underTest = new LocalVariableScope(parent);
    String identifier = some($Identifier());

    parent.declareVariable(some($MplVariable().withIdentifier(identifier)));

    // when:
    DuplicateVariableException actual = null;
    try {
      underTest.declareVariable(some($MplVariable().withIdentifier(identifier)));
    } catch (DuplicateVariableException ex) {
      actual = ex;
    }

    // then:
    assertThat(actual).isNotNull();
  }

  @Test
  public void test_declareVariable__Works_when_Variable_is_already_defined_in_Parent_Global_VariableScope()
      throws Exception {
    // given:
    GlobalVariableScope parent = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(parent);
    String identifier = some($Identifier());

    parent.declareVariable(some($MplVariable().withIdentifier(identifier)));

    // when:
    underTest.declareVariable(some($MplVariable().withIdentifier(identifier)));

    // then:
    assertThat(underTest.getVariables()).containsKey(identifier);
  }

  @Test
  public void test_findVariable__Returns_correct_Variable_in_the_Scope() throws Exception {
    // given:
    GlobalVariableScope root = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(root);
    MplVariable<?> variable = some($MplVariable());
    underTest.declareVariable(variable);
    underTest.declareVariable(some($MplVariable()));

    // when:
    MplVariable<?> actual = underTest.findVariable(variable.getIdentifier());

    // then:
    assertThat(actual).isSameAs(variable);
  }

  @Test
  public void test_findVariable__Returns_null_if_there_is_no_Variable_with_the_specified_Identifier()
      throws Exception {
    // given:
    GlobalVariableScope root = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(root);
    underTest.declareVariable(some($MplVariable()));

    // when:
    MplVariable<?> actual = underTest.findVariable(some($Identifier()));

    // then:
    assertThat(actual).isNull();
  }

  @Test
  public void test_findVariable__Returns_correct_Variable_from_Parent_LocalVariableScope()
      throws Exception {
    // given:
    GlobalVariableScope root = new GlobalVariableScope();
    LocalVariableScope parent = new LocalVariableScope(root);
    LocalVariableScope underTest = new LocalVariableScope(parent);
    MplVariable<?> variable = some($MplVariable());
    parent.declareVariable(variable);
    parent.declareVariable(some($MplVariable()));
    underTest.declareVariable(some($MplVariable()));

    // when:
    MplVariable<?> actual = underTest.findVariable(variable.getIdentifier());

    // then:
    assertThat(actual).isSameAs(variable);
  }

  @Test
  public void test_findVariable__Returns_correct_Variable_from_Parent_GlobalVariableScope()
      throws Exception {
    // given:
    GlobalVariableScope parent = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(parent);
    MplVariable<?> variable = some($MplVariable());
    parent.declareVariable(variable);
    parent.declareVariable(some($MplVariable()));
    underTest.declareVariable(some($MplVariable()));

    // when:
    MplVariable<?> actual = underTest.findVariable(variable.getIdentifier());

    // then:
    assertThat(actual).isSameAs(variable);
  }

  @Test
  public void test_findVariable__Returns_local_Variable_over_global_Variable() throws Exception {
    // given:
    GlobalVariableScope parent = new GlobalVariableScope();
    LocalVariableScope underTest = new LocalVariableScope(parent);
    MplVariable<?> variable = some($MplVariable());
    String identifier = variable.getIdentifier();
    parent.declareVariable(some($MplVariable().withIdentifier(identifier)));
    underTest.declareVariable(variable);

    // when:
    MplVariable<?> actual = underTest.findVariable(identifier);

    // then:
    assertThat(actual).isSameAs(variable);
  }

}
