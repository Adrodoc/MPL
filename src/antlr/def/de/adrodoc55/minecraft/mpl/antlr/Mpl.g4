grammar Mpl;

file
:
  (
    include
  )* install? uninstall?
  (
    projectFile
    | processFile
    | skriptFile
  )
;

projectFile
:
  project include* install? uninstall?
;

project
:
// TODO: Prefix, Orientation, max
  PROJECT IDENTIFIER ':'
;

include
:
  INCLUDE STRING
;

processFile
:
  importDeclaration* install? uninstall? process+
;

importDeclaration
:
  IMPORT STRING
;

install
:
  INSTALL line*
;

uninstall
:
  UNINSTALL line*
;

process
:
  (
    IMPULSE
    | REPEAT
  )? PROCESS IDENTIFIER ':' line*
;

skriptFile
:
  line*
;

line
:
  commandDeclaration
  | skip
  | ifDeclaration
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
    | intercept
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

intercept
:
  INTERCEPT IDENTIFIER
;

skip
:
  SKIP
;

ifDeclaration
:
  IF NOT? ':' command THEN ':' line*
  (
    ELSE ':' line*
  )? END
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

IMPORT
:
  'import'
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

INTERCEPT
:
  'intercept'
;

SKIP
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

END
:
  'end'
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

UNRECOGNIZED
:
  .
;
