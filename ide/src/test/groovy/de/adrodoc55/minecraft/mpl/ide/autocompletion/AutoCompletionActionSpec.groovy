package de.adrodoc55.minecraft.mpl.ide.autocompletion

import javax.swing.JTextArea
import javax.swing.text.JTextComponent

import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.Token

import spock.lang.Specification

class AutoCompletionActionSpec extends Specification {

  UnderTest underTest = new UnderTest()

  static class UnderTest extends AutoCompletionAction {
    static Token createToken(String text) {
      Token token = new CommonToken(0, text)
      token.stopIndex = -1
      token
    }
    String template
    UnderTest() {
      this('')
    }
    UnderTest(String text) {
      super(0, createToken(text))
    }
    String getDisplayName() {
      null
    }
    boolean shouldBeProposed(AutoCompletionContext context) {
      true
    }
  }

  void "Einzelnes dollar erzeugt Exception"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = '$'

    when:
    underTest.performOn(component)

    then:
    IllegalStateException ex = thrown()
    ex.message == "Template has incomplete variables. Type '\$\$' to enter the dollar character."
  }

  void "Doppeltes dollar wird zu einfachem dollar"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = '$$'

    when:
    underTest.performOn(component)

    then:
    component.text == '$'
  }

  void "Dreifaches dollar erzeugt Exception"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = '$$$'

    when:
    underTest.performOn(component)

    then:
    IllegalStateException ex = thrown()
    ex.message == "Template has incomplete variables. Type '\$\$' to enter the dollar character."
  }

  void "Cursor Variable bestimmt die Cursor Position"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = 'abcd${cursor}efgh'

    when:
    underTest.performOn(component)

    then:
    component.text == 'abcdefgh'
    component.caretPosition == 4
  }

  void "Bei Doppelter Cursor Variable wird nur die erste berücksichtigt"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = 'abcd${cursor}efgh${cursor}ijkl'

    when:
    underTest.performOn(component)

    then:
    component.text == 'abcdefghijkl'
    component.caretPosition == 4
  }

  void "Token Variable inserts Token Text"() {
    given:
    underTest = new UnderTest("my funny token")
    JTextComponent component = new JTextArea();
    underTest.template = 'abcd${token}efgh'

    when:
    underTest.performOn(component)

    then:
    component.text == 'abcdmy funny tokenefgh'
  }

  void "Unbekannte Variable erzeugt Exception"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = '${unknown}'

    when:
    underTest.performOn(component)

    then:
    IllegalArgumentException ex = thrown()
    ex.message == "Unknown variable 'unknown'"
  }

  void "Variable kann escaped werden"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = 'abcd$${cursor}efgh'

    when:
    underTest.performOn(component)

    then:
    component.text == 'abcd${cursor}efgh'
  }

  void "Doppelt escapede Variable wird nicht escaped"() {
    given:
    JTextComponent component = new JTextArea();
    underTest.template = 'abcd$$${cursor}efgh'

    when:
    underTest.performOn(component)

    then:
    component.text == 'abcd$efgh'
    component.caretPosition == 5
  }

}
