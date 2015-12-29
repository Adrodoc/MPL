grammar Mpl;

program
:
  (
    include
    | NEWLINE
  )* install? uninstall?
  (
    project
    | method
    | skript
  )
;

include
:
  INCLUDE STRING NEWLINE
;

install
:
  INSTALL NEWLINE line*
;

uninstall
:
  UNINSTALL NEWLINE line*
;

project
:
// TODO: Prefix, Orientation, max
  PROJECT NEWLINE
;

method
:
  (
    IMPULSE
    | REPEAT
  )? METHOD NEWLINE line*
;

skript
:
  line*
;

line
:
  NEWLINE
  | commandDeclaration
  | skip
;

commandDeclaration
:
  modifierList? command NEWLINE
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

skip
:
  SKIP NEWLINE
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

METHOD
:
  'method'
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

SKIP
:
  'skip'
;

UNSIGNED_INT
:
  [0-9]+
;

NEWLINE
:
  '\r'? '\n'
;

WS
:
  [ \t]+ -> skip
;

STRING
:
  '"' .+? '"'
;