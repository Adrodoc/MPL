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
package de.adrodoc55.minecraft.mpl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MplUtilsTest {

  @Test
  public void test_toString_fuer_kleine_ganze_Zahl() {
    // Given:
    double d = 100;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("100");
  }

  @Test
  public void test_toString_fuer_negative_kleine_ganze_Zahl() {
    // Given:
    double d = -100;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("-100");
  }

  @Test
  public void test_toString_fuer_grosse_ganze_Zahl() {
    // Given:
    double d = 10000000000D;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("10000000000");
  }

  @Test
  public void test_toString_fuer_negative_grosse_ganze_Zahl() {
    // Given:
    double d = -10000000000D;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("-10000000000");
  }

  @Test
  public void test_toString_fuer_komma_Zahl() {
    // Given:
    double d = 100.123;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("100.123");
  }

  @Test
  public void test_toString_fuer_negative_komma_Zahl() {
    // Given:
    double d = -100.123;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("-100.123");
  }

  @Test
  public void test_toString_fuer_zero() {
    // Given:
    double d = 0;

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("0");
  }

  @Test
  public void test_toString_fuer_negativ_zero() {
    // Given:
    double d = -1e-200 * 1e-200;

    // Expect:
    "-0.0".equals(String.valueOf(d));

    // When:
    String act = MplUtils.toString(d);

    // Then:
    assertThat(act).isEqualTo("0");
  }

}
