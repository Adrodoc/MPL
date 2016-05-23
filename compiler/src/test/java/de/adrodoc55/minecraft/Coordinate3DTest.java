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
package de.adrodoc55.minecraft;

import static de.adrodoc55.TestBase.someInt;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.adrodoc55.minecraft.coordinate.Coordinate3D;

public class Coordinate3DTest {

  @Test
  public void test_newCoordinate3D() {
    // When:
    Coordinate3D c = new Coordinate3D();
    // Then:
    assertThat(c.getX()).isZero();
    assertThat(c.getY()).isZero();
    assertThat(c.getZ()).isZero();
  }

  @Test
  public void test_getX() {
    // Given:
    int x = someInt();
    // When:
    Coordinate3D c = new Coordinate3D(x, someInt(), someInt());
    // Then:
    assertThat(c.getX()).isEqualTo(x);
  }

  @Test
  public void test_getY() {
    // Given:
    int y = someInt();
    // When:
    Coordinate3D c = new Coordinate3D(someInt(), y, someInt());
    // Then:
    assertThat(c.getY()).isEqualTo(y);
  }

  @Test
  public void test_getZ() {
    // Given:
    int z = someInt();
    // When:
    Coordinate3D c = new Coordinate3D(someInt(), someInt(), z);
    // Then:
    assertThat(c.getZ()).isEqualTo(z);
  }

  @Test
  public void test_equals() {
    // Given:
    int x = someInt();
    int y = someInt();
    int z = someInt();
    Coordinate3D c1 = new Coordinate3D(x, y, z);
    Coordinate3D c2 = new Coordinate3D(x, y, z);
    // Expect:
    assertThat(c1).isEqualTo(c2);
  }

  @Test
  public void test_newCoordinate3D_Coordinate3D() {
    // Given:
    int x = someInt();
    int y = someInt();
    int z = someInt();
    Coordinate3D c = new Coordinate3D(x, y, z);
    // When:
    Coordinate3D actual = new Coordinate3D(c);
    // Then:
    assertThat(actual).isEqualTo(c);
  }

  @Test
  public void test_copy() {
    // Given:
    int x = someInt();
    int y = someInt();
    int z = someInt();
    Coordinate3D c = new Coordinate3D(x, y, z);
    // When:
    Coordinate3D actual = c.copy();
    // Then:
    assertThat(actual).isNotSameAs(c);
    assertThat(actual).isEqualTo(c);
  }

  @Test
  public void test_toAbsoluteString() {
    // Given:
    int x = someInt();
    int y = someInt();
    int z = someInt();
    Coordinate3D c = new Coordinate3D(x, y, z);
    // When:
    String absoluteString = c.toAbsoluteString();
    // Then:
    assertThat(absoluteString).isEqualTo(x + " " + y + " " + z);
  }

  @Test
  public void test_toRelativeString() {
    // Given:
    int x = someInt();
    int y = someInt();
    int z = someInt();
    Coordinate3D c = new Coordinate3D(x, y, z);
    // When:
    String relativeString = c.toRelativeString();
    // Then:
    assertThat(relativeString).isEqualTo("~" + x + " ~" + y + " ~" + z);
  }

  @Test
  public void test_overflowSaveAddition_handles_normal_Addition() {
    // Given:
    int a = someInt(1000) - 500;
    int b = someInt(1000) - 500;
    // When:
    int c = Coordinate3D.overflowSaveAddition(a, b);
    // Then:
    assertThat(c).isEqualTo(a + b);
  }

