install (
  /scoreboard objectives add ST_Triangle dummy
  /scoreboard objectives add ST_X dummy
  /scoreboard objectives add ST_Z dummy
)

process reset (
  /kill @e[name=ST_Head]
  /summon ArmorStand ${origin + (0 0 5)} {CustomName:ST_Head,NoGravity:1}
)

repeat process triangle (
  /scoreboard players reset @e[name=ST_Head] ST_Triangle
  /execute @e[name=ST_Head] ~ ~ ~ detect ~-1 ~ ~ wool 15 scoreboard players add @e[r=0,c=1] ST_Triangle 1
  /execute @e[name=ST_Head] ~ ~ ~ detect ~ ~ ~-1 wool 15 scoreboard players add @e[r=0,c=1] ST_Triangle 1
  /execute @e[name=ST_Head] ~ ~ ~ setblock ~ ~ ~ wool 15
  /execute @e[name=ST_Head,score_ST_Triangle_min=1,score_ST_Triangle=1] ~ ~ ~ setblock ~ ~ ~ wool 0

  /tp @e[name=ST_Head,tag=up] ~1 ~ ~-1
  /scoreboard players add @e[name=ST_Head,tag=up] ST_X 1
  /scoreboard players remove @e[name=ST_Head,tag=up] ST_Z 1

  /tp @e[name=ST_Head,tag=!up] ~-1 ~ ~1
  /scoreboard players remove @e[name=ST_Head,tag=!up] ST_X 1
  /scoreboard players add @e[name=ST_Head,tag=!up] ST_Z 1

  /scoreboard players tag @e[name=ST_Head,score_ST_X=-1] add up
  /tp @e[name=ST_Head,score_ST_X=-1] ~1 ~ ~
  /scoreboard players add @e[name=ST_Head,score_ST_X=-1] ST_X 1

  /scoreboard players tag @e[name=ST_Head,score_ST_Z=-1] remove up
  /tp @e[name=ST_Head,score_ST_Z=-1] ~ ~ ~1
  /scoreboard players add @e[name=ST_Head,score_ST_Z=-1] ST_Z 1

  if not: /testfor @e[name=ST_Head]
  then (
    stop
  )
)
