install {
  /scoreboard objectives add SnowballThrown stat.useItem.minecraft.snowball
  start lightningSnowball
}

uninstall {
  /scoreboard objectives remove SnowballThrown
}

repeat process lightningSnowball {
  // Holding Snowball
  /scoreboard players tag @a remove HoldingLightningSnowball
  /scoreboard players tag @a add HoldingLightningSnowball {SelectedItem:{id:minecraft:snowball,tag:{display:{Name:Lightning}}}}

  // Snowball Gravity
  /entitydata @e[type=Snowball,tag=LightningSnowball] {NoGravity:1}

  // Tag special Snowballs
  /execute @e[tag=HoldingLightningSnowball,score_SnowballThrown_min=1] ~ ~ ~ scoreboard players tag @e[type=Snowball,c=1] add LightningSnowball

  /execute @e[type=Snowball,tag=LightningSnowball] ~ ~ ~ kill @e[type=AreaEffectCloud,name=Lightning,c=1]

  // Execute Action at last Snowball Position
  /execute @e[type=AreaEffectCloud,name=Lightning] ~ ~ ~ scoreboard players tag @e[name=!Lightning,r=3] add SummonLightning
  /scoreboard players tag @e remove SummonLightning {HurtTime:0s}
  /execute @e[tag=SummonLightning] ~ ~ ~ /summon LightningBolt
  /scoreboard players tag @e remove SummonLightning

  /execute @e[type=Snowball,tag=LightningSnowball] ~ ~ ~ summon AreaEffectCloud ~ ~ ~ {CustomName:Lightning,Duration:2}
}
