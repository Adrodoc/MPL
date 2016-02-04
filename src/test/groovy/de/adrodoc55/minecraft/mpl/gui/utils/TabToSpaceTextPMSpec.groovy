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
