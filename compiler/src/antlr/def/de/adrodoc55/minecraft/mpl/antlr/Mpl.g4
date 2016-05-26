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
grammar Mpl;

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
  importDeclaration*
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
  PROJECT IDENTIFIER '('
  (
    orientation
    | include
  )* ')'
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
  INSTALL '(' chain ')'
;

uninstall
:
  UNINSTALL '(' chain ')'
;

process
:
  (
    IMPULSE
    | REPEAT
  )? PROCESS IDENTIFIER '(' chain ')'
;

chain
:
  (
    ifDeclaration
    | whileDeclaration
    | mplCommand
    | skipDeclaration
  )+
;

ifDeclaration
:
  IF NOT? ':' COMMAND then? elseDeclaration?
;

then
:
  THEN '(' chain ')'
;

elseDeclaration
:
  ELSE '(' chain ')'
;

whileDeclaration
:
  WHILE NOT? ':' COMMAND REPEAT '(' chain ')'
  | REPEAT '(' chain ')'
  (
    DO WHILE NOT? ':' COMMAND
  )?
;

mplCommand
:
  modifierList?
  (
    command
    | start
    | stop
    | waitfor
    | notifyDeclaration
    | intercept
    | breakpoint
  )
;

modifierList
:
  (
    modus
    (
      ',' conditional
    )?
    (
      ',' auto
    )?
    | conditional
    (
      ',' auto
    )?
    | auto
  ) ':'
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
  COMMAND
;

start
:
  START IDENTIFIER
;

stop
:
  STOP IDENTIFIER?
;

waitfor
:
  WAITFOR
  (
    NOTIFY? IDENTIFIER
  )?
;

notifyDeclaration
:
  NOTIFY
;

intercept
:
  INTERCEPT IDENTIFIER
;

breakpoint
:
  BREAKPOINT
;

skipDeclaration
:
  SKIP_TOKEN
;

COMMENT
:
  (
    '//'
    | '#'
  ) ~( '\r' | '\n' )* -> channel ( HIDDEN )
;

COMMAND
:
  '/' ~( '\r' | '\n' )*
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

UNSIGNED_INT
:
  [0-9]+
;

WS
:
  [ \t\r\n]+ -> skip
;

STRING
:
  '"' .*? '"'
;

IDENTIFIER
:
  [a-zA-Z0-9_]+
;

UNRECOGNIZED
:
  .
;
