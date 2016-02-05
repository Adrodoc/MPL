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
package de.adrodoc55.minecraft.mpl.gui.utils;

import static de.adrodoc55.TestBase.someString
import spock.lang.Specification

public class TabToSpaceTextPMSpec extends Specification {

  void "String ohne tabs und newlines bleibt gleich"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM()
    String text = someString()
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text
  }

  void "String ohne tabs mit verschiedenen newlines bleibt gleich"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM()
    String text = someString() + '\r' +someString() + '\n' + someString() + '\r\n' + someString()
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text
  }

  void "String mit tab am anfang ohne newlines: tab wird durch tabwidth * spaces ersetzt"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = '\t' + string
    when:
    underTest.setText(text);
    then:
    underTest.getText() == '    ' + string
  }

  void "String mit tab in index 2 ohne newlines: tab wird durch (tabwidth - 2) * spaces ersetzt"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = 'ab\t' + string
    when:
    underTest.setText(text);
    then:
    underTest.getText() == 'ab  ' + string
  }

  void "String mit tab am anfang jeder newline: tab wird durch tabwidth * spaces ersetzt"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = '\t' + someString() + '\r\t' + someString() + '\n\t' + someString() + '\r\n\t' + someString()
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text.replace('\t', '    ')
  }

  void "String mit tab am anfang einiger newlines: tab wird durch tabwidth * spaces ersetzt"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = '\t' + someString() + '\r\t' + someString() + '\n\t' + '\r\n'
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text.replace('\t', '    ')
  }

  void "String mit tab in index 2 in jeder newline: tab wird durch (tabwidth - 2) * spaces ersetzt()"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = 'ab\t' + someString() + '\rcd\t' + someString() + '\nef\t' + someString() + '\rgh\n' + someString()
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text.replace('\t', '  ')
  }

  void "String mit tab in index 2 in einigen newlines: tab wird durch (tabwidth - 2) * spaces ersetzt()"() {
    given:
    TabToSpaceTextPM underTest = new TabToSpaceTextPM(4)
    String string = someString()
    String text = 'ab\t' + someString() + '\rcd\t' + someString() + '\nef\t' + '\rgh\n'
    when:
    underTest.setText(text);
    then:
    underTest.getText() == text.replace('\t', '  ')
  }
}
