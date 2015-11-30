grammar Mpl;

//http://www.alittlemadness.com/2006/07/06/antlr-by-example-part-4-tree-parsing/

options {
// antlr will generate java lexer and parser
	language = Java;
	// generated parser should create abstract syntax tree
	output = AST;
}

@lexer::header {
package de.adrodoc55.antlr.mpl;
}

@parser::header {
package de.adrodoc55.antlr.mpl;
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
	{$channel=HIDDEN;}

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

// Skipping

WS
:
	(
		' '
		| '\t'
	)+
	{$channel=HIDDEN;}

;
//
//LINE_COMMENT
//:
//	(
//		'//'
//		| '#'
//	) ~[\r\n]* -> skip
//;
