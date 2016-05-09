# -*- coding: utf-8 -*-
"""
    pygments.styles.mpl
    ~~~~~~~~~~~~~~~~~~
    Style for Minecraft Programming Language
"""

from pygments.style import Style
from pygments.token import Whitespace, Comment, Name, Keyword, String, Error

class MplStyle(Style):

    background_color = "#ffffff"
    default_style = ""

    styles = {
        Whitespace:             "#bbbbbb",
        Comment:                "#008000",
        Name.Insert:            "#800000 bg:#F0E68C",
        Keyword.LowFocus:       "bold #808080",
        Keyword.HighFocus:      "bold #800000",
        Keyword.Impulse:        "bold #FF7F50",
        Keyword.Chain:          "bold #3CB371",
        Keyword.Repeat:         "bold #6A5ACD",
        Keyword.NeedsRedstone:  "bold #FF0000",
        Name.Identifier:        "bold #808000",
        Error:                  "border:#FF0000"
    }
