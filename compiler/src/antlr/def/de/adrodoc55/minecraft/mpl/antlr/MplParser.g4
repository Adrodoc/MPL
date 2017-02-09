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
parser grammar MplParser;

 options {
   tokenVocab = MplLexer;
 }

 file
 :
   (
     scriptFile
     | projectFile
   ) EOF
 ;

 scriptFile
 :
   (
     orientation
     | install
     | uninstall
     | chain
   )*
 ;

 projectFile
 :
   include* importDeclaration*
   (
     project
     | install
     | uninstall
     | process
   )*
 ;

 importDeclaration
 :
   IMPORT STRING
 ;

 project
 :
 // TODO: Prefix, Orientation, max
   PROJECT IDENTIFIER OPENING_CURLY_BRACKET orientation* CLOSING_CURLY_BRACKET
 ;

 orientation
 :
   ORIENTATION STRING
 ;

 include
 :
   INCLUDE STRING
 ;

 install
 :
   INSTALL OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
 ;

 uninstall
 :
   UNINSTALL OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
 ;

 process
 :
   TAG*
   (
     REMOTE
     | INLINE
   )?
   (
     IMPULSE
     | REPEAT
   )? PROCESS IDENTIFIER OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
 ;

 chain
 :
   (
     mplIf
     | mplWhile
     | modifiableCommand
     | mplSkip
     | variableDeclaration
   )+
 ;

 mplIf
 :
   IF NOT? COLON command mplThen? mplElse?
 ;

 mplThen
 :
   THEN OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
 ;

 mplElse
 :
   ELSE OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
 ;

 mplWhile
 :
   (
     IDENTIFIER COLON
   )? WHILE NOT? COLON command REPEAT OPENING_CURLY_BRACKET chain?
   CLOSING_CURLY_BRACKET
   |
   (
     IDENTIFIER COLON
   )? REPEAT OPENING_CURLY_BRACKET chain? CLOSING_CURLY_BRACKET
   (
     DO WHILE NOT? COLON command
   )?
 ;

 modifiableCommand
 :
   modifierList?
   (
     mplCommand
     | mplCall
     | mplStart
     | mplStop
     | mplWaitfor
     | mplNotify
     | mplIntercept
     | mplBreakpoint
     | mplBreak
     | mplContinue
   )
 ;

 modifierList
 :
   (
     modus
     (
       COMMA conditional
     )?
     (
       COMMA auto
     )?
     | conditional
     (
       COMMA auto
     )?
     | auto
   ) COLON
 ;

 modus
 :
   IMPULSE
   | CHAIN
   | REPEAT
 ;

 conditional
 :
   UNCONDITIONAL
   | CONDITIONAL
   | INVERT
 ;

 auto
 :
   NEEDS_REDSTONE
   | ALWAYS_ACTIVE
 ;

 command
 :
   SLASH
   (
     COMMAND_STRING
     | insert
   )*
 ;

 insert
 :
   DOLLAR OPENING_CURLY_BRACKET IDENTIFIER
   (
     (
       PLUS_INSERT
       | MINUS_INSERT
     ) UNSIGNED_INTEGER
   )? CLOSING_CURLY_BRACKET
 ;

 mplCommand
 :
   command
 ;

 mplCall
 :
   IDENTIFIER OPENING_BRACKET CLOSING_BRACKET
 ;

 mplStart
 :
   START
   (
     IDENTIFIER
     | SELECTOR
   )
 ;

 mplStop
 :
   STOP
   (
     IDENTIFIER
     | SELECTOR
   )?
 ;

 mplWaitfor
 :
   WAITFOR IDENTIFIER?
 ;

 mplNotify
 :
   NOTIFY IDENTIFIER
 ;

 mplIntercept
 :
   INTERCEPT IDENTIFIER
 ;

 mplBreakpoint
 :
   BREAKPOINT
 ;

 mplBreak
 :
   BREAK IDENTIFIER?
 ;

 mplContinue
 :
   CONTINUE IDENTIFIER?
 ;

 mplSkip
 :
   SKIP_TOKEN
 ;

 variableDeclaration
 :
   TYPE IDENTIFIER EQUALS_SIGN
   (
     STRING
     | SELECTOR
     | MINUS? UNSIGNED_INTEGER
     | SELECTOR IDENTIFIER
   )
 ;