  @Test
  public void test_overflowSaveAddition_handles_Overflow() {
    // Given:
    int a = someInt(1000) + 1; // a < 0
    int b = Integer.MAX_VALUE - a + 1;
    // When:
    int c = Coordinate3D.overflowSaveAddition(a, b);
    // Then:
    assertThat(c).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void test_overflowSaveAddition_handles_Underflow() {
    // Given:
    int a = someInt(1000) + 1; // a < 0
    int b = Integer.MAX_VALUE - a + 1;
    a *= -1;
    b *= -1;
    // When:
    int c = Coordinate3D.overflowSaveAddition(a, b);
    // Then:
    assertThat(c).isEqualTo(Integer.MIN_VALUE);
  }

  @Test
  public void test_overflowSaveSubstraction_handles_normal_Substraction() {
    // Given:
    int a = someInt(1000) - 500;
    int b = someInt(1000) - 500;
    // When:
    int c = Coordinate3D.overflowSaveSubstraction(a, b);
    // Then:
    assertThat(c).isEqualTo(a - b);
  }

  @Test
  public void test_overflowSaveSubstraction_handles_Overflow() {
    // Given:
    int a = someInt(1000) + 1; // a < 0
    int b = Integer.MAX_VALUE - a + 1;
    b *= -1;
    // When:
    int c = Coordinate3D.overflowSaveSubstraction(a, b);
    // Then:
    assertThat(c).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void test_overflowSaveSubstraction_handles_Underflow() {
    // Given:
    int a = someInt(1000) + 1; // a < 0
    int b = Integer.MAX_VALUE - a + 1;
    a *= -1;
    // When:
    int c = Coordinate3D.overflowSaveSubstraction(a, b);
    // Then:
    assertThat(c).isEqualTo(Integer.MIN_VALUE);
  }

  @Test
  public void test_plus() {
    // Given:
    int x1 = someInt(1000);
    int y1 = someInt(1000);
    int z1 = someInt(1000);
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = someInt(1000);
    int y2 = someInt(1000);
    int z2 = someInt(1000);
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.plus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(x1 + x2);
    assertThat(actual.getY()).isEqualTo(y1 + y2);
    assertThat(actual.getZ()).isEqualTo(z1 + z2);
  }

  @Test
  public void test_plus_will_not_overflow() {
    // Given:
    int x1 = someInt(1000) + 1; // x1 > 0
    int y1 = someInt(1000) + 1; // y1 > 0
    int z1 = someInt(1000) + 1; // z1 > 0
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = Integer.MAX_VALUE - x1 + 1;
    int y2 = Integer.MAX_VALUE - y1 + 1;
    int z2 = Integer.MAX_VALUE - z1 + 1;
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.plus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actual.getY()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actual.getZ()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void test_plus_will_not_underflow() {
    // Given:
    int x1 = someInt(1000) * -1 - 1; // x1 < 0
    int y1 = someInt(1000) * -1 - 1; // y1 < 0
    int z1 = someInt(1000) * -1 - 1; // z1 < 0
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = Integer.MIN_VALUE - x1 - 1;
    int y2 = Integer.MIN_VALUE - y1 - 1;
    int z2 = Integer.MIN_VALUE - z1 - 1;
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.plus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actual.getY()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actual.getZ()).isEqualTo(Integer.MIN_VALUE);
  }

  @Test
  public void test_minus() {
    // Given:
    int x1 = someInt(1000);
    int y1 = someInt(1000);
    int z1 = someInt(1000);
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = someInt(1000);
    int y2 = someInt(1000);
    int z2 = someInt(1000);
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.minus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(x1 - x2);
    assertThat(actual.getY()).isEqualTo(y1 - y2);
    assertThat(actual.getZ()).isEqualTo(z1 - z2);
  }

  @Test
  public void test_minus_will_not_overflow() {
    // Given:
    int x1 = someInt(1000) + 1; // x1 > 0
    int y1 = someInt(1000) + 1; // y1 > 0
    int z1 = someInt(1000) + 1; // z1 > 0
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = Integer.MIN_VALUE + x1 - 1;
    int y2 = Integer.MIN_VALUE + y1 - 1;
    int z2 = Integer.MIN_VALUE + z1 - 1;
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.minus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actual.getY()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actual.getZ()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void test_minus_will_not_underflow() {
    // Given:
    int x1 = someInt(1000) * -1 - 1; // x1 < 0
    int y1 = someInt(1000) * -1 - 1; // y1 < 0
    int z1 = someInt(1000) * -1 - 1; // z1 < 0
    Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
    int x2 = Integer.MAX_VALUE + x1 + 1;
    int y2 = Integer.MAX_VALUE + y1 + 1;
    int z2 = Integer.MAX_VALUE + z1 + 1;
    Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
    // When:
    Coordinate3D actual = c1.minus(c2);
    // Then:
    assertThat(actual.getX()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actual.getY()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actual.getZ()).isEqualTo(Integer.MIN_VALUE);
  }

}
