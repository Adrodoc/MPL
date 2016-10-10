install {
  /scoreboard objectives add InLadder dummy
  start verticalRails
}

uninstall {
  stop verticalRails
  /scoreboard objectives remove InLadder
  /entitydata @e[type=minecart] {NoGravity:0}
}

repeat process verticalRails {
  /scoreboard players reset * InLadder
  /scoreboard players set @e[type=chest_minecart] InLadder 0
  /scoreboard players set @e[type=tnt_minecart] InLadder 0
  /scoreboard players set @e[type=spawner_minecart] InLadder 0
  /scoreboard players set @e[type=hopper_minecart] InLadder 0
  /scoreboard players set @e[type=minecart] InLadder 0
  /scoreboard players set @e[type=furnace_minecart] InLadder 0
  /execute @e[type=chest_minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /execute @e[type=tnt_minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /execute @e[type=spawner_minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /execute @e[type=hopper_minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /execute @e[type=minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /execute @e[type=furnace_minecart] ~ ~ ~ detect ~ ~ ~ ladder -1 scoreboard players set @e[r=0,c=1] InLadder 1
  /tp @e[score_InLadder_min=1] ~ ~.1 ~

  /entitydata @e[score_InLadder_min=1] {NoGravity:1}
  /execute @e[score_InLadder=1] ~ ~ ~ detect ~ ~-.5 ~.5 ladder 2 entitydata @e[r=0,c=1] {Motion:[0.0,0.0,0.2]}
  /execute @e[score_InLadder=1] ~ ~ ~ detect ~ ~-.5 ~-.5 ladder 3 entitydata @e[r=0,c=1] {Motion:[0.0,0.0,-0.2]}
  /execute @e[score_InLadder=1] ~ ~ ~ detect ~.5 ~-.5 ~ ladder 4 entitydata @e[r=0,c=1] {Motion:[0.2,0.0,0.0]}
  /execute @e[score_InLadder=1] ~ ~ ~ detect ~-.5 ~-.5 ~ ladder 5 entitydata @e[r=0,c=1] {Motion:[-0.2,0.0,0.0]}

  /entitydata @e[score_InLadder=0] {NoGravity:0}
  /execute @e[score_InLadder=0] ~ ~ ~ detect ~ ~-.5 ~ ladder 2 entitydata @e[r=0,c=1] {Motion:[0.0,0.0,0.2]}
  /execute @e[score_InLadder=0] ~ ~ ~ detect ~ ~-.5 ~ ladder 3 entitydata @e[r=0,c=1] {Motion:[0.0,0.0,-0.2]}
  /execute @e[score_InLadder=0] ~ ~ ~ detect ~ ~-.5 ~ ladder 4 entitydata @e[r=0,c=1] {Motion:[0.2,0.0,0.0]}
  /execute @e[score_InLadder=0] ~ ~ ~ detect ~ ~-.5 ~ ladder 5 entitydata @e[r=0,c=1] {Motion:[-0.2,0.0,0.0]}
}
  