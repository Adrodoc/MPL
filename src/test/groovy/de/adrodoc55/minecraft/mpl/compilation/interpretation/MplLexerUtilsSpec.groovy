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
package de.adrodoc55.minecraft.mpl.compilation.interpretation

import static de.adrodoc55.TestBase.*

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenStream

import de.adrodoc55.minecraft.mpl.MplSpecBase
import de.adrodoc55.minecraft.mpl.antlr.MplLexer

class MplLexerUtilsSpec extends MplSpecBase {

  void 'getContainedString throws NullPointerException'() {
    when:
    MplLexerUtils.getContainedString(null)
    then:
    NullPointerException ex = thrown()
    ex.message == 'stringToken == null!'
  }

  void 'getContainedString throws IllegalArgumentException'() {
    given:
    ANTLRInputStream input = new ANTLRInputStream('abc')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    MplLexerUtils.getContainedString(token)
    then:
    IllegalArgumentException ex = thrown()
    ex.message == 'The Given Token is not of type MplLexer.STRING!'
  }

  void 'getContainedString returns empty String for empty node'() {
    given:
    ANTLRInputStream input = new ANTLRInputStream('""')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    String result = MplLexerUtils.getContainedString(token)
    then:
    result == ''
  }

  void 'getContainedString returns contained spaces'() {
    given:
    ANTLRInputStream input = new ANTLRInputStream('"   "')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    String result = MplLexerUtils.getContainedString(token)
    then:
    result == '   '
  }

  void 'getContainedString returns contained String'() {
    given:
    String string = someString()

    ANTLRInputStream input = new ANTLRInputStream('"' + string + '"')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    String result = MplLexerUtils.getContainedString(token)
    then:
    result == string
  }

}
