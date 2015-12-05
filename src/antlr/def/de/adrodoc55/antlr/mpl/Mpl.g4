grammar Mpl;

//http://www.alittlemadness.com/2006/07/06/antlr-by-example-part-4-tree-parsing/

program
:
  line* EOF
;

line
:
  (
    modifierList? COMMAND
  )? EOL
;

modifierList
:
  (
    modus
    (
      ',' CONDITIONAL
    )?
    (
      ',' NEEDS_REDSTONE
    )?
    | CONDITIONAL
    (
      ',' NEEDS_REDSTONE
    )?
    | NEEDS_REDSTONE
  ) ':'
;

modus
:
  'impulse'
  | 'chain'
  | 'repeat'
;

COMMENT
:
  ( '//' | '#' ) ~( '\r' | '\n' )* -> skip
;

COMMAND
:
  '/' ~( '\r' | '\n' )*
;

CONDITIONAL
:
  'conditional'
;

NEEDS_REDSTONE
:
  'needsRedstone'
;

EOL
:
  '\n'
  | '\r\n'
;

WS
:
  [ \t]+ -> skip
;
