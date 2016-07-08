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
package de.adrodoc55.minecraft.mpl.interpretation

import static de.adrodoc55.TestBase.some
import static de.adrodoc55.TestBase.$String
import static de.adrodoc55.minecraft.mpl.MplTestBase.$Identifier

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.Token

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
    String string = some($String())

    ANTLRInputStream input = new ANTLRInputStream('"' + string + '"')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    String result = MplLexerUtils.getContainedString(token)
    then:
    result == string
  }

  void 'getTagString throws NullPointerException'() {
    when:
    MplLexerUtils.getTagString(null)
    then:
    NullPointerException ex = thrown()
    ex.message == 'tagToken == null!'
  }

  void 'getTagString throws IllegalArgumentException'() {
    given:
    ANTLRInputStream input = new ANTLRInputStream('abc')
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    MplLexerUtils.getTagString(token)
    then:
    IllegalArgumentException ex = thrown()
    ex.message == 'The Given Token is not of type MplLexer.TAG!'
  }

  void 'getTagString returns contained String'() {
    given:
    String string = some($Identifier())

    ANTLRInputStream input = new ANTLRInputStream('#' + string)
    MplLexer lexer = new MplLexer(input);
    Token token = lexer.nextToken()
    when:
    String result = MplLexerUtils.getTagString(token)
    then:
    result == string
  }

}
