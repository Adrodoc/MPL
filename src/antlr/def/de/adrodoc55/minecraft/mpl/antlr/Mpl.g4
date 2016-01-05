grammar Mpl;

program
:
  (
    include
  )* install? uninstall?
  (
    project
    | process+
    | skript
  )
;

include
:
  INCLUDE STRING
;

install
:
  INSTALL line*
;

uninstall
:
  UNINSTALL line*
;

project
:
// TODO: Prefix, Orientation, max
  PROJECT
;

process
:
  (
    IMPULSE
    | REPEAT
  )? PROCESS IDENTIFIER line*
;

skript
:
  line*
;

line
:
  commandDeclaration
  | skip
;

commandDeclaration
:
  modifierList? command
  |
  (
    conditional ':'
  )?
  (
    start
    | stop
    | notifyDeclaration
    | waitfor
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
  WAITFOR IDENTIFIER?
;

notifyDeclaration
:
  NOTIFY
;

skip
:
  SKIP
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

INCLUDE
:
  'include'
;

INSTALL
:
  'install'
;

UNINSTALL
:
  'uninstall'
;

PROJECT
:
  'project'
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

SKIP
:
  'skip'
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
  '"' .+? '"'
;

IDENTIFIER
:
  [a-zA-Z0-9_]+
;
