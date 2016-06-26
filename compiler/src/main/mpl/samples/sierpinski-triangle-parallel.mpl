install {
  /scoreboard objectives add ST_Triangle dummy
}

process reset {
  /kill @e[name=ST_Head]
  /summon ArmorStand ${origin + (0 0 5)} {CustomName:ST_Head,Tags:[ST_Main],NoGravity:1}
}

repeat process triangle {
  /scoreboard players reset @e[name=ST_Head] ST_Triangle
  /execute @e[name=ST_Head] ~ ~ ~ detect ~-1 ~ ~ wool 0 scoreboard players add @e[r=0,c=1] ST_Triangle 1
  /execute @e[name=ST_Head] ~ ~ ~ detect ~ ~ ~-1 wool 0 scoreboard players add @e[r=0,c=1] ST_Triangle 1
  /execute @e[name=ST_Head] ~ ~ ~ setblock ~ ~ ~ wool 0
  /execute @e[name=ST_Head,score_ST_Triangle_min=1,score_ST_Triangle=1] ~ ~ ~ setblock ~ ~ ~ wool 15

  /execute @e[name=ST_Head,tag=ST_Main] ~ ~ ~ summon ArmorStand ~ ~ ~ {CustomName:ST_Head,NoGravity:1}
  /tp @e[name=ST_Head,tag=ST_Main] ~1 ~ ~
  /tp @e[name=ST_Head,tag=!ST_Main] ~ ~ ~1

  if not: /testfor @e[name=ST_Head]
  then {
    stop
  }
}
