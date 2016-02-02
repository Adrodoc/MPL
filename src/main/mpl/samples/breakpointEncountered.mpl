process breakpointEncountered:
/tellraw @a [{"text":"[tp to Breakpoint]","color":"gold","clickEvent":{"action":"run_command","value":"/tp @p @e[name=breakpointEncountered_NOTIFY,c=-1]"}},{"text":"   "},{"text":"[continue program]","color":"gold","clickEvent":{"action":"run_command","value":"/execute @e[name=breakpointContinue_NOTIFY] ~ ~ ~ /setblock ~ ~ ~ redstone_block"}}]
waitfor breakpointContinue
/kill @e[name=breakpointContinue_NOTIFY]
notify



process testBreakpoint:
/say starting application
# (
/say encountered Breakpoint file1.mpl : line 12
start breakpointEncountered
waitfor
# )
/say continuing application
# (
/say encountered Breakpoint file1.mpl : line 18
start breakpointEncountered
waitfor
# )
/say application finished
