grammar Mpl;

program
:
  line
  (
    NEWLINE line
  )*
;

line
:
  (
    includeDeclaration
    | skipDeclaration
    | commandDeclaration
  )?
;

includeDeclaration
:
  DECLARATION_TOKEN 'include' '(' STRING
  (
    ',' includeAt
  )?
  (
    ',' includeMax
  )? ')'
;

includeAt
:
  'at' coordinate
;

includeMax
:
  'max' coordinate
;

coordinate
:
  '(' UNSIGNED_INT ',' UNSIGNED_INT ',' UNSIGNED_INT ')'
;

skipDeclaration
:
  DECLARATION_TOKEN 'skip'
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
      ',' CONDITIONAL
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
  'impulse'
  | 'chain'
  | 'repeat'
;

conditional
:
  CONDITIONAL
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

COMMENT
:
  (
    '//'
    | '#'
  ) ~( '\r' | '\n' )* -> skip
;

COMMAND
:
  '/' ~( '\r' | '\n' )*
;

STRING
:
  '"' .+? '"'
;

CONDITIONAL
:
  'conditional'
;

NEEDS_REDSTONE
:
  'needsRedstone'
;

ALWAYS_ACTIVE
:
  'alwaysActive'
;

DECLARATION_TOKEN
:
  '$'
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
