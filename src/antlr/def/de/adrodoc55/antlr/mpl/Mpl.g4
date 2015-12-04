grammar Mpl;

//http://www.alittlemadness.com/2006/07/06/antlr-by-example-part-4-tree-parsing/

options {
  language = Java;
}

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
  modifier
  (
    ',' modifier
  )* ':'
;

modifier
:
  'impulse'
  | 'chain'
  | 'repeat'
  | 'conditional'
;

COMMENT
:
  '//' ~( '\r' | '\n' )*
  -> skip

;

COMMAND
:
  '/' ~( '\r' | '\n' )*
;

EOL
:
  '\n'
  | '\r\n'
;

WS
:
  (
    ' '
    | '\t'
  )+ -> skip

;
