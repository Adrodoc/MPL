grammar Mpl;

program
:
  (
    include
  )* install? uninstall?
  (
    project
    | method
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

method
:
  (
    IMPULSE
    | REPEAT
  )? METHOD line*
;

skript
:
  line*
;

line
:
  commandDeclaration
  | skip
  | execute
  | waitfor
;

commandDeclaration
:
  modifierList? command
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

WS
:
  [ \t\r\n]+ -> skip
;

STRING
:
  '"' .+? '"'
;
