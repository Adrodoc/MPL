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
lexer grammar MplLexer;

 COLON
 :
   ':'
 ;

 COMMA
 :
   ','
 ;

 EQUALS_SIGN
 :
   '='
 ;

 OPENING_BRACKET
 :
   '('
 ;

 CLOSING_BRACKET
 :
   ')'
 ;

 OPENING_CURLY_BRACKET
 :
   '{'
 ;

 CLOSING_CURLY_BRACKET
 :
   '}'
 ;

 COMMENT
 :
   '//' ~( '\r' | '\n' )* -> channel ( HIDDEN )
 ;

 MULTILINE_COMMENT
 :
   '/*' .*? '*/' -> channel ( HIDDEN )
 ;

 PLUS
 :
   '+'
 ;

 MINUS
 :
   '-'
 ;

 SLASH
 :
   '/' -> pushMode ( COMMAND )
 ;

 IMPORT
 :
   'import'
 ;

 PROJECT
 :
   'project'
 ;

 INCLUDE
 :
   'include'
 ;

 ORIENTATION
 :
   'orientation'
 ;

 INSTALL
 :
   'install'
 ;

 UNINSTALL
 :
   'uninstall'
 ;

 PROCESS
 :
   'process'
 ;

 IMPULSE
 :
   'impulse'
 ;

 CHAIN
 :
   'chain'
 ;

 REPEAT
 :
   'repeat'
 ;

 UNCONDITIONAL
 :
   'unconditional'
 ;

 CONDITIONAL
 :
   'conditional'
 ;

 INVERT
 :
   'invert'
 ;

 ALWAYS_ACTIVE
 :
   'always active'
 ;

 NEEDS_REDSTONE
 :
   'needs redstone'
 ;

 START
 :
   'start'
 ;

 STOP
 :
   'stop'
 ;

 WAITFOR
 :
   'waitfor'
 ;

 NOTIFY
 :
   'notify'
 ;

 INTERCEPT
 :
   'intercept'
 ;

 BREAKPOINT
 :
   'breakpoint'
 ;

 SKIP_TOKEN
 :
   'skip'
 ;

 IF
 :
   'if'
 ;

 NOT
 :
   'not'
 ;

 THEN
 :
   'then'
 ;

 ELSE
 :
   'else'
 ;

 DO
 :
   'do'
 ;

 WHILE
 :
   'while'
 ;

 BREAK
 :
   'break'
 ;

 CONTINUE
 :
   'continue'
 ;

 REMOTE
 :
   'remote'
 ;

 INLINE
 :
   'inline'
 ;

 TAG
 :
   '#' IDENTIFIER
 ;

 TYPE
 :
   'String'
   | 'Selector'
   | 'Integer'
   | 'Value'
 ;

 UNSIGNED_INTEGER
 :
   [0-9]+
 ;

 SELECTOR
 :
   '@' [a-z]
   (
     '[' [a-zA-Z0-9,!=_]* ']'
   )?
 ;

 STRING
 :
   '"' ~( '\r' | '\n' )*? '"'
 ;

 WS
 :
   [ \t\r\n]+ -> skip
 ;

 IDENTIFIER
 :
   [a-zA-Z0-9_]+
 ;

 UNRECOGNIZED
 :
   .
 ;

 mode COMMAND;

 NEW_LINE
 :
   (
     '\r'
     | '\n'
   )+ -> popMode , skip
 ;

 DOLLAR
 :
   '$' -> pushMode ( INSERT )
 ;

 COMMAND_STRING
 :
   ~( '$' | '\r' | '\n' )+
 ;

 mode INSERT;

 INSERT_PLUS
 :
   PLUS
 ;

 INSERT_MINUS
 :
   MINUS
 ;

 INSERT_OPENING_CURLY_BRACKET
 :
   OPENING_CURLY_BRACKET
 ;

 INSERT_CLOSING_CURLY_BRACKET
 :
   CLOSING_CURLY_BRACKET -> popMode
 ;

 INSERT_THIS
 :
   'this'
 ;

 INSERT_UNSIGNED_INTEGER
 :
   UNSIGNED_INTEGER
 ;

 INSERT_IDENTIFIER
 :
   IDENTIFIER
 ;

 INSERT_WS
 :
   WS -> channel ( HIDDEN )
 ;
