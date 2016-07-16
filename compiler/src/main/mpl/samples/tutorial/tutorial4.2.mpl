// start the main process with: /execute @e[name=main] ~ ~ ~ setblock ~ ~ ~ redstone_block
process main (
  /say starting program
  start other
  /say I am the main process
)

process other (
  /say I am the other process
)
