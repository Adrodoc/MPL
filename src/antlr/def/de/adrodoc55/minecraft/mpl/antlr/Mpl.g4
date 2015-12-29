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
  'include' '(' STRING
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
  SKIP
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
  ) ~( '\r' | '\n' )* -> channel(HIDDEN)
;

COMMAND
:
  '/' ~( '\r' | '\n' )*
;

STRING
:
  '"' .+? '"'
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
