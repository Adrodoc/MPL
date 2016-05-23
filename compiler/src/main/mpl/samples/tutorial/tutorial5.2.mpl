install (
  /scoreboard objectives add ZombieKills stat.killEntity.Zombie
)

uninstall (
  /scoreboard objectives remove ZombieKills
)

# start: /execute @e[name=startZombieGame] ~ ~ ~ setblock ~ ~ ~ redstone_block
process startZombieGame (
  /say starting new zombie challenge
  /execute @a ~ ~ ~ summon Zombie
  /scoreboard players reset @a ZombieKills
  start testForWinner
  waitfor
  /say the winner is @p[score_ZombieKills_min=1]
)

repeat process testForWinner (
  /testfor @p[score_ZombieKills_min=1]
  conditional: /kill @e[type=Zombie]
  # if no zombies were killed by the previous command, this process will not stop
  conditional: notify
  conditional: stop
)
