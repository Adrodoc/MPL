// Start the machine:
// /execute @e[name=tntRain] ~ ~ ~ setblock ~ ~ ~ redstone_block
//
// Stop the machine:
// /execute @e[name=tntRain] ~ ~ ~ setblock ~ ~ ~ stone
//
// Set the delay to 100:
// /scoreboard players set delay Tnt 100
//
// Set the tnt count to 5:
// /scoreboard players set count Tnt -5



install (
  /gamerule commandBlockOutput false
  /gamerule logAdminCommands false
  /scoreboard objectives add Tnt dummy
  /scoreboard players set delay Tnt 50
  /scoreboard players set count Tnt -3
)

uninstall (
  /scoreboard objectives remove Tnt
)

repeat process tntRain (
  /execute @a ~ ~ ~ /scoreboard players tag @e[type=Item,r=10] add closeToPlayer
  /kill @e[type=Item,tag=!closeToPlayer]

  // Test for var <= count
  /scoreboard players operation temp Tnt = var Tnt
  /scoreboard players operation temp Tnt -= count Tnt
  /scoreboard players test temp Tnt * 0

  conditional: /scoreboard players operation var Tnt = delay Tnt
  /scoreboard players remove var Tnt 1
  
  if: /scoreboard players test var Tnt * -1
  then (
    /execute @a ~ 1 ~ summon PrimedTnt ~ ~ ~ {Fuse:100,Tags:[new]}
    /execute @e[type=PrimedTnt,tag=new] ~ ~ ~ /spreadplayers ~ ~ 1 10 false @e[c=1]
    /tp @e[type=PrimedTnt,tag=new] ~ ~100 ~
    /scoreboard players tag @e[type=PrimedTnt] remove new
  )
)
