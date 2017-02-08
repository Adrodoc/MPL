install {
  start tellraw
}

impulse process tellraw {
/tellraw @a [{"text":"I am the Essence of "},{"text":"MAGIC!!!","color":"aqua","bold":true}]
/tellraw @a {"text":"   -- Malygos\n","color":"gray","italic":true}

// 6 - 1 = 5 ticks delay
/summon area_effect_cloud ~ ~ ~ {CustomName:delay,Duration:6}
waitforDelay()

/tellraw @a {"text":"I have no time for games!"}
/tellraw @a {"text":"   -- Sylvanas Windrunner\n","color":"gray","italic":true}

// 51 - 1 = 50 ticks delay
/summon area_effect_cloud ~ ~ ~ {CustomName:delay,Duration:51}
waitforDelay()

/tellraw @a [{"text":"HMMMMM","color":"black"},{"text":" HMHHH","color":"white"}]
/tellraw @a {"text":"   -- Villager\n","color":"green","italic":true}

}

repeat process waitforDelay {
  if not: /testfor @e[type=area_effect_cloud,name=delay]
  then {
    stop
    notify waitforDelay
  }
}
