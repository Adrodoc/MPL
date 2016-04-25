impulse: /say starting
/say this
/say or this
/testforblock ${this - 1} chain_command_block -1 {SuccessCount:0}
conditional: /testforblock ${this - 3} chain_command_block -1 {SuccessCount:0}
invert: /say success
