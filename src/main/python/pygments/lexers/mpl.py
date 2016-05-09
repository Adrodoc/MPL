from pygments.lexer import RegexLexer
from pygments.token import Whitespace, Punctuation, Comment, Name, Keyword, String

__all__ = ['MplLexer']

class MplLexer(RegexLexer):
    """
    For the Minecraft Programming Language.
    """

    name = 'Minecraft Programming Language'
    aliases = ['mpl']
    filenames = ['*.mpl']
	
    lowFocusKeywords = (
	    'unconditional',
        'always active'
    )
	
    highFocusKeywords = (
	    'orientation',
        'include',
        'import',
        'install',
        'uninstall',
        'project',
        'process',
        'conditional',
        'invert',
        'start',
        'stop',
        'waitfor',
        'notify',
        'intercept',
        'breakpoint',
        'skip',
        'if',
        'not',
        'then',
        'else'
    )
	
    tokens = {
        'root': [
            (r'[ \t\r\n]+', Whitespace),
            (r'[():,]+', Punctuation),
            (r'(#|//).*?$', Comment),
            ('/', Name.Command, 'command'),
			
            (r'\b(%s)\b' % '|'.join(highFocusKeywords), Keyword.HighFocus),
            (r'\b(%s)\b' % '|'.join(lowFocusKeywords), Keyword.LowFocus),
            (r'\bimpulse\b', Keyword.Impulse),
            (r'\bchain\b', Keyword.Chain),
            (r'\brepeat\b', Keyword.Repeat),
            (r'\bneeds redstone\b', Keyword.NeedsRedstone),
            (r'".*?"', String),
            (r'\b[a-zA-Z0-9_]+\b', Name.Identifier),
        ],
		'command': [
		    (r'\R', Name.Command, '#pop'),
			(r'\$\{[^{}]*\}', Name.Insert),
		    (r'[^$\r\n]+', Name.Command)
		]
    }
