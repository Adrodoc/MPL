install {
  /scoreboard objectives add DamageDelt stat.damageDealt
  start lightningStick
}

uninstall {
  /scoreboard objectives remove DamageDelt
}

repeat process lightningStick {
  // Holding Stick
  /scoreboard players tag @a remove HoldingStick
  /scoreboard players tag @a add HoldingStick {SelectedItem:{id:minecraft:stick,tag:{display:{Name:Lightning}}}}

  // Execute Action at Stickholder
  /execute @a[tag=HoldingStick,score_DamageDelt_min=1] ~ ~ ~ scoreboard players tag @e[r=5] add SummonLightning
  /scoreboard players tag @e remove SummonLightning {HurtTime:0s}
  /execute @e[tag=SummonLightning] ~ ~ ~ /summon LightningBolt
  /scoreboard players tag @e remove SummonLightning

  /scoreboard players set @a DamageDelt 0
}
