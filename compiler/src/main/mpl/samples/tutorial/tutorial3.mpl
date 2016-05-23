install (
  /scoreboard objectives add COUNTER dummy
  /scoreboard objectives setdisplay sidebar COUNTER
)

uninstall (
  /scoreboard objectives remove COUNTER
)

repeat: /scoreboard players add @a COUNTER 1
/scoreboard players set @a[score_COUNTER_min=10] COUNTER 1
